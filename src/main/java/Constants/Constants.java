package Constants;

/* 設定ファイル(Plhamのようにjson的なものを渡したい*/

public class Constants {
    public static final boolean DEBUG = false;
    public static final boolean UPLOAD = true;
    public static final boolean SIMULATION = true;
    public static final boolean LOG = true;
    public static final boolean SAVE = true;

    public static final String BASE_URL = "http://127.0.0.1:8000/";


    public static final double MIN_Y = 0;
    public static final double MIN_X = 0;
    public static final double MAX_X = 100;
    public static final double MAX_Y = 100;

    public static void first(){
      UPLOAD = true;
      SIMULATION = true;
      LOG = true;
      SAVE = true;
    }

    public static void notFirst(){
      UPLOAD = false;
      SIMULATION = true;
      LOG = true;
      SAVE = false;
    }


}
