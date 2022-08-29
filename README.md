# Color Synesthesia Groups
Given an HTML document containing a table `<table>`, which contains both `<thead>` and `<tbody>` elements, this utility will allow the user to group rows from the table into *color groups* based on their defined queries.

*Color Synesthesia Groups* is a *Java* [*Swing*](https://docs.oracle.com/javase/8/docs/technotes/guides/swing/index.html) application.  This git repository contains a [*NetBeans*](https://netbeans.apache.org/) project.

# Installation
Modern Java developers are supposed to [release platform specific executables](http://launch4j.sourceforge.net/docs.html) in order to prevent end-users from needing to install a JRE on their machine.  Perhaps this may be available for *Color Synesthesia Groups* in the future.

*You must have a JRE installed*

1. [Install Java 8](https://www.java.com/en/download/manual.jsp),
2. [Download and extract the CSG distribution](https://ultasun.github.io/CSG.zip),
	- This creates a folder called `CSG`,
3. *Double click* on `ColorSynesthesiaGroups.jar` in the `CSG` folder,
	- Try it out with the included `demo-time-table.html` document.

# Help Window Text
Welcome to Color Synesthesia Groups!  Please read these instructions in their entirety.

This tool allows you to search a large table (which may include punched-in/punched-out columns on the right side) by the search criteria
(known as 'foreign keys') and group the results into colors assigned by letters.  You specify which column contains the unique row data,
(the column by which the program will color sort by), and the program will detect possible foreign key columns.  The program will also detect each row's
punch status, if applicable.  There is no limit to the number of rows, columns, foreign keys, punch columns...and the groups may be named anything.

Start by entering a file or URL into the "File or URL" box.
Then, indicate to the program the column title from the table within the file or URL you would like to use as the "Primary Key"
Next, indicate to the program the color grouping rules you would like, an example for the rule syntax is provided.
Select whether you need to see "Punched-in Rows Only".
Select whether you need the "Primary Key is human name".
Select whether you need to see the "Include Primary Search Key Option" in the foreign keys table.
Select the "Load Page" button to download and look for foreign key values in the table.

After pressing "Load Page", the table should be filled with possible foreign key values.  If not, check your "Primary Key".

 If the foreign keys loaded on the left-hand table, then you may start building queries.  Each query will be chained together using "OR" clauses.
1. Press the "Add" button
2. Click on foreign keys to match, this will add them together using "AND" clauses.
3. Press "Add" button again when finished selecting foreign keys.
Note: If a key from all columns are selected during "Add", then "Add" will disable after selecting the last foreign key.

To delete, much in the same fashion as add,
1. Press the "Delete" button first, then 
2. Click the target query you would like deleted, and 
3. Press "Delete" again.

Once you have defined all desired queries, choose whether you need to "Include all non-matching punched-in elements" to be included in the search result.
This is useful if you have NOT queries and would like to see which punched-in elements your queries are not catching (these would be considered anomalies in most contexts.)

After completing the above, you may press "Print" to print the result.
You may also "Show Results" in a windowed table for each color group.
A S-Expression view of the data may be also be seen by clicking "View S-exp".

The configuration may be loaded or saved using the "Load" and "Save" found under the "Setup" area.

If "Print/Show All Columns" is not selected, then the search result will show the "Primary Key", all foreign keys, and no additional unique row data.

Note: After executing the search ("Show Results", "Show S-exp", "Print"), it is required to create a "New" setup, or to "Load" another one, if it is necessary
to modify the query.  The query may not be modified after the search without restarting the program (which may be accomplished by pressing "New" or "Load")
Note: If the loaded table has no right-aligned punch-columns, then the option to "Include all non-matching punched-in elements" will include all rows
from the original table, functionally ignoring the queries.

# Credits
Authored 2019 by ultasun.  See the `LICENSE` file.  Thanks for reading!
