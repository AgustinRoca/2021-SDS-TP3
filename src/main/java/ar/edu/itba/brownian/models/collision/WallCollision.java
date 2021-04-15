package ar.edu.itba.brownian.models.collision;

import ar.edu.itba.brownian.models.Particle;

import java.util.Collections;

public abstract class WallCollision extends Collision {

    public WallCollision(double time, Particle particle) {
        super(time, Collections.singletonList(particle));
    }
}
