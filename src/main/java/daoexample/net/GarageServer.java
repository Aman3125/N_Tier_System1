package daoexample.net;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// F10: Multithreaded server using ExecutorService.
public class GarageServer {

    private static final int PORT = 5050;

    public static void main(String[] args) {
        new GarageServer().start();
    }

    public void start() {
        ExecutorService pool = Executors.newFixedThreadPool(10);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server running on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                pool.submit(new ClientHandler(clientSocket));
            }
        } catch (Exception e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }
}