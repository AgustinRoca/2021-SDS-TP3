package ar.edu.itba.brownian;

import ar.edu.itba.brownian.models.Particle;
import ar.edu.itba.brownian.models.Position;
import ar.edu.itba.brownian.models.Velocity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class InitialConfigurationGenerator {
    public static final double SPACE_SIZE = 6;
    public static final int PARTICLES_QTY = 147;
    public static final double SMALL_RADIUS = 0.2;
    public static final double BIG_RADIUS = 0.7;
    public static final double SMALL_MASS = 0.9;
    public static final double BIG_MASS = 2;
    public static final double SMALL_MAX_SPEED = 2;
    public static final Position INITIAL_BIG_POSITION = new Position(SPACE_SIZE/2, SPACE_SIZE/2);
    public static final Velocity INITIAL_BIG_VELOCITY = new Velocity(0,0);
    private static final String DEFAULT_INPUT_FILENAME = "./data/initialSetup.txt";
    private static final long MAX_ATTEMPTS = 10_000_000L;


    public static void main(String[] args){
        List<Particle> particles = null;
        boolean done = false;
        for (int attempt = 0; attempt < 1000 && !done; attempt++) {
            try {
                particles = randomParticlesGenerator(PARTICLES_QTY, SPACE_SIZE);
                done = true;
            } catch (RuntimeException e){
                System.out.println("Attempt #" + attempt + ": Failed");
                if (attempt == (1000 - 1)){
                    throw new RuntimeException("Could not arrange particles");
                }
            }
        }
        printInFile(particles, SPACE_SIZE, DEFAULT_INPUT_FILENAME);
    }

    private static void printInFile(List<Particle> particles, double spaceSize, String filename) {
        StringBuilder str = new StringBuilder();
        str.append(spaceSize).append('\n');
        str.append(particles.size()).append('\n');
        for (Particle particle : particles){
            str.append(particle.toFileString());
        }

        // check file
        File inputFile = new File(Paths.get(filename).toAbsolutePath().toString());
        if(!inputFile.getParentFile().exists()){
            if(!inputFile.getParentFile().mkdirs()){
                System.err.println("Input's folder does not exist and could not be created");
                System.exit(1);
            }
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(Paths.get(DEFAULT_INPUT_FILENAME).toAbsolutePath().toString(), false));
            writer.write(str.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Particle> randomParticlesGenerator(long particleQty, double spaceSize){
        List<Particle> particles = new ArrayList<>();
        Particle bigParticle = new Particle(0, BIG_MASS, BIG_RADIUS, INITIAL_BIG_POSITION, INITIAL_BIG_VELOCITY);
        particles.add(bigParticle);

        for(int particleNumber = 0; particleNumber < particleQty; particleNumber++){
            Position possiblePosition = null;
            boolean isValid = false;
            int attempts = 0;
            while(!isValid){
                isValid = true;
                double possibleX = SMALL_RADIUS + Math.random() * (spaceSize - 2*SMALL_RADIUS);
                double possibleY = SMALL_RADIUS + Math.random() * (spaceSize - 2*SMALL_RADIUS);
                possiblePosition = new Position(possibleX, possibleY);
                for(Particle particle: particles){
                    if(possiblePosition.getDistanceTo(particle.getPosition()) < SMALL_RADIUS + particle.getRadius()){
                        isValid = false;
                    }
                }
                attempts++;
                if(attempts > MAX_ATTEMPTS){
                    throw new RuntimeException("Could not arrange particles");
                }
            }

            double velocityAngle = Math.random() * 2 * Math.PI;
            double speed = Math.random() * SMALL_MAX_SPEED;
            Velocity velocity = new Velocity(speed * Math.cos(velocityAngle), speed * Math.sin(velocityAngle));
            particles.add(new Particle(particles.size(), SMALL_MASS, SMALL_RADIUS, possiblePosition, velocity));
            if((particleNumber % (particleQty/10)) == 0){
                System.out.println(particleNumber + "/" + particleQty + " = " + (double) particleNumber / particleQty * 100 + "%");
            }
        }
        System.out.println(particleQty + "/" + particleQty + " = 100%");
        return particles;
    }

}
