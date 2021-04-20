import matplotlib.pyplot as plt

FILE_PATH = '../data/testResults.txt'
SEPARATOR = ' '
TEST_TYPE_SEPARATOR = '\n'


f = open(FILE_PATH, 'r')
collisions_per_second = []
speeds = []
big_squared_displacements = []
small_squared_displacements = []

collisions_per_second_strings = f.readline().strip().split(SEPARATOR)
for collisions_per_second_string in collisions_per_second_strings:
    collisions_per_second.append(float(collisions_per_second_string))

f.readline()

speeds_strings = f.readline().strip().split(SEPARATOR)
for speed_string in speeds_strings:
    speeds.append(float(speed_string))

f.readline()

time_interval = float(f.readline().strip())
line = f.readline()
while line != '\n':
    big_squared_displacement_strings = line.strip().split(SEPARATOR)
    for big_squared_displacement_string in big_squared_displacement_strings:
        big_squared_displacements.append(float(big_squared_displacement_string))
    line = f.readline()

line = f.readline()
while line != '\n':
    small_squared_displacement_strings = line.strip().split(SEPARATOR)
    for small_squared_displacement_string in small_squared_displacement_strings:
        small_squared_displacements.append(float(small_squared_displacement_string))
    line = f.readline()

