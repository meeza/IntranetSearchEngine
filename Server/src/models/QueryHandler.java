package models;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QueryHandler extends Thread{
    
    private Thread t;
    private final Socket client;
//    private String query;

    public QueryHandler(Socket client_socket) {
        client = client_socket;
    }
    private TreeMap<String, Float> retrieve(ArrayList<String> qury){
        Retriever retriever = new Retriever(qury);
        try {
            return retriever.retrieveResults();
        } catch (IOException ex) {
            System.out.println("can not retrieve results..");
            Logger.getLogger(QueryHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    @Override
    public void run(){
        System.out.println("running new QueryHandler thread..");
        try {
            ObjectInputStream in_from_client = new ObjectInputStream(client.getInputStream());
            ObjectOutputStream out_result_to_client = new ObjectOutputStream(client.getOutputStream());
            System.out.println("trying to read client mssg..");
            ArrayList<String> query = null;
            try {
                query = new ArrayList<>((ArrayList<String>) in_from_client.readObject());
            } catch (IOException iOException) {
                System.out.println("I/O error in reading query..");
            } catch (ClassNotFoundException classNotFoundException) {
                System.out.println("ClassNotFoundException error in reading query..");
            }
            System.out.println("starting to retrieve..");
            if(query != null){
                TreeMap<String, Float> results = retrieve(query);
                System.out.println("results generated... now at QueryHandler of Server");
                out_result_to_client.writeObject(topResultsGenerator(results));                
            }
            else
                System.out.println("can not retrieve..");
        } catch (IOException ex) {
            System.out.println("can not get I/O stream from client..");
            Logger.getLogger(QueryHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    private ArrayList<String> topResultsGenerator(TreeMap<String, Float> unsorted_results){
        ArrayList<String> top_results = new ArrayList<>();
        ResultsManipulator rm = new ResultsManipulator();
        TreeMap<String, Float> sorted_results = rm.getRankedDocuments(unsorted_results);
        int i = 0;
        for (Map.Entry<String, Float> entry : sorted_results.entrySet()) {
            if(i >= 5)
                break;
            String doc_id = entry.getKey();
            top_results.add(i, doc_id);
            i++;
        }
        return top_results;
    }
}
