import matplotlib.pyplot as plt
import numpy as np
import statistics as stats

dcms_file = open("../data/testResults/dcms.txt","r")

time_data = dcms_file.readline().strip().split(" ")
interval = float(time_data[0])
start_time = float(time_data[1])
dcm_grande = list(map(float,dcms_file.readline().strip().split(" ")))
dcm_chica = list(map(float,dcms_file.readline().strip().split(" ")))

t_dcm_grande = []
for i in range(len(dcm_grande)):
    t_dcm_grande.append(interval*i + start_time)

t_dcm_chica = []
for i in range(len(dcm_chica)):
    t_dcm_chica.append(interval*i + start_time)

fig1,ax1 = plt.subplots()
fig1.set_size_inches((12,12))
ax1.title.set_text("DCM Grande")
ax1.plot(t_dcm_grande,dcm_grande)
fig2,ax2 = plt.subplots()
fig2.set_size_inches((12,12))
ax2.title.set_text("DCM Chica")
ax2.plot(t_dcm_grande,dcm_chica)

aux_dcm_grande = []
for i in range(len(t_dcm_grande)):
    aux_dcm_grande.append(dcm_grande[i]/(t_dcm_grande[i] * 2))

aux_dcm_chica = []
for i in range(len(t_dcm_chica)):
    aux_dcm_chica.append(dcm_chica[i]/(t_dcm_chica[i] * 2))

dcm_grande_val = stats.mean(aux_dcm_grande)
dcm_grande_error = stats.stdev(aux_dcm_grande)
dcm_chica_val = stats.mean(aux_dcm_chica)
dcm_chica_error = stats.stdev(aux_dcm_chica)

print(f"dcm grande:{dcm_grande_val}")
print(f"dcm grande error:{dcm_grande_error}")
print(f"dcm chica:{dcm_chica_val}")
print(f"dcm chica error:{dcm_chica_error}")


plt.show()