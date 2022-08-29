/**
 * LICENSE: This software has no license, restrictions, or warranty.
 */
package ultasun.csg.punchtable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.jsoup.select.Elements;

/**
 * A helper class designed to be initialized only by the PunchTimeTable class,
 * however its methods are used often by the user.
 *
 * The inner HashMap<String, String> is private, however several convenience
 * methods have been provided to avoid having to directly access the Map.
 *
 * @author ultasun
 */
public class PunchTableRow {
    /**
     * This constructor is designed to be called exclusively from the
     * PunchTimeTable.parseTableToArrayList() method, which is only called
     * during the constructor of PunchTimeTable.
     *
     * The reason is that this constructor is used in the middle of a
     * multiple-nested for-each loop, and so the relevant context (the
     * org.jsoup.select.Elements) are passed directly in to ease the transition.
     *
     * The detection of punched-in status happens within this constructor.
     *
     * @param html The html from whichever row number we're on.
     */
    public PunchTableRow(Elements html, List<String> keyOrder, int punchesStartAt) {
        this.keyOrder = keyOrder;
        this.values = new HashMap<>();
        boolean punchDetector = false;
        for (int i = 0; i < keyOrder.size(); i++) {
            String thisValue = html.get(i).text();
            String candidateKey = keyOrder.get(i);
            // Sometimes tables use ambiguous column-header names, lets fix that
            if(this.values.containsKey(candidateKey)) {
                int j = 0;
                candidateKey = keyOrder.get(i) + " " + j;
                while(this.values.containsKey(candidateKey)) {
                    j++;
                    candidateKey = keyOrder.get(i) + " " + j;
                }
            }
            this.values.put(candidateKey, thisValue);
            // call back and modify the greater key list to reflect this change
            if(!candidateKey.equals(keyOrder.get(i))) {
                keyOrder.set(i, candidateKey);
            }
            if (i >= punchesStartAt && isStringPunchTime(thisValue)) {
                // account for the column count being odd or even
                int flag = (keyOrder.size() - punchesStartAt) % 2;
                // are we on a punch-in column or punch-out column?
                int inOrOut = (i - punchesStartAt) % 2;
                punchDetector = inOrOut == flag;
            }
        }
        punchedIn = punchDetector;
    }

    /**
     * Generic PunchTimeTableRow constructor, pass in whatever you want. This is
     * provided for debugging purposes and really defeats the point... stranger,
     * why aren't you just using PunchTimeTable directly?
     *
     * Warning: this constructor will not process the values to determine if
     * this element is punched in. You must determine that yourself when
     * initializing.
     *
     * @param values The values you want in this object.
     * @param punchedIn Tell me if I'm punched in or not.
     */
    public PunchTableRow(HashMap<String, String> values, List<String> keyOrder, boolean punchedIn) {
        this.keyOrder = keyOrder;
        this.values = values;
        this.punchedIn = punchedIn;
    }

    /**
     * Returns a String representation of this PunchTableRow object, including
     * all attributes, and punched in status.
     *
     * @return A String representation of this PunchTableRow.
     */
    @Override
    public String toString() {
        String result = "(";
        for (String k : keyOrder) {
            result += "(\"" + k + "\" . ";
            if (this.values.get(k).length() > 0) {
                result += "\"" + this.values.get(k) + "\"";
            } else {
                result += "nil";
            }
            result += ") ";
        }
        result += "(punched-in? " + punchedIn + "))";
        return result;
    }

    public boolean equals(PunchTableRow other) {
        boolean result = true;
        for (String key : values.keySet()) {
            result = result && other.get(key).equals(this.values.get(key));
        }
        return result;
    }

    @Override
    public boolean equals(Object other) {
        PunchTableRow otherPtr = (PunchTableRow) other;
        String leftValue = values.get(keyOrder.get(0));
        String rightValue = otherPtr.getValues().get(keyOrder.get(0));
        return leftValue.equals(rightValue);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.values);
        hash = 67 * hash + Objects.hashCode(this.keyOrder);
        return hash;
    }

    /**
     * Is the element punched in?
     *
     * @return Boolean depending on status
     */
    public boolean isPunchedIn() {
        return punchedIn;
    }

    public List<String> getKeys() {
        return keyOrder;
    }

    /**
     * Return the HashMap for this table row.
     *
     * @return HashMap<String, String> of key/value pairs.
     */
    public Map<String, String> getValues() {
        return values;
    }

    /**
     * Get just one value from this table row.
     *
     * @param key The column header title name (key)
     * @return The value associated with this row for the requested column.
     */
    public String get(String key) {
        return values.get(key);
    }

    /**
     * Put a key/value pair into the internal Map. In case you wanted to add an
     * attribute after the fact (e.g., one not on the original table).
     *
     * @param key A String value
     * @param value A String value
     */
    public void put(String key, String value) {
        values.put(key, value);
    }

    /**
     * Helper method to query whether this table row has a column with a value
     * that matches the provided valueToMatch.
     *
     * @param key Which attribute to check (which table column)
     * @param valueToMatch Does this row have this value in the specified
     * column?
     * @return Boolean true/false of status
     */
    public boolean keyMatchesValue(String key, String valueToMatch) {
        return values.get(key).equals(valueToMatch);
    }

    /**
     * The function to determine if the String found in a table row looks like a
     * punch time.
     *
     * @param s A string such as 12:34, or anything else.
     * @return Boolean depending on status.
     */
    public static boolean isStringPunchTime(String s) {
        return s.length() == 5 && s.indexOf(":") == 2;
    }

    /**
     * The table column headers are the keys, and the values are each column
     * found in the particular data row.
     */
    private final Map<String, String> values;
    private final List<String> keyOrder;

    /**
     * Is the element punched in? This is final for a reason.
     */
    private final boolean punchedIn;
}
