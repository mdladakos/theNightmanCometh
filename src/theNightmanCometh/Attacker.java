package theNightmanCometh;

import battlecode.common.*;

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
                }else{
                    targetLoc = getAverageLocation(rc.getInitialArchonLocations(rc.getTeam().opponent()));
                }

                rc.setIndicatorLine(rc.getLocation(), targetLoc, 0,0,100);
                path(targetLoc);

                if(target != null) {
                    if (rc.canSenseRobot(target.getID()) && rc.canFireSingleShot()) {
                        rc.fireSingleShot(rc.getLocation().directionTo(targetLoc));
                    }
                }
                Clock.yield();

            }catch(Exception e){
                e.printStackTrace();
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
