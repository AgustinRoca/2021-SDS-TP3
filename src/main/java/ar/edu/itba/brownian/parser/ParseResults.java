package ar.edu.itba.brownian.parser;


import ar.edu.itba.brownian.models.Particle;

import java.util.Set;

public class ParseResults {
    private final Set<Particle> particles;
    private final double spaceSize;

    public ParseResults(Set<Particle> particles, double spaceSize) {
        this.particles = particles;
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
            str.append(particle.getPosition().getX()).append(' ').append(particle.getPosition().getY())
                    .append(' ').append(particle.getVelocityX()).append(' ').append(particle.getVelocityY())
                    .append(' ').append(particle.getMass()).append(' ').append(particle.getRadius()).append('\n');
        }
        str.append('\n');
        return str.toString();
    }
}
