import org.newdawn.slick.geom.Vector2f;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class ServerPlayer
{
    private final Server serverRef;

    Semaphore semaphore = new Semaphore(1);
    private final NetworkConnection nc;
    private final float speed = 1000;
    public int locationX ;
    public int locationY ;




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
                if(receivedMessage.contains("[clientpos]")) {
                    receivedMessage.replace("[clientpos]" , "");
                    String regex = "(-?\\d*\\.\\d*)\\|(-?\\d*\\.\\d*)";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(receivedMessage);
                    //System.out.println(receivedMessage);
                    if (matcher.find()) {
                        String xStr = matcher.group(1);
                        String yStr = matcher.group(2);
                        float x = Float.parseFloat(xStr);
                        float y = Float.parseFloat(yStr);
                        System.out.println("x et y valent: " + x + " " + y);

                        setLocation((int)x,(int)y);
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

    public int getLocationX() {

        if (semaphore.tryAcquire()) {
            try {
                return locationX;
            } finally {
                semaphore.release();
            }
        }
        return -1;
    }
    public int getLocationY() {

        if (semaphore.tryAcquire()) {
            try {
                return locationY;
            } finally {
                semaphore.release();
            }
        }
        return -1;
    }

    public void setLocation(int x, int y) {
        if (semaphore.tryAcquire()) {
            try {
                locationX = x;
                locationY = y;
                System.out.println("-----------setlocation--------" + x + " " + y);
            } finally {
                semaphore.release();
            }
        }
    }
}



