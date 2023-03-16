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
 ***********************************************************************/package com.turkerozturk;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DbLabel {
     public String[] dbSelectTemplateLabels(String templateName, Connection connection) throws SQLException {
        String[] labels = null;
        final String queryStringTagsContainingProjectStartTag = String.format("select " +
                "node.node_id, node.name, node.txt from node " +
                "where node.name = '%s' and node.syntax = 'plain-text';", templateName);

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(queryStringTagsContainingProjectStartTag);
        while (resultSet.next()) {
            long nodeId = resultSet.getLong("node_id");
            String templaName = resultSet.getString("name");
            String labelsRaw = resultSet.getString("txt");
            if(labelsRaw != null && labelsRaw.length() > 0) {
                labelsRaw = labelsRaw.replace("\r","");
                labels = labelsRaw.split("\n");
            } else {
                System.out.printf("Template Node %s exists (node id: %s), " +
                        "but it is not containing any field names.%n", templaName, nodeId);
            }
        }

        return labels;
    }




}
