package controllers;
import java.net.*;
import java.io.*;
import models.QueryHandler;

public class ServerSocketsController extends Thread {
    private ServerSocket serverSocket;
    public ServerSocketsController(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public void run() {
        while (true) {
            try {
                System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");
                Socket server_socket = serverSocket.accept();
                System.out.println("Just connected to " + server_socket.getRemoteSocketAddress());
                QueryHandler query_thread = new QueryHandler(server_socket);
                query_thread.start();
            } catch (SocketTimeoutException s) {
                System.out.println("Socket timed out!");
                break;
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    
}
