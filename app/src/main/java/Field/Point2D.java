package Field;

public class Point2D {

    private double x;
    private double y;

    public Point2D(){}

    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x){
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format("(%6.3f, %6.3f)", x, y);
    }
}
