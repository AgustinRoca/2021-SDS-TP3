package ar.edu.itba.brownian.parser;

import ar.edu.itba.brownian.models.Particle;
import ar.edu.itba.brownian.models.Position;
import ar.edu.itba.brownian.models.Velocity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Parser {
    public static ParseResults parse(String path) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(path));
        double spaceSize = Double.parseDouble(scanner.nextLine().trim());
        int particlesQty = Integer.parseInt(scanner.nextLine().trim());
        Set<Particle> particles = new HashSet<>(particlesQty);
        for(int particleNumber = 0; particleNumber < particlesQty; particleNumber++){
            String[] particleProperties = scanner.nextLine().trim().split(" ", ParticleProperties.values().length);
            double mass = Double.parseDouble(particleProperties[ParticleProperties.MASS.ordinal()]);
            double radius = Double.parseDouble(particleProperties[ParticleProperties.RADIUS.ordinal()]);
            Position position = new Position(Double.parseDouble(particleProperties[ParticleProperties.POSITION_X.ordinal()]), Double.parseDouble(particleProperties[ParticleProperties.POSITION_Y.ordinal()]));
            Velocity velocity = new Velocity(Double.parseDouble(particleProperties[ParticleProperties.VELOCITY_X.ordinal()]), Double.parseDouble(particleProperties[ParticleProperties.VELOCITY_Y.ordinal()]));
            Particle particle = new Particle(particleNumber, mass, radius, position, velocity);
            particles.add(particle);
        }
        return new ParseResults(particles, spaceSize);
    }

    private enum ParticleProperties {
        ID,
        POSITION_X,
        POSITION_Y,
        VELOCITY_X,
        VELOCITY_Y,
        MASS,
        RADIUS
    }
}
