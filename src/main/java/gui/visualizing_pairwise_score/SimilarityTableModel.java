/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.visualizing_pairwise_score;

import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Sule
 */
public class SimilarityTableModel extends AbstractTableModel {

    public SimilarityTableModel(String[] columnNames, Object[][] data) {
        this.columnNames = columnNames;
        this.data = data;
    }
   
    private String[] columnNames;
    private Object[][] data;

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return data.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        return data[row][col];
    }
  
    public void setValueAt(Object value, int row, int col) {
        data[row][col] = value;
        fireTableCellUpdated(row, col);
    }
 
    public int getLeadSelectionIndex() {
        return -1;
    }

}
