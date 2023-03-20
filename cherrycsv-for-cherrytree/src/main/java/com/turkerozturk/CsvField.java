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

import java.util.AbstractMap;
import java.util.regex.Pattern;

public class CsvField {

    private static final Logger logger = LogManager.getLogger(CsvField.class);

    public static final String NULL_VALUE_REPRESENTATION = "___";
    public static final String KEY_VALUE_PAIR_SEPARATOR = ("|||");
    public AbstractMap.SimpleEntry<String, String> parseKeyValuePair(String pairAsString) {

        String[] keyValuePair = pairAsString.split(Pattern.quote(KEY_VALUE_PAIR_SEPARATOR));
        String key = null;
        String value = null;
        if (keyValuePair != null) {

            if (keyValuePair.length == 2) {
                key = keyValuePair[0];
                value = keyValuePair[1];
                if (value.equals(null) || value.equals("")) {
                    value = NULL_VALUE_REPRESENTATION;
                }
            } else if (keyValuePair.length == 1) {
                key = keyValuePair[0];
                value = NULL_VALUE_REPRESENTATION;
            }

        }
        return new AbstractMap.SimpleEntry<>(key, value);
    }

}
