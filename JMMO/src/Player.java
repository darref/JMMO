import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


public class Player
{
    private Animation playerWalkFront;
    private Animation playerWalkTop;
    private Animation playerWalkLeft;
    private Animation playerWalkRight;
    private Animation currentAnimation;
    private float speed = 300.0f;
    private enumDirection direction;
    private NetworkConnection n ;
    private Vector2f location = new Vector2f(0.0f,0.0f);
    private boolean canMove;
    private boolean waitingForResponse;
    private Chronometer chrono;
    private List<Vector2f> positions = new ArrayList<Vector2f>();
    private Vector<MapLocal> mapLayersRef;


    public Player(Vector<MapLocal> mapLayersRef) throws SlickException, IOException {
        playerWalkFront = new Animation("C:/Users/jorda/Desktop/JMMO/ressources/animations/character/walk/front" , 60);
        playerWalkTop = new Animation("C:/Users/jorda/Desktop/JMMO/ressources/animations/character/walk/Top" , 60);
        playerWalkLeft = new Animation("C:/Users/jorda/Desktop/JMMO/ressources/animations/character/walk/Left" , 60);
        playerWalkRight = new Animation("C:/Users/jorda/Desktop/JMMO/ressources/animations/character/walk/Right" , 60);
        playerWalkFront.setSize(100,100);
        playerWalkTop.setSize(100,100);
        playerWalkLeft.setSize(100,100);
        playerWalkRight.setSize(100,100);

        currentAnimation = playerWalkFront;
        n = new NetworkConnection(new Socket(InetAddress.getLocalHost() , 7777));
        chrono = new Chronometer();
        chrono.init();

        this.mapLayersRef = mapLayersRef;
    }

    public void init(GameContainer container)
    {
        setLocation(0.0f,0.0f);

        Thread manageInputsThread = new Thread(  () ->
        {
            while (true) {
                Input input = container.getInput();
                if (input.isKeyDown(Input.KEY_S)) {
                    if (input.isKeyDown(Input.KEY_Q)) {
                        direction = enumDirection.downLeft;
                    } else if (input.isKeyDown(Input.KEY_D)) {
                        direction = enumDirection.downRight;
                    } else {
                        direction = enumDirection.down;
                    }

                } else if (input.isKeyDown(Input.KEY_Z)) {
                    if (input.isKeyDown(Input.KEY_Q)) {
                        direction = enumDirection.topLeft;
                    } else if (input.isKeyDown(Input.KEY_D)) {
                        direction = enumDirection.topRight;
                    } else {
                        direction = enumDirection.top;
                    }

                } else if (input.isKeyDown(Input.KEY_Q)) {
                    direction = enumDirection.left;
                } else if (input.isKeyDown(Input.KEY_D)) {
                    direction = enumDirection.right;
                } else {
                    direction = enumDirection.noDirection;
                }
                //
            }
        });
        manageInputsThread.start();

        Thread sendMoveThread = new Thread( () ->
        {

            while (true)
            {
                try {
                    Thread.sleep(5);
                    n.send("[clientpos]" + getLocation().x + "|" + getLocation().y);
                    System.out.println(getLocation());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        sendMoveThread.start();
        /*
        Thread receiveMoveThread = new Thread( () ->
        {
            while (true)
            {
                try {
                    String answer = n.receive();
                    if (answer.contains("[serverpos]"))
                    {
                        String stringServerPosX =  answer.substring(answer.indexOf("]") + 1 , answer.indexOf("|"));
                        String stringServerPosY =  answer.substring(answer.indexOf("|") + 1 , answer.length() - 1);
                        float serverPosX = Float.parseFloat(stringServerPosX);
                        float serverPosY = Float.parseFloat(stringServerPosY);
                        Vector2f realPos = reconciliatePositionFromServer(serverPosX,serverPosY);
                        this.setLocation(realPos);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        receiveMoveThread.start();*/
    }

    private Vector2f reconciliatePositionFromServer(float newX, float newY)
    {
        // Calcul de la distance entre la dernière position et la nouvelle position
        float distance = (float)(Math.sqrt(Math.pow(newX - location.x, 2) + Math.pow(newY - location.y, 2)));

        // Estimation de la nouvelle position à partir de la dernière position et de la direction
        float estimatedX = location.x + speed * distance;
        float estimatedY = location.y + speed * distance;

        // Retourne la nouvelle position estimée
        return new Vector2f ( estimatedX, estimatedY );
    }



    public void update(GameContainer container, float delta)
    {
            int windowWidth = container.getWidth();
            int windowHeight = container.getHeight();
            int playerWidth = (int)getSize().x;
            int playerHeight = (int)getSize().y;
            int playerX = (windowWidth - playerWidth) / 2;
            int playerY = (windowHeight - playerHeight) / 2;
            setLocation(playerX,playerY);
            //
            float realSpeed = speed*(delta/1000.0f);
            switch(direction)
            {
                case down -> {
                    currentAnimation = playerWalkFront;
                    currentAnimation.setStopped(false);
                    //move(0,realSpeed);
                    for(int i =0; i < mapLayersRef.size() ; i++)
                        mapLayersRef.get(i).scroll(0,realSpeed);
                }
                case top -> {
                    currentAnimation = playerWalkTop;
                    currentAnimation.setStopped(false);
                    //move(0,-realSpeed);
                    for(int i =0; i < mapLayersRef.size() ; i++)
                        mapLayersRef.get(i).scroll(0,-realSpeed);
                }
                case left -> {
                    currentAnimation = playerWalkLeft;
                    currentAnimation.setStopped(false);
                    //move(-realSpeed,0);
                    for(int i =0; i < mapLayersRef.size() ; i++)
                        mapLayersRef.get(i).scroll(-realSpeed , 0);
                }
                case right -> {
                    currentAnimation = playerWalkRight;
                    currentAnimation.setStopped(false);
                    //move(realSpeed,0);
                    for(int i =0; i < mapLayersRef.size() ; i++)
                        mapLayersRef.get(i).scroll(realSpeed , 0);
                }
                case topLeft -> {
                    currentAnimation = playerWalkTop;
                    currentAnimation.setStopped(false);
                    //move(-realSpeed,-realSpeed);
                    for(int i =0; i < mapLayersRef.size() ; i++)
                        mapLayersRef.get(i).scroll(-realSpeed , -realSpeed);
                }
                case topRight -> {
                    currentAnimation = playerWalkTop;
                    currentAnimation.setStopped(false);
                    //move(realSpeed,-realSpeed);
                    for(int i =0; i < mapLayersRef.size() ; i++)
                        mapLayersRef.get(i).scroll(realSpeed , -realSpeed);
                }
                case downLeft -> {
                    currentAnimation = playerWalkFront;
                    currentAnimation.setStopped(false);
                    //move(-realSpeed,realSpeed);
                    for(int i =0; i < mapLayersRef.size() ; i++)
                        mapLayersRef.get(i).scroll(-realSpeed , realSpeed);
                }
                case downRight -> {
                    currentAnimation = playerWalkFront;
                    currentAnimation.setStopped(false);
                    //move(realSpeed,realSpeed);
                    for(int i =0; i < mapLayersRef.size() ; i++)
                        mapLayersRef.get(i).scroll(realSpeed , realSpeed);
                }
                case  noDirection -> {
                    currentAnimation.setStopped(true);
                }
            }
            currentAnimation.update();;



    }
    public void draw()
    {
        currentAnimation.draw();
    }

    public void setLocation(float x , float y)
    {
        location.x = x;
        location.y = y;
        playerWalkFront.setPosition(x,y);
        playerWalkTop.setPosition(x,y);
        playerWalkLeft.setPosition(x,y);
        playerWalkRight.setPosition(x,y);
    }
    public void setLocation(Vector2f v)
    {
        location = v;
        playerWalkFront.setPosition(v.x,v.y);
        playerWalkTop.setPosition(v.x,v.y);
        playerWalkLeft.setPosition(v.x,v.y);
        playerWalkRight.setPosition(v.x,v.y);
    }
    public void setSize(int x , int y)
    {
        playerWalkFront.setSize(x,y);
        playerWalkTop.setSize(x,y);
        playerWalkLeft.setSize(x,y);
        playerWalkRight.setSize(x,y);
    }

    public Vector2f getLocation()
    {
        Vector2f loc = new Vector2f(playerWalkFront.getLocation().x + mapLayersRef.get(0).getScroll().x , playerWalkFront.getLocation().y + mapLayersRef.get(0).getScroll().y );

        return loc;
    }

    public Vector2f getSize()
    {
        return playerWalkFront.getSize();
    }



    public void closeConnection() throws IOException {
        n.close();
    }

    public void move(float x , float y )
    {
        setLocation(location.x += x,location.y += y);


    }
}


