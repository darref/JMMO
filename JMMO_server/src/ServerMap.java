import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class ServerMap
{
    private final NetworkConnection nc;
    private final Server serverRef;
    public volatile ServerPlayer playerRef;
    private final Map<ChunkCoordinates, MapChunk> chunks ;
    private Vector<Scenery> sceneries;
    private ChunkCoordinates currentPlayerChunk ;

    public ServerMap(Socket s , Server server ) throws IOException
    {
        this.nc = new NetworkConnection(s);
        this.serverRef = server;
        chunks = new HashMap<>();
        currentPlayerChunk = new ChunkCoordinates(0,0);
    }


    public   void init()
    {


        Thread chunkUpdateSendThread = new Thread( () ->
        {
            while(true) {
                try {
                    //Thread.sleep(50);
                    if(playerRef != null)
                    {
                        synchronized (playerRef.position) {
                            currentPlayerChunk.x =  (int)(playerRef.position.get().x / (20.0f * 32.0f));
                            currentPlayerChunk.y =  (int)(playerRef.position.get().y / (20.0f * 32.0f));
                            System.out.println("current chunk player = " + currentPlayerChunk.x + " " + currentPlayerChunk.y);
                            System.out.println("position actuelle du joueur = " + playerRef.position.get().x + " " + playerRef.position.get().y);
                        }

                    }


                    int abcisseMinChunkToLoad = currentPlayerChunk.x - 4;
                    int ordonneeMinChunkToLoad = currentPlayerChunk.y - 4;
                    int abcisseMaxChunkToLoad = currentPlayerChunk.x + 4;
                    int ordonneeMaxChunkToLoad = currentPlayerChunk.y + 4;

                    for (int i = abcisseMinChunkToLoad; i <= abcisseMaxChunkToLoad; i++)
                    {
                        for (int j = ordonneeMinChunkToLoad; j <= ordonneeMaxChunkToLoad; j++)
                        {
                            //
                            ChunkCoordinates chc = new ChunkCoordinates(i, j);
                            MapChunk chunk = new MapChunk(i, j);
                            chunks.put(chc, chunk);
                            if (!FileReaderWriter.fileExists("map/chunks/MapChunk[" + i + "," + j + "].chunk")) {
                                chunk.randomizeFloor();
                                chunk.saveInFile();
                                this.addOrNotSceneries();
                            } else {
                                chunk.readFromFile("map/chunks/MapChunk[" + i + "," + j + "].chunk");
                            }
                            String chunkMessage = chunk.generateFloorChunkDatas();
                            //System.out.println(chunkMessage);
                            Thread.sleep(10);
                            nc.send(chunkMessage);
                            //

                        }
                    }
                    //

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

    private void addOrNotSceneries()
    {
        addOrNotLake();
        addOrNotReliefs();
        addOrNotForest();

    }


}
