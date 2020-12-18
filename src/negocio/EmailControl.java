/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package negocio;

import datos.EmailDAO;
import entidades.Email;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Buddys
 */
public class EmailControl {

    private final EmailDAO DATOS;
    private Email obj;
    private DefaultTableModel modeloTabla;
    public int registrosMostrados;

    public EmailControl() {
        this.DATOS = new EmailDAO();
        this.obj = new Email();
        this.registrosMostrados = 0;
    }

    public DefaultTableModel listar(String correo, String descripcion) {
        List<Email> lista = new ArrayList();
        lista.addAll(DATOS.listar(correo, descripcion));

        String[] titulos = {"Id", "Correo", "Asunto", "Descripción"};//TITULOS DEL TABLE
        this.modeloTabla = new DefaultTableModel(null, titulos);

        String[] registro = new String[4];

        this.registrosMostrados = 0;
        for (Email item : lista) {
            registro[0] = Integer.toString(item.getId());
            registro[1] = item.getCorreo();
            registro[2] = item.getAsunto();
            registro[3] = item.getDescripcion();
            this.modeloTabla.addRow(registro);
            this.registrosMostrados = this.registrosMostrados + 1;
        }
        return this.modeloTabla;
    }

    public String insertar(String correo, String asunto, String descripcion) {
        obj.setCorreo(correo);
        obj.setAsunto(asunto);
        obj.setDescripcion(descripcion);
        if (DATOS.insertar(obj)) {
            return "OK";
        } else {
            return "Error en el registro.";
        }

    }

    public int total() {
        return DATOS.total();
    }

    public int totalMostrados() {
        return this.registrosMostrados;
    }
}
