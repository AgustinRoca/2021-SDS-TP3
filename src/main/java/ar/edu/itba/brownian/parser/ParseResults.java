package ar.edu.itba.brownian.parser;


import ar.edu.itba.brownian.models.Particle;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class ParseResults {
    private final SortedSet<Particle> particles;
    private final double spaceSize;

    public ParseResults(Set<Particle> particles, double spaceSize) {
        this.particles = new TreeSet<>(particles);
        this.spaceSize = spaceSize;
    }

    public Set<Particle> getParticles() {
        return particles;
    }

    public double getSpaceSize() {
        return spaceSize;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(spaceSize).append('\n');
        str.append(particles.size()).append('\n');
        str.append('\n');
        str.append(0).append('\n');
        for (Particle particle : particles){
            str.append(particle.getId()).append(' ')
                    .append(particle.getPosition().getX()).append(' ').append(particle.getPosition().getY())
                    .append(' ').append(particle.getVelocityX()).append(' ').append(particle.getVelocityY())
                    .append(' ').append(particle.getMass()).append(' ').append(particle.getRadius()).append('\n');
        }
        str.append('\n');
        return str.toString();
    }
}
