class SimData:
    def __init__(self, sim_side=0, particle_count=0, particles=None, events=None, time=0, delta_time=0, last_event=0):
        self.sim_side = sim_side
        self.particle_count = particle_count
        self.particles = particles if particles != None else {}
        self.events = events if events != None else []
        self.time = time
        self.delta_time = delta_time
        self.last_event = last_event

    def __repr__(self):
        return f"{{ sim_side:{self.sim_side}, particle_count:{self.particle_count},\n particles:{self.particles}, \n events:{self.events} }}"

    def update_particles_on_time(self):
        # update all particles with mru(movimiento rectilineo uniforme)
        for pid in self.particles:
            self.particles[pid].x += self.particles[pid].vx * self.delta_time
            self.particles[pid].y += self.particles[pid].vy * self.delta_time

        self.update_particles_on_event()
        # update time
        self.time += self.delta_time

    def update_particles_on_event(self):
        """ Check no events where left behind """
        while (self.last_event < len(self.events) - 1 and self.time > self.events[self.last_event + 1].time):
            self.__update_particles_on_event_helper()
            # update last event
            self.last_event += 1
        """ 
            check if there are events left and
            if one occur between last update and now 
        """
        while (self.last_event < len(self.events) - 1 and
            self.time < self.events[self.last_event + 1].time and
                self.time + self.delta_time >= self.events[self.last_event + 1].time):
            self.__update_particles_on_event_helper()
            # update last event
            self.last_event += 1

    def __update_particles_on_event_helper(self):
        for p in self.events[self.last_event+1].particles:
            # update speeds
            self.particles[p.id].vx = p.vx
            self.particles[p.id].vy = p.vy

            # calculate time between event and current time
            delta_with_event = (
                (self.time + self.delta_time) -
                self.events[self.last_event + 1].time
            )
            # update particle position
            self.particles[p.id].x = p.x + \
                self.particles[p.id].vx * delta_with_event
            self.particles[p.id].y = p.y + \
                self.particles[p.id].vy * delta_with_event


class CollideEvent:
    def __init__(self, time=0, particles=None):
        self.time = time
        self.particles = particles if particles != None else []

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
        event = CollideEvent(time=float(line), particles=[])
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

    simdata.events.sort(key=lambda e: e.time)
    return simdata
