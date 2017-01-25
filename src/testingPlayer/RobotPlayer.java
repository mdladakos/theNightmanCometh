package testingPlayer;
import battlecode.common.*;

import static testingPlayer.Mission.*;
import java.lang.Math;

public strictfp class RobotPlayer {
    static RobotController rc;
    static int transmissionId;
    static int mission = DEFAULT_MISSION.missionNum;
//
//
//    static List<Integer> TREELIST = new ArrayList<>();
//    static boolean hasbuilt = false;
//    static int GARDENERS_TO_HIRE = 10;
//    static int gardenersHired= 0;

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        // This is the RobotController object. You use it to perform actions from this robot,
        // and to get information on its current status.
        RobotPlayer.rc = rc;

        // This is a while because we make a new robot for each role. When a mission changes,
        //the while loop within that class breaks and we will return to this loop.
        while (true) {

            Mission newMission = getMissionByValue(mission);
            //default Roles for each Type
            if (newMission == DEFAULT_MISSION) {
                switch (rc.getType()) {
                    case ARCHON:
                        mission = ANCHOR_ARCHON.missionNum;
                        new AnchorArchon(rc);
                        break;
                    case SCOUT:

                        break;
                    case GARDENER:
                        mission = GARDENER_NUCLEUS.missionNum;
                        new GardenerNucleus(rc);
                        break;
                    case SOLDIER:

                        break;
                    case LUMBERJACK:
                        new Lumberjack(rc);
                        break;
                }
            } else {
                //Otherwise, create the robot of that mission
                switch (newMission) {
                    case ANCHOR_ARCHON:
                        new AnchorArchon(rc);
                        break;
                    case GARDENER_NUCLEUS:
                        new GardenerNucleus(rc);
                }
            }
        }
    }


//    static void runScout() throws GameActionException {
//
//        System.out.println("I'm a scout!");
//        while(true) {
//            try {
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
//                        System.out.println("Tree " + nextTree.getID() + " Bullets: " + nextTree.getContainedBullets());
//                        System.out.println("Tree " + nextTree.getID() + " Robot: " + nextTree.getContainedRobot());
//                        rc.shake(nextTree.getLocation());
//                        TREELIST.add(nextTree.getID());
//
//                        rc.donate(100);
//                    }
//
//                    // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
//                    Clock.yield();
//
//                } else {
//                    tryMove(randomDirection());
//                    Clock.yield();
//                }
//            } catch (Exception e) {
//                System.out.println("Scout Exception!");
//            }
//        }
//    }

    /**
     * Returns a random Direction
     *
     * @return a random Direction
     */
    static Direction randomDirection() {
        return new Direction((float) Math.random() * 2 * (float) Math.PI);
    }

    /**
     * Attempts to move in a given direction, while avoiding small obstacles directly in the path.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMove(Direction dir) throws GameActionException {
        return tryMove(dir, 20, 3);
    }

    /**
     * Attempts to move in a given direction, while avoiding small obstacles direction in the path.
     *
     * @param dir The intended direction of movement
     * @param degreeOffset Spacing between checked directions (degrees)
     * @param checksPerSide Number of extra directions checked on each side, if intended direction was unavailable
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMove(Direction dir, float degreeOffset, int checksPerSide) throws GameActionException {

        // First, try intended direction
        if (rc.canMove(dir)) {
            rc.move(dir);
            return true;
        }

        // Now try a bunch of similar angles
        boolean moved = false;
        int currentCheck = 1;

        while(currentCheck<=checksPerSide) {
            // Try the offset of the left side
            if(rc.canMove(dir.rotateLeftDegrees(degreeOffset*currentCheck))) {
                rc.move(dir.rotateLeftDegrees(degreeOffset*currentCheck));
                return true;
            }
            // Try the offset on the right side
            if(rc.canMove(dir.rotateRightDegrees(degreeOffset*currentCheck))) {
                rc.move(dir.rotateRightDegrees(degreeOffset*currentCheck));
                return true;
            }
            // No move performed, try slightly further
            currentCheck++;
        }

        // A move never happened, so return false.
        return false;
    }

    /**
     * A slightly more complicated example function, this returns true if the given bullet is on a collision
     * course with the current robot. Doesn't take into account objects between the bullet and this robot.
     *
     * @param bullet The bullet in question
     * @return True if the line of the bullet's path intersects with this robot's current position.
     */
    static boolean willCollideWithMe(BulletInfo bullet) {
        MapLocation myLocation = rc.getLocation();

        // Get relevant bullet information
        Direction propagationDirection = bullet.dir;
        MapLocation bulletLocation = bullet.location;

        // Calculate bullet relations to this robot
        Direction directionToRobot = bulletLocation.directionTo(myLocation);
        float distToRobot = bulletLocation.distanceTo(myLocation);
        float theta = propagationDirection.radiansBetween(directionToRobot);

        // If theta > 90 degrees, then the bullet is traveling away from us and we can break early
        if (Math.abs(theta) > Math.PI/2) {
            return false;
        }

        // distToRobot is our hypotenuse, theta is our angle, and we want to know this length of the opposite leg.
        // This is the distance of a line that goes from myLocation and intersects perpendicularly with propagationDirection.
        // This corresponds to the smallest radius circle centered at our location that would intersect with the
        // line that is the path of the bullet.
        float perpendicularDist = (float)Math.abs(distToRobot * Math.sin(theta)); // soh cah toa :)

        return (perpendicularDist <= rc.getType().bodyRadius);
    }

    /**
     * This should only happen once, and therefore should exist outside of a robot's while loop.
     *
     * @return the channel of the broadcast to listen to
     * @throws GameActionException
     */
    static public void getTransmissionID() throws GameActionException {

        int i = 0;
        while (rc.readBroadcast(i) != 0) {
            i = i + 1;
        }
        transmissionId = i;
    }

    /**
     * Checks a broadcast channel and returns the mission in that spot
     *
     * @return the mission to perform
     * @throws GameActionException
     */
    static public void updateMission() throws GameActionException {
        transmissionId = rc.readBroadcast(transmissionId);
    }
}
