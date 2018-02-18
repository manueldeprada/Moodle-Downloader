/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moodle.downloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;

/**
 *
 * @author mprad
 */
public class DownWorker implements Runnable {

    private HashMap<String, String> paquete;
    private String cookie;
    private JTextArea term;
    private boolean overwrite;
    private Downloader down;

    public DownWorker(HashMap<String, String> paquete, String cookie, JTextArea term, boolean overwrite, Downloader down) {
        this.paquete = paquete;
        this.cookie = cookie;
        this.term = term;
        this.overwrite = overwrite;
        this.down = down;
    }

    @Override
    public void run() {
        for (String url : paquete.keySet()) {
            String ruta = paquete.get(url);
            try {
                download(url, ruta);
            } catch (MalformedURLException ex) {
                Downloader.log(ex);
            } catch (IOException | URISyntaxException ex) {
                try {
                    download(url,ruta);
                } catch (IOException | URISyntaxException ex1) {
                    Downloader.log(ex);
                }
            }
        }
    }

    private void downloadFile(URL lnk, String dirr) {
        File donde = null;
        try {
            File file = new File(lnk.toURI().getPath());
            String name = file.getName();
            if (overwrite || !new File(dirr + File.separator + name).exists()) {
                term.append("\n" + "Downloading: " + dirr + File.separator + name);
                HttpURLConnection con2 = (HttpURLConnection) lnk.openConnection();
                con2.setRequestProperty("Cookie", cookie);
                ReadableByteChannel rbc = Channels.newChannel(con2.getInputStream());
                donde = new File(dirr + File.separator + name);
                donde.createNewFile();
                FileOutputStream fos = new FileOutputStream(donde);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            } else {
                term.append("\n" + "Already downloaded: " + dirr + File.separator + name);
            }
            down.descargado();
        } catch (URISyntaxException ex) {
            Downloader.log(ex);
        } catch (IOException ex) {
            System.out.println(donde.getPath());
            System.out.println(ex.getMessage());

        }
    }

    private void download(String url, String ruta) throws MalformedURLException, IOException, URISyntaxException {
        URL linkUrl = new URL(url);
        HttpURLConnection con = (HttpURLConnection) linkUrl.openConnection();
        con.setRequestProperty("Cookie", cookie);

        InputStream is = con.getInputStream();

        Scanner scanner = null;
        if (con.getURL().toString().contains("/course/view.php?id=")) {
            term.append("\n" + "No hay permisos.");//acceso no permitido
        } else if (con.getURL().toString().equals(url) && !url.contains("mod_folder/content")) {
            scanner = new Scanner(is, "UTF-8");
            scanner.useDelimiter("\\Z");
            String content = scanner.next();

            String[] split = content.split("mod_resource");
            URL lnk = new URL(content.substring(split[0].lastIndexOf("https"), split[0].length() + split[1].indexOf("\"") + 12));
            downloadFile(lnk, ruta);
        } else {
            File file = new File(con.getURL().toURI().getPath());
            String name = file.getName();
            if (overwrite || !new File(ruta + File.separator + name).exists()) {
                term.append("\n" + "Downloading: " + ruta + File.separator + name);
                ReadableByteChannel rbc = Channels.newChannel(is);
                FileOutputStream fos = new FileOutputStream(ruta + File.separator + name);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            } else {
                term.append("\n" + "Already downloaded: " + ruta + File.separator + name);
            }
            down.descargado();
        }
    }

}
