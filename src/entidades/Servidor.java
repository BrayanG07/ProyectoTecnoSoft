/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entidades;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;
import javax.swing.JOptionPane;

/**
 *
 * @author Buddys
 */
public class Servidor extends Observable implements Runnable {

    private int puerto;

    public Servidor(int puerto) {
        this.puerto = puerto;
    }

    public Servidor() {
    }

    @Override
    public void run() {
        ServerSocket servidor = null;//SOCEKT DEL SERVIDOR
        Socket sc = null;//SOCKET DEL CLIENTE
        DataInputStream in;
        DataOutputStream out;

        try {
            servidor = new ServerSocket(puerto);

            while (true) {//BUCLE INFINITO PARA ASI ESTAR SIEMPRE A LA ESPERA
                
                sc = servidor.accept();//QUEDA A LA ESPERA OSEA NO AVANZA A LA SIGUIENTE LINEA DE CODIGO
                //Y SE EJECUTARA HASTA QUE LLEGUE ALGUIEN OSEA UN MENSAJE
                
                //LO QUE DEVUELVE 'sc' ES EL SOCKET DEL CLIENTE,
                
                in = new DataInputStream(sc.getInputStream());

                String mensaje = in.readUTF();//QUEDA A LA ESPERA DE QUE EL CLIENTE LE MANDE ALGO

                this.setChanged();//AVISANDO QUE CAMBIARA ALGO
                this.notifyObservers(mensaje);
                this.clearChanged();//LIMPIA EL AVISO UNA VEZ NOTIFICADO

                sc.close();//CERRANDO EL CLIENTE
            }
            
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error: "+ex.getMessage(), "TecnoSoft",JOptionPane.WARNING_MESSAGE);
            ex.printStackTrace();
        }
    }

}
