package Config;

public class Config {

    public static double MIN_X = 0;
    public static double MIN_Y = 0;
    public static double MAX_X = 25;
    public static double MAX_Y = 25;
    public static String BASE_URL = "http://127.0.0.1:8000/";

    public static int application_id = 1;
    public static int numberOfServers = 16;
    public static int capacityOfServers = 640;
    public static double sizeOfDocs = 1;
    public static int cpLimit = 3200;
    public static int numberOfDocsPerClient = 1;
    public static int locality = 2;
    public static int numOfServersInCluster = 4;
    public static String method = "RELOC";
    public static String distinct = "disjoint";
    
    public static void read(){
        String MAX_X_ENV = System.getenv("FIELD_X_LENGTH");
        if(MAX_X_ENV != null){
            MAX_X = Integer.parseInt(MAX_X_ENV);
        }
        
        String MAX_Y_ENV = System.getenv("FIELD_Y_LENGTH");
        if(MAX_Y_ENV != null){
            MAX_Y = Integer.parseInt(MAX_Y_ENV);
        }

        String SERVER_IP_ENV = System.getenv("SERVER_IP");
        String SERVER_POST_ENV = System.getenv("SERVER_PORT");
        if(SERVER_IP_ENV != null && SERVER_POST_ENV != null){
            BASE_URL = "http://" + SERVER_IP_ENV + ":" + SERVER_POST_ENV + "/";
        }

        
        String numberOfServersEnv = System.getenv("NUM_OF_EDGE_SERVERS"); 
        if(numberOfServersEnv != null) {
            numberOfServers = Integer.parseInt(numberOfServersEnv);
        }

        String capacityOfServersEnv = System.getenv("EDGE_SERVER_CAPACITY"); 
        if(capacityOfServersEnv != null) {
            capacityOfServers = Integer.parseInt(capacityOfServersEnv);
        }

        String cpLimitEnv = System.getenv("EDGE_SERVER_COMPUTATIONAL_CAPACITY");
        if(cpLimitEnv != null){
            cpLimit = Integer.parseInt(cpLimitEnv);
        }
        
        String numberOfDocsPerClientEnv = System.getenv("NUM_OF_DOCS_PER_CLIENT"); 
        if(numberOfDocsPerClientEnv != null) {
            numberOfDocsPerClient = Integer.parseInt(numberOfDocsPerClientEnv);
        }
        
    }
}
