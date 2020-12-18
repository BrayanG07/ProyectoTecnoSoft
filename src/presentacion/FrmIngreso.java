/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package presentacion;

import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import negocio.IngresoControl;
import negocio.Variables;

/**
 *
 * @author Buddys
 */
public class FrmIngreso extends javax.swing.JInternalFrame {
    
    private final Variables COLORTABLA;
    private final IngresoControl CONTROL;
    private String accion;
    private String nombreAnt;

    private int totalPorPagina = 10;
    private int numPagina = 1;
    private boolean primeraCarga = true;
    private int totalRegistros;

    public DefaultTableModel modeloDetalles;
    public JFrame contenedor;

    /**
     * Creates new form FrmVenta
     */
    public FrmIngreso(JFrame frmP) {
        initComponents();
        this.contenedor = frmP;
        this.CONTROL = new IngresoControl();
        this.COLORTABLA = new Variables();
        this.paginar();
        this.listar("", false);
        this.primeraCarga = false;
        tabGeneral.setEnabledAt(1, false);
        this.accion = "guardar";
        this.crearDetalles();
    }

    private void paginar() {
        int totalPaginas;

        this.totalRegistros = this.CONTROL.total();
        this.totalPorPagina = Integer.parseInt((String) cboTotalPorPagina.getSelectedItem());
        totalPaginas = (int) (Math.ceil((double) this.totalRegistros / this.totalPorPagina));
        if (totalPaginas == 0) {
            totalPaginas = 1;
        }
        cboNumPagina.removeAllItems();

        for (int i = 1; i <= totalPaginas; i++) {
            cboNumPagina.addItem(Integer.toString(i));
        }
        cboNumPagina.setSelectedIndex(0);
    }

    private void listar(String texto, boolean paginar) {
        this.totalPorPagina = Integer.parseInt((String) cboTotalPorPagina.getSelectedItem());
        if ((String) cboNumPagina.getSelectedItem() != null) {
            this.numPagina = Integer.parseInt((String) cboNumPagina.getSelectedItem());
        }

        if (paginar == true) {
            tablaListado.setModel(this.CONTROL.listar(texto, this.totalPorPagina, this.numPagina));
        } else {
            tablaListado.setModel(this.CONTROL.listar(texto, this.totalPorPagina, 1));
        }
        this.COLORTABLA.cargarColor(tablaListado);
        TableRowSorter orden = new TableRowSorter(tablaListado.getModel());
        tablaListado.setRowSorter(orden);
        this.ocultarColumnas();
        lblTotalRegistros.setText("Mostrando " + this.CONTROL.totalMostrados() + " de un total de " + this.CONTROL.total() + " registros");
    }

    private void ocultarColumnas() {
        tablaListado.getColumnModel().getColumn(1).setMaxWidth(0);
        tablaListado.getColumnModel().getColumn(1).setMinWidth(0);
        tablaListado.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(0);
        tablaListado.getTableHeader().getColumnModel().getColumn(1).setMinWidth(0);

        tablaListado.getColumnModel().getColumn(3).setMaxWidth(0);
        tablaListado.getColumnModel().getColumn(3).setMinWidth(0);
        tablaListado.getTableHeader().getColumnModel().getColumn(3).setMaxWidth(0);
        tablaListado.getTableHeader().getColumnModel().getColumn(3).setMinWidth(0);
    }

    private void crearDetalles() {
        modeloDetalles = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                if (columna == 3) {
                    return columna == 3;
                }
                if (columna == 4) {
                    return columna == 4;
                }
                return columna == 3;
            }

            @Override
            public Object getValueAt(int row, int col) {
                if (col == 5) {
                    Double cantD;
                    try {
                        cantD = Double.parseDouble((String) getValueAt(row, 3));
                    } catch (Exception e) {
                        cantD = 1.0;
                    }
                    Double precioD = Double.parseDouble((String) getValueAt(row, 4));
                    if (cantD != null && precioD != null) {
                        return String.format("%.2f", cantD * precioD);
                    } else {
                        return 0;
                    }
                }
                return super.getValueAt(row, col);
            }

            @Override
            public void setValueAt(Object aValue, int row, int col) {
                super.setValueAt(aValue, row, col);
                calcularTotales();
                fireTableDataChanged();
            }

        };

        modeloDetalles.setColumnIdentifiers(new Object[]{"ID", "CODIGO", "ARTÍCULO", "CANTIDAD", "PRECIO", "SUBTOTAL"});
        tablaDetalles.setModel(modeloDetalles);
        this.COLORTABLA.cargarColor(tablaDetalles);
    }

    public void agregarDetalles(String id, String codigo, String nombre, String precio) {
        String idT;
        boolean existe = false;
        for (int i = 0; i < this.modeloDetalles.getRowCount(); i++) {
            idT = String.valueOf(this.modeloDetalles.getValueAt(i, 0));
            if (idT.equals(id)) {
                existe = true;
            }
        }
        if (existe) {
            this.mensajeError("El artículo ya ha sido agregado.");
        } else {
            this.modeloDetalles.addRow(new Object[]{id, codigo, nombre, "1", precio, precio});
            this.calcularTotales();
        }
    }

    private void calcularTotales() {
        double total = 0;
        double subTotal;
        int items = modeloDetalles.getRowCount();
        if (items == 0) {
            total = 0;
        } else {
            for (int i = 0; i < items; i++) {
                total = total + Double.parseDouble(String.valueOf(modeloDetalles.getValueAt(i, 5)));
            }
        }
        subTotal = total / (1 + Double.parseDouble(txtImpuesto.getText()));

        txtTotal.setText(String.format("%.2f", total));
        txtSubTotal.setText(String.format("%.2f", subTotal));
        txtTotalImpuesto.setText(String.format("%.2f", total - subTotal));
    }

    private void limpiar() {
        txtNombreProveedor.setText("");
        txtIdProveedor.setText("");
        txtSerieComprobante.setText("");
        txtNumComprobante.setText("");
        txtImpuesto.setText("0.18");

        this.accion = "guardar";

        txtTotal.setText("0.00");
        txtSubTotal.setText("0.00");
        txtTotalImpuesto.setText("0.00");
        this.crearDetalles();
        btnGuardar.setVisible(true);
    }

    private void mensajeError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "TecnoSoft", JOptionPane.ERROR_MESSAGE);
    }

    private void mensajeOk(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "TecnoSoft", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void obtenerNumero() {
        String tipoComprobante = (String) cboTipoComprobante.getSelectedItem();
        String serieComprobante = this.CONTROL.ultimoSerie(tipoComprobante);
        String numComprobante = this.CONTROL.ultimoNumero(tipoComprobante, serieComprobante);
        txtSerieComprobante.setText(serieComprobante);
        if (numComprobante.equals("")) {
            txtNumComprobante.setText("");
        } else {
            int num;
            num = Integer.parseInt(numComprobante) + 1;
            txtNumComprobante.setText(Integer.toString(num));
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabGeneral = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        btnBuscar = new javax.swing.JButton();
        btnNuevo = new javax.swing.JButton();
        btnDesactivar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaListado = new javax.swing.JTable();
        cboNumPagina = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        cboTotalPorPagina = new javax.swing.JComboBox<>();
        lblTotalRegistros = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        btnVerIngreso = new javax.swing.JButton();
        btnReporte = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtIdProveedor = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtImpuesto = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        cboTipoComprobante = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        txtSerieComprobante = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtNumComprobante = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtCodigo = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtSubTotal = new javax.swing.JTextField();
        txtTotalImpuesto = new javax.swing.JTextField();
        txtTotal = new javax.swing.JTextField();
        btnQuitar = new javax.swing.JButton();
        txtNombreProveedor = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        tablaDetalles = new javax.swing.JTable();
        jPanel10 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        btnGuardar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        jPanel12 = new javax.swing.JPanel();
        btnSeleccionarProveedor = new javax.swing.JButton();
        btnVerArticulos = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);

        tabGeneral.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel9.setBackground(new java.awt.Color(0, 153, 204));

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("Ingresos");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 153, 204));
        jLabel1.setText("Nombre:");

        txtBuscar.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        txtBuscar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204), 2));
        txtBuscar.setMinimumSize(new java.awt.Dimension(6, 28));
        txtBuscar.setPreferredSize(new java.awt.Dimension(6, 25));

        btnBuscar.setBackground(new java.awt.Color(102, 102, 102));
        btnBuscar.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        btnBuscar.setForeground(new java.awt.Color(255, 255, 255));
        btnBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/presentacion/imagenes/Buscar (2).png"))); // NOI18N
        btnBuscar.setText("Buscar");
        btnBuscar.setPreferredSize(new java.awt.Dimension(60, 37));
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        btnNuevo.setBackground(new java.awt.Color(102, 102, 102));
        btnNuevo.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        btnNuevo.setForeground(new java.awt.Color(255, 255, 255));
        btnNuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/presentacion/imagenes/nuevo.png"))); // NOI18N
        btnNuevo.setText("Nuevo");
        btnNuevo.setPreferredSize(new java.awt.Dimension(60, 37));
        btnNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoActionPerformed(evt);
            }
        });

        btnDesactivar.setBackground(new java.awt.Color(102, 102, 102));
        btnDesactivar.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        btnDesactivar.setForeground(new java.awt.Color(255, 255, 255));
        btnDesactivar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/presentacion/imagenes/cancelar.png"))); // NOI18N
        btnDesactivar.setText("Anular");
        btnDesactivar.setPreferredSize(new java.awt.Dimension(60, 37));
        btnDesactivar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDesactivarActionPerformed(evt);
            }
        });

        tablaListado.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tablaListado.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tablaListado.setRowHeight(22);
        jScrollPane1.setViewportView(tablaListado);

        cboNumPagina.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        cboNumPagina.setForeground(new java.awt.Color(0, 153, 204));
        cboNumPagina.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204), 2));
        cboNumPagina.setPreferredSize(new java.awt.Dimension(28, 25));
        cboNumPagina.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboNumPaginaActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 15)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 153, 204));
        jLabel10.setText("Nº Página");

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 15)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 153, 204));
        jLabel11.setText("Total de registros por página");

        cboTotalPorPagina.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        cboTotalPorPagina.setForeground(new java.awt.Color(0, 153, 204));
        cboTotalPorPagina.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "10", "20", "50", "100", "200", "500" }));
        cboTotalPorPagina.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204), 2));
        cboTotalPorPagina.setPreferredSize(new java.awt.Dimension(28, 25));
        cboTotalPorPagina.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTotalPorPaginaActionPerformed(evt);
            }
        });

        lblTotalRegistros.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblTotalRegistros.setForeground(new java.awt.Color(0, 153, 204));
        lblTotalRegistros.setText("Registros");

        jPanel13.setBackground(new java.awt.Color(0, 153, 204));

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 42, Short.MAX_VALUE)
        );

        btnVerIngreso.setBackground(new java.awt.Color(102, 102, 102));
        btnVerIngreso.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        btnVerIngreso.setForeground(new java.awt.Color(255, 255, 255));
        btnVerIngreso.setIcon(new javax.swing.ImageIcon(getClass().getResource("/presentacion/imagenes/Ver Ingreso.png"))); // NOI18N
        btnVerIngreso.setText("Ver Ingreso");
        btnVerIngreso.setPreferredSize(new java.awt.Dimension(60, 37));
        btnVerIngreso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerIngresoActionPerformed(evt);
            }
        });

        btnReporte.setBackground(new java.awt.Color(102, 102, 102));
        btnReporte.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        btnReporte.setForeground(new java.awt.Color(255, 255, 255));
        btnReporte.setIcon(new javax.swing.ImageIcon(getClass().getResource("/presentacion/imagenes/reporte-de-negocios.png"))); // NOI18N
        btnReporte.setText("Reporte");
        btnReporte.setPreferredSize(new java.awt.Dimension(60, 40));
        btnReporte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReporteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 503, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnNuevo, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnDesactivar, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnVerIngreso, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnReporte, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel10)
                .addGap(18, 18, 18)
                .addComponent(cboNumPagina, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39)
                .addComponent(jLabel11)
                .addGap(26, 26, 26)
                .addComponent(cboTotalPorPagina, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblTotalRegistros, javax.swing.GroupLayout.PREFERRED_SIZE, 364, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
            .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNuevo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDesactivar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnVerIngreso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnReporte, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 374, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboNumPagina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboTotalPorPagina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(lblTotalRegistros, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 174, Short.MAX_VALUE)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        tabGeneral.addTab("Listado", jPanel1);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 153, 204));
        jLabel2.setText("Proveedor (*)");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 153, 204));
        jLabel4.setText("(*) Indica que es un campo obligatorio.");

        txtIdProveedor.setEditable(false);
        txtIdProveedor.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        txtIdProveedor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204), 2));
        txtIdProveedor.setPreferredSize(new java.awt.Dimension(4, 25));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 153, 204));
        jLabel3.setText("Impuesto (*)");

        txtImpuesto.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        txtImpuesto.setText("0.18");
        txtImpuesto.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204), 2));
        txtImpuesto.setPreferredSize(new java.awt.Dimension(4, 25));

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 153, 204));
        jLabel5.setText("Tipo Comprobante (*)");

        cboTipoComprobante.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        cboTipoComprobante.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "FACTURA", "BOLETA", "TICKET", "GUIA" }));
        cboTipoComprobante.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204), 2));
        cboTipoComprobante.setPreferredSize(new java.awt.Dimension(87, 25));
        cboTipoComprobante.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTipoComprobanteActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 153, 204));
        jLabel6.setText("Serie Comprobante");

        txtSerieComprobante.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        txtSerieComprobante.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204), 2));
        txtSerieComprobante.setPreferredSize(new java.awt.Dimension(4, 25));
        txtSerieComprobante.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSerieComprobanteActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 153, 204));
        jLabel7.setText("Número Comprobante (*)");

        txtNumComprobante.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        txtNumComprobante.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204), 2));
        txtNumComprobante.setPreferredSize(new java.awt.Dimension(4, 25));

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 153, 204));
        jLabel8.setText("Artículo");

        txtCodigo.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        txtCodigo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204), 2));
        txtCodigo.setPreferredSize(new java.awt.Dimension(4, 25));
        txtCodigo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCodigoKeyReleased(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 153, 204));
        jLabel9.setText("Subtotal");

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(0, 153, 204));
        jLabel12.setText("Total Impuesto");

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(0, 153, 204));
        jLabel13.setText("Total");

        txtSubTotal.setEditable(false);
        txtSubTotal.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        txtSubTotal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204), 2));
        txtSubTotal.setPreferredSize(new java.awt.Dimension(4, 25));
        txtSubTotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSubTotalActionPerformed(evt);
            }
        });

        txtTotalImpuesto.setEditable(false);
        txtTotalImpuesto.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        txtTotalImpuesto.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204), 2));
        txtTotalImpuesto.setPreferredSize(new java.awt.Dimension(4, 25));

        txtTotal.setEditable(false);
        txtTotal.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        txtTotal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204), 2));
        txtTotal.setPreferredSize(new java.awt.Dimension(4, 25));

        btnQuitar.setBackground(new java.awt.Color(102, 102, 102));
        btnQuitar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnQuitar.setForeground(new java.awt.Color(255, 255, 255));
        btnQuitar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/presentacion/imagenes/Eliminar (2).png"))); // NOI18N
        btnQuitar.setText("Eliminar");
        btnQuitar.setPreferredSize(new java.awt.Dimension(127, 41));
        btnQuitar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQuitarActionPerformed(evt);
            }
        });

        txtNombreProveedor.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        txtNombreProveedor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204), 2));
        txtNombreProveedor.setPreferredSize(new java.awt.Dimension(4, 25));

        tablaDetalles.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tablaDetalles.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tablaDetalles.setRowHeight(22);
        jScrollPane3.setViewportView(tablaDetalles);

        jPanel10.setBackground(new java.awt.Color(0, 153, 204));

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("Ingresos");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        btnGuardar.setBackground(new java.awt.Color(102, 102, 102));
        btnGuardar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnGuardar.setForeground(new java.awt.Color(255, 255, 255));
        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/presentacion/imagenes/boton.png"))); // NOI18N
        btnGuardar.setText("Guardar");
        btnGuardar.setPreferredSize(new java.awt.Dimension(100, 37));
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        btnCancelar.setBackground(new java.awt.Color(102, 102, 102));
        btnCancelar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnCancelar.setForeground(new java.awt.Color(255, 255, 255));
        btnCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/presentacion/imagenes/cancelar.png"))); // NOI18N
        btnCancelar.setText("Cancelar");
        btnCancelar.setPreferredSize(new java.awt.Dimension(100, 37));
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        jPanel12.setBackground(new java.awt.Color(0, 153, 204));

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 42, Short.MAX_VALUE)
        );

        btnSeleccionarProveedor.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnSeleccionarProveedor.setText("...");
        btnSeleccionarProveedor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204), 2));
        btnSeleccionarProveedor.setPreferredSize(new java.awt.Dimension(4, 25));
        btnSeleccionarProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSeleccionarProveedorActionPerformed(evt);
            }
        });

        btnVerArticulos.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnVerArticulos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/presentacion/imagenes/buscarVenta.png"))); // NOI18N
        btnVerArticulos.setText("Ver");
        btnVerArticulos.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204), 2));
        btnVerArticulos.setPreferredSize(new java.awt.Dimension(4, 27));
        btnVerArticulos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerArticulosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel5)
                    .addComponent(jLabel8)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtIdProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtNombreProveedor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cboTipoComprobante, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnVerArticulos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSeleccionarProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(82, 82, 82))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7))
                        .addGap(18, 18, 18)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtNumComprobante, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtSerieComprobante, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                    .addComponent(txtImpuesto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 235, Short.MAX_VALUE)
                .addComponent(btnQuitar, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12)
                    .addComponent(jLabel9)
                    .addComponent(jLabel13))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtSubTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtTotalImpuesto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31))
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane3)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtIdProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtNombreProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSeleccionarProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel5)
                                    .addComponent(cboTipoComprobante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addComponent(btnVerArticulos, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtImpuesto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtSerieComprobante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel7)
                                    .addComponent(txtNumComprobante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(btnQuitar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(8, 8, 8)))))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 328, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtSubTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtTotalImpuesto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(14, 14, 14)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 64, Short.MAX_VALUE)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        tabGeneral.addTab("Mantenimiento", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabGeneral, javax.swing.GroupLayout.DEFAULT_SIZE, 1293, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabGeneral)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnQuitarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuitarActionPerformed
        // TODO add your handling code here:
        if (tablaDetalles.getSelectedRowCount() == 1) {
            this.modeloDetalles.removeRow(tablaDetalles.getSelectedRow());
            this.calcularTotales();
        } else {
            this.mensajeError("Seleccione el detalle a quitar.");
        }
    }//GEN-LAST:event_btnQuitarActionPerformed

    private void txtSubTotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSubTotalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSubTotalActionPerformed

    private void txtCodigoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodigoKeyReleased
        // TODO add your handling code here:
        if (txtCodigo.getText().length() > 0) {
            if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                entidades.Articulo art;
                art = this.CONTROL.obtenerArticuloCodigoIngreso(txtCodigo.getText());
                if (art == null) {
                    this.mensajeError("No existe un artículo con ese código");
                } else {
                    this.agregarDetalles(Integer.toString(art.getId()), art.getCodigo(), art.getNombre(), Double.toString(art.getPrecioVenta()));
                }
            }

        } else {
            this.mensajeError("Ingrese el código a buscar.");
        }
    }//GEN-LAST:event_txtCodigoKeyReleased

    private void txtSerieComprobanteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSerieComprobanteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSerieComprobanteActionPerformed

    private void cboTipoComprobanteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTipoComprobanteActionPerformed
        // TODO add your handling code here:
        this.obtenerNumero();
    }//GEN-LAST:event_cboTipoComprobanteActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        // TODO add your handling code here:
        this.listar(txtBuscar.getText(), false);
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void btnNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoActionPerformed
        // TODO add your handling code here:
        tabGeneral.setEnabledAt(1, true);
        tabGeneral.setEnabledAt(0, false);
        tabGeneral.setSelectedIndex(1);
        this.accion = "guardar";
        btnGuardar.setText("Guardar");
        this.obtenerNumero();
    }//GEN-LAST:event_btnNuevoActionPerformed

    private void btnDesactivarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDesactivarActionPerformed
        // TODO add your handling code here:
        if (tablaListado.getSelectedRowCount() == 1) {
            String id = String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(), 0));
            String comprobante = String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(), 5));
            String serie = String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(), 6));
            String numero = String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(), 7));

            if (JOptionPane.showConfirmDialog(this, "Deseas anular el registro: " + comprobante + " " + serie + "-" + numero + " ?", "TecnoSoft", JOptionPane.YES_NO_OPTION) == 0) {
                String resp = this.CONTROL.anular(Integer.parseInt(id));
                if (resp.equals("OK")) {
                    this.mensajeOk("Ingreso anulado");
                    this.listar("", false);
                } else {
                    this.mensajeError(resp);
                }
            }
        } else {
            this.mensajeError("Seleccione 1 ingreso para desactivar.");
        }
    }//GEN-LAST:event_btnDesactivarActionPerformed

    private void cboNumPaginaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboNumPaginaActionPerformed
        // TODO add your handling code here:
        if (this.primeraCarga == false) {
            this.listar("", true);
        }
    }//GEN-LAST:event_cboNumPaginaActionPerformed

    private void cboTotalPorPaginaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTotalPorPaginaActionPerformed
        // TODO add your handling code here:
        this.paginar();
    }//GEN-LAST:event_cboTotalPorPaginaActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        // TODO add your handling code here:
        if (txtIdProveedor.getText().length() == 0) {
            JOptionPane.showMessageDialog(this, "Debes seleccionar un proveedor.", "TecnoSoft", JOptionPane.WARNING_MESSAGE);
            btnSeleccionarProveedor.requestFocus();
            return;
        }
        if (txtSerieComprobante.getText().length() > 7) {
            JOptionPane.showMessageDialog(this, "Debes ingresar una serie no mayor a 7 caracteres.", "TecnoSoft", JOptionPane.WARNING_MESSAGE);
            txtSerieComprobante.requestFocus();
            return;
        }
        if (txtNumComprobante.getText().length() == 0 || txtNumComprobante.getText().length() > 10) {
            JOptionPane.showMessageDialog(this, "Debes ingresar un número de comprobante no mayor a 10 caracteres.", "TecnoSoft", JOptionPane.WARNING_MESSAGE);
            txtNumComprobante.requestFocus();
            return;
        }
        if (modeloDetalles.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Debes agregar artículos al detalle.", "TecnoSoft", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String resp = "";
        resp = this.CONTROL.insertar(Integer.parseInt(txtIdProveedor.getText()), (String) cboTipoComprobante.getSelectedItem(), txtSerieComprobante.getText(), txtNumComprobante.getText(), Double.parseDouble(txtImpuesto.getText()), Double.parseDouble(txtTotal.getText()), modeloDetalles);
        if (resp.equals("OK")) {
            this.mensajeOk("Registrado correctamente");
            this.limpiar();
            this.listar("", false);
        } else {
            this.mensajeError(resp);
        }
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        // TODO add your handling code here:
        tabGeneral.setEnabledAt(0, true);
        tabGeneral.setEnabledAt(1, false);
        tabGeneral.setSelectedIndex(0);
        this.limpiar();
        btnQuitar.setVisible(true);
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnSeleccionarProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSeleccionarProveedorActionPerformed
        // TODO add your handling code here:
        FrmSeleccionarProveedorC frm = new FrmSeleccionarProveedorC(contenedor, this, true);
        frm.toFront();
    }//GEN-LAST:event_btnSeleccionarProveedorActionPerformed

    private void btnVerArticulosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerArticulosActionPerformed
        // TODO add your handling code here:
        FrmSeleccionarArticuloC frm = new FrmSeleccionarArticuloC(contenedor, this, true);
        frm.toFront();
    }//GEN-LAST:event_btnVerArticulosActionPerformed

    private void btnVerIngresoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerIngresoActionPerformed
        // TODO add your handling code here:
        if (tablaListado.getSelectedRowCount() == 1) {
            String id = String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(), 0));
            String idProveedor = String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(), 3));
            String nombreProveedor = String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(), 4));
            String tipoComprobante = String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(), 5));
            String serie = String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(), 6));
            String numero = String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(), 7));
            String impuesto = String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(), 9));

            txtIdProveedor.setText(idProveedor);
            txtNombreProveedor.setText(nombreProveedor);
            cboTipoComprobante.setSelectedItem(tipoComprobante);
            txtSerieComprobante.setText(serie);
            txtNumComprobante.setText(numero);
            txtImpuesto.setText(impuesto);

            this.modeloDetalles = CONTROL.listarDetalle(Integer.parseInt(id));
            tablaDetalles.setModel(modeloDetalles);
            this.COLORTABLA.cargarColor(tablaDetalles);
            this.calcularTotales();
            btnQuitar.setVisible(false);

            tabGeneral.setEnabledAt(1, true);
            tabGeneral.setEnabledAt(0, false);
            tabGeneral.setSelectedIndex(1);
            btnGuardar.setVisible(false);
        } else {
            this.mensajeError("Seleccione el ingreso a mostrar.");
        }
    }//GEN-LAST:event_btnVerIngresoActionPerformed

    private void btnReporteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReporteActionPerformed
        // TODO add your handling code here:
//        String estado = String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(), 11));
//        if (estado.equals("Anulado")) {
//            this.mensajeError("El ingreso esta anulado no se puede generar el reporte");
//            return;
//        }
        if (tablaListado.getSelectedRowCount() == 1) {
            String id = String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(), 0));
            this.CONTROL.reporteIngreso(id);
        } else {
            this.mensajeError("Seleccione la venta para generar la factura.");
        }
    }//GEN-LAST:event_btnReporteActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnDesactivar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnNuevo;
    private javax.swing.JButton btnQuitar;
    private javax.swing.JButton btnReporte;
    private javax.swing.JButton btnSeleccionarProveedor;
    private javax.swing.JButton btnVerArticulos;
    private javax.swing.JButton btnVerIngreso;
    private javax.swing.JComboBox<String> cboNumPagina;
    private javax.swing.JComboBox<String> cboTipoComprobante;
    private javax.swing.JComboBox<String> cboTotalPorPagina;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblTotalRegistros;
    private javax.swing.JTabbedPane tabGeneral;
    private javax.swing.JTable tablaDetalles;
    private javax.swing.JTable tablaListado;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtCodigo;
    public javax.swing.JTextField txtIdProveedor;
    private javax.swing.JTextField txtImpuesto;
    public javax.swing.JTextField txtNombreProveedor;
    private javax.swing.JTextField txtNumComprobante;
    private javax.swing.JTextField txtSerieComprobante;
    private javax.swing.JTextField txtSubTotal;
    private javax.swing.JTextField txtTotal;
    private javax.swing.JTextField txtTotalImpuesto;
    // End of variables declaration//GEN-END:variables
}
