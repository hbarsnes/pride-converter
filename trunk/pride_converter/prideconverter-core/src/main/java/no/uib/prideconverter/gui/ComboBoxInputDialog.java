package no.uib.prideconverter.gui;

import no.uib.prideconverter.util.*;
import java.awt.event.KeyEvent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.border.TitledBorder;

/**
 * A dialog where the user can input the name of the new element to add 
 * to the combo box.
 *
 * @author Harald Barsnes
 * 
 * Created March 2008
 */
public class ComboBoxInputDialog extends javax.swing.JDialog {

    private ComboBoxInputable comboBoxInputable;

    /** 
     * Creates a new ComboboxInputDialog with a JFrame as a parent.
     * 
     * @param parent
     * @param comboBoxInputable
     * @param modal 
     */
    public ComboBoxInputDialog(JFrame parent, ComboBoxInputable comboBoxInputable, boolean modal) {
        super(parent, modal);
        initComponents();

        // set the icon for the dialog
        // only works for Java 1.6 and newer
//        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().
//                getResource("/no/uib/prideconverter/icons/prideConverter_16.GIF")));

        this.comboBoxInputable = comboBoxInputable;
        setLocationRelativeTo(parent);
    }

    /** 
     * Creates a new ComboboxInputDialog with a JDialog as a parent.
     * 
     * @param parent
     * @param comboBoxInputable
     * @param modal 
     */
    public ComboBoxInputDialog(JDialog parent, ComboBoxInputable comboBoxInputable, boolean modal) {
        super(parent, modal);
        initComponents();

        // set the icon for the dialog
        // only works for Java 1.6 and newer
//        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().
//                getResource("/no/uib/prideconverter/icons/prideConverter_16.GIF")));

        this.comboBoxInputable = comboBoxInputable;
        setLocationRelativeTo(parent);
    }

    /**
     * Sets the title of border.
     * 
     * @param title
     */
    public void setBorderTitle(String title) {
        ((TitledBorder) inputJPanel.getBorder()).setTitle(title);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        inputJPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        inputJTextField = new javax.swing.JTextField();
        cancelJButton = new javax.swing.JButton();
        okJButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        inputJPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, " ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(0, 0, 0))); // NOI18N

        jLabel1.setText("Name:");

        inputJTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                inputJTextFieldKeyReleased(evt);
            }
        });

        org.jdesktop.layout.GroupLayout inputJPanelLayout = new org.jdesktop.layout.GroupLayout(inputJPanel);
        inputJPanel.setLayout(inputJPanelLayout);
        inputJPanelLayout.setHorizontalGroup(
            inputJPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(inputJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .add(18, 18, 18)
                .add(inputJTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 280, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        inputJPanelLayout.setVerticalGroup(
            inputJPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(inputJPanelLayout.createSequentialGroup()
                .add(inputJPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(inputJTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        cancelJButton.setText("Cancel");
        cancelJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelJButtonActionPerformed(evt);
            }
        });

        okJButton.setText("OK");
        okJButton.setEnabled(false);
        okJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okJButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(inputJPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(239, 239, 239)
                        .add(okJButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cancelJButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(inputJPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 17, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(okJButton)
                    .add(cancelJButton))
                .add(12, 12, 12))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    /**
     * @see #cancelJButtonActionPerformed(java.awt.event.ActionEvent)
     * 
     * @param evt
     */
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        cancelJButtonActionPerformed(null);
    }//GEN-LAST:event_formWindowClosing

    /**
     * Checks if the given name is not already in the combo box, if not 
     * it is inserted and the dialog is closed.
     * 
     * @param evt
     */
    private void okJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okJButtonActionPerformed
        if (comboBoxInputable.alreadyInserted(inputJTextField.getText())) {
            JOptionPane.showMessageDialog(this, "Name is already in use!");
        } else {
            comboBoxInputable.insertIntoComboBox(inputJTextField.getText());
            this.setVisible(false);
            dispose();
        }
    }//GEN-LAST:event_okJButtonActionPerformed

    /**
     * Closes the dialog.
     * 
     * @param evt
     */
    private void cancelJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelJButtonActionPerformed
        comboBoxInputable.resetComboBox();
        this.setVisible(false);
        dispose();
    }//GEN-LAST:event_cancelJButtonActionPerformed

    /**
     * Enables the ok button if the a name is inserted into the input field.
     * 
     * @param evt
     */
    private void inputJTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inputJTextFieldKeyReleased
        if (inputJTextField.getText().length() > 0) {
            okJButton.setEnabled(true);
        } else {
            okJButton.setEnabled(false);
        }

        if (okJButton.isEnabled() && evt.getKeyCode() == KeyEvent.VK_ENTER) {
            okJButtonActionPerformed(null);
        }
    }//GEN-LAST:event_inputJTextFieldKeyReleased
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelJButton;
    private javax.swing.JPanel inputJPanel;
    private javax.swing.JTextField inputJTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton okJButton;
    // End of variables declaration//GEN-END:variables
}
