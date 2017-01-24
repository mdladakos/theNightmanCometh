package testingPlayer;

import battlecode.common.*;

public class Lumberjack extends Pathable{

    private RobotController rc;

    public Lumberjack(RobotController rc){
        this.rc = rc;
        runLumberjack();
    }

    public void runLumberjack(){

        MapLocation target;

        while(true){


            try {
                if (rc.senseNearbyTrees().length > 0) {
                    target = rc.senseNearbyTrees(rc.getType().sensorRadius,Team.NEUTRAL)[0].getLocation();
                }
                else {
                    target = rc.getInitialArchonLocations(rc.getTeam().opponent())[0];
                }
                if (rc.canStrike() && rc.senseNearbyRobots(3,rc.getTeam().opponent()).length > 0){
                    rc.strike();
                }
                else if(rc.canChop(target)) {
                    rc.chop(target);
                }
                else {
                    path(target);
                }

                Clock.yield();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
