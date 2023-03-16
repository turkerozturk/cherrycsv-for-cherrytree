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

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Motor {

    public String doCsvDataRowsAndExport(String projectStartTag, CsvHeader csvHeader, Connection connection) throws SQLException, IOException {
        StringBuilder reportStringBuilder = new StringBuilder();
        final String queryStringTagsContainingProjectStartTag = String.format("select node.node_id, node.name from node where tags like '%%%s%%';", projectStartTag);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(queryStringTagsContainingProjectStartTag);

        while (resultSet.next()) {

            long nodeId = resultSet.getLong("node_id");
            String projectName = resultSet.getString("name");
            CsvRowHigh csvRowHighLevel = new CsvRowHigh();
            reportStringBuilder.append(csvRowHighLevel.parse(csvHeader, nodeId, projectName, connection));

        }

        return reportStringBuilder.toString();
    }

}
