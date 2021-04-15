package ar.edu.itba.brownian.models.collision;

import ar.edu.itba.brownian.models.Particle;
import com.sun.istack.internal.NotNull;

import java.util.*;

public class ParticleCollision extends Collision {

    public ParticleCollision(double time, @NotNull Particle particle, @NotNull Particle otherParticle) {
        super(time, Arrays.asList(particle, otherParticle));
    }

    @Override
    public void applyCollision() {
        if(getParticlesInvolved().size() != 2)
            throw new IllegalStateException();
        Iterator<Particle> iterator = getParticlesInvolved().iterator();
        Particle particle1 = iterator.next();
        Particle particle2 = iterator.next();
        particle1.bounceWithParticle(particle2);
    }
}
