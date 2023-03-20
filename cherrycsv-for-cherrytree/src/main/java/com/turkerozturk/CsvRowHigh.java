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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;

public class CsvRowHigh {

    private static final Logger logger = LogManager.getLogger(CsvRowHigh.class);
    public static final String NEWLINE_SEPARATOR = "\r\n";
    public String parse(CsvHeaderRow csvHeaderRow, long nodeId, String projectName, Connection connection) throws SQLException {
        StringBuilder reportStringBuilder = new StringBuilder();
        LinkedHashSet<String> labelsMissingWillBeCreated;

        DbRow dbRow = new DbRow();
        ResultSet resultSetSecondQuery = dbRow.buildSecondQuery(nodeId, connection);
        while (resultSetSecondQuery.next()) {
            String parametrelerRaw = resultSetSecondQuery.getString("parameters");
            if(parametrelerRaw != null) {
                logger.debug(String.format("The 'data node's as one line String:\n\n%s", parametrelerRaw));
            } else {
                logger.debug("All of the 'data node's will be created, because they are absent.");
            }
            CsvDataRow csvDataRow = new CsvDataRow(nodeId, projectName, csvHeaderRow);

            // START - PARSING DATA AND RETURNIN CSV ROW
            String csvRowData = csvDataRow.parsePairsArray(parametrelerRaw);
            reportStringBuilder.append(csvRowData);
            reportStringBuilder.append(NEWLINE_SEPARATOR);
            // END - PARSING DATA AND RETURNIN CSV ROW

            // START - CREATING MISSING NODES AND THEIR PARENT RELATIONS
            labelsMissingWillBeCreated = csvDataRow.getLabelsMissingWillBeCreated();
            if(labelsMissingWillBeCreated != null && labelsMissingWillBeCreated.size() > 0) {
                logger.debug("The nodes defined in the template will be created under the parent Node Id " + nodeId +
                        ": " + String.join(", ", labelsMissingWillBeCreated));
                DbFieldCreator dbFieldCreator = new DbFieldCreator();
                dbFieldCreator.createMissingKeysOfProjectInDatabase(nodeId, labelsMissingWillBeCreated, connection);
            }
            // END - CREATING MISSING NODES AND THEIR PARENT RELATIONS

        }
        logger.debug(String.format("Result CSV Data Row:\n\n%s", reportStringBuilder));
        return reportStringBuilder.toString();
    }

}
