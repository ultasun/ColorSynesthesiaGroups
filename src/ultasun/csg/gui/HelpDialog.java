package ultasun.csg.gui;

/**
 *
 * @author ultasun
 */
public class HelpDialog extends javax.swing.JFrame {

        private static final String HELP_STRING
            = "Welcome to Color Synesthesia Groups!  Please read these instructions in their entirety.\n\n"
            + "This tool allows you to search a large table (which may include punched-in/punched-out columns on the right side) by the search criteria\n"
            + "(known as 'foreign keys') and group the results into colors assigned by letters.  You specify which column contains the unique row data,\n"
            + "(the column by which the program will color sort by), and the program will detect possible foreign key columns.  The program will also detect each row's\n"
            + "punch status, if applicable.  There is no limit to the number of rows, columns, foreign keys, punch columns...and the groups may be named anything.\n\n"
            + "Start by entering a file or URL into the \"File or URL\" box.\n"
            + "Then, indicate to the program the column title from the table within the file or URL you would like to use as the \"Primary Key\"\n"
            + "Next, indicate to the program the color grouping rules you would like, an example for the rule syntax is provided.\n"
            + "Select whether you need to see \"Punched-in Rows Only\".\n"
            + "Select whether you need the \"Primary Key is human name\".\n"
            + "Select the \"Load Page\" button to download and look for foreign key values in the table.\n\n"
            + "After pressing \"Load Page\", the table should be filled with possible foreign key values.  If not, check your \"Primary Key\".\n"
            + "1) Press the \"Add\" button\n"
            + "2) Click on foreign keys to match\n"
            + "3) Press \"Add\" button again when finished selecting foreign keys.\n"
            + "4) To delete, select the query first, and press \"Delete\" second.\n"
            + "Note: If a key from all columns are selected during \"Add\", then \"Add\" will disable after selecting the last foreign key.\n\n"
            + "Once you have defined all desired queries, choose whether you need to \"Include all non-matching punched-in elements\" to be included in the search result.\n\n"
            + "After completing the above, you may press \"Print\" to print the result.\n"
            + "You may also \"Show Results\" in a windowed table for each color group.\n"
            + "A S-Expression view of the data may be also be seen by clicking \"View Sexp\".\n\n"
            + "The configuration may be loaded or saved using the \"Load\" and \"Save\" found under the \"Setup\" area.\n\n"
            + "If \"Print/Show All Columns\" is not selected, then the search result will show the \"Primary Key\", all foreign keys, and no additional unique row data.\n\n"
            + "Note: After executing the search (\"Show Results\", \"Show Sexp\", \"Print\"), it is required to create a \"New\" setup, or to \"Load\" another one, if it is necessary\n"
            + "to modify the query.  The query may not be modified after the search without restarting the program (which may be accomplished by pressing \"New\" or \"Load\")";
        
        private static final String NEW_HELP_STRING = "";
    
    /**
     * Creates new form HelpDialog
     */
    public HelpDialog() {
        initComponents();
        setTitle("Help | Color Synesthesia Groups");
        jTextArea1.setText(HELP_STRING);
        jTextArea1.setEditable(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jButton1.setText("Close");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 880, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        dispose();
    }//GEN-LAST:event_jButton1ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
}