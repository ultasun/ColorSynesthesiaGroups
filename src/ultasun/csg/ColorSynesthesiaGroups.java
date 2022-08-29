package ultasun.csg;

import ultasun.csg.gui.JColorSynesthesiaGroups;
import ultasun.csg.gui.JcsgExecuteQuery;
import ultasun.csg.gui.JcsgFillForeignKeyTable;
import ultasun.csg.iohelper.GetTextFromFile;
import ultasun.csg.punchtable.PunchTableColorGrouper;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Thread-safe
 *
 * @author ultasun
 */
public class ColorSynesthesiaGroups implements Runnable {
    private PunchTableColorGrouper ptcg;
    private JColorSynesthesiaGroups thisGui;
    private List<Map<String, String>> queries;
    private List<Boolean> queriesNotList;
    private boolean hasExecutedQuery, hasDownloadedTable;
    private boolean onlyPunchedIn, includeAllMatchingNonPunchedInElements, pkIsName, lastNameNotFirst, showAllColumns, includePrimaryKeySearchOption;
    private String csgName;
    private String targetFileOrUrl, primaryKey, colorRules;

    public ColorSynesthesiaGroups() {
        synchronized (this) {
            hasExecutedQuery = hasDownloadedTable = false;
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

    public ColorSynesthesiaGroups(File csgFile) {
        this();
        synchronized (this) {
            csgName = getNewCsgName(csgFile);
            String fromCsgString = GetTextFromFile.read(csgFile);
            StringTokenizer st = new StringTokenizer(fromCsgString, "\n");
            targetFileOrUrl = st.nextToken();
            primaryKey = st.nextToken();
            colorRules = st.nextToken();
            String[] booleans = st.nextToken().split(" ");
            onlyPunchedIn = new Boolean(booleans[0]);
            includeAllMatchingNonPunchedInElements = new Boolean(booleans[1]);
            pkIsName = new Boolean(booleans[2]);
            lastNameNotFirst = new Boolean(booleans[3]);
            showAllColumns = new Boolean(booleans[4]);
            includePrimaryKeySearchOption = new Boolean(booleans[5]);
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
    }

    public void clearResultData() {
        synchronized (this) {
            ptcg.clearResultSet();
        }
    }

    public String getCsgName() {
        synchronized (this) {
            return csgName;
        }
    }

    public void setCsgName(String name) {
        synchronized (this) {
            csgName = name;
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

    public void clearQueries() {
        synchronized (this) {
            queries.clear();
            queriesNotList.clear();
            ptcg.clearResultSet();
        }
    }

    public void setNotIndex(int i, boolean value) {
        synchronized (this) {
            queriesNotList.set(i, value);
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

    public void notAllQueries() {
        synchronized (this) {
            for (int i = 0; i < queriesNotList.size(); i++) {
                queriesNotList.set(i, !queriesNotList.get(i));
            }
        }
    }

    public int size() {
        synchronized (this) {
            if (queries.size() == queriesNotList.size()) {
                return queries.size();
            } else {
                System.err.println("[ColorGrouperQueries] Parallel structure size mismatch!");
                return Integer.MIN_VALUE;
            }
        }
    }

    public PunchTableColorGrouper getPtcg() {
        synchronized (this) {
            return ptcg;
        }
    }

    public List<Boolean> getQueriesNotList() {
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

    public boolean hasDownloadedTable() {
        synchronized (this) {
            return hasDownloadedTable;
        }
    }

    public boolean hasExecutedQuery() {
        synchronized (this) {
            return hasExecutedQuery;
        }
    }

    public void setJColorGrouperQueries(JColorSynesthesiaGroups jcsg) {
        synchronized (this) {
            this.thisGui = jcsg;
        }
    }

    @Override
    public void run() {
        synchronized (this) {
            if (ptcg == null) {
                Exception result = null;
                try {
                    ptcg = new PunchTableColorGrouper(primaryKey, targetFileOrUrl, colorRules, onlyPunchedIn, pkIsName, lastNameNotFirst, includePrimaryKeySearchOption);
                    hasDownloadedTable = true;
                } catch (Exception ex) {
                    result = ex;
                }
                if (thisGui != null) {
                    java.awt.EventQueue.invokeLater(new JcsgFillForeignKeyTable(thisGui, result));
                }
            } else {
                ptcg.addQueriesToResult(queries, queriesNotList);
                if (includeAllMatchingNonPunchedInElements) {
                    ptcg.addNonMatchingElementsToResult();
                }
                hasExecutedQuery = true;
                if (thisGui != null) {
                    java.awt.EventQueue.invokeLater(new JcsgExecuteQuery(thisGui));
                }
            }
        }
    }

    @Override
    public String toString() {
        synchronized(this) {
            return getQuerySexp() + getResultSexp();
        }
    }

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

    public String getResultSexp() {
        synchronized (this) {
            return ptcg.toString();
        }
    }

    public static String getNewCsgName(File f) {
        return f.getName().substring(0, f.getName().length() - 4);
    }

    public static File writeCsgFile(String csgString, File f) throws Exception {
        String path = f.getAbsolutePath().trim();
        if (!path.toLowerCase().endsWith(".csg")) {
            path += ".csg";
            f = new File(path);
        }
        FileWriter fw = new FileWriter(f);
        fw.append(csgString);
        fw.close();
        return f;
    }

}
