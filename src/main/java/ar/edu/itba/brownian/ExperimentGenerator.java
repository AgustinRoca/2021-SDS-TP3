package ar.edu.itba.brownian;

import ar.edu.itba.brownian.models.Particle;
import ar.edu.itba.brownian.models.Position;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class ExperimentGenerator {
    private static final int ITERATIONS_QTY = 10;
    private static final int PARTICLES_QTY = 140;
    private static final double SPACE_SIZE = 6;
    private static final double MAX_TIME = 10;
    private static final double TIME_INTERVAL = MAX_TIME / 50;
    private static final String DEFAULT_INPUT_FILENAME = "./data/testResults.txt";

    public static void main(String[] args) {
        List<List<Particle>> initialConfigurations = InitialConfigurationGenerator.randomParticlesGenerator(PARTICLES_QTY, SPACE_SIZE, ITERATIONS_QTY);
        System.out.println("Initial configurations generated");
        List<List<SimulationApp.SimulationRecord>> records = new ArrayList<>(ITERATIONS_QTY);
        for (List<Particle> particles : initialConfigurations) {
            records.add(SimulationApp.simulate(particles, SPACE_SIZE, MAX_TIME));
        }

        List<Double> collisionsPerSecond = new ArrayList<>(ITERATIONS_QTY);
        List<Double> speeds = new LinkedList<>();
        List<List<Double>> bigSquaredDisplacements = new ArrayList<>(ITERATIONS_QTY);
        List<List<Double>> smallSquaredDisplacements = new ArrayList<>(ITERATIONS_QTY);
        for (int iterationNumber = 0; iterationNumber < ITERATIONS_QTY; iterationNumber++) {
            System.out.println("Iteration #" + iterationNumber);
            bigSquaredDisplacements.add(new ArrayList<>());
            smallSquaredDisplacements.add(new ArrayList<>());
            Map<Integer, Double> currentSpeeds = new HashMap<>();
            Particle bigParticle = null;
            Particle smallParticle = null;
            Position bigInitialPosition = new Position(SPACE_SIZE/2, SPACE_SIZE/2);
            Position smallInitialPosition = null;
            for (Particle particle : initialConfigurations.get(iterationNumber)){
                if (particle.getId() != 0){
                    currentSpeeds.put(particle.getId(), particle.getSpeed());
                } else {
                    bigParticle = new Particle(particle);
                }

                if(particle.getId() == 1) { // TODO: Choose a particle near the middle
                    smallParticle = new Particle(particle);
                    smallInitialPosition = new Position(smallParticle.getPosition().getX(), smallParticle.getPosition().getY());
                }
            }
            if(bigParticle == null || smallParticle == null){
                throw new RuntimeException("Could not find particles 0 and 1");
            }

            double time = 0;
            for (SimulationApp.SimulationRecord record : records.get(iterationNumber)) {
                double timeRemaining = record.getTime();
                while (time + timeRemaining >= TIME_INTERVAL){
                    bigParticle.moveStraightDuringTime(TIME_INTERVAL - time);
                    smallParticle.moveStraightDuringTime(TIME_INTERVAL - time);
                    double smallDisplacement = smallParticle.getPosition().getDistanceTo(smallInitialPosition);
                    double bigDisplacement = bigParticle.getPosition().getDistanceTo(bigInitialPosition);
                    bigSquaredDisplacements.get(iterationNumber).add(bigDisplacement * bigDisplacement);
                    smallSquaredDisplacements.get(iterationNumber).add(smallDisplacement * smallDisplacement);

                    if(record.getTime() >= MAX_TIME * 2 / 3){
                        double speedSum = 0;
                        for (Integer id : currentSpeeds.keySet()){
                            speedSum += currentSpeeds.get(id);
                        }
                        speeds.add(speedSum / PARTICLES_QTY);
                    }
                    timeRemaining -= TIME_INTERVAL - time;
                    time = 0;
                }
                time += timeRemaining;

                for (Particle particlesInCollision : record.getParticlesStates()){
                    if (particlesInCollision.getId() == bigParticle.getId()) {
                        bigParticle.setVelocity(particlesInCollision.getVelocityX(), particlesInCollision.getVelocityY());
                    } else if (particlesInCollision.getId() == smallParticle.getId()) {
                        smallParticle.setVelocity(particlesInCollision.getVelocityX(), particlesInCollision.getVelocityY());
                    }
                    currentSpeeds.put(particlesInCollision.getId(), particlesInCollision.getSpeed());
                }
            }
            collisionsPerSecond.add(records.get(iterationNumber).size() / MAX_TIME);
        }

        printInFile(collisionsPerSecond, speeds, bigSquaredDisplacements, smallSquaredDisplacements);
    }

    private static void printInFile(List<Double> collisionsPerSecond, List<Double> speeds, List<List<Double>> bigSquaredDisplacements, List<List<Double>> smallSquaredDisplacements){
        StringBuilder str = new StringBuilder();
        for (Double collisionPerSecond : collisionsPerSecond){
            str.append(collisionPerSecond).append(' ');
        }
        str.append('\n').append('\n');

        for (Double speed: speeds){
            str.append(speed).append(' ');
        }
        str.append('\n').append('\n');

        str.append(TIME_INTERVAL).append('\n');
        for (List<Double> iterationBigSquareDisplacement : bigSquaredDisplacements){
            for (Double bigSquareDisplacement : iterationBigSquareDisplacement){
                str.append(bigSquareDisplacement).append(' ');
            }
            str.append('\n');
        }
        str.append('\n');

        for (List<Double> iterationSmallSquareDisplacement : smallSquaredDisplacements){
            for (Double smallSquareDisplacement : iterationSmallSquareDisplacement){
                str.append(smallSquareDisplacement).append(' ');
            }
            str.append('\n');
        }
        str.append('\n');


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
