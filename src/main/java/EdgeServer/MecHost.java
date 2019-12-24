package EdgeServer;


import Constants.Constants;
import Data.Document;
import Field.Point2D;

import java.util.HashMap;
import java.util.Random;

/*
    サーバー上で動くサーバプログラムを想定
    サーバープログラムに組み込まなければならないものは,
    残り容量（もしくは差分）を取得することと,
    Managementサーバーに送信すること
    これは, ディベロッパーに任せる

    残り容量の取得は基本的にどのプログラミング言語でも可能であろうし,
    Managementサーバーに送信することも容易

    'サーバーレス'とかであれば, そのプラットフォームの提供者が組み込むことを想定
    なんにしろ, サーバーマシンの保有者が利用することを想定
 */

public class MecHost {
    private int applicationId;
    private int serverId;
    private Point2D location = new Point2D();
    private int used;
    private int capacity;
    private HashMap<Integer, Document> collection = new HashMap<>();

    private static final ManagementServiceForServer service = new ManagementServiceForServer();

    /**
     * Default Constructor
     */
    public MecHost(int applicationId){
        this.applicationId = applicationId;
    }

    public void initialize(int capacity){
        //set capacity
        this.capacity = capacity;
        this.used = 0;
        //set location
        initializeLocation();
        //set serverId
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

    public void updateState(int sizeOfDoc){
        this.used += sizeOfDoc;
        this.capacity -= sizeOfDoc;
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
