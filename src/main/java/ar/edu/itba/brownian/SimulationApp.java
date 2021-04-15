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
        Collision earliestCollision = horizontalWallCollision(particle, spaceSize);
        Collision verticalWallCollision = verticalWallCollision(particle, spaceSize);

        if (verticalWallCollision != null && (earliestCollision == null || earliestCollision.getTime() > verticalWallCollision.getTime())){
            earliestCollision = verticalWallCollision;
        }
        if(earliestCollision != null && earliestCollision.getTime() < 0){
        }
        for (Particle otherParticle : particles){
            double collisionTime = particle.timeUntilCollisionWithParticle(otherParticle);
            if(collisionTime != -1 && (earliestCollision == null || collisionTime < earliestCollision.getTime()))
                earliestCollision = new ParticleCollision(collisionTime, particle, otherParticle);
        }
        if(earliestCollision != null && earliestCollision.getTime() < 0)
            earliestCollision.setTime(0);
        return earliestCollision;
    }

    private static Collision verticalWallCollision(Particle particle, double spaceSize) {
        if(particle.getVelocityX() == 0){
            return null;
        } else if(particle.getVelocityX() > 0){
            return new VerticalWallCollision((spaceSize - particle.getPosition().getX() - particle.getRadius()) / particle.getVelocityX(), particle);
        } else {
            return new VerticalWallCollision(-1 * (particle.getPosition().getX() - particle.getRadius()) / particle.getVelocityX(), particle);
        }
    }

    private static Collision horizontalWallCollision(Particle particle, double spaceSize) {
        if(particle.getVelocityY() == 0){
            return null;
        } else if(particle.getVelocityY() > 0){
            return new HorizontalWallCollision((spaceSize - particle.getPosition().getY() - particle.getRadius()) / particle.getVelocityY(), particle);
        } else {
            return new HorizontalWallCollision(-1 * (particle.getPosition().getY() - particle.getRadius()) / particle.getVelocityY(), particle);
        }
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
