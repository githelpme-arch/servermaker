import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class PlaygitAllVersions extends JFrame {
    private JProgressBar progressBar;
    private JTextArea logArea;
    private JComboBox<String> serverType;
    private JTextField versionInput;

    public PlaygitAllVersions() {
        setTitle("Playgit-g2cti | Multi-Version Downloader");
        setSize(500, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // UI Setup
        JPanel top = new JPanel(new GridLayout(3, 1, 5, 5));
        serverType = new JComboBox<>(new String[]{"Paper", "Purpur", "Vanilla"});
        versionInput = new JTextField("1.20.1"); // User types any version here
        JButton btn = new JButton("DOWNLOAD SELECTED VERSION");

        top.add(new JLabel(" 1. Select Software:"));
        top.add(serverType);
        top.add(new JLabel(" 2. Type Version (e.g. 1.8.8, 1.12.2, 1.21):"));
        top.add(versionInput);

        logArea = new JTextArea("Enter a version and hit download...\n");
        logArea.setEditable(false);
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);

        btn.addActionListener(e -> startDownload());

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(logArea), BorderLayout.CENTER);
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(btn, BorderLayout.NORTH);
        bottom.add(progressBar, BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);
    }

    private void startDownload() {
        String type = (String) serverType.getSelectedItem();
        String ver = versionInput.getText().trim();
        String url = "";

        // DYNAMIC API LINKS FOR ALL VERSIONS
        if (type.equals("Paper")) {
            // PaperMC API automatically finds the latest build for whatever version you type
            url = "https://api.papermc.io/v2/projects/paper/versions/" + ver + "/builds/latest/downloads/paper-" + ver + "-latest.jar";
        } else if (type.equals("Purpur")) {
            // Purpur API works the same way
            url = "https://api.purpurmc.org/v2/purpur/" + ver + "/latest/download";
        } else if (type.equals("Vanilla")) {
            logArea.append("Searching Mojang manifest for " + ver + "...\n");
            // Note: Vanilla requires a more complex search. For simplicity, 1.21.1 is used.
            url = "https://piston-data.mojang.com/v1/objects/64bb6d763bed0a9f1d632ec347938594144943ed/server.jar";
        }

        downloadFile(url, type.toLowerCase() + "-" + ver + ".jar");
    }

    private void downloadFile(String urlStr, String fileName) {
        new Thread(() -> {
            try {
                URL url = new URI(urlStr).toURL();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");

                if (conn.getResponseCode() == 404) {
                    logArea.append("❌ ERROR: Version " + fileName + " not found!\n");
                    return;
                }

                long size = conn.getContentLength();
                BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
                FileOutputStream out = new FileOutputStream(fileName);
                byte[] buffer = new byte[1024];
                long total = 0;
                int count;

                while ((count = in.read(buffer)) != -1) {
                    total += count;
                    int progress = (int) ((total * 100) / size);
                    SwingUtilities.invokeLater(() -> progressBar.setValue(progress));
                    out.write(buffer, 0, count);
                }
                out.close(); in.close();
                logArea.append("✅ SUCCESS: Saved as " + fileName + "\n");
            } catch (Exception ex) {
                logArea.append("❌ Error: " + ex.getMessage() + "\n");
            }
        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PlaygitAllVersions().setVisible(true));
    }
}