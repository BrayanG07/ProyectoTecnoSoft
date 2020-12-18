/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entidades;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import javax.swing.JOptionPane;

/**
 * 
 * @author Buddys
 */
public class Cliente implements Runnable {
    private String host;
    private int puerto;
    private String mensaje;

    public Cliente(String host, int puerto, String mensaje) {
        this.host = host;
        this.puerto = puerto;
        this.mensaje = mensaje;
    }

    @Override
    public void run() {
        DataOutputStream out;

        try {
            Socket sc = new Socket(host, puerto);//LA CONEXION SE CREA APARTIR DEL SOCKET Y 
            //ESA CONEXION VIAJA CON EL OUTPUT STREAM Y ASI SE MANTIENE EL PUENTE CON CONEXION
            out = new DataOutputStream(sc.getOutputStream());

            out.writeUTF(mensaje);//MANDANDO UN MENSAJE
            //AQUI SOLO LANZO EL MSJ Y EL SERVIDOR SE ENCARCARGARA 
            //Y LO QUE HARA PUES ES NOTIFICAR QUE HAY CAMBIOS
            
            sc.close();

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Mensaje no enviado, empleado no activo");
            ex.getMessage();
        }
    }
}
