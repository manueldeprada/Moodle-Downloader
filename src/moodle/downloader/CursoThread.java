/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moodle.downloader;

/**
 *
 * @author mprad
 */
public class CursoThread implements Runnable {
    
    private final int curso;
    private final String contenido;
    private final Downloader padre;

    public CursoThread(int curso, String contenido, Downloader padre) {
        this.padre = padre;
        this.curso = curso;
        this.contenido = contenido;
    }

    @Override
    public void run() {
        padre.descargarCurso(contenido, curso);
    }
    
}
