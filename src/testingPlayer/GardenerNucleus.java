package testingPlayer;

import battlecode.common.*;

import static testingPlayer.RobotPlayer.randomDirection;
import static testingPlayer.RobotPlayer.tryMove;

/**
 * Created by Demetri on 1/15/2017.
 */
public class GardenerNucleus {

    private int NUM_CELL_TREES = 6; //Number of trees to surround the gardener in a tree cell,
    //the float value below is used instead of 2pi because that is the value used to transform the radians within the Direction constructor
    private float ANGLE_OFFSET = 6.2831855f/ NUM_CELL_TREES;

    private RobotController rc;
    private boolean isLocationFound = false;

    public GardenerNucleus(RobotController rc){
        this.rc = rc;
        runGardenerNucleus();
    }

    void runGardenerNucleus(){

        TreeInfo[] sensedTrees = null;
        System.out.println("I'm a gardener!");
        // Testing git commands
        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
                if(rc.senseNearbyTrees()!= null && rc.canBuildRobot(RobotType.LUMBERJACK, randomDirection()))
                    rc.buildRobot(RobotType.LUMBERJACK,randomDirection());


                // Determine if current location can hold a tree cell
                if(isLocationFound) {
                    sensedTrees = rc.senseNearbyTrees((float)1.1*GameConstants.BULLET_TREE_RADIUS);
                    if(sensedTrees.length<6) {
                        buildCell();
                    }

                } else{
                    // Move randomly
                    tryMove(randomDirection());
                    testLocation();
                }

                if(sensedTrees != null) {
                    waterTreeCell(sensedTrees);
                }
            } catch (Exception e) {
                System.out.println("Gardener Exception");
                e.printStackTrace();
            }
            // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
            Clock.yield();
        }
    }

    private void testLocation() throws GameActionException {
        int suitableLocation = 0;

        /* Currently, I think its better to test the 6 directions rather than a circle. Circles prevent
        tree cells from being built diagonally adjacent to other tree cells

        TODO: Break after getting a false
         */
        for(int i = 0; i<NUM_CELL_TREES; i++){
            if(rc.canPlantTree(new Direction(i * ANGLE_OFFSET))){
                suitableLocation++;
            }
        }

        if(suitableLocation== NUM_CELL_TREES){
            isLocationFound = true;
            buildCell();
        }
    }

    private void buildCell() throws GameActionException {

        boolean didPlant = false;
        int treeNum = 0;
        float treeDirRad = 0;

        if(rc.getTeamBullets() >= GameConstants.BULLET_TREE_COST) {

            while (!didPlant && rc.isBuildReady() && treeNum < NUM_CELL_TREES) {
                Direction dir = new Direction(treeDirRad);
                if (rc.canPlantTree(dir)) {
                    rc.plantTree(dir);
                    didPlant = true;
                }
                treeNum = (treeNum+1);
                treeDirRad = treeNum*ANGLE_OFFSET;
            }
        }
    }

    private void waterTreeCell(TreeInfo[] sensedTrees) throws GameActionException {
        if(rc.canWater()){
            int minHealthTreeId = 0;
            float lowestHealth= 51;
            for(TreeInfo tree : sensedTrees){
//                System.out.println("waterTreeCell: Tree #"+tree.getID()+"  Health: "+tree.getHealth());
                if(tree != null) {
                    try {
                        if (tree.getHealth() < lowestHealth) {
                            lowestHealth = tree.getHealth();
                            minHealthTreeId = tree.getID();
                        }
                    } catch (Exception e) {
                        System.out.println("waterTreeCell exception");
                        e.printStackTrace();
                    }
                }
            }

//            System.out.println("Watering: " + minHealthTreeId);
            if(minHealthTreeId != 0) {
                rc.water(minHealthTreeId);
            }
        }
    }

}
