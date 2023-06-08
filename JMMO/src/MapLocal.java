import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapLocal
{
    public Player playerRef;
    private  Map<ChunkCoordinates, MapChunk> chunks;
    private TileSet tileset;
    private NetworkConnection n;

    private Vector2f scroll = new Vector2f(0,0);

    private ChunkCoordinates currentPlayerChunk = new ChunkCoordinates(0 , 0);

    public MapLocal(Socket mainSocket ,String tilesetImagePath) throws SlickException, IOException {
        tileset = new TileSet(tilesetImagePath, 32);

        n = new NetworkConnection(mainSocket);
        chunks = new HashMap<>();
    }

    public void init()
    {

        Thread receiveChunkUpdateThread = new Thread( () ->
        {
            while(true)
            {
                String receivedMessage = null;
                try {
                    receivedMessage = n.receive();
                    //System.out.println(receivedMessage);
                } catch (IOException e) {
                    System.out.println("message non reçu");
                    throw new RuntimeException(e);
                }

                if(receivedMessage.contains("okUpdateChunk"))
                {
                    receivedMessage.replaceAll("okUpdateChunk" , "");
                    //System.out.println(receivedMessage +"------------");
                    String regex = "\\[(-?\\d+),(-?\\d+)\\]";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(receivedMessage);
                    if (matcher.find())
                    {
                        int idx = Integer.parseInt(matcher.group(1));
                        int idy = Integer.parseInt(matcher.group(2));

                        ChunkCoordinates chc = new ChunkCoordinates(idx ,idy);
                        //System.out.println(chc +"------------");

                        if(chunks.get(chc) != null )
                            chunks.get(chc).parseChunkUpdateMessage(receivedMessage);
                        else
                        {

                            try {
                                if (chunks.containsKey(chc)) {
                                    chunks.get(chc).parseChunkUpdateMessage(receivedMessage);
                                } else {
                                    chunks.put(chc, new MapChunk(new Vector2f(idx, idy)));
                                    chunks.get(chc).parseChunkUpdateMessage(receivedMessage);
                                    //System.out.println("Creation et remplissage d'un nouveau chunk");
                                }
                            } catch (SlickException e) {
                                System.out.println("Impossible d'accéder à ce chunk");
                                throw new RuntimeException(e);
                            } catch (IOException e) {
                                System.out.println("Impossible d'accéder à ce chunk");
                                throw new RuntimeException(e);
                            }

                        }

                    }


                }



            }
        });
        receiveChunkUpdateThread.start();
    }

    public void draw()
    {
        try {
                int x = currentPlayerChunk.x;
                int y = currentPlayerChunk.y;
                for (int i = x - 4; i < x + 4; i++)
                    for (int j = y - 4; j < y + 4; j++)
                        if (chunks.get(new ChunkCoordinates(i,j)) != null) {
                            chunks.get(new ChunkCoordinates(i,j)).draw(tileset, scroll);
                            System.out.println("dessin du chunk " + i + " , " + j );
                        }
                        else {
                            System.out.println("chunk introuvable" );
                        }

        }catch(ArrayIndexOutOfBoundsException e){}

    }

    public void scroll(Vector2f scrollAMount)
    {
        this.scroll.x += scrollAMount.x;
        this.scroll.y += scrollAMount.y;
    }
    public void scroll(float x , float y)
    {
        this.scroll.x += x;
        this.scroll.y += y;
    }

    public Vector2f getScroll()
    {
        return  scroll;
    }

    public void update()
    {
        currentPlayerChunk.x = (int)(playerRef.getLocation().x / (20*32) );
        currentPlayerChunk.y = (int)(playerRef.getLocation().y / (20*32) ) ;
        System.out.println("current chunk player = " + currentPlayerChunk.x + " " + currentPlayerChunk.y);
    }
}
