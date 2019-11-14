package MetaServer;


import EdgeServer.MecHost;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class HostResolver {
    public static final ConcurrentHashMap<Integer, HashMap<Integer, MecHost>> hosts = new ConcurrentHashMap<>();
}
