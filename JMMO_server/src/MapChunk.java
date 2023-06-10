import org.newdawn.slick.geom.Vector2f;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MapChunk
{
    private final int idx;
    private final int idy;
    private final int[][] chunk = new int[20][20];
    public MapChunk(int idx, int idy)
    {

        this.idx = idx;
        this.idy = idy;
    }
    public void randomizeFloor()
    {
            for(int i=0;i<20;i++)
            {
                for(int j=0;j<20;j++)
                {
                    chunk[i][j] = Utils.randomRanged(0,63);
                }

            }
    }

    public String generateChunkDatas()
    {
        String chunkDatas ="";
        for(int i=0;i<20;i++)
        {
            for(int j=0;j<20;j++) {
                //System.out.println(randomNumber);
                chunkDatas += "(" + i + "-" + j + "," + chunk[i][j] + ")";
            }

        }
        return "okUpdateChunk" + "[" + idx + "," + idy + "]" + chunkDatas ;
    }

    public void saveInFile()
    {
        String chunkDatas = generateChunkDatas().replaceAll("okUpdateChunk" , "");
        FileReaderWriter.writeToFile("map/chunks/MapChunk[" + idx + "," + idy + "].chunk" , chunkDatas);
    }

    public void readFromFile(String filePath)
    {
        String chunkDatas = FileReaderWriter.readFromFile(filePath);

        String regex = "\\(((\\d+-\\d+),(\\d+))\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(chunkDatas);

        while (matcher.find()) {
            String group = matcher.group(1);
            String[] parts = group.split("-");

            int firstNumber = Integer.parseInt(parts[0]);
            int secondNumber = Integer.parseInt(parts[1].split(",")[0]);
            int thirdNumber = Integer.parseInt(parts[1].split(",")[1]);

            //System.out.println(group);
            chunk[firstNumber][secondNumber] = thirdNumber;
        }
    }


    public void makeLake() {
        for(int i=0;i<20;i++)
        {
            for(int j=0;j<20;j++)
            {
                chunk[i][j] = 1000;
            }

        }

    }
}
