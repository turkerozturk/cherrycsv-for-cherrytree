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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class DbLabel {

    private static final Logger logger = LogManager.getLogger(DbLabel.class);
    public String[] dbSelectTemplateLabels(String templateName, Connection connection) throws SQLException {
        String[] labels = null;
        final String queryStringTagsContainingProjectStartTag = "select " +
                "node.node_id, node.name, node.txt from node " +
                "where node.name = ? and node.syntax = 'plain-text';";
        PreparedStatement statement = connection.prepareStatement(queryStringTagsContainingProjectStartTag);
        statement.setString(1, templateName);
        ResultSet resultSet = statement.executeQuery();
        logger.debug("SQL QUERY:\n\n" + statement);

        while (resultSet.next()) {
            long nodeId = resultSet.getLong("node_id");
            String templaName = resultSet.getString("name");
            String labelsRaw = resultSet.getString("txt");
            if(labelsRaw != null && labelsRaw.length() > 0) {
                labelsRaw = labelsRaw.replace("\r","");
                labels = labelsRaw.split("\n");
            } else {
                logger.warn(String.format("Template Node %s exists (node id: %s), " +
                        "but it is not containing any field names.\n", templaName, nodeId));
            }
        }
        logger.debug(String.format("The node named \"%s\" contains these 'data nodes' below:\n\n%s", templateName, String.join("\n", labels)));

        return labels;
    }




}
