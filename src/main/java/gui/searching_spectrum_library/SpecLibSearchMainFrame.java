package gui.searching_spectrum_library;

import gui.scoring_pipeline.MainFrame;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * The GUI main frame for spectrum library searching mode.
 *
 * @author Niels Hulstaert/Sule Yilmaz
 */
public class SpecLibSearchMainFrame extends MainFrame {

    /**
     * Constructor.
     */
    public SpecLibSearchMainFrame() {
        super(false); // to make sure that super class of MainFrame will not be called 
        this.initComponents();
    }

    @Override
    public JFileChooser getSpectraDirectoryChooser() {
        return spectraDirectoryChooser;
    }

    @Override
    public JFileChooser getComparisonSpectraDirectoryChooser() {
        return comparisonSpectraDirectoryChooser;
    }

    @Override
    public JFileChooser getOutputDirectoryChooser() {
        return outputDirectoryChooser;
    }

    @Override
    public JButton getComparisonSpectraDirectoryButton() {
        return comparisonSpectraDirectoryButton;
    }

    @Override
    public JTextField getComparisonSpectraDirectoryTextField() {
        return comparisonSpectraDirectoryTextField;
    }

    @Override
    public JButton getOutputDirectoryButton() {
        return outputDirectoryButton;
    }

    @Override
    public JTextField getOutputDirectoryTextField() {
        return outputDirectoryTextField;
    }

    @Override
    public JButton getSpectraDirectoryButton() {
        return spectraDirectoryButton;
    }

    @Override
    public JTextField getSpectraDirectoryTextField() {
        return spectraDirectoryTextField;
    }

    @Override
    public JCheckBox getChargeCheckBox() {
        return chargeCheckBox;
    }

    @Override
    public JTextField getFragmentLabelToleranceLabel() {
        return fragmentToleranceTextField;
    }

    @Override
    public JComboBox<String> getNoiseFilterComboBox() {
        return noiseFilterComboBox;
    }

    @Override
    public JTextField getNumberOfPeaksCutoffTextField() {
        return numberOfPeaksCutoffTextField;
    }

    @Override
    public JTextField getNumberOfThreadsTextField() {
        return numberOfThreadsTextField;
    }

    @Override
    public JTextField getPeakIntensityCutoffTextField() {
        return peakIntensityCutoffTextField;
    }

    @Override
    public JTextField getPrecursorToleranceTextField() {
        return precursorToleranceTextField;
    }

    @Override
    public JTextField getFragmentToleranceTextField() {
        return fragmentToleranceTextField;
    }

    @Override
    public JComboBox<String> getPreprocessingOrderComboBox() {
        return preprocessingOrderComboBox;
    }

    @Override
    public JCheckBox getRemovePrecursorIonPeaksCheckBox() {
        return removePrecursorIonPeaksCheckBox;
    }

    @Override
    public JComboBox<String> getTransformationComboBox() {
        return transformationComboBox;
    }

    @Override
    public JButton getCloseButton() {
        return closeButton;
    }

    @Override
    public JButton getRunButton() {
        return runButton;
    }

    @Override
    public void setNoiseFilterComboBox(JComboBox<String> noiseFilterComboBox) {
        this.noiseFilterComboBox = noiseFilterComboBox;
    }

    @Override
    public void setPreprocessingOrderComboBox(JComboBox<String> preprocessingOrderComboBox) {
        this.preprocessingOrderComboBox = preprocessingOrderComboBox;
    }

    @Override
    public void setTransformationComboBox(JComboBox<String> transformationComboBox) {
        this.transformationComboBox = transformationComboBox;
    }

    @Override
    public JTextField getMaxPrecursorChargejTextField() {
        return maxPrecursorChargejTextField;
    }

    @Override
    public void setMaxPrecursorChargejTextField(JTextField maxPrecursorChargejTextField) {
        this.maxPrecursorChargejTextField = maxPrecursorChargejTextField;
    }

    public JComboBox<String> getWeightingjComboBox() {
        return weightingjComboBox;
    }

    public void setWeightingjComboBox(JComboBox<String> weightingjComboBox) {
        this.weightingjComboBox = weightingjComboBox;
    }
    
    public JComboBox<String> getScoringFunctionjComboBox() {
        return scoringFunctionjComboBox;
    }

    public void setScoringFunctionjComboBox(JComboBox<String> scoringFunctionjComboBox) {
        this.scoringFunctionjComboBox = scoringFunctionjComboBox;
    }

    public JLabel getScoringFunctionjLabel() {
        return scoringFunctionjLabel;
    }

    public void setScoringFunctionjLabel(JLabel scoringFunctionjLabel) {
        this.scoringFunctionjLabel = scoringFunctionjLabel;
    }

    public JLabel getMaxMZjLabel() {
        return maxMZjLabel;
    }

    public void setMaxMZjLabel(JLabel maxMZjLabel) {
        this.maxMZjLabel = maxMZjLabel;
    }

    public JTextField getMaxMZjTextField() {
        return maxMZjTextField;
    }

    public void setMaxMZjTextField(JTextField maxMZjTextField) {
        this.maxMZjTextField = maxMZjTextField;
    }

    public JLabel getMinMZjLabel() {
        return minMZjLabel;
    }

    public void setMinMZjLabel(JLabel minMZjLabel) {
        this.minMZjLabel = minMZjLabel;
    }

    public JTextField getMinMZjTextField() {
        return minMZjTextField;
    }

    public void setMinMZjTextField(JTextField minMZjTextField) {
        this.minMZjTextField = minMZjTextField;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        inputAndOutputPanel = new javax.swing.JPanel();
        spectraDirectoryLabel = new javax.swing.JLabel();
        spectraDirectoryTextField = new javax.swing.JTextField();
        spectraDirectoryButton = new javax.swing.JButton();
        spectraToCompareDirectoryLabel = new javax.swing.JLabel();
        comparisonSpectraDirectoryTextField = new javax.swing.JTextField();
        comparisonSpectraDirectoryButton = new javax.swing.JButton();
        outputDirectoryLabel = new javax.swing.JLabel();
        outputDirectoryTextField = new javax.swing.JTextField();
        outputDirectoryButton = new javax.swing.JButton();
        pipelineParametersPanel = new javax.swing.JPanel();
        chargeCheckBox = new javax.swing.JCheckBox();
        precursorLabelToleranceLabel = new javax.swing.JLabel();
        precursorToleranceTextField = new javax.swing.JTextField();
        fragmentToleranceLabel = new javax.swing.JLabel();
        fragmentToleranceTextField = new javax.swing.JTextField();
        preprocessingParametersPanel = new javax.swing.JPanel();
        noiseFilterLabel = new javax.swing.JLabel();
        peakIntensityCutoffTextField = new javax.swing.JTextField();
        removePrecursorIonPeaksCheckBox = new javax.swing.JCheckBox();
        numberOfPeaksCutoffTextField = new javax.swing.JTextField();
        transformationJLabel = new javax.swing.JLabel();
        noiseFilterComboBox = new javax.swing.JComboBox<>();
        transformationComboBox = new javax.swing.JComboBox<>();
        numberOfPeaksCutoffLabel = new javax.swing.JLabel();
        peakIntensityCutoffLabel = new javax.swing.JLabel();
        preprocessingOrderLabel = new javax.swing.JLabel();
        preprocessingOrderComboBox = new javax.swing.JComboBox<>();
        scoringFunctionjLabel = new javax.swing.JLabel();
        scoringFunctionjComboBox = new javax.swing.JComboBox<>();
        minMZjLabel = new javax.swing.JLabel();
        minMZjTextField = new javax.swing.JTextField();
        maxMZjLabel = new javax.swing.JLabel();
        maxMZjTextField = new javax.swing.JTextField();
        maxPrecChargejLabel = new javax.swing.JLabel();
        maxPrecursorChargejTextField = new javax.swing.JTextField();
        weightingjLabel = new javax.swing.JLabel();
        weightingjComboBox = new javax.swing.JComboBox<>();
        otherParametersPanel = new javax.swing.JPanel();
        numberOfThreadsLabel = new javax.swing.JLabel();
        numberOfThreadsTextField = new javax.swing.JTextField();
        closeButton = new javax.swing.JButton();
        runButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        mainPanel.setBackground(new java.awt.Color(255, 255, 255));
        mainPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Spectrum library searching tool "));
        mainPanel.setPreferredSize(new java.awt.Dimension(900, 700));

        inputAndOutputPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Input & Output"));
        inputAndOutputPanel.setOpaque(false);

        spectraDirectoryLabel.setText("Select the spectra directory*:");
        spectraDirectoryLabel.setToolTipText("give a folder that contains spectra");

        spectraDirectoryButton.setText("browse...");

        spectraToCompareDirectoryLabel.setText("Select the comparison spectra directory*:");
        spectraToCompareDirectoryLabel.setToolTipText("give a folder that contains spectra on the comparison data set");

        comparisonSpectraDirectoryButton.setText("browse...");

        outputDirectoryLabel.setText("Select the output directory*:");
        outputDirectoryLabel.setToolTipText("give a folder where an output text file will be generated after the comparison");

        outputDirectoryButton.setText("browse...");

        javax.swing.GroupLayout inputAndOutputPanelLayout = new javax.swing.GroupLayout(inputAndOutputPanel);
        inputAndOutputPanel.setLayout(inputAndOutputPanelLayout);
        inputAndOutputPanelLayout.setHorizontalGroup(
            inputAndOutputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(inputAndOutputPanelLayout.createSequentialGroup()
                .addGroup(inputAndOutputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(inputAndOutputPanelLayout.createSequentialGroup()
                        .addGroup(inputAndOutputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(spectraToCompareDirectoryLabel)
                            .addComponent(outputDirectoryLabel)
                            .addComponent(spectraDirectoryLabel))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(inputAndOutputPanelLayout.createSequentialGroup()
                        .addGroup(inputAndOutputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(outputDirectoryTextField, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(comparisonSpectraDirectoryTextField)
                            .addComponent(spectraDirectoryTextField, javax.swing.GroupLayout.Alignment.LEADING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(inputAndOutputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(spectraDirectoryButton, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(comparisonSpectraDirectoryButton, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(outputDirectoryButton, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );
        inputAndOutputPanelLayout.setVerticalGroup(
            inputAndOutputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(inputAndOutputPanelLayout.createSequentialGroup()
                .addComponent(spectraDirectoryLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(inputAndOutputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spectraDirectoryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spectraDirectoryButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spectraToCompareDirectoryLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(inputAndOutputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comparisonSpectraDirectoryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comparisonSpectraDirectoryButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(outputDirectoryLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(inputAndOutputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(outputDirectoryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(outputDirectoryButton)))
        );

        pipelineParametersPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Scoring parameters"));
        pipelineParametersPanel.setOpaque(false);

        chargeCheckBox.setSelected(true);
        chargeCheckBox.setText("compare spectra regarding to precursor charge");
        chargeCheckBox.setToolTipText("enables the comparison of spectra only with the same precursor charge");

        precursorLabelToleranceLabel.setText("Precursor tolerance (ppm)*:");
        precursorLabelToleranceLabel.setToolTipText("provide a double value for a precursor tolerance");

        precursorToleranceTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        fragmentToleranceLabel.setText("Fragment tolerance (Da)*:");
        fragmentToleranceLabel.setToolTipText("provide a double value for a fragmentation tolerance");

        fragmentToleranceTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        fragmentToleranceTextField.setToolTipText("");

        preprocessingParametersPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Preprocessing"));
        preprocessingParametersPanel.setOpaque(false);

        noiseFilterLabel.setText("Noise filter*:");
        noiseFilterLabel.setToolTipText("select the noise filtering option to remove peaks");

        removePrecursorIonPeaksCheckBox.setText("Remove precursor ion peaks");

        transformationJLabel.setText("Transformation*:");
        transformationJLabel.setToolTipText("select a transformation option to transform peak intensity");

        noiseFilterComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "none", "prideAsap-Adaptive noise filtering", "topN intense peak selection", "discard peaks with less than x% of precursor-intensity" }));

        transformationComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "none", "log2", "square root" }));

        numberOfPeaksCutoffLabel.setText("Number of peaks cutoff:");
        numberOfPeaksCutoffLabel.setToolTipText("required for topN intense peak selection");

        peakIntensityCutoffLabel.setText("Peak intensity cutoff (%):");
        peakIntensityCutoffLabel.setToolTipText("required for discarding peaks with less than x% of precursor intensity");

        preprocessingOrderLabel.setText("Preprocessing order*:");
        preprocessingOrderLabel.setToolTipText("select the order for preprocessing: either first noise filtering or intensity transformation ");

        preprocessingOrderComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "noise filter - transformation", "transformation - noise filter" }));

        javax.swing.GroupLayout preprocessingParametersPanelLayout = new javax.swing.GroupLayout(preprocessingParametersPanel);
        preprocessingParametersPanel.setLayout(preprocessingParametersPanelLayout);
        preprocessingParametersPanelLayout.setHorizontalGroup(
            preprocessingParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(preprocessingParametersPanelLayout.createSequentialGroup()
                .addGroup(preprocessingParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(preprocessingParametersPanelLayout.createSequentialGroup()
                        .addComponent(noiseFilterLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(noiseFilterComboBox, 0, 1, Short.MAX_VALUE))
                    .addGroup(preprocessingParametersPanelLayout.createSequentialGroup()
                        .addGroup(preprocessingParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(preprocessingParametersPanelLayout.createSequentialGroup()
                                .addGroup(preprocessingParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(numberOfPeaksCutoffLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(peakIntensityCutoffLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(preprocessingParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(numberOfPeaksCutoffTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(peakIntensityCutoffTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(removePrecursorIonPeaksCheckBox))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(preprocessingParametersPanelLayout.createSequentialGroup()
                        .addGroup(preprocessingParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(transformationJLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(preprocessingOrderLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE))
                        .addGap(6, 6, 6)
                        .addGroup(preprocessingParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(preprocessingOrderComboBox, 0, 273, Short.MAX_VALUE)
                            .addComponent(transformationComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );

        preprocessingParametersPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {numberOfPeaksCutoffTextField, peakIntensityCutoffTextField});

        preprocessingParametersPanelLayout.setVerticalGroup(
            preprocessingParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(preprocessingParametersPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(preprocessingParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(preprocessingOrderLabel)
                    .addComponent(preprocessingOrderComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(preprocessingParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(transformationJLabel)
                    .addComponent(transformationComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(preprocessingParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(noiseFilterLabel)
                    .addComponent(noiseFilterComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(preprocessingParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(numberOfPeaksCutoffTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(numberOfPeaksCutoffLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(preprocessingParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(peakIntensityCutoffLabel)
                    .addComponent(peakIntensityCutoffTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addComponent(removePrecursorIonPeaksCheckBox))
        );

        preprocessingParametersPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {numberOfPeaksCutoffTextField, peakIntensityCutoffTextField});

        scoringFunctionjLabel.setText("Scoring function:*");
        scoringFunctionjLabel.setToolTipText("select a scoring function of your interest");

        scoringFunctionjComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Cum. Binom.", "Dot-product", "Pearson's", "Spearman's" }));

        minMZjLabel.setText("Minimum m/z value:");
        minMZjLabel.setToolTipText("provide a minimum m/z value, which is required to bin spectra");

        minMZjTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        minMZjTextField.setText("100");
        minMZjTextField.setPreferredSize(new java.awt.Dimension(6, 20));
        minMZjTextField.setSelectionEnd(1);
        minMZjTextField.setSelectionStart(1);

        maxMZjLabel.setText("Maximum m/z value:");
        maxMZjLabel.setToolTipText("provide a maximum m/z value, which is required to bin spectra");

        maxMZjTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        maxMZjTextField.setText("3500");
        maxMZjTextField.setPreferredSize(new java.awt.Dimension(6, 20));
        maxMZjTextField.setSelectionEnd(0);
        maxMZjTextField.setSelectionStart(0);
        maxMZjTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maxMZjTextFieldActionPerformed(evt);
            }
        });

        maxPrecChargejLabel.setText("Maximum precursor charge:");
        maxPrecChargejLabel.setToolTipText("provide an integer value for the maximum precursor chage value to score");

        maxPrecursorChargejTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        maxPrecursorChargejTextField.setText("5");

        weightingjLabel.setText("Weight option to bin spectra:*");
        weightingjLabel.setToolTipText("select an option to how to convert MS/MS spectrum into a binned MS/MS spectrum");

        weightingjComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Sum", "Mean", "Median" }));

        javax.swing.GroupLayout pipelineParametersPanelLayout = new javax.swing.GroupLayout(pipelineParametersPanel);
        pipelineParametersPanel.setLayout(pipelineParametersPanelLayout);
        pipelineParametersPanelLayout.setHorizontalGroup(
            pipelineParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pipelineParametersPanelLayout.createSequentialGroup()
                .addGroup(pipelineParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pipelineParametersPanelLayout.createSequentialGroup()
                        .addComponent(chargeCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(pipelineParametersPanelLayout.createSequentialGroup()
                        .addGroup(pipelineParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(maxPrecChargejLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(precursorLabelToleranceLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
                            .addComponent(fragmentToleranceLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(minMZjLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(maxMZjLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(pipelineParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(minMZjTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(fragmentToleranceTextField, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(precursorToleranceTextField, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(maxPrecursorChargejTextField, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(maxMZjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(23, 23, 23))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pipelineParametersPanelLayout.createSequentialGroup()
                        .addGroup(pipelineParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(pipelineParametersPanelLayout.createSequentialGroup()
                                .addComponent(weightingjLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 84, Short.MAX_VALUE)
                                .addComponent(weightingjComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pipelineParametersPanelLayout.createSequentialGroup()
                                .addComponent(scoringFunctionjLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(scoringFunctionjComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)))
                .addComponent(preprocessingParametersPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );

        pipelineParametersPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {fragmentToleranceLabel, maxMZjLabel, maxPrecChargejLabel, minMZjLabel, precursorLabelToleranceLabel});

        pipelineParametersPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {scoringFunctionjComboBox, weightingjComboBox});

        pipelineParametersPanelLayout.setVerticalGroup(
            pipelineParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pipelineParametersPanelLayout.createSequentialGroup()
                .addGroup(pipelineParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pipelineParametersPanelLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(pipelineParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(scoringFunctionjLabel)
                            .addComponent(scoringFunctionjComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pipelineParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(weightingjComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(weightingjLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(chargeCheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pipelineParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(maxPrecChargejLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(maxPrecursorChargejTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pipelineParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(precursorLabelToleranceLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(precursorToleranceTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pipelineParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(fragmentToleranceTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(fragmentToleranceLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pipelineParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(minMZjLabel)
                            .addComponent(minMZjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pipelineParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(maxMZjLabel)
                            .addComponent(maxMZjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(preprocessingParametersPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pipelineParametersPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {fragmentToleranceTextField, maxMZjTextField, maxPrecursorChargejTextField, minMZjTextField, precursorToleranceTextField});

        pipelineParametersPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {fragmentToleranceLabel, maxMZjLabel, maxPrecChargejLabel, minMZjLabel, precursorLabelToleranceLabel});

        pipelineParametersPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {scoringFunctionjComboBox, weightingjComboBox});

        otherParametersPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Other"));
        otherParametersPanel.setOpaque(false);

        numberOfThreadsLabel.setText("Number of threads*:");
        numberOfThreadsLabel.setToolTipText("required for multithreading");
        numberOfThreadsLabel.setPreferredSize(new java.awt.Dimension(176, 15));

        numberOfThreadsTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        numberOfThreadsTextField.setText("2");

        javax.swing.GroupLayout otherParametersPanelLayout = new javax.swing.GroupLayout(otherParametersPanel);
        otherParametersPanel.setLayout(otherParametersPanelLayout);
        otherParametersPanelLayout.setHorizontalGroup(
            otherParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(otherParametersPanelLayout.createSequentialGroup()
                .addComponent(numberOfThreadsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(69, 69, 69)
                .addComponent(numberOfThreadsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        otherParametersPanelLayout.setVerticalGroup(
            otherParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(otherParametersPanelLayout.createSequentialGroup()
                .addGroup(otherParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(numberOfThreadsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(numberOfThreadsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 9, Short.MAX_VALUE))
        );

        closeButton.setText("exit");
        closeButton.setToolTipText("exit the program ");
        closeButton.setMaximumSize(new java.awt.Dimension(85, 27));
        closeButton.setMinimumSize(new java.awt.Dimension(85, 27));
        closeButton.setPreferredSize(new java.awt.Dimension(85, 27));
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        runButton.setText("run");
        runButton.setToolTipText("run the pipeline with the given parameter values");
        runButton.setMaximumSize(new java.awt.Dimension(85, 27));
        runButton.setMinimumSize(new java.awt.Dimension(85, 27));
        runButton.setPreferredSize(new java.awt.Dimension(85, 27));

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(runButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(otherParametersPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pipelineParametersPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(inputAndOutputPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(87, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(inputAndOutputPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pipelineParametersPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(otherParametersPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(runButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(150, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 975, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 10, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        mainPanel.getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        System.exit(0);
    }//GEN-LAST:event_closeButtonActionPerformed

    private void maxMZjTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maxMZjTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_maxMZjTextFieldActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chargeCheckBox;
    private javax.swing.JButton closeButton;
    private javax.swing.JButton comparisonSpectraDirectoryButton;
    private javax.swing.JTextField comparisonSpectraDirectoryTextField;
    private javax.swing.JLabel fragmentToleranceLabel;
    private javax.swing.JTextField fragmentToleranceTextField;
    private javax.swing.JPanel inputAndOutputPanel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JLabel maxMZjLabel;
    private javax.swing.JTextField maxMZjTextField;
    private javax.swing.JLabel maxPrecChargejLabel;
    private javax.swing.JTextField maxPrecursorChargejTextField;
    private javax.swing.JLabel minMZjLabel;
    private javax.swing.JTextField minMZjTextField;
    private javax.swing.JComboBox<String> noiseFilterComboBox;
    private javax.swing.JLabel noiseFilterLabel;
    private javax.swing.JLabel numberOfPeaksCutoffLabel;
    private javax.swing.JTextField numberOfPeaksCutoffTextField;
    private javax.swing.JLabel numberOfThreadsLabel;
    private javax.swing.JTextField numberOfThreadsTextField;
    private javax.swing.JPanel otherParametersPanel;
    private javax.swing.JButton outputDirectoryButton;
    private javax.swing.JLabel outputDirectoryLabel;
    private javax.swing.JTextField outputDirectoryTextField;
    private javax.swing.JLabel peakIntensityCutoffLabel;
    private javax.swing.JTextField peakIntensityCutoffTextField;
    private javax.swing.JPanel pipelineParametersPanel;
    private javax.swing.JLabel precursorLabelToleranceLabel;
    private javax.swing.JTextField precursorToleranceTextField;
    private javax.swing.JComboBox<String> preprocessingOrderComboBox;
    private javax.swing.JLabel preprocessingOrderLabel;
    private javax.swing.JPanel preprocessingParametersPanel;
    private javax.swing.JCheckBox removePrecursorIonPeaksCheckBox;
    private javax.swing.JButton runButton;
    private javax.swing.JComboBox<String> scoringFunctionjComboBox;
    private javax.swing.JLabel scoringFunctionjLabel;
    private javax.swing.JButton spectraDirectoryButton;
    private javax.swing.JLabel spectraDirectoryLabel;
    private javax.swing.JTextField spectraDirectoryTextField;
    private javax.swing.JLabel spectraToCompareDirectoryLabel;
    private javax.swing.JComboBox<String> transformationComboBox;
    private javax.swing.JLabel transformationJLabel;
    private javax.swing.JComboBox<String> weightingjComboBox;
    private javax.swing.JLabel weightingjLabel;
    // End of variables declaration//GEN-END:variables
}
