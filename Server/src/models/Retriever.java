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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import static controllers.ServerMasterController.doc_IDs_to_Path_map;
import static controllers.ServerMasterController.documents_len_table;
import static controllers.ServerMasterController.inverted_index;

/**
 *
 * @author Mohd Azeem
 */
public class Retriever {

    private final ArrayList<String> query;
    TreeMap<String, Float> docs_weight_table;
    
    public Retriever(ArrayList<String> cur_query) {
        query = cur_query;
        docs_weight_table = new TreeMap<>();
    }

    public TreeMap<String, Float> retrieveResults() throws IOException {
        if (inverted_index == null) {
            return null;
        }
        
        for (String term : query) {
            if (inverted_index.containsKey(term)) {
                Set<Integer> map_of_Docs_to_this_term = inverted_index.get(term).keySet();
                for (Integer cur_docID : map_of_Docs_to_this_term) {
                    String cur_doc = doc_IDs_to_Path_map.get(cur_docID);
                    if (docs_weight_table.containsKey(cur_doc))
                        docs_weight_table.put(cur_doc, (docs_weight_table.get(cur_doc)*2 + tfIdfWeight(term, cur_docID)));
                    else
                        docs_weight_table.put(cur_doc, tfIdfWeight(term, cur_docID));
                }
            }
        }
        return docs_weight_table;
    }
    
    private float tfIdfWeight(String cur_term, Integer cur_document)
            throws IOException {
        String term = cur_term;
        float TermFrq = 0;
        float TotalTermsInDoc = 0;
        float NumOfDocs_in_corpus = 0;
        float num_of_docs_contain_the_term = 0;

        TermFrq = ((inverted_index.get(term)).get(cur_document));
        TotalTermsInDoc = (float) documents_len_table.get(cur_document);
        NumOfDocs_in_corpus = documents_len_table.size();
        num_of_docs_contain_the_term = (inverted_index.get(term)).size();

        if (TotalTermsInDoc <= 0) {
            TotalTermsInDoc = 1;
        }
        if (num_of_docs_contain_the_term <= 0) {
            num_of_docs_contain_the_term = 1;
        }
        if(num_of_docs_contain_the_term == NumOfDocs_in_corpus)
            NumOfDocs_in_corpus++;

//        float TF = (TermFrq) / TotalTermsInDoc;
//        float IDF = (float) Math.log((float) NumOfDocs_in_corpus / (float) num_of_docs_contain_the_term);        
        // modified formula
        float TF = (float) ((TermFrq) / Math.log(TotalTermsInDoc+1));
        float IDF = (float) NumOfDocs_in_corpus / (float) num_of_docs_contain_the_term;
//        System.out.println("TF="+TF+"  IFD="+IDF);
        return (TF * IDF);
    }
}
