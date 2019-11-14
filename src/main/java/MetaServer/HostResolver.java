package MetaServer;


import EdgeServer.MecHost;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class HostResolver {
    ConcurrentHashMap<Integer, HashMap<Integer, MecHost>> hosts = new ConcurrentHashMap<>();
}
