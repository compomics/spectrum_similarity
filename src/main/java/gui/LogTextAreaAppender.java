package gui;

import javax.swing.SwingUtilities;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Appender class for writing log messages to a JTextArea.
 *
 * @author Niels Hulstaert
 */
public class LogTextAreaAppender extends WriterAppender {

    /**
     * The dialog to log to.
     */
    private RunDialog runDialog;

    public void setRunDialog(RunDialog runDialog) {
        this.runDialog = runDialog;
    }

    @Override
    public void append(LoggingEvent event) {
        final String message = this.layout.format(event);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                runDialog.getLogTextArea().append(message);
                //repaint view
                runDialog.validate();
                runDialog.repaint();
            }
        });
    }

}
