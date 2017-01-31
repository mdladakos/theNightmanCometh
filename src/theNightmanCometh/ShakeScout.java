package theNightmanCometh;

import battlecode.common.*;

import java.util.ArrayList;
import java.util.List;

import static theNightmanCometh.RobotPlayer.tryMove;


/**
 * Created by Demetri on 1/25/2017.
 */
public class ShakeScout extends Pathable{

    private RobotController rc;
    private MapLocation pathingLoc;
    List<Integer> TREELIST = new ArrayList<>();

    public ShakeScout(RobotController rc){
        this.rc = rc;
        runScout();
    }

     void runScout(){

        System.out.println("I'm a scout!");
        while(true) {
            try {
//                rc.broadcast(1, 1);
//
//
//                TreeInfo[] trees = rc.senseNearbyTrees();
//                TreeInfo nextTree;
//                float nextTreeDist = 9999999;
//
//                if (trees.length != 0) {
//                    nextTree = trees[0];
//                    for (TreeInfo tree : trees) {
//
//                        if(!TREELIST.contains(tree.getID())) {
//
//                            if (rc.getLocation().distanceTo(tree.getLocation()) < nextTreeDist) {
//                                nextTree = tree;
//                            }
//                        }
//                    }
//                    try {
//                        if(!TREELIST.contains(nextTree.getID())) {
//                            rc.move(nextTree.getLocation());
//                        }else{
//                            tryMove(randomDirection());
//                        }
//                    }catch(GameActionException e){
//                        TREELIST.add(nextTree.getID());
//                    }
//                    if(rc.canShake(nextTree.getLocation())){
//                        rc.shake(nextTree.getLocation());
//                        TREELIST.add(nextTree.getID());

//                        rc.donate(100);
//                    }

//                    // Clock.yield() makes the robot wait until the next spin, then it will perform this loop again
//                    Clock.yield();
//
//                } else {
//
//                    Clock.yield();
//                }

                RobotInfo[] enemies = rc.senseNearbyRobots(rc.getType().sensorRadius, rc.getTeam().opponent());
                MapLocation[] enemyLocs = new MapLocation[enemies.length];

                if(enemies.length > 0){

                    int i = 0;
                    for(RobotInfo enemy: enemies){
                        enemyLocs[i] = enemy.getLocation();
                    }
                    MapLocation avgEnemyLoc = getAverageLocation(enemyLocs);
                    pathingLoc = rc.getLocation().subtract(rc.getLocation().directionTo(avgEnemyLoc));
                    path(pathingLoc);
                }

                Clock.yield();

            } catch (Exception e) {
                System.out.println("Scout Exception!");
                Clock.yield();
            }
        }
    }

    private MapLocation getAverageLocation(MapLocation[] locations) {

        //initialize
        float x = 0;
        float y = 0;

        //for each location, sum the x and y values
        for(MapLocation location : locations){
            x +=location.x;
            y +=location.y;
        }

        //average the values by the number of locations
        x = x/locations.length;
        y = y/locations.length;

        //return the average location
        return new MapLocation(x,y);
    }
}
