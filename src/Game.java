import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luke on 23/10/2017.
 */
public class Game {
    public Player player;
    public TileMap map;
    public List<GameObject> objects;
    public static List<List<Character>> gameMatrix;
    public static Camera camera;
    public Keys ctrl;

    public Game() {
        objects = new ArrayList<>();
        ctrl = new Keys();
        player = new Player(Player.TILES.get(0),9,22,ctrl);
        objects.add(player);
    }

    public static void main(String[] args) throws Exception {
        Game game = new Game();
        TileMap test = new TileMap(game,"map","school");
        game.map = test;
        gameMatrix = new ArrayList<>(game.map.matrix.size());
        DoorTile.initialisePoints();

        new JEasyFrame(test,"test").addKeyListener(game.ctrl);

        while (true) {
            game.update();
            test.repaint();
            Thread.sleep(20);
        }

    }

    public void update() {
        gameMatrix.clear();
        gameMatrix = new ArrayList<>(map.matrix.size());
        for (int i=0; i<map.matrix.size(); i++) {
            List<Character> line = map.matrix.get(i);
            gameMatrix.add(new ArrayList<>(line.size()));
            for (Character aLine : line) {
                gameMatrix.get(i).add(aLine);
            }
        }
        camera = new Camera(this.player.x-(TileMap.FRAME_WIDTH/64),this.player.y-(TileMap.FRAME_HEIGHT/64), gameMatrix);


        for (GameObject object : objects) {
            object.update();
            gameMatrix.get(object.y).set(object.x,object.tile.key);
        }

    }
}
