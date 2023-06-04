import org.newdawn.slick.geom.Vector2f;

import java.io.IOException;
import java.net.Socket;
import java.io.File;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerMap
{
    private final NetworkConnection nc;
    private final Server serverRef;
    private final ServerPlayer playerRef;

    private final Map<ChunkCoordinates, MapChunk> chunks ;
    private ChunkCoordinates currentPlayerChunk ;
    Semaphore semaphore = new Semaphore(1);

    public ServerMap(Socket s , Server server , ServerPlayer playerRef) throws IOException
    {
        this.nc = new NetworkConnection(s);
        this.serverRef = server;
        this.playerRef = playerRef;
        chunks = new HashMap<>();
        currentPlayerChunk = new ChunkCoordinates(0,0);
    }


    public   void init()
    {


        Thread chunkUpdateSendThread = new Thread( () ->
        {
            while(true) {
                try {
                    Thread.sleep(50);
                    currentPlayerChunk.x =  (playerRef.getLocationX() / (20 * 32));
                    currentPlayerChunk.y =  (playerRef.getLocationY() / (20 * 32));
                    System.out.println("current chunk player = " + currentPlayerChunk.x + " " + currentPlayerChunk.y);
                    System.out.println("position actuelle du joueur = " + playerRef.getLocationX() + " " + playerRef.getLocationY());

                    int idx = currentPlayerChunk.x;
                    int idy = currentPlayerChunk.y;

                    int abcisseMinChunkToLoad = idx - 4;
                    int ordonneeMinChunkToLoad = idy - 4;
                    int abcisseMaxChunkToLoad = idx + 4;
                    int ordonneeMaxChunkToLoad = idy + 4;

                    for (int i = abcisseMinChunkToLoad; i <= abcisseMaxChunkToLoad; i++)
                    {
                        for (int j = ordonneeMinChunkToLoad; j <= ordonneeMaxChunkToLoad; j++)
                        {
                            //System.out.println(" Indices = " + i + " " + j);

                            ChunkCoordinates chc = new ChunkCoordinates(i, j);
                            MapChunk chunk = new MapChunk(i, j);
                            chunks.put(chc, chunk);
                            if (!FileReaderWriter.fileExists("map/chunks/MapChunk[" + i + "," + j + "].chunk")) {
                                chunk.randomizedGeneration();
                                chunk.saveInFile();
                            } else {
                                chunk.readFromFile("map/chunks/MapChunk[" + i + "," + j + "].chunk");
                            }


                            String chunkMessage = chunk.createMessageChunk();
                            //System.out.println(chunkMessage);
                            Thread.sleep(10);
                            nc.send(chunkMessage);
                        }
                    }
                } catch (IOException e) {
                    System.out.println("current chunk player = " + currentPlayerChunk.x + " " + currentPlayerChunk.y);

                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    System.out.println("current chunk player = " + currentPlayerChunk.x + " " + currentPlayerChunk.y);

                    throw new RuntimeException(e);
                }
            }
        });
        chunkUpdateSendThread.start();

    }


}
