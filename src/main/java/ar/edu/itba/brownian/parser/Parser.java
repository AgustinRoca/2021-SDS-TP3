package ar.edu.itba.brownian.parser;

import ar.edu.itba.brownian.models.Particle;
import ar.edu.itba.brownian.models.Position;
import ar.edu.itba.brownian.models.Velocity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Parser {
    private static final int PROPERTIES_QTY = 6;

    public static ParseResults parse(String path) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(path));
        int particlesQty = Integer.parseInt(scanner.nextLine().trim());
        Set<Particle> particles = new HashSet<>(particlesQty);
        double spaceSize = Double.parseDouble(scanner.nextLine().trim());
        for(int particleNumber = 0; particleNumber < particlesQty; particleNumber++){
            String[] particleProperties = scanner.nextLine().trim().split(" ", PROPERTIES_QTY);
            double mass = Double.parseDouble(particleProperties[4]);
            double radius = Double.parseDouble(particleProperties[5]);
            Position position = new Position(Double.parseDouble(particleProperties[0]), Double.parseDouble(particleProperties[1]));
            Velocity velocity = new Velocity(Double.parseDouble(particleProperties[2]), Double.parseDouble(particleProperties[3]));
            Particle particle = new Particle(mass, radius, position, velocity);
            particles.add(particle);
        }
        return new ParseResults(particles, spaceSize);
    }
}
