import matplotlib.pyplot as plt

TEST_RESULTS_FILEPATH = "../data/speeds.txt"


test_file = open(TEST_RESULTS_FILEPATH, "r")

# initial
initial_interval = float(test_file.readline().strip())
initial = []
initial_total = 0
line = test_file.readline().strip()
while line and line != "":
    line = line.split(":")
    qty = int(line[1])
    initial_total += qty
    initial.append({"group": float(line[0]), "qty": qty})
    line = test_file.readline().strip()

# last third
last_third_interval = float(test_file.readline().strip())
last_third = []
last_third_total = 0
line = test_file.readline().strip()
while line and line != "":
    line = line.split(":")
    qty = int(line[1])
    last_third_total += qty
    last_third.append({"group": float(line[0]), "qty": qty})
    line = test_file.readline().strip()

initial.sort(key=lambda x: x["group"])
initial = list(map(lambda x: {"group": x["group"], "qty": x["qty"], "prob": x["qty"] /
               initial_total, "pdf": x["qty"]/(initial_interval*initial_total)}, initial))

last_third.sort(key=lambda x: x["group"])
last_third = list(map(lambda x: {"group": x["group"], "qty": x["qty"], "prob": x["qty"] /
                  last_third_total, "pdf": x["qty"]/(last_third_interval*last_third_total)}, last_third))

plt.gca().figure.set_size_inches((16, 12))
plt.subplot(1, 2, 1)
plt.title("Distribuciones de probabilidad")
plt.legend(
    [plt.bar(
        x=list(map(lambda x: x["group"], initial)),
        height=list(map(lambda x: x["prob"], initial)),
        width=initial_interval,
        align="edge"
    ),
    plt.bar(
        x=list(map(lambda x: x["group"], last_third)),
        height=list(map(lambda x: x["prob"], last_third)),
        width=last_third_interval,
        align="edge",
        color="none",
        edgecolor="red"
    )],
    ["Iniciales","Último tercio"]
)
plt.subplot(1, 2, 2)
plt.title("PDFs")
plt.legend(
    [plt.plot(
        list(map(lambda x: x["group"]+initial_interval/2, initial)),
        list(map(lambda x: x["pdf"], initial)),
    )[0],
    plt.plot(
        list(map(lambda x: x["group"]+last_third_interval/2, last_third)),
        list(map(lambda x: x["pdf"], last_third)),
    )[0]],
    ["Inicial","Último tercio"]
)

plt.show()

plt.gca().figure.set_size_inches((16, 12))
plt.rcParams.update({'font.size': 22})
plt.title("PDFs")
plt.legend(
    [plt.plot(
        list(map(lambda x: x["group"]+initial_interval/2, initial)),
        list(map(lambda x: x["pdf"], initial)),
    )[0],
    plt.plot(
        list(map(lambda x: x["group"]+last_third_interval/2, last_third)),
        list(map(lambda x: x["pdf"], last_third)),
    )[0]],
    ["Inicial","Último tercio"]
)
plt.show()