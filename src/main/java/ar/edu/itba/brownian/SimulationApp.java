package ar.edu.itba.brownian;

import ar.edu.itba.brownian.models.Particle;
import ar.edu.itba.brownian.models.Velocity;
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
    private static final double MAX_TIME = 10;

    public static void main(String[] args) {
        ParseResults results;
        try {
            results = Parser.parse(DEFAULT_INPUT_FILENAME);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("File " + DEFAULT_INPUT_FILENAME + " not found");
        }

        List<SimulationRecord> records = simulate(results.getParticles(), results.getSpaceSize());

        StringBuilder str = new StringBuilder(results.toString());
        for (SimulationRecord record : records){
            str.append(record.time).append('\n');
            for (Integer particleId : record.idToVelocityChange.keySet()){
                str
                        .append(particleId).append(' ')
                        .append(record.idToVelocityChange.get(particleId).getVelocityX()).append(' ')
                        .append(record.idToVelocityChange.get(particleId).getVelocityY()).append('\n');
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

    private static List<SimulationRecord> simulate(Set<Particle> particles, double spaceSize){
        List<Collision> possibleCollisions = new LinkedList<>();
        List<SimulationRecord> records = new LinkedList<>();

        // First round: Calculate first collisions of every particle
        for (Particle particle : particles){
            possibleCollisions.add(getEarliestCollision(particle, particles, spaceSize));
        }
        Collections.sort(possibleCollisions); // Sort ascending by time


        double time = 0;
        while(!possibleCollisions.isEmpty() &&  time < MAX_TIME){
            Collision nextCollision = possibleCollisions.get(0);
            possibleCollisions.remove(nextCollision);

            if(nextCollision.isValid()) {
                // Actualizo las posiciones de todas las particulas
                for (Particle particle : particles){
                    particle.moveStraightDuringTime(nextCollision.getTime());
                }
                time += nextCollision.getTime();

                nextCollision.applyCollision();

                // Le aviso al resto de los choques que paso cierta cantidad de tiempo
                for (Collision collision : possibleCollisions){
                    collision.setTime(collision.getTime() - nextCollision.getTime());
                }

                Map<Integer, Velocity> idToNewVelocity = new HashMap<>();
                for(Particle particle : nextCollision.getParticlesInvolved()) {
                    // Documento las nuevas velocidades que tendrán
                    idToNewVelocity.put(particle.getId(), particle.getVelocity());

                    // Calculo nuevos choques, pero solo los de las particulas involucradas en el choque
                    possibleCollisions.add(getEarliestCollision(particle, particles, spaceSize));
                }
                records.add(new SimulationRecord(time, idToNewVelocity));
                Collections.sort(possibleCollisions); // TODO: Se podría agregar ordenado directamente
            }
        }
        return records;
    }

    private static Collision getEarliestCollision(Particle particle, Set<Particle> particles, double spaceSize) {

        List<Collision> collisions = new ArrayList<>(3);
        collisions.add(horizontalWallCollision(particle, spaceSize));
        collisions.add(verticalWallCollision(particle, spaceSize));
        collisions.add(earliestParticleCollision(particle, particles));
        Collision earliestCollision = null;

        for (Collision collision : collisions) {
            if(collision != null) {
                if(earliestCollision == null || collision.getTime() < earliestCollision.getTime()) {
                    earliestCollision = collision;
                }
            }
        }

        return earliestCollision;
    }

    private static Collision earliestParticleCollision(Particle particle, Set<Particle> particles){
        ParticleCollision earliestCollision = null;
        for (Particle otherParticle : particles) {
            double relativeX = otherParticle.getPosition().getX() - particle.getPosition().getX();
            double relativeY = otherParticle.getPosition().getY() - particle.getPosition().getY();
            double relativeVelocityX = otherParticle.getVelocityX() - particle.getVelocityX();
            double relativeVelocityY = otherParticle.getVelocityY() - particle.getVelocityY();
            double distance = particle.getRadius() + otherParticle.getRadius();

            double velocityDotPosition = relativeX * relativeVelocityX + relativeY * relativeVelocityY;
            double velocityDotVelocity = relativeVelocityX * relativeVelocityX + relativeVelocityY * relativeVelocityY;
            double positionDotPosition = relativeX * relativeX + relativeY * relativeY;
            double d = velocityDotPosition * velocityDotPosition - velocityDotVelocity * (positionDotPosition - distance * distance);

            if (!(velocityDotPosition >= 0 || d < 0)) {
                double collisionTime = -1 * (velocityDotPosition + Math.sqrt(d)) / velocityDotVelocity;

                if (earliestCollision == null || collisionTime < earliestCollision.getTime())
                    earliestCollision = new ParticleCollision(collisionTime, particle, otherParticle);
            }
        }
        return earliestCollision;
    }

    private static Collision verticalWallCollision(Particle particle, double spaceSize) {
        double timeUntilCollision;
        if(particle.getVelocityX() == 0){
            return null;
        } else if(particle.getVelocityX() > 0){
            timeUntilCollision = (spaceSize - particle.getRadius() - particle.getPosition().getX()) / particle.getVelocityX();
        } else {
            timeUntilCollision = (particle.getRadius() - particle.getPosition().getX()) / particle.getVelocityX();
        }
        return new VerticalWallCollision(timeUntilCollision, particle);

    }

    private static Collision horizontalWallCollision(Particle particle, double spaceSize) {
        double timeUntilCollision;
        if(particle.getVelocityY() == 0){
            return null;
        } else if(particle.getVelocityY() > 0){
            timeUntilCollision = (spaceSize - particle.getRadius() - particle.getPosition().getY()) / particle.getVelocityY();
        } else {
            timeUntilCollision = (particle.getRadius() - particle.getPosition().getY()) / particle.getVelocityY();
        }
        return new HorizontalWallCollision(timeUntilCollision, particle);

    }

    private static class SimulationRecord {
        private final double time;
        private final Map<Integer, Velocity> idToVelocityChange;

        public SimulationRecord(double time, Map<Integer, Velocity> idToVelocityChange) {
            this.time = time;
            this.idToVelocityChange = idToVelocityChange;
        }
    }
}
