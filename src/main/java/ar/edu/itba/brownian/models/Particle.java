package ar.edu.itba.brownian.models;
import java.util.Objects;

public class Particle implements Comparable<Particle>{
    private final int id;
    private final double mass;
    private final double radius;
    private final Position position;
    private final Velocity velocity;
    private long collisionQty = 0;

    public Particle(Particle particle){
        id = particle.id;
        mass = particle.mass;
        radius = particle.radius;
        position = new Position(particle.position.getX(), particle.position.getY());
        velocity = new Velocity(particle.getVelocityX(), particle.getVelocityY());
        collisionQty = particle.collisionQty;
    }
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

    public double getVelocityX() {
        return velocity.getVelocityX();
    }

    public double getVelocityY() {
        return velocity.getVelocityY();
    }

    public void setVelocity(double velocityX, double velocityY){
        velocity.setVelocityX(velocityX);
        velocity.setVelocityY(velocityY);
    }

    public double getSpeed() {
        return Math.sqrt(getVelocityX() * getVelocityX() + getVelocityY() * getVelocityY());
    }

    public long getCollisionQty() {
        return collisionQty;
    }

    public void moveStraightDuringTime(double time){
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
        double relativeX = otherParticle.position.getX() - position.getX();
        double relativeY = otherParticle.position.getY() - position.getY();
        double relativeVelocityX = otherParticle.getVelocityX() - getVelocityX();
        double relativeVelocityY = otherParticle.getVelocityY() - getVelocityY();
        double distance = position.getDistanceTo(otherParticle.position);
        double impulse = (2 * mass * otherParticle.mass * (relativeX * relativeVelocityX + relativeY * relativeVelocityY))/
                (distance * (mass + otherParticle.mass));
        double impulseX = impulse * relativeX / distance;
        double impulseY = impulse * relativeY / distance;

        velocity.setVelocityX(getVelocityX() + impulseX/mass);
        velocity.setVelocityY(getVelocityY() + impulseY/mass);
        otherParticle.velocity.setVelocityX(otherParticle.getVelocityX() - impulseX/otherParticle.mass);
        otherParticle.velocity.setVelocityY(otherParticle.getVelocityY() - impulseY/otherParticle.mass);
        collisionQty++;
        otherParticle.collisionQty++;
    }

    public String toFileString(){
        return String.valueOf(id) + ' ' +
                getPosition().getX() + ' ' + getPosition().getY() +
                ' ' + getVelocityX() + ' ' + getVelocityY() +
                ' ' + getMass() + ' ' + getRadius() + '\n';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Particle particle = (Particle) o;
        return id == particle.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Particle o) {
        return Integer.compare(id, o.id);
    }
}
