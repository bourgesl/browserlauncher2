/************************************************
    Copyright 2004,2005 Jeff Chapman

    This file is part of BrowserLauncher2.

    BrowserLauncher2 is free software; you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    BrowserLauncher2 is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with BrowserLauncher2; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 ************************************************/
// $Id: BrowserLauncherTestApp.java,v 1.9 2005/02/03 00:59:56 jchapman0 Exp $
package edu.stanford.ejalbert.testing;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.exceptionhandler.BrowserLauncherErrorHandler;
import edu.stanford.ejalbert.BrowserLauncherRunner;

/**
 * Standalone gui that allows for testing the broserlauncher code and provides
 * a sample implementation.
 *
 * @author Jeff Chapman
 */
public class BrowserLauncherTestApp
        extends JFrame {
    private static final String debugResources =
            "edu.stanford.ejalbert.resources.Debugging";
    private JPanel urlPanel = new JPanel();
    private JButton browseButton = new JButton();
    private JLabel enterUrlLabel = new JLabel();
    private JTextField urlTextField = new JTextField();
    private BrowserLauncher launcher; // in ctor
    private BorderLayout borderLayout1 = new BorderLayout();
    private JTextArea debugTextArea = new JTextArea();
    private JPanel debugTextBttnPanel = new JPanel();
    private BoxLayout bttnBoxLayout = new BoxLayout(debugTextBttnPanel,
            BoxLayout.X_AXIS);
    private JScrollPane debugTextScrollPane = new JScrollPane();
    private JButton copyButton = new JButton();
    private ResourceBundle bundle; // in ctor
    private BorderLayout urlPaneLayout = new BorderLayout();

    public BrowserLauncherTestApp() {
        super();
        try {
            bundle = ResourceBundle.getBundle(debugResources);
            super.setTitle(bundle.getString("label.app.title"));
            launcher = new BrowserLauncher();
            jbInit();
            populateDebugInfo(bundle, debugTextArea);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        BrowserLauncherTestApp app = new BrowserLauncherTestApp();
        app.pack();
        app.setVisible(true);
    }

    private void populateDebugInfo(ResourceBundle bundle,
                                   JTextArea debugTextArea) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter, true);
        // display first message
        printWriter.println(bundle.getString("debug.mssg"));
        printWriter.println();
        // get property values to display
        StringTokenizer tokenizer =
                new StringTokenizer(bundle.getString("debug.propnames"),
                                    ";",
                                    false);
        int pipeSymbol;
        String token, display, property;
        while (tokenizer.hasMoreTokens()) {
            token = tokenizer.nextToken();
            pipeSymbol = token.indexOf('|');
            display = token.substring(0, pipeSymbol);
            property = token.substring(pipeSymbol + 1);
            printWriter.print(display);
            printWriter.println(System.getProperty(property));
        }
        printWriter.close();
        debugTextArea.append(stringWriter.toString());
    }

    private void jbInit()
            throws Exception {
        browseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browseButton_actionPerformed(e);
            }
        });
        browseButton.setText(bundle.getString("bttn.browse"));
        enterUrlLabel.setText(bundle.getString("label.url"));
        urlTextField.setText(bundle.getString("url.default"));
        urlTextField.setColumns(25);
        urlPanel.setLayout(urlPaneLayout);
        urlPanel.add(enterUrlLabel, BorderLayout.LINE_START);
        urlPanel.add(urlTextField, BorderLayout.CENTER);
        urlPanel.add(browseButton, BorderLayout.LINE_END);

        debugTextArea.setEditable(false);
        debugTextArea.setLineWrap(true);
        debugTextArea.setWrapStyleWord(true);
        debugTextArea.setText("");
        debugTextScrollPane.getViewport().add(debugTextArea);

        copyButton.setText(bundle.getString("bttn.copy"));
        copyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                copyButton_actionPerformed(e);
            }
        });
        debugTextBttnPanel.setLayout(bttnBoxLayout);
        debugTextBttnPanel.add(Box.createHorizontalGlue());
        debugTextBttnPanel.add(copyButton);
        debugTextBttnPanel.add(Box.createHorizontalStrut(2));

        this.getContentPane().setLayout(borderLayout1);
        this.getContentPane().add(debugTextScrollPane,
                                  java.awt.BorderLayout.CENTER);
        this.getContentPane().add(urlPanel,
                                  java.awt.BorderLayout.NORTH);
        this.getContentPane().add(debugTextBttnPanel,
                                  java.awt.BorderLayout.SOUTH);

        getRootPane().setDefaultButton(browseButton);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private static void updateDebugTextArea(Exception exception,
                                            JTextArea debugTextArea) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter, true);
        printWriter.println();
        exception.printStackTrace(printWriter);
        printWriter.println();
        printWriter.close();
        debugTextArea.append(stringWriter.toString());
    }

    private void browseButton_actionPerformed(ActionEvent e) {
        try {
            String urlString = urlTextField.getText();
            if(urlString == null || urlString.trim().length() == 0) {
                throw new MalformedURLException("You must specify a url.");
            }
            new URL(urlString); // may throw MalformedURLException
            BrowserLauncherErrorHandler errorHandler = new TestAppErrorHandler(
                    debugTextArea);
            BrowserLauncherRunner runner = new BrowserLauncherRunner(launcher,
                    urlString, errorHandler);
            Thread launcherThread = new Thread(runner);
            launcherThread.start();
        }
        catch (Exception ex) {
            // capture exception
            BrowserLauncherTestApp.updateDebugTextArea(ex, debugTextArea);
            // show message to user
            JOptionPane.showMessageDialog(this,
                                          ex.getMessage(),
                                          "Error Message",
                                          JOptionPane.ERROR_MESSAGE);
        }
    }

    private void copyButton_actionPerformed(ActionEvent e) {
        debugTextArea.selectAll();
        debugTextArea.copy();
        debugTextArea.select(0, 0);
    }

    private static class TestAppErrorHandler
            implements BrowserLauncherErrorHandler {
        private JTextArea debugTextArea; // in ctor

        TestAppErrorHandler(JTextArea debugTextArea) {
            this.debugTextArea = debugTextArea;
        }

        public void handleException(Exception ex) {
            // capture exception
            BrowserLauncherTestApp.updateDebugTextArea(ex, debugTextArea);
            // show message to user
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),
                                          ex.getMessage(),
                                          "Error Message",
                                          JOptionPane.ERROR_MESSAGE);
        }

    }
}
