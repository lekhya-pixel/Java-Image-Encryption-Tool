import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ImageCipherUtils {

    private static final byte[] MAGIC =
            new byte[]{'E', 'I', 'M', 'G'};


    // =========================================================
    // SAVE ENCRYPTED DATA AS PNG
    // =========================================================

    public static void saveCipherAsImage(
            byte[] cipher,
            String originalExtension,
            String outputPath) throws IOException {

        if (originalExtension == null ||
                originalExtension.isEmpty()) {

            originalExtension = ".bin";
        }

        byte[] extensionBytes =
                originalExtension.getBytes(StandardCharsets.UTF_8);


        /*
         * Header:
         *
         * 4 bytes  = MAGIC
         * 4 bytes  = extension length
         * N bytes  = original extension
         * 4 bytes  = cipher length
         * remaining = cipher
         */

        ByteArrayOutputStream output =
                new ByteArrayOutputStream();

        output.write(MAGIC);

        output.write(
                ByteBuffer.allocate(4)
                        .putInt(extensionBytes.length)
                        .array()
        );

        output.write(extensionBytes);

        output.write(
                ByteBuffer.allocate(4)
                        .putInt(cipher.length)
                        .array()
        );

        output.write(cipher);

        byte[] allData =
                output.toByteArray();


        // Each RGB pixel stores 3 bytes
        int numberOfPixels =
                (allData.length + 2) / 3;

        int width =
                (int) Math.ceil(
                        Math.sqrt(numberOfPixels)
                );

        int height =
                (numberOfPixels + width - 1) / width;


        BufferedImage image =
                new BufferedImage(
                        width,
                        height,
                        BufferedImage.TYPE_INT_RGB
                );


        int index = 0;

        for (int y = 0; y < height; y++) {

            for (int x = 0; x < width; x++) {

                int red = 0;
                int green = 0;
                int blue = 0;

                if (index < allData.length) {
                    red = allData[index++] & 0xFF;
                }

                if (index < allData.length) {
                    green = allData[index++] & 0xFF;
                }

                if (index < allData.length) {
                    blue = allData[index++] & 0xFF;
                }

                int rgb =
                        (red << 16)
                                | (green << 8)
                                | blue;

                image.setRGB(x, y, rgb);
            }
        }

        ImageIO.write(
                image,
                "png",
                new File(outputPath)
        );
    }


    // =========================================================
    // READ CIPHERTEXT FROM ENCRYPTED PNG
    // =========================================================

    public static byte[] readCipherFromImage(
            String path) throws IOException {

        BufferedImage image =
                ImageIO.read(new File(path));

        if (image == null) {
            throw new IOException(
                    "The selected file is not a valid PNG image."
            );
        }


        int width = image.getWidth();
        int height = image.getHeight();

        byte[] allData =
                new byte[width * height * 3];

        int index = 0;


        for (int y = 0; y < height; y++) {

            for (int x = 0; x < width; x++) {

                int rgb =
                        image.getRGB(x, y);

                allData[index++] =
                        (byte) ((rgb >> 16) & 0xFF);

                allData[index++] =
                        (byte) ((rgb >> 8) & 0xFF);

                allData[index++] =
                        (byte) (rgb & 0xFF);
            }
        }


        // Check MAGIC

        for (int i = 0; i < MAGIC.length; i++) {

            if (allData[i] != MAGIC[i]) {

                throw new IOException(
                        "This is not an encrypted image created by this application."
                );
            }
        }


        int indexPosition = 4;


        // Extension length

        int extensionLength =
                ByteBuffer.wrap(
                        allData,
                        indexPosition,
                        4
                ).getInt();

        indexPosition += 4;


        if (extensionLength < 0 ||
                extensionLength > 100 ||
                indexPosition + extensionLength >
                        allData.length) {

            throw new IOException(
                    "Invalid encrypted image."
            );
        }


        // Skip original extension

        indexPosition += extensionLength;


        // Cipher length

        if (indexPosition + 4 >
                allData.length) {

            throw new IOException(
                    "Invalid encrypted image."
            );
        }


        int cipherLength =
                ByteBuffer.wrap(
                        allData,
                        indexPosition,
                        4
                ).getInt();

        indexPosition += 4;


        if (cipherLength < 0 ||
                cipherLength >
                        allData.length - indexPosition) {

            throw new IOException(
                    "Invalid ciphertext length."
            );
        }


        byte[] cipher =
                new byte[cipherLength];

        System.arraycopy(
                allData,
                indexPosition,
                cipher,
                0,
                cipherLength
        );

        return cipher;
    }


    // =========================================================
    // GET ORIGINAL FILE EXTENSION
    // =========================================================

    public static String getOriginalExtension(
            String path) throws IOException {

        BufferedImage image =
                ImageIO.read(new File(path));

        if (image == null) {
            throw new IOException(
                    "Invalid encrypted image."
            );
        }


        int width = image.getWidth();
        int height = image.getHeight();

        byte[] header =
                new byte[Math.min(
                        width * height * 3,
                        200
                )];


        int index = 0;

        for (int y = 0;
             y < height && index < header.length;
             y++) {

            for (int x = 0;
                 x < width && index < header.length;
                 x++) {

                int rgb =
                        image.getRGB(x, y);

                if (index < header.length)
                    header[index++] =
                            (byte) ((rgb >> 16) & 0xFF);

                if (index < header.length)
                    header[index++] =
                            (byte) ((rgb >> 8) & 0xFF);

                if (index < header.length)
                    header[index++] =
                            (byte) (rgb & 0xFF);
            }
        }


        for (int i = 0; i < MAGIC.length; i++) {

            if (header[i] != MAGIC[i]) {

                throw new IOException(
                        "Not a valid encrypted image."
                );
            }
        }


        int extensionLength =
                ByteBuffer.wrap(
                        header,
                        4,
                        4
                ).getInt();


        if (extensionLength < 0 ||
                extensionLength > 100 ||
                8 + extensionLength >
                        header.length) {

            throw new IOException(
                    "Invalid extension information."
            );
        }


        return new String(
                header,
                8,
                extensionLength,
                StandardCharsets.UTF_8
        );
    }
}