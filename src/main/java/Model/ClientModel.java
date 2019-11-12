package Model;

public class ClientModel {

    int application_id;
    int client_id;
    float x;
    float y;
    int home;

    public ClientModel(int application_id, int client_id, float x, float y, int home) {
        this.application_id = application_id;
        this.client_id = client_id;
        this.x = x;
        this.y = y;
        this.home = home;
    }

    public int getApplication_id() {
        return application_id;
    }

    public void setApplication_id(int application_id) {
        this.application_id = application_id;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
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
}
