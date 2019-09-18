package CentralServer;

import Constants.Constants;
import EdgeServer.EdgeServer;
import Field.Point2D;

import java.util.*;

public class FieldManager {
    public static final ArrayList<EdgeServer> serverList = new ArrayList<>(100);
    public static final ArrayList<ServerGroup> groupList = new ArrayList<>(100);

    public static final double MIN_Y = 0;
    public static final double MIN_X = 0;
    public static final double MAX_X = 100;
    public static final double MAX_Y = 100;

    public static int grainLevel;

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

    /*
        divide field recursively
     */

    /**
     * create servers on the field
     * grainLevel : number of 4^(grainLevel) servers are created recursively.
     * @param capacity : size of memory on edgeServer which is allocated to particular application
     */
    public static void createServers(int capacity){

        double areaLengthX = MAX_X - MIN_X;
        double areaLengthY = MAX_Y - MIN_Y;

        serverList.add(createServerAt(0, capacity, new Point2D(areaLengthX / 2,areaLengthY / 2)));
        if(grainLevel == 0) return;

        int serverId = 0;
        ArrayList<EdgeServer> tmpServerList= new ArrayList<>();

        for(int level = 1; level <= Constants.GRAIN_LEVEL; level++) {
            for (EdgeServer server : serverList) {
                double x = server.getLocation().getX();
                double y = server.getLocation().getY();

                areaLengthX /= 2;
                areaLengthY /= 2;

                tmpServerList.add(createServerAt(serverId++, capacity,
                        new Point2D(x - areaLengthX / 2, y - areaLengthY / 2)));
                tmpServerList.add(createServerAt(serverId++, capacity,
                        new Point2D(x + areaLengthX / 2, y - areaLengthY / 2)));
                tmpServerList.add(createServerAt(serverId++, capacity,
                        new Point2D(x - areaLengthX / 2, y + areaLengthY / 2)));
                tmpServerList.add(createServerAt(serverId++, capacity,
                        new Point2D(x + areaLengthX / 2, y + areaLengthY / 2)));
            }

            serverId = 0;
            serverList.clear();
            serverList.addAll(tmpServerList);
        }

    }

    private static EdgeServer createServerAt(int id, int capacity, Point2D location){
        return new EdgeServer(id, capacity, location);
    }

    // time complexity : O(log_4(numberOfServers))
    public static int findNearestServer(Point2D myLocation){
        int serverId = 0;

        double areaLengthX = MAX_X - MIN_X;
        double areaLengthY = MAX_Y - MIN_Y;

        double myLocationX = myLocation.getX();
        double myLocationY = myLocation.getY();

        for(int level = Constants.GRAIN_LEVEL - 1; level >= 0; level--) {
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

}
