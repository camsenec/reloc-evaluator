package ClientSide;

import Field.Point2D;

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

    public void initialize(double locationX, double locationY){
        initializeLocation(locationX, locationY);
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
