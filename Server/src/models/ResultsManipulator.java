package models;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Mohd Azeem
 */

public class ResultsManipulator {
    public TreeMap<String, Float> getRankedDocuments(TreeMap<String, Float> results_table_map) {
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