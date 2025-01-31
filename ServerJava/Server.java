package ServerJava;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

import ServerJava.Game.GameCache;
import ServerJava.ServerContext.ClientHandler;
import ServerJava.ServerContext.DataBase;
import ServerJava.ServerContext.GlobalContext;
import ServerJava.ServerContext.User;
import ServerJava.ServerContext.UserCache;

public class Server {

    // port number to listen on
    private int PORT;

    // server socket service
    private ServerSocketService ss;

    private static GlobalContext globalContext;

    // binary semaphore to manage access to global context
    public static Semaphore globalContextSem;

    // binary semaphore to manage access to user cache
    public static Semaphore userCacheSem;

    UserCache userCache;

    GameCache gameCache;

    DataBase dataBase;

    // path to user database
    private final Path path = Paths.get("user_database.txt");

    public Server(int PORT){

        this.PORT = PORT;

        this.userCache = new UserCache();

        this.gameCache = new GameCache();

        ss = new ServerSocketService(PORT);

        this.dataBase = new DataBase(path, userCache);

        globalContext = new GlobalContext(userCache, gameCache, dataBase);


        start(ss);
    }

    /**
     * Start the server
     * @param ss
     */
    public void start(ServerSocketService ss) {
        // load database
        // TODO: make database not a thread and load it in the constructor do not run in thread 
        dataBase.run();

        while (ss.isAccepting()) {

            Socket clientSocket = ss.acceptConnection();
            Thread clientThread = new Thread(() -> {
                try {
                    ClientHandler clientHandler = new ClientHandler(clientSocket, globalContext);
                    while (clientHandler.clientStatus) {
                        clientHandler.CommandHandler();
                    }
                } catch (SocketException e) {
                    System.out.println("Client has left the server!!");
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            });
            clientThread.start();

            // TODO: handle when client disconnects
        }
        
        // TODO: handle when server disconnects
    }

    public static void main(String[] args) {
        System.out.println("multi-threaded server...");
        int PORT = 3001;
        Server server = new Server(PORT);
    }
}