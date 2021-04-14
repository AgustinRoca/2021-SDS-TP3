package ar.edu.itba.brownian.models;

public class Velocity {
    private double velocityX;
    private double velocityY;

    public Velocity(double velocityX, double velocityY) {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    public double getVelocityX() {
        return velocityX;
    }

    public void setVelocityX(double velocityX) {
        this.velocityX = velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }

    public void setVelocityY(double velocityY) {
        this.velocityY = velocityY;
    }

    @Override
    public String toString() {
        return "(" + velocityX + ", " + velocityY + ")";
    }
}
