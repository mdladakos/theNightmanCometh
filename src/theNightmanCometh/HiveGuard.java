package theNightmanCometh;

import battlecode.common.*;

import java.util.ArrayList;
import java.util.List;

import static theNightmanCometh.RobotPlayer.tryMove;


/**
 * Created by Demetri on 1/25/2017.
 */
public class HiveGuard extends Pathable{

    private RobotController rc;
    List<Integer> TREELIST = new ArrayList<>();

    public HiveGuard (RobotController rc){
        this.rc = rc;
        runScout();
    }

    void runScout(){

        System.out.println("I'm a scout!");
        while(true) {
            try {

                path(new MapLocation(601,601));

            } catch (Exception e) {
                System.out.println("Scout Exception!");
            }
        }
    }
}
