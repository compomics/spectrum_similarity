package main;

import gui.LogTextAreaAppender;
import gui.MainController;
import gui_specLib.SpecLibSearchMainFrame;
import gui_spectral_match_visualization.MainGUI;
import gui_spectral_match_visualization.StartDialog;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Painter;
import javax.swing.UIManager;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 * This class runs the score pipeline graphical user interface (GUI).
 *
 * @author Niels Hulstaert
 */
public class ScorePipelineStarter {

    /**
     * String instance for Header.
     */
    private static final String HEADER = "[Spectrum similarity - scoring functions to estimate the similarity of pairwise MS/MS spectra]\n";

    /**
     * String instance for Usage.
     */
    private static final String USAGE = "java -jar <jar file name>";

    /**
     * String instance for the selected option.
     */
    private static String selected_option = "pc";

    /**
     * Logger instance.
     */
    private static Logger LOGGER;

    /**
     * Options instance.
     */
    private static Options options;

    /**
     * The startup error message.
     */
    private static final String ERROR_MESSAGE = "An error occured during startup, please try again."
            + System.lineSeparator() + "If the problem persists, contact your administrator or post an issue on the google code page.";

    /**
     * The GUI main controller.
     */
    private MainController mainController = new MainController();

    /**
     * No-arg constructor.
     */
    public ScorePipelineStarter() {
    }

    /**
     * Main method.
     *
     * @param args the main method arguments
     */
    public static void main(final String[] commandLineArguments) {
        LOGGER = Logger.getLogger(ScorePipelineStarter.class);

        constructOptions();

        displayBlankLines(1, System.out);
        displayHeader(System.out);
        
        displayBlankLines(2, System.out);
        parse(commandLineArguments);


//        /*
//         * Set the Nimbus look and feel
//         */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /*
//         * If Nimbus (introduced in Java SE 6) is not available, stay with the
//         * default look and feel. For details see
//         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
//         */
//        try {
//            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
//            LOGGER.error(ex.getMessage(), ex);
//        }
//        //</editor-fold>
//
//        //set background color for JOptionPane and JPanel instances
//        UIManager.getLookAndFeelDefaults().put("OptionPane.background", Color.WHITE);
//        UIManager.getLookAndFeelDefaults().put("Panel.background", Color.WHITE);
//        UIManager.getLookAndFeelDefaults().put("FileChooser.background", Color.WHITE);
//        //set background color for JFileChooser instances
//        UIManager.getLookAndFeelDefaults().put("FileChooser[Enabled].backgroundPainter",
//                (Painter<JFileChooser>) new Painter<JFileChooser>() {
//            @Override
//            public void paint(Graphics2D g, JFileChooser object, int width, int height) {
//                g.setColor(Color.WHITE);
//                g.draw(object.getBounds());
//            }
//        });
//
//        ScorePipelineStarter scorePipelineStarter = new ScorePipelineStarter();
//        scorePipelineStarter.launch();
    }

    /**
     * Launch the GUI for the published pipeline.
     */
    private void launch() {
        try {
            mainController.init();
            mainController.showView();
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            //add message to JTextArea
            JTextArea textArea = new JTextArea(ERROR_MESSAGE + System.lineSeparator() + System.lineSeparator() + ex.getMessage());
            //put JTextArea in JScrollPane
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(600, 200));
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);

            JOptionPane.showMessageDialog(null, scrollPane, "Score pipeline startup error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    /**
     * Construct Options.
     *
     * There are 4 options: 1-Run pipeline GUI 2-Run pipeline CLI 3-Run spectrum
     * pairwise inspection GUI 4-Run spectrum library searching GUI
     *
     */
    private static void constructOptions() {
        options = new Options();

        options.addOption("h", "help", Boolean.FALSE, "Help");
        options.addOption("u", "usage", Boolean.FALSE, "Usage");

        Option pipelineCommandLineOption = new Option("pc", "pipeline_cli", Boolean.FALSE, "Pipeline CLI mode");
        pipelineCommandLineOption.setArgName("pipeline_cli");
        Option pipelineStartupGuiOption = new Option("pg", "pipeline_gui", Boolean.FALSE, "Pipeline GUI mode");
        pipelineStartupGuiOption.setArgName("pipeline_gui");
        Option visualizeGuiOption = new Option("v", "visualize_gui", Boolean.FALSE, "Pairwise spectrum visualization GUI mode");
        visualizeGuiOption.setArgName("visualize_gui");
        Option specLibSearchGuiOption = new Option("s", "speclibsearch_gui", Boolean.FALSE, "Spectrum library searching GUI mode");
        specLibSearchGuiOption.setArgName("speclibsearch_gui");

        OptionGroup commandLineModeOptionGroup = new OptionGroup();
        commandLineModeOptionGroup.addOption(pipelineCommandLineOption);
        commandLineModeOptionGroup.addOption(pipelineStartupGuiOption);
        commandLineModeOptionGroup.addOption(visualizeGuiOption);
        commandLineModeOptionGroup.addOption(specLibSearchGuiOption);

        options.addOptionGroup(commandLineModeOptionGroup);
    }

    /**
     * Display example application header.
     *
     * @out OutputStream to which header should be written.
     */
    private static void displayHeader(OutputStream out) {
        try {
            out.write(HEADER.getBytes());
        } catch (IOException ioEx) {
            System.out.println(HEADER);
        }
    }

    /**
     * Write the provided number of blank lines to the provided OutputStream.
     *
     * @param numberBlankLines Number of blank lines to write.
     * @param out OutputStream to which to write the blank lines.
     */
    private static void displayBlankLines(
            int numberBlankLines,
            OutputStream out) {
        try {
            for (int i = 0; i < numberBlankLines; ++i) {
                out.write("\n".getBytes());
            }
        } catch (IOException ioEx) {
            for (int i = 0; i < numberBlankLines; ++i) {
                System.out.println();
            }
        }
    }

    /**
     * Print usage information to provided OutputStream.
     *
     * @param applicationName Name of application to list in usage.
     * @param options Command-line options to be part of usage.
     * @param out OutputStream to which to write the usage information.
     */
    private static void printUsage(
            String applicationName,
            Options options,
            OutputStream out) {
        PrintWriter writer = new PrintWriter(out);
        HelpFormatter usageFormatter = new HelpFormatter();
        usageFormatter.printUsage(writer, 80, applicationName, options);
        writer.flush();
    }

    /**
     * Write "help" to the provided OutputStream.
     */
    private static void printHelp(
            Options options,
            int printedRowWidth,
            String header,
            String footer,
            int spacesBeforeOption,
            int spacesBeforeOptionDescription,
            boolean displayUsage,
            final OutputStream out) {
        PrintWriter writer = new PrintWriter(out);
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(
                writer,
                printedRowWidth,
                USAGE,
                header,
                options,
                spacesBeforeOption,
                spacesBeforeOptionDescription,
                footer,
                displayUsage);
        writer.flush();
    }

    /**
     * Apply Apache Commons CLI parser to command-line arguments.
     *
     * @param commandLineArguments Command-line arguments to be processed.
     */
    private static void parse(String[] commandLineArguments) {
        CommandLineParser cmdLineParser = new BasicParser();
        CommandLine commandLine;
        try {
            commandLine = cmdLineParser.parse(options, commandLineArguments);
            if (commandLine.getOptions().length == 0) {
                //launch startup GUI mode
                launchPipelineGuiMode();
            }
            if (commandLine.hasOption('h')) {
                selected_option = "h";
                printHelp(options, 80, "Help", "End of Help", 5, 3, true, System.out);
            }
            if (commandLine.hasOption('u')) {
                selected_option = "u";
                printUsage(USAGE, options, System.out);
            }
            if (commandLine.hasOption("pc")) {
                try {
                    //launch CLI mode for the published pipeline 
                    ScorePipeline.main(new String[1]);
                } catch (Exception ex) {
                    LOGGER.error(ex.getMessage(), ex);
                    printHelp(options, 80, "Help", "End of Help", 5, 3, true, System.out);
                }
            }
            if (commandLine.hasOption("pg")) {
                selected_option = "pg";
                //launch GUI mode for the published pipeline 
                launchPipelineGuiMode();
            }
            if (commandLine.hasOption('v')) {
                selected_option = "v";
                //launch Pairwise spectrum visualization GUI mode
                launchVisualizationGui();
            }
            if (commandLine.hasOption('s')) {
                selected_option = "s";
                //launch Spectrum library searching GUI mode
                launchSpectrumLibSearchingGui();
            }
        } catch (ParseException parseException) {
            System.out.println("Encountered exception while parsing :\n" + parseException.getMessage());
            printHelp(options, 80, "Help", "End of Help", 5, 3, true, System.out);
        }
    }

    /**
     * Run published pipeline in startup GUI mode.
     */
    private static void launchPipelineGuiMode() {
        try {
            /**
             * Set the Nimbus look and feel.
             */
            //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
            /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
             */
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
                LOGGER.error(ex.getMessage(), ex);
            }

            //set background color for JOptionPane and JPanel instances
            UIManager.getLookAndFeelDefaults().put("OptionPane.background", Color.WHITE);
            UIManager.getLookAndFeelDefaults().put("Panel.background", Color.WHITE);
            UIManager.getLookAndFeelDefaults().put("FileChooser.background", Color.WHITE);
            //set background color for JFileChooser instances
            UIManager.getLookAndFeelDefaults().put("FileChooser[Enabled].backgroundPainter",
                    (Painter<JFileChooser>) new Painter<JFileChooser>() {
                @Override
                public void paint(Graphics2D g, JFileChooser object, int width, int height) {
                    g.setColor(Color.WHITE);
                    g.draw(object.getBounds());
                }
            });
            //</editor-fold>

            MainController mainController = new MainController();
            mainController.init();
            mainController.showView();
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            //add message to JTextArea
            JTextArea textArea = new JTextArea(ERROR_MESSAGE + System.lineSeparator() + System.lineSeparator() + ex.getMessage());
            //put JTextArea in JScrollPane
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(600, 200));
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);

            JOptionPane.showMessageDialog(null, scrollPane, "Pipeline startup GUI error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    /**
     * Run pairwise spectrum visualization
     */
    private static void launchVisualizationGui() {
        /**
         * Set the Nimbus look and feel.
         */
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
            java.util.logging.Logger.getLogger(StartDialog.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(StartDialog.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(StartDialog.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(StartDialog.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        //set background color for JOptionPane and JPanel instances
        UIManager.getLookAndFeelDefaults().put("OptionPane.background", Color.WHITE);
        UIManager.getLookAndFeelDefaults().put("Panel.background", Color.WHITE);
        UIManager.getLookAndFeelDefaults().put("FileChooser.background", Color.WHITE);
        //set background color for JFileChooser instances
        UIManager.getLookAndFeelDefaults().put("FileChooser[Enabled].backgroundPainter",
                (Painter<JFileChooser>) new Painter<JFileChooser>() {
            @Override
            public void paint(Graphics2D g, JFileChooser object, int width, int height) {
                g.setColor(Color.WHITE);
                g.draw(object.getBounds());
            }
        });
        //</editor-fold>

        //Create and display the form
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new MainGUI().setVisible(true);
                } catch (MzMLUnmarshallerException | FileNotFoundException | ClassNotFoundException ex) {
                    LOGGER.error(ex.getMessage(), ex);
                }
            }
        });
    }

    /**
     * Run pairwise spectrum library searching mode
     */
    private static void launchSpectrumLibSearchingGui() {
        /**
         * Set the Nimbus look and feel.
         */
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
            java.util.logging.Logger.getLogger(SpecLibSearchMainFrame.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SpecLibSearchMainFrame.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SpecLibSearchMainFrame.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SpecLibSearchMainFrame.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        //set background color for JOptionPane and JPanel instances
        UIManager.getLookAndFeelDefaults().put("OptionPane.background", Color.WHITE);
        UIManager.getLookAndFeelDefaults().put("Panel.background", Color.WHITE);
        UIManager.getLookAndFeelDefaults().put("FileChooser.background", Color.WHITE);
        //set background color for JFileChooser instances
        UIManager.getLookAndFeelDefaults().put("FileChooser[Enabled].backgroundPainter",
                (Painter<JFileChooser>) new Painter<JFileChooser>() {
            @Override
            public void paint(Graphics2D g, JFileChooser object, int width, int height) {
                g.setColor(Color.WHITE);
                g.draw(object.getBounds());
            }
        });
        //</editor-fold>

        //Create and display the form
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new SpecLibSearchMainFrame().setVisible(true);
                } catch (Exception ex) {
                    LOGGER.error(ex.getMessage(), ex);
                }
            }
        });
    }

}
