package no.uib.prideconverter.gui;

import no.uib.prideconverter.PRIDEConverter;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import no.uib.prideconverter.util.Util;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshaller;
import uk.ac.ebi.pride.model.implementation.core.ReferenceImpl;
import uk.ac.ebi.pride.model.implementation.mzData.ContactImpl;
import uk.ac.ebi.pride.model.implementation.mzData.CvParamImpl;
import uk.ac.ebi.pride.xml.MzDataXMLUnmarshaller;

/**
 * This frame handles the experiment information (title and label) plus 
 * the contact and reference details.
 * 
 * @author Harald Barsnes
 * 
 * Created March 2008
 */
public class ExperimentProperties extends javax.swing.JFrame {

    private PRIDEConverter prideConverter;
    private Vector columnToolTips;
    private ProgressDialog progressDialog;

    /**
     * Opens a new ExperimentProperties frame, and inserts stored information.
     * 
     * @param prideConverter
     * @param location where to position the frame
     */
    public ExperimentProperties(PRIDEConverter prideConverter, Point location) {
        this.prideConverter = prideConverter;

        // sets the default wizard frame size
        this.setPreferredSize(new Dimension(prideConverter.getProperties().FRAME_WIDTH,
                prideConverter.getProperties().FRAME_HEIGHT));
        this.setSize(prideConverter.getProperties().FRAME_WIDTH,
                prideConverter.getProperties().FRAME_HEIGHT);
        this.setMaximumSize(new Dimension(prideConverter.getProperties().FRAME_WIDTH,
                prideConverter.getProperties().FRAME_HEIGHT));
        this.setMinimumSize(new Dimension(prideConverter.getProperties().FRAME_WIDTH,
                prideConverter.getProperties().FRAME_HEIGHT));

        initComponents();

        // alters the usage if TAB so that it can be used to move between fields
        KeyStroke ctrlTab = KeyStroke.getKeyStroke("ctrl TAB");
        KeyStroke tab = KeyStroke.getKeyStroke("TAB");
        Set set = new HashSet(experimentTitleJTextArea.getFocusTraversalKeys(
                KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        set.remove(ctrlTab);
        set.add(tab);
        experimentTitleJTextArea.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, set);
        experimentTitleJTextArea.getInputMap().put(ctrlTab, "insert-tab");

        // sets the icon of the frame
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().
                getResource("/no/uib/prideconverter/icons/prideConverter_16.GIF")));

        columnToolTips = new Vector();
        columnToolTips.add(null);
        columnToolTips.add("The reference tag");
        columnToolTips.add("PubMed ID");
        columnToolTips.add("Digital Object Identifier");

        experimentTitleJTextArea.setFont(new java.awt.Font("Tahoma", 0, 11));

        referencesJTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        referencesJTable.getTableHeader().setReorderingAllowed(false);
        contactsJTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        contactsJTable.getTableHeader().setReorderingAllowed(false);

        setTitle(prideConverter.getWizardName() + " " +
                prideConverter.getPrideConverterVersionNumber() + " - " + getTitle());

        contactsJTable.getColumn(" ").setMinWidth(40);
        contactsJTable.getColumn(" ").setMaxWidth(40);
        referencesJTable.getColumn(" ").setMaxWidth(40);
        referencesJTable.getColumn(" ").setMinWidth(40);

        insertStoredInformation();
        mandatoryFieldsCheck();

        setLocation(location);
        setVisible(true);

        // if the selected file is an mzData file some experiment properties are 
        // extracted from the file
        if (prideConverter.getProperties().getDataSource().equalsIgnoreCase("mzData") &&
                !prideConverter.getProperties().hasDataFileBeenLoaded()) {
            loadMzDataFile();
        }

//        else if (prideConverter.getProperties().getDataSource().equalsIgnoreCase("mzML") &&
//                !prideConverter.getProperties().hasMzMlFileBeenLoaded()) {
//            loadMzMlFile();
//        }
    }

    /**
     * Loads the mzData file. And extracts the contact details found and 
     * inserts them into the Contacts table.
     */
    private void loadMzDataFile() {

        progressDialog = new ProgressDialog(this, true);
        progressDialog.setTitle("Loading mzData. Please wait.");
        progressDialog.setString(null);
        progressDialog.setIntermidiate(true);

        final Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                progressDialog.setVisible(true);
            }
        }, "ProgressDialog");

        t.start();

        Thread t2 = new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    MzDataXMLUnmarshaller unmarshallerMzData = new MzDataXMLUnmarshaller();
                    prideConverter.getProperties().setMzDataFile(unmarshallerMzData.unMarshall(
                            new FileReader(prideConverter.getProperties().getSelectedSourceFiles().get(0))));
                    prideConverter.getProperties().setDataFileHasBeenLoaded(true);

                    prideConverter.getProperties().setContacts(
                            prideConverter.getProperties().getMzDataFile().getContactCollection());
                    insertStoredInformation();
                    mandatoryFieldsCheck();

                } catch (FileNotFoundException e) {

                    Util.writeToErrorLog("An error occured while trying to parse: " +
                            prideConverter.getProperties().getSelectedSourceFiles().get(0) + " " + e.toString());
                    e.printStackTrace();

                    JOptionPane.showMessageDialog(null,
                            "An error occured while trying to parse: " +
                            prideConverter.getProperties().getSelectedSourceFiles().get(0) + "\n\n" +
                            "See ../Properties/ErrorLog.txt for more details.\n" +
                            "The file can most likely not be converted to PRIDE XML.",
                            "Parsing Error",
                            JOptionPane.ERROR_MESSAGE);
                } catch (IOException e) {

                    Util.writeToErrorLog("An error occured while trying to parse: " +
                            prideConverter.getProperties().getSelectedSourceFiles().get(0) + " " + e.toString());
                    e.printStackTrace();

                    JOptionPane.showMessageDialog(null,
                            "An error occured while trying to parse: " +
                            prideConverter.getProperties().getSelectedSourceFiles().get(0) + "\n\n" +
                            "See ../Properties/ErrorLog.txt for more details.\n" +
                            "The file can most likely not be converted to PRIDE XML.",
                            "Parsing Error",
                            JOptionPane.ERROR_MESSAGE);
                }

                mandatoryFieldsCheck();
                progressDialog.setVisible(false);
                progressDialog.getParent().setVisible(true);
                progressDialog.getParent().requestFocus();
                progressDialog.dispose();

                experimentTitleJTextArea.requestFocus();
            }
        });

        t2.start();
    }

    /**
     * Loads the mzML file. And extracts the contact details found and 
     * inserts them into the Contacts table.
     */
    private void loadMzMLFile() {

        progressDialog = new ProgressDialog(this, true);
        progressDialog.setTitle("Loading mzData. Please wait.");
        progressDialog.setString(null);
        progressDialog.setIntermidiate(true);

        final Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                progressDialog.setVisible(true);
            }
        }, "ProgressDialog");

        t.start();

        Thread t2 = new Thread(new Runnable() {

            @Override
            public void run() {

//                try {


                if (!prideConverter.getProperties().hasDataFileBeenLoaded()) {
                    MzMLUnmarshaller unmarshallerMzMl = new MzMLUnmarshaller(
                            new File(prideConverter.getProperties().getSelectedSourceFiles().get(0)));
                    prideConverter.getProperties().setMzMlFile(unmarshallerMzMl.unmarshall());
                    prideConverter.getProperties().setDataFileHasBeenLoaded(true);
                }

//                    prideConverter.getProperties().getContacts()

//                    prideConverter.getProperties().setContacts(
//                            prideConverter.getProperties().getMzDataFile().getContactCollection());
                insertStoredInformation();
                mandatoryFieldsCheck();

//                } catch (FileNotFoundException e) {
//
////                    progressDialog.setVisible(false);
//
//                    Util.writeToErrorLog("An error occured while trying to parse: " +
//                            prideConverter.getProperties().getSelectedSourceFiles().get(0) + " " + e.toString());
//                    e.printStackTrace();
//
//                    JOptionPane.showMessageDialog(null,
//                            "An error occured while trying to parse: " +
//                            prideConverter.getProperties().getSelectedSourceFiles().get(0) + "\n\n" +
//                            "See ../Properties/ErrorLog.txt for more details.\n" +
//                            "The file can most likely not be converted to PRIDE XML.",
//                            "Parsing Error",
//                            JOptionPane.ERROR_MESSAGE);
//                } catch (IOException e) {
//
////                    progressDialog.setVisible(false);
//
//                    Util.writeToErrorLog("An error occured while trying to parse: " +
//                            prideConverter.getProperties().getSelectedSourceFiles().get(0) + " " + e.toString());
//                    e.printStackTrace();
//
//                    JOptionPane.showMessageDialog(null,
//                            "An error occured while trying to parse: " +
//                            prideConverter.getProperties().getSelectedSourceFiles().get(0) + "\n\n" +
//                            "See ../Properties/ErrorLog.txt for more details.\n" +
//                            "The file can most likely not be converted to PRIDE XML.",
//                            "Parsing Error",
//                            JOptionPane.ERROR_MESSAGE);
//                }

                mandatoryFieldsCheck();
                progressDialog.setVisible(false);
                progressDialog.getParent().setVisible(true);
                progressDialog.getParent().requestFocus();
                progressDialog.dispose();

                experimentTitleJTextArea.requestFocus();
            }
        });

        t2.start();
    }

    /**
     * Retrieves and inserts the stored information.
     */
    private void insertStoredInformation() {
        experimentTitleJTextArea.setText(prideConverter.getProperties().getExperimentTitle());
        experimentLabelJTextField.setText(prideConverter.getProperties().getExperimentLabel());
        projectJTextField.setText(prideConverter.getProperties().getExperimentProject());

        Iterator contacts = prideConverter.getProperties().getContacts().iterator();

        ContactImpl tempContact;

        while (contactsJTable.getRowCount() > 0) {
            ((DefaultTableModel) contactsJTable.getModel()).removeRow(0);
        }

        while (contacts.hasNext()) {
            tempContact = (ContactImpl) contacts.next();

            ((DefaultTableModel) contactsJTable.getModel()).addRow(
                    new Object[]{
                new Integer(contactsJTable.getRowCount() + 1),
                tempContact.getContactName(),
                tempContact.getContactInfo(),
                tempContact.getInstitution(),
                new ImageIcon("copy16.gif"),
                new ImageIcon("copy16.gif")
            });
        }

        Iterator ids;
        Iterator refs = prideConverter.getProperties().getReferences().iterator();

        while (referencesJTable.getRowCount() > 0) {
            ((DefaultTableModel) referencesJTable.getModel()).removeRow(0);
        }

        CvParamImpl temp;
        ReferenceImpl tempRef;
        String pubMed, doi;

        while (refs.hasNext()) {
            tempRef = ((ReferenceImpl) refs.next());

            if (tempRef.getReferenceCvParameterList() != null) {
                ids = tempRef.getReferenceCvParameterList().iterator();

                pubMed = null;
                doi = null;

                if (ids.hasNext()) {

                    temp = (CvParamImpl) ids.next();

                    if (temp.getCVLookup().equalsIgnoreCase("PubMed")) {
                        pubMed = temp.getName();
                    } else if (temp.getCVLookup().equalsIgnoreCase("DOI")) {
                        doi = temp.getName();
                    }
                }

                if (ids.hasNext()) {

                    temp = (CvParamImpl) ids.next();

                    if (temp.getCVLookup().equalsIgnoreCase("PubMed")) {
                        pubMed = temp.getName();
                    } else if (temp.getCVLookup().equalsIgnoreCase("DOI")) {
                        doi = temp.getName();
                    }
                }

                ((DefaultTableModel) referencesJTable.getModel()).addRow(new Object[]{
                    new Integer(referencesJTable.getRowCount() + 1),
                    tempRef.getReferenceLine(),
                    pubMed,
                    doi
                });
            } else {
                ((DefaultTableModel) referencesJTable.getModel()).addRow(new Object[]{
                    new Integer(referencesJTable.getRowCount() + 1),
                    tempRef.getReferenceLine(),
                    null,
                    null
                });
            }
        }
    }

    /**
     * Returns a reference the PRIDEConverter.
     * 
     * @return a reference the PRIDEConverter
     */
    public PRIDEConverter getPRIDEConverter_ref() {
        return prideConverter;
    }

    /**
     * Checks if all mandatory information is filled in and enables or disables 
     * the Next button.
     */
    private void mandatoryFieldsCheck() {
        if (experimentTitleJTextArea.getText().length() > 0 &&
                experimentLabelJTextField.getText().length() > 0 &&
                contactsJTable.getRowCount() > 0) {
            nextJButton.setEnabled(true);
        } else {
            nextJButton.setEnabled(false);
        }
    }

    /**
     * Returne true if the contact already is inserted into the table, false 
     * otherwise.
     * 
     * @param contactName
     * @return true if the contact already is inserted into the table, false 
     * otherwise
     */
    public boolean contactAlreadyInTable(String contactName) {
        boolean alreadyInTable = false;

        for (int i = 0; i < contactsJTable.getRowCount() && !alreadyInTable; i++) {
            if (contactName.equalsIgnoreCase((String) contactsJTable.getValueAt(i, 1))) {
                alreadyInTable = true;
            }
        }

        if (alreadyInTable) {
            JOptionPane.showMessageDialog(this, "This contact is already in the table.",
                    "Contact Already Added", JOptionPane.ERROR_MESSAGE);
        }

        return alreadyInTable;
    }

    /**
     * Adds the given contact to the contact table. If selectedRow is -1 a new 
     * row is added, otherwise the contact at the indicated row is modified.
     * 
     * @param contact
     * @param selectedRow the row to modify, set to -1 to add new row
     */
    public void addContact(ContactImpl contact, int selectedRow) {

        if (selectedRow == -1) {
            ((DefaultTableModel) contactsJTable.getModel()).addRow(
                    new Object[]{
                new Integer(contactsJTable.getRowCount() + 1),
                contact.getContactName(),
                contact.getContactInfo(),
                contact.getInstitution()
            });
        } else {
            contactsJTable.setValueAt(contact.getInstitution(), selectedRow, 3);
            contactsJTable.setValueAt(contact.getContactName(), selectedRow, 1);
            contactsJTable.setValueAt(contact.getContactInfo(), selectedRow, 2);
        }

        mandatoryFieldsCheck();
    }

    /**
     * Adds a reference to the reference table. If selectedRow is -1 a new 
     * row is added, otherwise the reference at the indicated row is modified.
     * 
     * @param reference the reference tag
     * @param pmid PubMed ID
     * @param doi Digital Object Identifier
     * @param selectedRow the row to modify, set to -1 to add new row
     */
    public void addReference(String reference, String pmid, String doi, int selectedRow) {

        Collection tempCollection = new ArrayList(2);

        int counter = 0;

        if (pmid.length() > 0) {
            tempCollection.add(new CvParamImpl(
                    pmid,
                    "PubMed",
                    pmid,
                    new Long(counter++),
                    ""));
        }

        if (doi.length() > 0) {
            tempCollection.add(new CvParamImpl(
                    doi,
                    "DOI",
                    doi,
                    new Long(counter),
                    ""));
        }

        if (pmid.length() == 0 && doi.length() == 0) {
            tempCollection = null;
        }

        if (selectedRow == -1) {
            ((DefaultTableModel) referencesJTable.getModel()).addRow(new Object[]{
                new Integer(referencesJTable.getRowCount() + 1),
                reference,
                pmid,
                doi
            });

            prideConverter.getProperties().getReferences().add(new ReferenceImpl(
                    reference,
                    tempCollection,
                    null));

        } else {
            referencesJTable.setValueAt(reference, selectedRow, 1);
            referencesJTable.setValueAt(pmid, selectedRow, 2);
            referencesJTable.setValueAt(doi, selectedRow, 3);

            Object[] refs = prideConverter.getProperties().getReferences().toArray();

            ReferenceImpl tempRef = new ReferenceImpl(
                    reference,
                    tempCollection,
                    null);

            refs[selectedRow] = tempRef;

            prideConverter.getProperties().setReferences(new ArrayList(referencesJTable.getRowCount()));

            for (int i = 0; i < refs.length; i++) {
                prideConverter.getProperties().getReferences().add(refs[i]);
            }
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        contactsPopupJMenu = new javax.swing.JPopupMenu();
        conEditJMenuItem = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        conMoveUpJMenuItem = new javax.swing.JMenuItem();
        conMoveDownJMenuItem = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        conDeleteSelectedRowJMenuItem = new javax.swing.JMenuItem();
        referencesPopupJMenu = new javax.swing.JPopupMenu();
        refEditJMenuItem = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JSeparator();
        refMoveUpJMenuItem = new javax.swing.JMenuItem();
        refMoveDownJMenuItem = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JSeparator();
        refDeleteSelectedRowJMenuItem = new javax.swing.JMenuItem();
        nextJButton = new javax.swing.JButton();
        backJButton = new javax.swing.JButton();
        cancelJButton = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        experimentLabelJTextField = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        experimentTitleJTextArea = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        projectJTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        contactsJTable = new javax.swing.JTable();
        addContactJButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        referencesJTable = new JTable() {
            protected JTableHeader createDefaultTableHeader() {
                return new JTableHeader(columnModel) {
                    public String getToolTipText(MouseEvent e) {
                        String tip = null;
                        java.awt.Point p = e.getPoint();
                        int index = columnModel.getColumnIndexAtX(p.x);
                        int realIndex = columnModel.getColumn(index).getModelIndex();
                        tip = (String) columnToolTips.get(realIndex);
                        return tip;
                    }
                };
            }
        };
        addRowJButton = new javax.swing.JButton();
        helpJButton = new javax.swing.JButton();
        aboutJButton = new javax.swing.JButton();

        conEditJMenuItem.setMnemonic('E');
        conEditJMenuItem.setText("Edit");
        conEditJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                conEditJMenuItemActionPerformed(evt);
            }
        });
        contactsPopupJMenu.add(conEditJMenuItem);
        contactsPopupJMenu.add(jSeparator3);

        conMoveUpJMenuItem.setMnemonic('U');
        conMoveUpJMenuItem.setText("Move Up");
        conMoveUpJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                conMoveUpJMenuItemActionPerformed(evt);
            }
        });
        contactsPopupJMenu.add(conMoveUpJMenuItem);

        conMoveDownJMenuItem.setMnemonic('D');
        conMoveDownJMenuItem.setText("Move Down");
        conMoveDownJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                conMoveDownJMenuItemActionPerformed(evt);
            }
        });
        contactsPopupJMenu.add(conMoveDownJMenuItem);
        contactsPopupJMenu.add(jSeparator4);

        conDeleteSelectedRowJMenuItem.setMnemonic('L');
        conDeleteSelectedRowJMenuItem.setText("Delete");
        conDeleteSelectedRowJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                conDeleteSelectedRowJMenuItemActionPerformed(evt);
            }
        });
        contactsPopupJMenu.add(conDeleteSelectedRowJMenuItem);

        refEditJMenuItem.setMnemonic('E');
        refEditJMenuItem.setText("Edit");
        refEditJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refEditJMenuItemActionPerformed(evt);
            }
        });
        referencesPopupJMenu.add(refEditJMenuItem);
        referencesPopupJMenu.add(jSeparator5);

        refMoveUpJMenuItem.setMnemonic('U');
        refMoveUpJMenuItem.setText("Move Up");
        refMoveUpJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refMoveUpJMenuItemActionPerformed(evt);
            }
        });
        referencesPopupJMenu.add(refMoveUpJMenuItem);

        refMoveDownJMenuItem.setMnemonic('D');
        refMoveDownJMenuItem.setText("Move Down");
        refMoveDownJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refMoveDownJMenuItemActionPerformed(evt);
            }
        });
        referencesPopupJMenu.add(refMoveDownJMenuItem);
        referencesPopupJMenu.add(jSeparator6);

        refDeleteSelectedRowJMenuItem.setMnemonic('L');
        refDeleteSelectedRowJMenuItem.setText("Delete");
        refDeleteSelectedRowJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refDeleteSelectedRowJMenuItemActionPerformed(evt);
            }
        });
        referencesPopupJMenu.add(refDeleteSelectedRowJMenuItem);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Experiment Properties - Step 3 of 8");
        setMinimumSize(new java.awt.Dimension(580, 560));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        nextJButton.setText("Next  >");
        nextJButton.setEnabled(false);
        nextJButton.setPreferredSize(new java.awt.Dimension(81, 23));
        nextJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextJButtonActionPerformed(evt);
            }
        });

        backJButton.setText("< Back");
        backJButton.setPreferredSize(new java.awt.Dimension(81, 23));
        backJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backJButtonActionPerformed(evt);
            }
        });

        cancelJButton.setText("Cancel");
        cancelJButton.setPreferredSize(new java.awt.Dimension(81, 23));
        cancelJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelJButtonActionPerformed(evt);
            }
        });

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Experiment Properties", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(0, 0, 0)));

        jLabel19.setText("Title:");

        jLabel20.setText("Label:");

        experimentLabelJTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                experimentLabelJTextFieldKeyReleased(evt);
            }
        });

        experimentTitleJTextArea.setColumns(20);
        experimentTitleJTextArea.setLineWrap(true);
        experimentTitleJTextArea.setRows(2);
        experimentTitleJTextArea.setWrapStyleWord(true);
        experimentTitleJTextArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                experimentTitleJTextAreaKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(experimentTitleJTextArea);

        jLabel1.setText("Project:");
        jLabel1.setToolTipText("Allows experiments to be grouped or organised under projects");

        projectJTextField.setToolTipText("Allows experiments to be grouped or organised under projects");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel20)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 482, Short.MAX_VALUE)
                    .addComponent(experimentLabelJTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 482, Short.MAX_VALUE)
                    .addComponent(projectJTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 482, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel19))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(experimentLabelJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(projectJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(6, Short.MAX_VALUE))
        );

        jLabel3.setFont(new java.awt.Font("Tahoma", 2, 11));
        jLabel3.setText("Insert the experiment properties, minimum one contact, references (if any) and click on 'Next' to continue.");

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Contact Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(0, 0, 0)));

        contactsJTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                " ", "Name", "E-mail", "Institution"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        contactsJTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                contactsJTableMouseClicked(evt);
            }
        });
        contactsJTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                contactsJTableKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(contactsJTable);

        addContactJButton.setText("Add Contact");
        addContactJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addContactJButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(addContactJButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 524, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 524, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addContactJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "References", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(0, 0, 0)));

        referencesJTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                " ", "Reference", "PMID", "DOI"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        referencesJTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                referencesJTableMouseClicked(evt);
            }
        });
        referencesJTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                referencesJTableKeyReleased(evt);
            }
        });
        jScrollPane3.setViewportView(referencesJTable);

        addRowJButton.setText("Add Reference");
        addRowJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addRowJButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(addRowJButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 524, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 524, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addRowJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        helpJButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/no/uib/prideconverter/icons/help.GIF"))); // NOI18N
        helpJButton.setToolTipText("Help");
        helpJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpJButtonActionPerformed(evt);
            }
        });

        aboutJButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/no/uib/prideconverter/icons/prideConverter_16.GIF"))); // NOI18N
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
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(helpJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(aboutJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 239, Short.MAX_VALUE)
                        .addComponent(backJButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nextJButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cancelJButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator2, javax.swing.GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE)
                    .addComponent(jLabel3)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cancelJButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(backJButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(nextJButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(helpJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(aboutJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    /**
     * Opens a NewContact dialog, where information about a new contact 
     * can be given.
     * 
     * @param evt
     */
    private void conEditJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_conEditJMenuItemActionPerformed
        int selectedRow = contactsJTable.getSelectedRow();

        new NewContact(this, true, selectedRow,
                (String) contactsJTable.getValueAt(selectedRow, 1));
    }//GEN-LAST:event_conEditJMenuItemActionPerformed

    /**
     * Moves the selected row in the contact table up one position.
     * 
     * @param evt
     */
    private void conMoveUpJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_conMoveUpJMenuItemActionPerformed
        int selectedRow = contactsJTable.getSelectedRow();
        int selectedColumn = contactsJTable.getSelectedColumn();

        Object[] tempRow = new Object[]{
            contactsJTable.getValueAt(selectedRow - 1, 0),
            contactsJTable.getValueAt(selectedRow - 1, 1),
            contactsJTable.getValueAt(selectedRow - 1, 2),
            contactsJTable.getValueAt(selectedRow - 1, 3)
        };

        ((DefaultTableModel) contactsJTable.getModel()).removeRow(selectedRow - 1);
        ((DefaultTableModel) contactsJTable.getModel()).insertRow(selectedRow, tempRow);

        contactsJTable.changeSelection(selectedRow - 1, selectedColumn, false, false);

        fixContactIndices();
    }//GEN-LAST:event_conMoveUpJMenuItemActionPerformed

    /**
     * Moves the selected row in the contact table down one position.
     * 
     * @param evt
     */
    private void conMoveDownJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_conMoveDownJMenuItemActionPerformed
        int selectedRow = contactsJTable.getSelectedRow();
        int selectedColumn = contactsJTable.getSelectedColumn();

        Object[] tempRow = new Object[]{
            contactsJTable.getValueAt(selectedRow + 1, 0),
            contactsJTable.getValueAt(selectedRow + 1, 1),
            contactsJTable.getValueAt(selectedRow + 1, 2),
            contactsJTable.getValueAt(selectedRow + 1, 3)
        };

        ((DefaultTableModel) contactsJTable.getModel()).removeRow(selectedRow + 1);
        ((DefaultTableModel) contactsJTable.getModel()).insertRow(selectedRow, tempRow);

        contactsJTable.changeSelection(selectedRow + 1, selectedColumn, false, false);

        fixContactIndices();
    }//GEN-LAST:event_conMoveDownJMenuItemActionPerformed

    /**
     * Delets the selected row in the contacts table.
     * 
     * @param evt
     */
    private void conDeleteSelectedRowJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_conDeleteSelectedRowJMenuItemActionPerformed
        if (contactsJTable.getSelectedRow() != -1) {

            int selectedRow = contactsJTable.getSelectedRow();
            int selectedColumn = contactsJTable.getSelectedColumn();

            int[] selectedRows = contactsJTable.getSelectedRows();

            for (int i = contactsJTable.getSelectedRows().length - 1; i >= 0; i--) {
                ((DefaultTableModel) contactsJTable.getModel()).removeRow(selectedRows[i]);
            }

            contactsJTable.changeSelection(selectedRow, selectedColumn, false, false);
            contactsJTable.editingCanceled(null);

            mandatoryFieldsCheck();
            fixContactIndices();
        }
    }//GEN-LAST:event_conDeleteSelectedRowJMenuItemActionPerformed

    /**
     * Delets the selected row in the reference table.
     * 
     * @param evt
     */
    private void refDeleteSelectedRowJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refDeleteSelectedRowJMenuItemActionPerformed
        if (referencesJTable.getSelectedRow() != -1) {

            int selectedRow = referencesJTable.getSelectedRow();
            int selectedColumn = referencesJTable.getSelectedColumn();

            int[] selectedRows = referencesJTable.getSelectedRows();

            for (int i = referencesJTable.getSelectedRows().length - 1; i >= 0; i--) {
                ((DefaultTableModel) referencesJTable.getModel()).removeRow(selectedRows[i]);
            }

            referencesJTable.changeSelection(selectedRow, selectedColumn, false, false);
            referencesJTable.editingCanceled(null);

            //remove from datastructure as well
            Object[] refs = prideConverter.getProperties().getReferences().toArray();

            prideConverter.getProperties().setReferences(new ArrayList(refs.length));

            for (int i = 0; i < refs.length; i++) {
                if (i != selectedRow) {
                    prideConverter.getProperties().getReferences().add(refs[i]);
                }
            }

            fixReferenceIndices();
        }
    }//GEN-LAST:event_refDeleteSelectedRowJMenuItemActionPerformed

    /**
     * Moves the selected row in the reference table down one position.
     * 
     * @param evt
     */
    private void refMoveDownJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refMoveDownJMenuItemActionPerformed
        int selectedRow = referencesJTable.getSelectedRow();
        int selectedColumn = referencesJTable.getSelectedColumn();

        Object[] tempRow = new Object[]{
            referencesJTable.getValueAt(selectedRow + 1, 0),
            referencesJTable.getValueAt(selectedRow + 1, 1),
            referencesJTable.getValueAt(selectedRow + 1, 2),
            referencesJTable.getValueAt(selectedRow + 1, 3)
        };

        ((DefaultTableModel) referencesJTable.getModel()).removeRow(selectedRow + 1);
        ((DefaultTableModel) referencesJTable.getModel()).insertRow(selectedRow, tempRow);

        referencesJTable.changeSelection(selectedRow + 1, selectedColumn, false, false);

        fixReferenceIndices();

        //move in data structure as well
        Object[] refs = prideConverter.getProperties().getReferences().toArray();

        Object tempRef = refs[selectedRow + 1];
        refs[selectedRow + 1] = refs[selectedRow];
        refs[selectedRow] = tempRef;

        prideConverter.getProperties().setReferences(new ArrayList(refs.length));

        for (int i = 0; i < refs.length; i++) {
            prideConverter.getProperties().getReferences().add(refs[i]);
        }
    }//GEN-LAST:event_refMoveDownJMenuItemActionPerformed

    /**
     * Moves the selected row in the reference table up one position.
     * 
     * @param evt
     */
    private void refMoveUpJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refMoveUpJMenuItemActionPerformed
        int selectedRow = referencesJTable.getSelectedRow();
        int selectedColumn = referencesJTable.getSelectedColumn();

        Object[] tempRow = new Object[]{
            referencesJTable.getValueAt(selectedRow - 1, 0),
            referencesJTable.getValueAt(selectedRow - 1, 1),
            referencesJTable.getValueAt(selectedRow - 1, 2),
            referencesJTable.getValueAt(selectedRow - 1, 3)
        };

        ((DefaultTableModel) referencesJTable.getModel()).removeRow(selectedRow - 1);
        ((DefaultTableModel) referencesJTable.getModel()).insertRow(selectedRow, tempRow);

        referencesJTable.changeSelection(selectedRow - 1, selectedColumn, false, false);

        fixReferenceIndices();

        //move in data structure as well
        Object[] refs = prideConverter.getProperties().getReferences().toArray();

        Object tempRef = refs[selectedRow - 1];
        refs[selectedRow - 1] = refs[selectedRow];
        refs[selectedRow] = tempRef;

        prideConverter.getProperties().setReferences(new ArrayList(refs.length));

        for (int i = 0; i < refs.length; i++) {
            prideConverter.getProperties().getReferences().add(refs[i]);
        }
    }//GEN-LAST:event_refMoveUpJMenuItemActionPerformed

    /**
     * Opens a NewReference dialog where the information about the selected 
     * reference can be altered.
     * 
     * @param evt
     */
    private void refEditJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refEditJMenuItemActionPerformed

        Object[] refs = prideConverter.getProperties().getReferences().toArray();

        new NewReference(this, true,
                referencesJTable.getSelectedRow(),
                (ReferenceImpl) refs[referencesJTable.getSelectedRow()]);
    }//GEN-LAST:event_refEditJMenuItemActionPerformed

    /**
     * If the user double clicks on a row in the contact table the NewContact 
     * dialog is shown where the information about the given contact can be 
     * altered. If the user right clicks a pop up menu is shown for editing, 
     * moving or delting the selected contact.
     * 
     * @param evt
     */
    private void contactsJTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_contactsJTableMouseClicked

        if (evt.getButton() == 1 && evt.getClickCount() == 2) {

            conEditJMenuItemActionPerformed(null);

        } else if (evt.getButton() == 3) {

            int row = contactsJTable.rowAtPoint(evt.getPoint());
            int column = contactsJTable.columnAtPoint(evt.getPoint());
            int[] selectedRows = contactsJTable.getSelectedRows();
            int[] selectedColumns = contactsJTable.getSelectedColumns();

            boolean changeRow = true;

            for (int i = 0; i < selectedRows.length && changeRow; i++) {
                if (row == selectedRows[i]) {
                    changeRow = false;
                }
            }

            if (changeRow) {
                contactsJTable.changeSelection(row, column, false, false);
            }

            //makes sure that only valid "moving options" are enabled
            conMoveUpJMenuItem.setEnabled(true);
            conMoveDownJMenuItem.setEnabled(true);

            if (row == contactsJTable.getRowCount() - 1) {
                conMoveDownJMenuItem.setEnabled(false);
            }

            if (row == 0) {
                conMoveUpJMenuItem.setEnabled(false);
            }

            contactsPopupJMenu.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_contactsJTableMouseClicked

    /**
     * Opens a NewReference dialog where a new reference can be created.
     * 
     * @param evt
     */
    private void addRowJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addRowJButtonActionPerformed
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        new NewReference(this, true);
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_addRowJButtonActionPerformed

    /**
     * If the user double clicks on a row in the contact table the NewReference 
     * dialog is shown where the information about the given reference can be 
     * altered. If the user right clicks a pop up menu is shown for editing, 
     * moving or delting the selected reference.
     * 
     * @param evt
     */
    private void referencesJTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_referencesJTableMouseClicked

        if (evt.getButton() == 1 && evt.getClickCount() == 2) {
            refEditJMenuItemActionPerformed(null);
        } else if (evt.getButton() == 3) {

            int row = referencesJTable.rowAtPoint(evt.getPoint());
            int column = referencesJTable.columnAtPoint(evt.getPoint());
            int[] selectedRows = referencesJTable.getSelectedRows();
            int[] selectedColumns = referencesJTable.getSelectedColumns();

            boolean changeRow = true;

            for (int i = 0; i < selectedRows.length && changeRow; i++) {
                if (row == selectedRows[i]) {
                    changeRow = false;
                }
            }

            if (changeRow) {
                referencesJTable.changeSelection(row, column, false, false);
            }

            //makes sure that only valid "moving options" are enabled
            this.refMoveUpJMenuItem.setEnabled(true);
            this.refMoveDownJMenuItem.setEnabled(true);

            if (row == referencesJTable.getRowCount() - 1) {
                this.refMoveDownJMenuItem.setEnabled(false);
            }

            if (row == 0) {
                this.refMoveUpJMenuItem.setEnabled(false);
            }

            referencesPopupJMenu.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_referencesJTableMouseClicked

    /**
     * Opens a NewContact dialog where a new contact can be created.
     * 
     * @param evt
     */
    private void addContactJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addContactJButtonActionPerformed
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        new NewContact(this, true);
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_addContactJButtonActionPerformed

    /**
     * Stores the inserted information and opens the frame of the next step 
     * (SampleDetails).
     * 
     * @param evt
     */
    private void nextJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextJButtonActionPerformed
        saveInsertedInformation();
        new SampleDetails(prideConverter, this.getLocation());
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_nextJButtonActionPerformed

    /**
     * Closes the frame and the wizard.
     * 
     * @param evt
     */
    private void cancelJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelJButtonActionPerformed
        this.setVisible(false);
        this.dispose();
        prideConverter.cancelConvertion();
    }//GEN-LAST:event_cancelJButtonActionPerformed

    /**
     * Stores the inserted information and opens the frame of the previous step 
     * (ProjectSelection).
     * 
     * @param evt
     */
    private void backJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backJButtonActionPerformed

        saveInsertedInformation();

        if (prideConverter.getProperties().getDataSource().equalsIgnoreCase("Sequest DTA File") ||
                prideConverter.getProperties().getDataSource().equalsIgnoreCase("Micromass PKL File") ||
                prideConverter.getProperties().getDataSource().equalsIgnoreCase("mzXML") ||
                prideConverter.getProperties().getDataSource().equalsIgnoreCase("mzML") ||
                prideConverter.getProperties().getDataSource().equalsIgnoreCase("mzData") ||
                prideConverter.getProperties().getDataSource().equalsIgnoreCase("VEMS") ||
                prideConverter.getProperties().getDataSource().equalsIgnoreCase("Mascot Generic File") ||
                prideConverter.getProperties().getDataSource().equalsIgnoreCase("MS2") ||
                prideConverter.getProperties().getDataSource().equalsIgnoreCase("TPP")) {
            new SpectraSelectionNoIdentifications(prideConverter, this.getLocation());
        } else if (prideConverter.getProperties().getDataSource().equalsIgnoreCase("X!Tandem") ||
                prideConverter.getProperties().getDataSource().equalsIgnoreCase("Spectrum Mill") ||
                prideConverter.getProperties().getDataSource().equalsIgnoreCase("Sequest Result File") ||
                prideConverter.getProperties().getDataSource().equalsIgnoreCase("OMSSA") ||
                prideConverter.getProperties().getDataSource().equalsIgnoreCase("ms_lims") ||
                prideConverter.getProperties().getDataSource().equalsIgnoreCase("Mascot Dat File")) {
            new SpectraSelectionWithIdentifications(prideConverter, this.getLocation());
        }

        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_backJButtonActionPerformed

    /**
     * See mandatoryFieldsCheck
     * 
     * @param evt
     */
    private void experimentTitleJTextAreaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_experimentTitleJTextAreaKeyReleased
        mandatoryFieldsCheck();
    }//GEN-LAST:event_experimentTitleJTextAreaKeyReleased

    /**
     * See mandatoryFieldsCheck
     * 
     * @param evt
     */
    private void experimentLabelJTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_experimentLabelJTextFieldKeyReleased
        mandatoryFieldsCheck();
    }//GEN-LAST:event_experimentLabelJTextFieldKeyReleased

    /**
     * See cancelJButtonActionPerformed
     * 
     * @param evt
     */
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        cancelJButtonActionPerformed(null);
    }//GEN-LAST:event_formWindowClosing

    /**
     * Opens a Help dialog.
     * 
     * @param evt
     */
    private void helpJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpJButtonActionPerformed
        setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        new HelpWindow(this, getClass().getResource("/no/uib/prideconverter/helpfiles/ExperimentProperties.html"));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_helpJButtonActionPerformed

    /**
     * Opens an About PRIDE Converter dialog.
     * 
     * @param evt
     */
    private void aboutJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutJButtonActionPerformed
        setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        new HelpWindow(this, getClass().getResource("/no/uib/prideconverter/helpfiles/AboutPRIDE_Converter.html"));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_aboutJButtonActionPerformed

    /**
     * If the Delete button is used the selected rows int the contacts table
     * are removed.
     * 
     * @param evt
     */
    private void contactsJTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_contactsJTableKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            conDeleteSelectedRowJMenuItemActionPerformed(null);
        }
    }//GEN-LAST:event_contactsJTableKeyReleased

    /**
     * If the Delete button is used the selected rows int the reference table
     * are removed.
     * 
     * @param evt
     */
    private void referencesJTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_referencesJTableKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            refDeleteSelectedRowJMenuItemActionPerformed(null);
        }
    }//GEN-LAST:event_referencesJTableKeyReleased

    /**
     * Fixes the indices in the contacts table so that they are in accending 
     * order starting from one.
     */
    private void fixContactIndices() {
        for (int row = 0; row < ((DefaultTableModel) contactsJTable.getModel()).getRowCount(); row++) {
            ((DefaultTableModel) contactsJTable.getModel()).setValueAt(new Integer(row + 1), row, 0);
        }
    }

    /**
     * Fixes the indices in the reference table so that they are in accending 
     * order starting from one.
     */
    private void fixReferenceIndices() {
        for (int row = 0; row < ((DefaultTableModel) referencesJTable.getModel()).getRowCount(); row++) {
            ((DefaultTableModel) referencesJTable.getModel()).setValueAt(new Integer(row + 1), row, 0);
        }
    }

    /**
     * Saves the inserted information to the Properties object.
     */
    private void saveInsertedInformation() {

        this.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

        prideConverter.getProperties().setExperimentTitle(experimentTitleJTextArea.getText());
        prideConverter.getProperties().setExperimentLabel(experimentLabelJTextField.getText());
        prideConverter.getProperties().setExperimentProject(projectJTextField.getText());

        prideConverter.getProperties().setContacts(new ArrayList(contactsJTable.getRowCount()));

        for (int i = 0; i < contactsJTable.getRowCount(); i++) {
            prideConverter.getProperties().getContacts().add(new ContactImpl(
                    (String) contactsJTable.getValueAt(i, 3),
                    (String) contactsJTable.getValueAt(i, 1),
                    (String) contactsJTable.getValueAt(i, 2)));
        }

        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton aboutJButton;
    private javax.swing.JButton addContactJButton;
    private javax.swing.JButton addRowJButton;
    private javax.swing.JButton backJButton;
    private javax.swing.JButton cancelJButton;
    private javax.swing.JMenuItem conDeleteSelectedRowJMenuItem;
    private javax.swing.JMenuItem conEditJMenuItem;
    private javax.swing.JMenuItem conMoveDownJMenuItem;
    private javax.swing.JMenuItem conMoveUpJMenuItem;
    private javax.swing.JTable contactsJTable;
    private javax.swing.JPopupMenu contactsPopupJMenu;
    private javax.swing.JTextField experimentLabelJTextField;
    private javax.swing.JTextArea experimentTitleJTextArea;
    private javax.swing.JButton helpJButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JButton nextJButton;
    private javax.swing.JTextField projectJTextField;
    private javax.swing.JMenuItem refDeleteSelectedRowJMenuItem;
    private javax.swing.JMenuItem refEditJMenuItem;
    private javax.swing.JMenuItem refMoveDownJMenuItem;
    private javax.swing.JMenuItem refMoveUpJMenuItem;
    private javax.swing.JTable referencesJTable;
    private javax.swing.JPopupMenu referencesPopupJMenu;
    // End of variables declaration//GEN-END:variables
}
