package Model;

public class EdgeServerModel {
    int application_id;
    int server_id;
    float x;
    float y;
    float capacity;
    float remain;
    int cluster_id;

    public EdgeServerModel(int application_id, int server_id, float x, float y, float capacity, float remain, int cluster_id) {
        this.application_id = application_id;
        this.server_id = server_id;
        this.x = x;
        this.y = y;
        this.capacity = capacity;
        this.remain = remain;
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

    public float getRemain() {
        return remain;
    }

    public void setRemain(float remain) {
        this.remain = remain;
    }

    public int getCluster_id() {
        return cluster_id;
    }

    public void setCluster_id(int cluster_id) {
        this.cluster_id = cluster_id;
    }
}
