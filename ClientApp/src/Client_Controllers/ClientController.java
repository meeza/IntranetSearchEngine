/*
 * Copyright 2014 Mohd Azeem.
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
package Client_Controllers;

import Client_Models.DataManipulator;
import Client_Models.Retriever;
import Client_Models.StatisticsSerializer;
import Client_Views.ErrorFrame;
import Client_Views.SearchQueryFrame;
import Client_Views.ShowResultFrame;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author Mohd Azeem
 */
public class ClientController {

    public static boolean load_status;
    /**
     * inverted_index field is a data structure which implements the 'Inverted
     * Index'. inverted_index is Red-Black tree which contains the mapping of
     * {term : {DocId : term_frequency}}. I am declaring the index as static
     * because this search engine needs only one Inverted Index and an Inverted
     * Index is unique anytime for every module or class of the search engine.
     *
     */
    public static TreeMap<String, TreeMap<Integer, Integer>> inverted_index;
    /**
     * It is the mapping between document Ids and the number of words in
     * corresponding document. I am declaring this table as static because only
     * one instance of this table is needed.
     */
    public static TreeMap<Integer, Integer> documents_len_table;

    /**
     * A TreeMap which contains the mapping between document paths to their IDs
     */
    public static TreeMap<String, Integer> docPaths_to_ids_map;
    public static TreeMap<Integer, String> doc_IDs_to_Path_map;



    /**
     * This string contains the path where the serialized objects of statistics
     * and Indexes are saved.
     */
    public static String installation_directory_path = "C:\\Khoji";

    /**
     * The constructor initializes Inverted Index and few more data structures
     * for storing statistics for the search engine.
     *
     * @param installation_dir the absolute path for the directory where all
     * statistics would be saved
     */
    
    
    public ClientController(String installation_dir) {
        inverted_index = new TreeMap<>();
        documents_len_table = new TreeMap<>();
        docPaths_to_ids_map = new TreeMap();
    }


    private static void startKhoji() {
        File hashMap_file1 = new File(installation_directory_path + "\\index.kh");
        File hashMap_file2 = new File(installation_directory_path + "\\docLenTable.kh");
        File hashMap_file3 = new File(installation_directory_path + "\\DocIdKeyMap.kh");
        if (hashMap_file1.exists() && hashMap_file2.exists() && hashMap_file3.exists()) {

            // TODO
            // loading...
            load_status = StatisticsSerializer.loadStatisticsObjects();
            reMapping();

            // loading finished!
            SearchQueryFrame.runFrame();

            // render welcome again window
        } else {
            // render installation window
            ErrorFrame.runFrame();
        }
    }
    /**
     * Redirects to home window.
     */
    public static void homeButtonController() {
        SearchQueryFrame.runFrame();
    }

    public static void queryController(String query) throws ClassNotFoundException {
        TreeMap<String, Float> results_table;
        System.out.println("load status: " + load_status);
        try {
            if (load_status) {
                Retriever my_query_retriever = new Retriever(query);
                results_table = my_query_retriever.retrieveResults();
                ShowResultFrame.runFrame(DataManipulator.getRankedDocuments(results_table));
            } else {
                ErrorFrame.runFrame();
            }

        } catch (IOException ex) {
            System.out.println("problem in search,, in queryController() method");
        }
    }

    /**
     * @param args the command line arguments
     */
    
    private static void reMapping() {
        System.out.println("reMapping..");
        doc_IDs_to_Path_map = new TreeMap<>();
        if (docPaths_to_ids_map != null) {
            System.out.println("remapping");
            Set<String> paths = docPaths_to_ids_map.keySet();
            for (String cur_path : paths) {
                Integer cur_docID = docPaths_to_ids_map.get(cur_path);
                doc_IDs_to_Path_map.put(cur_docID, cur_path);
            }
        }
    }
    public static void main(String[] args) {
        // TODO code application logic here
        startKhoji();
    }

}
