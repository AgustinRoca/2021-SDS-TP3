import matplotlib.pyplot as plt
from matplotlib.animation import FuncAnimation
import matplotlib.collections as clt
import numpy as np
import dataparser
import os
import argparse
import math

# Constants
DATA_PATH = os.path.join("..", "data")
OUTPUT_PATH = os.path.join(DATA_PATH, "output.txt")

DELTA_TIME = 0.01
SAVE_COUNT = 100
REPEAT_DELAY = 1000
REPEAT = True


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
        circle = plt.Circle((p.x, p.y), p.radius)
        circles.append(circle)
    return circles


def get_annotations(simdata):
    annotations = {}
    for k in simdata.particles:
        p = simdata.particles[k]
        annotations[k] = plt.annotate(k, (p.x, p.y), ha='center', va='center')
    return annotations


def update_annotations(simdata, annotations):
    for k in simdata.particles:
        p = simdata.particles[k]
        annotations[k].set_position((p.x, p.y))


def update_func(frame, *fargs):
    global simdata
    global patch_collection
    global ax
    global annotations
    if frame == 0:
        simdata.restart()
    else:
        simdata.update_particles_on_time()
    update_annotations(simdata, annotations)
    patch_collection.set_paths(get_circles_list(simdata))
    ax.set_title(get_title(simdata), fontdict={'fontsize': 20})
    return patch_collection    


def get_title(simdata):
    return f"Particles:{simdata.particle_count}  Time:{simdata.time:.2f}"


ax = plt.gca()
ax.margins(0.01)
ax.set_title(get_title(simdata), fontdict={'fontsize': 20})
ax.figure.set_size_inches((12, 12))
minor_ticks_x = np.arange(0, simdata.sim_side + 1, 1)
minor_ticks_y = np.arange(0, simdata.sim_side + 1, 1)
ax.set_xticks(minor_ticks_x, minor=True)
ax.set_yticks(minor_ticks_y, minor=True)

patch_collection = clt.PatchCollection(get_circles_list(simdata))
annotations = get_annotations(simdata)
ax.add_collection(patch_collection)

# calcualate save count
last_event = simdata.events[-1]
SAVE_COUNT = math.ceil(last_event.time / simdata.delta_time)

ani = FuncAnimation(
    plt.gcf(), update_func,
    frames=SAVE_COUNT,
    save_count=SAVE_COUNT,
    interval=int(DELTA_TIME*1000),
    repeat_delay=REPEAT_DELAY,
    repeat=REPEAT,
    blit=False
)

plt.axis('scaled')
plt.xlim([0, simdata.sim_side])
plt.ylim([0, simdata.sim_side])
plt.show()
