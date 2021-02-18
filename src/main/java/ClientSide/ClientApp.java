package ClientSide;

import Constants.Constants;
import Field.Point2D;

import java.util.Random;

public class ClientApp {

    private int applicationId;
    private int clientId; 
    private Point2D location = new Point2D(); 
    private int homeServerId;
    private static final ManagementServiceForClient service = new ManagementServiceForClient();

    public ClientApp(int applicationId, int clientId) {
        this.applicationId = applicationId;
        this.clientId = clientId;
    }

    public void initialize(){
        initializeLocation();
        service.registerToServer(this);
    }

    public void assignHomeserver(){
        service.getHomeServerId(this);
    }

    public void initializeLocation(double locationX, double locationY){
        this.location.setX(locationX);
        this.location.setY(locationY);
        service.registerToServer(this);
    }

    private void initializeLocation(){
        //Math.random()
        Random random = new Random();
        double areaLengthX = Constants.MAX_X - Constants.MIN_X;
        double areaLengthY = Constants.MAX_Y - Constants.MIN_Y;

        double locationX = Constants.MIN_X + random.nextDouble() * areaLengthX;
        double locationY = Constants.MIN_Y + random.nextDouble() * areaLengthY;
        this.location.setX(locationX);
        this.location.setY(locationY);
    }

    public void update(){
        updateLocation();
        updateHomeServer();
    }

    private void updateLocation() {
        Random random = new Random();
        double areaLengthX = Constants.MAX_X - Constants.MIN_X;
        double areaLengthY = Constants.MAX_Y - Constants.MIN_Y;

        this.location.setX((location.getX()
                + random.nextGaussian()) % areaLengthX + Constants.MIN_X);
        this.location.setY((location.getY()
                + random.nextGaussian()) % areaLengthY + Constants.MIN_Y);

        if(location.getX() < 0) this.location.setX(location.getX() + areaLengthX);
        if(location.getY() < 0) this.location.setY(location.getY() + areaLengthY);

        service.registerLocationToServer(this);
    }

    private void updateHomeServer(){
        service.getHomeServerId(this);
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


}
