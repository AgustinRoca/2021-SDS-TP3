import matplotlib.pyplot as plt
import statistics as stats

TEST_RESULTS_FILEPATH = "../data/testResults/collisions.txt"


test_file = open(TEST_RESULTS_FILEPATH,"r")

# collisions per second
cps = list(map(float,test_file.readline().strip().split(" ")))
# group interval time bettween collisions probability
interval = float(test_file.readline().strip())
# groups of time bettwen collisions probability
groups = []
# Total collisions
total_collisions = 0

# iterate over groups
line = test_file.readline().strip()
while line and line != "":
    line = line.split(":")
    qty = int(line[1])
    total_collisions += qty
    groups.append({"group":float(line[0]),"qty":qty})
    line = test_file.readline().strip()

groups.sort(key=lambda x:x["group"])
groups = list(map(lambda x:{"group":x["group"],"qty":x["qty"],"prob":x["qty"]/total_collisions},groups))

plt.gca().figure.set_size_inches((12, 12))

plt.bar(
    x=list(map(lambda x:x["group"],groups)),
    height=list(map(lambda x:x["prob"],groups)),
    width=interval,
    align="edge"
    )

# TODO: calculate frequency and display it
cps_mean = stats.mean(cps)
cps_error = stats.stdev(cps)
print(f"cps mean;{cps_mean}")
print(f"cps error:{cps_error}")

plt.show()