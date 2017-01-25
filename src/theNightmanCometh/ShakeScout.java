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

//                    // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
//                    Clock.yield();
//
//                } else {
//
//                    Clock.yield();
//                }

                path(new MapLocation(601,601));

            } catch (Exception e) {
                System.out.println("Scout Exception!");
            }
        }
    }
}
