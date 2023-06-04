import org.newdawn.slick.*;
import org.newdawn.slick.Graphics;

import java.io.IOException;
import java.util.Vector;


public class Main extends BasicGame
{
    private Player player;
    private Vector<MapLocal> mapLayers = new Vector<MapLocal>();

    public Main(String title) throws SlickException, IOException {
        super(title);
    }

    public void gameOver() throws IOException {
        player.closeConnection();
    }

    @Override
    public void init(GameContainer container) throws SlickException {


        try {
            mapLayers.add(new MapLocal(System.getProperty("user.dir").replace("\\" , "/") + "/ressources/tilesets/ground1.png"));
            //mapLayers.add(new MapLocal("C:/Users/jorda/Desktop/JMMO/ressources/tilesets/Trees.png"));

        } catch (IOException e) {
            System.out.println("mapes nn créées");
            throw new RuntimeException(e);
        }
        for(int i=0 ; i < mapLayers.size() ; i++)
            mapLayers.get(i).init();
        // Initialisation du jeu

        try {
            player = new Player(mapLayers);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        player.setSize(130,130);
        player.init(container);
        //
        for(int i=0 ; i < mapLayers.size() ; i++)
            mapLayers.get(i).playerRef = player;

    }

    @Override
    public void update(GameContainer container, int delta) throws SlickException {
        // Mise à jour du jeu
        player.update(container,delta);
        for(MapLocal map : mapLayers)
            map.update();

    }

    @Override
    public void render(GameContainer container, Graphics g) throws SlickException {
        // Dessin du jeu
        for(int i=0 ; i < mapLayers.size() ; i++)
            mapLayers.get(i).draw();
        player.draw();

    }

    public static void main(String[] args)
    {
        try {
            Main game = new Main("JMMO");
            AppGameContainer app = new AppGameContainer(game);
            int screenWidth = app.getScreenWidth();
            int screenHeight = app.getScreenHeight();
            app.setDisplayMode((int)(screenWidth*0.8f), (int)(screenHeight*0.8f), false);
            app.start();

        } catch (SlickException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
