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
            for (Particle particle : record.particlesStates){
                str
                        .append(particle.getId()).append(' ')
                        .append(particle.getPosition().getX()).append(' ')
                        .append(particle.getPosition().getY()).append(' ')
                        .append(particle.getVelocityX()).append(' ')
                        .append(particle.getVelocityY()).append('\n');
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

    private static List<SimulationRecord> simulate(Set<Particle> particleSet, double spaceSize){
        // Clone the set to ensure that I am not changing the original Set
        double bigParticleRadius = -1;
        Set<Particle> particles = new HashSet<>();
        for (Particle particle : particleSet){
            if(bigParticleRadius == -1 || particle.getRadius() > bigParticleRadius){
                bigParticleRadius = particle.getRadius();
            }
            particles.add(new Particle(particle));
        }

        List<Collision> possibleCollisions = new LinkedList<>();
        List<SimulationRecord> records = new LinkedList<>();

        // First round: Calculate first collisions of every particle
        for (Particle particle : particles){
            Collision collision = getEarliestCollision(particle, particles, spaceSize);
            if(collision != null) {
                possibleCollisions.add(collision);
            }
        }
        Collections.sort(possibleCollisions); // Sort ascending by time


        double time = 0;
        boolean bigTouchWall = false;
        while(!possibleCollisions.isEmpty() &&  time < MAX_TIME && !bigTouchWall){
            Collision nextCollision = possibleCollisions.get(0);
            possibleCollisions.remove(nextCollision);

            if(nextCollision.isValid()) {
                // Move every particle to current state
                for (Particle particle : particles){
                    particle.moveStraightDuringTime(nextCollision.getTime());
                }
                time += nextCollision.getTime();

                if(isBigParticleAgainstWallCollision(nextCollision, bigParticleRadius)){
                    bigTouchWall = true;
                } else {
                    // Change velocities of involved particles
                    nextCollision.applyCollision();

                    // Update the remaining time of other collisions
                    for (Collision collision : possibleCollisions) {
                        collision.setTime(collision.getTime() - nextCollision.getTime());
                    }

                    // Calculate new possible collisions
                    Set<Particle> particlesToRecalculate = new HashSet<>();
                    for (Particle particle : nextCollision.getParticlesInvolved()) {
                        particlesToRecalculate.add(particle);
                        for (Collision collision : possibleCollisions){
                            if(collision.getParticlesInvolved().size() == 2 && collision.getParticlesInvolved().contains(particle)){
                                for (Particle particleInCollision : collision.getParticlesInvolved()){
                                    if (!particleInCollision.equals(particle)) {
                                        particlesToRecalculate.add(particleInCollision);
                                    }
                                }
                            }
                        }
                    }
                    for (Particle particleToRecalculate : particlesToRecalculate) {
                        Collision newCollision = getEarliestCollision(particleToRecalculate, particles, spaceSize);
                        orderedAdd(possibleCollisions, newCollision);
                    }
                }

                // Record the collision
                Set<Particle> particlesStates = new HashSet<>();
                for (Particle particle : nextCollision.getParticlesInvolved()) {
                    particlesStates.add(new Particle(particle));
                }
                records.add(new SimulationRecord(time, particlesStates));
            }
        }
        return records;
    }

    private static boolean isBigParticleAgainstWallCollision(Collision nextCollision, double bigParticleRadius) {
        boolean isWallCollision = nextCollision.getParticlesInvolved().size() == 1;
        if(isWallCollision){
            Optional<Particle> particleOptional = nextCollision.getParticlesInvolved().stream().findFirst();
            return particleOptional.filter(particle -> particle.getRadius() == bigParticleRadius).isPresent();
        } else {
            return false;
        }
    }

    private static void orderedAdd(List<Collision> collisions, Collision collision) {
        ListIterator<Collision> itr = collisions.listIterator();
        while(true) {
            if (!itr.hasNext()) {
                itr.add(collision);
                return;
            }

            Collision elementInList = itr.next();
            if (elementInList.compareTo(collision) > 0) {
                itr.previous();
                itr.add(collision);
                return;
            }
        }
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

        if(earliestCollision != null && earliestCollision.getTime() < 0){
            earliestCollision.setTime(0);
        }

        return earliestCollision;
    }

    private static Collision earliestParticleCollision(Particle particle, Set<Particle> particles){
        ParticleCollision earliestCollision = null;
        for (Particle otherParticle : particles) {
            if(!particle.equals(otherParticle)) {
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
        private final Set<Particle> particlesStates;

        public SimulationRecord(double time, Set<Particle> particlesStates) {
            this.time = time;
            this.particlesStates = particlesStates;
        }
    }
}
