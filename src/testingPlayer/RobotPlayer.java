package testingPlayer;
import battlecode.common.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.lang.Math;

public strictfp class RobotPlayer {
    static RobotController rc;
    static List<Integer> TREELIST = new ArrayList<>();
    static boolean hasbuilt = false;
    static int GARDENERS_TO_HIRE = 10;
    static int gardenersHired= 0;

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
    **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        // This is the RobotController object. You use it to perform actions from this robot,
        // and to get information on its current status.
        RobotPlayer.rc = rc;

        // Here, we've separated the controls into a different method for each RobotType.
        // You can add the missing ones or rewrite this into your own control structure.
        switch (rc.getType()) {
            case ARCHON:
                new AnchorArchon(rc);
                break;
            case SCOUT:
                runScout();
                break;
            case GARDENER:
                new GardenerNucleus(rc);
                break;
            case SOLDIER:
                runSoldier();
                break;
            case LUMBERJACK:
                new Lumberjack(rc);
                break;
        }
	}

    static void runArchon() throws GameActionException {
        System.out.println("I'm an archon!");

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {

                // Generate a random direction
                Direction dir = randomDirection();

                // Randomly attempt to build a gardener in this direction
                if (rc.canHireGardener(dir) && gardenersHired < GARDENERS_TO_HIRE) {
                    rc.hireGardener(dir);
                    gardenersHired++;
                }

                // Move randomly
                MapLocation endDest = rc.getLocation().add(0, 5);


                // Broadcast archon's location for other robots on the team to know
//                MapLocation myLocation = rc.getLocation();
//                rc.broadcast(0,(int)myLocation.x);
//                rc.broadcast(1,(int)myLocation.y);

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Archon Exception");
            }
        }
    }

	static void runGardener() throws GameActionException {
        System.out.println("I'm a gardener!");

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {

                // Listen for home archon's location
//                int xPos = rc.readBroadcast(0);
//                int yPos = rc.readBroadcast(1);
//                MapLocation archonLoc = new MapLocation(xPos,yPos);

                // Generate a random direction
                Direction dir = randomDirection();

                // Randomly attempt to build a soldier or lumberjack in this direction
                if(rc.readBroadcast(1) != 1) {
                    if (rc.canBuildRobot(RobotType.SCOUT, dir)) {
                        rc.buildRobot(RobotType.SCOUT, dir);
                        hasbuilt = true;
                    }
                }

                if(rc.getTeamBullets() > 100 ){
                    rc.buildRobot(RobotType.SOLDIER, dir);
                }
                // Move randomly
                tryMove(randomDirection());

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Gardener Exception");
                e.printStackTrace();
            }
        }
    }

    static void runScout() throws GameActionException {

        System.out.println("I'm a scout!");
        while(true) {
            try {
                rc.broadcast(1, 1);


                TreeInfo[] trees = rc.senseNearbyTrees();
                TreeInfo nextTree;
                float nextTreeDist = 9999999;

                if (trees.length != 0) {
                    nextTree = trees[0];
                    for (TreeInfo tree : trees) {

                        if(!TREELIST.contains(tree.getID())) {

                            if (rc.getLocation().distanceTo(tree.getLocation()) < nextTreeDist) {
                                nextTree = tree;
                            }
                        }
                    }
                    try {
                        if(!TREELIST.contains(nextTree.getID())) {
                            rc.move(nextTree.getLocation());
                        }else{
                            tryMove(randomDirection());
                        }
                    }catch(GameActionException e){
                        TREELIST.add(nextTree.getID());
                    }
                    if(rc.canShake(nextTree.getLocation())){
                        System.out.println("Tree " + nextTree.getID() + " Bullets: " + nextTree.getContainedBullets());
                        System.out.println("Tree " + nextTree.getID() + " Robot: " + nextTree.getContainedRobot());
                        rc.shake(nextTree.getLocation());
                        TREELIST.add(nextTree.getID());

                        rc.donate(100);
                    }

                    // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                    Clock.yield();

                } else {
                    tryMove(randomDirection());
                    Clock.yield();
                }
            } catch (Exception e) {
                System.out.println("Scout Exception!");
            }
        }
    }

    static void runSoldier() throws GameActionException {
        System.out.println("I'm an soldier!");
        Team enemy = rc.getTeam().opponent();

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
                MapLocation myLocation = rc.getLocation();

                // See if there are any nearby enemy robots
                RobotInfo[] robots = rc.senseNearbyRobots(-1, enemy);

                // If there are some...
                if (robots.length > 0) {
                    // And we have enough bullets, and haven't attacked yet this turn...
                    if (rc.canFirePentadShot()) {
                        // ...Then fire a bullet in the direction of the enemy

                        rc.firePentadShot(new Direction( rc.getLocation(), rc.getInitialArchonLocations(rc.getTeam().opponent())[0]));
                    }
                }

                // Move randomly
                tryMove(randomDirection());

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Soldier Exception");
                e.printStackTrace();
            }
        }
    }

    static void runLumberjack() throws GameActionException {
        System.out.println("I'm a lumberjack!");
        Team enemy = rc.getTeam().opponent();

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
                runLumberjack();



                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Lumberjack Exception");
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns a random Direction
     * @return a random Direction
     */
    static Direction randomDirection() {
        return new Direction((float)Math.random() * 2 * (float)Math.PI);
    }

    /**
     * Attempts to move in a given direction, while avoiding small obstacles directly in the path.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMove(Direction dir) throws GameActionException {
        return tryMove(dir,20,3);
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
}
