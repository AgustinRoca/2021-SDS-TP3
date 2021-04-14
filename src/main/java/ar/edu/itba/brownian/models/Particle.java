package ar.edu.itba.brownian.models;

import java.util.Objects;

public class Particle {
    private final int id;
    private final double mass;
    private final double radius;
    private final Position position;
    private final Velocity velocity;
    private long collisionQty = 0;

    public Particle(int id, double mass, double radius, Position position, Velocity velocity) {
        this.id = id;
        this.mass = mass;
        this.radius = radius;
        this.position = position;
        this.velocity = velocity;
    }

    public int getId() {
        return id;
    }

    public double getMass() {
        return mass;
    }

    public double getRadius() {
        return radius;
    }

    public Position getPosition() {
        return position;
    }

    public Velocity getVelocity() {
        return velocity;
    }

    public double getVelocityX() {
        return velocity.getVelocityX();
    }

    public double getVelocityY() {
        return velocity.getVelocityY();
    }

    public long getCollisionQty() {
        return collisionQty;
    }

    private void moveStraightDuringTime(double time){
        position.setX(position.getX() + getVelocityX() * time);
        position.setY(position.getY() + getVelocityY() * time);
    }

    public void bounceWithVerticalWall(){
        velocity.setVelocityX(getVelocityX() * -1);
        collisionQty++;
    }

    public void bounceWithHorizontalWall(){
        velocity.setVelocityY(getVelocityY() * -1);
        collisionQty++;
    }

    public void bounceWithParticle(Particle otherParticle){
        boolean areTouching = position.getDistanceTo(otherParticle.position) <= radius + otherParticle.radius;
        if(!areTouching){
            throw new IllegalArgumentException("The particles are not touching with each other");
        }

        double relativeX = otherParticle.position.getX() - position.getX();
        double relativeY = otherParticle.position.getY() - position.getY();
        double relativeVelocityX = otherParticle.getVelocityX() - getVelocityX();
        double relativeVelocityY = otherParticle.getVelocityY() - getVelocityY();
        double distance = radius + otherParticle.radius;
        double impulse = (2 * mass * otherParticle.mass * (relativeX * relativeVelocityX + relativeY * relativeVelocityY))/
                (distance * (mass + otherParticle.mass));
        double impulseX = impulse * relativeX / distance;
        double impulseY = impulse * relativeY / distance;

        velocity.setVelocityX(getVelocityX() + impulseX/mass);
        velocity.setVelocityY(getVelocityY() + impulseY/mass);
        otherParticle.velocity.setVelocityX(otherParticle.getVelocityX() - impulseX/otherParticle.mass);
        otherParticle.velocity.setVelocityY(otherParticle.getVelocityY() - impulseY/otherParticle.mass);
    }

    public double timeUntilCollisionWithParticle(Particle otherParticle){
        double relativeX = otherParticle.position.getX() - position.getX();
        double relativeY = otherParticle.position.getY() - position.getY();
        double relativeVelocityX = otherParticle.getVelocityX() - getVelocityX();
        double relativeVelocityY = otherParticle.getVelocityY() - getVelocityY();
        double velocityDotPosition = relativeX * relativeVelocityX + relativeY * relativeVelocityY;
        double velocityDotVelocity = relativeVelocityX * relativeVelocityX + relativeVelocityY * relativeVelocityY;
        double positionDotPosition = relativeX * relativeX + relativeY * relativeY;
        double distance = radius + otherParticle.radius;
        double d = velocityDotPosition * velocityDotPosition - velocityDotVelocity * (positionDotPosition - distance * distance);
        if(velocityDotPosition >= 0 || d < 0)
            return -1;
        return -1 * (velocityDotPosition + Math.sqrt(d)) / velocityDotVelocity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Particle particle = (Particle) o;
        return position.equals(particle.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position);
    }
}
