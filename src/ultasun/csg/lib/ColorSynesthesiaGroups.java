package ultasun.csg.lib;

import ultasun.csg.gui.JColorSynesthesiaGroups;
import ultasun.csg.gui.Runnables;
import ultasun.csg.iohelper.StringFile;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * ColorGrouperQueries holds a PunchTableColorGrouper, a list of queries, and
 * allows access to the query set search result (via getPtcg()). It also holds
 * all of the configuration options (as seen on the GUI). It has the ability to
 * serialize and name the current configuration (known as a Color Grouper
 * Queries file).
 *
 * Class methods all make use of a synchronize(this) {} block in the outermost
 * scope.
 *
 * ColorGrouperQueries is a Runnable, and is meant to be executed twice. Once
 * after the basic parameters are set, and multiple times again after the
 * queries are set. Queries may be cleared after executing a search, and new
 * queries loaded; without having to execute step 1 again (which usually
 * involves downloading a file from the Internet).
 *
 * If setJColorGrouperQueries() is utilized before any run() invocations, then
 * each run will make call-backs to update UI elements to indicate to the user
 * that the process has complete. It does this by invoking
 * java.awt.EventQueue.invokeLater(), with a dedicated Runnable for each action,
 * from "this" Thread. So the only object that is touched by multiple Threads is
 * this (ColorGrouperQueries). While the other Thread has access to the object
 * reference for a swing component, it only passes it as a constructor argument
 * to the runnable during EventQueue.invokeLater().
 *
 * @author ultasun
 */
public class ColorSynesthesiaGroups implements Runnable {

    /**
     * The run method needs to be executed twice to finish the full process path
     * for this class. After the first execution, the page has been downloaded
     * and parsed, and the system knows of all the foreign keys present in the
     * table. This is when queries are added. Then after the second execution,
     * the search results are available.
     *
     * The methods in this class will only move from NEW to LOADED_PAGE, from
     * LOADED_PAGE to EXECUTED_SEARCH, and from EXECUTED_SEARCH to LOADED_PAGE.
     */
    public enum ColorGrouperQueriesStatus {
        NEW, LOADED_PAGE, EXECUTED_SEARCH;
    }

    /**
     * Get the current run status of this instance. In case you somehow forgot
     * (JColorGrouperQueries remembers.)
     *
     * @return ColorGrouperQueriesStatus The current process path progress.
     */
    public ColorGrouperQueriesStatus getStatus() {
        return current;
    }

    /**
     * Start a new Color Synesthesia Groups file.
     */
    public ColorSynesthesiaGroups() {
        synchronized (this) {
            current = ColorGrouperQueriesStatus.NEW;
            ptcg = null;
            targetFileOrUrl = "";
            primaryKey = "";
            colorRules = "";
            onlyPunchedIn = includeAllMatchingNonPunchedInElements = pkIsName = lastNameNotFirst = showAllColumns = includePrimaryKeySearchOption = false;
            queriesNotList = new ArrayList<>();
            queries = new ArrayList<>();
            csgName = "";
        }
    }

    /**
     * Load an existing Color Synesthesia Groups file.
     *
     * @param csgFile File of the .csg file to load.
     * @throws java.io.IOException if .csg file failed to load.
     */
    public ColorSynesthesiaGroups(File csgFile) throws IOException {
        this();
        synchronized (this) {
            csgName = getCsgName(csgFile);
            String fromCsgString = StringFile.read(csgFile);
            StringTokenizer st = new StringTokenizer(fromCsgString, "\n");
            targetFileOrUrl = st.nextToken();
            primaryKey = st.nextToken();
            colorRules = st.nextToken();
            configureBooleans(st.nextToken().split(" "));
            configureQueries(st);
        }
    }

    /**
     * Set this parameter before invoking run(). Will call-back to the GUI when
     * downloading/processing is complete.
     *
     * @param jcsg The JColorSynesthesiaGroups JFrame you created this object in
 (this).
     */
    public void setJColorGrouperQueries(JColorSynesthesiaGroups jcsg) {
        synchronized (this) {
            this.thisGui = jcsg;
        }
    }

    /**
     *
     */
    @Override
    public void run() {
        synchronized (this) {
            if (ptcg == null) {
                Exception result = null;
                try {
                    ptcg = new PunchTableColorGrouper(primaryKey, targetFileOrUrl, colorRules, onlyPunchedIn, pkIsName, lastNameNotFirst, includePrimaryKeySearchOption);
                    current = ColorGrouperQueriesStatus.LOADED_PAGE;
                } catch (Exception ex) {
                    result = ex;
                }
                if (thisGui != null) {
                    java.awt.EventQueue.invokeLater(new Runnables.JcsgFillForeignKeyTable(thisGui, result));
                }
            } else {
                ptcg.addQueriesToResult(queries, queriesNotList);
                if (includeAllMatchingNonPunchedInElements) {
                    ptcg.addNonMatchingElementsToResult();
                }
                current = ColorGrouperQueriesStatus.EXECUTED_SEARCH;
                if (thisGui != null) {
                    java.awt.EventQueue.invokeLater(new Runnables.JcsgExecuteQuery(thisGui));
                }
            }
        }
    }

    /**
     * Serialize the current state of the object to a human-readable form. This
     * is a CSG file.
     *
     * @return The String representing this CSG object.
     */
    public String getCsgString() {
        synchronized (this) {
            String result = "";
            result += targetFileOrUrl + "\n";
            result += primaryKey + "\n";
            result += colorRules + "\n";
            result += onlyPunchedIn + " ";
            result += includeAllMatchingNonPunchedInElements + " ";
            result += pkIsName + " ";
            result += lastNameNotFirst + " ";
            result += showAllColumns + " ";
            result += includePrimaryKeySearchOption + "\n";
            for (int i = 0; i < queries.size(); i++) {
                if (queriesNotList.get(i)) {
                    result += "!";
                }
                for (String s : queries.get(i).keySet()) {
                    result += s + "=" + queries.get(i).get(s) + "^";
                }
                result = result.substring(0, result.length() - 1);
                result += "\n";
            }
            return result;
        }
    }

    public void addQuery(Map<String, String> query) {
        synchronized (this) {
            if (!queries.contains(query)) {
                queries.add(query);
                queriesNotList.add(false);
            }
        }
    }

    public void removeQuery(int index) {
        synchronized (this) {
            queries.remove(index);
            queriesNotList.remove(index);
        }
    }

    /**
     * Clear all queries and results.
     */
    public void clearQueries() {
        synchronized (this) {
            queries.clear();
            queriesNotList.clear();
            clearResultData();
        }
    }

    public void setNotIndex(int i, boolean value) {
        synchronized (this) {
            queriesNotList.set(i, value);
        }
    }

    public void notAllQueries() {
        synchronized (this) {
            for (int i = 0; i < queriesNotList.size(); i++) {
                queriesNotList.set(i, !queriesNotList.get(i));
            }
        }
    }

    public int getQueriesSize() {
        synchronized (this) {
            return queries.size(); // Note is same as queriesNotList.size().  
        }
    }

    /**
     * Concatenate the query and result s-expressions.
     *
     * @return String concatenation of getQuerySexp() and getResultSexp().
     */
    @Override
    public String toString() {
        synchronized (this) {
            return getQuerySexp() + getResultSexp();
        }
    }

    /**
     * Build the query S-Expression in a human-readable format with newlines.
     *
     * @return String containing the query S-Expression.
     */
    public String getQuerySexp() {
        synchronized (this) {
            String result = "(cg-query \n";
            if (queries.isEmpty()) {
                result += " ()\n";
            } else {
                for (int i = 0; i < queries.size(); i++) {
                    result += " (";
                    if (queriesNotList.get(i)) {
                        result += "NOT ";
                    }
                    boolean notFirst = false;
                    for (String k : queries.get(i).keySet()) {
                        if (notFirst) {
                            result += " ";
                        }
                        result += "(\"" + k + "\" . \"" + queries.get(i).get(k) + "\")";
                        notFirst = true;
                    }
                    result += ")\n";
                }
            }
            result += ")\n";
            return result;
        }
    }

    /**
     * Get the name of this query set, or configuration.
     *
     * @return String of the file name.
     */
    public String getCsgName() {
        synchronized (this) {
            return csgName;
        }
    }

    /**
     * Set the name of this query set, or configuration. Note that this will
     * also be the file name.
     *
     * @param name String desired name.
     */
    public void setCsgName(String name) {
        synchronized (this) {
            csgName = name;
        }
    }

    /**
     * Clear the result data from any queries that have been executed. Note:
     * this will not clear the queries.
     */
    public void clearResultData() {
        synchronized (this) {
            ptcg.clearResultSet();
            current = ColorGrouperQueriesStatus.LOADED_PAGE;
        }
    }

    /**
     * Get the result S-Expression.
     *
     * @return String containing S-Expression split into readable format with
     * newlines.
     */
    public String getResultSexp() {
        synchronized (this) {
            return ptcg.toString();
        }
    }

    /**
     * Access the internal PunchTableColorGrouper. This is provided to relieve
     * the ColorGrouperQueries class of an unnecessary number of additional
     * getters & setters.
     *
     * This is bad, however; because we can break our thread-safety this way. So
     * this needs to be addressed eventually.
     *
     * @return PunchTableColorGrouper internal.
     */
    public PunchTableColorGrouper getPtcg() {
        synchronized (this) {
            return ptcg;
        }
    }

    public List<Map<String, String>> getQueries() {
        synchronized (this) {
            return queries;
        }
    }

    public List<Boolean> getNotList() {
        synchronized (this) {
            return queriesNotList;
        }
    }

    public void setQueriesNotList(List<Boolean> queriesNotList) {
        synchronized (this) {
            this.queriesNotList = queriesNotList;
        }
    }

    public boolean isOnlyPunchedIn() {
        synchronized (this) {
            return onlyPunchedIn;
        }
    }

    public void setOnlyPunchedIn(boolean onlyPunchedIn) {
        synchronized (this) {
            this.onlyPunchedIn = onlyPunchedIn;
        }
    }

    public boolean isIncludeAllMatchingNonPunchedInElements() {
        synchronized (this) {
            return includeAllMatchingNonPunchedInElements;
        }
    }

    public void setIncludeAllMatchingNonPunchedInElements(boolean includeAllMatchingNonPunchedInElements) {
        synchronized (this) {
            this.includeAllMatchingNonPunchedInElements = includeAllMatchingNonPunchedInElements;
        }
    }

    public boolean isPkIsName() {
        synchronized (this) {
            return pkIsName;
        }
    }

    public void setPkIsName(boolean pkIsName) {
        synchronized (this) {

            this.pkIsName = pkIsName;
        }
    }

    public boolean isLastNameNotFirst() {
        synchronized (this) {
            return lastNameNotFirst;
        }
    }

    public void setLastNameNotFirst(boolean lastNameNotFirst) {
        synchronized (this) {
            this.lastNameNotFirst = lastNameNotFirst;
        }
    }

    public boolean isShowAllColumns() {
        synchronized (this) {
            return showAllColumns;
        }
    }

    public void setShowAllColumns(boolean showAllColumns) {
        synchronized (this) {
            this.showAllColumns = showAllColumns;
        }
    }

    public String getTargetFileOrUrl() {
        synchronized (this) {
            return targetFileOrUrl;
        }
    }

    public void setTargetFileOrUrl(String targetFileOrUrl) {
        synchronized (this) {
            this.targetFileOrUrl = targetFileOrUrl;
        }
    }

    public String getColorRules() {
        synchronized (this) {
            return colorRules;
        }
    }

    public void setColorRules(String colorRules) {
        synchronized (this) {
            this.colorRules = colorRules;
        }
    }

    public void setPrimaryKey(String primaryKey) {
        synchronized (this) {
            this.primaryKey = primaryKey;
        }
    }

    public String getPrimaryKey() {
        synchronized (this) {
            return primaryKey;
        }
    }

    public boolean isIncludePrimaryKeySearchOption() {
        synchronized (this) {
            return includePrimaryKeySearchOption;
        }
    }

    public void setIncludePrimaryKeySearchOption(boolean includePrimaryKeySearchOption) {
        synchronized (this) {
            this.includePrimaryKeySearchOption = includePrimaryKeySearchOption;
        }
    }

    /**
     * Return the name of this query set/configuration. It is just the file
     * name.
     *
     * @param f The File containing the CSG data.
     * @return The query set/configuration String name.
     */
    public static String getCsgName(File f) {
        return f.getName().substring(0, f.getName().length() - 4);
    }

    /**
     * Write a CSG file.
     *
     * @param csgString The String as returned by getCsgString
     * @param f The target File
     * @return The File that was actually written, incase it needed the
     * extension added.
     * @throws java.io.IOException
     */
    public static File writeCsgFile(String csgString, File f) throws IOException {
        String path = f.getAbsolutePath().trim();
        if (!path.toLowerCase().endsWith(".csg")) {
            path += ".csg";
            f = new File(path);
        }
        StringFile.write(csgString, f);
        return f;
    }

    private void configureBooleans(String[] booleans) {
        onlyPunchedIn = new Boolean(booleans[0]);
        includeAllMatchingNonPunchedInElements = new Boolean(booleans[1]);
        pkIsName = new Boolean(booleans[2]);
        lastNameNotFirst = new Boolean(booleans[3]);
        showAllColumns = new Boolean(booleans[4]);
        includePrimaryKeySearchOption = new Boolean(booleans[5]);
    }

    private void configureQueries(StringTokenizer st) {
        while (st.hasMoreTokens()) {
            String thisToken = st.nextToken();
            boolean thisNot = thisToken.charAt(0) == '!';
            if (thisNot) {
                thisToken = thisToken.substring(1);
            }
            Map<String, String> thisQuery = new HashMap<>();
            for (String s : thisToken.split("\\^")) {
                thisQuery.put(s.split("=")[0], s.split("=")[1]);
            }
            queriesNotList.add(thisNot);
            queries.add(thisQuery);
        }
    }

    private ColorGrouperQueriesStatus current;
    private PunchTableColorGrouper ptcg;
    private JColorSynesthesiaGroups thisGui;
    private List<Map<String, String>> queries;
    private List<Boolean> queriesNotList;
    private boolean onlyPunchedIn, includeAllMatchingNonPunchedInElements, pkIsName, lastNameNotFirst, showAllColumns, includePrimaryKeySearchOption;
    private String csgName, targetFileOrUrl, primaryKey, colorRules;
}
