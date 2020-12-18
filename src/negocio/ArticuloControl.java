/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package negocio;

import database.Conexion;
import datos.ArticuloDAO;
import datos.CategoriaDAO;
import entidades.Articulo;
import entidades.Categoria;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author Buddys
 */
public class ArticuloControl {

    private final ArticuloDAO DATOS;
    private final CategoriaDAO DATOSCAT;
    private Articulo obj;
    private DefaultTableModel modeloTabla;
    public int registrosMostrados;

    public ArticuloControl() {
        this.DATOS = new ArticuloDAO();
        this.DATOSCAT = new CategoriaDAO();
        this.obj = new Articulo();
        this.registrosMostrados = 0;
    }

    public DefaultTableModel listar(String texto, int totalPorPagina, int numPagina) {
        List<Articulo> lista = new ArrayList();
        lista.addAll(DATOS.listar(texto, totalPorPagina, numPagina));

        String[] titulos = {"Id", "Categoria ID", "Categoria", "Codigo", "Nombre", "Precio Venta", "Stock", "Descripción", "Imagen", "Estado"};
        this.modeloTabla = new DefaultTableModel(null, titulos);

        String estado;
        String[] registro = new String[10];

        this.registrosMostrados = 0;
        for (Articulo item : lista) {
            if (item.isActivo()) {
                estado = "Activo";
            } else {
                estado = "Inactivo";
            }
            registro[0] = Integer.toString(item.getId());
            registro[1] = Integer.toString(item.getCategoriaId());
            registro[2] = item.getCategoriaNombre();
            registro[3] = item.getCodigo();
            registro[4] = item.getNombre();
            registro[5] = Double.toString(item.getPrecioVenta());
            registro[6] = Integer.toString(item.getStock());
            registro[7] = item.getDescripcion();
            registro[8] = item.getImagen();
            registro[9] = estado;
            this.modeloTabla.addRow(registro);
            this.registrosMostrados = this.registrosMostrados + 1;
        }
        return this.modeloTabla;
    }

    public DefaultTableModel listarArticuloVenta(String texto, int totalPorPagina, int numPagina) {
        List<Articulo> lista = new ArrayList();
        lista.addAll(DATOS.listarArticuloVenta(texto, totalPorPagina, numPagina));

        String[] titulos = {"Id", "Categoria ID", "Categoria", "Codigo", "Nombre", "Precio Venta", "Stock", "Descripción", "Imagen", "Estado"};
        this.modeloTabla = new DefaultTableModel(null, titulos);

        String estado;
        String[] registro = new String[10];

        this.registrosMostrados = 0;
        for (Articulo item : lista) {
            if (item.isActivo()) {
                estado = "Activo";
            } else {
                estado = "Inactivo";
            }
            registro[0] = Integer.toString(item.getId());
            registro[1] = Integer.toString(item.getCategoriaId());
            registro[2] = item.getCategoriaNombre();
            registro[3] = item.getCodigo();
            registro[4] = item.getNombre();
            registro[5] = Double.toString(item.getPrecioVenta());
            registro[6] = Integer.toString(item.getStock());
            registro[7] = item.getDescripcion();
            registro[8] = item.getImagen();
            registro[9] = estado;
            this.modeloTabla.addRow(registro);
            this.registrosMostrados = this.registrosMostrados + 1;
        }
        return this.modeloTabla;
    }

    public DefaultComboBoxModel seleccionar() {
        DefaultComboBoxModel items = new DefaultComboBoxModel();
        List<Categoria> lista = new ArrayList();
        lista = DATOSCAT.seleccionar();
        for (Categoria categoria : lista) {
            items.addElement(new Categoria(categoria.getId(), categoria.getNombre()));
        }
        return items;

    }

    public String insertar(int categoriaId, String codigo, String nombre, double precioVenta, int stock, String descripcion, String imagen) {
        if (DATOS.existe(nombre)) {
            return "El registro ya existe.";
        } else {
            obj.setCategoriaId(categoriaId);
            obj.setCodigo(codigo);
            obj.setNombre(nombre);
            obj.setPrecioVenta(precioVenta);
            obj.setStock(stock);
            obj.setDescripcion(descripcion);
            obj.setImagen(imagen);
            if (DATOS.insertar(obj)) {
                return "OK";
            } else {
                return "Error en el registro.";
            }
        }
    }

    public String actualizar(int id, int categoriaId, String codigo, String nombre, String nombreAnt, double precioVenta, int stock, String descripcion, String imagen) {
        if (nombre.equals(nombreAnt)) {
            obj.setId(id);
            obj.setCategoriaId(categoriaId);
            obj.setCodigo(codigo);
            obj.setNombre(nombre);
            obj.setPrecioVenta(precioVenta);
            obj.setStock(stock);
            obj.setDescripcion(descripcion);
            obj.setImagen(imagen);
            if (DATOS.actualizar(obj)) {
                return "OK";
            } else {
                return "Error en la actualización.";
            }
        } else {
            if (DATOS.existe(nombre)) {
                return "El registro ya existe.";
            } else {
                obj.setId(id);
                obj.setCategoriaId(categoriaId);
                obj.setCodigo(codigo);
                obj.setNombre(nombre);
                obj.setPrecioVenta(precioVenta);
                obj.setStock(stock);
                obj.setDescripcion(descripcion);
                obj.setImagen(imagen);
                if (DATOS.actualizar(obj)) {
                    return "OK";
                } else {
                    return "Error en la actualización.";
                }
            }
        }
    }

    public String desactivar(int id) {
        if (DATOS.desactivar(id)) {
            return "OK";
        } else {
            return "No se puede desactivar el registro";
        }
    }

    public String activar(int id) {
        if (DATOS.activar(id)) {
            return "OK";
        } else {
            return "No se puede activar el registro";
        }
    }

    public int total() {
        return DATOS.total();
    }

    public int totalMostrados() {
        return this.registrosMostrados;
    }

    public void reporteArticulo() {
        Map mapa = new HashMap();
        JasperReport reporte;
        JasperPrint print;

        Conexion cnn = Conexion.getInstancia();
        try {
            reporte = JasperCompileManager.compileReport(new File("").getAbsolutePath()
                    + "/src/reportes/ReportesArticulos.jrxml");
            print = JasperFillManager.fillReport(reporte, mapa, cnn.conectar());
            JasperViewer view = new JasperViewer(print, false);//PERMITE LA VIZUALISACION DEL REPORTE
            view.setVisible(true);
        } catch (JRException e) {
            e.getMessage();
        }
    }

    public void reporteArticulosBarras() {
        Map mapa = new HashMap();
        JasperReport reporte;
        JasperPrint print;

        Conexion cnn = Conexion.getInstancia();
        try {
            reporte = JasperCompileManager.compileReport(new File("").getAbsolutePath()
                    + "/src/reportes/ReporteArticulosCodigoBarra.jrxml");
            print = JasperFillManager.fillReport(reporte, mapa, cnn.conectar());
            JasperViewer view = new JasperViewer(print, false);
            view.setVisible(true);
        } catch (JRException e) {
            e.getMessage();
        }
    }

    public void resportesPDF(String ruta, String archi) {
        try {
            Conexion cnn = Conexion.getInstancia();
            String rutaInforme = ruta;
            JasperPrint informe = JasperFillManager.fillReport(rutaInforme, null, cnn.conectar());
            JasperExportManager.exportReportToPdfFile(informe, archi);

            JasperViewer ventanavisor = new JasperViewer(informe, false);
            ventanavisor.setTitle("INFORME");
            ventanavisor.setVisible(false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERROR AL EXPORTAR DOCUMENTO");
        }
    }
}
