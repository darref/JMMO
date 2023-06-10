import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.SlickException;
import java.util.Vector;

public class Animation  {
    private Vector<Image> images;
    private Image currentImage;
    private int currentFrameIndex;
    private long lastFrameTime;
    private int framerate;
    private boolean stopped = false;
    private Vector2f location = new Vector2f(0,0);
    private Vector2f size = new Vector2f(100,100);


    public Animation(String folderPath , int framerate ) throws SlickException {
        this.framerate = framerate;
        images = new Vector<Image>();
        Vector<String> paths = FolderFilesGetter.getFilePaths(folderPath);
        for(String s : paths)
        {
            System.out.println(s + "-> image chargée");
            images.add(new Image(s));
        }
        currentImage = images.get(0);
        currentFrameIndex = 0;
        lastFrameTime = System.currentTimeMillis();
    }


    public void update()
    {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - lastFrameTime;
        //System.out.println(elapsedTime);
        if(elapsedTime > (1000 / framerate) && !stopped)
        {
            currentFrameIndex = (currentFrameIndex + 1) % images.size();
            currentImage = images.get( currentFrameIndex);
            lastFrameTime = currentTime;

        }
    }

    public void setStopped(boolean is)
    {
        stopped = is;
    }

    public void setPosition(float x , float y)
    {
        location.x = x; location.y=y;
    }


    public Vector2f getLocation()
    {
        return location;
    }

    public void draw(Vector2f loc)
    {
        currentImage.draw(loc.x , loc.y , size.x , size.y);
    }
    public void draw()
    {
        currentImage.draw(location.x , location.y , size.x , size.y);
    }

    public void setSize(int i, int i1) {
        size.x = i;
        size.y = i1;
    }

    public Vector2f getSize() {
        return  size;
    }
}
