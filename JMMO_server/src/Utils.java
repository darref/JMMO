import java.util.Random;

public class Utils
{
    public static int randomRanged(int min , int max)
    {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }
    public static boolean randomBool()
    {
        return randomRanged(0,1) == 1;
    }
}
