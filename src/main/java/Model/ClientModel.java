package Model;

public class ClientModel {

    private int application_id;
    private int clientId;
    private float x;
    private float y;
    private int home;

    public ClientModel(int application_id, int clientId, float x, float y, int home) {
        this.application_id = application_id;
        this.clientId = clientId;
        this.x = x;
        this.y = y;
        this.home = home;
    }

    public int getApplicationId() {
        return application_id;
    }

    public void setApplicationId(int applicationId) {
        this.application_id = applicationId;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
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

    public int getHome() {
        return home;
    }

    public void setHome(int home) {
        this.home = home;
    }

    @Override
    public String toString() {
        return "ClientModel{" +
                "application_id=" + application_id +
                ", clientId=" + clientId +
                ", x=" + x +
                ", y=" + y +
                ", home=" + home +
                '}';
    }
}
