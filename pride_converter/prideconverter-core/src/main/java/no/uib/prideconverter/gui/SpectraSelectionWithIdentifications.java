package no.uib.prideconverter.gui;

import be.proteomics.lims.db.accessors.Identification;
import be.proteomics.lims.db.accessors.Spectrumfile;
import be.proteomics.mascotdatfile.util.interfaces.MascotDatfileInf;
import be.proteomics.mascotdatfile.util.interfaces.QueryToPeptideMapInf;
import be.proteomics.mascotdatfile.util.mascot.PeptideHit;
import be.proteomics.mascotdatfile.util.mascot.Query;
import be.proteomics.mascotdatfile.util.mascot.enumeration.MascotDatfileType;
import be.proteomics.mascotdatfile.util.mascot.factory.MascotDatfileFactory;
import be.proteomics.mascotdatfile.util.mascot.iterator.QueryEnumerator;
import de.proteinms.omxparser.OmssaOmxFile;
import de.proteinms.omxparser.util.MSHitSet;
import de.proteinms.omxparser.util.MSSpectrum;
import no.uib.prideconverter.PRIDEConverter;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import no.uib.prideconverter.util.Util;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This frame handles the spectra selection for all the dataformats with identifications
 * (e.g., X!Tandem, OMSSA, Spectrum Mill and Sequest Result Files).
 * 
 * @author Harald Barsnes
 * 
 * Created March 2008
 */
public class SpectraSelectionWithIdentifications extends javax.swing.JFrame {

    private PRIDEConverter prideConverter;
    private int numberOfSelectedSpectra;
    private boolean selectAll;
    private File file;
    private FileReader f;
    private BufferedReader b;
    private String currentLine;
    private StringTokenizer tok;
    private Double precursorMass;
    private Integer precursorCharge;
    private ProgressDialog progressDialog;
    private String selectionCriteria;
    private Vector columnToolTips;

    /** 
     * Opens a new SpectraSelectionWithIdentifications frame, and inserts stored information.
     * 
     * @param prideConverter
     * @param location where to position the frame
     */
    public SpectraSelectionWithIdentifications(PRIDEConverter prideConverter, Point location) {

        this.prideConverter = prideConverter;

        this.setPreferredSize(new Dimension(prideConverter.getProperties().FRAME_WIDTH, 
                prideConverter.getProperties().FRAME_HEIGHT));
        this.setSize(prideConverter.getProperties().FRAME_WIDTH, 
                prideConverter.getProperties().FRAME_HEIGHT);
        this.setMaximumSize(new Dimension(prideConverter.getProperties().FRAME_WIDTH, 
                prideConverter.getProperties().FRAME_HEIGHT));
        this.setMinimumSize(new Dimension(prideConverter.getProperties().FRAME_WIDTH, 
                prideConverter.getProperties().FRAME_HEIGHT));

        initComponents();

        spectraJTable.getColumn("Selected").setMinWidth(80);
        spectraJTable.getColumn("Selected").setMaxWidth(80);
        spectraJTable.getColumn("Identified").setMinWidth(80);
        spectraJTable.getColumn("Identified").setMaxWidth(80);
        spectraJTable.getColumn("ID").setMaxWidth(50);
        spectraJTable.getColumn("ID").setMinWidth(50);
        spectraJTable.getColumn("PID").setMinWidth(50);
        spectraJTable.getColumn("PID").setMaxWidth(50);

        columnToolTips = new Vector();
        columnToolTips.add("Project Identification");
        columnToolTips.add("Filename of Spectrum File");
        columnToolTips.add("Spectrum Identification");
        columnToolTips.add(null);
        columnToolTips.add(null);

        spectraJTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        spectraJTable.getTableHeader().setReorderingAllowed(false);
        spectraJTable.setAutoCreateColumnsFromModel(false);
        spectraJTable.setAutoCreateRowSorter(true);

        selectAllSpectraJRadioButton.setSelected(prideConverter.getProperties().selectAllSpectra());
        selectIdentifiedJRadioButton.setSelected(prideConverter.getProperties().selectAllIdentifiedSpectra());

        peptideScoreJTextField.setText("" +
                prideConverter.getProperties().getPeptideScoreThreshold());

        if (prideConverter.getProperties().getSpectraSelectionCriteria() != null) {
            advancedSelectionJTextArea.setText(prideConverter.getProperties().getSpectraSelectionCriteria());
            selectAllSpectraJRadioButton.setEnabled(false);
            selectIdentifiedJRadioButton.setEnabled(false);
        }

        if (prideConverter.getProperties().isSelectionCriteriaFileName()) {
            fileNamesSelectionJRadioButton.setSelected(true);
            identificationIdsSelectionJRadioButton.setSelected(false);
        } else {
            fileNamesSelectionJRadioButton.setSelected(false);
            identificationIdsSelectionJRadioButton.setSelected(true);
        }

        mascotConfidenceLevelJSpinner.setValue(prideConverter.getProperties().getMascotConfidenceLevel());

        selectAllSpectraJRadioButton.requestFocus();

        separatorJTextField.setText(prideConverter.getUserProperties().getCurrentFileNameSelectionCriteriaSeparator());

        if (prideConverter.getProperties().getSpectrumTableModel() != null) {
            spectraJTable.setModel(prideConverter.getProperties().getSpectrumTableModel());
            loadSpectraJButton.setEnabled(false);
            selectedSpectraJLabel.setEnabled(true);
            numberOfSelectedSpectraJTextField.setEnabled(true);
            selectAllSpectraJRadioButton.setEnabled(false);
            selectIdentifiedJRadioButton.setEnabled(false);
            applyAdvancedSelectionButton.setEnabled(true);

            for (int i = 0; i < spectraJTable.getRowCount(); i++) {
                if (((Boolean) spectraJTable.getValueAt(i, 4)).booleanValue()) {
                    numberOfSelectedSpectra++;
                }
            }

            numberOfSelectedSpectraJTextField.setText("" +
                    numberOfSelectedSpectra + "/" +
                    spectraJTable.getRowCount());
        }

        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().
                getResource("/no/uib/prideconverter/icons/prideConverter_16.GIF")));

        setTitle(prideConverter.getWizardName() + " " +
                prideConverter.getPrideConverterVersionNumber() + " - " + getTitle());

        if (location != null) {
            setLocation(location);
        } else {
            setLocationRelativeTo(null);
        }

        setVisible(true);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        selectAllJPopupMenu = new javax.swing.JPopupMenu();
        selectAllJMenuItem = new javax.swing.JMenuItem();
        selectIdentifiedJMenuItem = new javax.swing.JMenuItem();
        invertSelectionJMenuItem = new javax.swing.JMenuItem();
        simpleSelectionButtonGroup = new javax.swing.ButtonGroup();
        advancedSelectionButtonGroup = new javax.swing.ButtonGroup();
        nextJButton = new javax.swing.JButton();
        backJButton = new javax.swing.JButton();
        cancelJButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        helpJButton = new javax.swing.JButton();
        aboutJButton = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        loadSpectraJButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        spectraJTable = new JTable() {
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
        selectedSpectraJLabel = new javax.swing.JLabel();
        numberOfSelectedSpectraJTextField = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        selectAllSpectraJRadioButton = new javax.swing.JRadioButton();
        selectIdentifiedJRadioButton = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        advancedSelectionJTextArea = new javax.swing.JTextArea();
        applyAdvancedSelectionButton = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        separatorJTextField = new javax.swing.JTextField();
        fileNamesSelectionJRadioButton = new javax.swing.JRadioButton();
        identificationIdsSelectionJRadioButton = new javax.swing.JRadioButton();
        selectionTypeJLabel = new javax.swing.JLabel();
        findOutPutFileJButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        peptideScoreJTextField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        mascotConfidenceLevelJSpinner = new javax.swing.JSpinner();

        selectAllJMenuItem.setMnemonic('A');
        selectAllJMenuItem.setText("Select All");
        selectAllJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAllJMenuItemActionPerformed(evt);
            }
        });
        selectAllJPopupMenu.add(selectAllJMenuItem);

        selectIdentifiedJMenuItem.setText("Select Identified");
        selectIdentifiedJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectIdentifiedJMenuItemActionPerformed(evt);
            }
        });
        selectAllJPopupMenu.add(selectIdentifiedJMenuItem);

        invertSelectionJMenuItem.setText("Invert Selection");
        invertSelectionJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                invertSelectionJMenuItemActionPerformed(evt);
            }
        });
        selectAllJPopupMenu.add(invertSelectionJMenuItem);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Spectra Selection - Step 2 of 8");
        setMinimumSize(new java.awt.Dimension(580, 560));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        nextJButton.setText("Next  >");
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

        jLabel3.setFont(new java.awt.Font("Tahoma", 2, 11));
        jLabel3.setText("Select the spectra to include in the PRIDE XML file, and click on 'Next' to continue.");

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

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Manual Spectra Selection", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(0, 0, 0)));

        loadSpectraJButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/no/uib/prideconverter/icons/load2.GIF"))); // NOI18N
        loadSpectraJButton.setText("Load Spectra");
        loadSpectraJButton.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        loadSpectraJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadSpectraJButtonActionPerformed(evt);
            }
        });

        spectraJTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "PID", "Filename", "ID", "Identified", "Selected"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.Boolean.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        spectraJTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                spectraJTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(spectraJTable);

        selectedSpectraJLabel.setText("Selected Spectra:");
        selectedSpectraJLabel.setToolTipText("Number of Selected Spectra");
        selectedSpectraJLabel.setEnabled(false);

        numberOfSelectedSpectraJTextField.setEditable(false);
        numberOfSelectedSpectraJTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        numberOfSelectedSpectraJTextField.setToolTipText("Number of Selected Spectra");
        numberOfSelectedSpectraJTextField.setEnabled(false);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 594, Short.MAX_VALUE)
                    .addComponent(loadSpectraJButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 594, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(selectedSpectraJLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(numberOfSelectedSpectraJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(numberOfSelectedSpectraJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectedSpectraJLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(loadSpectraJButton)
                .addContainerGap())
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Simple Spectra Selection", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(0, 0, 0)));

        simpleSelectionButtonGroup.add(selectAllSpectraJRadioButton);
        selectAllSpectraJRadioButton.setSelected(true);
        selectAllSpectraJRadioButton.setText("Select All Spectra");
        selectAllSpectraJRadioButton.setIconTextGap(20);

        simpleSelectionButtonGroup.add(selectIdentifiedJRadioButton);
        selectIdentifiedJRadioButton.setText("Select Identified Spectra");
        selectIdentifiedJRadioButton.setIconTextGap(20);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(selectAllSpectraJRadioButton)
                    .addComponent(selectIdentifiedJRadioButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 120, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(10, 10, 10))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(selectAllSpectraJRadioButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addContainerGap(19, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addGap(27, 27, 27)))
                .addComponent(selectIdentifiedJRadioButton)
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Advanced Spectra Selection", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(0, 0, 0)));

        advancedSelectionJTextArea.setColumns(20);
        advancedSelectionJTextArea.setLineWrap(true);
        advancedSelectionJTextArea.setRows(3);
        advancedSelectionJTextArea.setWrapStyleWord(true);
        advancedSelectionJTextArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                advancedSelectionJTextAreaKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(advancedSelectionJTextArea);

        applyAdvancedSelectionButton.setText("Apply Selection");
        applyAdvancedSelectionButton.setToolTipText("Apply the selection to the spectra table");
        applyAdvancedSelectionButton.setEnabled(false);
        applyAdvancedSelectionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyAdvancedSelectionButtonActionPerformed(evt);
            }
        });

        jLabel5.setText("Separator:");

        separatorJTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        separatorJTextField.setText(",");

        advancedSelectionButtonGroup.add(fileNamesSelectionJRadioButton);
        fileNamesSelectionJRadioButton.setSelected(true);
        fileNamesSelectionJRadioButton.setText("(Partial) Filenames");
        fileNamesSelectionJRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileNamesSelectionJRadioButtonActionPerformed(evt);
            }
        });

        advancedSelectionButtonGroup.add(identificationIdsSelectionJRadioButton);
        identificationIdsSelectionJRadioButton.setText("Identification IDs");
        identificationIdsSelectionJRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                identificationIdsSelectionJRadioButtonActionPerformed(evt);
            }
        });

        selectionTypeJLabel.setText("Select Spectra Based On:");

        findOutPutFileJButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/no/uib/prideconverter/icons/Directory.gif"))); // NOI18N
        findOutPutFileJButton.setToolTipText("Load The Filenames From File");
        findOutPutFileJButton.setBorderPainted(false);
        findOutPutFileJButton.setContentAreaFilled(false);
        findOutPutFileJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findOutPutFileJButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 594, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                        .addComponent(selectionTypeJLabel)
                        .addGap(18, 18, 18)
                        .addComponent(fileNamesSelectionJRadioButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(identificationIdsSelectionJRadioButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 100, Short.MAX_VALUE)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(separatorJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(findOutPutFileJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(applyAdvancedSelectionButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 594, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(selectionTypeJLabel)
                        .addComponent(fileNamesSelectionJRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(identificationIdsSelectionJRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(separatorJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5))
                    .addComponent(findOutPutFileJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(applyAdvancedSelectionButton)
                .addContainerGap())
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Peptide Identifications", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(0, 0, 0)));

        jLabel2.setText("Peptide Score Threshold:");
        jLabel2.setToolTipText("Only include identifications with peptide scores higher than this value");

        peptideScoreJTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        peptideScoreJTextField.setToolTipText("Only include identifications with peptide scores higher than this value");

        jLabel4.setText("Mascot Confidence Level:");

        mascotConfidenceLevelJSpinner.setModel(new javax.swing.SpinnerNumberModel(95, 0, 100, 1));
        mascotConfidenceLevelJSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                mascotConfidenceLevelJSpinnerStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mascotConfidenceLevelJSpinner, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
                    .addComponent(peptideScoreJTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(peptideScoreJTextField))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(mascotConfidenceLevelJSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(helpJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(aboutJButton, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 309, Short.MAX_VALUE)
                        .addComponent(backJButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nextJButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cancelJButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 630, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cancelJButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(nextJButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(backJButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
     * Closes the frame and open the next frame (ExperimentProperties).
     * 
     * @param evt
     */
    private void nextJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextJButtonActionPerformed
        if (saveInsertedInformation()) {
            new ExperimentProperties(prideConverter, this.getLocation());
            this.setVisible(false);
            this.dispose();
        }
    }//GEN-LAST:event_nextJButtonActionPerformed

    /**
     * See cancelJButtonActionPerformed
     * 
     * @param evt
     */
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        cancelJButtonActionPerformed(null);
    }//GEN-LAST:event_formWindowClosing

    /**
     * Opens a help window.
     * 
     * @param evt
     */
    private void helpJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpJButtonActionPerformed
        setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        new HelpWindow(this, getClass().getResource("/no/uib/prideconverter/helpfiles/SpectraSelectionWithIdentifications.html"));
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
     * Closes the frame and opens the previous step (DataFileSelection or 
     * DataFileSelection_TwoFileTypes).
     * 
     * @param evt
     */
    private void backJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backJButtonActionPerformed

        if (saveInsertedInformation()) {
            if (prideConverter.getProperties().getDataSource().equalsIgnoreCase("X!Tandem") ||
                    prideConverter.getProperties().getDataSource().equalsIgnoreCase("Mascot Dat File") ||
                    prideConverter.getProperties().getDataSource().equalsIgnoreCase("OMSSA")) {
                new DataFileSelection(prideConverter, this.getLocation());
            } else if (prideConverter.getProperties().getDataSource().equalsIgnoreCase("Spectrum Mill") ||
                    prideConverter.getProperties().getDataSource().equalsIgnoreCase("Sequest Result File")) {
                new DataFileSelectionTwoFileTypes(prideConverter, this.getLocation());
            } else if (prideConverter.getProperties().getDataSource().equalsIgnoreCase("ms_lims")) {
                new ProjectSelection(prideConverter, this.getLocation());
            }

            this.setVisible(false);
            this.dispose();
        }
    }//GEN-LAST:event_backJButtonActionPerformed

    /**
     * Loads the spectra from the selected files. Done in a separate thread 
     * to keep the program from hanging.
     * 
     * @param evt
     */
    private void loadSpectraJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadSpectraJButtonActionPerformed

        setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

        numberOfSelectedSpectra = 0;

        progressDialog = new ProgressDialog(this, true);
        progressDialog.setTitle("Loading Spectra. Please Wait.");
        progressDialog.setIntermidiate(true);

        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                progressDialog.setVisible(true);
            }
        });

        t.start();


        Thread t2 = new Thread(new Runnable() {

            @Override
            public void run() {

                int spectrumID;
                double expect;
                String label;
                int start;
                String peptideSequence;
                DocumentBuilderFactory dbf;
                DocumentBuilder db;
                Document dom;
                Element docEle;
                NodeList nodes;
                NodeList idNodes,
                        proteinNodes, peptideNodes, traceNodes, spectrumNodes,
                        xDataNodes, yDataNodes;

                boolean matchFound;
                boolean selected;
                double precursorIntensty;
                OmssaOmxFile omxFile;
                HashMap<MSSpectrum, MSHitSet> results;
                Iterator<MSSpectrum> iterator;
                MSSpectrum tempSpectrum;

                prideConverter.getProperties().setSelectedSpectraNames(new ArrayList());

                MascotDatfileInf tempMascotDatfile;
                QueryToPeptideMapInf queryToPeptideMap;
                QueryEnumerator queries;
                Query currentQuery;
                PeptideHit tempPeptideHit;
                numberOfSelectedSpectra = 0;
                int totalNumberOfSpectra = 0;

                while (((DefaultTableModel) spectraJTable.getModel()).getRowCount() >
                        0) {
                    ((DefaultTableModel) spectraJTable.getModel()).removeRow(0);
                }

                if (prideConverter.getProperties().getSelectedSourceFiles() != null) {

                    for (int j = 0; j <
                            prideConverter.getProperties().getSelectedSourceFiles().size(); j++) {

                        file = new File(prideConverter.getProperties().getSelectedSourceFiles().get(j));

                        progressDialog.setString(file.getName() + " (" + (j + 1) +
                                "/" + prideConverter.getProperties().getSelectedSourceFiles().size() +
                                ")");

                        spectrumID = -1;
                        precursorMass = 0.0;
                        precursorCharge = 0;
                        expect = 0.0;

                        // parse X!Tandem file
                        // should be replaced by better and easier xml parsing
                        if (prideConverter.getProperties().getDataSource().equalsIgnoreCase("X!Tandem")) {

                            try {
                                //get the factory
                                dbf = DocumentBuilderFactory.newInstance();

                                //Using factory get an instance of document builder
                                db = dbf.newDocumentBuilder();

                                //parse using builder to get DOM representation of the XML file
                                dom = db.parse(file);

                                //get the root elememt
                                docEle = dom.getDocumentElement();

                                nodes = docEle.getChildNodes();

                                for (int i = 0; i <
                                        nodes.getLength(); i++) {

                                    if (!progressDialog.isVisible()) {

                                        while (((DefaultTableModel) spectraJTable.getModel()).getRowCount() >
                                                0) {
                                            ((DefaultTableModel) spectraJTable.getModel()).removeRow(0);
                                        }

                                        numberOfSelectedSpectraJTextField.setText("");

                                        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                                        return;
                                    }

                                    if (nodes.item(i).getAttributes() != null) {

                                        if (nodes.item(i).getAttributes().getNamedItem("type") !=
                                                null) {

                                            if (nodes.item(i).getAttributes().getNamedItem("type").getNodeValue().equalsIgnoreCase(
                                                    "model")) {

                                                if (nodes.item(i).getAttributes().getNamedItem("id") !=
                                                        null) {
                                                    spectrumID = new Integer(nodes.item(i).getAttributes().getNamedItem("id").getNodeValue()).intValue();
                                                }

                                                if (nodes.item(i).getAttributes().getNamedItem("mh") !=
                                                        null) {
                                                    precursorMass = new Double(nodes.item(i).getAttributes().getNamedItem("mh").getNodeValue()).doubleValue();
                                                }

                                                if (nodes.item(i).getAttributes().getNamedItem("z") !=
                                                        null) {
                                                    precursorCharge = new Integer(nodes.item(i).getAttributes().getNamedItem("z").getNodeValue()).intValue();
                                                }

                                                if (nodes.item(i).getAttributes().getNamedItem("expect") !=
                                                        null) {
                                                    expect = new Double(nodes.item(i).getAttributes().getNamedItem("expect").getNodeValue()).doubleValue();
                                                }

                                                if (nodes.item(i).getAttributes().getNamedItem("label") !=
                                                        null) {
                                                    label = nodes.item(i).getAttributes().getNamedItem("label").getNodeValue();
                                                }

                                                ((DefaultTableModel) spectraJTable.getModel()).addRow(
                                                        new Object[]{
                                                    null,
                                                    file.getName(),
                                                    spectrumID,
                                                    new Boolean(true),
                                                    new Boolean(true)
                                                });

                                                numberOfSelectedSpectra++;
                                            }
                                        }
                                    }
                                }

                                numberOfSelectedSpectraJTextField.setText(numberOfSelectedSpectra +
                                        "/" +
                                        numberOfSelectedSpectra);

                            } catch (FileNotFoundException ex) {
                                JOptionPane.showMessageDialog(null, "The file named " +
                                        prideConverter.getProperties().getSelectedSourceFiles().get(j) +
                                        "\ncould not be found.",
                                        "File Not Found", JOptionPane.ERROR_MESSAGE);
                                Util.writeToErrorLog("Error when reading X!Tandem file: ");
                                ex.printStackTrace();
                            } catch (Exception e) {

                                Util.writeToErrorLog("Error parsing X!Tandem file: ");
                                e.printStackTrace();

                                JOptionPane.showMessageDialog(null,
                                        "The following file could not parsed as an X!Tandem file:\n " +
                                        prideConverter.getProperties().getSelectedSourceFiles().get(j) +
                                        "\n\n" +
                                        "See ../Properties/ErrorLog.txt for more details.",
                                        "Error Parsing File", JOptionPane.ERROR_MESSAGE);
                            }
                        } else if (prideConverter.getProperties().getDataSource().equalsIgnoreCase("Spectrum Mill") ||
                                prideConverter.getProperties().getDataSource().equalsIgnoreCase("Sequest Result File")) {

                            try {
                                f = new FileReader(file);
                                b = new BufferedReader(f);

                                currentLine = b.readLine();

                                tok = new StringTokenizer(currentLine);

                                precursorMass = new Double(tok.nextToken()).doubleValue();
                                precursorIntensty = Double.NaN;

                                if (prideConverter.getProperties().getDataSource().equalsIgnoreCase("Spectrum Mill")) {
                                    precursorIntensty = new Double(tok.nextToken()).doubleValue();
                                }

                                precursorCharge = new Integer(tok.nextToken()).intValue();

                                matchFound = false;

                                for (int i = 0; i <
                                        prideConverter.getProperties().getSelectedIdentificationFiles().size() &&
                                        !matchFound; i++) {

                                    if (prideConverter.getProperties().getDataSource().equalsIgnoreCase("Spectrum Mill")) {
                                        matchFound = new File(prideConverter.getProperties().getSelectedIdentificationFiles().get(i)).getName().equalsIgnoreCase(file.getName() +
                                                ".spo");
                                    } else if (prideConverter.getProperties().getDataSource().equalsIgnoreCase("Sequest Result File")) {

                                        matchFound = new File(prideConverter.getProperties().getSelectedIdentificationFiles().get(i)).getName().equalsIgnoreCase(
                                                file.getName().substring(0, file.getName().length() -
                                                4) + ".out");
                                    }
                                }

                                selected = false;

                                if (selectAllSpectraJRadioButton.isSelected()) {
                                    selected = true;
                                } else if (selectIdentifiedJRadioButton.isSelected()) {
                                    selected = matchFound;
                                }

                                ((DefaultTableModel) spectraJTable.getModel()).addRow(
                                        new Object[]{
                                    null,
                                    file.getName(),
                                    null,
                                    new Boolean(matchFound),
                                    new Boolean(selected)
                                });

                                if (selected) {
                                    numberOfSelectedSpectra++;
                                }

                                numberOfSelectedSpectraJTextField.setText(numberOfSelectedSpectra +
                                        "/" + spectraJTable.getRowCount());

                            } catch (FileNotFoundException ex) {
                                JOptionPane.showMessageDialog(null, "The file named " +
                                        file.getName() +
                                        "\ncould not be found.",
                                        "File Not Found", JOptionPane.ERROR_MESSAGE);
                                Util.writeToErrorLog("File not found: ");
                                ex.printStackTrace();
                            } catch (Exception e) {

                                String fileName = "Sequest DTA file";

                                if (prideConverter.getProperties().getDataSource().equalsIgnoreCase("Spectrum Mill")) {
                                    fileName = "Micromass PKL file";
                                }

                                Util.writeToErrorLog("Error parsing " + fileName + ": ");
                                e.printStackTrace();

                                JOptionPane.showMessageDialog(null,
                                        "The following file could not parsed as a " +
                                        fileName + ":\n " +
                                        file.getPath() + "\n\n" +
                                        "See ../Properties/ErrorLog.txt for more details.",
                                        "Error Parsing File", JOptionPane.ERROR_MESSAGE);
                            }

                            if (!progressDialog.isVisible()) {

                                while (((DefaultTableModel) spectraJTable.getModel()).getRowCount() >
                                        0) {
                                    ((DefaultTableModel) spectraJTable.getModel()).removeRow(0);
                                }

                                numberOfSelectedSpectraJTextField.setText("");

                                setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                                return;
                            }

                        } else if (prideConverter.getProperties().getDataSource().equalsIgnoreCase("OMSSA")) {

                            try {

                                selected = false;

                                omxFile = new OmssaOmxFile(file.getPath(), null, null);
                                results = omxFile.getSpectrumToHitSetMap();

                                iterator = results.keySet().iterator();

                                while (iterator.hasNext()) {

                                    tempSpectrum = iterator.next();

                                    selected = false;

                                    if (selectAllSpectraJRadioButton.isSelected()) {
                                        selected = true;
                                        numberOfSelectedSpectra++;
                                    } else if (results.get(tempSpectrum).MSHitSet_hits.MSHits.size() >
                                            0) {
                                        selected = true;
                                        numberOfSelectedSpectra++;
                                    }

                                    ((DefaultTableModel) spectraJTable.getModel()).addRow(
                                            new Object[]{
                                        null,
                                        tempSpectrum.MSSpectrum_ids.MSSpectrum_ids_E.get(0),
                                        tempSpectrum.MSSpectrum_number,
                                        new Boolean(results.get(tempSpectrum).MSHitSet_hits.MSHits.size() >
                                        0),
                                        new Boolean(selected)
                                    });

                                    numberOfSelectedSpectraJTextField.setText(numberOfSelectedSpectra +
                                            "/" +
                                            spectraJTable.getRowCount());
                                }

                                if (!progressDialog.isVisible()) {

                                    while (((DefaultTableModel) spectraJTable.getModel()).getRowCount() >
                                            0) {
                                        ((DefaultTableModel) spectraJTable.getModel()).removeRow(0);
                                    }

                                    numberOfSelectedSpectraJTextField.setText("");

                                    setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                                    return;
                                }
                            } catch (Exception e) {

                                Util.writeToErrorLog("Error parsing OMSSA file: ");
                                e.printStackTrace();

                                JOptionPane.showMessageDialog(null,
                                        "The following file could not parsed as an OMSSA file:\n " +
                                        prideConverter.getProperties().getSelectedSourceFiles().get(j) +
                                        "\n\n" +
                                        "See ../Properties/ErrorLog.txt for more details.",
                                        "Error Parsing File", JOptionPane.ERROR_MESSAGE);
                            }
                        } else if (prideConverter.getProperties().getDataSource().equalsIgnoreCase("Mascot Dat File")) {

                            prideConverter.getProperties().setMascotConfidenceLevel(
                                    (Integer) mascotConfidenceLevelJSpinner.getValue());

                            double confidenceLevel = (100 -
                                    (double) prideConverter.getProperties().getMascotConfidenceLevel()) /
                                    100;

                            double size = (double) file.length() / 1048576;

                            if (size >
                                    prideConverter.getProperties().MAX_MASCOT_DAT_FILESIZE_BEFORE_INDEXING) {

                                //if the file is large
                                tempMascotDatfile = MascotDatfileFactory.create(
                                        file.getPath(),
                                        MascotDatfileType.INDEX);
                            } else {
                                tempMascotDatfile = MascotDatfileFactory.create(
                                        file.getPath(),
                                        MascotDatfileType.MEMORY);
                            }

                            queryToPeptideMap = tempMascotDatfile.getQueryToPeptideMap();
                            queries = tempMascotDatfile.getQueryEnumerator();

                            while (queries.hasMoreElements()) {

                                currentQuery = queries.nextElement();
                                tempPeptideHit = queryToPeptideMap.getPeptideHitOfOneQuery(currentQuery.getQueryNumber());

                                if (tempPeptideHit != null) {
                                    ((DefaultTableModel) spectraJTable.getModel()).addRow(new Object[]{
                                        null,
                                        currentQuery.getFilename(),
                                        null,
                                        new Boolean(queryToPeptideMap.getPeptideHitsAboveIdentityThreshold(currentQuery.getQueryNumber(), confidenceLevel).size() >
                                        0),
                                        new Boolean(queryToPeptideMap.getPeptideHitsAboveIdentityThreshold(currentQuery.getQueryNumber(), confidenceLevel).size() >
                                        0)
                                    });

                                    if (queryToPeptideMap.getPeptideHitsAboveIdentityThreshold(currentQuery.getQueryNumber(), confidenceLevel).size() >
                                            0) {
                                        numberOfSelectedSpectra++;
                                    }
                                } else {
                                    ((DefaultTableModel) spectraJTable.getModel()).addRow(new Object[]{
                                        null,
                                        currentQuery.getFilename(),
                                        null,
                                        new Boolean(false),
                                        new Boolean(false)
                                    });
                                }

                                totalNumberOfSpectra++;

                                if (!progressDialog.isVisible()) {

                                    setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

                                    while (((DefaultTableModel) spectraJTable.getModel()).getRowCount() >
                                            0) {
                                        ((DefaultTableModel) spectraJTable.getModel()).removeRow(0);
                                    }

                                    numberOfSelectedSpectra = 0;
                                    numberOfSelectedSpectraJTextField.setText("");
                                    loadSpectraJButton.setEnabled(true);

                                    setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                                    return;
                                }

                                numberOfSelectedSpectraJTextField.setText(numberOfSelectedSpectra +
                                        "/" +
                                        totalNumberOfSpectra);
                            }
                        }
                    }
                }

                if (prideConverter.getProperties().getDataSource().equalsIgnoreCase("ms_lims")) {
                    try {
                        boolean identified;

                        Identification id;
                        Integer identificationId;

                        StringTokenizer tok;
                        String tempToken;

                        Object[][] dbSpectra;

                        numberOfSelectedSpectra = 0;

                        for (int j = 0; j < prideConverter.getProperties().getProjectIds().size(); j++) {

                            dbSpectra = Spectrumfile.getFilenameAndIdentifiedStatusForAllSpectraForProject(
                                    prideConverter.getProperties().getProjectIds().get(j), prideConverter.getConn());

                            identified = false;
                            selected = false;

                            progressDialog.setValue(0);
                            progressDialog.setMax(dbSpectra.length);

                            for (int i = 0; i < dbSpectra.length; i++) {

                                if (!progressDialog.isVisible()) {

                                    while (spectraJTable.getRowCount() > 0) {
                                        ((DefaultTableModel) spectraJTable.getModel()).removeRow(0);
                                    }

                                    setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                                    return;
                                }

                                progressDialog.setValue(i);

                                id = Identification.getIdentification(prideConverter.getConn(), (String) dbSpectra[i][1]);

                                if (id != null) {
                                    identificationId = new Long(id.getIdentificationid()).intValue();
                                } else {
                                    identificationId = null;
                                }

                                if (((Integer) dbSpectra[i][2]).intValue() > 0 &&
                                        id.getValid() > 0) {
                                    identified = true;
                                } else {
                                    identified = false;
                                }

                                selected = false;

                                if (selectionCriteria != null) {

                                    tok = new StringTokenizer(selectionCriteria, separatorJTextField.getText());

                                    while (tok.hasMoreTokens() && !selected) {

                                        tempToken = tok.nextToken();
                                        tempToken = tempToken.trim();

                                        if (((String) dbSpectra[i][1]).lastIndexOf(tempToken) !=
                                                -1) {
                                            selected = true;
                                        }
                                    }
                                } else {

                                    if (selectAllSpectraJRadioButton.isSelected()) {
                                        selected = true;
                                    } else if (selectIdentifiedJRadioButton.isSelected()) {
                                        if (identified) {
                                            selected = true;
                                        }
                                    }
                                }

                                if (selected) {
                                    numberOfSelectedSpectra++;
                                }

                                ((DefaultTableModel) spectraJTable.getModel()).addRow(new Object[]{
                                    prideConverter.getProperties().getProjectIds().get(j),
                                    (String) dbSpectra[i][1],
                                    identificationId,
                                    new Boolean(identified),
                                    new Boolean(selected)
                                });

                                numberOfSelectedSpectraJTextField.setText("" +
                                        numberOfSelectedSpectra + "/" +
                                        spectraJTable.getRowCount());
                            }
                        }
                    } catch (OutOfMemoryError error) {
                        progressDialog.setVisible(false);
                        progressDialog.dispose();
                        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                        Runtime.getRuntime().gc();
                        JOptionPane.showMessageDialog(null,
                                "The task used up all the available memory and had to be stopped.\n" +
                                "Memory boundaries are set in ../Properties/JavaOptions.txt.\n\n" +
                                "If the data sets are too big for your computer, e-mail a support\n" +
                                "request to the PRIDE team at the EBI: pride-support@ebi.ac.uk",
                                "Out of memory error",
                                JOptionPane.ERROR_MESSAGE);
                    } catch (Exception e) {
                        progressDialog.setVisible(false);
                        progressDialog.dispose();
                        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                        JOptionPane.showMessageDialog(null, "Error while retrieving spectrum data.");
                        Util.writeToErrorLog("Error while retrieving spectrum data: ");
                        e.printStackTrace();
                    }
                }

                selectedSpectraJLabel.setEnabled(true);
                numberOfSelectedSpectraJTextField.setEnabled(true);
                loadSpectraJButton.setEnabled(false);
                selectAllSpectraJRadioButton.setEnabled(false);
                selectIdentifiedJRadioButton.setEnabled(false);

                if (advancedSelectionJTextArea.getText().length() > 0) {
                    applyAdvancedSelectionButton.setEnabled(true);
                }

                progressDialog.setVisible(false);
                progressDialog.dispose();

                numberOfSelectedSpectraJTextField.setText(numberOfSelectedSpectra +
                        "/" + spectraJTable.getRowCount());

                if (numberOfSelectedSpectra == 0) {
//                    JOptionPane.showMessageDialog(null, "The file did not contain any spectra.",
//                            "No Spectra Found", JOptionPane.ERROR_MESSAGE);
                    nextJButton.setEnabled(false);
                } else {
                    nextJButton.setEnabled(true);
                }
            }
        });

        t2.start();

        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_loadSpectraJButtonActionPerformed

    /**
     * Makes sure that the number of selected spectra is updated when the 
     * user selects or deselects a spectra in the list. Right clicking in 
     * the last column open a popup menu with advanced selection option.
     * 
     * @param evt
     */
    private void spectraJTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_spectraJTableMouseClicked
        if (spectraJTable.columnAtPoint(evt.getPoint()) == 4) {

            this.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

            numberOfSelectedSpectra = 0;

            for (int i = 0; i <
                    spectraJTable.getRowCount(); i++) {
                if (((Boolean) spectraJTable.getValueAt(i, 4)).booleanValue()) {
                    numberOfSelectedSpectra++;
                }
            }

            numberOfSelectedSpectraJTextField.setText("" +
                    numberOfSelectedSpectra + "/" +
                    spectraJTable.getRowCount());

            if (numberOfSelectedSpectra == 0) {
                nextJButton.setEnabled(false);
            } else {
                nextJButton.setEnabled(true);
            }
        }

        int column = spectraJTable.columnAtPoint(evt.getPoint());

        if (evt.getButton() == java.awt.event.MouseEvent.BUTTON3 && column == 4) {
            selectAllJPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
        }

        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_spectraJTableMouseClicked

    /**
     * Selects all the spectra in the table.
     * 
     * @param evt
     */
    private void selectAllJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAllJMenuItemActionPerformed
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

        if (selectAll) {
            for (int i = 0; i <
                    spectraJTable.getRowCount(); i++) {
                if (!(((Boolean) spectraJTable.getValueAt(i, 4)).booleanValue())) {
                    spectraJTable.setValueAt(new Boolean(true), i, 4);
                }
            }

            numberOfSelectedSpectraJTextField.setText("" +
                    spectraJTable.getRowCount() + "/" +
                    spectraJTable.getRowCount());
            numberOfSelectedSpectra =
                    spectraJTable.getRowCount();
        } else {
            for (int i = 0; i <
                    spectraJTable.getRowCount(); i++) {
                if ((((Boolean) spectraJTable.getValueAt(i, 4)).booleanValue())) {
                    spectraJTable.setValueAt(new Boolean(false), i, 4);
                }
            }

            numberOfSelectedSpectraJTextField.setText("0" + "/" +
                    spectraJTable.getRowCount());
            numberOfSelectedSpectra = 0;
        }

        if (numberOfSelectedSpectra == 0) {
            nextJButton.setEnabled(false);
        } else {
            nextJButton.setEnabled(true);
        }

        selectAll = !selectAll;

        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_selectAllJMenuItemActionPerformed

    /**
     * Inverts the selection in the table.
     * 
     * @param evt
     */
    private void invertSelectionJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_invertSelectionJMenuItemActionPerformed
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

        numberOfSelectedSpectra = 0;

        for (int i = 0; i < spectraJTable.getRowCount(); i++) {
            spectraJTable.setValueAt(
                    new Boolean(!((Boolean) spectraJTable.getValueAt(i, 4)).booleanValue()), i, 4);

            if (((Boolean) spectraJTable.getValueAt(i, 4)).booleanValue()) {
                numberOfSelectedSpectra++;
            }
        }

        numberOfSelectedSpectraJTextField.setText("" + numberOfSelectedSpectra +
                "/" + spectraJTable.getRowCount());

        if (numberOfSelectedSpectra == 0) {
            nextJButton.setEnabled(false);
        } else {
            nextJButton.setEnabled(true);
        }

        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_invertSelectionJMenuItemActionPerformed

    /**
     * Selects all the identified spectra in the table.
     * 
     * @param evt
     */
    private void selectIdentifiedJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectIdentifiedJMenuItemActionPerformed
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

        numberOfSelectedSpectra = 0;

        for (int i = 0; i < spectraJTable.getRowCount(); i++) {
            if ((((Boolean) spectraJTable.getValueAt(i, 3)).booleanValue())) {
                spectraJTable.setValueAt(new Boolean(true), i, 4);

                numberOfSelectedSpectra++;
            } else {
                spectraJTable.setValueAt(new Boolean(false), i, 4);
            }
        }

        numberOfSelectedSpectraJTextField.setText("" + numberOfSelectedSpectra +
                "/" + spectraJTable.getRowCount());

        if (numberOfSelectedSpectra == 0) {
            nextJButton.setEnabled(false);
        } else {
            nextJButton.setEnabled(true);
        }

        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_selectIdentifiedJMenuItemActionPerformed

    /**
     * Enables or disables the buttons associated with the advanced 
     * spectra selection depending on the amount of text in the 
     * advanced selection text area.
     * 
     * @param evt
     */
    private void advancedSelectionJTextAreaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_advancedSelectionJTextAreaKeyReleased
        if (advancedSelectionJTextArea.getText().length() > 0) {
            selectAllSpectraJRadioButton.setEnabled(false);
            selectAllSpectraJRadioButton.setSelected(false);
            selectIdentifiedJRadioButton.setEnabled(false);
            selectIdentifiedJRadioButton.setSelected(false);

            if (spectraJTable.getRowCount() > 0) {
                applyAdvancedSelectionButton.setEnabled(true);
            } else {
                applyAdvancedSelectionButton.setEnabled(false);
            }
        } else {
            if (spectraJTable.getRowCount() == 0) {
                selectAllSpectraJRadioButton.setEnabled(true);
                selectIdentifiedJRadioButton.setEnabled(true);
            }

            applyAdvancedSelectionButton.setEnabled(false);
        }
    }//GEN-LAST:event_advancedSelectionJTextAreaKeyReleased

    /**
     * Applies the advanced spectra selection.
     * 
     * @param evt
     */
    private void applyAdvancedSelectionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyAdvancedSelectionButtonActionPerformed
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

        StringTokenizer selectionCriteriaTokenizer;
        String tempToken;
        boolean selected = false;
        boolean errorDetected = false;

        selectionCriteria = advancedSelectionJTextArea.getText();

        int selectionColumn;

        if (fileNamesSelectionJRadioButton.isSelected()) {
            selectionColumn = 1;
        } else {
            selectionColumn = 2;
        }

        for (int i = 0; i < spectraJTable.getRowCount(); i++) {
            spectraJTable.setValueAt(new Boolean(false), i, 4);
        }

        numberOfSelectedSpectra = 0;

        for (int i = 0; i < spectraJTable.getRowCount() && !errorDetected; i++) {

            if (separatorJTextField.getText().length() == 0) {
                selectionCriteriaTokenizer = new StringTokenizer(selectionCriteria);
            } else {
                selectionCriteriaTokenizer = new StringTokenizer(selectionCriteria, separatorJTextField.getText());
            }

            selected = false;
            errorDetected = false;

            while (selectionCriteriaTokenizer.hasMoreTokens() && !selected && !errorDetected) {

                tempToken = selectionCriteriaTokenizer.nextToken();
                tempToken = tempToken.trim();

                if (selectionColumn == 1) {
                    if (((String) spectraJTable.getValueAt(i, selectionColumn)).lastIndexOf(tempToken) != -1) {
                        spectraJTable.setValueAt(new Boolean(true), i, 4);
                        selected = true;
                    }
                } else {
                    try {
                        if (spectraJTable.getValueAt(i, selectionColumn) !=
                                null) {

                            if (((Integer) spectraJTable.getValueAt(i, selectionColumn)).intValue() ==
                                    new Integer(tempToken)) {
                                spectraJTable.setValueAt(new Boolean(true), i, 4);
                                selected = true;
                            }
                        }
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, tempToken +
                                " is not an integer!\n" +
                                "Selection not completed.");
                        errorDetected = true;
                    }
                }
            }

            if (selected) {
                numberOfSelectedSpectra++;
            }
        }

        numberOfSelectedSpectraJTextField.setText("" +
                numberOfSelectedSpectra + "/" +
                spectraJTable.getRowCount());

        if (numberOfSelectedSpectra == 0) {
            nextJButton.setEnabled(false);
        } else {
            nextJButton.setEnabled(true);
        }

        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_applyAdvancedSelectionButtonActionPerformed

    /**
     * Sets the advanced spectra selection criteria to file name.
     * 
     * @param evt
     */
    private void fileNamesSelectionJRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileNamesSelectionJRadioButtonActionPerformed
        prideConverter.getProperties().setSelectionCriteriaIsFileName(true);
    }//GEN-LAST:event_fileNamesSelectionJRadioButtonActionPerformed

    /**
     * Sets the advanced spectra selection criteria to id.
     * 
     * @param evt
     */
    private void identificationIdsSelectionJRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_identificationIdsSelectionJRadioButtonActionPerformed
        prideConverter.getProperties().setSelectionCriteriaIsFileName(false);
    }//GEN-LAST:event_identificationIdsSelectionJRadioButtonActionPerformed

    /**
     * Opens a file chooser where the user can select a file to import the advanced 
     * spectra selection values from.
     * 
     * @param evt
     */
    private void findOutPutFileJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findOutPutFileJButtonActionPerformed
        JFileChooser chooser = new JFileChooser();

        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String path = (chooser.getSelectedFile().getAbsoluteFile().getPath());

            try {
                FileReader fr = new FileReader(path);
                BufferedReader br = new BufferedReader(fr);

                String s = br.readLine();
                String temp = "";

                while (s != null) {
                    temp += s + "\n";
                    s = br.readLine();
                }

                advancedSelectionJTextArea.setText(temp);
                advancedSelectionJTextArea.setCaretPosition(0);

                if (advancedSelectionJTextArea.getText().length() > 0) {
                    applyAdvancedSelectionButton.setEnabled(true);
                }

                br.close();
                fr.close();
            } catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(null, 
                        "Error when reading file. See ../Properties/ErrorLog.txt for more details.", 
                        "File Not Found", JOptionPane.ERROR_MESSAGE);
                Util.writeToErrorLog("Error when reading file: ");
                ex.printStackTrace();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, 
                        "Error when reading file. See ../Properties/ErrorLog.txt for more details.", 
                        "File Error", JOptionPane.ERROR_MESSAGE);
                Util.writeToErrorLog("Error when reading file: ");
                ex.printStackTrace();
            }
        }
    }//GEN-LAST:event_findOutPutFileJButtonActionPerformed

    /**
     * If the spectra has been loaded they are noe loaded again but using the 
     * updated mascot confidence level to set the identification level.
     * 
     * @param evt
     */
    private void mascotConfidenceLevelJSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_mascotConfidenceLevelJSpinnerStateChanged
        if (spectraJTable.getRowCount() > 0) {
            loadSpectraJButtonActionPerformed(null);
        }
    }//GEN-LAST:event_mascotConfidenceLevelJSpinnerStateChanged

    /**
     * Saves the inserted information in the Properties object.
     * 
     * @return true if save was successfull, false otherwise.
     */
    private boolean saveInsertedInformation() {
        this.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

        prideConverter.getProperties().setSelectAllSpectra(selectAllSpectraJRadioButton.isSelected());
        prideConverter.getProperties().setSelectAllIdentifiedSpectra(selectIdentifiedJRadioButton.isSelected());
        prideConverter.getProperties().setMascotConfidenceLevel((Integer) mascotConfidenceLevelJSpinner.getValue());

        boolean saveOk = true;

        try {
            prideConverter.getProperties().setPeptideScoreThreshold(new Double(this.peptideScoreJTextField.getText()).doubleValue());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "The peptide score threshold is not a number!");
            peptideScoreJTextField.requestFocus();
            saveOk = false;
        }

        if (saveOk) {

            prideConverter.getProperties().setSelectedSpectraNames(new ArrayList());

            if (spectraJTable.getRowCount() > 0) {

                for (int i = 0; i < spectraJTable.getRowCount(); i++) {
                    if (((Boolean) spectraJTable.getValueAt(i, 4)).booleanValue()) {
                        prideConverter.getProperties().getSelectedSpectraNames().add(
                                new Object[]{
                            spectraJTable.getValueAt(i, 1),
                            spectraJTable.getValueAt(i, 2)
                        });
                    }
                }

                prideConverter.getProperties().setSelectAllSpectra(false);
                prideConverter.getProperties().setSelectAllIdentifiedSpectra(false);

                prideConverter.getProperties().setSpectrumTableModel((DefaultTableModel) spectraJTable.getModel());
            } else {
                prideConverter.getProperties().setSpectrumTableModel(null);
            }

            if (advancedSelectionJTextArea.getText().length() > 0) {
                prideConverter.getProperties().setSpectraSelectionCriteria(advancedSelectionJTextArea.getText());
            } else {
                prideConverter.getProperties().setSpectraSelectionCriteria(null);
            }

            prideConverter.getUserProperties().setCurrentFileNameSelectionCriteriaSeparator(separatorJTextField.getText());
        }

        this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        return saveOk;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton aboutJButton;
    private javax.swing.ButtonGroup advancedSelectionButtonGroup;
    private javax.swing.JTextArea advancedSelectionJTextArea;
    private javax.swing.JButton applyAdvancedSelectionButton;
    private javax.swing.JButton backJButton;
    private javax.swing.JButton cancelJButton;
    private javax.swing.JRadioButton fileNamesSelectionJRadioButton;
    private javax.swing.JButton findOutPutFileJButton;
    private javax.swing.JButton helpJButton;
    private javax.swing.JRadioButton identificationIdsSelectionJRadioButton;
    private javax.swing.JMenuItem invertSelectionJMenuItem;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JButton loadSpectraJButton;
    private javax.swing.JSpinner mascotConfidenceLevelJSpinner;
    private javax.swing.JButton nextJButton;
    private javax.swing.JTextField numberOfSelectedSpectraJTextField;
    private javax.swing.JTextField peptideScoreJTextField;
    private javax.swing.JMenuItem selectAllJMenuItem;
    private javax.swing.JPopupMenu selectAllJPopupMenu;
    private javax.swing.JRadioButton selectAllSpectraJRadioButton;
    private javax.swing.JMenuItem selectIdentifiedJMenuItem;
    private javax.swing.JRadioButton selectIdentifiedJRadioButton;
    private javax.swing.JLabel selectedSpectraJLabel;
    private javax.swing.JLabel selectionTypeJLabel;
    private javax.swing.JTextField separatorJTextField;
    private javax.swing.ButtonGroup simpleSelectionButtonGroup;
    private javax.swing.JTable spectraJTable;
    // End of variables declaration//GEN-END:variables
}
