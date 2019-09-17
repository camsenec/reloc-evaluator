package CentralServer;

import EdgeServer.EdgeServer;
import java.util.ArrayList;

public class FieldManager {
    public static final ArrayList<EdgeServer> serverList = new ArrayList<>(100);
    public static final ArrayList<ServerGroup> groupList = new ArrayList<>(100);

    public static double MIN_Y = 0;
    public static double MIN_X = 0;
    public static double MAX_X = 100;
    public static double MAX_Y = 100;

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


    public static void createServers(int numberOfServers){
        createServerAt(0, (MAX_X - MIN_Y)/2,(MAX_X - MIN_Y)/2, --numberOfServers);
    }

    private static void createServerAt(int id, double x, double y){
        EdgeServer edgeServer = new EdgeServer(i, 100, Point(x, y));
    }

}
