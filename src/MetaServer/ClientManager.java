package MetaServer;

import EdgeServer.EdgeServer;

import java.util.ArrayList;

public class ClientManager {
    public static final ArrayList<EdgeServer> clientList = new ArrayList<>(100);

    public static final double MIN_Y = 0;
    public static final double MIN_X = 0;
    public static final double MAX_X = 100;
    public static final double MAX_Y = 100;

    private static int GRAIN_LEVEL = 2;

}
