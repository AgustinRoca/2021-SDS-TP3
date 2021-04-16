import matplotlib.pyplot as plt
from matplotlib.animation import FuncAnimation
import matplotlib.collections as clt
import numpy as np
import dataparser
import os
import argparse

# Constants
DATA_PATH = os.path.join("..", "data")
OUTPUT_PATH = os.path.join(DATA_PATH, "output.txt")

DELTA_TIME = 0.01
SAVE_COUNT = 100


argp = argparse.ArgumentParser(description="Particle collision visualizer")
argp.add_argument('--output-file-path', dest='output_path',
                  help='path to output of sim')

args = argp.parse_args()

if args.output_path:
    OUTPUT_PATH = args.output_path

print("Parsing data")
simdata = dataparser.parse_output_file(OUTPUT_PATH)
simdata.delta_time = DELTA_TIME
print("Data parsed")


def get_circles_list(simdata):
    circles = []
    for k in simdata.particles:
        p = simdata.particles[k]
        circles.append(plt.Circle((p.x, p.y), p.radius))
    return circles

def update_func(time, *fargs):
    global simdata
    global patch_collection
    global ax
    simdata.update_particles_on_time()
    patch_collection.set_paths(get_circles_list(simdata))
    ax.set_title(f"Particles:{simdata.particle_count}  Time:{simdata.time}", fontdict={'fontsize': 20})
    return patch_collection    

def gen_time(delta_time):
    time=0
    while True:
        time += delta_time
        yield time

ax=plt.gca()
ax.margins(0.01)
ax.set_title(f"Particles:{simdata.particle_count}  Time:{simdata.time}", fontdict={'fontsize': 20})
ax.figure.set_size_inches((12, 12))
minor_ticks_x = np.arange(0, simdata.sim_side + 1, 1)
minor_ticks_y = np.arange(0, simdata.sim_side + 1, 1)
ax.set_xticks(minor_ticks_x, minor=True)
ax.set_yticks(minor_ticks_y, minor=True)



patch_collection=clt.PatchCollection(get_circles_list(simdata))
ax.add_collection(patch_collection)

ani=FuncAnimation(
    plt.gcf(), update_func,
    frames = lambda: gen_time(DELTA_TIME),
    save_count = SAVE_COUNT,
    interval = int(DELTA_TIME*1000),
    blit=False
)
plt.show()
