package controllers;

import models.StatisticsSerializer;
import models.Indexer;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import models.Crawler;

/**
 *
 * @author Mohd Azeem
 */
public class ServerController {

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
     * This string contains the absolute path of the directory which contains
     * all the documents and subdirectories of documents.
     *
     */
    public static String corpus_path;

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
     * @param corpus
     */
    public ServerController() {
        inverted_index = new TreeMap<>();
        documents_len_table = new TreeMap<>();
        docPaths_to_ids_map = new TreeMap();

//        logger = new Logger(installation_dir + "\\logger.txt");
//        logger.log("Constructor called");
    }

    private static void installController() {
        File installation_folder = new File(installation_directory_path);
        if (!installation_folder.exists()) {
            installation_folder.mkdir();
        }
    }

    public static void CrawlController(String path) {
        // crawl
        System.out.println("Strarting to indexing the following corpus:\n" + path);
//        Indexer my_crawler = new Indexer(path);
//        my_crawler.indexFtpServer();        
        Crawler my_crawler = new Crawler(path);
        try {
            my_crawler.crawl();
            System.out.println("Can not Index\n");
        } catch (IOException ex) {

            Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
        }
        StatisticsSerializer.saveStatisticObjects();
        System.out.println("Indexing completed successfully! documents indexed");
    }

    public static void FtpCrawlController(String path) {
        // crawl
        System.out.println("Strarting to indexing the following corpus:\n" + path);
        Indexer my_crawler = new Indexer(path);
        my_crawler.indexFtpServer();
        StatisticsSerializer.saveStatisticObjects();
        System.out.println("Indexing completed successfully! documents indexed");
    }

    public static void main(String[] args) {
        new ServerController();
        installController();
        System.out.println("Welcome to the Khoji Intranet Search Engine. \nThis is the server app");
        Scanner in = new Scanner(System.in);
        System.out.println("Please Enter the path of corpus or ftp server ip address:\n");
        // read the corpus path   
        corpus_path = in.next();
        System.out.println("enter 1 to start indexing of local machine. enter 0 for ftp server");
        Scanner response_in = new Scanner(System.in);
        int response = response_in.nextInt();
        if (response == 1) {
            CrawlController(corpus_path);
        } else {
            if (response == 0) {
                FtpCrawlController(corpus_path);
            } else {
                System.out.println("Thank you for using Khoji!!");
            }
        }

    }

}
