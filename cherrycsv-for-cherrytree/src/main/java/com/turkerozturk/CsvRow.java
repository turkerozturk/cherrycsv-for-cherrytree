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

import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class CsvRow {
    public final static String TAB_PLACEHOLDER = "{TAB}";
    public final static String NEWLINE_PLACEHOLDER = "{NEWLINE}";
    private final StringBuilder unIdentifiedKeyNodeNames = new StringBuilder();
    public static final String KEY_VALUE_PAIRS_ARRAY_SEPARATOR = Character.toString ((char) 11);
    private final long nodeId;
    private final String projectName;
    private LinkedHashSet<String> labelsMissingWillBeCreated;
    private final CsvHeader csvHeader;
    public CsvRow(long nodeId, String projectName, CsvHeader csvHeader) {
        this.nodeId = nodeId;
        this.projectName = projectName;
        this.csvHeader = csvHeader;
    }
    public String parsePairsArray(String parametrelerRaw) {

        String[] values = new String[csvHeader.getColumnCount()];

        int nodeIdLabelPosition = csvHeader.getHeaderConstantMap().get(CsvHeader.NODE_ID_LABEL);
        values[nodeIdLabelPosition] = String.valueOf(nodeId);
        int nodeNamePosition = csvHeader.getHeaderConstantMap().get(CsvHeader.NODE_NAME_LABEL);
        values[nodeNamePosition] = projectName;

        LinkedHashSet<String> labels = new LinkedHashSet<>();
        for(String label : csvHeader.getHeaderVariantMap().keySet()) {
            labels.add(label);
        }
        labelsMissingWillBeCreated = new LinkedHashSet<>(labels);

        LinkedHashMap<String, Integer> columnVariantMap = new LinkedHashMap<>();

        if(parametrelerRaw != null) {
            String[] pairsArrayAstring = parametrelerRaw.split(KEY_VALUE_PAIRS_ARRAY_SEPARATOR);
            for (String pairAsString : pairsArrayAstring) {
                CsvField csvField = new CsvField();
                AbstractMap.SimpleEntry<String, String> keyValue = csvField.parseKeyValuePair(pairAsString);
                if (keyValue.getKey() != null) {
                    labelsMissingWillBeCreated.remove(keyValue.getKey());
                    int position = csvHeader.getHeaderVariantMap().get(keyValue.getKey());
                    columnVariantMap.put(keyValue.getValue(), position);
                    String value = keyValue.getValue().replaceAll("\t",TAB_PLACEHOLDER)
                            .replaceAll("\r", "")
                            .replaceAll("\n",NEWLINE_PLACEHOLDER);
                    if (!value.trim().equals("")) {
                        values[position] = value;
                    }

                } else {
                    // REPRESENTS WHETHER IF THERE IS A PROBLEM WITH KEY AND VALUE SEPARATION. NOT TESTED.
                    unIdentifiedKeyNodeNames.append(String.format("%s, ", pairAsString));
                    System.out.println("WARNING: unIdentifiedKeyNodeNames: \"" + unIdentifiedKeyNodeNames + "\" ");
                }
            }
        }
        for(int i = 0; i < values.length; i++) {
            if (values[i] == null) {
                values[i] = CsvField.NULL_VALUE_REPRESENTATION;
            }
        }

        String csvColumnCellContent = getCsvRow(values);
        return csvColumnCellContent;
    }

    public LinkedHashSet<String> getLabelsMissingWillBeCreated() {
        return labelsMissingWillBeCreated;
    }

    /**
     * verilerin arasina TAB koyup tek parca string yapar.
     */
    public static String getCsvRow(String[] values) {

        StringBuilder stringBuilder = new StringBuilder();

        for(String string : values) {
            stringBuilder.append(String.format("%s\t", string));
        }

        final int sondanSilinecekKArakterSayisi = 1;
        stringBuilder.deleteCharAt(stringBuilder.length() - sondanSilinecekKArakterSayisi);
        return stringBuilder.toString();
    }

}
