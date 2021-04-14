package ar.edu.itba.brownian.models.collision;

import ar.edu.itba.brownian.models.Particle;

public class VerticalWallCollision extends WallCollision{

    public VerticalWallCollision(double time, Particle particle) {
        super(time, particle);
    }

    @Override
    public void applyCollision() {
        Particle particle = getParticlesInvolved().iterator().next();
        particle.bounceWithVerticalWall();
    }
}
