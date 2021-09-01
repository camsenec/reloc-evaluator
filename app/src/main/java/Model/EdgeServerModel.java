package Model;

public class EdgeServerModel {
    private int application_id;
    private int server_id;
    private float x;
    private float y;
    private float capacity;
    private float used;
    private int connection;
    private float cp;
    private int cluster_id;

    public EdgeServerModel(int application_id, int server_id, float x, float y, float capacity, float used, int connection, float cp, int cluster_id) {
        this.application_id = application_id;
        this.server_id = server_id;
        this.x = x;
        this.y = y;
        this.capacity = capacity;
        this.used = used;
        this.connection = connection;
        this.cp = cp;
        this.cluster_id = cluster_id;
    }

    public int getApplication_id() {
        return application_id;
    }

    public void setApplication_id(int application_id) {
        this.application_id = application_id;
    }

    public int getServer_id() {
        return server_id;
    }

    public void setServer_id(int server_id) {
        this.server_id = server_id;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getCapacity() {
        return capacity;
    }

    public void setCapacity(float capacity) {
        this.capacity = capacity;
    }

    public float getUsed() {
        return used;
    }

    public void setUsed(float used) {
        this.used = used;
    }

    public int getConnection() {
        return connection;
    }

    public void setConnection(int connection) {
        this.connection = connection;
    }

    public float getCp() {
        return cp;
    }

    public void setCp(float cp) {
        this.cp = cp;
    }

    public int getCluster_id() {
        return cluster_id;
    }

    public void setCluster_id(int cluster_id) {
        this.cluster_id = cluster_id;
    }

    @Override
    public String toString() {
        return "EdgeServerModel{" +
                "application_id=" + application_id +
                ", server_id=" + server_id +
                ", x=" + x +
                ", y=" + y +
                ", capacity=" + capacity +
                ", used=" + used +
                ", connection=" + connection + 
                ", cluster_id=" + cluster_id +
                '}';
    }
    

}
