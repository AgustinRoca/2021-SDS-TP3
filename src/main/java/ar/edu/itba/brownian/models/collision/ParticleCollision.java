package ar.edu.itba.brownian.models.collision;

import ar.edu.itba.brownian.models.Particle;

import java.util.*;

public class ParticleCollision implements Collision {
    private final List<Particle> particlesInvolved;
    private final List<Long> particlesInvolvedCollisionsCount;
    private final double time;


    public ParticleCollision(double time, Particle particle, Particle otherParticle) {
        particlesInvolved = new ArrayList<>(2);
        particlesInvolved.add(particle);
        particlesInvolved.add(otherParticle);
        particlesInvolvedCollisionsCount = new ArrayList<>(2);
        particlesInvolvedCollisionsCount.add(particle.getCollisionQty());
        particlesInvolvedCollisionsCount.add(otherParticle.getCollisionQty());
        this.time = time;
    }

    @Override
    public double getTime() {
        return time;
    }

    @Override
    public List<Particle> getParticlesInvolved() {
        return particlesInvolved;
    }

    @Override
    public List<Long> getParticleCollisionsCount() {
        return particlesInvolvedCollisionsCount;
    }

    @Override
    public void applyCollision() {
        Iterator<Particle> iterator = particlesInvolved.iterator();
        Particle particle1 = iterator.next();
        Particle particle2 = iterator.next();
        particle1.bounceWithParticle(particle2);
    }
}
