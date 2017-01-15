package testingPlayer;

import battlecode.common.*;

import static testingPlayer.RobotPlayer.randomDirection;
import static testingPlayer.RobotPlayer.tryMove;

/**
 * Created by Demetri on 1/15/2017.
 */
public class GardenerNucleus {

    private float NUM_CELL_TREES = 6; //Number of trees to surround the gardener in a tree cell,
    private float ANGLE_OFFSET = (float) ((Math.PI * 2)/ NUM_CELL_TREES);

    private RobotController rc;
    private boolean isLocationFound = false;
    private TreeInfo[] treeCellTrees = new TreeInfo[6];

    public GardenerNucleus(RobotController rc){
        this.rc = rc;
        runGardenerNucleus();
    }

    void runGardenerNucleus(){
        System.out.println("I'm a gardener!");

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {

                // Generate a random direction
                Direction dir = randomDirection();

                // Determine if current location can hold a tree cell
                if(isLocationFound) {
                    if(rc.senseNearbyTrees(2).length <=6) {
                        buildCell();
                    }
                } else{
                    // Move randomly
                    tryMove(randomDirection());
                    testLocation();

                }
                waterTreeCell();

            } catch (Exception e) {
                System.out.println("Gardener Exception");
                e.printStackTrace();
            }
            // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
            Clock.yield();
        }
    }

    private void testLocation() throws GameActionException {
        boolean isSuitable = false;
        int suitableLocation = 0;

        /* Currently, I think its better to test the 6 directions rather than a circle. Circles prevent
        tree cells from being built diagonally adjacent to other tree cells
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
            while (!didPlant) {
                Direction dir = new Direction(treeDirRad);
                if (rc.canPlantTree(dir)) {
                    rc.plantTree(dir);
                    treeCellTrees[treeNum] = rc.senseTreeAtLocation(rc.getLocation().add(dir, 2* GameConstants.BULLET_TREE_RADIUS));
                    treeNum++;
                    didPlant = true;
                }
                treeDirRad = treeDirRad + ANGLE_OFFSET;
            }
        }
    }

    private void waterTreeCell() throws GameActionException {
        System.out.println(rc.canWater());
        if(rc.canWater()){
            int minHealthTreeId = 0;
            int lowestHealth= Integer.MAX_VALUE;
            for(TreeInfo tree : treeCellTrees){
                    try {
                        float health = tree.getHealth();
                        if (tree.getHealth() < lowestHealth) {
                            minHealthTreeId = tree.getID();
                        }
                    }
                    catch(Exception e){

                    }
            }
            //If all trees are null or dead, water should not be called

            System.out.println("Watering: " + rc.senseTree(minHealthTreeId));
            if(minHealthTreeId != 0) {
                rc.water(minHealthTreeId);
                System.out.println("Did Water: " + rc.senseTree(minHealthTreeId));
            }
        }
    }

}
