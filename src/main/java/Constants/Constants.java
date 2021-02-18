package Constants;

public class Constants {
    public static final double MIN_Y = 0;
    public static final double MIN_X = 0;
    public static final double MAX_X = 100;
    public static final double MAX_Y = 100;

    public static final int INF = 10000000;
    
    public static boolean DEBUG = false;

    public static boolean UPLOAD = true;
    public static boolean SIMULATION = true;
    public static boolean TEST = true;
    public static boolean LOG = true;
    public static boolean SAVE = true;
    public static boolean RESULT = true;
    public static boolean RESET = true;

    public static final String BASE_URL = "http://127.0.0.1:8000/";

    public static void first(){
      UPLOAD = true;
    }

    public static void notFirst(){
      UPLOAD = false;
    }


}
