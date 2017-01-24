package testingPlayer;

/**
 * Created by Demetri on 1/23/2017.
 */
public enum Mission {

        DEFAULT_MISSION(-1),
        ANCHOR_ARCHON(101),
        GARDENER_NUCLEUS(201);

        public final int missionNum;

        Mission(int value){
                this.missionNum = value;
        }

        public static Mission getMissionByValue(int value){
           for(Mission mission : values()){
                if(mission.missionNum == value){
                        return mission;
                }
           }
           return DEFAULT_MISSION;
        }
}
