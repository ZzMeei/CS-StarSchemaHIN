import matplotlib.pyplot as plt
from matplotlib.ticker import MultipleLocator, FormatStrFormatter

# For Figure 6 (c), (d)

x = [2, 3, 4, 5]

# PubMed
# y_1 = [2.43, 2.58, 2.64, 2.67]  # length
# y_2 = [2.625, 2.48, 2.41, 2.38]  # size

# IMDB
# y_1 = [3.1472082, 3.253886, 3.2631578, 3.308108] # length
# y_2 = [1.9441625, 1.8134716, 1.7473685, 1.6702703] # size

# DBLP
# y_1 = [3.01, 3.03, 3.06, 3.07] # length
# y_2 = [2.13, 2.07, 2.045, 2.02] # size

# FourSquare
# y_1 = [4.0, 4.0, 4.0, 4.0] # length
# y_2 = [1.4086957, 1.3859649, 1.3539823, 1.3362832] # size

# length
y_1 = [2.43, 2.58, 2.64, 2.67] # PubMed
y_2 = [3.1472082, 3.253886, 3.2631578, 3.308108] # IMDB
y_3 = [3.01, 3.03, 3.06, 3.07] # DBLP
y_4 = [4.0, 4.0, 4.0, 4.0] # FourSquare

# size
# y_1 = [2.625, 2.48, 2.41, 2.38] # PubMed
# y_2 = [1.9441625, 1.8134716, 1.7473685, 1.6702703] # IMDB
# y_3 = [2.13, 2.07, 2.045, 2.02] # DBLP
# y_4 = [1.4086957, 1.3859649, 1.3539823, 1.3362832] # FourSquare

plt.figure(figsize=(3.6, 2.4))

plt.rcParams['xtick.direction'] = 'in'
plt.rcParams['ytick.direction'] = 'in'

# plt.axes(yscale="log")

ax = plt.gca()
ax.spines['bottom'].set_linewidth(2)
ax.spines['left'].set_linewidth(2)
ax.spines['top'].set_linewidth(2)
ax.spines['right'].set_linewidth(2)

# plt.plot(x, y_1, color = '#81B8DF') # blue
# plt.plot(x, y_2, color = '#FE817D') # red

p_1, = plt.plot(x, y_1, color = '#171717', marker = '^', markersize = 8, label = 'PubMed')
p_2, = plt.plot(x, y_2, color = '#06DF06', marker = 'v', markersize = 8, label = 'IMDB')
p_3, = plt.plot(x, y_3, color = '#FF1C00', marker = 'o', markersize = 8, label = 'DBLP')
p_4, = plt.plot(x, y_4, color = '#0025FF', marker = 'x', markersize = 8, label = 'FourSquare')

plt.xlabel("Query size", fontdict={'size' : 13})
plt.ylabel("meta-path length", fontdict={'size' : 13})
# plt.ylabel("meta-path number", fontdict={'size' : 13})

plt.xlim(1.8, 5.2)
plt.ylim(2, 5.2)

ymajorLocator = MultipleLocator(0.6)
ax.yaxis.set_major_locator(ymajorLocator)

plt.xticks(x, [2, 3, 4, 5], size = 12)
plt.yticks(size = 12)
plt.tick_params(bottom=False, top=False, left=False, right=False, which="minor")

l1 = plt.legend([p_1, p_2], ['PubMed', 'IMDB'], loc=2, frameon=False, fontsize=10)

plt.legend([p_3, p_4], ['DBLP', 'FourSquare'] ,loc=1, frameon=False, fontsize=10)
plt.gca().add_artist(l1)

plt.savefig('Size_Length.pdf', dpi=600, format='pdf', bbox_inches='tight')

plt.show()