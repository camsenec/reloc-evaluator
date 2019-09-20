package MetaServer;

import Constants.Constants;
import EdgeServer.EdgeServer;
import Field.Point2D;

import java.util.HashMap;

import static java.lang.Math.log;

public class ServerManager {

    public static final HashMap<Integer, EdgeServer> serverMap = new HashMap();
    public static final HashMap<Integer, ServerGroup> groupMap = new HashMap();
    private static int NUMBER_OF_SERVERS = 2;

    /*
      Allocation of EdgeServer ID (grainLevel = 2)

      A(MIN_X, MIN_Y)          B(MAX_X, MIN_Y)
       ------------------------
      |     |     |     |     |
      |  1  |  2  |  5  |  6  |
      |     |     |     |     |
       ------------------------
      |     |     |     |     |
      |  3  |  4  |  7  |  8  |
      |     |     |     |     |
       ------------------------
      |     |     |     |     |
      |  9  |  10 |  13 |  14 |
      |     |     |     |     |
       ------------------------
      |     |     |     |     |
      |  11 |  12 |  15 |  16 |
      |     |     |     |     |
       -----------------------
      C(MIN_X, MAX_Y)          D(MAX_X, MAX_Y)
     */


    /**
     * create servers on the field. Edge servers are allocated equally on the field.
     * Example of the allocation is shown above.
     * grainLevel : number of 4^(grainLevel) servers are created recursively.
     * @param capacity : size of memory on edgeServer which is allocated to particular application
     */
    public static void createServers(int capacity){

        int grainLevel = (int)(log((double)NUMBER_OF_SERVERS) / log(4.0)) + 1;

        double areaLengthX = FieldManager.MAX_X - FieldManager.MIN_X;
        double areaLengthY = FieldManager.MAX_Y - FieldManager.MIN_Y;

        serverMap.put(0, createServerAt(0, capacity, new Point2D(areaLengthX / 2,areaLengthY / 2)));
        if(grainLevel == 0) return;

        int serverId = 0;
        HashMap<Integer, EdgeServer> tmpServerMap= new HashMap<>();

        for(int level = 1; level <= grainLevel; level++) {
            for (int currentServerId : serverMap.keySet()) {
                EdgeServer server = serverMap.get(currentServerId);
                double x = server.getLocation().getX();
                double y = server.getLocation().getY();

                areaLengthX /= 2;
                areaLengthY /= 2;

                tmpServerMap.put(serverId, createServerAt(serverId++, capacity,
                        new Point2D(x - areaLengthX / 2, y - areaLengthY / 2)));
                tmpServerMap.put(serverId, createServerAt(serverId++, capacity,
                        new Point2D(x + areaLengthX / 2, y - areaLengthY / 2)));
                tmpServerMap.put(serverId, createServerAt(serverId++, capacity,
                        new Point2D(x - areaLengthX / 2, y + areaLengthY / 2)));
                tmpServerMap.put(serverId, createServerAt(serverId++, capacity,
                        new Point2D(x + areaLengthX / 2, y + areaLengthY / 2)));
            }

            serverId = 0;
            serverMap.clear();
            serverMap.putAll(tmpServerMap);
        }

    }

    private static EdgeServer createServerAt(int id, int capacity, Point2D location){
        return new EdgeServer(id, capacity, location);
    }

    // time complexity : O(log_4(numberOfServers))
    public static int findNearestServer(Point2D myLocation){
        int grainLevel = (int)(log((double)NUMBER_OF_SERVERS) / log(4.0)) + 1;

        double areaLengthX = FieldManager.MAX_X - FieldManager.MIN_X;
        double areaLengthY = FieldManager.MAX_Y - FieldManager.MIN_Y;
        double myLocationX = myLocation.getX();
        double myLocationY = myLocation.getY();

        int serverId = 0;

        for(int level = grainLevel - 1; level >= 0; level--) {
            int interval = (int)Math.pow(4, (double)level);

            if (myLocationX <= areaLengthX / 2 && myLocationY <= areaLengthY / 2) {
                serverId += interval * 0;
            } else if (myLocationX > areaLengthX / 2 && myLocationY <= areaLengthY / 2) {
                serverId += interval * 1;
            } else if (myLocationX <= areaLengthX / 2 && myLocationY > areaLengthY / 2) {
                serverId += interval * 2;
            } else {
                serverId += interval * 3;
            }
        }

        return serverId;

    }

    public static void groupingServer(int groupGrainLevel){



    }

    public static void setnumberOfServers(int numberOfServers) {
        NUMBER_OF_SERVERS = numberOfServers;
    }



}
