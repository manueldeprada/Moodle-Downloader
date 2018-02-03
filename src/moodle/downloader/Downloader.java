/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moodle.downloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

/**
 *
 * @author mprad
 */
public class Downloader {

    private String baseurl;
    private String cookie;
    private JTextArea term;
    private String folder;
    private JList<String> lista;
    private JProgressBar barra;
    private float paso;
    private boolean overwrite;

    public Downloader(String baseurl, String cookie, JTextArea term, String folder, JList<String> lista, JProgressBar barra, float paso, boolean overwrite) {
        this.baseurl = baseurl;
        this.cookie = cookie;
        this.term = term;
        this.folder = folder;
        this.cookie = cookie;
        this.barra = barra;
        this.paso = paso;
        this.overwrite = overwrite;
    }

    public void startDownload() {
        Thread thread = new Thread(new MyRunnable());
        thread.start();

    }

    public class MyRunnable implements Runnable {

        public void run() {
            if (term.getText().contains("ERROR")) {
                term.append("\n");
            }
            term.append("Connecting to moodle...");
            loadCursos();
        }
    }

    private void loadCursos() {
        String content = null;

        URL myUrl;
        URLConnection urlConn;
        try {
            myUrl = new URL(baseurl + "/my/");
            urlConn = myUrl.openConnection();
            urlConn.setRequestProperty("Cookie", cookie);
            urlConn.connect();
            Scanner scanner = new Scanner(urlConn.getInputStream(), "UTF-8");
            scanner.useDelimiter("\\Z");
            term.append("\n" + "Fetching courses...");
            content = scanner.next();
            String[] cursosR = content.split(baseurl + "/course/view.php\\?id=");
            List<Integer> cursos = new ArrayList<>();
            List<String> conts = new ArrayList<>();
            for (int i = 1; i < cursosR.length; i++) {
                int s = Integer.parseInt(cursosR[i].substring(0, 4).replaceAll("[^0-9]", ""));
                if (!cursos.contains(s)) {
                    cursos.add(s);
                }
            }
            DefaultListModel<String> model = new DefaultListModel<>();
            lista.setModel(model);
            barra.setValue((int) (paso = ((float) 1 / (cursos.size() + 1)) * 100));

            for (Integer curso : cursos) {
                conts.add(getCont(curso, "course"));
                model.addElement(getTitle(conts.get(cursos.indexOf(curso))));
            }
            term.append("\n" + "Starting download...");
            for (Integer curso : cursos) {
                descargarCurso(conts.get(cursos.indexOf(curso)), curso);
                barra.setValue((int) (((float) (cursos.indexOf(curso) + 2) / (cursos.size() + 1)) * 100));
            }
        } catch (ProtocolException ex) {
            term.append("\n" + "ERROR: Incorrect cookie");
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            term.append("\n" + "ERROR: URL is incorrect");
        } catch (IOException ex) {
            log(ex);
        }
        term.append("\n" + "Descarga finalizada!");
        barra.setValue(0);
    }

    private void descargarCurso(String cont, int id) {
        String tit = getTitle(cont);
        String title = tit.split(" \\[")[0];

        File dir = new File(folder + File.separator + title);
        dir.mkdirs();
        new File(dir.getPath() + File.separator + "html").mkdirs();
        File html = new File(dir.getPath() + File.separator + "html" + File.separator + title + ".html");

        try (PrintWriter out = new PrintWriter(html)) {
            out.println(cont);
        } catch (FileNotFoundException ex) {
            log(ex);
        }
        String[] split_res = cont.split("activity resource modtype\\_resource");
        List<Integer> resources = new ArrayList<>();
        for (int i = 1; i < split_res.length; i++) {
            resources.add(Integer.parseInt(split_res[i].substring(14, 19).replaceAll("[^0-9]", "")));
        }
        downloadRes(resources, id, title);
        barra.setValue((int) (barra.getValue() + 0.2 * paso));
        String[] split_folder = cont.split("activity folder modtype\\_folder");
        List<Integer> folders = new ArrayList<>();
        for (int i = 1; i < split_folder.length; i++) {
            folders.add(Integer.parseInt(split_folder[i].substring(14, 19).replaceAll("[^0-9]", "")));
        }
        downloadFolders(folders, tit, folder + File.separator + title);

        //int forum = Integer.parseInt(cont.split("activity forum modtype\\_forum")[1].substring(14,19).replaceAll("[^0-9]", ""));
        //String forumHtml = getCont(forum,"mod/forum");
        String[] split_sections = cont.split("id=\"section-");
        List<Integer> sections = new ArrayList<>();

        for (int i = 1; i < split_sections.length; i++) {
            sections.add(Integer.parseInt(split_sections[i].substring(0, 1)));
        }
        downloadSections(sections, id, title);
    }

    private void downloadSections(List<Integer> sections, int id, String dir) {
        for (Integer sec : sections) {
            String section = getContPlus(id, "course", sec);
            if (!section.contains("single-section")) {
                continue;
            }
            String title = getTitleSection(section).trim();
            section = section.split("sectionname")[section.split("sectionname").length - 1];
            File dirf = new File(folder + File.separator + dir + File.separator + title);
            if (dirf.exists()) {
                for (int i = 1; i < 30; i++) {
                    dirf = new File(folder + File.separator + dir + File.separator + title + "(" + i + ")");
                    if (!dirf.exists()) {
                        break;
                    }
                }
            }
            dirf.mkdirs();
            String[] split_res = section.split("activity resource modtype\\_resource");
            List<Integer> resources = new ArrayList<>();
            for (int i = 1; i < split_res.length; i++) {
                resources.add(Integer.parseInt(split_res[i].substring(14, 19).replaceAll("[^0-9]", "")));
            }

            downloadRes(resources, id, dir + File.separator + title);

        }
    }

    private void downloadRes(List<Integer> resources, int id, String title) {
        for (Integer res : resources) {
            try {
                String link = baseurl + "/mod/resource/view.php?id=" + res;
                URL linkUrl = new URL(link);
                HttpURLConnection con = (HttpURLConnection) linkUrl.openConnection();
                con.setRequestProperty("Cookie", cookie);

                //term.append("\n"+"link: "+link);
                InputStream is = con.getInputStream();

                Scanner scanner = null;
                if (con.getURL().toString().equals(baseurl + "/course/view.php?id=" + id)) {
                    term.append("\n" + "No hay permisos.");//acceso no permitido
                } else if (con.getURL().toString().equals(link)) {
                    scanner = new Scanner(is, "UTF-8");
                    scanner.useDelimiter("\\Z");
                    String content = scanner.next();

                    String[] split = content.split("mod_resource");
                    URL lnk = new URL(content.substring(split[0].lastIndexOf("https"), split[0].length() + split[1].indexOf("\"") + 12));
                    downloadFile(lnk, folder + File.separator + title);

                } else {
                    File file = new File(con.getURL().toURI().getPath());
                    String name = file.getName();
                    if (overwrite || !new File(folder + File.separator + title + File.separator + name).exists()) {
                        term.append("\n" + "Downloading: " + folder + File.separator + title + File.separator + name);
                        ReadableByteChannel rbc = Channels.newChannel(is);
                        FileOutputStream fos = new FileOutputStream(folder + File.separator + title + File.separator + name);
                        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                    } else {
                        term.append("\n" + "Already downloaded: " + folder + File.separator + title + File.separator + name);
                    }
                }

            } catch (MalformedURLException ex) {
                log(ex);
            } catch (IOException | URISyntaxException ex) {
                log(ex);
            }
        }
    }

    private void downloadFolders(List<Integer> folders, String curso, String dir) {
        for (Integer fol : folders) {
            String cont = getCont(fol, "mod/folder");
            String title = getTitle(cont).replace(curso, "").replace(": ", "").trim();

            File dirf = new File(dir + File.separator + title);
            if (dirf.exists()) {
                for (int i = 1; i < 30; i++) {
                    dirf = new File(dir + File.separator + title + "(" + i + ")");
                    if (!dirf.exists()) {
                        break;
                    }
                }
            }
            dirf.mkdirs();
            String[] split_res = cont.split("fp-filename-icon\"><a href=\"");
            List<String> resources = new ArrayList<>();
            for (int i = 1; i < split_res.length; i++) {
                try {
                    downloadFile(new URL(split_res[i].substring(0, split_res[i].indexOf("\">"))), dirf.getPath());
                } catch (MalformedURLException ex) {
                    log(ex);
                }
                //term.append("\n"+split_res[i].substring(0,split_res[i].indexOf("\">")));
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
        } catch (URISyntaxException ex) {
            log(ex);
        } catch (IOException ex) {
            System.out.println(donde.getPath());
            System.out.println(ex.getMessage());

        }
    }

    private String getTitle(String content) {
        String title = content.substring(content.indexOf("<title>") + 7, content.indexOf("</title>")); //Curso: 
        return title.substring(title.indexOf(": ") + 2, title.length());
    }

    private String getContPlus(int id, String type, int section) {
        String content = null;
        URL myUrl;
        URLConnection urlConn;
        try {
            myUrl = new URL(baseurl + "/" + type + "/view.php?id=" + id + "&section=" + section);

            urlConn = myUrl.openConnection();
            urlConn.setRequestProperty("Cookie", cookie);
            urlConn.connect();
            Scanner scanner = new Scanner(urlConn.getInputStream(), "UTF-8");
            scanner.useDelimiter("\\Z");
            content = scanner.next();
        } catch (MalformedURLException ex) {
            log(ex);
        } catch (IOException ex) {
            log(ex);
        }
        return content;
    }

    private String getCont(int id, String type) {
        String content = null;
        URL myUrl;
        URLConnection urlConn;
        try {
            myUrl = new URL(baseurl + "/" + type + "/view.php?id=" + id);

            urlConn = myUrl.openConnection();
            urlConn.setRequestProperty("Cookie", cookie);
            urlConn.connect();
            Scanner scanner = new Scanner(urlConn.getInputStream(), "UTF-8");
            scanner.useDelimiter("\\Z");
            content = scanner.next();
        } catch (MalformedURLException ex) {
            log(ex);
        } catch (IOException ex) {
            log(ex);
        }
        return content;
    }

    private String getTitleSection(String section) {
        String[] partir = section.split("class=\"sectionname\"><span>");
        String a = partir[1].split("</span>")[0];
        a = a.replaceAll(":", ".");
        if (a.charAt(a.length() - 1) == '.') {
            a = a.substring(0, a.length() - 1);
        }
        return a;
    }

    private void log(Exception ex) {
        Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        term.append("\n" + "ERROR: Unknown error. Debug info: " + sw.toString());

    }
}
