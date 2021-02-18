package Result;

public class Result {
    public static double meanOfUsed = 0;
    public static double minOfUsed = 0;
    public static double maxOfUsed = 0;
    public static double rateOfSaved = 0;
    public static double meanOfCachedDocs = 0;


    public static int kindOfDocument = 0;
    public static int numberOfCachedDocument = 0;
    public static int numberOfSender = 0;
    public static int numberOfClient = 0;
    public static int saved = 0;

    public static void reset(){
        meanOfUsed = 0;
        minOfUsed = 0;
        maxOfUsed = 0;
        rateOfSaved = 0;
        meanOfCachedDocs = 0;
        kindOfDocument = 0;
        numberOfCachedDocument = 0;
        numberOfSender = 0;
        numberOfClient = 0;
        saved = 0;
    }
}
