package ar.edu.itba.brownian.models.collision;

import ar.edu.itba.brownian.models.Particle;
import com.sun.istack.internal.NotNull;

public class HorizontalWallCollision extends WallCollision{

    public HorizontalWallCollision(double time, @NotNull Particle particle) {
        super(time, particle);
    }

    @Override
    public void applyCollision() {
        for (Particle particle : getParticlesInvolved()){
            particle.bounceWithHorizontalWall();
        }
    }
}
