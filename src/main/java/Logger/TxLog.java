package Logger;

import java.util.ArrayList;
import java.util.HashMap;

import Utility.Tuple;

public class TxLog {
    
    public static final HashMap<Tuple<Integer, Integer>, ArrayList<Integer>> txLog = new HashMap<>();
    public static final HashMap<Integer, ArrayList<Integer>> txLogSec = new HashMap<>();
    
    public static final HashMap<Tuple<Integer, Integer>, ArrayList<Integer>> txLogDocs = new HashMap<>();

    public static final HashMap<Integer, ArrayList<Integer>> rxLogDocs = new HashMap<>();
}
