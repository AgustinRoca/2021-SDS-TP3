package ar.edu.itba.brownian.models.collision;

import ar.edu.itba.brownian.models.Particle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class Collision implements Comparable<Collision>, CollisionApplier{
    private double time;
    private final Map<Particle, Long> particleToCollisionsMap = new HashMap<>();

    public Collision(double time, List<Particle> particlesInvolved) {
        this.time = time;
        for (Particle particle : particlesInvolved) {
            particleToCollisionsMap.put(particle, particle.getCollisionQty());
        }
    }

    public double getTime(){
        return time;
    }

    public void setTime(double time){
        this.time = time;
    }

    public Set<Particle> getParticlesInvolved(){
        return particleToCollisionsMap.keySet();
    }

    @Override
    public int compareTo(Collision o){
        return Double.compare(getTime(), o.getTime());
    }

    public boolean isValid(){
        for (Particle particle : particleToCollisionsMap.keySet()){
            if(particle.getCollisionQty() != particleToCollisionsMap.get(particle))
                return false;
        }
        return true;
    }
}
