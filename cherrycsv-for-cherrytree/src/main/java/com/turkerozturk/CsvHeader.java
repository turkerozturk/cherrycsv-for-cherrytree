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

import java.util.LinkedHashMap;

public class CsvHeader {
    public static final String NODE_ID_LABEL = "nodeid";
    public static final String NODE_NAME_LABEL = "nodename";
    private final LinkedHashMap<String, Integer> headerConstantMap = new LinkedHashMap<>();
    private final LinkedHashMap<String, Integer> headerVariantMap = new LinkedHashMap<>();
    private int columnCount;
    public String doCsvHeaderRow(String[] labels) {

        headerConstantMap.put(NODE_ID_LABEL, 0);
        headerConstantMap.put(NODE_NAME_LABEL, 1);

        int counter = headerConstantMap.size();
        for(String label : labels) {
            headerVariantMap.put(label, counter);
            counter++;
        }

        LinkedHashMap<String, Integer> headerAllMap = new LinkedHashMap<>();
        headerAllMap.putAll(headerConstantMap);
        headerAllMap.putAll(headerVariantMap);
        columnCount = headerAllMap.size();

        String[] columnNames = new String[headerAllMap.size()];
        int sayac = 0;
        for(String s: headerAllMap.keySet()) {
            columnNames[sayac] = s;
            sayac++;
        }
        String reportColumnHeaders = CsvRow.getCsvRow(columnNames);

        return(reportColumnHeaders);
    }

    public LinkedHashMap<String, Integer> getHeaderConstantMap() {
        return headerConstantMap;
    }

    public LinkedHashMap<String, Integer> getHeaderVariantMap() {
        return headerVariantMap;
    }

    public int getColumnCount() {
        return columnCount;
    }
}
