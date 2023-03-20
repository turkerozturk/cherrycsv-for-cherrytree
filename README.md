# cherrycsv-for-cherrytree
CherryCSV is a tool to create CSV files by extracting data from the CTB SQLite database file, which belongs to the hierarchial note-taking software named CherryTree.

Downloads:
https://github.com/turkerozturk/cherrycsv-for-cherrytree/releases

Why I created it?

In short, I wanted to see and control my similar data nodes easily.

How to use this tool?

I assume that you already are a CherryTree note taking software user. If not, you can download and use it from its website until to get familiar with it;
https://www.giuspen.net/cherrytree/

You need to have JAVA_HOME environment variable.

A VERY BASIC TUTORIAL TO CREATE AN EXAMPLE CSV FILE:

Create a new CTB file using CherryTree application, or use your existing copy of one with caution.
Rename it to cherrytree.ctb and put into the same folder with this tool. Otherwise you need to give the full path of the CTB file as an argument to this commandline tool.

A CTB file consists of nodes. Instead of an external configuration file, I decided to use some nodes with special purpose.

Therefore, I gave them virtual names in my mind and you will understand them after completing this tutorial:

Tasks node: Only one tasks node must exist. Inside its content, each line is equal to one template.

Template node: One or more template nodes can exist. Inside its content, each line is equal to data node names.

Chosen node: A chosen node is, if you wrote the template name inside "Tags for Searching" field of any node, using right click node properties option, it will become chosen node. It means that this tool will collect data from its children.

Data node: These nodes are the children of chosen nodes. If data nodes are not exist, they will created with empty content. If they exist, this tool only collects data from them.

In your CTB file create these three nodes below;

TASKS NODE
Create a new node with setting its node name to cherrytemplatetasks and with setting its node type to plain text.
Put the word test into its content area.

TEMPLATE NODE
Create a new node with setting its node name to cherrytemplate-test and with setting its node type to plain text.
Put the word datanode1 into first line of its content area.

CHOSEN NODE
Create a new node with setting its node name to anything and with setting its node type anything.
Set the value of its Tags for Searching property to cherrytemplate-test.

Run this tool like below (choose one of them);

cherrycsv-for-cherrytree.exe

OR

java -jar cherrycsv-for-cherrytree.jar

OR

cherrycsv-for-cherrytree.exe fullPathOfYourCtbFile

OR

java -jar cherrycsv-for-cherrytree.jar fullPathOfYourCtbFile

Answer typing y key to the question and type ENTER twice.

If everything is ok, 

It will create a new node under the CHOSEN NODE you created before, as a child node, named datanode1, with empty content.

We call it DATA NODE.

It will create these files below in the same folder of this tool;

cherrytree.ctb-cherrycsv-test.csv

cherrycsvlog.html

Now you can open that CSV file with Libreoffice Calc, Microsoft Excel, or even import to the same CTB file into a node content as a table using CherryTree program.

What will happen if you run this tool again,

Because the data node was created before, it will leave that node as is.

It will overwrite the log file and all of CSV files with newly created files.

Please open the generated cherrycsvlog.html file with your web browser to see the logs, it contains detailed explanation for each step. It can help you to understand the mechanism better.







