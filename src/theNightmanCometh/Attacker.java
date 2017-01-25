package theNightmanCometh;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

/**
 * Created by Demetri on 1/25/2017.
 */
public class Attacker extends Pathable{

    private RobotController rc;
    private MapLocation targetLoc;
    private RobotInfo target;

    public Attacker(RobotController rc){
        this.rc = rc;
        runAttacker();
    }

    public void runAttacker(){

        targetLoc = rc.getInitialArchonLocations(rc.getTeam().opponent())[0];
        while(true){
            try {


                RobotInfo[] enemies = rc.senseNearbyRobots(rc.getType().sensorRadius, rc.getTeam().opponent());
                if(enemies!=null) {
                    for (RobotInfo enemy : enemies) {
                        if (enemy.getType() == RobotType.ARCHON) {
                            target = enemy;
                            targetLoc = enemy.getLocation();
                        } else if(enemy.getType() != RobotType.ARCHON){
                            if(rc.getLocation().distanceTo(enemy.getLocation())<rc.getLocation().distanceTo(targetLoc)){
                                target = enemy;
                                targetLoc = enemy.getLocation();
                            }
                        }
                    }
                }

                path(targetLoc);

                if(rc.canSenseRobot(target.getID()) && rc.canFireSingleShot()){
                    rc.fireSingleShot(rc.getLocation().directionTo(targetLoc));
                }

            }catch(Exception e){
                e.printStackTrace();
            }


        }



    }
}
