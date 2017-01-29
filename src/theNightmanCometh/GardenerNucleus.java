package theNightmanCometh;

import battlecode.common.*;

import static theNightmanCometh.RobotPlayer.*;

/**
 * Created by Demetri on 1/15/2017.
 */
public class GardenerNucleus extends Pathable {

    private int NUM_CELL_TREES = 6; //Number of trees to surround the gardener in a tree cell,
    //the float value below is used instead of 2pi because that is the value used to transform the radians within the Direction constructor
    private float ANGLE_OFFSET = 6.2831855f/ NUM_CELL_TREES;

    private RobotController rc;
    private boolean isLocationFound = false;
    private int MISSION_NUMBER=Mission.GARDENER_NUCLEUS.missionNum;
    private int derpderp;
    private MapLocation anchor;
    int archonId = 0;

    public GardenerNucleus(RobotController rc){
        this.rc = rc;
        anchor = rc.getInitialArchonLocations(rc.getTeam())[0];
        runGardenerNucleus();
    }

    void runGardenerNucleus() {

        TreeInfo[] sensedTrees = null;
        System.out.println("I'm a gardener!");
        int numberLumber = 0;
        float centerX = 0;
        float centerY = 0;

        try {
            centerX = rc.readBroadcastFloat(9998);
            centerY = rc.readBroadcastFloat(9999);
        } catch (Exception e) {
            e.printStackTrace();
        }

        MapLocation center = new MapLocation(centerX, centerY);

        // Testing git commands
        // The code you want your robot to perform every round should be in this loop

        // Try/catch blocks stop unhandled exceptions, which cause your robot to explode

        //this should only happen once, and is therefore outside of the while loop
        try {
            getTransmissionID();
        } catch (GameActionException e) {
            e.printStackTrace();
        }
        if(rc.senseNearbyRobots()[0].getType() == RobotType.ARCHON) {
            archonId = rc.senseNearbyRobots()[0].getID();
        }

        while (mission == MISSION_NUMBER) {
            try {
//                anchor = rc.getInitialArchonLocations(rc.getTeam())[0].add(rc.getInitialArchonLocations(rc.getTeam())[0].directionTo(center), (float) 5.5);
                if(rc.canSenseRobot(archonId)){
                    RobotInfo archon = rc.senseRobot(archonId);
                    anchor = archon.getLocation().add(archon.getLocation().directionTo(center), 5.5f);
                }
                //Every turn, check to see if the mission is updated,
                //but the robot won't change behavior for a turn
                updateMission();
                // Try/catch blocks stop unhandled exceptions, which cause your robot to explode

                if (rc.senseNearbyTrees(rc.getType().sensorRadius, Team.NEUTRAL).length > numberLumber) {
                    Direction dir = rc.getLocation().directionTo(rc.senseNearbyRobots()[0].getLocation()).opposite();
                    if(rc.canBuildRobot(RobotType.LUMBERJACK, dir)){
                        rc.canBuildRobot(RobotType.LUMBERJACK, dir);
                        numberLumber++;
                    }else{
                        for(int i = 0; i < 10; i++ ) {
                            Direction dir2 = randomDirection();
                            if (rc.canBuildRobot(RobotType.LUMBERJACK, dir2)) {
                                rc.buildRobot(RobotType.LUMBERJACK, dir2);
                                numberLumber++;
                            }
                        }
                    }
                }

                // Determine if current location can hold a tree cell
                if (isLocationFound) {
                    sensedTrees = rc.senseNearbyTrees((float) 1.1 * GameConstants.BULLET_TREE_RADIUS);
                    if (sensedTrees.length < 6) {
                        buildCell();
                    }

                } else {
                    path(anchor);
                    testLocation();
                }

                if (sensedTrees != null) {
                    waterTreeCell(sensedTrees);
                }

                //donate method goes at the end of each robot's turn
                trollToll();

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

        if(suitableLocation== NUM_CELL_TREES && rc.getLocation().distanceTo(rc.getInitialArchonLocations(rc.getTeam())[0]) > 5){
            isLocationFound = true;
            buildCell();

        }

        if(rc.senseNearbyRobots(3.5f,rc.getTeam())[0].getType() == rc.getType()){
            derpderp = derpderp  + 1;
            if (derpderp == 33){
                derpderp = 0;
                isLocationFound = true;

                buildCell();

            }
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
