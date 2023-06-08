import org.newdawn.slick.geom.Vector2f;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class ServerPlayer
{
    private final Server serverRef;

    Semaphore semaphore = new Semaphore(1);
    private final NetworkConnection nc;
    private final float speed = 1000;
    public  AtomicReference<Vector2f> position = new AtomicReference<Vector2f>(new Vector2f(555.0f,555.0f));

    public ServerMap mapRef;




    public ServerPlayer(Socket s , Server server) throws IOException
    {
        this.nc = new NetworkConnection(s);
        this.serverRef = server;

    }

    public   void init()
    {


        Thread moveThread = new Thread( () ->
        {
            while(true)
            {

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                String receivedMessage = "";
                try {
                    receivedMessage = nc.receive();
                } catch (IOException e) {
                    System.out.println("ERROR RECEIVING MESSAGE");
                    throw new RuntimeException(e);

                }
                System.out.println(receivedMessage);
                if(receivedMessage.contains("[clientpos]"))
                {
                    receivedMessage = receivedMessage.replace("[clientpos]" , "");
                    String regex = "(-?\\d*\\.\\d*)\\|(-?\\d*\\.\\d*)";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(receivedMessage);
                    System.out.println(receivedMessage);
                    if (matcher.find())
                    {
                        String xStr = matcher.group(1);
                        String yStr = matcher.group(2);
                        float x = Float.parseFloat(xStr);
                        float y = Float.parseFloat(yStr);
                        System.out.println("x et y valent: " + x + " " + y);

                        synchronized (position) {
                            position.set(new Vector2f(x,y));
                            //position.x = x;
                            //position.y = y;
                        }
                    }
                        //System.out.println("Nouvelle position player envoyée par le client: " + (int)getLocation().x + " | " + (int)getLocation().y);
                }
            }


        });
        moveThread.start();


    }


    public void closeConnection() throws IOException {
        nc.close();
    }

    public void setPosition(Vector2f newPosition) {
        synchronized (position) {
            position.set(newPosition);
        }
    }

    public Vector2f getPosition() {
        synchronized (position) {
            return position.get();
        }
    }
}



