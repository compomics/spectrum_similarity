package gui;

import config.ConfigHolder;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import main.ScorePipeline;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;

/**
 * This class is main controller for the graphical user interface (GUI).
 *
 * @author Niels Hulstaert
 */
public class MainController {

    private static final Logger LOGGER = Logger.getLogger(MainController.class);

    /**
     * The parameters properties names.
     */
    private static final String SPECTRA_PROP = "spectra.folder";
    private static final String COMP_SPECTRA_PROP = "spectra.to.compare.folder";
    private static final String OUTPUT_PROP = "output.folder";
    private static final String CHARGE_PROP = "is.charged.based";
    private static final String PRECURSOR_PROP = "precursor.tolerance";
    private static final String FRAGMENT_PROP = "fragment.tolerance";
    private static final String MIN_MZ_PROP = "min.mz";
    private static final String MAX_MZ_PROP = "max.mz";
    private static final String NEIGHBOUR_SLICE_PROP = "calculate.only5";
    private static final String FILE_NAME_SLICE_INDEX_PROP = "slice.index";
    private static final String BIN_WEIGHTING_PROP = "sum_mean_median";
    private static final String TRANSORMATION_PROP = "transformation";
    private static final String NOISE_FILTER_PROP = "noise.filtering";
    private static final String NUMBER_OF_PEAKS_CUTOFF_PROP = "topN";
    private static final String PEAK_PERCENTAGE_CUTOFF = "percent";
    private static final String PREPROCESSING_ORDER_PROP = "isNFTR";
    private static final String PRECURSOR_PEAK_REMOVAL_PROP = "precursor.peak.removal";
    private static final String NUMBER_OF_THREADS_PROP = "thread.numbers";

    /**
     * Model fields.
     */
    private ScorePipelineSwingWorker scorePipelineSwingWorker;

    /**
     * The views of this controller.
     */
    private final MainFrame mainFrame = new MainFrame();
    private RunDialog runDialog;

    /**
     * Init the controller.
     */
    public void init() {
        mainFrame.setTitle("Spectrum similarity score pipeline " + ConfigHolder.getInstance().getString("score.pipeline.version", ""));

        runDialog = new RunDialog(mainFrame, true);
        runDialog.getLogTextArea().setText("..." + System.lineSeparator());

        //get the appender for setting the text area
        LogTextAreaAppender logTextAreaAppender = (LogTextAreaAppender) Logger.getLogger("gui").getAppender("gui");
        logTextAreaAppender.setRunDialog(runDialog);

        //disable the necessary text fields
        mainFrame.getFileNameSliceIndexTextField().setEnabled(false);
        mainFrame.getNumberOfPeaksCutoffTextField().setEnabled(false);
        mainFrame.getPeakIntensityCutoffTextField().setEnabled(false);

        //init file choosers
        //disable select multiple files
        mainFrame.getSpectraDirectoryChooser().setMultiSelectionEnabled(false);
        mainFrame.getComparisonSpectraDirectoryChooser().setMultiSelectionEnabled(false);
        mainFrame.getOutputDirectoryChooser().setMultiSelectionEnabled(false);
        //set select directories only
        mainFrame.getSpectraDirectoryChooser().setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        mainFrame.getComparisonSpectraDirectoryChooser().setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        mainFrame.getOutputDirectoryChooser().setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        //add action listeners
        mainFrame.getSpectraDirectoryButton().addActionListener(e -> {
            //in response to the button click, show open dialog
            int returnVal = mainFrame.getSpectraDirectoryChooser().showOpenDialog(mainFrame);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                //show spectra directory path in text field
                mainFrame.getSpectraDirectoryTextField().setText(mainFrame.getSpectraDirectoryChooser().getSelectedFile().getAbsolutePath());
            }
        });

        mainFrame.getComparisonSpectraDirectoryButton().addActionListener(e -> {
            //in response to the button click, show open dialog
            int returnVal = mainFrame.getComparisonSpectraDirectoryChooser().showOpenDialog(mainFrame);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                //show spectra directory path in text field
                mainFrame.getComparisonSpectraDirectoryTextField().setText(mainFrame.getComparisonSpectraDirectoryChooser().getSelectedFile().getAbsolutePath());
            }
        });

        mainFrame.getOutputDirectoryButton().addActionListener(e -> {
            //in response to the button click, show open dialog
            int returnVal = mainFrame.getOutputDirectoryChooser().showOpenDialog(mainFrame);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                //show spectra directory path in text field
                mainFrame.getOutputDirectoryTextField().setText(mainFrame.getOutputDirectoryChooser().getSelectedFile().getAbsolutePath());
            }
        });

        mainFrame.getNeighbourSlicesOnlyCheckBox().addItemListener(e -> {
            if (mainFrame.getNeighbourSlicesOnlyCheckBox().isSelected()) {
                mainFrame.getFileNameSliceIndexTextField().setEnabled(true);
            } else {
                mainFrame.getFileNameSliceIndexTextField().setEnabled(false);
            }
        });

        mainFrame.getNoiseFilterComboBox().addActionListener(e -> {
            switch (mainFrame.getNoiseFilterComboBox().getSelectedIndex()) {
                case 2:
                    mainFrame.getNumberOfPeaksCutoffTextField().setEnabled(true);
                    mainFrame.getPeakIntensityCutoffTextField().setEnabled(false);
                    break;
                case 3:
                    mainFrame.getNumberOfPeaksCutoffTextField().setEnabled(false);
                    mainFrame.getPeakIntensityCutoffTextField().setEnabled(true);
                    break;
                default:
                    if (mainFrame.getNumberOfPeaksCutoffTextField().isEnabled()) {
                        mainFrame.getNumberOfPeaksCutoffTextField().setEnabled(false);
                    }
                    if (mainFrame.getPeakIntensityCutoffTextField().isEnabled()) {
                        mainFrame.getPeakIntensityCutoffTextField().setEnabled(false);
                    }
                    break;
            }
        });

        mainFrame.getRunButton().addActionListener(e -> {
            //validate input
            List<String> validationMessages = validateInput();
            if (!validationMessages.isEmpty()) {
                String message = validationMessages.stream().collect(Collectors.joining(System.lineSeparator()));
                showMessageDialog("Validation errors", message, JOptionPane.WARNING_MESSAGE);
            } else {
                //copy the parameter values to the ConfigHolder
                copyParameterValues();
                int reply = JOptionPane.showConfirmDialog(mainFrame, "Save the current settings for future usage?"
                        + System.lineSeparator() + "Otherwise the settings will be used for this run only.", "Save settings", JOptionPane.INFORMATION_MESSAGE);
                if (reply == JOptionPane.YES_OPTION) {
                    try {
                        ConfigHolder.getInstance().save();
                    } catch (ConfigurationException ce) {
                        showMessageDialog("Save problem", "The settings could not be saved."
                                + System.lineSeparator()
                                + ce.getMessage(), JOptionPane.ERROR_MESSAGE);
                    }
                }
                if (reply != JOptionPane.CANCEL_OPTION) {
                    scorePipelineSwingWorker = new ScorePipelineSwingWorker();
                    scorePipelineSwingWorker.execute();

                    //show the run dialog
                    centerRunDialog();
                    runDialog.setVisible(true);
                }
            }
        });

        mainFrame.getCloseButton().addActionListener(e -> {
            mainFrame.dispose();
            System.exit(0);
        });

        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent we) {
                System.exit(0);
            }
        });

        runDialog.getClearButton().addActionListener(e -> {
            runDialog.getLogTextArea().setText("..." + System.lineSeparator());
        });

        runDialog.getCancelButton().addActionListener(e -> {
            scorePipelineSwingWorker.cancel(true);
        });

        //load the parameters from the properties file
        loadParameterValues();
    }

    /**
     * Show the view of this controller.
     */
    public void showView() {
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    /**
     * Load the parameter values from the properties file and set them in the
     * matching fields.
     */
    private void loadParameterValues() {
        mainFrame.getSpectraDirectoryTextField().setText(ConfigHolder.getInstance().getString(SPECTRA_PROP));
        mainFrame.getComparisonSpectraDirectoryTextField().setText(ConfigHolder.getInstance().getString(COMP_SPECTRA_PROP));
        mainFrame.getOutputDirectoryTextField().setText(ConfigHolder.getInstance().getString(OUTPUT_PROP));
        mainFrame.getChargeCheckBox().setSelected(ConfigHolder.getInstance().getBoolean(CHARGE_PROP));
        mainFrame.getPrecursorToleranceTextField().setText(Double.toString(ConfigHolder.getInstance().getDouble(PRECURSOR_PROP)));
        mainFrame.getFragmentToleranceTextField().setText(Double.toString(ConfigHolder.getInstance().getDouble(FRAGMENT_PROP)));
        mainFrame.getMinimumMzTextField().setText(Double.toString(ConfigHolder.getInstance().getDouble(MIN_MZ_PROP)));
        mainFrame.getMaximumMzTextField().setText(Double.toString(ConfigHolder.getInstance().getDouble(MAX_MZ_PROP)));
        mainFrame.getNeighbourSlicesOnlyCheckBox().setSelected(ConfigHolder.getInstance().getBoolean(NEIGHBOUR_SLICE_PROP));
        mainFrame.getFileNameSliceIndexTextField().setText(Integer.toString(ConfigHolder.getInstance().getInt(FILE_NAME_SLICE_INDEX_PROP)));
        boolean preprocessingOrder = ConfigHolder.getInstance().getBoolean(PREPROCESSING_ORDER_PROP);
        mainFrame.getPreprocessingOrderComboBox().setSelectedIndex(preprocessingOrder ? 0 : 1);
        mainFrame.getBinWeightingComboBox().setSelectedIndex(ConfigHolder.getInstance().getInt(BIN_WEIGHTING_PROP));
        mainFrame.getTransformationComboBox().setSelectedIndex(ConfigHolder.getInstance().getInt(TRANSORMATION_PROP));
        mainFrame.getNoiseFilterComboBox().setSelectedIndex(ConfigHolder.getInstance().getInt(NOISE_FILTER_PROP));
        mainFrame.getRemovePrecursorIonPeaksCheckBox().setSelected(ConfigHolder.getInstance().getBoolean(PRECURSOR_PEAK_REMOVAL_PROP));
        mainFrame.getNumberOfPeaksCutoffTextField().setText(Integer.toString(ConfigHolder.getInstance().getInt(NUMBER_OF_PEAKS_CUTOFF_PROP)));
        mainFrame.getPeakIntensityCutoffTextField().setText(Double.toString(ConfigHolder.getInstance().getDouble(PEAK_PERCENTAGE_CUTOFF)));
        mainFrame.getNumberOfThreadsTextField().setText(Integer.toString(ConfigHolder.getInstance().getInt(NUMBER_OF_THREADS_PROP)));
    }

    /**
     * Copy the parameter values to the ConfigHolder so that the can be used in
     * the pipeline.
     */
    private void copyParameterValues() {
        ConfigHolder.getInstance().setProperty(SPECTRA_PROP, mainFrame.getSpectraDirectoryTextField().getText());
        ConfigHolder.getInstance().setProperty(COMP_SPECTRA_PROP, mainFrame.getComparisonSpectraDirectoryTextField().getText());
        ConfigHolder.getInstance().setProperty(OUTPUT_PROP, mainFrame.getOutputDirectoryTextField().getText());
        ConfigHolder.getInstance().setProperty(CHARGE_PROP, mainFrame.getChargeCheckBox().isSelected());
        ConfigHolder.getInstance().setProperty(PRECURSOR_PROP, mainFrame.getPrecursorToleranceTextField().getText());
        ConfigHolder.getInstance().setProperty(FRAGMENT_PROP, mainFrame.getFragmentToleranceTextField().getText());
        ConfigHolder.getInstance().setProperty(MIN_MZ_PROP, mainFrame.getMinimumMzTextField().getText());
        ConfigHolder.getInstance().setProperty(MAX_MZ_PROP, mainFrame.getMaximumMzTextField().getText());
        ConfigHolder.getInstance().setProperty(NEIGHBOUR_SLICE_PROP, mainFrame.getNeighbourSlicesOnlyCheckBox().isSelected());
        if (mainFrame.getNeighbourSlicesOnlyCheckBox().isSelected()) {
            ConfigHolder.getInstance().setProperty(FILE_NAME_SLICE_INDEX_PROP, mainFrame.getFileNameSliceIndexTextField().getText());
        }
        ConfigHolder.getInstance().setProperty(PREPROCESSING_ORDER_PROP, mainFrame.getPreprocessingOrderComboBox().getSelectedIndex() == 0);
        ConfigHolder.getInstance().setProperty(BIN_WEIGHTING_PROP, mainFrame.getBinWeightingComboBox().getSelectedIndex());
        ConfigHolder.getInstance().setProperty(TRANSORMATION_PROP, mainFrame.getTransformationComboBox().getSelectedIndex());
        ConfigHolder.getInstance().setProperty(NOISE_FILTER_PROP, mainFrame.getNoiseFilterComboBox().getSelectedIndex());
        if (mainFrame.getNoiseFilterComboBox().getSelectedIndex() == 2) {
            ConfigHolder.getInstance().setProperty(NUMBER_OF_PEAKS_CUTOFF_PROP, mainFrame.getNumberOfPeaksCutoffTextField().getText());
        } else if (mainFrame.getNoiseFilterComboBox().getSelectedIndex() == 3) {
            ConfigHolder.getInstance().setProperty(PEAK_PERCENTAGE_CUTOFF, mainFrame.getPeakIntensityCutoffTextField().getText());
        }
        ConfigHolder.getInstance().setProperty(PRECURSOR_PEAK_REMOVAL_PROP, mainFrame.getRemovePrecursorIonPeaksCheckBox().isSelected());
        ConfigHolder.getInstance().setProperty(NUMBER_OF_THREADS_PROP, mainFrame.getNumberOfThreadsTextField().getText());
    }

    /**
     * Validate the user input and return a list of validation messages if
     * necessary.
     *
     * @return the list of validation messages
     */
    private List<String> validateInput() {
        List<String> validationMessages = new ArrayList<>();

        if (mainFrame.getSpectraDirectoryTextField().getText().isEmpty()) {
            validationMessages.add("Please provide a spectra input directory.");
        }
        if (mainFrame.getComparisonSpectraDirectoryTextField().getText().isEmpty()) {
            validationMessages.add("Please provide a comparison spectra input directory.");
        }
        if (mainFrame.getOutputDirectoryTextField().getText().isEmpty()) {
            validationMessages.add("Please provide an output directory.");
        }
        if (mainFrame.getPrecursorToleranceTextField().getText().isEmpty()) {
            validationMessages.add("Please provide a precursor tolerance value.");
        } else {
            try {
                Double tolerance = Double.valueOf(mainFrame.getPrecursorToleranceTextField().getText());
                if (tolerance < 0.0) {
                    validationMessages.add("Please provide a positive precursor tolerance value.");
                }
            } catch (NumberFormatException nfe) {
                validationMessages.add("Please provide a numeric precursor tolerance value.");
            }
        }
        if (mainFrame.getFragmentToleranceTextField().getText().isEmpty()) {
            validationMessages.add("Please provide a fragment tolerance value.");
        } else {
            try {
                Double tolerance = Double.valueOf(mainFrame.getFragmentToleranceTextField().getText());
                if (tolerance < 0.0) {
                    validationMessages.add("Please provide a positive fragment tolerance value.");
                }
            } catch (NumberFormatException nfe) {
                validationMessages.add("Please provide a numeric fragment tolerance value.");
            }
        }
        if (mainFrame.getMinimumMzTextField().getText().isEmpty()) {
            validationMessages.add("Please provide a minimum m/z value.");
        } else {
            try {
                Double tolerance = Double.valueOf(mainFrame.getMinimumMzTextField().getText());
                if (tolerance < 0.0) {
                    validationMessages.add("Please provide a positive minimum m/z value.");
                }
            } catch (NumberFormatException nfe) {
                validationMessages.add("Please provide a numeric minimum m/z value.");
            }
        }
        if (mainFrame.getMaximumMzTextField().getText().isEmpty()) {
            validationMessages.add("Please provide a maximum m/z value.");
        } else {
            try {
                Double tolerance = Double.valueOf(mainFrame.getMaximumMzTextField().getText());
                if (tolerance < 0.0) {
                    validationMessages.add("Please provide a positive maximum m/z value.");
                }
            } catch (NumberFormatException nfe) {
                validationMessages.add("Please provide a numeric maximum m/z value.");
            }
        }
        if (mainFrame.getNeighbourSlicesOnlyCheckBox().isSelected()) {
            if (mainFrame.getMaximumMzTextField().getText().isEmpty()) {
                validationMessages.add("Please provide a file name slice index value.");
            } else {
                try {
                    Integer index = Integer.valueOf(mainFrame.getFileNameSliceIndexTextField().getText());
                    if (index < 0) {
                        validationMessages.add("Please provide a positive file name slice index value.");
                    }
                } catch (NumberFormatException nfe) {
                    validationMessages.add("Please provide a numeric file name slice index value.");
                }
            }
        }
        if (mainFrame.getNoiseFilterComboBox().getSelectedIndex() == 2) {
            if (mainFrame.getNumberOfPeaksCutoffTextField().getText().isEmpty()) {
                validationMessages.add("Please a provide peak cutoff number when choosing the TopN intense peak selection filter.");
            } else {
                try {
                    Integer number = Integer.valueOf(mainFrame.getFileNameSliceIndexTextField().getText());
                    if (number < 0) {
                        validationMessages.add("Please provide a positive peak cutoff number value.");
                    }
                } catch (NumberFormatException nfe) {
                    validationMessages.add("Please provide a numeric peak cutoff number value.");
                }
            }
        } else if (mainFrame.getNoiseFilterComboBox().getSelectedIndex() == 3) {
            if (mainFrame.getNumberOfPeaksCutoffTextField().getText().isEmpty()) {
                validationMessages.add("Please provide peak cutoff percentage when choosing the Discard peaks with less than x% of precursor-intensity filter.");
            } else {
                try {
                    Double percentage = Double.valueOf(mainFrame.getFileNameSliceIndexTextField().getText());
                    if (percentage < 0.0) {
                        validationMessages.add("Please provide a positive peak cutoff percentage value.");
                    }
                } catch (NumberFormatException nfe) {
                    validationMessages.add("Please provide a numeric peak cutoff percentage value.");
                }
            }
        }

        return validationMessages;
    }

    /**
     * Shows a message dialog.
     *
     * @param title the dialog title
     * @param message the dialog message
     * @param messageType the dialog message type
     */
    private void showMessageDialog(final String title, final String message, final int messageType) {
        //add message to JTextArea
        JTextArea textArea = new JTextArea(message);
        //put JTextArea in JScrollPane
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 200));
        scrollPane.getViewport().setOpaque(false);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JOptionPane.showMessageDialog(mainFrame.getContentPane(), scrollPane, title, messageType);
    }

    /**
     * Center the run dialog on the frame.
     */
    private void centerRunDialog() {
        Point topLeft = mainFrame.getLocationOnScreen();
        Dimension parentSize = mainFrame.getSize();

        Dimension dialogSize = runDialog.getSize();

        int x;
        int y;

        if (parentSize.width > dialogSize.width) {
            x = ((parentSize.width - dialogSize.width) / 2) + topLeft.x;
        } else {
            x = topLeft.x;
        }

        if (parentSize.height > dialogSize.height) {
            y = ((parentSize.height - dialogSize.height) / 2) + topLeft.y;
        } else {
            y = topLeft.y;
        }

        runDialog.setLocation(x, y);
    }

    private class ScorePipelineSwingWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            LOGGER.info("starting spectrum similarity score pipeline");
            ScorePipeline.run();

            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                LOGGER.info("finished spectrum similarity score pipeline");
                JOptionPane.showMessageDialog(runDialog, "The score pipeline has finished.");
            } catch (InterruptedException | ExecutionException ex) {
                LOGGER.error(ex.getMessage(), ex);
                showMessageDialog("Unexpected error", ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            } catch (CancellationException ex) {
                LOGGER.info("the spectrum similarity score pipeline run was cancelled");
            } finally {

            }
        }
    }

}
