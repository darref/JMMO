import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Vector;

public class Server {
    private  ArrayList<ServerPlayer> players = new ArrayList<>();
    private  ArrayList<ServerMap> clientMaps = new ArrayList<>();

    private Vector<SocketAddress> allSocketsAdresses = new Vector<>();

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


                    if(!allSocketsAdresses.contains(clientSocket.getLocalSocketAddress()))
                    {
                        allSocketsAdresses.add(clientSocket.getLocalSocketAddress());
                        //
                        ServerPlayer player = new ServerPlayer(clientSocket ,this);
                        players.add(player);
                        player.init();
                        System.out.println("New player(client) connected: " + player);
                        //
                        ServerMap map = new ServerMap(clientSocket, this );
                        clientMaps.add(map);
                        map.init();
                        System.out.println("Player [" + player + "] is ready to receive map update");
                        //
                        player.mapRef = map;
                        map.playerRef = player;
                    }

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
