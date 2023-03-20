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

public class DbRow {
    private static final Logger logger = LogManager.getLogger(DbRow.class);
    public ResultSet buildSecondQuery(long nodeId, Connection connection) throws SQLException {

         String queryString = " select dugum.node_id, dugum.name, cocuk.parameters\n" +
                "        from node as dugum\n" +
                "        left join\n" +
                "\n" +
                "        (\n" +
                "        select *, group_concat(name || ? || txt , CHAR(11)) as parameters from children\n" +
                "        left join node\n" +
                "        on node.node_id = children.node_id\n" +
                "\n" +
                "        where children.father_id = ? and syntax = \"plain-text\"\n" +
                "        order by sequence\n" +
                "        ) as cocuk\n" +
                "\n" +
                "        where dugum.node_id = ?;";

        PreparedStatement statement = connection.prepareStatement(queryString);
        statement.setString(1, CsvField.KEY_VALUE_PAIR_SEPARATOR);
        statement.setLong(2, nodeId);
        statement.setLong(3, nodeId);


        logger.debug(String.format("Building the SQL query to obtain the 'data node's as key-value pairs from the 'chosen node'\n\n%s", statement.toString()));
        ResultSet resultSet = statement.executeQuery();
        return resultSet;
    }

}
