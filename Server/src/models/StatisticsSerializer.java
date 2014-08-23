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

import controllers.ServerMasterController;
import static controllers.ServerMasterController.docPaths_to_ids_map;
import static controllers.ServerMasterController.doc_IDs_to_Path_map;
import static controllers.ServerMasterController.installation_directory_path;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author Mohd Azeem
 */
public class StatisticsSerializer {
    public static boolean loadStatisticsObjects(){
        try {
            System.out.println("loading serialized objects..");
            FileInputStream fileInDocLenTable = new FileInputStream(installation_directory_path + "\\docLenTable.kh");
            FileInputStream fileInIndex = new FileInputStream(installation_directory_path + "\\index.kh");
            FileInputStream fileIn_docPaths_to_ids_map = new FileInputStream(installation_directory_path + "\\DocIdKeyMap.kh");
            ObjectInputStream in_index = new ObjectInputStream(fileInIndex);
            ObjectInputStream in_doc_len_table = new ObjectInputStream(fileInDocLenTable);
            ObjectInputStream in_docPaths_to_ids_map = new ObjectInputStream(fileIn_docPaths_to_ids_map);
            ServerMasterController.inverted_index = (TreeMap<String, TreeMap<Integer,Integer>>) in_index.readObject();
            System.out.println("Index loaded.");
            ServerMasterController.documents_len_table = (TreeMap<Integer, Integer>) in_doc_len_table.readObject();
            System.out.println("doc len table loaded.");
            ServerMasterController.docPaths_to_ids_map = (TreeMap<String, Integer>) in_docPaths_to_ids_map.readObject();
            System.out.println("doc-path to ids map loaded.");
            in_index.close();
            in_doc_len_table.close();
            in_docPaths_to_ids_map.close();
            fileInIndex.close();
            fileInDocLenTable.close();
            fileIn_docPaths_to_ids_map.close();
            return true;
        } catch (FileNotFoundException ex) {
            System.out.println("FileNotFoundException");
            return false;
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("IOException or ClassNotFoundException");
            return false;
        }
    }
    
    public static void reMapping() {
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
}
