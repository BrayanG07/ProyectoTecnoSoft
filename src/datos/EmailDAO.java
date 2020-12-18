/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datos;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import database.Conexion;;
import entidades.Email;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Buddys
 */
public class EmailDAO {

    private final Conexion CON;
    private PreparedStatement ps;
    private ResultSet rs;
    private boolean resp;

    public EmailDAO() {
        CON = Conexion.getInstancia();
    }

    public List<Email> listar(String correo, String descripcion) {
        List<Email> registros = new ArrayList();
        try {
            ps = CON.conectar().prepareStatement("SELECT * FROM email  WHERE correo LIKE ? AND descripcion LIKE ?");
            ps.setString(1, "%" + correo + "%");
            ps.setString(2, "%" + descripcion + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                registros.add(new Email(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4)));
            }
            ps.close();
            rs.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } finally {
            ps = null;
            rs = null;
            CON.desconectar();
        }
        return registros;
    }

    public boolean insertar(Email obj) {
        resp = false;
        try {
            ps = CON.conectar().prepareStatement("INSERT INTO email (correo,asunto,descripcion,fecha) VALUES (?,?,?,now())");
            ps.setString(1, obj.getCorreo());
            ps.setString(2, obj.getAsunto());
            ps.setString(3, obj.getDescripcion());
            if (ps.executeUpdate() > 0) {
                resp = true;
            }
            ps.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } finally {
            ps = null;
            CON.desconectar();
        }
        return resp;
    }
    
    public int total() {
        int totalRegistros = 0;
        try {
            ps = CON.conectar().prepareStatement("SELECT COUNT(id) FROM categoria");
            rs = ps.executeQuery();

            while (rs.next()) {
                totalRegistros = rs.getInt("COUNT(id)");
            }
            ps.close();
            rs.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } finally {
            ps = null;
            rs = null;
            CON.desconectar();
        }
        return totalRegistros;
    }

}
