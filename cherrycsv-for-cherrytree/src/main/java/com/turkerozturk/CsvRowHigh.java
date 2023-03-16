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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashSet;

public class CsvRowHigh {
    public static final String NEWLINE_SEPARATOR = "\r\n";
    public String parse(CsvHeader csvHeader, long nodeId, String projectName, Connection connection) throws SQLException {
        StringBuilder reportStringBuilder = new StringBuilder();
        LinkedHashSet<String> labelsMissingWillBeCreated;

        Statement statementNo2 = connection.createStatement();
        DbRow dbRow = new DbRow();
        ResultSet resultSetSecondQuery = statementNo2.executeQuery(dbRow.buildSecondQuery(nodeId));
        while (resultSetSecondQuery.next()) {
            String parametrelerRaw = resultSetSecondQuery.getString("parameters");
            CsvRow csvRow = new CsvRow(nodeId, projectName, csvHeader);

            // START - PARSING DATA AND RETURNIN CSV ROW
            String csvRowData = csvRow.parsePairsArray(parametrelerRaw);
            reportStringBuilder.append(csvRowData);
            reportStringBuilder.append(NEWLINE_SEPARATOR);
            // END - PARSING DATA AND RETURNIN CSV ROW

            // START - CREATING MISSING NODES AND THEIR PARENT RELATIONS
            labelsMissingWillBeCreated = csvRow.getLabelsMissingWillBeCreated();
            if(labelsMissingWillBeCreated != null && labelsMissingWillBeCreated.size() > 0) {
                System.out.println("The nodes defined in the template will be created under the parent Node Id " + nodeId +
                        ": " + String.join(", ", labelsMissingWillBeCreated));
                DbFieldCreator dbFieldCreator = new DbFieldCreator();
                dbFieldCreator.createMissingKeysOfProjectInDatabase(nodeId, labelsMissingWillBeCreated, connection);
            }
            // END - CREATING MISSING NODES AND THEIR PARENT RELATIONS

        }
        return reportStringBuilder.toString();
    }

}
