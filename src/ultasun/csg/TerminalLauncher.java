package ultasun.csg;

import ultasun.csg.lib.ColorSynesthesiaGroups;
import ultasun.csg.gui.JColorSynesthesiaGroups;
import ultasun.csg.iohelper.CsgPrinter;
import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Finally, the terminal version. This version is only a novelty to help confirm
 * that the ColorGrouperQuery build/search feature has been completely isolated
 * from JColorGrouperQueries (it has). This version is also good to study,
 * compared to the GUI version, to see how the underlying library works.
 *
 * @author ultasun
 */
public class TerminalLauncher implements Runnable {
    private final boolean headless;
    private final String url;
    private File argumentCsg;

    public TerminalLauncher() {
        this(false);
    }

    public TerminalLauncher(boolean headless) {
        this(headless, null);
    }

    public TerminalLauncher(boolean headless, File csgf) {
        this(headless, csgf, null);
    }
    
    public TerminalLauncher(boolean headless, File csgf, String url) {
        this.headless = headless;
        this.argumentCsg = csgf;       
        this.url = url;
    }

    @Override
    public void run() {
        if (headless) {
            headless();
        } else {
            wizardLoop();
        }
    }

    private void headless() {
        try {
            System.out.println("Loading CSG file and allocating memory...");
            ColorSynesthesiaGroups csgo = new ColorSynesthesiaGroups(argumentCsg);
            if(url != null) {
                csgo.setTargetFileOrUrl(url);
            }
            System.out.println("...downloading and parsing...");
            csgo.run();
            System.out.println("...found " + csgo.getPtcg().getAllRows().size() + " elements on the table...");
            System.out.println("...executing search...");
            csgo.run();
            System.out.println("...found " + csgo.getPtcg().getResultRowCount() + " elements matching queries...");
            System.out.println("...printing " + csgo.getPtcg().getColorCount() + " color groups...");
            CsgPrinter.printJobPerColor(csgo, true);
            System.out.println("...complete.");
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private void wizardLoop() {
        Scanner s = new Scanner(System.in);
        do {
            wizard(s);
        } while (askQuestion("Would you like to run the wizard again?", false, s));
    }

    private void wizard(Scanner s) {
        ColorSynesthesiaGroups csgo;
        System.out.println("Welcome to Color Synesthesia Groups");
        if (argumentCsg != null || askQuestion("Do you have a CSG file?", false, s)) {
            if(argumentCsg == null) {
                System.out.print("Enter the full path for the CSG file: \n");
                argumentCsg = new File(s.nextLine());
            }
            try {
                csgo = new ColorSynesthesiaGroups(argumentCsg);
            } catch (IOException e) {
                System.out.println("Error loading file:\n" + e.getMessage());
                return;
            } 
            if (askQuestion("Would you like to edit the CSG file?", false, s)) {
                editCsgFile(csgo, s, false);
            }
        } else {
            csgo = new ColorSynesthesiaGroups();
            editCsgFile(csgo, s, true);
        }
        System.out.println("Downloading...");
        csgo.run();
        System.out.println("...download complete.");
        do {
            if (askQuestion("Would you like to modify the queries?", false, s)) {
                queryBuilder(csgo, s);
            }
            System.out.println("Searching...");
            csgo.run();
            System.out.println("...searching complete");
            if (askQuestion("Would you like to see the query and result on screen, now?", true, s)) {
                System.out.println("Search query: ");
                System.out.println(csgo.getQuerySexp());
                System.out.println("Search result: ");
                System.out.println(csgo.getResultSexp());
            }
        } while (askQuestion("Would you like to modify the queries or search again?", true, s));

        boolean breakLoop = false;
        while (!breakLoop && askQuestion("Would you like to save the current configuration?", true, s)) {
            String path;
            System.out.print("Enter a full file path to use (end the file name with .csg to use with GUI program: \n");
            path = s.nextLine();
            try {
                ColorSynesthesiaGroups.writeCsgFile(csgo.getCsgString(), new File(path));
            } catch (Exception e) {
                System.out.println("Error: " + e.getLocalizedMessage());
            } finally {
                System.out.println("Saved file " + path);
                breakLoop = true;
            }
        }

        if (askQuestion("Would you like to print out the result?", false, s)) {
            try {
                CsgPrinter.printJobPerColor(csgo, false);
            } catch (HeadlessException he) {
                System.out.println("Printing in headless mode.");
                try {
                    CsgPrinter.printJobPerColor(csgo, true);
                } catch (Exception e) {
                    System.out.println("Failed to print in headless mode: " + e.getLocalizedMessage());
                }
            } catch (Exception e) {
                System.out.println("Failed to print: " + e.getLocalizedMessage());
            }
        }
    }

    private static void queryBuilder(ColorSynesthesiaGroups csgo, Scanner s) {
        System.out.println("These are the foreign keys: ");
        System.out.println(csgo.getPtcg().getForeignKeysSexp());
        System.out.println("This is your query: ");
        System.out.println(csgo.getQuerySexp());
        if (askQuestion("Would you like to add a query?", true, s)) {
            do {
                Map<String, String> query = new HashMap<>();
                String fk, value;
                do {
                    System.out.print("Enter the foreign key name: ");
                    fk = s.nextLine();
                    System.out.print("Enter the foreign key value: ");
                    value = s.nextLine();
                    query.put(fk, value);
                } while (query.size() < csgo.getPtcg().getForeignKeys().size() && askQuestion("Would you like to add another foreign key/value to this as an AND clause?", false, s));
                csgo.addQuery(query);
            } while (askQuestion("Would you like to add another query as an OR clause?", false, s));
        }
        if (askQuestion("Would you like to clear all the queries?", false, s)) {
            csgo.clearQueries();
        }
        if (askQuestion("Would you like to modify the queries?", true, s)) {
            queryBuilder(csgo, s);
        }
    }

    private static void newFileHelper(boolean newFile, String s, String x) {
        System.out.print(s);
        if (!newFile) {
            System.out.print(".  Press enter to keep current value: \n[" + x + "]\n");
        } else {
            System.out.print(": ");
        }
    }

    private static void editCsgFile(ColorSynesthesiaGroups csgo, Scanner s, boolean newFile) {
        newFileHelper(newFile, "Enter the File or URL path", csgo.getTargetFileOrUrl());
        csgo.setTargetFileOrUrl(blankForDefaultResponse(s.nextLine(), csgo.getTargetFileOrUrl()));
        newFileHelper(newFile, "Enter the Primary Key", csgo.getPrimaryKey());
        csgo.setPrimaryKey(blankForDefaultResponse(s.nextLine(), csgo.getPrimaryKey()));
        newFileHelper(false, "Enter the Color Rules", JColorSynesthesiaGroups.DEFAULT_COLOR_RULES);
        csgo.setColorRules(blankForDefaultResponse(s.nextLine(), JColorSynesthesiaGroups.DEFAULT_COLOR_RULES));

        csgo.setOnlyPunchedIn(askQuestion("Only punched-in elements, or all elements?", csgo.isOnlyPunchedIn(), s));
        csgo.setPkIsName(askQuestion("Primary key is human name?", csgo.isPkIsName(), s));
        csgo.setLastNameNotFirst(askQuestion("Use last name, not first name?", csgo.isLastNameNotFirst(), s));
        csgo.setIncludePrimaryKeySearchOption(askQuestion("Include primary key in search option?", csgo.isIncludePrimaryKeySearchOption(), s));
        csgo.setIncludeAllMatchingNonPunchedInElements(askQuestion("Include all non-matching punched-in elements in result?", csgo.isIncludeAllMatchingNonPunchedInElements(), s));
        csgo.setShowAllColumns(askQuestion("Show all columns, or only foreign-key columns in result?", csgo.isShowAllColumns(), s));
    }

    private static String blankForDefaultResponse(String input, String defaultResponse) {
        if (input.length() == 0) {
            return defaultResponse;
        } else {
            return input;
        }
    }

    private static boolean fromYesNo(String value) {
        if (value.toLowerCase().startsWith("y")) {
            return true;
        } else {
            return false;
        }
    }

    private static String toYesNo(boolean value) {
        if (value) {
            return "y";
        } else {
            return "n";
        }
    }

    private static boolean askQuestion(String yesOrNoQuestion, boolean defaultResponse, Scanner s) {
        String answer;
        System.out.print(yesOrNoQuestion + " (y/n) [" + toYesNo(defaultResponse) + "] ");
        answer = s.nextLine();
        if (answer.length() == 0) {
            answer = toYesNo(defaultResponse);
        }
        return fromYesNo(answer);
    }
}
