package testingPlayer;

import battlecode.common.*;

import static testingPlayer.RobotPlayer.getTransmissionID;
import static testingPlayer.RobotPlayer.mission;
import static testingPlayer.RobotPlayer.updateMission;

/**
 * Created by Demetri on 1/20/2017.
 */
public class AnchorArchon extends Pathable{

    private RobotController rc;
    private MapLocation center;
    private int MISSION_NUMBER = Mission.ANCHOR_ARCHON.missionNum;

    public AnchorArchon(RobotController rc){
        this.rc = rc;
        mission = MISSION_NUMBER;
        runAnchorArchon();
    }


    public void runAnchorArchon(){

        try {
            //this should only happen once, and is therefore outside of the while loop
            getTransmissionID();

            center = getAverageLocation(rc.getInitialArchonLocations(rc.getTeam().opponent()));
            MapLocation target = center;
            rc.setIndicatorLine(rc.getLocation(), target, 0,0,0);

            while (mission == MISSION_NUMBER) {
                //Every turn, check to see if the mission is updated,
                //but the robot won't change behavior for a turn
                updateMission();

                //It is currently expected that the archon will not move or move very little
                //He will spawn a couple gardeners that will be mechanics and spawn a scout
                //and lumberjacks. Then tree cells will be spawned and built around the archon.

                Clock.yield();
            }

        }catch(GameActionException e){
            e.printStackTrace();
        }
    }

    private MapLocation attemptFindCenter(){

        float centerX = 300;
        float centerY = 300;

        //get center knowledge
        MapLocation[] alliedArchons = rc.getInitialArchonLocations(rc.getTeam());
        MapLocation[] enemyArchons = rc.getInitialArchonLocations(rc.getTeam().opponent());

        MapLocation alliedAverage = getAverageLocation(alliedArchons);
        MapLocation enemyAverage = getAverageLocation(enemyArchons);
        MapLocation potentialCenter = getAverageLocation(alliedAverage, enemyAverage);

        if (alliedAverage.x == enemyAverage.x) {
            //If our X's match, then it's a horizontal reflection and we know the map's Y-axis midpoint
            centerY = potentialCenter.y;
        }

        if (alliedAverage.y == enemyAverage.y){
            //If our Y's match, then it's a vertical reflection and know the map's X-axis midpoint
            centerX = potentialCenter.x;
        }

        if(alliedAverage.x != enemyAverage.x && alliedAverage.y != enemyAverage.y) {
            //If neither values match, then it is a diagonal reflection and we know the maps true center.
            centerY = potentialCenter.y;
            centerX = potentialCenter.x;
        }

        return center = new MapLocation(centerX, centerY);
    }

    private MapLocation getAverageLocation(MapLocation[] locations){

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

    private MapLocation getAverageLocation(MapLocation location1, MapLocation location2){
        //create an array of the 2 locations and pass that into the getAverageLocation(MapLocation [])

        return getAverageLocation(new MapLocation[]{location1, location2});
    }

}
