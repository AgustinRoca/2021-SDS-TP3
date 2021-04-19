JAVA_SOURCES = $(shell find src -name *.java)
OUT_FOLDER = out

compile:$(JAVA_SOURCES)
	mkdir -p $(OUT_FOLDER)
	javac -d $(OUT_FOLDER) $(JAVA_SOURCES)

run:compile
	java -cp $(OUT_FOLDER) ar.edu.itba.brownian.SimulationApp

experiment:compile
	java -cp $(OUT_FOLDER) ar.edu.itba.brownian.ExperimentGenerator

init:compile
	java -cp $(OUT_FOLDER) ar.edu.itba.brownian.InitialConfigurationGenerator

visualizer:compile run
	cd visualization
	bash -c "source .env/bin/activate; python visualizer.py"

test_graphs_without_java:
	cd visualization
	bash -c "source .env/bin/activate; python test_graphs.py"

test_graphs:compile experiment test_graphs_without_java
