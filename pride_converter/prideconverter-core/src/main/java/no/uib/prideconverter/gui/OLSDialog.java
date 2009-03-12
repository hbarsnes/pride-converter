package no.uib.prideconverter.gui;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import javax.xml.rpc.ServiceException;
import uk.ac.ebi.ook.web.services.Query;
import uk.ac.ebi.ook.web.services.QueryService;
import uk.ac.ebi.ook.web.services.QueryServiceLocator;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import no.uib.prideconverter.util.OLSInputable;
import no.uib.prideconverter.util.Util;

/**
 * A dialog for interacting with the Ontology Lookup Service OLS 
 * (http://www.ebi.ac.uk/ontology-lookup/).
 *
 * @author  Harald Barsnes
 * 
 * Created March 2008
 */
public class OLSDialog extends javax.swing.JDialog {

    private String field;
    private String selectedOntology;
    private int modifiedRow = -1;
    private OLSInputable olsInputable;
    private String mappedTerm;

    /**
     * Opens a dialog that lets you search for terms using the OLS.
     * 
     * @param olsInputable 
     * @param field the name of the field to insert the results
     * @param selectedOntology the name of the ontology to search in
     */
    public OLSDialog(OLSInputable olsInputable, boolean modal, String field,
            String selectedOntology, String term) {
        new OLSDialog(olsInputable, modal, field, selectedOntology, -1, term);
    }

    /**
     * Opens a dialog that lets you search for terms using the OLS.
     * 
     * @param olsInputable 
     * @param field the name of the field to insert the results
     * @param selectedOntology the name of the ontology to search in
     * @param modifiedRow the row to modify, use -1 if adding a new row
     */
    public OLSDialog(OLSInputable olsInputable, boolean modal, String field,
            String selectedOntology, int modifiedRow, String term) {
        super(olsInputable.getWindow());

        this.setModal(modal);
        this.olsInputable = olsInputable;
        this.field = field;
        this.selectedOntology = selectedOntology;
        this.modifiedRow = modifiedRow;
        this.mappedTerm = term;

        initComponents();

        olsResultsJTable.getTableHeader().setReorderingAllowed(false);

        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().
                getResource("/no/uib/prideconverter/icons/ols_transparent_small.GIF")));

        boolean error = insertOntologyNames();

        if (error) {
            this.dispose();
        } else {
            olsSearchTextField.requestFocus();

            if (mappedTerm != null) {
                olsSearchTextField.setText(mappedTerm);
                olsSearchTextFieldKeyReleased(null);
            }

            this.setLocationRelativeTo(olsInputable.getWindow());
            this.setVisible(true);
        }
    }

    /**
     * Retrieves and inserts the ontology names into the ontology combo box.
     * 
     * @return false if an error occured, true otherwise
     */
    public boolean insertOntologyNames() {

        boolean error = false;

        Vector ontologyNamesAndKeys = new Vector();

        try {
            QueryService locator = new QueryServiceLocator();
            Query qs = locator.getOntologyQuery();
            Map map = qs.getOntologyNames();

            String temp = "";

            for (Iterator i = map.keySet().iterator(); i.hasNext();) {
                String key = (String) i.next();

                temp = map.get(key) + " [" + key + "]";
                ontologyNamesAndKeys.add(temp);
            }

            java.util.Collections.sort(ontologyNamesAndKeys);

            ontologyJComboBox.setModel(new DefaultComboBoxModel(ontologyNamesAndKeys));
            ontologyJComboBox.setSelectedItem(selectedOntology);
        } catch (RemoteException ex) {
            JOptionPane.showMessageDialog(
                    this, "Not able to contact the OLS. Make sure that you are online and try again.\n" +
                    "You may also want to check your firewall settings.",
                    "OLS Not Available", JOptionPane.ERROR_MESSAGE);
            Util.writeToErrorLog("Error when trying to access OLS: ");
            ex.printStackTrace();
            error = true;
        } catch (ServiceException ex) {
            JOptionPane.showMessageDialog(
                    this, "Not able to contact the OLS. Make sure that you are online and try again.\n" +
                    "You may also want to check your firewall settings.",
                    "OLS Not Available", JOptionPane.ERROR_MESSAGE);
            Util.writeToErrorLog("Error when trying to access OLS: ");
            ex.printStackTrace();
            error = true;
        }

        return error;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        insertSelectedJButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane = new javax.swing.JScrollPane();
        olsResultsJTable = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        ontologyJComboBox = new javax.swing.JComboBox();
        olsSearchTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        numberOfTermsJTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        olsGraphSearchJButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        olsResultsJEditorPane = new javax.swing.JEditorPane();
        cancelJButton = new javax.swing.JButton();
        helpJButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        aboutJButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(" Ontology Lookup Service (OLS)");

        insertSelectedJButton.setText("Use Selected Term");
        insertSelectedJButton.setEnabled(false);
        insertSelectedJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertSelectedJButtonActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "OLS Results", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(0, 0, 0))); // NOI18N

        olsResultsJTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Accession", "CV Term"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        olsResultsJTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                olsResultsJTableMouseClicked(evt);
            }
        });
        olsResultsJTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                olsResultsJTableKeyReleased(evt);
            }
        });
        jScrollPane.setViewportView(olsResultsJTable);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 453, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Search Parameters", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(0, 0, 0))); // NOI18N

        jLabel1.setText("Ontology:");

        ontologyJComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ontologyJComboBoxItemStateChanged(evt);
            }
        });

        olsSearchTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                olsSearchTextFieldKeyReleased(evt);
            }
        });

        jLabel3.setText("Term:");

        numberOfTermsJTextField.setEditable(false);
        numberOfTermsJTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        numberOfTermsJTextField.setToolTipText("Number of Matching Terms");

        jLabel2.setFont(new java.awt.Font("Tahoma", 2, 10));
        jLabel2.setText("An alternative way of searching the ontology is available here:");

        olsGraphSearchJButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/no/uib/prideconverter/icons/ols_transparent.GIF"))); // NOI18N
        olsGraphSearchJButton.setToolTipText("Go to the Ontology Lookup Service web page");
        olsGraphSearchJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                olsGraphSearchJButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ontologyJComboBox, 0, 401, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(olsSearchTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(olsGraphSearchJButton, 0, 0, Short.MAX_VALUE)
                            .addComponent(numberOfTermsJTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(ontologyJComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(numberOfTermsJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(olsSearchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(olsGraphSearchJButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(11, 11, 11))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Term Details", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(0, 0, 0))); // NOI18N

        olsResultsJEditorPane.setEditable(false);
        jScrollPane1.setViewportView(olsResultsJEditorPane);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 453, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
                .addContainerGap())
        );

        cancelJButton.setText("Cancel");
        cancelJButton.setMaximumSize(new java.awt.Dimension(121, 23));
        cancelJButton.setMinimumSize(new java.awt.Dimension(121, 23));
        cancelJButton.setPreferredSize(new java.awt.Dimension(121, 23));
        cancelJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelJButtonActionPerformed(evt);
            }
        });

        helpJButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/no/uib/prideconverter/icons/help.GIF"))); // NOI18N
        helpJButton.setToolTipText("Help");
        helpJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpJButtonActionPerformed(evt);
            }
        });

        aboutJButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/no/uib/prideconverter/icons/ols_transparent_small.GIF"))); // NOI18N
        aboutJButton.setToolTipText("About");
        aboutJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutJButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(helpJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(aboutJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 178, Short.MAX_VALUE)
                        .addComponent(insertSelectedJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelJButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 489, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cancelJButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(insertSelectedJButton)))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(aboutJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(helpJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(12, 12, 12))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Updates the search results if the ontology is changed.
     * 
     * @param evt
     */
    private void ontologyJComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ontologyJComboBoxItemStateChanged
        olsSearchTextFieldKeyReleased(null);
    }//GEN-LAST:event_ontologyJComboBoxItemStateChanged

    /**
     * Searches the selected ontology for terms matching the inserted string. 
     * The search finds all terms having the current string as a substring. 
     * (But seems to be limited somehow, seeing as using two letters, can 
     * result in more hits, than using just one of the letters...)
     * 
     * @param evt
     */
    private void olsSearchTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_olsSearchTextFieldKeyReleased
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        olsSearchTextField.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

        insertSelectedJButton.setEnabled(false);

        try {
            int rowCount = ((DefaultTableModel) olsResultsJTable.getModel()).getRowCount();

            for (int i = rowCount - 1; i >= 0; i--) {
                ((DefaultTableModel) olsResultsJTable.getModel()).removeRow(i);
            }

            String ontology;
            QueryService locator = new QueryServiceLocator();
            Query qs = locator.getOntologyQuery();
            Map map;

            ontology = ((String) ontologyJComboBox.getSelectedItem());
            ontology = ontology.substring(ontology.lastIndexOf("[") + 1, ontology.length() -
                    1);

            map = qs.getTermsByName(olsSearchTextField.getText(),
                    ontology + "", false);

            for (Iterator i = map.keySet().iterator(); i.hasNext();) {
                String key = (String) i.next();
                ((DefaultTableModel) olsResultsJTable.getModel()).addRow(new Object[]{key, map.get(key)});
            }

            olsResultsJEditorPane.setText("");
            olsSearchTextField.requestFocus();

            this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            olsSearchTextField.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
            numberOfTermsJTextField.setText("" + map.size());

            //No mathcing terms found
            if (map.size() == 0) {
                //JOptionPane.showMessageDialog(this, "No mathcing terms found.");
                //this.olsSearchTextField.requestFocus();
            }
        } catch (RemoteException ex) {
            JOptionPane.showMessageDialog(
                    this, "Not able to contact the OLS. Make sure that you are online and try again.\n" +
                    "You may also want to check your firewall settings.",
                    "OLS Not Available", JOptionPane.ERROR_MESSAGE);
            Util.writeToErrorLog("Error when trying to access OLS: ");
            ex.printStackTrace();
        } catch (ServiceException ex) {
            JOptionPane.showMessageDialog(
                    this, "Not able to contact the OLS. Make sure that you are online and try again.\n" +
                    "You may also want to check your firewall settings.",
                    "OLS Not Available", JOptionPane.ERROR_MESSAGE);
            Util.writeToErrorLog("Error when trying to access OLS: ");
            ex.printStackTrace();
        }

        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        olsSearchTextField.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
    }//GEN-LAST:event_olsSearchTextFieldKeyReleased

    /**
     * Inserts the selected ontology into the parents text field or table and 
     * then closes the dialog.
     * 
     * @param evt
     */
    private void insertSelectedJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertSelectedJButtonActionPerformed

        this.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

        String ontologyLong = ((String) ontologyJComboBox.getSelectedItem());
        String ontologyShort = ontologyLong.substring(ontologyLong.lastIndexOf("[") +
                1, ontologyLong.length() - 1);
        String selectedValue = "";
        String accession = "" +
                olsResultsJTable.getValueAt(olsResultsJTable.getSelectedRow(), 0);

        try {
            QueryService locator = new QueryServiceLocator();
            Query qs = locator.getOntologyQuery();
            selectedValue = qs.getTermById(accession, ontologyShort);

            //insert the value into the correct text field or table
            olsInputable.insertOLSResult(field, selectedValue, accession,
                    ontologyShort, ontologyLong, modifiedRow, mappedTerm);

            this.setVisible(false);
            this.dispose();

        } catch (RemoteException ex) {
            JOptionPane.showMessageDialog(
                    this, "Not able to contact the OLS. Make sure that you are online and try again.\n" +
                    "You may also want to check your firewall settings.",
                    "OLS Not Available", JOptionPane.ERROR_MESSAGE);
            Util.writeToErrorLog("Error when trying to access OLS: ");
            ex.printStackTrace();
        } catch (ServiceException ex) {
            JOptionPane.showMessageDialog(
                    this, "Not able to contact the OLS. Make sure that you are online and try again.\n" +
                    "You may also want to check your firewall settings.",
                    "OLS Not Available", JOptionPane.ERROR_MESSAGE);
            Util.writeToErrorLog("Error when trying to access OLS: ");
            ex.printStackTrace();
        }

        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_insertSelectedJButtonActionPerformed

    /**
     * If the user double clicks the selected row is inserted into the parent 
     * frame and closes the dialog. A single click retrieves the additional
     * information known about the term and displays it in the "Term Details" 
     * frame.
     * 
     * @param evt
     */
    private void olsResultsJTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_olsResultsJTableMouseClicked

        this.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

        int row = this.olsResultsJTable.getSelectedRow();

        if (row != -1) {
            insertSelectedJButton.setEnabled(true);
        } else {
            insertSelectedJButton.setEnabled(false);
        }

        boolean doSearch = true;

        if (evt != null) {
            if (evt.getClickCount() == 2 && evt.getButton() ==
                    java.awt.event.MouseEvent.BUTTON1) {
                insertSelectedJButtonActionPerformed(null);
                doSearch = false;
            }
        }

        // This does not seem to work... The search is always performed. It 
        // seems as the first click in the double click results in one event 
        // and the second in another. This results in the term details always 
        // beeing retrieved...
        if (doSearch) {

            if (row != -1) {

                String termID = (String) olsResultsJTable.getValueAt(row, 0);
                String temp = "";

                try {
                    String ontology = ((String) ontologyJComboBox.getSelectedItem());
                    ontology = ontology.substring(ontology.lastIndexOf("[") + 1, ontology.length() -
                            1);

                    if (ontology.equalsIgnoreCase("NEWT")) {
                        temp = "\n\n    Retreiving 'Term Details' is disabled for NEWT" +
                                "\n    (For more information see the Help pages).";
                    } else {
                        QueryService locator = new QueryServiceLocator();
                        Query qs = locator.getOntologyQuery();
                        Map map = qs.getTermMetadata(termID, ontology);

                        for (Iterator i = map.keySet().iterator(); i.hasNext();) {
                            String key = (String) i.next();
                            temp += key + " - " + map.get(key) + "\n";
                        }

                        map = qs.getTermXrefs(termID, ontology);

                        for (Iterator i = map.keySet().iterator(); i.hasNext();) {
                            String key = (String) i.next();
                            temp += key + " - " + map.get(key) + "\n";
                        }
                    }

                    olsResultsJEditorPane.setText(temp);
                    olsResultsJEditorPane.setCaretPosition(0);

                } catch (RemoteException ex) {
                    JOptionPane.showMessageDialog(
                            this, "Not able to contact the OLS. Make sure that you are online and try again.\n" +
                            "You may also want to check your firewall settings.",
                            "OLS Not Available", JOptionPane.ERROR_MESSAGE);
                    Util.writeToErrorLog("Error when trying to access OLS: ");
                    ex.printStackTrace();
                } catch (ServiceException ex) {
                    JOptionPane.showMessageDialog(
                            this, "Not able to contact the OLS. Make sure that you are online and try again.\n" +
                            "You may also want to check your firewall settings.",
                            "OLS Not Available", JOptionPane.ERROR_MESSAGE);
                    Util.writeToErrorLog("Error when trying to access OLS: ");
                    ex.printStackTrace();
                }
            }
        }

        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_olsResultsJTableMouseClicked

    /**
     * Closes the dialog.
     * 
     * @param evt
     */
    private void cancelJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelJButtonActionPerformed
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_cancelJButtonActionPerformed

    /**
     * Opens a help frame.
     * 
     * @param evt
     */
    private void helpJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpJButtonActionPerformed
        setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        new HelpWindow(this, getClass().getResource("/no/uib/prideconverter/helpfiles/OLSDialog.html"));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_helpJButtonActionPerformed

    /**
     * Opens an About frame.
     * 
     * @param evt
     */
    private void aboutJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutJButtonActionPerformed
        setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        new HelpWindow(this, getClass().getResource("/no/uib/prideconverter/helpfiles/AboutOLS.html"));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_aboutJButtonActionPerformed

    /**
     * Opens a web browser showing the graph version of the currently selected 
     * ontology.
     * 
     * @param evt
     */
    private void olsGraphSearchJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_olsGraphSearchJButtonActionPerformed
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

        try {
            String ontologyLong = ((String) ontologyJComboBox.getSelectedItem());
            String ontologyShort = ontologyLong.substring(ontologyLong.lastIndexOf("[") +
                    1, ontologyLong.length() - 1);

            Desktop.getDesktop().browse(new URI("http://www.ebi.ac.uk/ontology-lookup/browse.do?ontName=" +
                    ontologyShort));
        } catch (URISyntaxException ex) {
            JOptionPane.showMessageDialog(
                    this, "Not able to open the OLS web page. Make sure that you are online and try again.\n" +
                    "You may also want to check your firewall settings.",
                    "OLS Not Available", JOptionPane.ERROR_MESSAGE);
            Util.writeToErrorLog("Error when trying to open OLS web page: ");
            ex.printStackTrace();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    this, "Not able to open the OLS web page. Make sure that you are online and try again.\n" +
                    "You may also want to check your firewall settings.",
                    "OLS Not Available", JOptionPane.ERROR_MESSAGE);
            Util.writeToErrorLog("Error when trying to open OLS web page: ");
            ex.printStackTrace();
        }

        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
}//GEN-LAST:event_olsGraphSearchJButtonActionPerformed

    /**
     * See olsResultsJTableMouseClicked
     * 
     * @param evt
     */
    private void olsResultsJTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_olsResultsJTableKeyReleased
        olsResultsJTableMouseClicked(null);
    }//GEN-LAST:event_olsResultsJTableKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton aboutJButton;
    private javax.swing.JButton cancelJButton;
    private javax.swing.JButton helpJButton;
    private javax.swing.JButton insertSelectedJButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField numberOfTermsJTextField;
    private javax.swing.JButton olsGraphSearchJButton;
    private javax.swing.JEditorPane olsResultsJEditorPane;
    private javax.swing.JTable olsResultsJTable;
    private javax.swing.JTextField olsSearchTextField;
    private javax.swing.JComboBox ontologyJComboBox;
    // End of variables declaration//GEN-END:variables
}
