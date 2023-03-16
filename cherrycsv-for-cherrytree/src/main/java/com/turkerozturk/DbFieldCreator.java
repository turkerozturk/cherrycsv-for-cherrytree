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

public class DbFieldCreator {

    /**
     * Each project node must contain certain child nodes determined by this program.
     * If some of them are not exist, this method will create them.
     */
    public void createMissingKeysOfProjectInDatabase(long nodeId, LinkedHashSet<String> missingLabels, Connection connection) throws SQLException {

        // CTB dosyasinda olusturulacak Node icin benzersiz bos id belirlemek icin toplam Node sayisini elde ediyoruz.
        String queryForNodeId = "select max(node_id) as sayi from node limit 1;";
        Statement statementNo3 = connection.createStatement();
        ResultSet rtt = statementNo3.executeQuery(queryForNodeId);
        int musaitNodeId = rtt.getInt("sayi") + 1;
        // CTB dosyasindaki bir Node nin alt Nodelerinin siralanmasi sequence isimli sql alani ile yapiliyor.
        // Ekleyecegimiz dugumler varsa mevcut dugumlerin sirasini bozmadan sona eklenmesi ve bir sira numarasi verebilmemiz icin
        // en yuksek sequence degeri elde ediyoruz.
        String queryStringCurrentCountOfChildNodes = "select max(sequence) as sayi from children where father_id = " + nodeId + " limit 1;";
        Statement statementNo4 = connection.createStatement();
        ResultSet rrr = statementNo4.executeQuery(queryStringCurrentCountOfChildNodes);
        int musaitSequenceNumber = rrr.getInt("sayi") + 1;

        for (String missingLabel: missingLabels) {

            // START - CREATE THE MISSING NODE
            String queryForInsertNode = "insert into node (node_id, name, txt, syntax, tags, is_ro, is_richtxt, " +
                    "has_codebox, has_table, has_image, level, ts_creation, ts_lastsave) values (" +
                    musaitNodeId +
                    ", \"" +
                    missingLabel +
                    "\", \"\", \"plain-text\", \"variable\", 0, 0, 0, 0, 0, 0, " +
                    "CAST(strftime('%statement','now') || '.' || substr(strftime('%f','now'),4,3) AS REAL), " +
                    "CAST(strftime('%statement','now') || '.' || substr(strftime('%f','now'),4,3) AS REAL));";
            Statement qfin = connection.createStatement();
            qfin.execute(queryForInsertNode);
            // END - CREATE THE MISSING NODE

            // START - CREATE RELATION BETWEEN NEW NODE AND HIS PARENT NODE
            String queryForInsertFatherChild = "insert into children(node_id, father_id, sequence) values(" +
                    musaitNodeId + ", " + nodeId + ", " + musaitSequenceNumber + ");";
            Statement qffc = connection.createStatement();
            qffc.execute(queryForInsertFatherChild);
            // END - CREATE RELATION BETWEEN NEW NODE AND HIS PARENT NODE

            musaitNodeId++;
            musaitSequenceNumber++;

        }

    }

}
