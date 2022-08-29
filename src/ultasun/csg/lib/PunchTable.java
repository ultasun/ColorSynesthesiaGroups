/**
 * LICENSE: This software has no license, restrictions, or warranty.
 */
package ultasun.csg.lib;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * If a web page has a table on it, such that the table's first row is a list of
 * column names, and such that the table contains an arbitrary number of
 * punch-time columns on the right side, this program will organize the table
 * into a list of elements which are punched-in.
 *
 * The punch times should be hours and minutes, XX:XX
 *
 * This program will also search for attributes that may be found in an
 * arbitrary number of columns between the first column, and the first column
 * with a punch time in it.
 *
 * When instantiating this program, provide to it an ordered list of header
 * titles (not case-sensitive), to help it figure out which table on the page
 * you'd like to process. This list does not have to be comprehensive, and may
 * be the first few (or one) column title. If the column header has spaces in
 * it, you may provide just the first word.
 *
 * Then, doing reverse lookups on the table is easy such that you can find all
 * punched-in elements which are members of certain classes. For example, a
 * punch table might contain color as one of the columns. This program will
 * allow you to search all punched-in elements whose color member status is
 * "blue."
 *
 * A rich set of search methods are provided to search and return results based
 * on one, or serial AND searches (color is blue AND status is OK AND manager is
 * joe, etc.)
 *
 * Note, however, that punch status is stored as a separate field than the rest
 * of the discovered attribute. This was done because this is a separate
 * dimension of attribute. Meaning, all of the discovered attributes pertain to
 * maybe some static, fixed information; meanwhile, punch status is the only
 * true dynamic variable here.
 *
 * @author ultasun
 *
 */
public class PunchTable {

    public PunchTable() {
        elements = new ArrayList<>();
        columnNames = new ArrayList<>();
        anyPunchedIn = false;
    }

    /**
     * Use this constructor when needing to use a file already stored locally.
     *
     * @param f
     * @param primaryKey
     * @throws IOException
     */
    public PunchTable(File f, String primaryKey) throws IOException {
        this();
        parseTablesToArrayList(Jsoup.parse(f, "UTF-8"), primaryKey);
    }
    
    public PunchTable(String html, String primaryKey) throws IOException {
        this();
        parseTablesToArrayList(Jsoup.parse(html), primaryKey);
    }
    
    public PunchTable(URL url, String primaryKey) throws IOException {
        this();
        parseTablesToArrayList(Jsoup.parse(url, 20 * 1000), primaryKey);
    }

    private int parseTableForColumnNames(Document doc, String primaryKey) {
        int result = 0; // the index of the table 
        for (Element a : doc.select("table")) {
            List<String> candidate = new ArrayList<>();
            String x;
            if (a.select("tr").select("th").size() > 0) {
                x = "th";
            } else if (a.select("tr").select("td").size() > 0) {
                x = "td";
            } else {
                return result; // this table has no rows...
            }
            for (Element b = a.select("tr").select(x).first(); b != null; b = b.nextElementSibling()) {
                candidate.add(b.text());
            }
            if (candidate.contains(primaryKey)) {
                columnNames = candidate;
                return result;
            } else {
                result++;
            }
        }
        return 0; // we did not find a table with the primary key 
    }

    private void parseTablesToArrayList(Document doc, String primaryKey) {
        int tableIndex = parseTableForColumnNames(doc, primaryKey);
        Element dataTable = doc.select("table").get(tableIndex);
        int punchesStartAt = detectPunchColumnCount(dataTable);
        boolean skipFirst = true;
        for (Element row : dataTable.select("tr")) {
            if(skipFirst) {
                skipFirst = false;
                continue;
            }
            PunchTableRow newRow = new PunchTableRow(row.select("td"), columnNames, punchesStartAt);
            elements.add(newRow);
            anyPunchedIn = anyPunchedIn || newRow.isPunchedIn();
        }
    }
    
    public boolean hasAnyPunchedInRows() {
        return anyPunchedIn;
    }
    
    /**
     * "Figure out" where the punch columns are by taking a sample.  
     * @param table
     * @return 
     */
    private int detectPunchColumnCount(Element table) {
        int result = table.select("tr").size();
        for(int i = 1; i < PUNCH_COLUMN_DETECTOR_SAMPLE_SIZE && i < table.select("tr").size(); i++) {
            Element row = table.select("tr").get(i);
            for(Element column : row.select("td")) {
                if(PunchTableRow.isStringPunchTime(column.text())) {
                    if(i < result) {
                        result = i;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Return the key list in the original order. Useful because the HashMap
     * you'll otherwise get from this class will only give you an unordered
     * list.
     *
     * @return
     */
    public List<String> getOrderedKeyList() {
        return columnNames;
    }

    /**
     * Gives a list of each table row, for everybody that is punched in.
     * Provided as a convenience for debugging purposes.
     *
     * @return A list of all punched in elements and their attributes
     */
    public ArrayList<PunchTableRow> getRawPunchedInRows() {
        ArrayList<PunchTableRow> result
                = new ArrayList<>();

        for (PunchTableRow pttr : elements) {
            if (pttr.isPunchedIn()) {
                result.add(pttr);
            }
        }
        return result;
    }

    /**
     * Gives a list of each table row, punched in or not. Provided as a
     * convenience for debugging purposes.
     *
     * @return
     */
    public ArrayList<String> getRawStringRows() {
        ArrayList<String> result = new ArrayList<>();

        for (PunchTableRow pttr : elements) {
            result.add(pttr.toString());
        }
        return result;
    }

    public List<PunchTableRow> getRawRows() {
        return elements;
    }

    /**
     * Print all elements in the table, showing each key/value pair.
     */
    public void printRawTable() {
        for (PunchTableRow pttr : elements) {
            System.out.println(pttr);
        }
    }

    /**
     * Print all punched-in elements in the table, showing each key/value pair.
     */
    public void printRawPunchedInTable() {
        for (PunchTableRow pttr : elements) {
            if (pttr.isPunchedIn()) {
                System.out.println(pttr);
            }
        }
    }

    /**
     * Return a List<PunchTimeTableRow> of members matching multiple search
     * criteria. The criteria is passed in as a Map<String, String>. The result
     * will be members who are both punched-in and punched-out.
     *
     * @param attributes A Map<String, String> of multiple AND search criteria.
     * @return A List<PunchTimeTableRow> of members who match all search
     * criteria.
     */
    public ArrayList<PunchTableRow> getRowsForMultipleAttributeMembers(Map<String, String> attributes) {
        ArrayList<PunchTableRow> result = new ArrayList<>();
        boolean willAdd;
        for (PunchTableRow pttr : elements) {
            willAdd = true;
            for (String k : attributes.keySet()) {
                willAdd = willAdd && pttr.keyMatchesValue(k, attributes.get(k));
                if (!willAdd) {
                    break;
                }
            }
            if (willAdd) {
                result.add(pttr);
            }
        }
        return result;
    }

    /**
     * Return a List<PunchTimeTableRow> of members matching multiple search
     * criteria. The criteria is passed in as a Map<String, String>. The result
     * will be members who are punched-in.
     *
     * @param attributes A Map<String, String> of multiple AND search criteria.
     * @return A List<PunchTimeTableRow> of members who match all search
     * criteria.
     */
    public ArrayList<PunchTableRow> getRowsForMultipleAttributeMembersPunchedIn(Map<String, String> attributes) {
        ArrayList<PunchTableRow> result = new ArrayList<>();
        boolean willAdd;
        for (PunchTableRow pttr : elements) {
            willAdd = true;
            for (String k : attributes.keySet()) {
                willAdd = willAdd && pttr.keyMatchesValue(k, attributes.get(k)) && pttr.isPunchedIn();
                if (!willAdd) {
                    break;
                }
            }
            if (willAdd) {
                result.add(pttr);
            }
        }
        return result;
    }

    private final List<PunchTableRow> elements;
    private List<String> columnNames;
    private boolean anyPunchedIn;
    
    public static int PUNCH_COLUMN_DETECTOR_SAMPLE_SIZE = 25;
}
