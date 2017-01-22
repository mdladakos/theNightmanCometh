package testingPlayer;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

/**
 * Created by Demetri on 1/20/2017.
 */
public class AnchorArchon {

    private RobotController rc;
    private float centerX = 300; //Possible ranges for the x value of center is 0 - 600
    private float centerY = 300; //Possible ranges for the y value of center is 0 - 600

    public AnchorArchon(RobotController rc){
        this.rc = rc;
        runAnchorArchon();
    }


    public void runAnchorArchon(){

        try {

            //this should only happen once, and is therefore outside of the while loop
            attemptFindCenter();

            while (true) {
                //go near corner

                //create gardeners
            }

        }catch(GameActionException e){
            e.printStackTrace();
        }
    }

    private void attemptFindCenter(){

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
