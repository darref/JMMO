import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private static final ArrayList<ServerPlayer> players = new ArrayList<>();
    private static final ArrayList<ServerMap> clientMaps = new ArrayList<>();

    public  void ListenForConnections() throws IOException
    {
        ServerSocket serverSocket = new ServerSocket(7777);
        System.out.println("Server started");

        // Thread pour accepter les connexions des clients
        Thread acceptThread = new Thread( () ->
        {
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();

                    //
                    ServerPlayer player = new ServerPlayer(clientSocket ,this);
                    players.add(player);
                    player.init();
                    System.out.println("New player(client) connected: " + player);
                    //
                    ServerMap map = new ServerMap(clientSocket, this , player);
                    clientMaps.add(map);
                    map.init();
                    System.out.println("Player [" + player + "] is ready to receive map update");
                    //

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        acceptThread.start();





    }


    public void removePlayer(ServerPlayer serverPlayer)
    {
        players.remove(serverPlayer);
    }
}
