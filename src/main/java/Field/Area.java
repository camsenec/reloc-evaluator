package Field;

public class Area {
    Point2D upperLeft;
    Point2D upperRight;
    Point2D lowerLeft;
    Point2D lowerRight;

    public Area(Point2D upperLeft, Point2D upperRight, Point2D lowerLeft, Point2D lowerRight) {
        this.upperLeft = upperLeft;
        this.upperRight = upperRight;
        this.lowerLeft = lowerLeft;
        this.lowerRight = lowerRight;
    }
}
