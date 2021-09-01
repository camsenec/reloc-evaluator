package ClientSide;

import java.util.HashMap;
import java.util.Random;

import Field.Point2D;
import MP.MessageProcessor;

import Config.Config;

public class ClientApp {

    private int applicationId;
    private int clientId; 
    private Point2D location = new Point2D(); 
    private int homeServerId;
    private HashMap<Integer, MessageProcessor> MPmap = new HashMap<>();
    private static final ManagementServiceForClient service = new ManagementServiceForClient();

    public ClientApp(int applicationId, int clientId) {
        this.applicationId = applicationId;
        this.clientId = clientId;
    }

    public void initialize(){
        initializeLocation();
        service.registerToServer(this);
    }

    public void initialize(double locationX, double locationY){
        initializeLocation(locationX, locationY);
        service.registerToServer(this);
    }

    public void assignHomeserver(){
        service.getHomeServerId(this);
    }

    public void assignHomeserver(double plus_cp, double plus_used){
        service.getHomeServerId(this, plus_cp, plus_used);
    }
    
    public void updateState(int newHomeId){
        service.updateState(this, newHomeId);
    }

    public void initializeLocation(){
        Random random = new Random();
        double locationX = Config.MIN_X + random.nextDouble() * (Config.MAX_X - Config.MIN_X);
        double locationY = Config.MIN_Y + random.nextDouble() * (Config.MAX_Y - Config.MIN_Y);
        this.location.setX(locationX);
        this.location.setY(locationY);
    }

    public void initializeLocation(double locationX, double locationY){
        this.location.setX(locationX);
        this.location.setY(locationY);
    }

    public int getApplicationId() {
        return applicationId;
    }

    public int getClientId() {
        return clientId;
    }

    public Point2D getLocation() {
        return location;
    }

    public int getHomeServerId() {
        return homeServerId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }


    public void setClientId(int clientId) {
        this.clientId = clientId;
    }


    public void setLocation(Point2D location) {
        this.location = location;
    }

    public void setHomeServerId(int homeServerId) {
        this.homeServerId = homeServerId;
    }

    public HashMap<Integer, MessageProcessor> getMPmap() {
        return MPmap;
    }

    public void setMPmap(HashMap<Integer, MessageProcessor> mPmap) {
        MPmap = mPmap;
    }


}
