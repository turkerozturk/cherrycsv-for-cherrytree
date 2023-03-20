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

import java.sql.*;

public class Motor {
    private static final Logger logger = LogManager.getLogger(Motor.class);
    public String doCsvDataRowsAndExport(String projectStartTag, CsvHeaderRow csvHeaderRow, Connection connection){
        StringBuilder reportStringBuilder = new StringBuilder();

        final String queryStringTagsContainingProjectStartTag = "select node.node_id, node.name from node where tags like ?;";
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(queryStringTagsContainingProjectStartTag);
            statement.setString(1, projectStartTag);
            logger.debug(String.format("Searching for 'chosen nodes', whose having the 'template name' in their \"search for tags\" field;\n\n%s", statement));
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                long nodeId = resultSet.getLong("node_id");
                String projectName = resultSet.getString("name");
                logger.debug(String.format("Chosen Node Id: %d\nChosen Node Name: %s\nWhy it is chosen, " +
                        "because it has the\n\"%s\" template name in its search tag field.", nodeId, projectName, projectStartTag));
                CsvRowHigh csvRowHighLevel = new CsvRowHigh();
                reportStringBuilder.append(csvRowHighLevel.parse(csvHeaderRow, nodeId, projectName, connection));

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return reportStringBuilder.toString();
    }

}
