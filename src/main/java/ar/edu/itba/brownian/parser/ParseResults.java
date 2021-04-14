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
}
