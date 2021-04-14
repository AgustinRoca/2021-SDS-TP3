package ar.edu.itba.brownian.models.collision;

import ar.edu.itba.brownian.models.Particle;

import java.util.Collections;
import java.util.List;

public abstract class WallCollision implements Collision {
    private final Particle particle;
    private double time;
    private final long collisionQty;

    public WallCollision(double time, Particle particle) {
        this.particle = particle;
        this.time = time;
        this.collisionQty = particle.getCollisionQty();
    }

    @Override
    public double getTime() {
        return time;
    }

    @Override
    public void setTime(double time) {
        this.time = time;
    }

    @Override
    public List<Particle> getParticlesInvolved() {
        return Collections.singletonList(particle);
    }

    @Override
    public List<Long> getParticleCollisionsCount() {
        return Collections.singletonList(collisionQty);
    }
}
