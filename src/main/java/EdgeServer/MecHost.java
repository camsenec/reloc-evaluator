package EdgeServer;


import Constants.Constants;
import Data.Document;
import Field.Point2D;

import java.util.HashMap;
import java.util.Random;


public class MecHost {
    private int applicationId;
    private int serverId;
    private Point2D location = new Point2D();
    private int used;
    private int capacity;
    private int connection;
    private HashMap<Integer, Document> collection = new HashMap<>();
    private static final ManagementServiceForServer service = new ManagementServiceForServer();

    /**
     * Default Constructor
     */
    public MecHost(int applicationId){
        this.applicationId = applicationId;
    }

    public void initialize(int capacity){
        this.capacity = capacity;
        this.used = 0;
        this.connection = 0;
        initializeLocation();
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

    public void addUsed(int sizeOfDoc){
        this.used += sizeOfDoc;
        service.updateState(this);
    }

    public void addConnection(){
        this.connection++;
        service.updateState(this);
    }

    public void resetState(){
        this.used = 0;
        this.capacity = 0;
        service.updateState(this);
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
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

    @Override
    public String toString() {
        return "MecHost{" +
                "applicationId=" + applicationId +
                ", serverId=" + serverId +
                ", location=" + location +
                ", used=" + used +
                ", capacity=" + capacity +
                ", collection=" + collection +
                '}';
    }

}
