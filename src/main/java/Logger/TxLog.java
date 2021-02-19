package Logger;

import java.util.ArrayList;
import java.util.HashMap;

public class TxLog {
    
    public static final HashMap<Integer, ArrayList<Integer>> txLog = new HashMap<>();
    
    public static final HashMap<Integer, ArrayList<Integer>> txLogDocs = new HashMap<>();

    public static final HashMap<Integer, ArrayList<Integer>> rxLogDocs = new HashMap<>();
}
