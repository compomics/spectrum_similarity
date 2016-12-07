/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui_specLib;

import config.ConfigHolder;
import gui.LogTextAreaAppender;
import gui.MainController;
import gui.RunDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFileChooser;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;
import java.util.List;
import javax.swing.JOptionPane;
import org.apache.commons.configuration.ConfigurationException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

/**
 *
 * @author Sule
 */
public class SpecLibSearchModeMainController extends MainController {

    private static final Logger LOGGER = Logger.getLogger(SpecLibSearchModeMainController.class);
    protected static final String SUM_MEAN_MEDIAN_PROP = "sum.mean.median";

    /**
     * Constructor.
     */
    public SpecLibSearchModeMainController() {
        super(false); // to make sure that super class of MainController will not be called        
    }

    /**
     * Init the controller for Spectrum Library Searching Mode.
     */
    @Override
    public void init() {
        mainFrame = new SpecLibSearchMainFrame();
        if (mainFrame instanceof SpecLibSearchMainFrame) {
            ((SpecLibSearchMainFrame) mainFrame).getScoringFunctionjComboBox();
        }
        mainFrame.setVisible(true);

        // add gui appender
        LogTextAreaAppender logTextAreaAppender = new LogTextAreaAppender();
        logTextAreaAppender.setThreshold(Priority.INFO);
        logTextAreaAppender.setImmediateFlush(true);
        PatternLayout layout = new org.apache.log4j.PatternLayout();
        layout.setConversionPattern("%d{yyyy-MM-dd HH:mm:ss} - %m%n");
        logTextAreaAppender.setLayout(layout);

        LOGGER.addAppender(logTextAreaAppender);
        LOGGER.setLevel((Level) Level.INFO);

        mainFrame.setTitle("Spectrum library searching mode " + ConfigHolder.getInstance().getString("score.pipeline.version", ""));

        runDialog = new RunDialog(((SpecLibSearchMainFrame) mainFrame), true);
        runDialog.getLogTextArea().setText("..." + System.lineSeparator());

        //get the appender for setting the text area
        logTextAreaAppender.setRunDialog(runDialog);

        //disable the necessary text fields
//        mainFrame.getFileNameSliceIndexTextField().setEnabled(false);
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
        mainFrame.getSpectraDirectoryButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //in response to the button click, show open dialog
                int returnVal = mainFrame.getSpectraDirectoryChooser().showOpenDialog(mainFrame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    //show spectra directory path in text field
                    mainFrame.getSpectraDirectoryTextField().setText(mainFrame.getSpectraDirectoryChooser().getSelectedFile().getAbsolutePath());
                }
            }
        });

        mainFrame.getComparisonSpectraDirectoryButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //in response to the button click, show open dialog
                int returnVal = mainFrame.getComparisonSpectraDirectoryChooser().showOpenDialog(mainFrame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    //show spectra directory path in text field
                    mainFrame.getComparisonSpectraDirectoryTextField().setText(mainFrame.getComparisonSpectraDirectoryChooser().getSelectedFile().getAbsolutePath());
                }
            }
        });

        mainFrame.getOutputDirectoryButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //in response to the button click, show open dialog
                int returnVal = mainFrame.getOutputDirectoryChooser().showOpenDialog(mainFrame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    //show spectra directory path in text field
                    mainFrame.getOutputDirectoryTextField().setText(mainFrame.getOutputDirectoryChooser().getSelectedFile().getAbsolutePath());
                }
            }
        });

        mainFrame.getNoiseFilterComboBox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
            }
        });

        mainFrame.getRunButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //validate input
                List<String> validationMessages = validateInput();
                if (!validationMessages.isEmpty()) {
                    StringBuilder message = new StringBuilder();
                    for (String validationMessage : validationMessages) {
                        message.append(validationMessage).append(System.lineSeparator());
                    }
                    showMessageDialog("Validation errors", message.toString(), JOptionPane.WARNING_MESSAGE);
                } else {
                    //copy the parameter values to the ConfigHolder - MUST BE UPDATED!
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
                        scorePipelineSwingWorker = new ScorePipelineSwingWorker(true);
                        scorePipelineSwingWorker.execute();

                        //show the run dialog
                        centerRunDialog();
                        runDialog.setVisible(true);
                    }
                }
            }
        });

        mainFrame.getCloseButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent we) {
                System.exit(0);
            }
        });

        runDialog.getClearButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runDialog.getLogTextArea().setText("..." + System.lineSeparator());
            }
        });

        runDialog.getCancelButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                scorePipelineSwingWorker.cancel(true);
//                runDialog.dispose();
                System.exit(0);
            }
        });

        // load the parameters from the properties file
        loadParameterValues();
    }

    /**
     * Copy the parameter values to the ConfigHolder so that the can be used for
     * spectrum library searching.
     */
    @Override
    protected void copyParameterValues() {
        ConfigHolder.getInstance().setProperty(SPECTRA_PROP, mainFrame.getSpectraDirectoryTextField().getText());
        ConfigHolder.getInstance().setProperty(COMP_SPECTRA_PROP, mainFrame.getComparisonSpectraDirectoryTextField().getText());
        ConfigHolder.getInstance().setProperty(OUTPUT_PROP, mainFrame.getOutputDirectoryTextField().getText());
        ConfigHolder.getInstance().setProperty(CHARGE_PROP, mainFrame.getChargeCheckBox().isSelected());
        ConfigHolder.getInstance().setProperty(MAX_PREC_CHARGE_PROP, ((SpecLibSearchMainFrame) mainFrame).getMaxPrecursorChargejTextField().getText());

        ConfigHolder.getInstance().setProperty(PRECURSOR_PROP, mainFrame.getPrecursorToleranceTextField().getText());
        ConfigHolder.getInstance().setProperty(FRAGMENT_PROP, mainFrame.getFragmentToleranceTextField().getText());

        // NO NEED FOR NEIGHBOUR SLICE PROPERTIES
        // NO NEED FOR FILE NAME SLICE INDEX PROPERTIES
        ConfigHolder.getInstance().setProperty(MIN_MZ_PROP, ((SpecLibSearchMainFrame) mainFrame).getMinMZjTextField().getText());
        ConfigHolder.getInstance().setProperty(MAX_MZ_PROP, ((SpecLibSearchMainFrame) mainFrame).getMaxMZjTextField().getText());
        ConfigHolder.getInstance().setProperty(SCORING_FUNC_PROP, ((SpecLibSearchMainFrame) mainFrame).getScoringFunctionjComboBox().getSelectedIndex());
        ConfigHolder.getInstance().setProperty(SUM_MEAN_MEDIAN_PROP, ((SpecLibSearchMainFrame) mainFrame).getScoringFunctionjComboBox().getSelectedIndex());

        ConfigHolder.getInstance().setProperty(PREPROCESSING_ORDER_PROP, mainFrame.getPreprocessingOrderComboBox().getSelectedIndex() == 0);

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
     * Load the parameter values from the properties file to perform spectrum
     * library searching and set them in the matching fields.
     */
    @Override
    protected void loadParameterValues() {
        mainFrame.getSpectraDirectoryTextField().setText(ConfigHolder.getInstance().getString(SPECTRA_PROP));
        mainFrame.getComparisonSpectraDirectoryTextField().setText(ConfigHolder.getInstance().getString(COMP_SPECTRA_PROP));
        mainFrame.getOutputDirectoryTextField().setText(ConfigHolder.getInstance().getString(OUTPUT_PROP));
        mainFrame.getChargeCheckBox().setSelected(ConfigHolder.getInstance().getBoolean(CHARGE_PROP));
        mainFrame.getMaxPrecursorChargejTextField().setText(ConfigHolder.getInstance().getString(MAX_PREC_CHARGE_PROP));
        mainFrame.getPrecursorToleranceTextField().setText(Double.toString(ConfigHolder.getInstance().getDouble(PRECURSOR_PROP)));
        mainFrame.getFragmentToleranceTextField().setText(Double.toString(ConfigHolder.getInstance().getDouble(FRAGMENT_PROP)));

        ((SpecLibSearchMainFrame) mainFrame).getMinMZjTextField().setText(Double.toString(ConfigHolder.getInstance().getDouble(MIN_MZ_PROP)));
        ((SpecLibSearchMainFrame) mainFrame).getMaxMZjTextField().setText(Double.toString(ConfigHolder.getInstance().getDouble(MAX_MZ_PROP)));
        ((SpecLibSearchMainFrame) mainFrame).getScoringFunctionjComboBox().setSelectedIndex(ConfigHolder.getInstance().getInt(SCORING_FUNC_PROP));
        ((SpecLibSearchMainFrame) mainFrame).getScoringFunctionjComboBox().setSelectedIndex(ConfigHolder.getInstance().getInt(SUM_MEAN_MEDIAN_PROP));

        mainFrame.getTransformationComboBox().setSelectedIndex(ConfigHolder.getInstance().getInt(TRANSORMATION_PROP));
        mainFrame.getNoiseFilterComboBox().setSelectedIndex(ConfigHolder.getInstance().getInt(NOISE_FILTER_PROP));
        mainFrame.getRemovePrecursorIonPeaksCheckBox().setSelected(ConfigHolder.getInstance().getBoolean(PRECURSOR_PEAK_REMOVAL_PROP));
        mainFrame.getNumberOfPeaksCutoffTextField().setText(Integer.toString(ConfigHolder.getInstance().getInt(NUMBER_OF_PEAKS_CUTOFF_PROP)));
        mainFrame.getPeakIntensityCutoffTextField().setText(Double.toString(ConfigHolder.getInstance().getDouble(PEAK_PERCENTAGE_CUTOFF)));

        boolean preprocessingOrder = ConfigHolder.getInstance().getBoolean(PREPROCESSING_ORDER_PROP);
        mainFrame.getPreprocessingOrderComboBox().setSelectedIndex(preprocessingOrder ? 0 : 1);

        mainFrame.getNumberOfThreadsTextField().setText(Integer.toString(ConfigHolder.getInstance().getInt(NUMBER_OF_THREADS_PROP)));
    }

    /**
     * Validate the user input for Spectrum Library searching mode and return a
     * list of validation messages if necessary.
     *
     * @return the list of validation messages
     */
    @Override
    protected List<String> validateInput() {
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
        if (mainFrame.getMaxPrecursorChargejTextField().getText().isEmpty()) {
            validationMessages.add("Please provide a maximum precursor charge value in both data sets.");
        } else {
            try {
                Double maxCharge = Double.valueOf(mainFrame.getMaxPrecursorChargejTextField().getText());
                if (maxCharge < 0.0) {
                    validationMessages.add("Please provide a maximum precursor charge value.");
                }
            } catch (NumberFormatException nfe) {
                validationMessages.add("Please provide a numeric maximum precursor charge value.");
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

        if (((SpecLibSearchMainFrame) mainFrame).getMinMZjTextField().getText().isEmpty()) {
            validationMessages.add("Please provide a minimum m/z value.");
        } else {
            try {
                Double minMZ = Double.valueOf(((SpecLibSearchMainFrame) mainFrame).getMinMZjTextField().getText());
                if (minMZ < 0.0) {
                    validationMessages.add("Please provide a positive minimum m/z value.");
                }
            } catch (NumberFormatException nfe) {
                validationMessages.add("Please provide a numeric minimum m/z value.");
            }
        }

        if (((SpecLibSearchMainFrame) mainFrame).getMaxMZjTextField().getText().isEmpty()) {
            validationMessages.add("Please provide a maximum m/z value.");
        } else {
            try {
                Double maxMZ = Double.valueOf(((SpecLibSearchMainFrame) mainFrame).getMaxMZjTextField().getText());
                if (maxMZ < 0.0) {
                    validationMessages.add("Please provide a positive maximum m/z value.");
                }
            } catch (NumberFormatException nfe) {
                validationMessages.add("Please provide a numeric maximum m/z value.");
            }
        }

        if (mainFrame.getNoiseFilterComboBox().getSelectedIndex() == 2) {
            if (mainFrame.getNumberOfPeaksCutoffTextField().getText().isEmpty()) {
                validationMessages.add("Please a provide peak cutoff number when choosing the TopN intense peak selection filter.");
            } else {
                try {
                    Integer number = Integer.valueOf(mainFrame.getNumberOfPeaksCutoffTextField().getText());
                    if (number < 0) {
                        validationMessages.add("Please provide a positive peak cutoff number value.");
                    }
                } catch (NumberFormatException nfe) {
                    validationMessages.add("Please provide a numeric peak cutoff number value.");
                }
            }
        } else if (mainFrame.getNoiseFilterComboBox().getSelectedIndex() == 3) {
            if (mainFrame.getPeakIntensityCutoffTextField().getText().isEmpty()) {
                validationMessages.add("Please provide peak cutoff percentage when choosing the Discard peaks with less than x% of precursor-intensity filter.");
            } else {
                try {
                    Double percentage = Double.valueOf(mainFrame.getPeakIntensityCutoffTextField().getText());
                    if (percentage < 0.0) {
                        validationMessages.add("Please provide a positive peak cutoff percentage value.");
                    }
                } catch (NumberFormatException nfe) {
                    validationMessages.add("Please provide a numeric peak cutoff percentage value.");
                }
            }
        }
        if (mainFrame.getNumberOfThreadsTextField().getText().isEmpty()) {
            validationMessages.add("Please provide a number of threads.");
        } else {
            try {
                Integer numberOfThreads = Integer.valueOf(mainFrame.getNumberOfThreadsTextField().getText());
                if (numberOfThreads < 0) {
                    validationMessages.add("Please provide a positive number of threads.");
                }
            } catch (NumberFormatException nfe) {
                validationMessages.add("Please provide a numeric number of threads.");
            }
        }

        return validationMessages;
    }

}
