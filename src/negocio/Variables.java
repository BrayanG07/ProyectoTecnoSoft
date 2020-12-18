/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package negocio;

import entidades.ColorTabla;
import javax.swing.JTable;

/**
 *
 * @author Buddys
 */
public class Variables {
    public static int usuarioID;
    public static int rolID;
    public static String rolNombre;
    public static String usuarioNombre;
    public static String usuarioEmail;
    
    public void cargarColor(JTable tabla){
        ColorTabla color = new ColorTabla();
        for (int i = 0; i < tabla.getColumnCount(); i++) {
                tabla.getColumnModel().getColumn(i).setCellRenderer(color);
            }
    }
}
