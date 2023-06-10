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
    public  Map<ChunkCoordinates, MapChunk> chunks;
    private Vector<Tree> treesVector = new Vector<Tree>();
    private TileSet tileset;
    private NetworkConnection n;

    private Vector2f scroll = new Vector2f(0,0);

    public ChunkCoordinates currentPlayerChunk = new ChunkCoordinates(0 , 0);

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
                    receivedMessage = receivedMessage.replaceAll("okUpdateChunk" , "");
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
                                chunks.put(chc, new MapChunk(new Vector2f(idx, idy)));
                            } catch (SlickException e) {
                                throw new RuntimeException(e);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            chunks.get(chc).parseChunkUpdateMessage(receivedMessage);
                            //System.out.println("Creation et remplissage d'un nouveau chunk");
                            //
                        }




                    }


                }



            }
        });
        receiveChunkUpdateThread.start();
        /*
        Thread receiveTreesUpdate = new Thread( () ->
        {
            while(true)
            {
                String message = "";
                try {
                    message = n.receive();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if(message.contains("ThereIsATreeAt"))
                {
                    Tree t = null;
                    try {
                        t = new Tree(new Vector2f(300,300));
                    } catch (SlickException e) {
                        throw new RuntimeException(e);
                    }
                    String regex = "\\[(\\d+)\\|(\\d+)\\]";

                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(message);

                    if (matcher.find()) {
                        t.setLocation(Float.parseFloat(matcher.group(1)) , Float.parseFloat(matcher.group(2)));
                    }
                    boolean existAlready = false;
                    for(Tree tr : treesVector)
                        if(tr.location.equals(t.location) )
                            existAlready = true;
                    if(!existAlready)
                        treesVector.add(t);
                }
            }

        });
        receiveTreesUpdate.start();*/
    }

    public void draw()
    {

        try {
                int x = currentPlayerChunk.x;
                int y = currentPlayerChunk.y;
                for (int i = x - 3; i < x + 3; i++)
                    for (int j = y - 3; j < y + 3; j++)
                        if (chunks.get(new ChunkCoordinates(i,j)) != null) {
                            chunks.get(new ChunkCoordinates(i,j)).draw(tileset, scroll);
                            //System.out.println("dessin du chunk " + i + " , " + j );
                        }
                        else {
                            //System.out.println("chunk introuvable" );
                        }

        }catch(ArrayIndexOutOfBoundsException e){}

        for(Tree t :treesVector)
            t.draw();

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
        currentPlayerChunk.x = (int)(playerRef.getLocation().x / (20*32)) + (playerRef.getLocation().x>0? 0 : -1);
        currentPlayerChunk.y = (int)(playerRef.getLocation().y / (20*32)) + (playerRef.getLocation().y>0? 0 : -1) ;
        System.out.println("current chunk player = " + currentPlayerChunk.x + " " + currentPlayerChunk.y);
    }
}
