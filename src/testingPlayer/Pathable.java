package testingPlayer;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;

import static testingPlayer.RobotPlayer.rc;

/**
 * If you want something to have access to Pathing, simply have it extend Pathable in the class declaration.
 * Ex: public class AnchorArchon extends Pathable {}
 */
public abstract class Pathable {

    private float TRACING_PATH_OFFSET = 6.2831855f/ 100;
    private int CHECKS_PER_SIDE = 100;

    private MapLocation start;
    private Float traceStartDist;
    private MapLocation dest = rc.getLocation();
    private Direction mLine;
    private boolean isTracing;

    //TODO: protect against loops by not attaching to mLine if further away than before

    /**
     * Attempts a bug like path to the destination. Follows a straight line to its destination,
     * tracing the perimeter or objects in its path until it intersects with the straight line again.
     * It then follows the straight line to the path.
     * If no legal path is found, will stand still.
     * @param dest the final location to attempt to reach
     * @throws GameActionException - most likely due to attempting an illegal move
     */
    public void path(MapLocation dest) throws GameActionException {

        //reset values if the destination passed in is different than the one stored
        if(!this.dest.equals(dest)) {
            isTracing = false; //if there's a new destination, ditch tracing
            this.dest = dest; //set the new destination
        }

        //if tracing, continue to trace. Otherwise, following moving rules
        if (isTracing) {
            trace();
        }else {
            if (rc.canMove(dest)) {
                rc.move(dest);
            }else{
                start = rc.getLocation(); //set the new start
                mLine = start.directionTo(dest); //set the new mLine
                isTracing=true;
                traceStartDist = rc.getLocation().distanceTo(dest);
                trace();
            }
        }

    }

    //This is a variation of tryMove from RobotPlayer
    private void trace() throws GameActionException {

        //Direction from the current location to the destination
        Direction cLine = rc.getLocation().directionTo(dest);
        //The radians between mLine and cLine. Positive values are to the "left".
        float deviation = mLine.radiansBetween(cLine);

        Direction retVal = rc.getLocation().directionTo(dest);
        int currentCheck = 0;
        boolean firstNoFound = false;
        boolean canMove = false;
        boolean didMove = false;

        System.out.println("Deviation = " + deviation);
        while(currentCheck<=CHECKS_PER_SIDE && !didMove) {

                //If deviation is less than 0, it means we turned left and should continue doing so
                if (deviation <= 0) {
//                    System.out.println("Checking left: "+cLine.rotateLeftRads(TRACING_PATH_OFFSET*currentCheck));
                    if (rc.canMove(cLine.rotateLeftRads(TRACING_PATH_OFFSET * currentCheck))) {
                        retVal = cLine.rotateLeftRads(TRACING_PATH_OFFSET * currentCheck);
                        canMove = true; // flag for if
                    }else{
                        //we should fail at least once before accepting the direction
                        firstNoFound = true;
                        canMove = false;
//                        System.out.println("Got first no!");
                    }
                }

                // If deviation is greater than 0, it means we turned right and should continue doing so
                if (deviation >= 0) {
//                    System.out.println("Checking right: "+cLine.rotateRightRads(TRACING_PATH_OFFSET*currentCheck));
                    if (rc.canMove(cLine.rotateRightRads(TRACING_PATH_OFFSET * currentCheck))) {
                        retVal = cLine.rotateRightRads(TRACING_PATH_OFFSET * currentCheck);
                        canMove = true; //flag for if
                    }else{
                        //we should fail at least once before accepting the direction
                        canMove = false;
                        firstNoFound = true;
//                        System.out.println("Got first no!");
                    }
                }

                if(firstNoFound&&canMove){
                //determine future location
                MapLocation future = rc.getLocation().add(retVal,rc.getType().strideRadius);
                //get future location deviation
                float check = mLine.degreesBetween(future.directionTo(dest));

                if((deviation*check)>0) {
                    rc.move(retVal);
                    didMove = true;
                } else if((deviation*check)<0) {
                    float dist = distToMLine(cLine, future);
                    System.out.println("dist = " + dist);
                    rc.move(retVal, dist);
                    didMove=true;
                    if(rc.getLocation().distanceTo(dest) < traceStartDist) {
                        isTracing = false;
                    }
                } else if((deviation*check)==0){
                    rc.move(retVal);
                    didMove = true;
                    if(rc.getLocation().distanceTo(dest) < traceStartDist) {
                        isTracing = false;
                    }
                }
            }

            // No move performed, try slightly further
            currentCheck++;
        }

    }

    private float distToMLine(Direction cLine, MapLocation future){
         /* We need to calculate the value of b below. We will use the triangle
                          below with known information and the Law of Sines to calculate the desired
                          value.
                               a
                           |---------- Goal
                           |C       B/
                          b|       /
                           |     / c
                           | A /
                           | /
                           bot

                           We know the bot's location, the goal location, and the direction of mLine.
                           We know A, B, and c, and can use that to calculate b.
                           c = 180 - (a + b).
                           The Law of Sines can then be done to determine the length of b.
                           a/(sinA) = b/(sinB) =c/(sinC)
                           b = (c/(sinC))*(sinB)
                         */

        float c = rc.getLocation().distanceTo(dest);
        float A = Math.abs(cLine.degreesBetween(rc.getLocation().directionTo(future)));
        float B = Math.abs(mLine.degreesBetween(cLine));
        float C = 180 - (A + B);

        //check for reverse mLine (caused by going beyond goal)
        if((A+B+C)!= 180){
            B = 180 - B;
            C = 180 - (A+B);
        }


        System.out.println("A+B+C="+(A+B+C));


        //return b
        return Math.abs((float)((c/Math.sin(C))*(Math.sin(B))));
    }
}
