package com.imcode.imcms.tools;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class Tool9755Dialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonRun;
    private JButton buttonClose;
    private JTextField sourceField;
    private JButton browseSourceButton;
    private JTextField destinationField;
    private JButton browseDestinationButton;
    private JLabel statusLabel;
    private JFileChooser directoryChooser;

    public Tool9755Dialog() {
        setTitle("Eliminate build time I18n templates");
        setPreferredSize(new Dimension(500, 200));
        setResizable(false);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonRun);

        buttonRun.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onRun();
            }
        });

        buttonClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        browseSourceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                directoryChooser = new JFileChooser();
                directoryChooser.setDialogTitle("Select source directory");
                directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if (directoryChooser.showDialog(Tool9755Dialog.this, "Select") == JFileChooser.APPROVE_OPTION)
                {
                    sourceField.setText(directoryChooser.getSelectedFile().getAbsolutePath());
                }
            }
        });

        browseDestinationButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                directoryChooser = new JFileChooser();
                directoryChooser.setDialogTitle("Select destination directory");
                directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if (directoryChooser.showDialog(Tool9755Dialog.this, "Select") == JFileChooser.APPROVE_OPTION)
                {
                    destinationField.setText(directoryChooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
    }

    private void onRun() {
        try {
            String source = sourceField.getText().trim();
            String destination = destinationField.getText().trim();
            boolean valid = true;
            if (source == null || source.length() == 0) {
                valid = false;
                JOptionPane.showMessageDialog(this, "The source directory must be selected",
                       "Validation error", JOptionPane.ERROR_MESSAGE);
            }
            else if (destination == null || destination.length() == 0) {
                valid = false;
                JOptionPane.showMessageDialog(this, "The destination directory must be selected",
                       "Validation error", JOptionPane.ERROR_MESSAGE);
            }

            if (valid) {
                statusLabel.setText("Migration has beed started");
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                Issue9755Fixer fixer = new Issue9755Fixer(source, destination);
                fixer.initialize();
                fixer.fixJspPages();
                statusLabel.setText("Migration done!");
//                fixer.fixHtmlTemplates();
//                fixer.fixReferences();
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        }
        catch(Throwable t){
            JOptionPane.showMessageDialog(this, t.getMessage(),
                       "Aplication error", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void onCancel() {
        dispose();
    }

    public static void main(String[] args) {
        Tool9755Dialog dialog = new Tool9755Dialog();
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
