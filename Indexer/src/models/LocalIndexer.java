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
package models;

import static controllers.ServerController.docPaths_to_ids_map;
import static controllers.ServerController.documents_len_table;
import static controllers.ServerController.installation_directory_path;
import static controllers.ServerController.inverted_index;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

/**
 *
 * @author Mohd Azeem
 */
public class LocalIndexer {

    FTPClient ftpClient;
    File serialized_inverted_index;
    File serialized_doc_lenght_table;
    File serialized_docPaths_to_ids_map;
    public static int num_of_docs_indexed = 0;
    private final String ftp_address;
    public static boolean all_files_downloaded_status = false;
//    public Queue documents_queue;

    public LocalIndexer(String ftp_addr) {
//        documents_queue = new LinkedList();        
        ftp_address = ftp_addr;
        ftpClient = new FTPClient();
    }

    public int indexFtpServer() {
        try {
            ftpClient.connect(ftp_address);
            boolean login = ftpClient.login("anonymous", "");
            if (login) {
                System.out.println("Connection established...");

    // get all files from server and store them in an array of  
                // FTPFiles  
                FTPFile[] files = ftpClient.listFiles();
                ArrayList<String> all_files= new ArrayList<>();
                for (FTPFile cur_file : files) {
                    if (cur_file.getType() == FTPFile.FILE_TYPE) {
                        String file_name = cur_file.getName();
                        if(file_name.endsWith(".pdf"))
                            all_files.add(file_name);
                    }
                }
                downloadAndIndex(all_files);
                // logout the user, returned true if logout successfully  
                boolean logout = ftpClient.logout();
                if (logout) {
                    System.out.println("Connection close...");
                }
            } else {
                System.out.println("Connection fail...");
            }

        } catch (IOException ex) {
            System.out.println("Error to connect ftp server where the corpus is stored");
            Logger.getLogger(LocalIndexer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return num_of_docs_indexed;
    }
    
    private void downloadAndIndex(ArrayList<String> all_files_on_ftp){
        FtpFileDownloader indexer = new FtpFileDownloader(ftp_address, all_files_on_ftp);
        indexer.start();
        File directory = new File(installation_directory_path + "\\downloads");
        if(!directory.exists())
            directory.mkdir();
        ArrayList<String> completed = new ArrayList<>();
        while(!all_files_downloaded_status){
            for (File cur_file : directory.listFiles()) {
            if(cur_file.isFile() && !completed.contains(cur_file.getName())){
                try {
                    performIndexing(cur_file.getName());
                    completed.add(cur_file.getName());
                } catch (IOException ex) {
                    System.out.println("can not perform indexing on the file:  "+cur_file.getName());
                    Logger.getLogger(LocalIndexer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        }
        
    }
    
    private void performIndexing(String cur_file_name) throws IOException {
        int cur_docId;
        if (!docPaths_to_ids_map.containsKey(cur_file_name)) {
            cur_docId = num_of_docs_indexed;
            docPaths_to_ids_map.put(cur_file_name, num_of_docs_indexed);
            num_of_docs_indexed++;
        } else {
            cur_docId = docPaths_to_ids_map.get(cur_file_name);
        }

        if (cur_file_name.endsWith("pdf")) {
            System.out.println("reading: " + cur_file_name + " file");
            String text = null;
            PDFdoc cur_doc = new PDFdoc();
            try {
                text = cur_doc.extractPDF(installation_directory_path + "\\downloads\\"+cur_file_name);
                
            } catch (Exception e) {
                System.out.println("can not extract pdf");
                System.err.println();
            }
            String[] tokens_arr;
            if (text == null) {
                System.out.println("problem to read pdf");
                return;
            }
            tokens_arr = text.toLowerCase().split("[^a-z]");
            int total_words = tokens_arr.length;
            for (String tokens_arr1 : tokens_arr) {
                String term = tokens_arr1;
                if (inverted_index.containsKey(term)) // is this term present in Index?
                {
                    TreeMap<Integer, Integer> Doc_hashtable;
                    Doc_hashtable = inverted_index.get(term);

                    if (Doc_hashtable.containsKey(cur_docId)) //is this file (value) present in Index ?
                    {
                        Integer TF_of_the_term_in_this_doc = (Integer) inverted_index.get(term).get(cur_docId);
                        TF_of_the_term_in_this_doc++;
                        Doc_hashtable.put(cur_docId, TF_of_the_term_in_this_doc);
                        inverted_index.put(term, Doc_hashtable);
                    } else // Index does not contain this term
                    {
                        Integer TF_of_this_term_in_this_doc = 1;
                        Doc_hashtable.put(cur_docId, TF_of_this_term_in_this_doc);
                        inverted_index.put(term, Doc_hashtable);

                    }
                } else // Index does not contain this term
                {
                    Integer TF = 1;
                    TreeMap<Integer, Integer> doc_hashtable = new TreeMap<>();
                    doc_hashtable.put(cur_docId, TF);
                    inverted_index.put(term, doc_hashtable);
                }
            }
//            System.out.println(inverted_index);
            documents_len_table.put(cur_docId, new Integer(total_words));

        }

    }


}
