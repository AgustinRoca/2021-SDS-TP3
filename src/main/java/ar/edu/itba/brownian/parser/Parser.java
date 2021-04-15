package ar.edu.itba.brownian.parser;

import ar.edu.itba.brownian.models.Particle;
import ar.edu.itba.brownian.models.Position;
import ar.edu.itba.brownian.models.Velocity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Parser {
    private static final int PROPERTIES_QTY = 7;

    public static ParseResults parse(String path) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(path));
        double spaceSize = Double.parseDouble(scanner.nextLine().trim());
        int particlesQty = Integer.parseInt(scanner.nextLine().trim());
        Set<Particle> particles = new HashSet<>(particlesQty);
        for(int particleNumber = 0; particleNumber < particlesQty; particleNumber++){
            String[] particleProperties = scanner.nextLine().trim().split(" ", PROPERTIES_QTY);
            double mass = Double.parseDouble(particleProperties[ParticleProperties.MASS.order]);
            double radius = Double.parseDouble(particleProperties[ParticleProperties.RADIUS.order]);
            Position position = new Position(Double.parseDouble(particleProperties[ParticleProperties.POSITION_X.order]), Double.parseDouble(particleProperties[ParticleProperties.POSITION_Y.order]));
            Velocity velocity = new Velocity(Double.parseDouble(particleProperties[ParticleProperties.VELOCITY_X.order]), Double.parseDouble(particleProperties[ParticleProperties.VELOCITY_Y.order]));
            Particle particle = new Particle(particleNumber, mass, radius, position, velocity);
            particles.add(particle);
        }
        return new ParseResults(particles, spaceSize);
    }

    private enum ParticleProperties {
        ID(0),
        POSITION_X(1),
        POSITION_Y(2),
        VELOCITY_X(3),
        VELOCITY_Y(4),
        MASS(5),
        RADIUS(6);

        private final int order;

        ParticleProperties(int order) {
            this.order = order;
        }

        public int getOrder() {
            return order;
        }
    }
}
