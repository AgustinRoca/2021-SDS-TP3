package ar.edu.itba.brownian.models.collision;

import ar.edu.itba.brownian.models.Particle;

public class HorizontalWallCollision extends WallCollision{

    public HorizontalWallCollision(double time, Particle particle) {
        super(time, particle);
    }

    @Override
    public void applyCollision() {
        for (Particle particle : getParticlesInvolved()){
            particle.bounceWithHorizontalWall();
        }
    }
}
