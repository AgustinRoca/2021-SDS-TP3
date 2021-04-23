import matplotlib.pyplot as plt
import numpy as np
import statistics as stats

dcms_file = open("../data/testResults/dcms.txt","r")

time_data = dcms_file.readline().strip().split(" ")
interval = float(time_data[0])
start_time = float(time_data[1])
dcm_grande = list(map(float,dcms_file.readline().strip().split(" ")))
dcm_chica = list(map(float,dcms_file.readline().strip().split(" ")))

t = x = np.linspace(start_time, interval*(len(dcm_grande)-1) + start_time, len(dcm_grande))

sum_yi_by_t = 0
sum_t_squared = 0
for i in range(len(dcm_grande)):
    sum_yi_by_t += dcm_grande[i] * t[i]
    sum_t_squared += t[i] * t[i]
d_grande = sum_yi_by_t / (2*sum_t_squared)

error_grande = 0
for i in range(len(dcm_grande)):
    error_grande += (dcm_grande[i] - 2*d_grande*t[i])**2

sum_yi_by_t = 0
sum_t_squared = 0
for i in range(len(dcm_chica)):
    sum_yi_by_t += dcm_chica[i] * t[i]
    sum_t_squared += t[i] * t[i]
d_chica = sum_yi_by_t / (2*sum_t_squared)

error_chica = 0
for i in range(len(dcm_chica)):
    error_chica += (dcm_chica[i] - 2*d_chica*t[i])**2

fig1, ax1 = plt.subplots()
fig1.set_size_inches((12,12))
ax1.title.set_text("DCM Grande")
ax1.scatter(t, dcm_grande)
ax1.plot(t, 2*d_grande*t, c='r')
ax1.set_xlabel("Tiempo (s)")
ax1.set_ylabel("Desplazamiento cuadratico medio (m^2)")
fig2, ax2 = plt.subplots()
fig2.set_size_inches((12, 12))
ax2.title.set_text("DCM Chica")
ax2.scatter(t, dcm_chica)
ax2.plot(t, 2*d_chica*t, c='r')
ax2.set_xlabel("Tiempo (s)")
ax2.set_ylabel("Desplazamiento cuadratico medio (m^2)")

print(f"d grande:{d_grande}")
print(f"d grande error:{error_grande}")
print(f"d chica:{d_chica}")
print(f"d chica error:{error_chica}")


plt.show()