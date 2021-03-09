package Result;

import java.util.HashMap;

public class Result {
    public static double meanOfUsed = 0;
    public static double minOfUsed = 0;
    public static double maxOfUsed = 0;
    public static double rateOfSaved = 0;
    public static double meanOfCachedDocs = 0;
    public static int publishedDocument = 0;
    public static int numberOfCachedDocument = 0;
    public static int numberOfGroups = 0;
    public static int numberOfSenders = 0;
    public static int numberOfClient = 0;
    public static int saved = 0;
    
    public static final HashMap<Integer, Double> aMap = new HashMap<>();    
    public static final HashMap<Integer, Double> bMap = new HashMap<>();
    public static final HashMap<Integer, Double> distanceMap = new HashMap<>();

    public static void reset(){
        meanOfUsed = 0;
        minOfUsed = 0;
        maxOfUsed = 0;
        rateOfSaved = 0;
        meanOfCachedDocs = 0;
        publishedDocument = 0;
        numberOfCachedDocument = 0;
        numberOfGroups = 0;
        numberOfSenders = 0;
        numberOfClient = 0;
        saved = 0;
        aMap.clear();
        bMap.clear();
        distanceMap.clear();
    }
}