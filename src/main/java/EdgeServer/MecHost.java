package EdgeServer;


import Constants.Constants;
import Data.Document;
import Field.Point2D;

import java.util.HashMap;
import java.util.Random;

import Config.Config;
import MP.MessageProcessor;

public class MecHost {
    private int application_id;
    private int server_id;
    private Point2D location = new Point2D();
    private int used;
    private int capacity;
    private int connection;
    private HashMap<Integer, Document> collection = new HashMap<>();
    private HashMap<Integer, MessageProcessor> MPmap = new HashMap<>();
    private static final ManagementServiceForServer service = new ManagementServiceForServer();
    private int clusterId;

    /**
     * Default Constructor
     */
    public MecHost(int applicationId){
        this.application_id = applicationId;
    }

    public void initialize(int capacity){
        this.capacity = capacity;
        this.used = 0;
        this.connection = 0;
        initializeLocation();
        service.registerToServer(this);
    }

    public void initialize(int capacity, int id){
        this.capacity = capacity;
        this.used = 0;
        this.connection = 0;
        initializeLocation(id);
        service.registerToServer(this);
    }


    private void initializeLocation(){
        double areaLengthX = Constants.MAX_X - Constants.MIN_X;
        double areaLengthY = Constants.MAX_Y - Constants.MIN_Y;

        Random random = new Random();
        double locationX = Constants.MIN_X + random.nextDouble() * areaLengthX;
        double locationY = Constants.MIN_Y + random.nextDouble() * areaLengthY;
        this.location.setX(locationX);
        this.location.setY(locationY);
    }
    
    private void initializeLocation(int id){
        assert(Config.numberOfServers == 100);
        int j = (int)(id / 10);
        int i = id % 10;
        double locationX = 5 + 10 * i;
        double locationY = 5 + 10 * j;
        this.location.setX(locationX);
        this.location.setY(locationY);
    }

    public void addUsed(double sizeOfDoc){
        this.used += sizeOfDoc;
        service.updateState(this);
    }

    public void addConnection(int connection){
        this.connection+=connection;
        service.updateState(this);
    }

    public void resetState(){
        this.used = 0;
        this.connection = 0;
        service.updateState(this);
    }

    public int getApplicationId() {
        return application_id;
    }

    public void setApplicationId(int application_id) {
        this.application_id = application_id;
    }

    public int getServerId() {
        return server_id;
    }

    public void setServerId(int server_id) {
        this.server_id = server_id;
    }

    public int getUsed() {
        return used;
    }

    public void setUsed(int used) {
        this.used = used;
    }

    public Point2D getLocation() {
        return location;
    }

    public void setLocation(Point2D location) {
        this.location = location;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

     public int getConnection() {
        return connection;
    }

    public void setConnection(int connection) {
        this.connection = connection;
    }

    

    public HashMap<Integer, Document> getCollection() {
        return collection;
    }

    public HashMap<Integer, MessageProcessor> getMPmap() {
        return MPmap;
    }

     public int getClusterId() {
        return clusterId;
    }

    public void setClusterId(int clusterId) {
        this.clusterId = clusterId;
    }

    

    @Override
    public String toString() {
        return "MecHost{" +
                "applicationId=" + application_id +
                ", serverId=" + server_id +
                ", location=" + location +
                ", used=" + used +
                ", capacity=" + capacity +
                ", collection=" + collection +
                '}';
    }

    

}
