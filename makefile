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

# You should run run before any of this commands
visualizer:
	cd visualization
	bash -c "source .env/bin/activate; python visualizer.py"

# You should run experiment before any of this commands
graph1:
	cd visualization
	bash -c "source .env/bin/activate; python graph1.py"
graph2:
	cd visualization
	bash -c "source .env/bin/activate; python graph2.py"
graph3:
	cd visualization
	bash -c "source .env/bin/activate; python graph3.py"
graph4:
	cd visualization
	bash -c "source .env/bin/activate; python graph4.py"
