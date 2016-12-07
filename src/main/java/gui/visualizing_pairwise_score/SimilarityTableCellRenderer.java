/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.visualizing_pairwise_score;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Sule
 */
public class SimilarityTableCellRenderer extends DefaultTableCellRenderer {

    private int indSpecA = 1, 
            indSpecB = 4;

    public int getIndSpecA() {
        return indSpecA;
    }

    public void setIndSpecA(int indSpecA) {
        this.indSpecA = indSpecA;
    }

    public int getIndSpecB() {
        return indSpecB;
    }

    public void setIndSpecB(int indSpecB) {
        this.indSpecB = indSpecB;
    }
 
    @Override
    public Component getTableCellRendererComponent(JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column) {
        Component c = super.getTableCellRendererComponent(table, value,
                isSelected, hasFocus,
                row, column);
        Color lightBlue = Color.getHSBColor(0.56f, 0.3f, 1f),
                lightPink = Color.getHSBColor(0.92f, 0.3f, 1f);
        // 0.16f,0.4f, 1f = lightYellow
        // Color.getHSBColor(0.76f,0.4f, 1f) = purple        
        if (column == indSpecA) {
            c.setBackground(lightPink);
            c.setForeground(Color.BLACK);
        } else if (column == indSpecB) {
            c.setBackground(lightBlue);
            c.setForeground(Color.BLACK);
        } else {
            c.setBackground(Color.WHITE);
            c.setForeground(Color.BLACK);
        }
        // if a cell is selected, highlight a row 
        if (isSelected) {
            if (column == indSpecA) {
                c.setBackground(Color.RED);
                c.setForeground(Color.WHITE);
            } else if (column == indSpecB) {
                c.setBackground(Color.BLUE);
                c.setForeground(Color.WHITE);
            } else {
                c.setBackground(Color.GRAY);
                c.setForeground(Color.WHITE);
            }
        }
        return c;
    }
}
