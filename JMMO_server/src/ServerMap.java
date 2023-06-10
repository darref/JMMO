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


                    int abcisseMinChunkToLoad = currentPlayerChunk.x - 10;
                    int ordonneeMinChunkToLoad = currentPlayerChunk.y - 10;
                    int abcisseMaxChunkToLoad = currentPlayerChunk.x + 10;
                    int ordonneeMaxChunkToLoad = currentPlayerChunk.y + 10;


                    boolean mapHasBeenExtended = false;
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
                                mapHasBeenExtended = true;
                            } else {
                                chunk.readFromFile("map/chunks/MapChunk[" + i + "," + j + "].chunk");
                            }
                            String chunkMessage = chunk.generateChunkDatas();
                            //System.out.println(chunkMessage);
                            //Thread.sleep(10);
                            nc.send(chunkMessage);
                            //

                        }
                    }
                    if(mapHasBeenExtended)
                        this.addOrNotSceneries();
                    //

                } catch (IOException e) {
                    System.out.println("current chunk player = " + currentPlayerChunk.x + " " + currentPlayerChunk.y);

                    throw new RuntimeException(e);
                }
            }
        });
        chunkUpdateSendThread.start();

    }

    private void addOrNotSceneries() throws IOException {
        addOrNotLake();
        addOrNotReliefs();
        addOrNotForest();

    }

    private void addOrNotForest() {
        if(!Utils.randomBool())
            return;

    }

    private void addOrNotReliefs() {
        if(!Utils.randomBool())
            return;

    }

    private void addOrNotLake() throws IOException {

        boolean negativeAbcisse = Utils.randomBool();
        boolean negativeOrdonnee = Utils.randomBool();
        ChunkCoordinates lakeCenter = new ChunkCoordinates(currentPlayerChunk.x  + (negativeAbcisse? -15 : 15) , currentPlayerChunk.y + (negativeOrdonnee? -15 : 15));
        System.out.println("le centre du lac est :" + lakeCenter.x + " " + lakeCenter.y);
        int lakeSizeX = Utils.randomRanged(1,4);
        int lakeSizeY = Utils.randomRanged(1,4);

        if(chunks.get(lakeCenter) == null)
            chunks.put(lakeCenter , new MapChunk(lakeCenter.x , lakeCenter.y));
        chunks.get(lakeCenter).makeLake();
        chunks.get(lakeCenter).saveInFile();
        nc.send(chunks.get(lakeCenter).generateChunkDatas());
        for (int i=0; i < lakeSizeX ; i++)
            for(int j = 0 ; j < lakeSizeY ; j++)
            {
                System.out.println(i + " " + j + "= lakesize");
                if(chunks.get(new ChunkCoordinates(lakeCenter.x + i, lakeCenter.y + j)) == null)
                    chunks.put(new ChunkCoordinates(lakeCenter.x + i, lakeCenter.y + j) , new MapChunk(lakeCenter.x + i, lakeCenter.y + j));
                chunks.get(new ChunkCoordinates(lakeCenter.x + i, lakeCenter.y + j)).makeLake();
                chunks.get(new ChunkCoordinates(lakeCenter.x + i, lakeCenter.y + j)).saveInFile();
                nc.send(chunks.get(new ChunkCoordinates(lakeCenter.x + i, lakeCenter.y + j)).generateChunkDatas());
            }

    }


}
