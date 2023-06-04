import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import java.util.Vector;

public class TileSet
{
    private String sourceImagePath;
    private Image sourceImage;
    public Vector<Image> core = new Vector<Image>();
    private int tileSize;

    public TileSet(String sourceImagePath, int tileSize) throws SlickException
    {
            // Afficher un autre message
        this.sourceImagePath = sourceImagePath;
        sourceImage = new Image(sourceImagePath);
        this.tileSize = tileSize;
        //
        int lineOfTilesWidth = sourceImage.getWidth()/tileSize;
        int lineOfTilesHeight = sourceImage.getHeight()/tileSize;
        //System.out.println(lineOfTilesWidth + " , " + lineOfTilesHeight);
        int i =0 , j=0;
        while(j < lineOfTilesHeight)
        {
            i=0;
            while(i < lineOfTilesWidth)
            {
                core.add(sourceImage.getSubImage(i*tileSize, j*tileSize , tileSize, tileSize));
                i++;
            }
            j++;
        }
        //System.out.println("tileset size = " + core.size());
    }

    public Image getImageAtIndex(int i)
    {
        return core.get(i);
    }
}
