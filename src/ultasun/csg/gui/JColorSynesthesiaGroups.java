package ultasun.csg.gui;

import ultasun.csg.iohelper.CsgPrinter;
import ultasun.csg.lib.ColorSynesthesiaGroups;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author ultasun
 */
public class JColorSynesthesiaGroups extends javax.swing.JFrame implements Runnable {

    /**
     * Display the form, with default configuration.
     */
    public static void newInstance() {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new JColorSynesthesiaGroups());
    }

    /**
     * Display the form, but configure it using the provided File.
     *
     * Note: Null may be passed to csg to use default value (no File).
     *
     * @param csg A CSG File to load for this new instance.
     */
    public static void newInstance(File csg) {
        java.awt.EventQueue.invokeLater(new JColorSynesthesiaGroups(csg));
    }

    /**
     * Display the form, configure it using the provided file, but override the
     * URL found in the file with the provided URL here.
     *
     * Note: Null may be passed to csg and url in order to use default values.
     *
     * @param csg A CSG File to load for this instance.
     * @param url A URL to use instead of the one found in the CSG File.
     * @param skipUi Load page, execute search, and print; or show the form.
     */
    public static void newInstance(File csg, String url, boolean skipUi) {
        java.awt.EventQueue.invokeLater(new JColorSynesthesiaGroups(csg, url, skipUi));
    }

    /**
     * Display the form, using all the provided configuration.
     *
     * Note: Null may be passed to csg and url in order to use default values.
     *
     * @param csg A CSG File to load for this instance.
     * @param url A URL to use instead of the one found in the CSG File.
     * @param skipUi Load page, execute search, and print; or show the form.
     * @param primaryKey The primary key to use (overrides default and CSG).
     * @param colorRules The color rules to use (overrides default and CSG).
     */
    public static void newInstance(File csg, String url, boolean skipUi, String primaryKey, String colorRules) {
        java.awt.EventQueue.invokeLater(new JColorSynesthesiaGroups(csg, url, skipUi, primaryKey, colorRules));
    }

    /**
     * The default File/URL to use each time a new instance is constructed.
     */
    public static String DEFAULT_FILE_OR_URL = "(choose a file or URL)";
    /**
     * Ignore CSG file File/URL and use default URL instead. However, if a
     * constructor is used which specifies a URL, that URL will always be used.
     */
    public static boolean IGNORE_CSG_FILE_URL_AND_USE_DEFAULT_URL = false;
    /**
     * The default Primary Key to use each time a new instance is constructed.
     */
    public static String DEFAULT_PRIMARY_KEY = "(enter a row header to use as the primary key)";
    /**
     * The default Color Rules to use each time a new instance is constructed.
     */
    public static String DEFAULT_COLOR_RULES = "yellow:a:r white:s:z";
    /**
     * The default Window Title to use each time a new instance is constructed.
     */
    public static String WINDOW_TITLE_POSTFIX = "Color Synesthesia Groups";

    /**
     * Construct a new instance, using all default values.
     *
     * Note: this constructor only invokes the three-argument constructor with
     * values (null, null, false).
     */
    public JColorSynesthesiaGroups() {
        this(null, null, false);
    }

    /**
     * Construct a new instance, loading the specified CSG file.
     *
     * Note: this constructor only invokes the three-argument constructor with
     * values (csgf, null, false).
     *
     * @param csgf A CSG file to load upon constructing this instance.
     */
    public JColorSynesthesiaGroups(File csgf) {
        this(csgf, null, false);
    }

    /**
     * Construct a new instance, using specified values.
     *
     * Note: this constructor first invokes the three-argument constructor with
     * values (csgf, null, false). It then processes the last two arguments.
     *
     * @param csgf A CSG File to load for this instance.
     * @param url A URL to use instead of the one found in the CSG File.
     * @param skipUi Load page, execute search, and print; or show the form.
     * @param primaryKey The primary key to use (overrides default and CSG).
     * @param colorRules The color rules to use (overrides default and CSG).
     */
    public JColorSynesthesiaGroups(File csgf, String url, boolean skipUi, String primaryKey, String colorRules) {
        this(csgf, url, skipUi);
        csgo.setPrimaryKey(primaryKey);
        tfPrimaryKey.setText(primaryKey);
        csgo.setColorRules(colorRules);
        tfColorRules.setText(colorRules);
    }

    /**
     * Construct a new instance, using specified values.
     *
     * Note: this is the master constructor.
     *
     * Note: The constructor here references 'this' in order to create a
     * JOptionPane.showMessageDialog()...but since Swing is uni-threaded we need
     * not worry.
     *
     * @param csgf A CSG File to load for this instance.
     * @param url A URL to use instead of the one found in the CSG File.
     * @param skipUi Load page, execute search, and print; or show the form.
     */
    @SuppressWarnings("LeakingThisInConstructor")
    public JColorSynesthesiaGroups(File csgf, String url, boolean skipUi) {
        super();
        executeQueryDisplayModel = 0;
        this.skipUi = skipUi;
        initComponents();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setComponentsPreInit();
        if (!this.skipUi) {
            jfc = new JFileChooser();
            thisBuild = new HashMap<>();
        }
        if (csgf == null) {
            csgo = new ColorSynesthesiaGroups();
        } else {
            ColorSynesthesiaGroups candidate = null;
            try {
                candidate = new ColorSynesthesiaGroups(csgf);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Failed to load .CSG file:\n" + e.getMessage(), "Error loading CSG file.", JOptionPane.ERROR_MESSAGE);
                candidate = new ColorSynesthesiaGroups();
            } finally {
                csgo = candidate;
            }
        }
        if (!"".equals(csgo.getTargetFileOrUrl())) {
            tfTargetFileOrUrl.setText(csgo.getTargetFileOrUrl());
        }
        if (!"".equals(csgo.getPrimaryKey())) {
            tfPrimaryKey.setText(csgo.getPrimaryKey());
        }
        if (!"".equals(csgo.getColorRules())) {
            tfColorRules.setText(csgo.getColorRules());
        }
        ckbxPunchedInRowsOnly.setSelected(csgo.isOnlyPunchedIn());
        ckbxIncludeAllNonMatchingPunchedInElements.setSelected(csgo.isIncludeAllMatchingNonPunchedInElements());
        ckbxPrimaryKeyIsHumanName.setSelected(csgo.isPkIsName());
        ckbxUseLastNameNotFirstName.setSelected(csgo.isLastNameNotFirst());
        ckbxPrintShowAllcolumns.setSelected(csgo.isShowAllColumns());
        ckbxIncludePrimaryKeySearchOption.setSelected(csgo.isIncludePrimaryKeySearchOption());
        if (ckbxUseLastNameNotFirstName.isSelected()) {
            ckbxUseLastNameNotFirstName.setEnabled(true);
        }
        listBuiltQueries.setListData(buildJlistData(csgo));
        setQuerySetName(csgo.getCsgName());
        // Override the URL with the default if so desired
        if (IGNORE_CSG_FILE_URL_AND_USE_DEFAULT_URL) {
            tfTargetFileOrUrl.setText(DEFAULT_FILE_OR_URL);
            csgo.setTargetFileOrUrl(url);
        }

        // Override the URL with the constructor supplied URL
        if (url != null && !"".equals(url)) {
            tfTargetFileOrUrl.setText(url);
            csgo.setTargetFileOrUrl(url);
        }
    }

    /**
     * This method should only be invoked by calling
     * java.awt.EventQueue.invokeLater(new JcsgExecuteQuery(...)) from the
     * worker thread (which is ColorGrouperQueries.run()). The worker thread is
     * started in the method JColorGrouperQueries.startExecuteQuery().
     */
    public void finishExecuteQuery() {
        synchronized (csgo) {
            try {
                jww.dispose();
                if (csgo.getPtcg().getResultRowCount() == 0) {
                    JOptionPane.showMessageDialog(this, "There are no results in the query!", "No matching results!", JOptionPane.WARNING_MESSAGE);
                } else {
                    switch (executeQueryDisplayModel) {
                        case 1:
                            CsgPrinter.printJobPerColor(csgo, false);
                            break;
                        case 2:
                            CsgPrinter.showJFrames(csgo);
                            break;
                        case 3:
                            displaySexpJFrame();
                            break;
                        default:
                            break;
                    }
                    csgo.clearResultData();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Printing error: " + e.getMessage(), "Printing error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * This method should only be invoked by calling
     * java.awt.EventQueue.invokeLater(new JcsgFillForeignKeyTable(...)) from
     * the worker thread (which is ColorGrouperQueries.run()). The worker thread
     * is started in the method JColorGrouperQueries.startFillForeignKeyTable().
     *
     * @param e An Exception, if any, thrown by the subroutines while processing
     * the requested page. Pass null for no Exception.
     */
    public void finishFillForeignKeyTable(Exception e) {
        synchronized (csgo) {
            if (e == null) {
                List<String> ourPrintKeys = new ArrayList<>();
                for (String k : csgo.getPtcg().getOrderedKeyList()) {
                    if (k.equals(tfPrimaryKey.getText())) {
                        ourPrintKeys.add(k);
                    } else {
                        if (csgo.getPtcg().getForeignKeys().contains(k)) {
                            ourPrintKeys.add(k);
                        }
                    }
                }
                csgo.getPtcg().setResultDisplayedColumns(ourPrintKeys);
                tableDiscoveredForeignKeys.setModel(new TableModels.ForeignKeysTableModel(csgo.getPtcg().getForeignKeys(), csgo.getPtcg().getForeignKeyValues()));
                setComponentsPostInit();
                labelNumberOfRows.setText(csgo.getPtcg().getAllRows().size() + " rows in loaded table");
                jww.dispose();
                if (!csgo.getPtcg().hasAnyPunchedInRows()) {
                    ckbxIncludeAllNonMatchingPunchedInElements.setText("Include all rows in result");
                    if (ckbxPunchedInRowsOnly.isSelected()) {
                        if (!skipUi) {
                            JOptionPane.showMessageDialog(this, "Set to only show punched-in elements, but the loaded page has no punched-in elements, or it is not a table with right-aligned punch columns.\nDe-selecting \"Punched-In Rows Only\", continue as normal.", "No punches found", JOptionPane.WARNING_MESSAGE);
                        }
                        ckbxPunchedInRowsOnly.setSelected(false);
                        csgo.setOnlyPunchedIn(false);
                    }
                }
                if(skipUi) {
                    searchThenPrintTables();
                }
            } else {
                jww.dispose();
                if(e.getMessage().equals("The specified color rules are not valid.")) {
                    JOptionPane.showMessageDialog(rootPane, e.getMessage() + "\nThe syntax must be valid.\nFor example, \"red:a:f blue:g:i yellow:j:z\"\nOmitted ranges will cause affected rows to not appear for searching (or in the result).\nFor example, \"red:b:m blue:n:y\"\n    will cause all rows whose primary keys have the first letter matching 'a' and 'z' to be discarded.", "Color rules error", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(rootPane, "The file or URL entered was not able to be processed.\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                newButtonPressed();
            }
        }
    }
    
    /**
     * Allows us to directly pass an instance of this object to 
     * java.awt.EventQueue.invokeLater().  
     * 
     */
    @Override
    public void run() {
        if (skipUi) {
            this.setVisible(false);
            directPrintCsg();
            this.dispose();
        } else {
            setVisible(true);
        }
    }

    private void notAllQueries() {
        synchronized (csgo) {
            csgo.notAllQueries();
            listBuiltQueries.setListData(buildJlistData(csgo));
        }
    }

    private void showSelectTargetFile() {
        jfc.setFileFilter(new FileFilters.AllFilesFilter());
        if (jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            tfTargetFileOrUrl.setText(jfc.getSelectedFile().getAbsolutePath());
        }
    }

    private void directPrintCsg() {
        startFillForeignKeyTable();
    }

    private void loadCsgFile() {
        File selectedFile;
        jfc.setFileFilter(new FileFilters.CsgFileFilter());
        int returnVal = jfc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            selectedFile = jfc.getSelectedFile();
            newInstance(selectedFile);
            if (isCurrentWindowCleanSlate()) {
                dispose();
            }
        }
    }

    private void writeCsgFile() {
        synchronized (csgo) {
            jfc.setFileFilter(new FileFilters.CsgFileFilter());
            int retVal = jfc.showSaveDialog(this);
            if (retVal == JFileChooser.APPROVE_OPTION) {
                try {
                    File f = jfc.getSelectedFile();
                    f = ColorSynesthesiaGroups.writeCsgFile(csgo.getCsgString(), f);
                    csgo.setCsgName(ColorSynesthesiaGroups.getCsgName(f));
                    setQuerySetName(csgo.getCsgName());
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                    JOptionPane.showMessageDialog(this, ex.getLocalizedMessage(), "Error saving CSG file", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void setQuerySetName(String newName) {
        if ("".equals(newName)) {
            setTitle(WINDOW_TITLE_POSTFIX);
            labelQueriesList.setText("Queries");
        } else {
            setTitle(newName + " | " + WINDOW_TITLE_POSTFIX);
            labelQueriesList.setText("Queries (" + newName + ")");
        }
    }

    private boolean isCurrentWindowCleanSlate() {
        return DEFAULT_FILE_OR_URL.equals(tfTargetFileOrUrl.getText().trim())
                && DEFAULT_PRIMARY_KEY.equals(tfPrimaryKey.getText().trim())
                && DEFAULT_COLOR_RULES.equals(tfColorRules.getText().trim());
    }

    private void newButtonPressed() {
        newInstance();
        if (isCurrentWindowCleanSlate()) {
            dispose();
        }
    }

    private void showHelpDialog() {
        JHelpDialog hd = new JHelpDialog();
        hd.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        hd.setVisible(true);
    }

    private void startExecuteQuery() {
        synchronized (csgo) {
            if (!skipUi && !csgo.getPtcg().hasAnyPunchedInRows() && ckbxIncludeAllNonMatchingPunchedInElements.isSelected()) {
                JOptionPane.showMessageDialog(this, "The loaded table has no punched-in elements, or is not a table with right-aligned punch columns.\nSince \"Include all rows in result\" is selected, all rows from the table will be in the result, regardless of whether or not the row matches a query.", "Ignorning queries", JOptionPane.INFORMATION_MESSAGE);
            }
            if (csgo.getQueries().isEmpty() && !csgo.isIncludeAllMatchingNonPunchedInElements()) {
                JOptionPane.showMessageDialog(this,
                        "There is nothing to print, you must define at least one query.\nDisplaying HELP text...",
                        "No query", JOptionPane.WARNING_MESSAGE);
                showHelpDialog();
            } else {
                Thread t = new Thread(csgo);
                jww = new JWaitWindow("Searching");
                jww.setVisible(true);
                t.start();
            }
        }
    }

    private void searchThenPrintTables() {
        executeQueryDisplayModel = 1;
        startExecuteQuery();
    }

    private void searchThenShowTables() {
        executeQueryDisplayModel = 2;
        startExecuteQuery();
    }

    private void searchThenShowSexp() {
        executeQueryDisplayModel = 3;
        startExecuteQuery();
    }

    private void displaySexpJFrame() {
        JFrame sexpFrame = new JFrame(csgo.getCsgName() + " | Symbolic Expression (Lisp) Output");
        JTextArea JTextAreaN = new JTextArea(buildFullSexp(csgo), 40, 80);
        JScrollPane jsp = new JScrollPane(JTextAreaN);
        sexpFrame.add(jsp);
        sexpFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        sexpFrame.setSize(1000, 600);
        sexpFrame.setVisible(true);
    }

    private void setComponentsPreInit() {
        tableDiscoveredForeignKeys.setAutoCreateRowSorter(true);
        tableDiscoveredForeignKeys.setDragEnabled(false);
        tableDiscoveredForeignKeys.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tableDiscoveredForeignKeys.setRowSelectionAllowed(true);
        tableDiscoveredForeignKeys.setColumnSelectionAllowed(true);
        tableDiscoveredForeignKeys.getTableHeader().setReorderingAllowed(false);
        if ("".equals(tfTargetFileOrUrl.getText())) {
            tfTargetFileOrUrl.setText(DEFAULT_FILE_OR_URL);
        }
        if ("".equals(tfPrimaryKey.getText())) {
            tfPrimaryKey.setText(DEFAULT_PRIMARY_KEY);
        }
        if ("".equals(tfColorRules.getText())) {
            tfColorRules.setText(DEFAULT_COLOR_RULES);
        }
        ckbxPunchedInRowsOnly.setEnabled(true);
        buttonQueryAdd.setEnabled(false);
        buttonQueriesClear.setEnabled(false);
        listBuiltQueries.setEnabled(false);
        buttonQueryDelete.setEnabled(false);
        buttonSetupSave.setEnabled(false);
        buttonPrint.setEnabled(false);
        buttonSetupNew.setEnabled(false);
        buttonQueryNot.setEnabled(false);
        buttonQueriesNotAll.setEnabled(false);
        tableDiscoveredForeignKeys.setEnabled(false);
        listBuiltQueries.setEnabled(false);
        buttonBrowseTargetFile.setEnabled(true);
        ckbxPrimaryKeyIsHumanName.setEnabled(true);
        labelFileOrUrl.setEnabled(true);
        labelPrimaryKey.setEnabled(true);
        labelColorRules.setEnabled(true);
        labelQueriesList.setEnabled(false);
        labelQueryBuilderButtons.setEnabled(false);
        labelQueriesControl.setEnabled(false);
        buttonShowSexp.setEnabled(false);
        ckbxUseLastNameNotFirstName.setEnabled(false);
        ckbxIncludeAllNonMatchingPunchedInElements.setEnabled(false);
        buttonShowResults.setEnabled(false);
        ckbxPrintShowAllcolumns.setEnabled(false);
        ckbxIncludePrimaryKeySearchOption.setEnabled(true);
        buttonLoadPage.setRequestFocusEnabled(true);
    }

    private void setComponentsPostInit() {
        buttonQueryAdd.setEnabled(true);
        buttonQueriesClear.setEnabled(true);
        listBuiltQueries.setEnabled(true);
        buttonQueryDelete.setEnabled(true);
        buttonSetupSave.setEnabled(true);
        buttonPrint.setEnabled(true);
        buttonSetupNew.setEnabled(true);
        buttonLoadPage.setEnabled(false);
        tfTargetFileOrUrl.setEnabled(false);
        tfPrimaryKey.setEnabled(false);
        tfColorRules.setEnabled(false);
        ckbxPunchedInRowsOnly.setEnabled(false);
        buttonQueryNot.setEnabled(true);
        buttonQueriesNotAll.setEnabled(true);
        tableDiscoveredForeignKeys.setEnabled(true);
        listBuiltQueries.setEnabled(true);
        buttonBrowseTargetFile.setEnabled(false);
        ckbxPrimaryKeyIsHumanName.setEnabled(false);
        labelFileOrUrl.setEnabled(false);
        labelPrimaryKey.setEnabled(false);
        labelColorRules.setEnabled(false);
        labelQueriesList.setEnabled(true);
        labelQueryBuilderButtons.setEnabled(true);
        labelQueriesControl.setEnabled(true);
        buttonShowSexp.setEnabled(true);
        ckbxUseLastNameNotFirstName.setEnabled(false);
        ckbxIncludeAllNonMatchingPunchedInElements.setEnabled(true);
        buttonShowResults.setEnabled(true);
        ckbxPrintShowAllcolumns.setEnabled(true);
        ckbxIncludePrimaryKeySearchOption.setEnabled(false);
    }

    private void startFillForeignKeyTable() {
        synchronized (csgo) {
            String target;
            try {
                target = new URL(tfTargetFileOrUrl.getText()).getHost();
            } catch (MalformedURLException e) {
                target = new File(tfTargetFileOrUrl.getText()).getName();
            }

            csgo.setTargetFileOrUrl(tfTargetFileOrUrl.getText());
            csgo.setPrimaryKey(tfPrimaryKey.getText());
            csgo.setColorRules(tfColorRules.getText());
            //if(!skipUi) {
                csgo.setJColorGrouperQueries(this);
            //}
            Thread t = new Thread(csgo);
            jww = new JWaitWindow("Waiting for " + target + " to serve us");
            jww.setVisible(true);
            t.start();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelFileOrUrl = new javax.swing.JLabel();
        tfTargetFileOrUrl = new javax.swing.JTextField();
        buttonLoadPage = new javax.swing.JButton();
        labelPrimaryKey = new javax.swing.JLabel();
        tfPrimaryKey = new javax.swing.JTextField();
        labelColorRules = new javax.swing.JLabel();
        tfColorRules = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        tableDiscoveredForeignKeys = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        listBuiltQueries = new javax.swing.JList<>();
        buttonQueryAdd = new javax.swing.JToggleButton();
        buttonSetupSave = new javax.swing.JButton();
        buttonSetupLoad = new javax.swing.JButton();
        buttonQueriesClear = new javax.swing.JButton();
        buttonPrint = new javax.swing.JButton();
        ckbxPunchedInRowsOnly = new javax.swing.JCheckBox();
        buttonSetupNew = new javax.swing.JButton();
        buttonBrowseTargetFile = new javax.swing.JButton();
        labelQueriesList = new javax.swing.JLabel();
        labelQueryBuilderButtons = new javax.swing.JLabel();
        labelQueriesControl = new javax.swing.JLabel();
        buttonQueriesNotAll = new javax.swing.JButton();
        labelQueriesSetup = new javax.swing.JLabel();
        ckbxIncludeAllNonMatchingPunchedInElements = new javax.swing.JCheckBox();
        ckbxPrimaryKeyIsHumanName = new javax.swing.JCheckBox();
        buttonQueryNot = new javax.swing.JToggleButton();
        buttonShowSexp = new javax.swing.JButton();
        ckbxUseLastNameNotFirstName = new javax.swing.JCheckBox();
        buttonHelp = new javax.swing.JButton();
        buttonShowResults = new javax.swing.JButton();
        ckbxPrintShowAllcolumns = new javax.swing.JCheckBox();
        ckbxIncludePrimaryKeySearchOption = new javax.swing.JCheckBox();
        labelNumberOfRows = new javax.swing.JLabel();
        buttonQueryDelete = new javax.swing.JToggleButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        labelFileOrUrl.setText("File or URL");

        tfTargetFileOrUrl.setText("");
        tfTargetFileOrUrl.setMaximumSize(new java.awt.Dimension(72, 20));
        tfTargetFileOrUrl.setMinimumSize(new java.awt.Dimension(72, 20));

        buttonLoadPage.setText("Load Page");
        buttonLoadPage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonLoadPageActionPerformed(evt);
            }
        });

        labelPrimaryKey.setText("Primary Key");

        tfPrimaryKey.setText("");

        labelColorRules.setText("Color Rules");

        tfColorRules.setText("");

        tableDiscoveredForeignKeys.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"type1", "typeA", "classA", "..."},
                {"type2", "typeB", "classB", "..."},
                {"etc.", "etc.", "etc.", "..."}
            },
            new String [] {
                "Foreign", "Keys", "Appear ", "Here"
            }
        ));
        tableDiscoveredForeignKeys.setFillsViewportHeight(true);
        tableDiscoveredForeignKeys.setRequestFocusEnabled(false);
        tableDiscoveredForeignKeys.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableDiscoveredForeignKeysMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tableDiscoveredForeignKeys);

        listBuiltQueries.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Queries will appear here" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        listBuiltQueries.setRequestFocusEnabled(false);
        listBuiltQueries.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listBuiltQueriesMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(listBuiltQueries);

        buttonQueryAdd.setText("Add");
        buttonQueryAdd.setRequestFocusEnabled(false);
        buttonQueryAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonQueryAddActionPerformed(evt);
            }
        });

        buttonSetupSave.setText("Save");
        buttonSetupSave.setRequestFocusEnabled(false);
        buttonSetupSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSetupSaveActionPerformed(evt);
            }
        });

        buttonSetupLoad.setText("Load");
        buttonSetupLoad.setRequestFocusEnabled(false);
        buttonSetupLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSetupLoadActionPerformed(evt);
            }
        });

        buttonQueriesClear.setText("Clear");
        buttonQueriesClear.setRequestFocusEnabled(false);
        buttonQueriesClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonQueriesClearActionPerformed(evt);
            }
        });

        buttonPrint.setText("Print");
        buttonPrint.setRequestFocusEnabled(false);
        buttonPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonPrintActionPerformed(evt);
            }
        });

        ckbxPunchedInRowsOnly.setText("Punched-In Rows Only");
        ckbxPunchedInRowsOnly.setRequestFocusEnabled(false);
        ckbxPunchedInRowsOnly.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ckbxPunchedInRowsOnlyActionPerformed(evt);
            }
        });

        buttonSetupNew.setText("New");
        buttonSetupNew.setRequestFocusEnabled(false);
        buttonSetupNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSetupNewActionPerformed(evt);
            }
        });

        buttonBrowseTargetFile.setText("Browse...");
        buttonBrowseTargetFile.setRequestFocusEnabled(false);
        buttonBrowseTargetFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonBrowseTargetFileActionPerformed(evt);
            }
        });

        labelQueriesList.setText("Queries");

        labelQueryBuilderButtons.setText("Query");

        labelQueriesControl.setText("Queries");

        buttonQueriesNotAll.setText("Not All");
        buttonQueriesNotAll.setRequestFocusEnabled(false);
        buttonQueriesNotAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonQueriesNotAllActionPerformed(evt);
            }
        });

        labelQueriesSetup.setText("Setup");

        ckbxIncludeAllNonMatchingPunchedInElements.setText("Include all non-matching punched-in elements");
        ckbxIncludeAllNonMatchingPunchedInElements.setRequestFocusEnabled(false);
        ckbxIncludeAllNonMatchingPunchedInElements.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ckbxIncludeAllNonMatchingPunchedInElementsActionPerformed(evt);
            }
        });

        ckbxPrimaryKeyIsHumanName.setText("Primary key is human name");
        ckbxPrimaryKeyIsHumanName.setRequestFocusEnabled(false);
        ckbxPrimaryKeyIsHumanName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ckbxPrimaryKeyIsHumanNameActionPerformed(evt);
            }
        });

        buttonQueryNot.setText("Not");
        buttonQueryNot.setRequestFocusEnabled(false);

        buttonShowSexp.setText("Show S-exp");
        buttonShowSexp.setRequestFocusEnabled(false);
        buttonShowSexp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonShowSexpActionPerformed(evt);
            }
        });

        ckbxUseLastNameNotFirstName.setText("Use last name, not first name");
        ckbxUseLastNameNotFirstName.setRequestFocusEnabled(false);
        ckbxUseLastNameNotFirstName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ckbxUseLastNameNotFirstNameActionPerformed(evt);
            }
        });

        buttonHelp.setText("Help");
        buttonHelp.setRequestFocusEnabled(false);
        buttonHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonHelpActionPerformed(evt);
            }
        });

        buttonShowResults.setText("Show Results");
        buttonShowResults.setRequestFocusEnabled(false);
        buttonShowResults.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonShowResultsActionPerformed(evt);
            }
        });

        ckbxPrintShowAllcolumns.setText("Print/Show All Columns");
        ckbxPrintShowAllcolumns.setRequestFocusEnabled(false);
        ckbxPrintShowAllcolumns.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ckbxPrintShowAllcolumnsActionPerformed(evt);
            }
        });

        ckbxIncludePrimaryKeySearchOption.setText("Include primary key search option");
        ckbxIncludePrimaryKeySearchOption.setRequestFocusEnabled(false);
        ckbxIncludePrimaryKeySearchOption.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ckbxIncludePrimaryKeySearchOptionActionPerformed(evt);
            }
        });

        buttonQueryDelete.setText("Delete");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelPrimaryKey, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelFileOrUrl))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(tfPrimaryKey, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelColorRules)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tfColorRules))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(tfTargetFileOrUrl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(buttonBrowseTargetFile)))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(buttonLoadPage)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelNumberOfRows)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(ckbxIncludeAllNonMatchingPunchedInElements)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ckbxPrintShowAllcolumns)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(buttonShowResults)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(buttonShowSexp)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(buttonPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(ckbxPunchedInRowsOnly)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(ckbxPrimaryKeyIsHumanName)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(ckbxUseLastNameNotFirstName)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(ckbxIncludePrimaryKeySearchOption)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(labelQueriesList, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(labelQueriesControl)
                                    .addComponent(labelQueriesSetup))
                                .addGap(52, 52, 52))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(labelQueryBuilderButtons)
                                    .addComponent(buttonQueryAdd, javax.swing.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
                                    .addComponent(buttonQueryNot, javax.swing.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
                                    .addComponent(buttonQueriesClear, javax.swing.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
                                    .addComponent(buttonQueriesNotAll, javax.swing.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
                                    .addComponent(buttonSetupSave, javax.swing.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
                                    .addComponent(buttonSetupLoad, javax.swing.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
                                    .addComponent(buttonSetupNew, javax.swing.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
                                    .addComponent(buttonHelp, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(buttonQueryDelete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelFileOrUrl)
                    .addComponent(tfTargetFileOrUrl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonBrowseTargetFile))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelPrimaryKey)
                    .addComponent(tfPrimaryKey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelColorRules)
                    .addComponent(tfColorRules, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(labelQueriesList)
                        .addComponent(labelQueryBuilderButtons))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ckbxPrimaryKeyIsHumanName)
                        .addComponent(ckbxUseLastNameNotFirstName)
                        .addComponent(ckbxPunchedInRowsOnly)
                        .addComponent(ckbxIncludePrimaryKeySearchOption)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(buttonQueryAdd)
                        .addGap(5, 5, 5)
                        .addComponent(buttonQueryNot)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonQueryDelete)
                        .addGap(18, 18, 18)
                        .addComponent(labelQueriesControl)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonQueriesClear)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonQueriesNotAll)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelQueriesSetup)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonSetupSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonSetupLoad)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonSetupNew))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 499, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 499, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonPrint)
                    .addComponent(buttonShowSexp)
                    .addComponent(buttonLoadPage)
                    .addComponent(ckbxIncludeAllNonMatchingPunchedInElements)
                    .addComponent(buttonHelp)
                    .addComponent(buttonShowResults)
                    .addComponent(ckbxPrintShowAllcolumns)
                    .addComponent(labelNumberOfRows))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonLoadPageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonLoadPageActionPerformed
        startFillForeignKeyTable();
    }//GEN-LAST:event_buttonLoadPageActionPerformed

    private void buttonSetupSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSetupSaveActionPerformed
        writeCsgFile();
    }//GEN-LAST:event_buttonSetupSaveActionPerformed

    private void buttonSetupNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSetupNewActionPerformed
        newButtonPressed();
    }//GEN-LAST:event_buttonSetupNewActionPerformed

    private void tableDiscoveredForeignKeysMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableDiscoveredForeignKeysMouseClicked
        synchronized (csgo) {
            if (buttonQueryAdd.isSelected()) {
                int selRow = tableDiscoveredForeignKeys.getSelectionModel().getLeadSelectionIndex();
                int selCol = tableDiscoveredForeignKeys.getColumnModel().getSelectionModel().getLeadSelectionIndex();
                thisBuild.put(tableDiscoveredForeignKeys.getModel().getColumnName(selCol),
                        (String) tableDiscoveredForeignKeys.getValueAt(selRow, selCol));
                // If the user selects one row from each column...
                if (thisBuild.size() == tableDiscoveredForeignKeys.getModel().getColumnCount()) {
                    if (thisBuild.keySet().size() > 0) {
                        csgo.addQuery(thisBuild);
                        listBuiltQueries.setListData(buildJlistData(csgo));
                        buttonQueryAdd.setSelected(false);
                    } else {
                        JOptionPane.showMessageDialog(this, "Unable to determine selected cell.  Please, try again.\n(Multiple cells were accidentally selected.)", "Please try not to click and drag...", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        }
    }//GEN-LAST:event_tableDiscoveredForeignKeysMouseClicked

    private void buttonQueryAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonQueryAddActionPerformed
        synchronized (csgo) {
            if (buttonQueryAdd.isSelected()) {
                thisBuild = new HashMap<>();
            } else {
                if (thisBuild.keySet().size() > 0) {
                    csgo.addQuery(thisBuild);
                    listBuiltQueries.setListData(buildJlistData(csgo));
                } else {
                    JOptionPane.showMessageDialog(this, "Unable to determine selected cell.  Multiple cells were accidentally selected.  Please try again.", "Please try not to click and drag...", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_buttonQueryAddActionPerformed

    private void listBuiltQueriesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listBuiltQueriesMouseClicked
        synchronized (csgo) {
            if (buttonQueryNot.isSelected() && !listBuiltQueries.getSelectedValue().equals(LIST_OR_DELIMETER)) {
                int selected = listBuiltQueries.getSelectedIndex() / 2;
                csgo.setNotIndex(selected, !csgo.getNotList().get(selected));
                buttonQueryNot.setSelected(false);
                listBuiltQueries.setListData(buildJlistData(csgo));
            }
            if (buttonQueryDelete.isSelected() && !listBuiltQueries.getSelectedValue().equals(LIST_OR_DELIMETER)) {
                int selected = listBuiltQueries.getSelectedIndex() / 2;
                csgo.removeQuery(selected);
                buttonQueryDelete.setSelected(false);
                listBuiltQueries.setListData(buildJlistData(csgo));
            }
        }
    }//GEN-LAST:event_listBuiltQueriesMouseClicked

    private void buttonQueriesClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonQueriesClearActionPerformed
        listBuiltQueries.setListData(new String[]{"Queries will appear here"});
        csgo.clearQueries();
    }//GEN-LAST:event_buttonQueriesClearActionPerformed

    private void buttonSetupLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSetupLoadActionPerformed
        loadCsgFile();
    }//GEN-LAST:event_buttonSetupLoadActionPerformed

    private void buttonPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonPrintActionPerformed
        searchThenPrintTables();
    }//GEN-LAST:event_buttonPrintActionPerformed

    private void ckbxPrimaryKeyIsHumanNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ckbxPrimaryKeyIsHumanNameActionPerformed
        ckbxUseLastNameNotFirstName.setEnabled(ckbxPrimaryKeyIsHumanName.isSelected());
        csgo.setPkIsName(ckbxPrimaryKeyIsHumanName.isSelected());
    }//GEN-LAST:event_ckbxPrimaryKeyIsHumanNameActionPerformed

    private void buttonQueriesNotAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonQueriesNotAllActionPerformed
        notAllQueries();
    }//GEN-LAST:event_buttonQueriesNotAllActionPerformed

    private void buttonBrowseTargetFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonBrowseTargetFileActionPerformed
        showSelectTargetFile();
    }//GEN-LAST:event_buttonBrowseTargetFileActionPerformed

    private void buttonShowSexpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonShowSexpActionPerformed
        searchThenShowSexp();
    }//GEN-LAST:event_buttonShowSexpActionPerformed

    private void buttonHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonHelpActionPerformed
        showHelpDialog();
    }//GEN-LAST:event_buttonHelpActionPerformed

    private void buttonShowResultsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonShowResultsActionPerformed
        searchThenShowTables();
    }//GEN-LAST:event_buttonShowResultsActionPerformed

    private void ckbxPunchedInRowsOnlyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ckbxPunchedInRowsOnlyActionPerformed
        csgo.setOnlyPunchedIn(ckbxPunchedInRowsOnly.isSelected());
    }//GEN-LAST:event_ckbxPunchedInRowsOnlyActionPerformed

    private void ckbxUseLastNameNotFirstNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ckbxUseLastNameNotFirstNameActionPerformed
        csgo.setLastNameNotFirst(ckbxUseLastNameNotFirstName.isSelected());
    }//GEN-LAST:event_ckbxUseLastNameNotFirstNameActionPerformed

    private void ckbxIncludePrimaryKeySearchOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ckbxIncludePrimaryKeySearchOptionActionPerformed
        csgo.setIncludePrimaryKeySearchOption(ckbxIncludePrimaryKeySearchOption.isSelected());
    }//GEN-LAST:event_ckbxIncludePrimaryKeySearchOptionActionPerformed

    private void ckbxIncludeAllNonMatchingPunchedInElementsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ckbxIncludeAllNonMatchingPunchedInElementsActionPerformed
        csgo.setIncludeAllMatchingNonPunchedInElements(ckbxIncludeAllNonMatchingPunchedInElements.isSelected());
    }//GEN-LAST:event_ckbxIncludeAllNonMatchingPunchedInElementsActionPerformed

    private void ckbxPrintShowAllcolumnsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ckbxPrintShowAllcolumnsActionPerformed
        csgo.setShowAllColumns(ckbxPrintShowAllcolumns.isSelected());
    }//GEN-LAST:event_ckbxPrintShowAllcolumnsActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonBrowseTargetFile;
    private javax.swing.JButton buttonHelp;
    private javax.swing.JButton buttonLoadPage;
    private javax.swing.JButton buttonPrint;
    private javax.swing.JButton buttonQueriesClear;
    private javax.swing.JButton buttonQueriesNotAll;
    private javax.swing.JToggleButton buttonQueryAdd;
    private javax.swing.JToggleButton buttonQueryDelete;
    private javax.swing.JToggleButton buttonQueryNot;
    private javax.swing.JButton buttonSetupLoad;
    private javax.swing.JButton buttonSetupNew;
    private javax.swing.JButton buttonSetupSave;
    private javax.swing.JButton buttonShowResults;
    private javax.swing.JButton buttonShowSexp;
    private javax.swing.JCheckBox ckbxIncludeAllNonMatchingPunchedInElements;
    private javax.swing.JCheckBox ckbxIncludePrimaryKeySearchOption;
    private javax.swing.JCheckBox ckbxPrimaryKeyIsHumanName;
    private javax.swing.JCheckBox ckbxPrintShowAllcolumns;
    private javax.swing.JCheckBox ckbxPunchedInRowsOnly;
    private javax.swing.JCheckBox ckbxUseLastNameNotFirstName;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel labelColorRules;
    private javax.swing.JLabel labelFileOrUrl;
    private javax.swing.JLabel labelNumberOfRows;
    private javax.swing.JLabel labelPrimaryKey;
    private javax.swing.JLabel labelQueriesControl;
    private javax.swing.JLabel labelQueriesList;
    private javax.swing.JLabel labelQueriesSetup;
    private javax.swing.JLabel labelQueryBuilderButtons;
    private javax.swing.JList<String> listBuiltQueries;
    private javax.swing.JTable tableDiscoveredForeignKeys;
    private javax.swing.JTextField tfColorRules;
    private javax.swing.JTextField tfPrimaryKey;
    private javax.swing.JTextField tfTargetFileOrUrl;
    // End of variables declaration//GEN-END:variables

    private static String buildFullSexp(ColorSynesthesiaGroups cgo) {
        synchronized (cgo) {
            String result = ";; ---- QUERY ------------------------\n";
            result += "'" + cgo.getQuerySexp() + "\n";
            result += ";; ---- RESULT -----------------------\n";
            result += "'" + cgo.getResultSexp();
            return result;
        }
    }

    private static String[] buildJlistData(ColorSynesthesiaGroups cgo) {
        synchronized (cgo) {
            if (cgo.getQueries().isEmpty()) {
                return new String[]{""};
            }
            List<String> result = new ArrayList<>();
            for (int i = 0; i < cgo.getQueriesSize(); i++) {
                String builder = "";
                if (cgo.getNotList().get(i)) {
                    builder += "!NOT ";
                }
                for (String k : cgo.getQueries().get(i).keySet()) {
                    builder += k + "=" + cgo.getQueries().get(i).get(k) + " AND ";
                }
                builder = builder.substring(0, builder.length() - 4);
                result.add(builder);
                result.add(LIST_OR_DELIMETER);
            }
            result.remove(result.size() - 1);
            String[] strArrayResult = new String[result.size()];
            return result.toArray(strArrayResult);
        }
    }

    private static final String LIST_OR_DELIMETER = "OR";
    private Map<String, String> thisBuild;
    private JFileChooser jfc;
    private JWaitWindow jww;
    private boolean skipUi;
    private final ColorSynesthesiaGroups csgo;
    private int executeQueryDisplayModel;

}
