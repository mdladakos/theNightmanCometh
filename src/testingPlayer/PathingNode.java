package testingPlayer;

import battlecode.common.*;

import static testingPlayer.RobotPlayer.rc;

/**
 * Created by malad on 1/21/2017.
 */
public class PathingNode {
    public MapLocation startDest;
    public MapLocation endDest;
    public MapLocation nodeLocation;
    float fCost;
    float gCost;
    float hCost;

    public PathingNode(MapLocation startDest, MapLocation endDest, MapLocation nodeLocation){
        this.startDest=startDest;
        this.endDest=endDest;
        this.nodeLocation=nodeLocation;

        gCost=startDest.distanceTo(nodeLocation);
        hCost=nodeLocation.distanceTo(endDest);
        fCost=gCost+hCost;
    }
}
