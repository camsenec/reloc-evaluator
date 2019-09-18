package CentralServer;

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

    /*
      A(MIN_X, MIN_Y)                       B(MAX_X, MIN_Y)
       -------------------------------------
      |                                     |
      |                                     |
      |                                     |
      |                                     |
      |                                     |
      |                                     |
      |                                     |
      |                                     |
      |                                     |
       -------------------------------------
      C(MIN_X, MAX_Y)                       B(MAX_X, MAX_Y)
     */

    /*
        divide field recursively
     */
    public static void createServers(int numberOfServers, int capacity){
        double fieldLengthX = MAX_X - MIN_X;
        double fieldLengthY = MAX_Y - MIN_Y;

        serverList.add(createServerAt(0, capacity, new Point2D(fieldLengthX / 2,fieldLengthY / 2)));
        if(numberOfServers == 1) return;

        int counter = 0;
        ArrayList<EdgeServer> tmpServerList= new ArrayList<>();

        while(true) {

            for (EdgeServer server : serverList) {
                if(counter >= numberOfServers) return;

                double x = server.getLocation().getX();
                double y = server.getLocation().getY();
                fieldLengthX /= 2;
                fieldLengthY /= 2;
                tmpServerList.add(createServerAt(counter++, capacity,
                        new Point2D(x + fieldLengthX / 2, y + fieldLengthY / 2)));
                tmpServerList.add(createServerAt(counter++, capacity,
                        new Point2D(x + fieldLengthX / 2, y - fieldLengthY / 2)));
                tmpServerList.add(createServerAt(counter++, capacity,
                        new Point2D(x - fieldLengthX / 2, y + fieldLengthY / 2)));
                tmpServerList.add(createServerAt(counter++, capacity,
                        new Point2D(x - fieldLengthX / 2, y - fieldLengthY / 2)));
            }

            counter = 0;
            serverList.clear();
            serverList.addAll(tmpServerList);
        }

    }

    private static EdgeServer createServerAt(int id, int capacity, Point2D location){
        return new EdgeServer(id, capacity, location);
    }

}
