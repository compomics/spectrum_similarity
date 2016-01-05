/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui_spectral_match_visualization;

import preprocess.filter.noise.implementation.NoiseFilteringPrideAsap;
import preprocess.sort.Sorting;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import com.compomics.util.experiment.massspectrometry.SpectrumFactory;
import com.compomics.util.gui.spectrum.SpectrumPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.TableRowSorter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.DefaultXYZDataset;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 *
 * @author Sule
 */
public final class MainGUI extends javax.swing.JFrame {

    private MSnSpectrum original_spec_A = new MSnSpectrum(),
            original_spec_B = new MSnSpectrum(),
            tmp_spec_A = new MSnSpectrum(),
            tmp_spec_B = new MSnSpectrum();
    private DefaultXYZDataset xyzDataset = new DefaultXYZDataset();
    private String x = "m/z",
            y = "intensity",
            z = "default";
    private int default_bubble_size_v1 = 100000,
            default_bubble_size_v2 = 10,
            indSpecA,
            indSpecB,
            indScore;
    private String[] specTitles = new String[2];
    private JFreeChart chart = null;
    private String specAFolder = "",
            specBFolder = "";
    private ChartPanel chartPanel = new ChartPanel(null);
    private SpectrumPanel spectrumPanel;
    private StartDialog startDialog;
    private String[] columnNames = null;
    private File scoreFile;
    private boolean isOpenFileMenu = false;
    private SpectrumFactory spFct = SpectrumFactory.getInstance();

    /**
     * Creates new form MainGUI
     *
     * @throws uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException
     * @throws java.io.FileNotFoundException
     * @throws java.lang.ClassNotFoundException
     */
    public MainGUI() throws MzMLUnmarshallerException, FileNotFoundException, ClassNotFoundException {
        startDialog = new StartDialog(this, true);
        initComponents();
        setSpecAFolder(startDialog.getSpecAFolder());
        setSpecBFolder(startDialog.getSpecBFolder());
        bubbleSpectrajRadioButton.setSelected(true);
        prepareTable();
        setLocation(200, WIDTH);
        this.setVisible(true);
    }

    public String getSpecAFolder() {
        return specAFolder;
    }

    public void setSpecAFolder(String specAFolder) {
        this.specAFolder = specAFolder;
    }

    public String getSpecBFolder() {
        return specBFolder;
    }

    public void setSpecBFolder(String specBFolder) {
        this.specBFolder = specBFolder;
    }

    public void prepareTable() throws MzMLUnmarshallerException, FileNotFoundException, ClassNotFoundException {
        if (!isOpenFileMenu) {
            scoreFile = new File(startDialog.getPathToScoreFilejTextField().getText());
        }
        try {
            indSpecA = startDialog.getIndSpecA();
            indSpecB = startDialog.getIndSpecB();
            indScore = startDialog.getIndScore();
            // read the file
            BufferedReader br = new BufferedReader(new FileReader(scoreFile.getAbsolutePath()));
            String line = null;
            int row_number = 0,
                    control = 0;
            ArrayList<String[]> dataStrArr = new ArrayList<String[]>();
            while ((line = br.readLine()) != null) {
                if (!line.isEmpty()) {
                    String[] split = line.split("\t");
                    // prepare column names
                    if (control == 0) {
                        columnNames = new String[split.length + 1];
                        columnNames[0] = "number";
                        for (int i = 0; i < split.length; i++) {
                            columnNames[i + 1] = split[i];
                        }
                    }
                    // prepare data
                    if (control != 0) {
                        if (!split[0].equals(split[1])) {
                            dataStrArr.add(split);
                            row_number++;
                        }
                    }
                    control++;
                }
            }
            // construct a 2D array for TableModel
            Object[][] data = new Object[row_number][columnNames.length];

            // Prepare similarity table
            SimilarityTableModel similarityTableModel = new SimilarityTableModel(columnNames, data);
            scoreTable.setModel(similarityTableModel);
            scoreTable.setFillsViewportHeight(true);

            // Prepare a sorter
            TableRowSorter sorter = new TableRowSorter(scoreTable.getModel()) {
                @Override
                public Comparator getComparator(int column) {
                    Comparator<String> comparator = new Comparator<String>() {
                        public int compare(String o1, String o2) {
                            Double o1_integer = Double.parseDouble(o1),
                                    o2_integer = Double.parseDouble(o2);
                            return (o1_integer.compareTo(o2_integer));
                        }
                    };
                    return comparator;
                }
            };

            // Fill information on data.
            int number = 0;
            for (int arr = 0; arr < dataStrArr.size(); arr++) {
                String[] strArr = dataStrArr.get(arr);
                if (!strArr[0].equals(strArr[1])) {
                    data[arr][0] = number;
                    number++;
                    scoreTable.setValueAt(number, arr, 0);
                    for (int i = 0; i < strArr.length; i++) {
                        data[arr][i + 1] = strArr[i];
                        scoreTable.setValueAt(strArr[i], arr, i + 1);
                    }
                }
            }
            // set some variables on a table         
            SimilarityTableCellRenderer renderer = new SimilarityTableCellRenderer();
            if(indSpecA != 1){
                renderer.setIndSpecA(indSpecA);
            }
            if(indSpecB != 1){
                renderer.setIndSpecB(indSpecB);
            }
            
            scoreTable.setDefaultRenderer(Object.class, renderer);
            scoreTable.setRowSorter(sorter);
            scoreTable.setAutoCreateColumnsFromModel(true);
            scoreTable.setPreferredScrollableViewportSize(scoreTable.getPreferredSize());

            // select an entire row by clicking
            scoreTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
            scoreTable.setRowSelectionAllowed(true);
            scoreTable.setColumnSelectionAllowed(false);

            scoreTable.setRowSelectionInterval(0, 0);
            String specAname = (String) scoreTable.getValueAt(0, indSpecA),
                    specBname = (String) scoreTable.getValueAt(0, indSpecB);
            System.out.println("specAname=" + specAname + " and specBname=" + specBname);
            prepareSpectraForPlotting(specAname, specBname);
            plotBubbleSpectra(original_spec_A, original_spec_B);
            // prepare the data and load a file
            slider_specA_jSlider.setValue(100);
            slider_specB_jSlider.setValue(100);
        } catch (IOException ex) {
            System.out.println("problem accessing file" + scoreFile.getAbsolutePath());
        }
    }

    public void setX(String x) {
        this.x = x;
    }

    public void setY(String y) {
        this.y = y;
    }

    public void setZ(String z) {
        this.z = z;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileChooser = new javax.swing.JFileChooser();
        spectrumjPanel = new javax.swing.JPanel();
        plotjPanel = new javax.swing.JPanel();
        mirroredSpectrajRadioButton = new javax.swing.JRadioButton();
        bubbleSpectrajRadioButton = new javax.swing.JRadioButton();
        bubblePlotSettingsjPanel = new javax.swing.JPanel();
        xAxisjLabel = new javax.swing.JLabel();
        xAxisComboBox = new javax.swing.JComboBox();
        yAxisjLabel = new javax.swing.JLabel();
        yAxisComboBox = new javax.swing.JComboBox();
        zAxisjLabel = new javax.swing.JLabel();
        zAxisjComboBox = new javax.swing.JComboBox();
        replotButton = new javax.swing.JButton();
        scoreTablejScrollPane = new javax.swing.JScrollPane();
        scoreTable = new javax.swing.JTable();
        filteringjTabbedPane = new javax.swing.JTabbedPane();
        specA_filtering_jPanel = new javax.swing.JPanel();
        slider_specA_jSlider = new javax.swing.JSlider();
        remaining_peaks_specA_jTextField = new javax.swing.JTextField();
        auto_specA_jButton = new javax.swing.JButton();
        specA_remaining_percentage_jLabel = new javax.swing.JLabel();
        specB_filtering_jPanel = new javax.swing.JPanel();
        auto_specB_jButton = new javax.swing.JButton();
        remaining_peaks_specB_jTextField = new javax.swing.JTextField();
        slider_specB_jSlider = new javax.swing.JSlider();
        specB_remaining_percentage_jLabel = new javax.swing.JLabel();
        FileMenuBar = new javax.swing.JMenuBar();
        menuMenu = new javax.swing.JMenu();
        openScoreFileMenuItem = new javax.swing.JMenuItem();
        loadSpecAFolderjMenuItem = new javax.swing.JMenuItem();
        loadSpecBFolderjMenuItem = new javax.swing.JMenuItem();
        clearjMenuItem = new javax.swing.JMenuItem();
        saveImagejMenuItem = new javax.swing.JMenuItem();
        exitMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Similarity visualization");
        setBackground(new java.awt.Color(236, 233, 233));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setFont(new java.awt.Font("Trebuchet MS", 0, 12)); // NOI18N

        spectrumjPanel.setBackground(new java.awt.Color(255, 255, 255));
        spectrumjPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        spectrumjPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        spectrumjPanel.setPreferredSize(new java.awt.Dimension(1198, 559));
        spectrumjPanel.setLayout(new java.awt.GridBagLayout());

        plotjPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "PLOT", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Trebuchet MS", 1, 14))); // NOI18N

        mirroredSpectrajRadioButton.setFont(new java.awt.Font("Trebuchet MS", 0, 14)); // NOI18N
        mirroredSpectrajRadioButton.setText("Mirrored spectra");
        mirroredSpectrajRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mirroredSpectrajRadioButtonActionPerformed(evt);
            }
        });

        bubbleSpectrajRadioButton.setFont(new java.awt.Font("Trebuchet MS", 0, 14)); // NOI18N
        bubbleSpectrajRadioButton.setText("Bubble spectra");
        bubbleSpectrajRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bubbleSpectrajRadioButtonActionPerformed(evt);
            }
        });

        bubblePlotSettingsjPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Bubble plot setting", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Trebuchet MS", 1, 14))); // NOI18N

        xAxisjLabel.setFont(new java.awt.Font("Trebuchet MS", 0, 14)); // NOI18N
        xAxisjLabel.setText("x axis:");
        xAxisjLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        xAxisComboBox.setFont(new java.awt.Font("Trebuchet MS", 0, 13)); // NOI18N
        xAxisComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "m/z", "intensity" }));
        xAxisComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xAxisComboBoxActionPerformed(evt);
            }
        });

        yAxisjLabel.setFont(new java.awt.Font("Trebuchet MS", 0, 14)); // NOI18N
        yAxisjLabel.setText("y axis:");
        yAxisjLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        yAxisComboBox.setFont(new java.awt.Font("Trebuchet MS", 0, 13)); // NOI18N
        yAxisComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "m/z", "intensity", "default" }));
        yAxisComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yAxisComboBoxActionPerformed(evt);
            }
        });

        zAxisjLabel.setText("z axis:");

        zAxisjComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "m/z", "intensity", "default" }));
        zAxisjComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zAxisjComboBoxActionPerformed(evt);
            }
        });

        replotButton.setFont(new java.awt.Font("Trebuchet MS", 0, 14)); // NOI18N
        replotButton.setText("REPLOT");
        replotButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replotButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout bubblePlotSettingsjPanelLayout = new javax.swing.GroupLayout(bubblePlotSettingsjPanel);
        bubblePlotSettingsjPanel.setLayout(bubblePlotSettingsjPanelLayout);
        bubblePlotSettingsjPanelLayout.setHorizontalGroup(
            bubblePlotSettingsjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bubblePlotSettingsjPanelLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(xAxisjLabel)
                .addGap(5, 5, 5)
                .addComponent(xAxisComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addGroup(bubblePlotSettingsjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(bubblePlotSettingsjPanelLayout.createSequentialGroup()
                        .addComponent(yAxisjLabel)
                        .addGap(5, 5, 5)
                        .addComponent(yAxisComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(zAxisjLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(zAxisjComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(bubblePlotSettingsjPanelLayout.createSequentialGroup()
                        .addComponent(replotButton, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        bubblePlotSettingsjPanelLayout.setVerticalGroup(
            bubblePlotSettingsjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bubblePlotSettingsjPanelLayout.createSequentialGroup()
                .addGroup(bubblePlotSettingsjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(bubblePlotSettingsjPanelLayout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(xAxisjLabel))
                    .addGroup(bubblePlotSettingsjPanelLayout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(xAxisComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(bubblePlotSettingsjPanelLayout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(yAxisjLabel))
                    .addGroup(bubblePlotSettingsjPanelLayout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addGroup(bubblePlotSettingsjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(yAxisComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(zAxisjLabel)
                            .addComponent(zAxisjComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addComponent(replotButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout plotjPanelLayout = new javax.swing.GroupLayout(plotjPanel);
        plotjPanel.setLayout(plotjPanelLayout);
        plotjPanelLayout.setHorizontalGroup(
            plotjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(plotjPanelLayout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addComponent(mirroredSpectrajRadioButton)
                .addGap(30, 30, 30)
                .addComponent(bubbleSpectrajRadioButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(plotjPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(bubblePlotSettingsjPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        plotjPanelLayout.setVerticalGroup(
            plotjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(plotjPanelLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(plotjPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mirroredSpectrajRadioButton)
                    .addComponent(bubbleSpectrajRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(bubblePlotSettingsjPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        scoreTablejScrollPane.setMaximumSize(new java.awt.Dimension(999999999, 999999999));

        scoreTable.setAutoCreateRowSorter(true);
        scoreTable.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        scoreTable.setMaximumSize(new java.awt.Dimension(100, 100));
        scoreTable.setMinimumSize(new java.awt.Dimension(10, 10));
        scoreTable.setName("table"); // NOI18N
        scoreTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                scoreTableMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                scoreTableMouseReleased(evt);
            }
        });
        scoreTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                scoreTableKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                scoreTableKeyReleased(evt);
            }
        });
        scoreTablejScrollPane.setViewportView(scoreTable);

        filteringjTabbedPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "FILTERING", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Trebuchet MS", 1, 14))); // NOI18N
        filteringjTabbedPane.setAutoscrolls(true);
        filteringjTabbedPane.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        filteringjTabbedPane.setInheritsPopupMenu(true);
        filteringjTabbedPane.setName(""); // NOI18N

        slider_specA_jSlider.setMajorTickSpacing(20);
        slider_specA_jSlider.setMinorTickSpacing(5);
        slider_specA_jSlider.setPaintLabels(true);
        slider_specA_jSlider.setPaintTicks(true);
        slider_specA_jSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slider_specA_jSliderStateChanged(evt);
            }
        });

        auto_specA_jButton.setText("NoiseFilter");
        auto_specA_jButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                auto_specA_jButtonActionPerformed(evt);
            }
        });

        specA_remaining_percentage_jLabel.setText("Percentage of remaining peaks ");

        javax.swing.GroupLayout specA_filtering_jPanelLayout = new javax.swing.GroupLayout(specA_filtering_jPanel);
        specA_filtering_jPanel.setLayout(specA_filtering_jPanelLayout);
        specA_filtering_jPanelLayout.setHorizontalGroup(
            specA_filtering_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(specA_filtering_jPanelLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(specA_remaining_percentage_jLabel))
            .addGroup(specA_filtering_jPanelLayout.createSequentialGroup()
                .addComponent(slider_specA_jSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(remaining_peaks_specA_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(auto_specA_jButton))
        );
        specA_filtering_jPanelLayout.setVerticalGroup(
            specA_filtering_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(specA_filtering_jPanelLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(specA_remaining_percentage_jLabel)
                .addGap(16, 16, 16)
                .addGroup(specA_filtering_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(slider_specA_jSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(remaining_peaks_specA_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(auto_specA_jButton)))
        );

        filteringjTabbedPane.addTab("SpecA", specA_filtering_jPanel);

        auto_specB_jButton.setText("NoiseFilter");
        auto_specB_jButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                auto_specB_jButtonActionPerformed(evt);
            }
        });

        slider_specB_jSlider.setMajorTickSpacing(20);
        slider_specB_jSlider.setMinorTickSpacing(5);
        slider_specB_jSlider.setPaintLabels(true);
        slider_specB_jSlider.setPaintTicks(true);
        slider_specB_jSlider.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        slider_specB_jSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slider_specB_jSliderStateChanged(evt);
            }
        });

        specB_remaining_percentage_jLabel.setText("Percentage of remaining peaks ");

        javax.swing.GroupLayout specB_filtering_jPanelLayout = new javax.swing.GroupLayout(specB_filtering_jPanel);
        specB_filtering_jPanel.setLayout(specB_filtering_jPanelLayout);
        specB_filtering_jPanelLayout.setHorizontalGroup(
            specB_filtering_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(specB_filtering_jPanelLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(specB_remaining_percentage_jLabel))
            .addGroup(specB_filtering_jPanelLayout.createSequentialGroup()
                .addComponent(slider_specB_jSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(remaining_peaks_specB_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(auto_specB_jButton))
        );
        specB_filtering_jPanelLayout.setVerticalGroup(
            specB_filtering_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(specB_filtering_jPanelLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(specB_remaining_percentage_jLabel)
                .addGap(16, 16, 16)
                .addGroup(specB_filtering_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(slider_specB_jSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(remaining_peaks_specB_jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(auto_specB_jButton)))
        );

        filteringjTabbedPane.addTab("SpecB", specB_filtering_jPanel);

        menuMenu.setText("Menu");

        openScoreFileMenuItem.setFont(new java.awt.Font("Trebuchet MS", 0, 15)); // NOI18N
        openScoreFileMenuItem.setText("Open a score file");
        openScoreFileMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openScoreFileMenuItemActionPerformed(evt);
            }
        });
        menuMenu.add(openScoreFileMenuItem);

        loadSpecAFolderjMenuItem.setFont(new java.awt.Font("Trebuchet MS", 0, 15)); // NOI18N
        loadSpecAFolderjMenuItem.setText("Load a specA folder");
        loadSpecAFolderjMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadSpecAFolderjMenuItemActionPerformed(evt);
            }
        });
        menuMenu.add(loadSpecAFolderjMenuItem);

        loadSpecBFolderjMenuItem.setFont(new java.awt.Font("Trebuchet MS", 0, 15)); // NOI18N
        loadSpecBFolderjMenuItem.setText("Load a specB folder");
        loadSpecBFolderjMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadSpecBFolderjMenuItemActionPerformed(evt);
            }
        });
        menuMenu.add(loadSpecBFolderjMenuItem);

        clearjMenuItem.setFont(new java.awt.Font("Trebuchet MS", 0, 15)); // NOI18N
        clearjMenuItem.setText("Clear");
        clearjMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearjMenuItemActionPerformed(evt);
            }
        });
        menuMenu.add(clearjMenuItem);

        saveImagejMenuItem.setFont(new java.awt.Font("Trebuchet MS", 0, 15)); // NOI18N
        saveImagejMenuItem.setText("Save image");
        saveImagejMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveImagejMenuItemActionPerformed(evt);
            }
        });
        menuMenu.add(saveImagejMenuItem);

        exitMenuItem.setFont(new java.awt.Font("Trebuchet MS", 0, 15)); // NOI18N
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        menuMenu.add(exitMenuItem);

        FileMenuBar.add(menuMenu);

        setJMenuBar(FileMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(scoreTablejScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 1422, Short.MAX_VALUE)
                        .addGap(21, 21, 21))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(spectrumjPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 984, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(plotjPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(filteringjTabbedPane))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(spectrumjPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGap(32, 32, 32))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(plotjPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(filteringjTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 168, Short.MAX_VALUE)))
                .addComponent(scoreTablejScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
                .addContainerGap(50, Short.MAX_VALUE))
        );

        filteringjTabbedPane.getAccessibleContext().setAccessibleName("Filtering");
        filteringjTabbedPane.getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void xAxisComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xAxisComboBoxActionPerformed
        setX((String) xAxisComboBox.getSelectedItem());
    }//GEN-LAST:event_xAxisComboBoxActionPerformed

    private void yAxisComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yAxisComboBoxActionPerformed
        setY((String) yAxisComboBox.getSelectedItem());
    }//GEN-LAST:event_yAxisComboBoxActionPerformed

    private void replotButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_replotButtonActionPerformed
        slider_specA_jSlider.setValue(100);
        slider_specB_jSlider.setValue(100);
        remaining_peaks_specA_jTextField.setText(" ");
        remaining_peaks_specB_jTextField.setText(" ");

        spectrumjPanel.removeAll();
        int selectedRowIndex = scoreTable.getSelectedRow();

        String specAname = scoreTable.getValueAt(selectedRowIndex, indSpecA).toString(),
                specBname = scoreTable.getValueAt(selectedRowIndex, indSpecB).toString();
        try {
            prepareSpectraForPlotting(specAname, specBname);
        } catch (IOException ex) {
            Logger.getLogger(MainGUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MzMLUnmarshallerException ex) {
            Logger.getLogger(MainGUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MainGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        plotBubbleSpectra(original_spec_A, original_spec_B);
    }//GEN-LAST:event_replotButtonActionPerformed

    public MSnSpectrum filterSpectrum(MSnSpectrum ms, MSnSpectrum tmp_ms, int removal_percentage) {
        HashMap<Double, Peak> specApeakMap = ms.getPeakMap(),
                remainedSpecAMap = new HashMap<Double, Peak>();
        // prepare mzAintensity map
        HashMap<Double, Double> mzAintensity = new HashMap<Double, Double>();
        for (Double mz : specApeakMap.keySet()) {
            mzAintensity.put(mz, specApeakMap.get(mz).intensity);
        }
        Map sortedByValue = Sorting.sortByValue(mzAintensity);
        Set<Double> specSortedSet = sortedByValue.keySet();
        ArrayList<Double> specSortedList = new ArrayList<Double>(specSortedSet);
        int initial_peak_size = specSortedList.size(),
                final_size = initial_peak_size * removal_percentage / 100;
        for (int i = initial_peak_size - final_size; i < initial_peak_size; i++) {
            double mz = specSortedList.get(i);
            Peak peak = specApeakMap.get(mz);
            remainedSpecAMap.put(mz, peak);
        }
        tmp_ms.setPeakList(remainedSpecAMap);
        return tmp_ms;
    }

    private void mirroredSpectrajRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mirroredSpectrajRadioButtonActionPerformed
        if (mirroredSpectrajRadioButton.isSelected()) {
            String[] options = new String[2];
            options[0] = "m/z";
            options[1] = "intensity";
            xAxisComboBox.setModel(new DefaultComboBoxModel(options));
            plotMirroredSpectra(original_spec_A, original_spec_B);
            bubbleSpectrajRadioButton.setSelected(false);
            for (Component c : bubblePlotSettingsjPanel.getComponents()) {
                c.setEnabled(false);
            }
            setX("m/z");
            setY("intensity");
            tmp_spec_A = create_tmp_ms(original_spec_A);
            tmp_spec_B = create_tmp_ms(original_spec_B);
        }
    }//GEN-LAST:event_mirroredSpectrajRadioButtonActionPerformed

    private void scoreTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_scoreTableMouseClicked
        // get specA and specB
        slider_specA_jSlider.setValue(100);
        slider_specB_jSlider.setValue(100);
        remaining_peaks_specA_jTextField.setText(" ");
        remaining_peaks_specB_jTextField.setText(" ");
        for (Component c : filteringjTabbedPane.getComponents()) {
            c.setEnabled(true);
        }
        int selectedRow = scoreTable.getSelectedRow();
        String specAname = scoreTable.getValueAt(selectedRow, indSpecA).toString(),
                specBname = scoreTable.getValueAt(selectedRow, indSpecB).toString();
        try {
            prepareSpectraForPlotting(specAname, specBname);
        } catch (IOException ex) {
            Logger.getLogger(MainGUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MzMLUnmarshallerException ex) {
            Logger.getLogger(MainGUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MainGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        // plot graphics
        if (mirroredSpectrajRadioButton.isSelected()) {
            plotMirroredSpectra(original_spec_A, original_spec_B);
        } else {
            plotBubbleSpectra(original_spec_A, original_spec_B);
        }
    }//GEN-LAST:event_scoreTableMouseClicked

    private void bubbleSpectrajRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bubbleSpectrajRadioButtonActionPerformed
        if (bubbleSpectrajRadioButton.isSelected()) {
            mirroredSpectrajRadioButton.setSelected(false);
            for (Component c : bubblePlotSettingsjPanel.getComponents()) {
                c.setEnabled(true);
            }
            plotBubbleSpectra(tmp_spec_A, tmp_spec_B);
            tmp_spec_A = create_tmp_ms(original_spec_A);
            tmp_spec_B = create_tmp_ms(original_spec_B);
        }
    }//GEN-LAST:event_bubbleSpectrajRadioButtonActionPerformed

    private void zAxisjComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zAxisjComboBoxActionPerformed
        setZ((String) zAxisjComboBox.getSelectedItem());
    }//GEN-LAST:event_zAxisjComboBoxActionPerformed

    private void slider_specA_jSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_slider_specA_jSliderStateChanged
        Integer slider_value = slider_specA_jSlider.getValue();
        remaining_peaks_specA_jTextField.setText(slider_value.toString());
        tmp_spec_A = filterSpectrum(original_spec_A, tmp_spec_A, (slider_value));
        if (bubbleSpectrajRadioButton.isSelected()) {
            plotBubbleSpectra(tmp_spec_A, tmp_spec_B);
        } else {
            plotMirroredSpectra(tmp_spec_A, tmp_spec_B);
        }
    }//GEN-LAST:event_slider_specA_jSliderStateChanged

    private void auto_specA_jButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_auto_specA_jButtonActionPerformed
        int size_initial = tmp_spec_A.getPeakList().size();
        NoiseFilteringPrideAsap obj = new NoiseFilteringPrideAsap();
        obj.noiseFilter(tmp_spec_A);
        if (bubbleSpectrajRadioButton.isSelected()) {
            plotBubbleSpectra(tmp_spec_A, tmp_spec_B);
        } else {
            plotMirroredSpectra(tmp_spec_A, tmp_spec_B);
        }
        int size_after_filtering = tmp_spec_A.getPeakList().size();
        Integer percentage = (int) ((int) (double) (100 * size_after_filtering) / (double) size_initial);
        slider_specA_jSlider.setValue(percentage);
        remaining_peaks_specA_jTextField.setText(percentage.toString());
        remaining_peaks_specA_jTextField.setEditable(false);
        slider_specA_jSlider.setPaintTicks(true);
        slider_specA_jSlider.setSnapToTicks(true);
        slider_specA_jSlider.setPaintLabels(true);
        slider_specA_jSlider.setInheritsPopupMenu(true);
    }//GEN-LAST:event_auto_specA_jButtonActionPerformed

    private void auto_specB_jButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_auto_specB_jButtonActionPerformed
        int size_initial = tmp_spec_B.getPeakList().size();
        NoiseFilteringPrideAsap obj = new NoiseFilteringPrideAsap();
        obj.noiseFilter(tmp_spec_B);
        if (bubbleSpectrajRadioButton.isSelected()) {
            plotBubbleSpectra(tmp_spec_A, tmp_spec_B);
        } else {
            plotMirroredSpectra(tmp_spec_A, tmp_spec_B);
        }
        int size_after_filtering = tmp_spec_B.getPeakList().size();
        Integer percentage = (int) ((int) (double) (100 * size_after_filtering) / (double) size_initial);
        slider_specB_jSlider.setValue(percentage);
        remaining_peaks_specB_jTextField.setText(percentage.toString());
        remaining_peaks_specB_jTextField.setEditable(false);
        slider_specB_jSlider.setPaintTicks(true);
        slider_specB_jSlider.setSnapToTicks(true);
        slider_specB_jSlider.setPaintLabels(true);
        slider_specB_jSlider.setInheritsPopupMenu(true);
    }//GEN-LAST:event_auto_specB_jButtonActionPerformed

    private void slider_specB_jSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_slider_specB_jSliderStateChanged
        Integer slider_value = slider_specB_jSlider.getValue();
        remaining_peaks_specB_jTextField.setText(slider_value.toString());
        tmp_spec_B = filterSpectrum(original_spec_B, tmp_spec_B, (slider_value));
        if (bubbleSpectrajRadioButton.isSelected()) {
            plotBubbleSpectra(tmp_spec_A, tmp_spec_B);
        } else {
            plotMirroredSpectra(tmp_spec_A, tmp_spec_B);
        }
    }//GEN-LAST:event_slider_specB_jSliderStateChanged

    private void openScoreFileMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openScoreFileMenuItemActionPerformed
        isOpenFileMenu = true;
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            // get a file name from openScore menu item.
            File openedFile = fileChooser.getSelectedFile();
            scoreFile = new File(openedFile.getAbsolutePath());
            fileChooser.setSelectedFile(scoreFile);
            System.out.println("Scorefile =" + scoreFile);
            try {
                prepareTable();
            } catch (MzMLUnmarshallerException ex) {
                Logger.getLogger(MainGUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(MainGUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(MainGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_openScoreFileMenuItemActionPerformed

    private void loadSpecAFolderjMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadSpecAFolderjMenuItemActionPerformed
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setMultiSelectionEnabled(true);
        int returnVal = fileChooser.showOpenDialog(this);
        // empty spec Arraylist 
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File folder = fileChooser.getSelectedFile();
            specAFolder = folder.getAbsolutePath();
        } else {
            System.out.println("File access is cancelled by user.");
        }

    }//GEN-LAST:event_loadSpecAFolderjMenuItemActionPerformed

    private void clearjMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearjMenuItemActionPerformed
        // clear all stored objects
        spectrumjPanel.removeAll();
        // construct a 2D array for TableModel
        Object[][] data = new Object[0][0];
        // Prepare similarity table
        columnNames = new String[0];
        SimilarityTableModel similarityTableModel = new SimilarityTableModel(columnNames, data);
        scoreTable.setModel(similarityTableModel);
        scoreTable.setFillsViewportHeight(true);
    }//GEN-LAST:event_clearjMenuItemActionPerformed

    private void saveImagejMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveImagejMenuItemActionPerformed
        // Now save image 
        JFileChooser savePlaylistDialog = new JFileChooser();
        int status = savePlaylistDialog.showSaveDialog(this);
        if (status == JFileChooser.APPROVE_OPTION) {
            File savePlaylist = savePlaylistDialog.getSelectedFile();
            BufferedImage bi = new BufferedImage(spectrumjPanel.getSize().width, spectrumjPanel.getSize().height, BufferedImage.TYPE_INT_ARGB);
            Graphics g = bi.createGraphics();
            this.paint(g);  //this == JComponent
            g.dispose();
            try {
                ImageIO.write(bi, "png", new File(savePlaylist + ".png"));
            } catch (Exception e) {
            }
        }
    }//GEN-LAST:event_saveImagejMenuItemActionPerformed

    private void loadSpecBFolderjMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadSpecBFolderjMenuItemActionPerformed
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setMultiSelectionEnabled(true);
        int returnVal = fileChooser.showOpenDialog(this);
        // empty spec Arraylist 
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File folder = fileChooser.getSelectedFile();
            specBFolder = folder.getAbsolutePath();
        } else {
            System.out.println("File access is cancelled by user.");
        }
    }//GEN-LAST:event_loadSpecBFolderjMenuItemActionPerformed

    private void scoreTableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_scoreTableKeyPressed
        // get specA and specB
        slider_specA_jSlider.setValue(100);
        slider_specB_jSlider.setValue(100);
        remaining_peaks_specA_jTextField.setText(" ");
        remaining_peaks_specB_jTextField.setText(" ");
        for (Component c : filteringjTabbedPane.getComponents()) {
            c.setEnabled(true);
        }
        int selectedRow = scoreTable.getSelectedRow();
        String specAname = scoreTable.getValueAt(selectedRow, indSpecA).toString(),
                specBname = scoreTable.getValueAt(selectedRow, indSpecB).toString();
        try {
            prepareSpectraForPlotting(specAname, specBname);
        } catch (IOException ex) {
            Logger.getLogger(MainGUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MzMLUnmarshallerException ex) {
            Logger.getLogger(MainGUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MainGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        // plot graphics
        if (mirroredSpectrajRadioButton.isSelected()) {
            plotMirroredSpectra(original_spec_A, original_spec_B);
        } else {
            plotBubbleSpectra(original_spec_A, original_spec_B);
        }
    }//GEN-LAST:event_scoreTableKeyPressed

    private void scoreTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_scoreTableMouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_scoreTableMouseReleased

    private void scoreTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_scoreTableKeyReleased
         slider_specA_jSlider.setValue(100);
        slider_specB_jSlider.setValue(100);
        remaining_peaks_specA_jTextField.setText(" ");
        remaining_peaks_specB_jTextField.setText(" ");
        for (Component c : filteringjTabbedPane.getComponents()) {
            c.setEnabled(true);
        }
        int selectedRow = scoreTable.getSelectedRow();
        String specAname = scoreTable.getValueAt(selectedRow, indSpecA).toString(),
                specBname = scoreTable.getValueAt(selectedRow, indSpecB).toString();
        try {
            prepareSpectraForPlotting(specAname, specBname);
        } catch (IOException ex) {
            Logger.getLogger(MainGUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MzMLUnmarshallerException ex) {
            Logger.getLogger(MainGUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MainGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        // plot graphics
        if (mirroredSpectrajRadioButton.isSelected()) {
            plotMirroredSpectra(original_spec_A, original_spec_B);
        } else {
            plotBubbleSpectra(original_spec_A, original_spec_B);
        }
    }//GEN-LAST:event_scoreTableKeyReleased

    public void addDataseries(MSnSpectrum spec, int specIndex) {
        int tmp_size = spec.getPeakList().size();
        double[][] tempXYZData = new double[3][tmp_size];
        ArrayList<Peak> peaks = new ArrayList<Peak>(spec.getPeakList());
        if (peaks == null) {
            JOptionPane.showMessageDialog(this, "Spectrum is null!", "Cannot plot", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (x.equals("m/z") && y.equals("intensity") && z.equals("default")) {
            for (int i = 0; i < peaks.size() - 1; i++) {
                tempXYZData[0][i] = peaks.get(i).mz;
                tempXYZData[1][i] = peaks.get(i).intensity;
                tempXYZData[2][i] = default_bubble_size_v1;
            }
        } else if (y.equals("m/z") && x.equals("intensity") && z.equals("default")) {
            for (int i = 0; i < peaks.size() - 1; i++) {
                tempXYZData[0][i] = peaks.get(i).intensity;
                tempXYZData[1][i] = peaks.get(i).mz;
                tempXYZData[2][i] = default_bubble_size_v2;
            }
        } else if (x.equals("m/z") && y.equals("default") && z.equals("intensity")) {
            tempXYZData = new double[3][tmp_size + 2];
            tempXYZData[0][0] = peaks.get(0).mz - 10;
            tempXYZData[1][0] = 1;
            tempXYZData[2][0] = 0;
            for (int i = 0; i < peaks.size() - 1; i++) {
                tempXYZData[0][i + 1] = peaks.get(i).mz;
                tempXYZData[1][i + 1] = 0;
                tempXYZData[2][i + 1] = (((double) peaks.get(i).intensity) / spec.getTotalIntensity());
            }
            tempXYZData[0][tmp_size + 1] = peaks.get(peaks.size() - 1).mz + 10;
            tempXYZData[1][tmp_size + 1] = -1;
            tempXYZData[2][tmp_size + 1] = 0;
        } else if (x.equals("intensity") && y.equals("default") && z.equals("m/z")) {
            tempXYZData = new double[3][tmp_size + 2];
            tempXYZData[2][0] = peaks.get(0).mz - 10;
            tempXYZData[1][0] = 2500;
            tempXYZData[0][0] = 0;
            for (int i = 0; i < peaks.size() - 1; i++) {
                tempXYZData[2][i + 1] = peaks.get(i).mz;
                tempXYZData[1][i + 1] = 0;
                tempXYZData[0][i + 1] = (((double) peaks.get(i).intensity) / spec.getTotalIntensity());
            }
            tempXYZData[2][tmp_size + 1] = peaks.get(peaks.size() - 1).mz + 10;
            tempXYZData[1][tmp_size + 1] = 0;
            tempXYZData[0][tmp_size + 1] = 0;
        } else if (x.equals("")) {
            JOptionPane.showMessageDialog(this, "x axis cannot be empty!", "Cannot plot", JOptionPane.INFORMATION_MESSAGE);
            return;
        } else if (x.equals("m/z") && y.equals("m/z")) {
            JOptionPane.showMessageDialog(this, "m/z cannot be plotted against m/z!", "Cannot plot", JOptionPane.INFORMATION_MESSAGE);
            return;
        } else if (x.equals("intensity") && y.equals("intensity")) {
            JOptionPane.showMessageDialog(this, "intensity cannot be plotted against intensity!", "Cannot plot", JOptionPane.INFORMATION_MESSAGE);
            return;
        } else {
            JOptionPane.showMessageDialog(this, "There are 4 plot options. z-axis cannot be the same as x-axis or y-axis.", "Cannot plot", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        xyzDataset.addSeries(specIndex, tempXYZData);
        specTitles[specIndex] = spec.getSpectrumTitle();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                MainGUI m;
                try {
                    m = new MainGUI();
                    m.setVisible(true);
                } catch (MzMLUnmarshallerException ex) {
                    Logger.getLogger(MainGUI.class.getName()).log(Level.SEVERE, null, ex);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(MainGUI.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(MainGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuBar FileMenuBar;
    private javax.swing.JButton auto_specA_jButton;
    private javax.swing.JButton auto_specB_jButton;
    private javax.swing.JPanel bubblePlotSettingsjPanel;
    private javax.swing.JRadioButton bubbleSpectrajRadioButton;
    private javax.swing.JMenuItem clearjMenuItem;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JTabbedPane filteringjTabbedPane;
    private javax.swing.JMenuItem loadSpecAFolderjMenuItem;
    private javax.swing.JMenuItem loadSpecBFolderjMenuItem;
    private javax.swing.JMenu menuMenu;
    private javax.swing.JRadioButton mirroredSpectrajRadioButton;
    private javax.swing.JMenuItem openScoreFileMenuItem;
    private javax.swing.JPanel plotjPanel;
    private javax.swing.JTextField remaining_peaks_specA_jTextField;
    private javax.swing.JTextField remaining_peaks_specB_jTextField;
    private javax.swing.JButton replotButton;
    private javax.swing.JMenuItem saveImagejMenuItem;
    private javax.swing.JTable scoreTable;
    private javax.swing.JScrollPane scoreTablejScrollPane;
    private javax.swing.JSlider slider_specA_jSlider;
    private javax.swing.JSlider slider_specB_jSlider;
    private javax.swing.JPanel specA_filtering_jPanel;
    private javax.swing.JLabel specA_remaining_percentage_jLabel;
    private javax.swing.JPanel specB_filtering_jPanel;
    private javax.swing.JLabel specB_remaining_percentage_jLabel;
    private javax.swing.JPanel spectrumjPanel;
    private javax.swing.JComboBox xAxisComboBox;
    private javax.swing.JLabel xAxisjLabel;
    private javax.swing.JComboBox yAxisComboBox;
    private javax.swing.JLabel yAxisjLabel;
    private javax.swing.JComboBox zAxisjComboBox;
    private javax.swing.JLabel zAxisjLabel;
    // End of variables declaration//GEN-END:variables

    private void plotBubbleSpectra(MSnSpectrum specA, MSnSpectrum specB) {
        addDataseries(specA, 0);
        addDataseries(specB, 1);
        chart = ChartFactory.createBubbleChart("Spectrum similarity", x, y, xyzDataset, PlotOrientation.VERTICAL, true, false, true);
        xyzDataset = new DefaultXYZDataset();
        // fine tune the chart properites
        XYPlot plot = chart.getXYPlot();
        XYItemRenderer xyitemrenderer = plot.getRenderer();
        xyitemrenderer.setSeriesPaint(0, Color.BLUE);
        xyitemrenderer.setSeriesPaint(1, Color.RED);
        plot.getDomainAxis().setLowerBound(0);
        plot.getDomainAxis().setUpperBound(plot.getDomainAxis().getUpperBound() + 100);
        // remove space before/after the domain axis
        plot.getDomainAxis().setUpperMargin(0);
        plot.getDomainAxis().setLowerMargin(0);
        plot.setRangeGridlinePaint(Color.black);
        // make semi see through
        plot.setForegroundAlpha(0.5f);
        plot.setFixedLegendItems(new LegendItemCollection());
        // set background color
        chart.getPlot().setBackgroundPaint(Color.getHSBColor(10, 0, 8));
        chart.setBackgroundPaint(Color.WHITE);
        chartPanel.setChart(chart);
        chartPanel.setBackground(Color.WHITE);

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;

        if (spectrumPanel != null) {
            spectrumjPanel.remove(spectrumPanel);
        }
        spectrumjPanel.add(chartPanel, gridBagConstraints);
        chartPanel.repaint();
        spectrumjPanel.revalidate();
        spectrumjPanel.repaint();
        // set axis information
        xAxisComboBox.setSelectedItem(x);
        yAxisComboBox.setSelectedItem(y);
        zAxisjComboBox.setSelectedItem(z);
    }

    private void prepareSpectraForPlotting(String specAname, String specBname) throws IOException, MzMLUnmarshallerException, FileNotFoundException, ClassNotFoundException {
        boolean isSpecAfound = false,
                isSpecBfound = false;
        // prepare file and spectrum names     
        for (File f : new File(specAFolder).listFiles()) {
            if (f.getName().endsWith(".mgf")) {
                spFct.addSpectra(f);
                for (String title : spFct.getSpectrumTitles(f.getName())) {
                    if (title.equals(specAname)) {
                        System.out.println("specA=" + specAname + "\t" + f.getName());
                        original_spec_A = (MSnSpectrum) spFct.getSpectrum(f.getName(), title);
                    }
                }
            }
        }
        for (File f : new File(specBFolder).listFiles()) {
            if (f.getName().endsWith(".mgf")) {
                spFct.addSpectra(f);
                for (String title : spFct.getSpectrumTitles(f.getName())) {
                    if (title.equals(specBname)) {
                        System.out.println("specB=" + specBname + "\t" + f.getName());
                        original_spec_B = (MSnSpectrum) spFct.getSpectrum(f.getName(), title);
                    }
                }
            }
        }
        tmp_spec_A = new MSnSpectrum(2, original_spec_A.getPrecursor(), original_spec_A.getSpectrumTitle(), original_spec_A.getPeakMap(), original_spec_A.getFileName(), original_spec_A.getScanStartTime());
        tmp_spec_B = new MSnSpectrum(2, original_spec_B.getPrecursor(), original_spec_B.getSpectrumTitle(), original_spec_B.getPeakMap(), original_spec_B.getFileName(), original_spec_B.getScanStartTime());

        if (original_spec_A != null) {
            isSpecAfound = true;
        }
        if (original_spec_B != null) {
            isSpecBfound = true;
        }
        if (isSpecAfound == false && isSpecBfound != false) {
            JOptionPane.showMessageDialog(this, "SpecA cannot be found on the selected folder!", "Input file error", JOptionPane.ERROR_MESSAGE);
        }
        if (isSpecAfound != false && isSpecBfound == false) {
            JOptionPane.showMessageDialog(this, "SpecB cannot be found on the selected folder!", "Input file error", JOptionPane.ERROR_MESSAGE);
        }
        if (isSpecAfound == false && isSpecBfound == false) {
            JOptionPane.showMessageDialog(this, "SpecA and SpecB cannot be found on the selected folder!", "Input file error", JOptionPane.ERROR_MESSAGE);
        }
        spFct.clearFactory();
    }

    private void plotMirroredSpectra(MSnSpectrum spectrum_A, MSnSpectrum spectrum_B) {
        double[] specA_intensity = spectrum_A.getIntensityValuesAsArray(),
                specA_mz = spectrum_A.getMzValuesAsArray(),
                specB_intensity = spectrum_B.getIntensityValuesAsArray(),
                specB_mz = spectrum_B.getMzValuesAsArray();
        double specA_precursor_mz = spectrum_A.getPrecursor().getMz(),
                specA_precursor_charge = spectrum_A.getPrecursor().getPossibleCharges().get(0).value,
                specB_precursor_mz = spectrum_B.getPrecursor().getMz(),
                specB_precursor_charge = spectrum_B.getPrecursor().getPossibleCharges().get(0).value;
        String specA_fileName = spectrum_A.getFileName(),
                specB_fileName = spectrum_B.getFileName();

        spectrumPanel = new SpectrumPanel(
                specA_mz, specA_intensity,
                specA_precursor_mz, "" + specA_precursor_charge,
                "" + specA_fileName,
                50, false, false, false, 2, false);

        // spectrumPanel.setaSpectrumPeakColor(Color.BLUE);
        spectrumPanel.addMirroredSpectrum(
                specB_mz, specB_intensity,
                specB_precursor_mz, "" + specB_precursor_charge,
                "" + specB_fileName,
                false, Color.BLUE, Color.BLUE);

        spectrumPanel.setBackground(Color.getHSBColor(10, 0, 8));

        spectrumjPanel.remove(chartPanel);
        spectrumjPanel.removeAll();
        spectrumjPanel.repaint();
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        spectrumjPanel.add(spectrumPanel, gridBagConstraints);
        spectrumjPanel.revalidate();
        spectrumjPanel.repaint();
    }

    private MSnSpectrum create_tmp_ms(MSnSpectrum ms) {
        // Fill all variables to tmp_ms
        MSnSpectrum tmp_ms = new MSnSpectrum();
        tmp_ms.setPrecursor(ms.getPrecursor());
        tmp_ms.setScanNumber(ms.getScanNumber());
        tmp_ms.setScanStartTime(ms.getScanStartTime());
        tmp_ms.setSpectrumTitle(ms.getSpectrumTitle());
        tmp_ms.setPeakList(ms.getPeakMap());
        return tmp_ms;
    }
}
