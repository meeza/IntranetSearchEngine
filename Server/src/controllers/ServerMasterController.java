package controllers;

import java.io.File;
import java.io.IOException;
import java.util.TreeMap;
import models.StatisticsSerializer;
import static models.StatisticsSerializer.reMapping;

public class ServerMasterController {

    static int port = 6066;
    public static String installation_directory_path = "C:\\Khoji";
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
    private static boolean load_status;

    private static void startKhoji() {
        inverted_index = new TreeMap<>();
        documents_len_table = new TreeMap<>();
        docPaths_to_ids_map = new TreeMap<>();
        doc_IDs_to_Path_map = new TreeMap<>();
        File hashMap_file1 = new File(installation_directory_path + "\\index.kh");
        File hashMap_file2 = new File(installation_directory_path + "\\docLenTable.kh");
        File hashMap_file3 = new File(installation_directory_path + "\\DocIdKeyMap.kh");
        if (hashMap_file1.exists() && hashMap_file2.exists() && hashMap_file3.exists()) {

            // TODO
            // loading...
            load_status = StatisticsSerializer.loadStatisticsObjects();
            System.out.println("index load_status: "+load_status);
            reMapping();

            // loading finished!
            runKhojiServer();

            // render welcome again window
        } else {
            System.out.println("do not have Indexes loaded..");
        }
    }

    public static void main(String[] args) {
        startKhoji();
    }

    private static void runKhojiServer()
    {
        try {
            Thread socket_thread = new ServerSocketsController(port);
            socket_thread.start();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("can not create server socket threads");
        }
    }

}
