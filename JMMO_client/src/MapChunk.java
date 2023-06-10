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
    public int[][] tilemap = new int[chunkSize][chunkSize];;



    public MapChunk( Vector2f chunkID ) throws SlickException, IOException
    {



        this.chunkID = chunkID;
        chunkLocation = new Vector2f(chunkID.x*chunkPixelsSize , chunkID.y*chunkPixelsSize);
    }


    public void parseChunkUpdateMessage(String receivedMessage)
    {

        String regex = "\\((\\d+)-(\\d+),(\\d+)\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(receivedMessage);

        while (matcher.find()) {

            int firstNumber = Integer.parseInt(matcher.group(1));
            int secondNumber = Integer.parseInt(matcher.group(2));
            int thirdNumber = Integer.parseInt(matcher.group(3));

            //System.out.println(thirdNumber);
            tilemap[firstNumber][secondNumber] = thirdNumber;
        }





    }

    public void draw(TileSet t , Vector2f scroll)
    {
        t.waterAnim.update();
        for(int i = 0 ; i < chunkSize ; i++)
            for(int j = 0 ; j < chunkSize ; j++)
            {

                if(tilemap[i][j] == 1000)
                {
                    t.waterAnim.update();
                    t.waterAnim.draw(new Vector2f((chunkLocation.x + (32 * i)) - scroll.x , (chunkLocation.y + (32 * j)) - scroll.y));
                }
                else
                    t.core.get(tilemap[i][j]).draw((chunkLocation.x + (32 * i)) - scroll.x , (chunkLocation.y + (32 * j)) - scroll.y);
                //System.out.println((chunkLocation.x+32*i -scroll.x) + "  ----  " + (chunkLocation.y + 32 * j - scroll.y));
            }
    }

}
