import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MapChunk
{
    public Vector2f chunkID;
    public Vector2f chunkLocation ;

    public final int chunkSize = 20 ;
    public final int chunkPixelsSize = chunkSize *32;
    public int[][] tilemap;



    public MapChunk( Vector2f chunkID ) throws SlickException, IOException
    {



        this.chunkID = chunkID;
        tilemap = new int[chunkSize][chunkSize];
        chunkLocation = new Vector2f(chunkID.x*chunkPixelsSize , chunkID.y*chunkPixelsSize);
    }


    public void parseChunkUpdateMessage(String receivedMessage)
    {

        String regex = "\\(((\\d+-\\d+),(\\d+))\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(receivedMessage);

        while (matcher.find()) {
            String group = matcher.group(1);
            String[] parts = group.split("-");

            int firstNumber = Integer.parseInt(parts[0]);
            int secondNumber = Integer.parseInt(parts[1].split(",")[0]);
            int thirdNumber = Integer.parseInt(parts[1].split(",")[1]);

            //System.out.println(group);
            tilemap[firstNumber][secondNumber] = thirdNumber;
        }





    }

    public void draw(TileSet t , Vector2f scroll)
    {
        for(int i = 0 ; i < chunkSize ; i++)
            for(int j = 0 ; j < chunkSize ; j++)
            {
                t.core.get(tilemap[i][j]).draw((chunkLocation.x + (32 * i)) - scroll.x , (chunkLocation.y + (32 * j)) - scroll.y);
                //System.out.println((chunkLocation.x+32*i -scroll.x) + "  ----  " + (chunkLocation.y + 32 * j - scroll.y));
            }
    }

}
