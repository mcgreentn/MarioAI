package competition.cig.mechanicextractor.extractor;

import java.util.List;

import ch.idsia.mario.engine.level.Level;
import competition.cig.mechanicextractor.astar.*;
import competition.cig.mechanicextractor.astar.sprites.*;
import java.util.ArrayList;
public class GraphBuilder {

    public static List<Node> nodes;
    public static List<Node> actions;
    public static List<Node> entities;
    public static List<Node> conditions;


    public static boolean verbose = true;
    public static int globalIDCounter = 0;
    public static void init() {
        nodes = new ArrayList<Node>();
        entities = new ArrayList<Node>();
        actions = new ArrayList<Node>();
        conditions = new ArrayList<Node>();
    }

    public static void addEntity(Node n){
        entities.add(n);
    }

    public static boolean containsEntity(Node toTest) {
        for (Node item: entities) {
            if(((Entity)item).getType() == ((Entity)toTest).getType()) {
                return true;
            }
        }
        return false;
    }

    public static void compareStates(LevelScene parent, LevelScene child) {
        // get lists of the parent and child sprites
        List<Sprite> parentSprites = parent.getSprites();
        List<Sprite> childSprites = child.getSprites();

        for (Sprite citem : childSprites) {
            boolean contains = false;
            checkCollision(parent, child, citem);
//            for (Sprite citem : childSprites) {
//                if(pitem.id == citem.id) {
//                    // child level contains the same sprite, no change
//                    contains = true;
//                    break;
//                }
//            }
            // child level does not contain the same sprite, there was a change
//            if(!contains) {
//                // figure out if there was a collision, we are simplifying the game here to only look at collision based mechanics
////                System.out.println("Sprite missing between observations...Checking...");
////                missingSprite(parent, child, pitem);
//            }
        }
//        System.out.println("Comparing sprites between observations...");

    }
    public static void checkCollision(LevelScene parent, LevelScene child, Sprite pitem) {
        if(pitem.collideCheck2()) {
            String returnMe = pitem.kind + " collided with Mario";
            System.out.println(returnMe);

            if(parent.mario.damage < child.mario.damage) {
                // then we can safely say that this collision hurt mario. Mark that this is bad
                if(verbose)
                    System.out.println("Mario took damage.");
                // refer to how this occured. Lets get information about this collision and put it into the graph
                // height differences / positional differences between mario and the enemy at this moment in time
                // make a conditional node with this information
                // the action node should contain that mario is damaged
                float mariox = child.mario.x;
                float marioy = child.mario.y;

                float enemyx = pitem.x;
                float enemyy = pitem.y;

                float changex = enemyx - mariox;
                float changey = enemyy - marioy;
                if(verbose)
                    System.out.println(changex + ", " + changey);

                if(changey > -1 && changey < 1) {
                    // this is a normal collision (not landing on top) and mario got hurt
                    if(verbose)
                        System.out.println("Mario ran into this on the ground.");
                    Node condition = addNewCondition("Collision");
                    conditions.add(condition);

                    Node action = addNewAction("Damages");

                    condition.addOutput(action);
                    action.addInput(condition);


                } else if (changey > 1) {
                    // mario jumped/fell on top of this enemy and got hurt
                    if(verbose)
                        System.out.println("Mario landed on top of this.");
                }
            }
            // struggling to rectify this, somehow mario is getting powered when he doest collide with a mushroom or a fire flower. Are these represented in the forward model?
            // TODO add this to forward model / check for bugs
//            else if(parent.mario.large != child.mario.large || parent.mario.fire != child.mario.fire) {
//                // its an upgrade, mario is doing better now
//                System.out.println("Mario was upgraded");
//            }
            // coins are not currently represented in the forward model.
            // TODO add this to forward model
            else if(parent.coinsCollected < child.coinsCollected) {
                if(verbose)
                    System.out.println("Mario collected a coin");

            }
            else if(parent.enemiesKilled < child.enemiesKilled) {
                if(verbose)
                    System.out.println("Mario killed an enemy");
            }
            else if(parent.enemiesJumpedOn < child.enemiesJumpedOn) {
                if(verbose)
                    System.out.println("Mario jumped on an enemy");
            }

        }




    }

    public static Node addNewCondition(String name) {
        Node node = new Condition();
        node.setName(name);
        ((Condition) node).setType("Condition");

        return node;
    }

    public static Node addNewAction(String name) {
        Node node = new Action();
        node.setName(name);
        ((Action)node).setType("Action");

        return node;
    }

    public static Node searchEntities(byte type) {
        for (Node e : entities)
        {
            if(((Entity) e).getType() == type) {
                return e;
            }

        }
        return null;
    }
//    public static Node searchEntities(String )
//    public static void missingSprite(LevelScene parent, LevelScene child, Sprite pitem) {
//        // do a search on the stuff around pitem
//        for(Sprite others : parent.getSprites()) {
//            if(!others.equals(pitem)) {
//                // compare distance. If it falls within the hitbox, then perhaps a collision caused the sprite to disappear
//                System.out.println(pitem.kind + " collided with Mario: " + pitem.collideCheck2());
//            }
//        }
//    }

    
}
