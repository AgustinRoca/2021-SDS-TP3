import matplotlib.pyplot as plt
from matplotlib.path import Path
import matplotlib.patches as patches
import dataparser

simdata = dataparser.parse_output_file("../data/output.txt")
vertices = []
vertices.append((simdata.particles[0].x,simdata.particles[0].y))
for e in simdata.events:
    if e.particles[0].id == 0:
        vertices.append((e.particles[0].x,e.particles[0].y))
    elif len(e.particles) == 2 and e.particles[1].id == 0:
        vertices.append((e.particles[1].x,e.particles[1].y))

path = Path(vertices)

fig, ax = plt.subplots()
patch = patches.PathPatch(path, facecolor="none", lw=2)
fig.set_size_inches((12,12))

ax.add_patch(patch)
ax.set_xlim(0, simdata.sim_side)
ax.set_ylim(0, simdata.sim_side)

plt.show()