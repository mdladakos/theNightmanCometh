package theNightmanCometh;

import battlecode.common.*;

import static theNightmanCometh.RobotPlayer.trollToll;

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
                if (rc.senseNearbyTrees(rc.getType().sensorRadius,Team.NEUTRAL).length > 0) {
                    target = rc.senseNearbyTrees(rc.getType().sensorRadius,Team.NEUTRAL)[0].getLocation();
                }
                else {
                    target = rc.getInitialArchonLocations(rc.getTeam().opponent())[0];
                }

                if(rc.senseNearbyRobots(rc.getType().sensorRadius,rc.getTeam().opponent()).length > 0) {
                    if (rc.canStrike() && rc.senseNearbyRobots(GameConstants.LUMBERJACK_STRIKE_RADIUS, rc.getTeam().opponent()).length > 0) {
                        rc.strike();
                        target = rc.senseNearbyRobots(rc.getType().sensorRadius, rc.getTeam().opponent())[0].getLocation();
                    }
                }

                if(rc.canChop(target)) {
                    if(rc.canShake(target)) {
                        rc.shake(target);
                    }
                    rc.chop(target);
                }
                else {
                    path(target);
                }

                //donate method at the end of each spin
                trollToll();
                Clock.yield();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
