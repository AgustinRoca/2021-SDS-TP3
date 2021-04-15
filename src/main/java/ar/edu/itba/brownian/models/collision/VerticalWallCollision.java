package ar.edu.itba.brownian.models.collision;

import ar.edu.itba.brownian.models.Particle;
import com.sun.istack.internal.NotNull;

public class VerticalWallCollision extends WallCollision {

    public VerticalWallCollision(double time, @NotNull Particle particle) {
        super(time, particle);
    }

    @Override
    public void applyCollision() {
        for (Particle particle : getParticlesInvolved()){
            particle.bounceWithVerticalWall();
        }
    }
}
