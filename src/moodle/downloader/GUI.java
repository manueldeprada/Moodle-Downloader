/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moodle.downloader;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;

/**
 *
 * @author prada
 */
public class GUI extends javax.swing.JFrame {

    private boolean overwrite = false;
    private String baseurl = "";
    private String cookie = "MoodleSession=pemhbkdjseflph6p43qm1v5ce7";
    private String carpetaBase = "descargas";
    private Downloader down;

    /**
     * Creates new form GUI
     */
    public GUI() {
        initComponents();
        JPopupMenu popup = new JPopupMenu();
        cookieBox.add(popup);
        cookieBox.setComponentPopupMenu(popup);
        tocopyText.add(popup);
        tocopyText.setComponentPopupMenu(popup);
        term.add(popup);
        term.setComponentPopupMenu(popup);

        Action cut = new DefaultEditorKit.CutAction();
        cut.putValue(Action.NAME, "Cut");
        cut.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control X"));
        popup.add(cut);

        Action copy = new DefaultEditorKit.CopyAction();
        copy.putValue(Action.NAME, "Copy");
        copy.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control C"));
        popup.add(copy);

        Action paste = new DefaultEditorKit.PasteAction();
        paste.putValue(Action.NAME, "Paste");
        paste.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control V"));
        popup.add(paste);

        new SmartScroller(this.outputPane, SmartScroller.VERTICAL, SmartScroller.END);

        dirLabel.setText(System.getProperty("user.dir") + File.separator + carpetaBase);
        DefaultListModel<String> model = new DefaultListModel<>();
        lista.setModel(model);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pastelabel = new javax.swing.JLabel();
        toPasteCodePane = new javax.swing.JScrollPane();
        tocopyText = new javax.swing.JTextPane();
        cookieBox = new javax.swing.JTextField();
        loadButton = new javax.swing.JButton();
        copyButton = new javax.swing.JButton();
        coursesPane = new javax.swing.JScrollPane();
        lista = new javax.swing.JList<>();
        instructions = new javax.swing.JLabel();
        coursesLabel = new javax.swing.JLabel();
        barra = new javax.swing.JProgressBar();
        outputPane = new javax.swing.JScrollPane();
        term = new javax.swing.JTextArea();
        videoButton = new javax.swing.JButton();
        urlLabel = new javax.swing.JLabel();
        urlField = new javax.swing.JTextField();
        folderLabel = new javax.swing.JLabel();
        folderButton = new javax.swing.JButton();
        authorLabel = new javax.swing.JLabel();
        githubLabel = new javax.swing.JLabel();
        folderPane = new javax.swing.JScrollPane();
        dirLabel = new javax.swing.JTextArea();
        currentFolderLabel = new javax.swing.JLabel();
        overrideCheckBox = new javax.swing.JCheckBox();
        exploredLabel = new javax.swing.JLabel();
        exploredFiles = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        downloadedLabel = new javax.swing.JLabel();
        downloadedFiles = new javax.swing.JLabel();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Moodle Downloader");
        setMinimumSize(new java.awt.Dimension(620, 640));

        pastelabel.setText("Paste this in your browser console:");

        tocopyText.setEditable(false);
        tocopyText.setText("(function() {\n    var a=RegExp(\"\"+\"MoodleSession\"+\"[^;]+\").exec(document.cookie); \n    var u=\"MoodleSession=\"+unescape(!!a ? a.toString().replace(/^[^=]+./,\"\") : \"\"); \n    copy(u);\n    console.log(u);\n})();");
        toPasteCodePane.setViewportView(tocopyText);

        cookieBox.setText("Paste here the cookie");
        cookieBox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cookieBoxFocusGained(evt);
            }
        });

        loadButton.setText("Download!");
        loadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadButtonActionPerformed(evt);
            }
        });

        copyButton.setText("Copy");
        copyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyButtonActionPerformed(evt);
            }
        });

        coursesPane.setViewportView(lista);

        instructions.setText("<html>Instructions:<br>\n1.Log in to your moodle account.<br>\n2.Right-click in any point of the page.<br>\n3.Click \"Inspect element\".<br>\n4.Choose \"Console\" tab.<br>\n5.Paste the code given here.<br>\n6.Paste back the result here and hit the \"Load\" button.<br>\n");

        coursesLabel.setText("Courses:");

        term.setEditable(false);
        term.setColumns(20);
        term.setRows(5);
        outputPane.setViewportView(term);

        videoButton.setText("Help! How to get the cookie??");
        videoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                videoButtonActionPerformed(evt);
            }
        });

        urlLabel.setText("Base Moodle URL");

        urlField.setText("https://cv.usc.es");

        folderLabel.setText("Download base folder");

        folderButton.setText("Choose folder");
        folderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                folderButtonActionPerformed(evt);
            }
        });

        authorLabel.setText("By Manuel de Prada");

        githubLabel.setText("github.com/manudroid19");

        dirLabel.setColumns(20);
        folderPane.setViewportView(dirLabel);

        currentFolderLabel.setText("Current folder:");

        overrideCheckBox.setText("Overwrite files?");
        overrideCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                overrideCheckBoxActionPerformed(evt);
            }
        });

        exploredLabel.setText("Explored files:");

        exploredFiles.setText("0");

        downloadedLabel.setText("Downloaded files:");

        downloadedFiles.setText("0");

        cancelButton.setText("Cancel");
        cancelButton.setEnabled(false);
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(barra, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addComponent(loadButton)
                        .addGap(18, 18, 18)
                        .addComponent(cancelButton)
                        .addGap(82, 82, 82)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(instructions, javax.swing.GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(folderLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(folderButton))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(urlLabel)
                                .addGap(18, 18, 18)
                                .addComponent(urlField))
                            .addComponent(folderPane, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(currentFolderLabel)
                                    .addComponent(overrideCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(pastelabel)
                                                .addGap(18, 18, 18)
                                                .addComponent(copyButton))
                                            .addComponent(cookieBox, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
                                            .addComponent(toPasteCodePane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                                        .addGap(33, 33, 33)
                                        .addComponent(videoButton))
                                    .addComponent(coursesLabel))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(coursesPane, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(authorLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(githubLabel)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(downloadedLabel)
                                                .addGap(18, 18, 18))
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(exploredLabel)
                                                .addGap(35, 35, 35)))
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(downloadedFiles, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                                            .addComponent(exploredFiles, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGap(0, 0, Short.MAX_VALUE))))))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(outputPane, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(pastelabel)
                            .addComponent(copyButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(toPasteCodePane, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cookieBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(loadButton)
                            .addComponent(cancelButton)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(instructions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(videoButton)
                        .addGap(24, 24, 24)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(urlLabel)
                            .addComponent(urlField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(folderLabel)
                            .addComponent(folderButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(currentFolderLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(folderPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(overrideCheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 5, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(coursesLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(coursesPane, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(exploredLabel)
                                            .addComponent(exploredFiles))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(downloadedLabel)
                                            .addComponent(downloadedFiles)))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(authorLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(githubLabel)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(barra, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(outputPane, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void cookieBoxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cookieBoxFocusGained
        if (cookieBox.getText().contains("Paste here")) {
            cookieBox.setText("");
        }
    }//GEN-LAST:event_cookieBoxFocusGained

    private void loadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadButtonActionPerformed
        baseurl = this.urlField.getText();
        carpetaBase = dirLabel.getText();
        cookie = cookieBox.getText();
        if(!cookie.contains("MoodleSession=")){
            cookie="MoodleSession="+cookie;
        }
        cookieBox.setText(cookie);
        down = new Downloader(this);
        down.startDownload();
    }//GEN-LAST:event_loadButtonActionPerformed

    private void copyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyButtonActionPerformed
        StringSelection stringSelection = new StringSelection(tocopyText.getText());
        Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
        clpbrd.setContents(stringSelection, null);

    }//GEN-LAST:event_copyButtonActionPerformed

    private void folderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_folderButtonActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Select the download directory");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            dirLabel.setText(chooser.getSelectedFile().getPath());
            carpetaBase = chooser.getSelectedFile().getPath();
        }
    }//GEN-LAST:event_folderButtonActionPerformed

    private void videoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_videoButtonActionPerformed

        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(new URL("https://youtu.be/XjOylmSQAwE").toURI());
            } catch (URISyntaxException | IOException ex) {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_videoButtonActionPerformed

    private void overrideCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_overrideCheckBoxActionPerformed
        overwrite = this.overrideCheckBox.isSelected();
    }//GEN-LAST:event_overrideCheckBoxActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        if(down!=null){
            down.stopDownload();
        }
    }//GEN-LAST:event_cancelButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("GTK+".equals(info.getName()) || "Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        java.awt.EventQueue.invokeLater(() -> {
            new GUI().setVisible(true);
        });

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel authorLabel;
    private javax.swing.JProgressBar barra;
    private javax.swing.JButton cancelButton;
    private javax.swing.JTextField cookieBox;
    private javax.swing.JButton copyButton;
    private javax.swing.JLabel coursesLabel;
    private javax.swing.JScrollPane coursesPane;
    private javax.swing.JLabel currentFolderLabel;
    private javax.swing.JTextArea dirLabel;
    private javax.swing.JLabel downloadedFiles;
    private javax.swing.JLabel downloadedLabel;
    private javax.swing.JLabel exploredFiles;
    private javax.swing.JLabel exploredLabel;
    private javax.swing.JButton folderButton;
    private javax.swing.JLabel folderLabel;
    private javax.swing.JScrollPane folderPane;
    private javax.swing.JLabel githubLabel;
    private javax.swing.JLabel instructions;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JList<String> lista;
    private javax.swing.JButton loadButton;
    private javax.swing.JScrollPane outputPane;
    private javax.swing.JCheckBox overrideCheckBox;
    private javax.swing.JLabel pastelabel;
    private static javax.swing.JTextArea term;
    private javax.swing.JScrollPane toPasteCodePane;
    private javax.swing.JTextPane tocopyText;
    private javax.swing.JTextField urlField;
    private javax.swing.JLabel urlLabel;
    private javax.swing.JButton videoButton;
    // End of variables declaration//GEN-END:variables

    public boolean isOverwrite() {
        return overwrite;
    }

    public String getBaseurl() {
        if(baseurl.charAt(baseurl.length()-1)=='/'){
            return baseurl.substring(0,baseurl.length()-1);
        }
        return baseurl;
    }

    public String getCookie() {
        return cookie;
    }

    public String getCarpetaBase() {
        return carpetaBase;
    }

    public javax.swing.JProgressBar getBarra() {
        return barra;
    }

    public static javax.swing.JTextArea getTerm() {
        return term;
    }

    public void setCarpetaBase(String carpetaBase) {
        this.carpetaBase = carpetaBase;
    }

    public javax.swing.JList<String> getLista() {
        return lista; 
    }

    public javax.swing.JLabel getDownloadedFiles() {
        return downloadedFiles;
    }

    public javax.swing.JLabel getExploredFiles() {
        return exploredFiles;
    }
    public void setDonwloadButton(boolean enabled){
        loadButton.setEnabled(enabled);
    }

    public void setCancelButton(boolean b) {
        cancelButton.setEnabled(b);
    }
}
