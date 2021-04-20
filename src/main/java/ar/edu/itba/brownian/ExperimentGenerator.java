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
    private static final int ITERATIONS_QTY = 100;
    private static final int PARTICLES_QTY = 140;
    private static final double SPACE_SIZE = 6;
    public static final double SMALL_MAX_SPEED_COLD = 1;
    public static final double SMALL_MAX_SPEED_NORMAL = 2;
    public static final double SMALL_MAX_SPEED_HOT = 4;
    private static final double MAX_TIME = 10;
    private static final int INTERVALS_QTY = 50;
    private static final double TIME_INTERVAL = MAX_TIME / INTERVALS_QTY;
    private static final double DELTA_COLLISIONS_TIME = 0.0001;
    private static final double DELTA_SPEED = 0.01;
    private static final String DEFAULT_PATH = "./data/testResults/";
    private static final String DEFAULT_COLLISIONS_FILE = "collisions.txt";
    private static final String DEFAULT_SPEEDS_FILE = "speeds.txt";
    private static final String DEFAULT_TEMPERATURES_FILE = "temperatures.txt";
    private static final String DEFAULT_DCMS_FILE = "dcms.txt";

    public static void main(String[] args) {
        List<List<Particle>> initialConfigurations = InitialConfigurationGenerator.randomParticlesGenerator(PARTICLES_QTY, SPACE_SIZE, ITERATIONS_QTY, SMALL_MAX_SPEED_NORMAL);
        System.out.println("Initial configurations generated");
        List<List<SimulationApp.SimulationRecord>> records = new ArrayList<>(ITERATIONS_QTY);
        for (List<Particle> particles : initialConfigurations) {
            records.add(SimulationApp.simulate(particles, SPACE_SIZE, MAX_TIME));
        }
        List<List<SimulationApp.SimulationRecord>> differentTemperaturesRecords = new ArrayList<>(3);
        List<List<Particle>> initialTemperatureConfigurations = new ArrayList<>(3);
        initialTemperatureConfigurations.add(InitialConfigurationGenerator.randomParticlesGenerator(PARTICLES_QTY, SPACE_SIZE, 1, SMALL_MAX_SPEED_COLD).get(0));
        initialTemperatureConfigurations.add(InitialConfigurationGenerator.randomParticlesGenerator(PARTICLES_QTY, SPACE_SIZE, 1, SMALL_MAX_SPEED_NORMAL).get(0));
        initialTemperatureConfigurations.add(InitialConfigurationGenerator.randomParticlesGenerator(PARTICLES_QTY, SPACE_SIZE, 1, SMALL_MAX_SPEED_HOT).get(0));
        for (List<Particle> particles : initialTemperatureConfigurations) {
            differentTemperaturesRecords.add(SimulationApp.simulate(particles, SPACE_SIZE, MAX_TIME));
        }

        ExperimentResults experimentResults = analyze(initialConfigurations, records, differentTemperaturesRecords);

        printInFiles(experimentResults);
    }

    private static ExperimentResults analyze(List<List<Particle>> initialConfigurations, List<List<SimulationApp.SimulationRecord>> records, List<List<SimulationApp.SimulationRecord>> differentTemperatureRecords) {
        ExperimentResults results = new ExperimentResults();

        // Collisions per second
        for (int iterationNumber = 0; iterationNumber < ITERATIONS_QTY; iterationNumber++) {
            double lastTime = 0;
            for (SimulationApp.SimulationRecord record : records.get(iterationNumber)){
                results.timesBetweenCollisions.add(record.getTime() - lastTime);
                lastTime = record.getTime();
            }
            results.collisionsPerSecond.add(records.get(iterationNumber).size() / MAX_TIME);
        }

        // Speeds
        for (List<Particle> configuration: initialConfigurations) {
            for (Particle particle : configuration) {
                results.initialSpeeds.add(particle.getSpeed());
            }
        }

        for (int iterationNumber = 0; iterationNumber < ITERATIONS_QTY; iterationNumber++) {
            // Initialize map
            Map<Integer, Double> currentSpeeds = new HashMap<>();
            for (Particle particle : initialConfigurations.get(iterationNumber)){
                if (particle.getId() != 0){
                    currentSpeeds.put(particle.getId(), particle.getSpeed());
                }
            }
            // Record only last third
            for (SimulationApp.SimulationRecord record : records.get(iterationNumber)) {
                if(record.getTime() >= MAX_TIME * 2 / 3){
                    double speedSum = 0;
                    for (Integer id : currentSpeeds.keySet()){
                        if(id != 0) {
                            speedSum += currentSpeeds.get(id);
                        }
                    }
                    results.speeds.add(speedSum / PARTICLES_QTY);
                }
            }
        }

        // Different temperatures
        int temperatureNumber = 0;
        for (List<SimulationApp.SimulationRecord> temperatureRecord : differentTemperatureRecords){
            results.temperatureBigPositions.add(new ArrayList<>());
            for(SimulationApp.SimulationRecord record : temperatureRecord){
                for (Particle particle : record.getParticlesStates()) {
                    if(particle.getId() == 0) {
                        results.temperatureBigPositions.get(temperatureNumber).add(particle.getPosition());
                    }
                }
            }
            temperatureNumber++;
        }

        // Follow big and small particle
        for (int iterationNumber = 0; iterationNumber < ITERATIONS_QTY; iterationNumber++) {
            results.bigSquaredDisplacements.add(new ArrayList<>());
            results.smallSquaredDisplacements.add(new ArrayList<>());
            Particle bigParticle = null;
            Particle smallParticle = null;
            Position bigInitialPosition = null;
            Position smallInitialPosition = null;
            for (Particle particle : initialConfigurations.get(iterationNumber)){
                if (particle.getId() == 0) {
                    bigParticle = new Particle(particle);
                    bigInitialPosition = new Position(bigParticle.getPosition().getX(), bigParticle.getPosition().getY());
                }

                if(particle.getId() == chooseParticleIdToFollow(initialConfigurations.get(iterationNumber))) {
                    smallParticle = new Particle(particle);
                    smallInitialPosition = new Position(smallParticle.getPosition().getX(), smallParticle.getPosition().getY());
                }
            }

            if(bigParticle == null || smallParticle == null){
                throw new RuntimeException("Could not find particles 0 and 1");
            }

            double time = 0;
            double timeRemaining = 0;
            boolean smallTouchedBorder = false;
            for (SimulationApp.SimulationRecord record : records.get(iterationNumber)) {
                timeRemaining += record.getTime() - time;
                time = record.getTime();
                bigParticle.moveStraightDuringTime(timeRemaining);
                smallParticle.moveStraightDuringTime(timeRemaining);

                while (timeRemaining >= TIME_INTERVAL) {
                    if(time >= MAX_TIME / 2) {
                        double bigDisplacement = bigParticle.getPosition().getDistanceTo(bigInitialPosition);
                        results.bigSquaredDisplacements.get(iterationNumber).add(bigDisplacement * bigDisplacement);

                        if(!smallTouchedBorder) {
                            double smallDisplacement = smallParticle.getPosition().getDistanceTo(smallInitialPosition);
                            results.smallSquaredDisplacements.get(iterationNumber).add(smallDisplacement * smallDisplacement);
                        }
                    }
                    timeRemaining -= TIME_INTERVAL;
                }

                for (Particle particlesInCollision : record.getParticlesStates()){
                    if (particlesInCollision.getId() == bigParticle.getId()) {
                        bigParticle.setVelocity(particlesInCollision.getVelocityX(), particlesInCollision.getVelocityY());
                    } else if (particlesInCollision.getId() == smallParticle.getId()) {
                        smallParticle.setVelocity(particlesInCollision.getVelocityX(), particlesInCollision.getVelocityY());
                        if(record.getParticlesStates().size() == 1){
                            smallTouchedBorder = true;
                        }
                    }
                }
            }
        }
        return results;
    }

    private static int chooseParticleIdToFollow(List<Particle> particles) { // TODO: Choose a particle near the middle
        return 1;
    }

    private static void printInFiles(ExperimentResults results) {
        // Collisions per second
        StringBuilder collisionsPerSecondStr = new StringBuilder();
        for (Double collisionsPerSecond : results.collisionsPerSecond) {
            collisionsPerSecondStr.append(collisionsPerSecond).append(' ');
        }
        collisionsPerSecondStr.append('\n');
        Map<Double, Integer> collisionTimeHistogram = new HashMap<>();
        for (Double collisionTime : results.timesBetweenCollisions) {
            double correspondingGroup = Math.floor(collisionTime / DELTA_COLLISIONS_TIME) * DELTA_COLLISIONS_TIME;
            collisionTimeHistogram.put(correspondingGroup, collisionTimeHistogram.getOrDefault(correspondingGroup, 0) + 1);
        }
        collisionsPerSecondStr.append(DELTA_COLLISIONS_TIME).append('\n');
        for (Double group : collisionTimeHistogram.keySet()) {
            collisionsPerSecondStr.append(String.format(Locale.US, "%.4f", group)).append(':').append(collisionTimeHistogram.get(group)).append('\n');
        }

        printStrInFile(DEFAULT_PATH + DEFAULT_COLLISIONS_FILE, collisionsPerSecondStr.toString());

        // Speeds
        StringBuilder speedsStr = new StringBuilder();
        Map<Double, Integer> initialSpeedTimeHistogram = new HashMap<>();
        for (Double initialSpeed : results.initialSpeeds) {
            double correspondingGroup = Math.floor(initialSpeed / DELTA_SPEED) * DELTA_SPEED;
            initialSpeedTimeHistogram.put(correspondingGroup, initialSpeedTimeHistogram.getOrDefault(correspondingGroup, 0) + 1);
        }
        speedsStr.append(DELTA_SPEED).append('\n');
        for (Double group : initialSpeedTimeHistogram.keySet()) {
            speedsStr.append(String.format(Locale.US, "%.2f", group)).append(':').append(initialSpeedTimeHistogram.get(group)).append('\n');
        }
        speedsStr.append('\n');

        Map<Double, Integer> speedTimeHistogram = new HashMap<>();
        for (Double speed : results.speeds) {
            double correspondingGroup = Math.floor(speed / DELTA_SPEED) * DELTA_SPEED;
            speedTimeHistogram.put(correspondingGroup, speedTimeHistogram.getOrDefault(correspondingGroup, 0) + 1);
        }
        speedsStr.append(DELTA_SPEED).append('\n');
        for (Double group : speedTimeHistogram.keySet()) {
            speedsStr.append(String.format(Locale.US, "%.2f", group)).append(':').append(speedTimeHistogram.get(group)).append('\n');
        }
        speedsStr.append('\n');

        printStrInFile(DEFAULT_PATH + DEFAULT_SPEEDS_FILE, speedsStr.toString());

        // Temperatures
        StringBuilder temperaturesStr = new StringBuilder();
        temperaturesStr.append(SPACE_SIZE).append('\n');
        for (List<Position> temperaturePositions : results.temperatureBigPositions){
            for (Position position : temperaturePositions){
                temperaturesStr.append(position.getX()).append(':').append(position.getY()).append(' ');
            }
            temperaturesStr.append('\n');
        }

        printStrInFile(DEFAULT_PATH + DEFAULT_TEMPERATURES_FILE, temperaturesStr.toString());

        // DCMs
        List<Double> bigDcms = new ArrayList<>(INTERVALS_QTY);
        for (int t = 0; t < results.bigSquaredDisplacements.get(0).size(); t++) {
            bigDcms.add(0.0);
        }

        for (int iteration = 0; iteration < ITERATIONS_QTY; iteration++) {
            for (int t = 0; t < results.bigSquaredDisplacements.get(iteration).size(); t++) {
                bigDcms.set(t, bigDcms.get(t) + results.bigSquaredDisplacements.get(iteration).get(t));
            }
        }

        for (int t = 0; t < bigDcms.size(); t++) {
            bigDcms.set(t, bigDcms.get(t) / ITERATIONS_QTY);
        }

        List<Double> smallDcms = new ArrayList<>(INTERVALS_QTY);
        for (int t = 0; t < results.bigSquaredDisplacements.get(0).size(); t++) {
            smallDcms.add(0.0);
        }

        for (int iteration = 0; iteration < ITERATIONS_QTY; iteration++) {
            for (int t = 0; t < results.smallSquaredDisplacements.get(iteration).size(); t++) {
                smallDcms.set(t, smallDcms.get(t) + results.smallSquaredDisplacements.get(iteration).get(t));
            }
        }

        for (int t = 0; t < smallDcms.size(); t++) {
            smallDcms.set(t, smallDcms.get(t) / ITERATIONS_QTY);
        }

        StringBuilder dcmStr = new StringBuilder();
        dcmStr.append(TIME_INTERVAL).append(' ').append(MAX_TIME / 2).append('\n');
        for (Double bigDcm : bigDcms) {
            dcmStr.append(bigDcm).append(' ');
        }
        dcmStr.append('\n');
        for (Double smallDcm : smallDcms) {
            dcmStr.append(smallDcm).append(' ');
        }
        dcmStr.append('\n');

        printStrInFile(DEFAULT_PATH + DEFAULT_DCMS_FILE, dcmStr.toString());
    }

    private static void printStrInFile(String path, String str) {
        File inputFile = new File(Paths.get(path).toAbsolutePath().toString());
        if(!inputFile.getParentFile().exists()){
            if(!inputFile.getParentFile().mkdirs()){
                System.err.println("Input's folder does not exist and could not be created");
                System.exit(1);
            }
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(Paths.get(path).toAbsolutePath().toString(), false));
            writer.write(str);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static class ExperimentResults {
        private final List<Double> collisionsPerSecond = new ArrayList<>(ITERATIONS_QTY);
        private final List<Double> timesBetweenCollisions = new LinkedList<>();
        private final List<Double> speeds = new LinkedList<>();
        private final List<Double> initialSpeeds = new LinkedList<>();
        private final List<List<Position>> temperatureBigPositions = new ArrayList<>(3);
        private final List<List<Double>> bigSquaredDisplacements = new ArrayList<>(ITERATIONS_QTY);
        private final List<List<Double>> smallSquaredDisplacements = new ArrayList<>(ITERATIONS_QTY);
    }
}
