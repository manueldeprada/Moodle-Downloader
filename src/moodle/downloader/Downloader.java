/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moodle.downloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;

/**
 *
 * @author mprad
 */
public class Downloader {

    private String baseurl;
    private String cookie;
    private GUI gui;
    private String carpetaBase;
    private boolean overwrite;
    private HashMap<String, String> resources;
    private List<Integer> cursos;
    private List<Integer> acabados;
    private static int archivos = -1;
    private static int descargados = 0;
    private Thread thread;
    private ArrayList<Thread> threads = new ArrayList<>();

    Downloader(GUI gui) {
        this.gui = gui;
        this.baseurl = gui.getBaseurl();
        this.cookie = gui.getCookie();
        this.carpetaBase = gui.getCarpetaBase();
        this.overwrite = gui.isOverwrite();
        resources = new HashMap<>();    //HashMap URL, carpeta
        acabados = new ArrayList<>();
        cursos = new ArrayList<>();
    }

    public void startDownload() {
        thread = new Thread(new WorkerMainThread());
        thread.start();
    }

    public void stopDownload() {
        if (thread != null) {
            thread.stop();
        }
        for (Thread th : threads) {
            if (th != null) {
                th.stop();
            }
        }
        gui.setDonwloadButton(true);
        gui.setCancelButton(false);
        gui.getBarra().setValue(0);
        GUI.getTerm().append("\n" + "Process canceled");
    }

    private void setCursoAcabado(int idCurso) {
        acabados.add(idCurso);
        int progreso = 40 * acabados.size() / cursos.size();
        gui.getBarra().setValue(10 + progreso);
        if (acabados.size() != cursos.size()) {
            return;
        }
        archivos = resources.size();
        descargados=0;
        int factor = 10;
        int lotes = (int) Math.ceil(((double) resources.size()) / (double) factor);
        HashMap<String, String> resourcesADescargar = (HashMap<String, String>) resources.clone();
        Iterator<String> it = resourcesADescargar.keySet().iterator();
        for (int i = 0; i < factor; i++) { //recorro los paquetes
            HashMap<String, String> paquete = new HashMap<>();
            for (int j = 0; j < lotes; j++) { //meto en los paquetes
                if (it.hasNext()) {
                    String clave = it.next();
                    paquete.put(clave, resourcesADescargar.get(clave));
                }
            }
            DownWorker worker = new DownWorker(paquete, cookie, GUI.getTerm(), overwrite, this);
            Thread th = new Thread(worker);
            threads.add(th);
            th.start();
        }
    }

    public class WorkerMainThread implements Runnable {

        @Override
        public void run() {
            if (GUI.getTerm().getText().length() != 0) {
                GUI.getTerm().append("\n");
            }
            GUI.getTerm().append("Connecting to moodle...");
            loadCursos();
        }
    }

    private void loadCursos() {
        String content = null;

        URL myUrl;
        URLConnection urlConn;
        try {
            gui.setDonwloadButton(false);
            gui.setCancelButton(true);
            myUrl = new URL(baseurl + "/my/"); //Abro conexion para getear cursos
            urlConn = myUrl.openConnection();
            urlConn.setRequestProperty("Cookie", cookie);
            urlConn.connect();
            Scanner scanner = new Scanner(urlConn.getInputStream(), "UTF-8");
            scanner.useDelimiter("\\Z");

            GUI.getTerm().append("\n" + "Fetching courses...");
            content = scanner.next(); //bajando cursos...
            gui.getBarra().setValue(3);

            String[] cursosR = content.split(baseurl + "/course/view.php\\?id="); //Spliteamos y conseguimos los numeros de curso
            List<String> contenidoCursos = new ArrayList<>();
            for (int i = 1; i < cursosR.length; i++) {
                int s = Integer.parseInt(cursosR[i].substring(0, 4).replaceAll("[^0-9]", ""));
                if (!cursos.contains(s)) {
                    cursos.add(s);
                }
            }
            ((DefaultListModel<String>) gui.getLista().getModel()).removeAllElements();
            gui.getDownloadedFiles().setText("0");
            gui.getExploredFiles().setText("0");
            for (Integer curso : cursos) { //bajamos el html de los cursos y los añadimos a la lista
                contenidoCursos.add(getHtml(curso, "course"));
                ((DefaultListModel<String>) gui.getLista().getModel()).addElement(getTitle(contenidoCursos.get(cursos.indexOf(curso))));
                gui.getBarra().setValue(3 + 7 * (cursos.indexOf(curso)) / (cursosR.length - 1));
            }

            GUI.getTerm().append("\n" + "Starting exploring...");
            gui.getBarra().setValue(10);
            for (Integer curso : cursos) {//Procesamos y descargamos contenidos curso a curso
                Thread th = new Thread(new CursoThread(curso, contenidoCursos.get(cursos.indexOf(curso)), this));
                threads.add(th);
                th.start();
            }
        } catch (ProtocolException ex) {
            GUI.getTerm().append("\n" + "ERROR: Incorrect cookie");
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            gui.setDonwloadButton(true);
            gui.setCancelButton(false);
        } catch (FileNotFoundException ex) {
            GUI.getTerm().append("\n" + "ERROR: URL is incorrect");
            gui.setDonwloadButton(true);
            gui.setCancelButton(false);
        } catch (IOException ex) {
            log(ex);
            gui.setDonwloadButton(true);
            gui.setCancelButton(false);
        }

    }

    public void descargarCurso(String htmlCurso, int idCurso) {
        String tituloCursoConCodigo = getTitle(htmlCurso);
        String tituloCurso;
        if (tituloCursoConCodigo.contains(" \\[")) {
            tituloCurso = tituloCursoConCodigo.split(" \\[")[0];
        } else {
            tituloCurso = tituloCursoConCodigo;
        }

        File directorioCurso = new File(carpetaBase + File.separator + tituloCurso);
        directorioCurso.mkdirs();

        guardarHtml(directorioCurso, tituloCurso, htmlCurso);

        String[] split_folder = htmlCurso.split("activity folder modtype\\_folder");
        List<Integer> folders = new ArrayList<>();
        for (int i = 1; i < split_folder.length; i++) {
            folders.add(Integer.parseInt(split_folder[i].substring(14, 19).replaceAll("[^0-9]", "")));
        }
        procesarCarpetas(folders, tituloCursoConCodigo, directorioCurso.getPath(), resources);

        //int forum = Integer.parseInt(cont.split("activity forum modtype\\_forum")[1].substring(14,19).replaceAll("[^0-9]", ""));
        //String forumHtml = getHtml(forum,"mod/forum");
        String[] split_sections = htmlCurso.split("id=\"section-");
        List<Integer> sections = new ArrayList<>();
        for (int i = 1; i < split_sections.length; i++) {
            int s = Integer.parseInt(split_sections[i].substring(0, 1));
            if (!sections.contains(s)) {
                sections.add(s);
            }
        }
        procesarSections(sections, idCurso, tituloCurso, resources);

        String[] split_res = htmlCurso.split("activity resource modtype\\_resource");
        for (int i = 1; i < split_res.length; i++) {
            saveRecurso(baseurl + "/mod/resource/view.php?id=" + Integer.parseInt(split_res[i].substring(14, 19).replaceAll("[^0-9]", "")), directorioCurso.getPath());
        }
        GUI.getTerm().append("\n" + "Course " + tituloCurso + " explored.");
        setCursoAcabado(idCurso);
    }

    private void procesarSections(List<Integer> sections, int idCurso, String tituloCurso, HashMap<String, String> recursos) {
        for (Integer sec : sections) {

            String htmlSeccion = getHtmlSeccion(idCurso, "course", sec);
            if (htmlSeccion == null || !htmlSeccion.contains("single-section")) {
                continue;
            }
            String tituloSeccion = getTitleSection(htmlSeccion).trim();

            htmlSeccion = htmlSeccion.split("sectionname")[htmlSeccion.split("sectionname").length - 1];
            File directorioSeccion = new File(carpetaBase + File.separator + tituloCurso + File.separator + tituloSeccion);

            directorioSeccion.mkdirs();
            String[] split_res = htmlSeccion.split("activity resource modtype\\_resource");
            for (int i = 1; i < split_res.length; i++) {
                saveRecurso(baseurl + "/mod/resource/view.php?id=" + Integer.parseInt(split_res[i].substring(14, 19).replaceAll("[^0-9]", "")), carpetaBase + File.separator + tituloCurso + File.separator + tituloSeccion);
            }
        }
    }

    private void procesarCarpetas(List<Integer> folders, String tituloCursoConCodigo, String directorioCurso, HashMap<String, String> recursos) {
        for (Integer folderInt : folders) {
            String htmlCarpeta = getHtml(folderInt, "mod/folder");
            String nombreCarpeta = getTitle(htmlCarpeta).replace(tituloCursoConCodigo, "").replace(": ", "").trim();

            File directorioCarpeta = new File(directorioCurso + File.separator + nombreCarpeta);

            directorioCarpeta.mkdirs();

            String[] split_res = htmlCarpeta.split("fp-filename-icon\"><a href=\"");
            for (int i = 1; i < split_res.length; i++) {
                saveRecurso(split_res[i].substring(0, split_res[i].indexOf("\">")), directorioCarpeta.getPath());
            }
        }
    }

    private String getHtmlSeccion(int id, String type, int section) {
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
        } catch (FileNotFoundException ex) {
            return null;
        } catch (IOException ex) {
            log(ex);
        }
        return content;
    }

    private String getHtml(int id, String type) {
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

    private void guardarHtml(File directorioCurso, String tituloCurso, String contenido) {
        new File(directorioCurso.getPath() + File.separator + "html").mkdirs();
        File html = new File(directorioCurso.getPath() + File.separator + "html" + File.separator + tituloCurso + ".html");

        try (PrintWriter out = new PrintWriter(html)) {
            out.println(contenido);
        } catch (FileNotFoundException ex) {
            log(ex);
        }
    }

    private String getTitle(String content) {
        String title = content.substring(content.indexOf("<title>") + 7, content.indexOf("</title>")); //Curso: 
        return title.substring(title.indexOf(": ") + 2, title.length());
    }

    private String getTitleSection(String section) {
        String[] partir = section.split("<h3 class=\"sectionname\">");
        String a = partir[1].split("</h3>")[0];
        a = a.replaceAll(":", ".");
        a = a.replace("<span>", "");
        a = a.replace("</span>", "");
        if (a.charAt(a.length() - 1) == '.') {
            a = a.substring(0, a.length() - 1);
        }
        return a;
    }

    private void saveRecurso(String a, String b) {
        if (!resources.containsKey(a)) {
            resources.put(a, b);
            gui.getExploredFiles().setText(Integer.toString(resources.size()));
        }
    }

    public static void log(Exception ex) {
        Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        GUI.getTerm().append("\n" + "ERROR: Unknown error. Debug info: " + sw.toString());

    }

    public void descargado() {
        descargados++;
        if (archivos != -1) {
            gui.getBarra().setValue(50 + (50 * descargados / archivos));
            gui.getDownloadedFiles().setText(descargados + "/" + archivos);
            if (archivos == descargados) {
                GUI.getTerm().append("\n" + "Finished!!!");
                gui.setDonwloadButton(true);
                gui.setCancelButton(false);
            }
        }
    }

}
