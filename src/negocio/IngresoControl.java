/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package negocio;

import database.Conexion;
import datos.ArticuloDAO;
import datos.IngresoDAO;
import entidades.Articulo;
import entidades.DetalleIngreso;
import entidades.Ingreso;
import entidades.Venta;
import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author Buddy
 */
public class IngresoControl {

    private final IngresoDAO DATOS;
    private final ArticuloDAO DATOSART;
    private Ingreso obj;
    private DefaultTableModel modeloTabla;
    public int registrosMostrados;

    public IngresoControl() {
        this.DATOS = new IngresoDAO();
        this.DATOSART = new ArticuloDAO();
        this.obj = new Ingreso();
        this.registrosMostrados = 0;
    }

    public DefaultTableModel listar(String texto, int totalPorPagina, int numPagina) {
        List<Ingreso> lista = new ArrayList();
        lista.addAll(DATOS.listar(texto, totalPorPagina, numPagina));

        String[] titulos = {"Id", "Usuario ID", "Usuario", "Proveedor ID ", "Proveedor", "Tipo comprobante", "Serie ", "Numero", "Fecha", "Impuesto", "Total", "Estado"};
        this.modeloTabla = new DefaultTableModel(null, titulos);

        String estado;
        String[] registro = new String[12];
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        this.registrosMostrados = 0;
        for (Ingreso item : lista) {
            registro[0] = Integer.toString(item.getId());
            registro[1] = Integer.toString(item.getUsuarioId());
            registro[2] = item.getUsuarioNombre();
            registro[3] = Integer.toString(item.getPersonaId());
            registro[4] = item.getPersonaNombre();
            registro[5] = item.getTipoComprobante();
            registro[6] = item.getSerieComprobante();
            registro[7] = item.getNumComprobante();
            registro[8] = sdf.format(item.getFecha());
            registro[9] = Double.toString(item.getImpuesto());
            registro[10] = Double.toString(item.getTotal());
            registro[11] = item.getEstado();
            this.modeloTabla.addRow(registro);
            this.registrosMostrados = this.registrosMostrados + 1;
        }
        return this.modeloTabla;
    }

    public DefaultTableModel listarDetalle(int id) {
        List<DetalleIngreso> lista = new ArrayList();
        lista.addAll(DATOS.listarDetalle(id));

        String[] titulos = {"ID", "CODIGO", "ARTICULO", "CANTIDAD", "PRECIO", "SUBTOTAL"};
        this.modeloTabla = new DefaultTableModel(null, titulos);

        String[] registro = new String[6];

        for (DetalleIngreso item : lista) {
            registro[0] = Integer.toString(item.getArticuloId());
            registro[1] = item.getArticuloCodigo();
            registro[2] = item.getArticuloNombre();
            registro[3] = Integer.toString(item.getCantidad());
            registro[4] = Double.toString(item.getPrecio());
            registro[5] = Double.toString(item.getSubTotal());
            this.modeloTabla.addRow(registro);
        }
        return this.modeloTabla;
    }

    public Articulo obtenerArticuloCodigoIngreso(String codigo) {
        Articulo art = DATOSART.obtenerArticuloCodigoIngreso(codigo);
        return art;
    }

    public String insertar(int personaId, String tipoComprobante, String serieComprobante, String numComprobante, double impuesto, double total, DefaultTableModel modeloDetalle) {// EL DEFAULT.. TENDRA TODOS LOS DETALLES DE CADA REGISTRO
        if (DATOS.existe(serieComprobante, numComprobante)) {
            return "El registro ya existe.";
        } else {
            obj.setUsuarioId(Variables.usuarioID);
            obj.setPersonaId(personaId);
            obj.setTipoComprobante(tipoComprobante);
            obj.setSerieComprobante(serieComprobante);
            obj.setNumComprobante(numComprobante);
            obj.setImpuesto(impuesto);
            obj.setTotal(total);

            List<DetalleIngreso> detalles = new ArrayList();//EL obj.setDetalles espera un objeto de ArrayList POR ESO CREO UNA INSTANCIA DEL LIST
            int articuloId, cantidad;//ESTOS SON ALGUNOS CAMPOS DE
            double precio;//DE LA BDD EN LA TABLA  DETALLE_INGRESO 
            for (int i = 0; i < modeloDetalle.getRowCount(); i++) {
                articuloId = Integer.parseInt(String.valueOf(modeloDetalle.getValueAt(i, 0)));
                cantidad = Integer.parseInt(String.valueOf(modeloDetalle.getValueAt(i, 3)));//3 PORQUE EN LA CAPA PRESENTACION EN LA COLUMNA 3 DE LA TABLA ESTARA UBICADA LA CANTIDAD
                precio = Double.parseDouble(String.valueOf(modeloDetalle.getValueAt(i, 4)));
                detalles.add(new DetalleIngreso(articuloId, cantidad, precio));
            }
            obj.setDetalles(detalles);
            if (DATOS.insertar(obj)) {
                return "OK";
            } else {
                return "Error en el registro.";
            }
        }
    }

    public String anular(int id) {
        if (DATOS.anular(id)) {
            return "OK";
        } else {
            return "No se puede anular el registro";
        }
    }

    public int total() {
        return DATOS.total();
    }

    public int totalMostrados() {
        return this.registrosMostrados;
    }

    public DefaultTableModel consultaFechas(Date fechaInicio, Date fechaFin) {
        List<Venta> lista = new ArrayList();
        lista.addAll(DATOS.consultaFechas(fechaInicio, fechaFin));

        String[] titulos = {"Id", "Usuario ID", "Usuario", "Cliente ID", "Cliente", "Tipo Comprobante", "Serie", "Número", "Fecha", "Impuesto", "Total", "Estado"};
        this.modeloTabla = new DefaultTableModel(null, titulos);

        String[] registro = new String[12];
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        this.registrosMostrados = 0;
        for (Venta item : lista) {
            registro[0] = Integer.toString(item.getId());
            registro[1] = Integer.toString(item.getUsuarioId());
            registro[2] = item.getUsuarioNombre();
            registro[3] = Integer.toString(item.getPersonaId());
            registro[4] = item.getPersonaNombre();
            registro[5] = item.getTipoComprobante();
            registro[6] = item.getSerieComprobante();
            registro[7] = item.getNumComprobante();
            registro[8] = sdf.format(item.getFecha());
            registro[9] = Double.toString(item.getImpuesto());
            registro[10] = Double.toString(item.getTotal());
            registro[11] = item.getEstado();

            this.modeloTabla.addRow(registro);
            this.registrosMostrados = this.registrosMostrados + 1;
        }
        return this.modeloTabla;
    }

    public String ultimoSerie(String tipoComprobante) {
        return this.DATOS.ultimoSerie(tipoComprobante);
    }

    public String ultimoNumero(String tipoComprobante, String serieComprobante) {
        return this.DATOS.ultimoNumero(tipoComprobante, serieComprobante);
    }
    
    public void reporteIngreso(String idIngreso) {
        Map mapa = new HashMap();
        mapa.put("idingreso", idIngreso);
        JasperReport reporte;
        JasperPrint print;

        Conexion cnn = Conexion.getInstancia();
        try {
            reporte = JasperCompileManager.compileReport(new File("").getAbsolutePath()
                    + "/src/reportes/ReporteComprobanteIngreso.jrxml");
            print = JasperFillManager.fillReport(reporte, mapa, cnn.conectar());
            JasperViewer view = new JasperViewer(print, false);
            view.setVisible(true);
        } catch (JRException e) {
            e.getMessage();
        }
    }
    
}
