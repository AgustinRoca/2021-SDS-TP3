import matplotlib.pyplot as plt
from matplotlib.path import Path
import matplotlib.patches as patches
from matplotlib import cm

temperature_file = open("../data/testResults/temperatures.txt","r")

sim_side = float(temperature_file.readline().strip())

# even temperatures are for the big one and odd ones are for the small one
tpaths = []
line = temperature_file.readline().strip()
while line and line != '':
    line = line.split(" ")
    vertices = []
    for vertex in line:
        vertex = vertex.split(":")
        vertices.append((float(vertex[0]),float(vertex[1])))
    tpaths.append(Path(vertices,closed=False))
    line = temperature_file.readline().strip()

ax = plt.gca()
ax.figure.set_size_inches((12,12))

total_temperatures = len(tpaths)
color_mapper = cm.get_cmap("nipy_spectral", total_temperatures + 4)
my_patches = []
my_labels = ["Fria", "Intermedia", "Caliente"]
for i,p in enumerate(tpaths):
    pat = patches.PathPatch(p, facecolor="none", edgecolor=color_mapper(2*i + 1), lw=1)
    ax.add_patch(pat)
    my_patches.append(pat)
    # my_labels.append( "Temperatura " + str(i) )
ax.legend(my_patches, my_labels, prop={"size":16})
plt.axis('scaled')
plt.xlim([0, sim_side])
plt.ylim([0, sim_side])
plt.xlabel("X (m)", fontsize=16)
plt.ylabel("Y (m)", fontsize=16)
plt.setp(ax.get_xticklabels(), fontsize=16)
plt.setp(ax.get_yticklabels(), fontsize=16)
plt.show()