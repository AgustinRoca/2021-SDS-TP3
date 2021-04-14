package ar.edu.itba.brownian.models.collision;

import ar.edu.itba.brownian.models.Particle;

import java.util.List;

public interface Collision extends Comparable<Collision>{

    double getTime();
    List<Particle> getParticlesInvolved();
    List<Long> getParticleCollisionsCount();
    void applyCollision();

    @Override
    default int compareTo(Collision o){
        return Double.compare(getTime(), o.getTime());
    }

    default boolean isValid(){
        List<Particle> particles = getParticlesInvolved();
        List<Long> collisionsCounts = getParticleCollisionsCount();
        for (int i = 0; i < particles.size(); i++) {
            if (particles.get(i).getCollisionQty() != collisionsCounts.get(i))
                return false;
        }
        return true;
    }
}
