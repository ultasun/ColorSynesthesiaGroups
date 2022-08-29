package ultasun.csg.lib;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author ultasun
 */
public class PunchTableColorGrouper {

    public PunchTableColorGrouper(String primaryKey, String fileOrUrl, String orgColorRules, boolean onlyPunchedIn, boolean pkIsName, boolean lastNameNotFirst, boolean includePrimaryKeySearchOption) throws Exception {
        this.includePrimaryKeySearchOption = includePrimaryKeySearchOption;
        this.onlyPunchedIn = onlyPunchedIn;
        this.primaryKeyIsName = pkIsName;
        this.useLastNameNotFirstName = lastNameNotFirst;
        this.primaryKey = primaryKey;
        if (fileOrUrl.contains("http")) {
            URL u = new URL(fileOrUrl);
            this.pt = new PunchTable(u, primaryKey);
        } else {
            this.pt = new PunchTable(new File(fileOrUrl), primaryKey);
        }
        if (!pt.getOrderedKeyList().contains(primaryKey)) {
            throw new Exception("Specified column to use as primary key is not found in the table.");
        }
        try {
            colorRules = new HashMap<>();
            colors = new String[orgColorRules.split(" ").length];
            populateColorRules(orgColorRules);
            populatePunchTableColorInfo();
        } catch (Exception e) {
            throw new Exception("The specified color rules are not valid.");
        }
        this.resultDataOrderedKeys = new ArrayList<>();
        this.notList = new ArrayList<>();
        this.resultData = new HashMap<>();
        for (String color : colors) {
            resultData.put(color, new ArrayList<>());
        }
        this.foreignKeyValues = new HashMap<>();
        this.orderedForeignKeys = new ArrayList<>();
        populateForeignKeys();
        if (orderedForeignKeys.isEmpty()) {
            throw new Exception("The loaded table has no columns whose values are shared amongst multiple rows, so this program serves no purpose here.");
        }
    }

    /**
     * Get the color group assigned to the queried particular word.
     *
     * @param word The word to query
     * @return The color group assigned to this word
     */
    public String getColorForString(String word) {
        String target = word;
        if (primaryKeyIsName) {
            if (useLastNameNotFirstName) {
                String[] spaceSplit;
                if (word.contains(",")) {
                    String[] commaSplit = word.split(",");
                    spaceSplit = commaSplit[0].split(" ");
                } else {
                    spaceSplit = word.split(" ");
                }
                target = spaceSplit[0];

            } else {
                if (word.contains(",")) {
                    String[] commaSplit = word.split(",");
                    target = commaSplit[0];
                } else {
                    target = word;
                }
            }
        }

        return colorRules.get(target.toLowerCase().substring(0, 1));
    }

    public void addQueriesToResult(List<Map<String, String>> allAttributes) {
        for (Map<String, String> map : allAttributes) {
            List<PunchTableRow> thisQueryResult;
            if (onlyPunchedIn) {
                thisQueryResult = pt.getRowsForMultipleAttributeMembersPunchedIn(map);
            } else {
                thisQueryResult = pt.getRowsForMultipleAttributeMembers(map);
            }
            for (PunchTableRow ptr : thisQueryResult) {
                List<PunchTableRow> thisPrintColorSet = resultData.get(ptr.get(COLOR_KEY));
                if (!thisPrintColorSet.contains(ptr) && !notList.contains(ptr)) {
                    thisPrintColorSet.add(ptr);
                }
                resultData.put(ptr.get(COLOR_KEY), thisPrintColorSet);
            }
        }
        if (resultDataOrderedKeys.isEmpty()) {
            resultDataOrderedKeys = pt.getOrderedKeyList();
        }
    }

    public void addQueriesToResult(List<Map<String, String>> allAttributes, List<Boolean> notList) {
        for (int i = 0; i < notList.size(); i++) {
            List<PunchTableRow> thisQueryResult;
            if (onlyPunchedIn) {
                thisQueryResult = pt.getRowsForMultipleAttributeMembersPunchedIn(allAttributes.get(i));
            } else {
                thisQueryResult = pt.getRowsForMultipleAttributeMembers(allAttributes.get(i));
            }
            for (PunchTableRow ptr : thisQueryResult) {
                List<PunchTableRow> thisPrintColorSet = resultData.get(ptr.get(COLOR_KEY));
                if (notList.get(i)) { // NOT should be placed infront of the query
                    this.notList.add(ptr);
                    thisPrintColorSet.remove(ptr);
                } else { // regular query (without NOT)
                    if (!thisPrintColorSet.contains(ptr) && !this.notList.contains(ptr)) {
                        thisPrintColorSet.add(ptr);
                    }
                }
            }
        }
        if (resultDataOrderedKeys.isEmpty()) {
            resultDataOrderedKeys = pt.getOrderedKeyList();
        }
    }

    public void addNonMatchingElementsToResult() {
        if (onlyPunchedIn) {
            addNonMatchingPunchedInElementsToResult();
        } else {
            for (PunchTableRow ptr : pt.getRawRows()) {
                if (notList.contains(ptr)) {
                    continue;
                }
                String color = ptr.get(COLOR_KEY);
                boolean presentInGroup = resultData.get(color).contains(ptr);
                if (!presentInGroup && !notList.contains(ptr)) {
                    resultData.get(color).add(ptr);
                }
            }
        }
    }

    public int getResultRowCount() {
        int result = 0;
        for (String c : colors) {
            result += resultData.get(c).size();
        }
        return result;
    }

    public List<PunchTableRow> getAllRows() {
        if (onlyPunchedIn) {
            return pt.getRawPunchedInRows();
        } else {
            return pt.getRawRows();
        }
    }

    public String getForeignKeysSexp() {
        String result = "(cg-fk \n";
        for (String k : orderedForeignKeys) {
            result += " (\"" + k + "\"\n  (";
            for (String v : foreignKeyValues.get(k)) {
                result += "\n   \"" + v + "\"";
            }
            result += "\n  )\n )\n";
        }
        result += ")\n";
        return result;
    }

    @Override
    public String toString() {
        String result = "(cg-result \n";
        for (String color : colors) {
            result += " (" + color + "\n";
            if (resultData.get(color).isEmpty()) {
                result += "  ()\n";
            } else {
                for (PunchTableRow ptr : resultData.get(color)) {
                    result += "  " + ptr.toString() + "\n";
                }
            }
            result += " )\n";
        }
        result += ")";
        return result;
    }

    public void clearResultSet() {
        resultData.clear();
        for (String color : colors) {
            resultData.put(color, new ArrayList<>());
        }
        notList.clear();
    }

    /**
     * Return the key (column header) list in the order which it was found on
     * the web page.
     *
     * This is available after the constructor and before calls to
     * setColorSortedQuery are made.
     *
     * @return List<String> of column headers/titles from the processed table
     */
    public List<String> getOrderedKeyList() {
        return pt.getOrderedKeyList();
    }

    public int getColorCount() {
        return colors.length;
    }

    public String[] getColors() {
        return colors;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public boolean hasAnyPunchedInRows() {
        return pt.hasAnyPunchedInRows();
    }

    public Map<String, List<String>> getForeignKeyValues() {
        return foreignKeyValues;
    }

    public List<String> getForeignKeys() {
        return orderedForeignKeys;
    }

    /**
     * Return the search foreignKeyValues from setColorSortedQuery()
     *
     * @return
     */
    public Map<String, List<PunchTableRow>> getResultPrintSets() {
        return this.resultData;
    }

    public void setResultDisplayedColumns(List<String> foreignKeys) {
        this.resultDataOrderedKeys = foreignKeys;
    }

    public List<String> getResultDisplayedColumns() {
        return this.resultDataOrderedKeys;
    }

    /**
     * Creates an alphabet table where the foreignKeyValues for each letter is
     * the assigned color group.
     *
     * Ranges are using lowercase letters.
     */
    private void populateColorRules(String orgColorRuleStr) {
        String[] orgColorRules = orgColorRuleStr.split(" ");
        if (!colorRules.isEmpty()) {
            return;
        }
        int i = 0;
        for (String s : orgColorRules) {
            String[] thisArgs = s.split(":");
            colors[i] = thisArgs[0].toLowerCase();
            i++;
            for (char x = thisArgs[1].toLowerCase().charAt(0); x <= thisArgs[2].toLowerCase().charAt(0); x++) {
                colorRules.put(("" + x).toLowerCase(), thisArgs[0].toLowerCase());
            }
        }

    }

    /**
     * Adds the color group as a key/value attribute to the PunchTableRow
     *
     * Note that the color group is not a "column" found on the web page, but it
     * is being injected here.
     */
    private void populatePunchTableColorInfo() {
        List<PunchTableRow> ptrl;
        if (onlyPunchedIn) {
            ptrl = pt.getRawPunchedInRows();
        } else {
            ptrl = pt.getRawRows();
        }
        for (PunchTableRow ptr : ptrl) {
            ptr.put(COLOR_KEY, getColorForString(ptr.get(primaryKey)));
        }
    }

    /*
    This method needs to be optimized, it's probably what is slowing us down.
    (this is where the discovery of foreign keys happen, as long as two rows
    share the same value for a given column, it will be considered a foreign 
    key, unless it is a punch column)
     */
    private void populateForeignKeys() {
        Map<String, Map<String, Integer>> counter = new HashMap<>();
        for (String k : pt.getOrderedKeyList()) {
            counter.put(k, new HashMap<>());
        }
        for (PunchTableRow ptr : pt.getRawRows()) {
            for (String key : ptr.getKeys()) {
                Map<String, Integer> thisCounter = counter.get(key);
                if (!PunchTableRow.isStringPunchTime(ptr.get(key))) {
                    if (thisCounter.get(ptr.get(key)) == null) {
                        thisCounter.put(ptr.get(key), 1);
                    } else {
                        thisCounter.put(ptr.get(key), thisCounter.get(ptr.get(key)) + 1);
                    }
                }
            }
        }

        for (String k : counter.keySet()) {
            for (String l : counter.get(k).keySet()) {
                // We should be checking greater than 1, not 2...
                if (counter.get(k).get(l) > 2 && l.length() > 0) {
                    if (!orderedForeignKeys.contains(k)) {
                        orderedForeignKeys.add(k);
                    }
                }
            }
        }

        for (String k : orderedForeignKeys) {
            for (String l : counter.get(k).keySet()) {
                if (!foreignKeyValues.containsKey(k)) {
                    foreignKeyValues.put(k, new ArrayList<>());
                }
                if (!foreignKeyValues.get(k).contains(l)) {
                    foreignKeyValues.get(k).add(l);
                }
            }
        }

        if (includePrimaryKeySearchOption) {
            orderedForeignKeys.add(0, primaryKey);
            foreignKeyValues.put(primaryKey, new ArrayList<>());
            if (onlyPunchedIn) {
                for (PunchTableRow ptr : pt.getRawPunchedInRows()) {
                    foreignKeyValues.get(primaryKey).add(ptr.get(primaryKey));
                }
            } else {
                for (PunchTableRow ptr : pt.getRawRows()) {
                    foreignKeyValues.get(primaryKey).add(ptr.get(primaryKey));
                }
            }
        }
    }

    private void addNonMatchingPunchedInElementsToResult() {
        for (PunchTableRow ptr : pt.getRawPunchedInRows()) {
            if (notList.contains(ptr)) {
                continue;
            }
            String color = ptr.get(COLOR_KEY);
            boolean presentInGroup = resultData.get(color).contains(ptr);
            if (!presentInGroup && ptr.isPunchedIn() && !notList.contains(ptr)) {
                resultData.get(color).add(ptr);
            }
        }
    }

    public static final String COLOR_KEY = "color";
    protected final String primaryKey;
    protected final PunchTable pt;
    protected final Map<String, String> colorRules;
    protected final String[] colors;
    protected final boolean onlyPunchedIn;
    protected final boolean primaryKeyIsName;
    protected final boolean useLastNameNotFirstName;
    protected final Map<String, List<PunchTableRow>> resultData;
    protected final List<PunchTableRow> notList;
    protected final Map<String, List<String>> foreignKeyValues;
    protected final List<String> orderedForeignKeys;
    protected List<String> resultDataOrderedKeys;
    protected boolean includePrimaryKeySearchOption;
}
