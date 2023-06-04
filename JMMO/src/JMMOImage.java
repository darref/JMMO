import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class JMMOImage {
    private Image image;
    private float locationx, locationy;
    private int sizeX , sizeY;

    public JMMOImage(String imagePath, int locx, int locy , int sizeX , int sizeY) throws SlickException {
        image = new Image(imagePath);
        this.locationx = locx;
        this.locationy = locy;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

    public void draw()
    {

        image.draw(locationx, locationy , sizeX , sizeY);
    }

    public void setLocation(float x,float y)
    {
        this.locationx = x;
        this.locationy = y;
    }
    public void setSize(int x,int y)
    {
        this.sizeX = x;
        this.sizeY = y;
    }

    public Vector2f getLocation()
    {
        return new Vector2f(locationx,locationy);
    }
    public Vector2f getSize()
    {
        return new Vector2f(sizeX,sizeY);
    }
}
