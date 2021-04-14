package ar.edu.itba.brownian;

import ar.edu.itba.brownian.models.Particle;
import ar.edu.itba.brownian.models.collision.Collision;
import ar.edu.itba.brownian.models.collision.HorizontalWallCollision;
import ar.edu.itba.brownian.models.collision.ParticleCollision;
import ar.edu.itba.brownian.models.collision.VerticalWallCollision;
import ar.edu.itba.brownian.parser.ParseResults;
import ar.edu.itba.brownian.parser.Parser;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class SimulationApp {
    private static final String DEFAULT_INPUT_FILENAME = "./data/initialSetup.txt";
    private static final String DEFAULT_OUTPUT_FILENAME = "./data/output.txt";
    private static final double MAX_TIME = 100;

    public static void main(String[] args) {
        Set<Particle> particles;
        double spaceSize;
        try {
            ParseResults results = Parser.parse(DEFAULT_INPUT_FILENAME);
            particles = results.getParticles();
            spaceSize = results.getSpaceSize();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("File " + DEFAULT_INPUT_FILENAME + " not found");
        }

        StringBuilder str = new StringBuilder();
        str.append(spaceSize).append('\n');
        str.append(particles.size()).append('\n');
        str.append('\n');
        str.append(0);
        for (Particle particle : particles){
            str.append(particle.getPosition().getX()).append(' ').append(particle.getPosition().getY())
                    .append(' ').append(particle.getVelocityX()).append(' ').append(particle.getVelocityY())
                    .append(' ').append(particle.getMass()).append(' ').append(particle.getRadius()).append('\n');
        }
        str.append('\n');
        List<Collision> collisions = simulate(particles, spaceSize);


        for (Collision collision : collisions){
            str.append(collision.getTime()).append('\n');
            for (Particle particle : collision.getParticlesInvolved()){
                str.append(particle.getId()).append(' ').append(particle.getVelocityX()).append(' ').append(particle.getVelocityY()).append('\n');
            }
            str.append('\n');
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(Paths.get(DEFAULT_OUTPUT_FILENAME).toAbsolutePath().toString(), false));
            writer.write(str.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("File " + DEFAULT_INPUT_FILENAME + " not found");
        }

    }

    private static List<Collision> simulate(Set<Particle> particles, double spaceSize){
        List<Collision> possibleCollisions = new LinkedList<>();
        List<Collision> confirmedCollisions = new LinkedList<>();
        for (Particle particle : particles){
            possibleCollisions.add(getEarliestCollision(particle, particles, spaceSize));
        }
        Collections.sort(possibleCollisions);
        while(!possibleCollisions.isEmpty() && possibleCollisions.get(0).getTime() < MAX_TIME){
            Collision nextCollision = possibleCollisions.get(0);
            if(nextCollision.isValid()) {
                nextCollision.applyCollision();
                confirmedCollisions.add(nextCollision);
                for(Particle particle : nextCollision.getParticlesInvolved()) {
                    possibleCollisions.add(getEarliestCollision(particle, particles, spaceSize));
                }
            }
            possibleCollisions.remove(nextCollision);
            Collections.sort(possibleCollisions); // TODO: Se podría agregar ordenado directamente
        }
        return confirmedCollisions;
    }

    private static Collision getEarliestCollision(Particle particle, Set<Particle> particles, double spaceSize) {
        Collision earliestCollision = horizontalWallCollision(particle, spaceSize);
        Collision verticalWallCollision = verticalWallCollision(particle, spaceSize);
        if (verticalWallCollision != null && (earliestCollision == null || earliestCollision.getTime() > verticalWallCollision.getTime())){
            earliestCollision = verticalWallCollision;
        }
        for (Particle otherParticle : particles){
            double collisionTime = particle.timeUntilCollisionWithParticle(otherParticle);
            if(collisionTime != -1 && (earliestCollision == null || collisionTime < earliestCollision.getTime()))
                earliestCollision = new ParticleCollision(collisionTime, particle, otherParticle);
        }
        return earliestCollision;
    }

    private static Collision horizontalWallCollision(Particle particle, double spaceSize) {
        if(particle.getVelocityX() == 0){
            return null;
        } else if(particle.getVelocityX() > 0){
            return new HorizontalWallCollision((spaceSize - particle.getPosition().getX() - particle.getRadius()) / particle.getVelocityX(), particle);
        } else {
            return new HorizontalWallCollision(-1 * (particle.getPosition().getX() - particle.getRadius()) / particle.getVelocityX(), particle);
        }
    }

    private static Collision verticalWallCollision(Particle particle, double spaceSize) {
        if(particle.getVelocityY() == 0){
            return null;
        } else if(particle.getVelocityY() > 0){
            return new VerticalWallCollision((spaceSize - particle.getPosition().getY() - particle.getRadius()) / particle.getVelocityY(), particle);
        } else {
            return new VerticalWallCollision(-1 * (particle.getPosition().getY() - particle.getRadius()) / particle.getVelocityY(), particle);
        }
    }
}
