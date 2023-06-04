import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.SlickException;
import java.util.Vector;

public class Animation  {
    private Vector<JMMOImage> images;
    private JMMOImage currentImage;
    private int currentFrameIndex;
    private long lastFrameTime;
    private int framerate;
    private boolean stopped = false;


    public Animation(String folderPath , int framerate ) throws SlickException {
        this.framerate = framerate;
        images = new Vector<JMMOImage>();
        Vector<String> paths = FolderFilesGetter.getFilePaths(folderPath);
        for(String s : paths)
        {
            System.out.println(s + "-> image chargée");
            images.add(new JMMOImage(s, 0,0 , 100, 100));
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
        for(JMMOImage i : images)
        {
            i.setLocation(x,y);
        }
    }
    public void setSize(int x , int y)
    {
        for(JMMOImage i : images)
        {
            i.setSize(x,y);
        }
    }

    public Vector2f getLocation()
    {
        return images.get(0).getLocation();
    }
    public Vector2f getSize()
    {
        return images.get(0).getSize();
    }

    public void draw()
    {
        currentImage.draw();
    }
}
