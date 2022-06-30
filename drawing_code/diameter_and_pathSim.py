import matplotlib.pyplot as plt
from matplotlib.ticker import MultipleLocator, FormatStrFormatter
import numpy as np
import matplotlib as mpl

# For Figure 6 (a), (b)

dataset_list = ['PubMed', 'IMDB', 'DBLP', 'FourSquare']
NMC_list = [2.717391304347826, 10.587209302325581, 4.861111111111111, 5.0]
CSH_list = [3.0700483091787443, 12.472868217054263, 5.0, 5.5]
# NMC_list = [0.0562880892054933, 6.135587195483042E-4, 0.0073479227267580485, 0.004106527887561876]
# CSH_list = [0.05267714158761052, 4.293139661127957E-4, 0.007282547588144863, 0.0035212987973627986]


plt.figure(figsize=(3.6, 2.4))

plt.rcParams['xtick.direction'] = 'in'
plt.rcParams['ytick.direction'] = 'in'

# plt.axes(yscale="log")

x = np.arange(4)

ax = plt.gca()
ax.spines['bottom'].set_linewidth(2)
ax.spines['left'].set_linewidth(2)
ax.spines['top'].set_linewidth(2)
ax.spines['right'].set_linewidth(2)

total_width, n = 1.6, 4
width = total_width / n

ymajorLocator = MultipleLocator(4)
ax.yaxis.set_major_locator(ymajorLocator)

# mpl.rcParams['hatch.linewidth'] = 2

# plt.bar(x, NMC_list, width=width-0.02, label='CSSH', fc='#81B8DF', edgecolor='black', hatch='\\\\')
# plt.bar(x + width,  CSH_list, width=width-0.02, label='CSH', fc='#FE817D', edgecolor='black', hatch='//')

plt.bar(x, NMC_list, width=width-0.05, label='CSSH', fc='#81B8DF', edgecolor='black', hatch='\\\\')
plt.bar(x + width,  CSH_list, width=width-0.05, label='CSH', fc='#FE817D', edgecolor='black', hatch='//')


plt.xticks(x + width / 2, dataset_list, size=10.5)
# plt.xticks(rotation=15)
print(x)
# plt.yticks(y)

plt.tick_params(bottom=False, top=False, left=False, right=False, which="minor")

plt.ylim(0, 13)
plt.xlim(-0.5, 3.7)

# plt.xlabel()
plt.ylabel('diameter', fontsize=13)
# plt.ylabel('PathSim', fontsize=13)

plt.legend(frameon=False)

# plt.savefig('Diameter_new.pdf', dpi=600, format='pdf', bbox_inches='tight')
plt.show()
