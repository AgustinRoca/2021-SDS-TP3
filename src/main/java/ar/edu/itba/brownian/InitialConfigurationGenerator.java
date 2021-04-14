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
    public static double SPACE_SIZE = 6;
    public static int PARTICLES_QTY = 140;
    public static double SMALL_RADIUS = 0.2;
    public static double BIG_RADIUS = 0.7;
    public static double SMALL_MASS = 0.9;
    public static double BIG_MASS = 2;
    public static double SMALL_MAX_SPEED = 2;
    public static Position INITIAL_BIG_POSITION = new Position(SPACE_SIZE/2, SPACE_SIZE/2);
    public static Velocity INITIAL_BIG_VELOCITY = new Velocity(0,0);
    private static final String DEFAULT_INPUT_FILENAME = "./data/initialSetup.txt";
    private static final int MAX_ATTEMPTS = 100;


    public static void main(String[] args){
        List<Particle> particles = new ArrayList<>();
        Particle bigParticle = new Particle(BIG_MASS, BIG_RADIUS, INITIAL_BIG_POSITION, INITIAL_BIG_VELOCITY);
        particles.add(bigParticle);

        for(int particleNumber = 0; particleNumber < PARTICLES_QTY; particleNumber++){
            Position possiblePosition = null;
            boolean isValid = false;
            int attempts = 0;
            while(!isValid){
                isValid = true;
                double possibleX = SMALL_RADIUS + Math.random() * (SPACE_SIZE - SMALL_RADIUS);
                double possibleY = SMALL_RADIUS + Math.random() * (SPACE_SIZE - SMALL_RADIUS);
                possiblePosition = new Position(possibleX, possibleY);
                for(Particle particle: particles){
                    if(possiblePosition.getDistanceTo(particle.getPosition()) < SMALL_RADIUS + particle.getRadius()){
                        isValid = false;
                    }
                }
                attempts++;
            }
            if(attempts > MAX_ATTEMPTS){
                throw new RuntimeException("Could not arrange particles");
            }
            double velocityAngle = Math.random() * 2 * Math.PI;
            double speed = Math.random() * SMALL_MAX_SPEED;
            Velocity velocity = new Velocity(speed * Math.cos(velocityAngle), speed * Math.sin(velocityAngle));
            particles.add(new Particle(SMALL_MASS, SMALL_RADIUS, possiblePosition, velocity));
        }

        StringBuilder str = new StringBuilder();
        str.append(SPACE_SIZE).append('\n');
        str.append(PARTICLES_QTY).append('\n');
        for (Particle particle : particles){
            str.append(
                    particle.getPosition().getX()).append(' ').append(particle.getPosition().getY())
                    .append(' ').append(particle.getVelocityX()).append(' ').append(particle.getVelocityY())
                    .append(' ').append(particle.getMass()).append(' ').append(particle.getRadius()).append('\n');
        }

        // check file
        File inputFile = new File(Paths.get(DEFAULT_INPUT_FILENAME).toAbsolutePath().toString());
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
}
