package Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class TxLog {
    /* {送信元Id : 送信先Idのリスト}*/
    public static final HashMap<Integer, ArrayList<Integer>> txLog = new HashMap<>();
    /* {送信元Id : 送信候補Documentのリスト} */
    public static final HashMap<Integer, ArrayList<Integer>> txLogDocs = new HashMap<>();

    public static final HashMap<Integer, ArrayList<Integer>> rxLogDocs = new HashMap<>();
}
