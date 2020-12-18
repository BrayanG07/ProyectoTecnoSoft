/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entidades;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Buddys
 */
public class ColorTabla extends DefaultTableCellRenderer {
    
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,int columna){
       super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, columna);
        if (Par(row)) {
            this.setBackground(new Color(0,153,204));
            this.setForeground(Color.BLACK);
        }else{
            this.setBackground(new Color(255,255,255));
            this.setForeground(Color.BLACK);
        }
 
       return this;
    }
    
    protected boolean Par(int row){
        if (row%2 == 0){
            return true;
        }else{
            return false;
        }
    }
}
