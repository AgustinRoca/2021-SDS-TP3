import matplotlib.pyplot as plt


TEST_RESULTS_FILEPATH = "../data/collisions.txt"


test_file = open(TEST_RESULTS_FILEPATH,"r")

# collisions per second
cps = map(float,test_file.readline().strip().split(" "))
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
    height=list(map(lambda x:x["qty"],groups)),
    width=interval,
    align="edge"
    )
plt.show()