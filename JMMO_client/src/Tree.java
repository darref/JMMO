import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class Tree
{
    public Vector2f location = new Vector2f(0,0);
    public Vector2f size = new Vector2f(0,0);
    private Image image = new Image(System.getProperty("user.dir").replace("\\" , "/") + "ressources/trees/tree1.png");

    public Tree(Vector2f size) throws SlickException {
        this.size = size;
    }

    public void setLocation(float x , float y)
    {
        location = new Vector2f(x,y);
    }

    public void draw()
    {
        image.draw(location.x , location.y , size.x , size.y);
    }
}
