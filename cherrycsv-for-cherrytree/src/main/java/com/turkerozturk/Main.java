/***********************************************************************
 * Copyright 2023 Turker Ozturk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***********************************************************************/
package com.turkerozturk;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.LinkedHashSet;
import java.util.Scanner;

/**
 * @author Turker Ozturk
 * @version First Version 2023-03-16
 * This software website: <a href="https://github.com/turkerozturk/cherrycsv-for-cherrytree">...</a>
 * CherryTree owner's website: <a href="https://github.com/giuspen/cherrytree">...</a>
 * Tested on CTB SQLite file of CherryTree (version 0.99.53) application.
 */
public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    private static final String WORKING_DIR = System.getProperty("user.dir");
    private static final String JDBC_STRING = "jdbc:sqlite:";
    private static final String TASK_LIST = "cherrytemplatetasks";
    private static final String TEMPLATE_PREFIX = "cherrytemplate";
    private static final String PROJECT_PREFIX = TEMPLATE_PREFIX;
    private static final String CSV_OUTPUT_FILE_PREFIX = "cherrycsv";

    private static final String DEFAULT_CTB = "cherrytree.ctb";

    private static final String helpTextForUsage = "" +
            "USAGE: \n\t\t" +
            "java -jar cherrycsv-for-cherrytree.jar \n" +
            "\tWithout given a path parameter, the program assumes that the input file " +
            "\n\tis named \"cherrytree.ctb\" and located in the " +
            "same path with itself.\n\n" +
            "\tOR\n" +
            "\t\tjava -jar cherrycsv-for-cherrytree.jar <full local path of the ctb file>\n" +
            "\tThe local full path of the cherrytree database must be given.\n\t" +
            "\n\tEXAMPLE:\n" +
            "\t\tjava -jar cherrycsv-for-cherrytree.jar \"C:\\Users\\u\\Desktop\\mydb.ctb\"\n";

    public static final String CHERRY_LINE_COMMENT_CHAR = "#";

    public static void main(String[] args) {

        logger.info("START\n");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("COMMAND LINE ARGUMENTS\n\n");
        if(args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                stringBuilder.append(String.format("Argument %d = \"%s\"\n", i, args[i]));
            }
        } else {
            stringBuilder.append("Started without arguments. Default values are;\n\n");
            stringBuilder.append("CTB File Full Path: " + WORKING_DIR + File.separator + DEFAULT_CTB + "\n" +
                    "(It will search the CTB file in the folder which it is running.)\n");
        }
        logger.debug(stringBuilder);

        String inputPath = null;
        String inputFilename = null;
        String fullLocalPathToCherryDb;

        if (args.length != 1) {
            inputPath = WORKING_DIR;
            inputFilename = DEFAULT_CTB;
            fullLocalPathToCherryDb = inputPath + File.separator + inputFilename;
        } else {
            fullLocalPathToCherryDb = args[0];
            try {
                Path p = Paths.get(fullLocalPathToCherryDb);
                inputPath = p.getParent().toString();
                inputFilename = p.getFileName().toString();
            } catch (InvalidPathException | NullPointerException ex) {
                logger.error("ERROR: File Path is invalid: \"" + fullLocalPathToCherryDb + "\"\n\n" +
                        helpTextForUsage);
                goExit();
            }
        }

        if (checkExistenceOfCtbDatabase(fullLocalPathToCherryDb)) {
            logger.info("FOUND: CTB file \n\t" + fullLocalPathToCherryDb + "\n");
            logger.info("IMPORTANT: Always work with backup copy of the CTB file!" +
                    "\n\tIt can brake your database!!! " +
                    "\n\tNO RESPONSIBILITY!!!" +
                    "\n\tTESTED ON CherryTree 0.99.53\n");

            logger.info("Do you want to continue (y/n)? (DEFAULT: n)");
            Scanner input = new Scanner(System.in);
            String c = input.nextLine();
            if(!c.equalsIgnoreCase("y")){
                logger.info("CONFIRM?\n\nn\n\nCANCELLED!..");
                goExit();
            } else {
                logger.debug("CONFIRM?\n\ny\n\ncontinuing..");
            }


        } else {
            logger.info(helpTextForUsage);
            goExit();
        }

        String OUTPUT_PATH = inputPath;


        try (Connection connection = DriverManager.getConnection(JDBC_STRING + fullLocalPathToCherryDb)) {

            String[] cherryTaskNames = getTaskNames(connection);
            logger.info(String.format("The node named \"%s\" contains these 'template tasks' below:" +
                    "\n\n%s\n", TASK_LIST, String.join("\n", cherryTaskNames)));

            if(cherryTaskNames != null && cherryTaskNames.length != 0) {
                logger.debug(String.format("To each task label, we add the \"%s-\" prefix.\n\n" +
                        "A 'template task' with that prefix together called 'template name'.\n\n ", TEMPLATE_PREFIX));
                for (int i = 0; i < cherryTaskNames.length; i++) {
                    // BASLA FARKLI CSVLER DONGU BASLANGICI
                    String cherryTaskName = cherryTaskNames[i];
                    final String projectStartTag = String.format("%s-%s", PROJECT_PREFIX, cherryTaskName);
                    final String templateName = String.format("%s-%s", TEMPLATE_PREFIX, cherryTaskName);
                    final String OUTPUT_FILENAME = String.format("%s-%s-%s.csv", inputFilename,
                            CSV_OUTPUT_FILE_PREFIX, cherryTaskName);
                    final String pathToCsvFile = OUTPUT_PATH + File.separator + OUTPUT_FILENAME;
                    final File fout = new File(pathToCsvFile);

                    DbLabel label = new DbLabel();
                    logger.debug(String.format("TASK %d/%d: Searching the template name in the node names;" +
                            "\n\n\"%s\"", i + 1, cherryTaskNames.length ,templateName));
                    final String[] labels = label.dbSelectTemplateLabels(templateName, connection);
                    if (labels != null) {
                        logger.info("TASK: Task " + (i + 1) + " STARTED \"" + cherryTaskName +
                                "\". Processing \"" + templateName + "\" template.");
                        deleteCurrentOutputFile(pathToCsvFile);


                        StringBuilder reportStringBuilder = new StringBuilder();

                        CsvHeaderRow csvHeaderRow = new CsvHeaderRow();
                        reportStringBuilder.append(csvHeaderRow.doCsvHeaderRow(labels));
                        reportStringBuilder.append(CsvRowHigh.NEWLINE_SEPARATOR);

                        Motor motor = new Motor();
                        String finalRowAsLine = motor.doCsvDataRowsAndExport(projectStartTag, csvHeaderRow, connection);
                        logger.debug("FİNAL ROW LINE:\n\n" + finalRowAsLine);
                        reportStringBuilder.append(finalRowAsLine);

                        FileOutputStream fileOutputStream = new FileOutputStream(fout);
                        fileOutputStream.write(239); // This prefix indicates that the content is
                        fileOutputStream.write(187); //  in 'UTF-8 with BOM'
                        fileOutputStream.write(191); // (otherwise it is just 'UTF-8 without BOM').
                        PrintWriter printWriter = new PrintWriter(
                                new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8));
                        BufferedWriter bufferedWriter = new BufferedWriter(printWriter);

                        bufferedWriter.write(reportStringBuilder.toString());
                        bufferedWriter.flush();
                        bufferedWriter.close();
                        logger.info("TASK: Task " + (i + 1) + " COMPLETED. Output created:" +
                                "\n\t\"" + pathToCsvFile + "\"\n");
                    } else {
                        logger.warn(String.format("The \"%s\" file in \"%s\" path \n" +
                                "has no template definition " +
                                "named \"%s\" inside the tag field of any " +
                                "nodes.\n", inputFilename, inputPath, templateName));
                    }
                }
            } else {
                goExit();
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            goExit();
        }

    }

    private static String[] getTaskNames(Connection connection) throws SQLException {
        String query = "SELECT node.txt FROM node WHERE node.name IS ? LIMIT 1;";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, TASK_LIST);
        ResultSet resultSet = statement.executeQuery();
        String taskListStringRaw = resultSet.getString("txt");
        if(taskListStringRaw != null) {
            if( taskListStringRaw.length() > 0){
                String[] tmpArray = taskListStringRaw.replace("\r", "").split("\n");

                LinkedHashSet<String> taskNames = new LinkedHashSet<>();
                for (String taskName : tmpArray) {
                    if (!taskName.startsWith(CHERRY_LINE_COMMENT_CHAR) && taskName.trim().length() != 0) {
                        taskNames.add(taskName);
                    }
                }
                if (taskNames.size() > 0) {
                    String[] strArray = new String[taskNames.size()];
                    taskNames.toArray(strArray);
                    return strArray;
                }
            } else {
                logger.warn("WARNING: The node named \""+ TASK_LIST + "\" content was empty or started with \""
                        + CHERRY_LINE_COMMENT_CHAR + "\" comment character. No task template names found.");
            }
        } else {
            logger.warn("WARNING: The node named \""+ TASK_LIST + "\" " +
                    "not found in any node names. You need to create one.");
        }




        return null;
    }

    private static void deleteCurrentOutputFile(String outputPath) {
        try {
            Files.deleteIfExists(Paths.get(outputPath));
        } catch (IOException ex) {
            logger.error("ERROR: Cannot delete previous CSV report file! Please " +
                    "delete " + outputPath + " manually and then run again!");
            goExit();
        }
    }
    private static boolean checkExistenceOfCtbDatabase(String pathToCherryDb) {
        if (!Files.exists(Paths.get(pathToCherryDb))) {
            logger.error(String.format("NOT FOUND: CTB database\n\n%s", pathToCherryDb ));
            return false;
        }
        return true;
    }
    public static void goExit() {
        logger.info(HELP_STRING);

        logger.info("EXIT");
        System.exit(0);
    }

    public static final String HELP_STRING = "\nFOR HELP AND UPDATES, VISIT: " +
            "\n\thttps://github.com/turkerozturk/cherrycsv-for-cherrytree\n" +
            "SOURCE CODE:" +
            "\n\t\"https://github.com/turkerozturk/cherrycsv-for-cherrytree.git\"" +
            "\n\t\"gh repo clone turkerozturk/cherrycsv-for-cherrytree\"";

}