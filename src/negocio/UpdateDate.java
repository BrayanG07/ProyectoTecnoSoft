package negocio;

import java.util.Calendar;
import javax.swing.JLabel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author alico
 */
public class UpdateDate implements Runnable {

    //Creamos una clase que implementa interfaz runnable
    JLabel someLabel;

    //Debemos guardar el label que queremos modificar, o en el que va el tiempo
    public UpdateDate(JLabel dateLabel) {
        //Hacemos un constructor de la clase, que reciba el label que queremos actualizar
        someLabel = dateLabel;
        //Colocamos el label en la variable someLabel, para mas tarde referirlo, y modificarlo
    }

    @Override
    public void run() {

        while (true) {
            try {
                Thread.sleep(999);

                Calendar calendario = Calendar.getInstance();
                int hora, minutos, segundos, anio, mes, dia;
                hora = calendario.get(Calendar.HOUR_OF_DAY);
                minutos = calendario.get(Calendar.MINUTE);
                segundos = calendario.get(Calendar.SECOND);
                anio = calendario.get(Calendar.YEAR);
                mes = calendario.get(Calendar.MONTH) + 1;
                dia = calendario.get(Calendar.DATE);

                someLabel.setText("Fecha: " + dia + "/" + mes + "/" + anio + "    Hora: " + hora + ":" + minutos + ":" + segundos);

            } catch (Exception ex) {
            }
        }
    }
}
