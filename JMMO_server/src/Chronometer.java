public class Chronometer
{
    long currentTime =0 , elapsedTime =0 , lastTime =0;

    public  Chronometer()
    {

    }
    public void init()
    {

        Thread chronoThread = new Thread( () ->
        {
            lastTime = System.currentTimeMillis();
            while(true)
            {
                currentTime = System.currentTimeMillis();
                elapsedTime += (currentTime - lastTime);
                lastTime = currentTime;

                System.out.println(elapsedTime);
            }

        });
        chronoThread.start();
    }

    public void reset()
    {
        elapsedTime = 0;
    }

    public int getElapsedTime()
    {
        return (int)elapsedTime;
    }
}
