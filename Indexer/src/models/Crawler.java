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
import static controllers.ServerController.inverted_index;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mohd Azeem
 */
public class Crawler {

    File corpus_folder;
    File serialized_inverted_index;
    File serialized_doc_lenght_table;
    File serialized_docPaths_to_ids_map;
    private int num_of_docs_indexed = 0;
    private final String corpus_path;

    public Crawler(String path) {
        corpus_path = path;
        corpus_folder = new File(path);
    }

    public int crawl() throws IOException {
        System.out.println("at line 50 i.e. in crawl()");        
        ArrayList<File> list_of_Files_in_folder = new ArrayList<>();
        if (listAllFilesInFolder(corpus_folder, list_of_Files_in_folder)) {
            System.out.println("at line 53 i.e. in crawl() while list of files is retrieved");
            for (File cur_file : list_of_Files_in_folder) {
                performIndexing(cur_file);
            }
//            load_status = true;
        } else {            
            System.out.println("can't list all the files of corpus\n");
        }

        // till this point the indexing is completed.
        // so now save the inverted_index and others as serialized object
        StatisticsSerializer.saveStatisticObjects();
        return num_of_docs_indexed;
    }

    private boolean listAllFilesInFolder(final File folder, ArrayList<File> List_of_Files) {
//        System.out.println("at line 69 i.e. entry in listAllFilesInFolder()");
        try {
            for (final File fileEntry : folder.listFiles()) {
                if (fileEntry.isDirectory()) {
                    listAllFilesInFolder(fileEntry, List_of_Files);
                } else {
                    List_of_Files.add(fileEntry);
                }
            }
//        total_no_of_docs = List_of_Files.size();
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    private void performIndexing(File cur_file) throws IOException {
        String cur_file_path = cur_file.getAbsolutePath();
        int cur_docId;
        if (!docPaths_to_ids_map.containsKey(cur_file_path)) {
            cur_docId = num_of_docs_indexed;
            docPaths_to_ids_map.put(cur_file_path, num_of_docs_indexed);
            num_of_docs_indexed++;
        } else {
            cur_docId = docPaths_to_ids_map.get(cur_file_path);
        }

        if (cur_file_path.endsWith("pdf")) {
            System.out.println("reading: " + cur_file_path + " file");
            String text = null;
            PDFdoc cur_doc = new PDFdoc();
            try {
                text = cur_doc.extractPDF(cur_file_path);
            } catch (Exception e) {
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
        else if (cur_file.getPath().endsWith("txt")) {
            BufferedReader in = null;
            try {
                System.out.println("reading .txt file");
                String string_containing_line;
                String[] str_arr;
                in = new BufferedReader(new FileReader(cur_file_path));
                if(in == null)
                    return;
                int total_words = 0;
                while ((string_containing_line = in.readLine()) != null) {
                    str_arr = (string_containing_line.toLowerCase().split("[^a-z]"));
                    int terms_in_line = str_arr.length;
                    total_words = total_words + terms_in_line;
                    for (String str_arr1 : str_arr) {
                        String term = str_arr1;
                        if (inverted_index.containsKey(term)) //is this file (value) present in Index ?
                        {
                            TreeMap<Integer, Integer> doc_hashtable;
                            doc_hashtable = inverted_index.get(term);
                            if (inverted_index.get(term).containsKey(cur_docId)) //is this file (value) present in Index ?
                            {
                                Integer TF_of_the_term_in_this_doc = (Integer) inverted_index.get(term).get(cur_docId);
                                TF_of_the_term_in_this_doc++;
                                doc_hashtable.put(cur_docId, TF_of_the_term_in_this_doc);
                                inverted_index.put(term, doc_hashtable);
                            } else // this file is not present in Index
                            {
                                Integer TF_of_this_term_in_this_doc = 1;
                                doc_hashtable.put(cur_docId, TF_of_this_term_in_this_doc);
                                inverted_index.put(term, doc_hashtable);
                                
                            }
                        } else // Index does not contain this term
                        {
                            TreeMap<Integer, Integer> doc_hashtable = new TreeMap<>();
                            Integer TF = 1;
                            doc_hashtable.put(cur_docId, TF);
                            inverted_index.put(term, doc_hashtable);
                        }
                    }
                    documents_len_table.put(cur_docId, new Integer(total_words));
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    in.close();
                } catch (IOException ex) {
                    Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

}
