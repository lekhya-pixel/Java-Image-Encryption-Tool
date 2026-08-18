import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MainWindow extends JFrame {

    // =========================================================
    // GUI COMPONENTS
    // =========================================================

    private JTextField txtFilePath;

    private JButton btnBrowse;

    private JComboBox<String> comboAlgorithm;

    private JTextField txtKey;

    private JButton btnEncrypt;
    private JButton btnDecrypt;

    private JLabel lblStatus;
    private JLabel lblPreview;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public MainWindow() {

        setTitle("Image Encryption Tool");

        setSize(800, 600);

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setLocationRelativeTo(null);


        // Main layout

        setLayout(
                new GridLayout(6, 1)
        );


        // =====================================================
        // FILE PANEL
        // =====================================================

        JPanel filePanel =
                new JPanel(new BorderLayout());

        txtFilePath =
                new JTextField();

        btnBrowse =
                new JButton("Browse");

        filePanel.add(
                txtFilePath,
                BorderLayout.CENTER
        );

        filePanel.add(
                btnBrowse,
                BorderLayout.EAST
        );

        add(filePanel);


        // =====================================================
        // ALGORITHM PANEL
        // =====================================================

        JPanel algoPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT
                        )
                );

        algoPanel.add(
                new JLabel("Algorithm:")
        );


        comboAlgorithm =
                new JComboBox<>(
                        new String[]{
                                "DES",
                                "2DES",
                                "3DES",
                                "AES-128",
                                "AES-192",
                                "AES-256",
                                "Blowfish"
                        }
                );


        algoPanel.add(
                comboAlgorithm
        );

        add(algoPanel);


        // =====================================================
        // KEY PANEL
        // =====================================================

        JPanel keyPanel =
                new JPanel(new BorderLayout());

        keyPanel.add(
                new JLabel("Key:"),
                BorderLayout.WEST
        );


        txtKey =
                new JTextField();

        keyPanel.add(
                txtKey,
                BorderLayout.CENTER
        );

        add(keyPanel);


        // =====================================================
        // BUTTON PANEL
        // =====================================================

        JPanel buttonPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.CENTER
                        )
                );


        btnEncrypt =
                new JButton("Encrypt");

        btnDecrypt =
                new JButton("Decrypt");


        buttonPanel.add(btnEncrypt);
        buttonPanel.add(btnDecrypt);

        add(buttonPanel);


        // =====================================================
        // STATUS
        // =====================================================

        lblStatus =
                new JLabel("Status: Ready");

        add(lblStatus);


        // =====================================================
        // PREVIEW
        // =====================================================

        lblPreview =
                new JLabel(
                        "Image preview will appear here",
                        SwingConstants.CENTER
                );


        lblPreview.setBorder(
                BorderFactory.createTitledBorder(
                        "Preview"
                )
        );


        add(lblPreview);


        // =====================================================
        // BROWSE BUTTON
        // =====================================================

        btnBrowse.addActionListener(e -> {

            JFileChooser chooser =
                    new JFileChooser();

            chooser.setDialogTitle(
                    "Select Image or Encrypted Image"
            );


            int result =
                    chooser.showOpenDialog(this);


            if (result ==
                    JFileChooser.APPROVE_OPTION) {

                File selectedFile =
                        chooser.getSelectedFile();

                String path =
                        selectedFile.getAbsolutePath();


                txtFilePath.setText(path);

                lblStatus.setText(
                        "Status: File selected"
                );


                // Try to display preview

                try {

                    ImageIcon icon =
                            new ImageIcon(path);

                    Image image =
                            icon.getImage();


                    Image scaled =
                            image.getScaledInstance(
                                    250,
                                    250,
                                    Image.SCALE_SMOOTH
                            );


                    lblPreview.setIcon(
                            new ImageIcon(scaled)
                    );

                    lblPreview.setText("");


                } catch (Exception ex) {

                    lblPreview.setIcon(null);

                    lblPreview.setText(
                            "Preview not available"
                    );
                }
            }
        });


        // =====================================================
        // ENCRYPT BUTTON
        // =====================================================

        btnEncrypt.addActionListener(e -> {

            try {

                String inputPath =
                        txtFilePath.getText().trim();

                String key =
                        txtKey.getText();


                // Check file

                if (inputPath.isEmpty()) {

                    lblStatus.setText(
                            "Status: Select a file first."
                    );

                    return;
                }


                // Check key

                if (key.isEmpty()) {

                    lblStatus.setText(
                            "Status: Enter a key."
                    );

                    return;
                }


                File inputFile =
                        new File(inputPath);


                if (!inputFile.exists()) {

                    lblStatus.setText(
                            "Status: File does not exist."
                    );

                    return;
                }


                // Read original file

                byte[] originalData =
                        FileUtils.readFileToBytes(
                                inputPath
                        );


                byte[] encryptedData;


                // Selected algorithm

                String algorithm =
                        (String)
                                comboAlgorithm
                                        .getSelectedItem();


                // =================================================
                // ENCRYPTION
                // =================================================

                switch (algorithm) {

                    case "DES":

                        encryptedData =
                                CryptoUtils.encryptDES(
                                        originalData,
                                        key
                                );

                        break;


                    case "2DES":

                        encryptedData =
                                CryptoUtils.encrypt2DES(
                                        originalData,
                                        key
                                );

                        break;


                    case "3DES":

                        encryptedData =
                                CryptoUtils.encrypt3DES(
                                        originalData,
                                        key
                                );

                        break;


                    case "AES-128":

                        encryptedData =
                                CryptoUtils.encryptAES128(
                                        originalData,
                                        key
                                );

                        break;


                    case "AES-192":

                        encryptedData =
                                CryptoUtils.encryptAES192(
                                        originalData,
                                        key
                                );

                        break;


                    case "AES-256":

                        encryptedData =
                                CryptoUtils.encryptAES256(
                                        originalData,
                                        key
                                );

                        break;


                    case "Blowfish":

                        encryptedData =
                                CryptoUtils.encryptBlowfish(
                                        originalData,
                                        key
                                );

                        break;


                    default:

                        lblStatus.setText(
                                "Unknown algorithm."
                        );

                        return;
                }


                // =================================================
                // GET ORIGINAL EXTENSION
                // =================================================

                String fileName =
                        inputFile.getName();


                int dotIndex =
                        fileName.lastIndexOf('.');


                String extension = "";

                if (dotIndex != -1) {

                    extension =
                            fileName.substring(
                                    dotIndex
                            );
                }


                // =================================================
                // CREATE OUTPUT NAME
                // =================================================

                String baseName =
                        inputPath;


                if (dotIndex != -1) {

                    int pathDotIndex =
                            inputPath.lastIndexOf('.');

                    if (pathDotIndex != -1) {

                        baseName =
                                inputPath.substring(
                                        0,
                                        pathDotIndex
                                );
                    }
                }


                String outputPath =
                        baseName +
                                "_encrypted.png";


                // =================================================
                // SAVE CIPHERTEXT AS IMAGE
                // =================================================

                ImageCipherUtils.saveCipherAsImage(
                        encryptedData,
                        extension,
                        outputPath
                );


                lblStatus.setText(
                        "Encrypted image saved: " +
                                outputPath
                );


                // Display encrypted image

                ImageIcon icon =
                        new ImageIcon(outputPath);

                Image image =
                        icon.getImage();

                Image scaled =
                        image.getScaledInstance(
                                250,
                                250,
                                Image.SCALE_SMOOTH
                        );

                lblPreview.setIcon(
                        new ImageIcon(scaled)
                );

                lblPreview.setText("");


            } catch (Exception ex) {

                ex.printStackTrace();

                lblStatus.setText(
                        "Encryption error: " +
                                ex.getMessage()
                );
            }
        });


        // =====================================================
        // DECRYPT BUTTON
        // =====================================================

        btnDecrypt.addActionListener(e -> {

            try {

                String inputPath =
                        txtFilePath.getText().trim();

                String key =
                        txtKey.getText();


                if (inputPath.isEmpty()) {

                    lblStatus.setText(
                            "Status: Select encrypted image."
                    );

                    return;
                }


                if (key.isEmpty()) {

                    lblStatus.setText(
                            "Status: Enter the key."
                    );

                    return;
                }


                // =================================================
                // READ CIPHERTEXT FROM PNG
                // =================================================

                byte[] encryptedData =
                        ImageCipherUtils
                                .readCipherFromImage(
                                        inputPath
                                );


                // =================================================
                // GET ORIGINAL EXTENSION
                // =================================================

                String originalExtension =
                        ImageCipherUtils
                                .getOriginalExtension(
                                        inputPath
                                );


                byte[] decryptedData;


                String algorithm =
                        (String)
                                comboAlgorithm
                                        .getSelectedItem();


                // =================================================
                // DECRYPTION
                // =================================================

                switch (algorithm) {

                    case "DES":

                        decryptedData =
                                CryptoUtils.decryptDES(
                                        encryptedData,
                                        key
                                );

                        break;


                    case "2DES":

                        decryptedData =
                                CryptoUtils.decrypt2DES(
                                        encryptedData,
                                        key
                                );

                        break;


                    case "3DES":

                        decryptedData =
                                CryptoUtils.decrypt3DES(
                                        encryptedData,
                                        key
                                );

                        break;


                    case "AES-128":

                        decryptedData =
                                CryptoUtils.decryptAES128(
                                        encryptedData,
                                        key
                                );

                        break;


                    case "AES-192":

                        decryptedData =
                                CryptoUtils.decryptAES192(
                                        encryptedData,
                                        key
                                );

                        break;


                    case "AES-256":

                        decryptedData =
                                CryptoUtils.decryptAES256(
                                        encryptedData,
                                        key
                                );

                        break;


                    case "Blowfish":

                        decryptedData =
                                CryptoUtils.decryptBlowfish(
                                        encryptedData,
                                        key
                                );

                        break;


                    default:

                        lblStatus.setText(
                                "Unknown algorithm."
                        );

                        return;
                }


                // =================================================
                // CREATE DECRYPTED FILE NAME
                // =================================================

                File encryptedFile =
                        new File(inputPath);


                String encryptedName =
                        encryptedFile.getName();


                int encryptedDot =
                        encryptedName.lastIndexOf('.');


                String nameWithoutExtension =
                        encryptedName;


                if (encryptedDot != -1) {

                    nameWithoutExtension =
                            encryptedName.substring(
                                    0,
                                    encryptedDot
                            );
                }


                // Remove "_encrypted"

                if (nameWithoutExtension
                        .endsWith("_encrypted")) {

                    nameWithoutExtension =
                            nameWithoutExtension.substring(
                                    0,
                                    nameWithoutExtension.length()
                                            - "_encrypted".length()
                            );
                }


                File parentFolder =
                        encryptedFile.getParentFile();


                String outputPath;


                if (parentFolder != null) {

                    outputPath =
                            new File(
                                    parentFolder,
                                    nameWithoutExtension
                                            + "_decrypted"
                                            + originalExtension
                            ).getAbsolutePath();

                } else {

                    outputPath =
                            nameWithoutExtension
                                    + "_decrypted"
                                    + originalExtension;
                }


                // =================================================
                // SAVE ORIGINAL FILE
                // =================================================

                FileUtils.writeBytesToFile(
                        outputPath,
                        decryptedData
                );


                lblStatus.setText(
                        "Decrypted successfully: " +
                                outputPath
                );


                // =================================================
                // SHOW DECRYPTED IMAGE
                // =================================================

                ImageIcon icon =
                        new ImageIcon(outputPath);

                Image image =
                        icon.getImage();

                Image scaled =
                        image.getScaledInstance(
                                250,
                                250,
                                Image.SCALE_SMOOTH
                        );

                lblPreview.setIcon(
                        new ImageIcon(scaled)
                );

                lblPreview.setText("");


            } catch (Exception ex) {

                ex.printStackTrace();

                lblStatus.setText(
                        "Decryption error: " +
                                ex.getMessage()
                );
            }
        });
    }
}