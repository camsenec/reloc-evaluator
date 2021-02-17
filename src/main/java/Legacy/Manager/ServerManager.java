package Legacy.Manager;

public class ServerManager {

    //public static final ConcurrentHashMap<Integer, MecHost> serverMap = new ConcurrentHashMap();
    private static int NUMBER_OF_SERVERS = 16;

    /**
     * create servers on the field. Edge servers are allocated equally on the field.
     * Example of the allocation is shown above.
     * grainLevel : number of 4^(grainLevel) servers are created recursively.
     *
     * @param capacity size of memory on edgeServer which is allocated to particular application
     *
     * ※全サーバーのキャパシティを明示的に示しても良いし, 候補だけ与えておいても良い. とりあえず100ブロックで固定する.
     * 実機からはManagementサーバーに登録するだけ
     */

    /*
    public static void createServers(int applicationId, int capacity) {

        double areaLengthX = FieldManager.MAX_X - FieldManager.MIN_X;
        double areaLengthY = FieldManager.MAX_Y - FieldManager.MIN_Y;

        for (int i = 0; i < NUMBER_OF_SERVERS; i++) {
            Random random = new Random();
            double locationX = FieldManager.MIN_X + random.nextDouble() * areaLengthX;
            double locationY = FieldManager.MIN_Y + random.nextDouble() * areaLengthY;
            MecHost.registerToServer(applicationId, locationX, locationY);
        }
    }

    public static void setNumberOfServers(int numberOfServers) {
        NUMBER_OF_SERVERS = numberOfServers;
    }
    */

    /*
    public static void printAllServers(){
        for (int serverId : serverMap.keySet()) {
            System.out.println(serverMap.get(serverId));
        }
    }

    public static void printRemainOfAllServers(){
        for (int serverId : serverMap.keySet()) {
            System.out.println(serverMap.get(serverId).getUsed());
        }
    }

    public static void printCollectionSizeOfAllServers(){
        for (int serverId : serverMap.keySet()) {
            System.out.println(serverMap.get(serverId).getCollection().size());
        }
    }
    */



      /* Legacy

          private static int GROUP_SIZE = NUMBER_OF_SERVERS / 4;

          Allocation of MecHost ID (grainLevel = 2)

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


        public static void groupingServer() {
            int number_of_groups = (int) Math.ceil(NUMBER_OF_SERVERS / GROUP_SIZE);
            for (int i = 0; i < number_of_groups; i++) {
                int lowest = GROUP_SIZE * i;
                int highest = GROUP_SIZE * (i + 1) - 1;
                for (int j = 0; j < GROUP_SIZE; j++) {
                    if (i * GROUP_SIZE + j == NUMBER_OF_SERVERS) break;
                    serverMap.get(i).setSameGroupServers(new Range(lowest, highest));
                }
            }
        }

        public static void createServers(int capacity) {

            int grainLevel = (int) (log((double) NUMBER_OF_SERVERS) / log(4.0));

            double areaLengthX = FieldManager.MAX_X - FieldManager.MIN_X;
            double areaLengthY = FieldManager.MAX_Y - FieldManager.MIN_Y;

            serverMap.put(0, createServerAt(0, capacity, new Point2D(areaLengthX / 2, areaLengthY / 2)));
            if (grainLevel == 0) return;

            int serverId = 0;
            HashMap<Integer, MecHost> tmpServerMap = new HashMap<>();

            for (int level = 1; level <= grainLevel; level++) {

                areaLengthX /= 2;
                areaLengthY /= 2;

                for (int currentServerId : serverMap.keySet()) {
                    MecHost server = serverMap.get(currentServerId);
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

        // time complexity : O(log_4(numberOfServers))
        public static int findNearestServer(Point2D location) {
            int grainLevel = (int) (log((double) NUMBER_OF_SERVERS) / log(4.0));

            double areaLengthX = (FieldManager.MAX_X - FieldManager.MIN_X) / 2;
            double areaLengthY = (FieldManager.MAX_Y - FieldManager.MIN_Y) / 2;
            double locationX = location.getX();
            double locationY = location.getY();
            double posX = areaLengthX;
            double posY = areaLengthY;

            int serverId = 0;
            int interval;

            for (int level = grainLevel - 1; level >= 0; level--) { //長さではなくposition
                interval = (int) Math.pow(4, (double) level);
                areaLengthX /= 2;
                areaLengthY /= 2;

                if (locationX <= posX && locationY <= posY) {
                    serverId += interval * 0;
                    posX -= areaLengthX;
                    posY -= areaLengthY;
                } else if (locationX > posX && locationY <= posY) {
                    serverId += interval * 1;
                    posX += areaLengthX;
                    posY -= areaLengthY;
                } else if (locationX <= posX && locationY > posY) {
                    serverId += interval * 2;
                    posX -= areaLengthX;
                    posY += areaLengthY;
                } else {
                    posX += areaLengthX;
                    posY += areaLengthY;
                    serverId += interval * 3;
                }
            }

            return serverId;

        }

        public static void setGroupSize(int groupSize) {
            if(groupSize < NUMBER_OF_SERVERS) {
                GROUP_SIZE = groupSize;
            }else{
                System.err.println("group size must be less than number of servers");
            }
        }

    */


}
