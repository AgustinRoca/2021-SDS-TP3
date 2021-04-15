class SimData:
    def __init__(self, sim_side=0, particle_count=0, particles={}, events=[]):
        self.sim_side = sim_side
        self.particle_count = particle_count
        self.particles = particles
        self.events = events

    def __repr__(self):
        return f"{{ sim_side:{self.sim_side}, particle_count:{self.particle_count},\n particles:{self.particles}, \n events:{self.events} }}"


class CollideEvent:
    def __init__(self, time=0, particles=[]):
        self.time = time
        self.particles = particles

    def __repr__(self):
        return f"{{time:{self.time}, particles:{self.particles}}}"


class Particle:
    def __init__(self, id=0, x=0, y=0, vx=0, vy=0, mass=0, radius=0):
        self.id = id
        self.x = x
        self.y = y
        self.vx = vx
        self.vy = vy
        self.radius = radius
        self.mass = mass

    def __repr__(self):
        return f"{{id:{self.id}, x:{self.x}, y:{self.y}, vx:{self.vx}, vy:{self.vy}, radius:{self.radius}, mass:{self.mass}}}"


def parse_output_file(output_filepath):
    simdata = SimData()
    ofile = open(output_filepath, "r")

    simdata.sim_side = float(ofile.readline())
    simdata.particle_count = int(ofile.readline())

    ofile.readline()
    ofile.readline()

    event = CollideEvent()

    line = ofile.readline()
    while line != "\n" and line != "":
        line = line.strip().split(" ")
        particle = Particle(
            int(line[0]),
            float(line[1]), float(line[2]),
            float(line[3]), float(line[4]),
            float(line[5]), float(line[6])
        )
        event.particles.append(particle)
        simdata.particles[particle.id] = particle
        line = ofile.readline()
    simdata.events.append(event)
    print(line)
    line = ofile.readline()
    while line and line != '\n' and line != '':
        event = CollideEvent(time=float(line))
        line = ofile.readline()
        while line != '\n' and line != '':
            line = line.strip().split(" ")
            event.particles.append(Particle(
                int(line[0]),
                float(line[1]), float(line[2]),
                float(line[3]), float(line[4])
            ))
            line = ofile.readline()
        simdata.events.append(event)

        line = ofile.readline()

    ofile.close()

    simdata.events.sort(key = lambda e:e.time)
    return simdata
