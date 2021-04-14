public class Position {
    private double x;
    private double y;

    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getDistanceTo(Position other){
        double relativeX = x - other.x;
        double relativeY = y - other.y;
        return Math.sqrt(relativeX * relativeX + relativeY * relativeY);
    }
}
