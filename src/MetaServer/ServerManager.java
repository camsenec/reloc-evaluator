package MetaServer;

import Constants.Constants;
import EdgeServer.EdgeServer;
import Field.Point2D;
import Utility.Range;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Math.log;

public class ServerManager {

    public static final ConcurrentHashMap<Integer, EdgeServer> serverMap = new ConcurrentHashMap();
    private static int NUMBER_OF_SERVERS = 16;
    private static int GROUP_SIZE = NUMBER_OF_SERVERS / 4;

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
     * ->全サーバーのキャパシティを明示的に示しても良いし, 候補だけ与えておいても良い. とりあえず100ブロックで固定する.
     */
    public static void createServers(int capacity){

        int grainLevel = (int)(log((double)NUMBER_OF_SERVERS) / log(4.0));

        double areaLengthX = FieldManager.MAX_X - FieldManager.MIN_X;
        double areaLengthY = FieldManager.MAX_Y - FieldManager.MIN_Y;

        serverMap.put(0, createServerAt(0, capacity, new Point2D(areaLengthX / 2,areaLengthY / 2)));
        if(grainLevel == 0) return;

        int serverId = 0;
        HashMap<Integer, EdgeServer> tmpServerMap= new HashMap<>();

        for(int level = 1; level <= grainLevel; level++) {

            areaLengthX /= 2;
            areaLengthY /= 2;

            for (int currentServerId : serverMap.keySet()) {
                EdgeServer server = serverMap.get(currentServerId);
                double x = server.getLocation().getX();
                double y = server.getLocation().getY();

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
    public static int findNearestServer(Point2D location){
        int grainLevel = (int)(log((double)NUMBER_OF_SERVERS) / log(4.0));

        double areaLengthX = FieldManager.MAX_X - FieldManager.MIN_X;
        double areaLengthY = FieldManager.MAX_Y - FieldManager.MIN_Y;
        double locationX = location.getX();
        double locationY = location.getY();

        int serverId = 0;
        int interval;

        for(int level = grainLevel - 1; level >= 0; level--) {
            interval = (int)Math.pow(4, (double)level);

            if (locationX <= areaLengthX / 2 && locationY <= areaLengthY / 2) {
                serverId += interval * 0;
            } else if (locationX > areaLengthX / 2 && locationY <= areaLengthY / 2) {
                serverId += interval * 1;
            } else if (locationX <= areaLengthX / 2 && locationY > areaLengthY / 2) {
                serverId += interval * 2;
            } else {
                serverId += interval * 3;
            }

            areaLengthX /= 2;
            areaLengthY /= 2;
        }

        return serverId;

    }

    public static void groupingServer(){
        int number_of_groups = (int)Math.ceil(NUMBER_OF_SERVERS / GROUP_SIZE);
        for(int i = 0; i < number_of_groups; i++){
            int lowest = GROUP_SIZE * i;
            int highest = GROUP_SIZE * (i + 1) - 1;
            for(int j = 0; j < GROUP_SIZE; j++){
                if(i * GROUP_SIZE + j == NUMBER_OF_SERVERS) break;
                serverMap.get(i).setSameGroupServers(new Range(lowest, highest));
            }
        }
    }

    public static void setNumberOfServers(int numberOfServers) {
        NUMBER_OF_SERVERS = numberOfServers;
    }

    public static void setGroupSize(int groupSize) {
        if(groupSize < NUMBER_OF_SERVERS) {
            GROUP_SIZE = groupSize;
        }else{
            System.err.println("group size must be less than number of servers");
        }
    }

    public static void printAllServers(){
        for (int serverId : serverMap.keySet()) {
            System.out.println(serverMap.get(serverId));
        }
    }

    public static void printRemainOfAllServers(){
        for (int serverId : serverMap.keySet()) {
            System.out.println(serverMap.get(serverId).getRemain());
        }
    }

    public static void printCollectionSizeOfAllServers(){
        for (int serverId : serverMap.keySet()) {
            System.out.println(serverMap.get(serverId).getCollection().size());
        }
    }
}
