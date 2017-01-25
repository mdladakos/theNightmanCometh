package testingPlayer;

import battlecode.common.*;

/**
 * Created by Demetri on 1/20/2017.
 */
public class AnchorArchon extends Pathable {

    private RobotController rc;
    private MapLocation center;

    public AnchorArchon(RobotController rc) {
        this.rc = rc;
        runAnchorArchon();
    }


    public void runAnchorArchon() {

        try {
            while (true) {
                //this should only happen once, and is therefore outside of the while loop
                center = attemptFindCenter();
                MapLocation endDest = rc.getLocation().subtract(rc.getLocation().directionTo(center), rc.getType().strideRadius*10);
                rc.setIndicatorLine(rc.getLocation(), endDest, 0, 0, 0);

                rc.broadcastFloat(9998,center.x);
                rc.broadcastFloat(9999,center.y);

                if (rc.canHireGardener(rc.getLocation().directionTo(center).opposite()) && rc.getTeamBullets() > 200) {
                    rc.hireGardener(rc.getLocation().directionTo(center).opposite());
                }
                Clock.yield();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private MapLocation attemptFindCenter() {

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
            centerX = enemyAverage.x;
        }

        if (alliedAverage.y == enemyAverage.y) {
            //If our Y's match, then it's a vertical reflection and know the map's X-axis midpoint
            centerX = potentialCenter.x;
            centerY = enemyAverage.y;
        }

        if (alliedAverage.x != enemyAverage.x && alliedAverage.y != enemyAverage.y) {
            //If neither values match, then it is a diagonal reflection and we know the maps true center.
            centerY = potentialCenter.y;
            centerX = potentialCenter.x;
        }

        return center = new MapLocation(centerX, centerY);
    }

    private MapLocation getAverageLocation(MapLocation[] locations) {

        //initialize
        float x = 0;
        float y = 0;

        //for each location, sum the x and y values
        for (MapLocation location : locations) {
            x += location.x;
            y += location.y;
        }

        //average the values by the number of locations
        x = x / locations.length;
        y = y / locations.length;

        //return the average location
        return new MapLocation(x, y);
    }

    private MapLocation getAverageLocation(MapLocation location1, MapLocation location2) {
        //create an array of the 2 locations and pass that into the getAverageLocation(MapLocation [])

        return getAverageLocation(new MapLocation[]{location1, location2});
    }
}
