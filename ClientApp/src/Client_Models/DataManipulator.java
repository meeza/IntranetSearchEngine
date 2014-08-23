/*
 * Copyright 2014 Mohd Azeem
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package Client_Models;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Mohd Azeem
 */
public class DataManipulator {

    public static TreeMap<String, Float> getRankedDocuments(TreeMap<String, Float> results_table_map) {
        ValueComparator bvc = new ValueComparator(results_table_map);
        TreeMap<String, Float> sorted_map = new TreeMap<>(bvc);
//        System.out.println("unsorted map: " + results_table_map);
        sorted_map.putAll(results_table_map);
//        System.out.println("sorted map: " + sorted_map);
        return sorted_map;
    }
}

class ValueComparator implements Comparator<String> {
    Map<String, Float> base;
    public ValueComparator(Map<String, Float> base) {
        this.base = base;
    }
    // Note: this comparator imposes orderings that are inconsistent with equals.    
    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}