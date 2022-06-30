import matplotlib.pyplot as plt

# For Figure 10

x = [0.2, 0.4, 0.6, 0.8, 1]

# MKC
# y_1 = [0.06152919, 0.440590357, 1.754845049, 3.611251141, 9]  # PubMed
# y_1 = [0.19929046, 0.762432934, 2.665323563, 4.969741292, 12] # IMDB
# y_1 = [5.132749468, 83.067918702, 973.788799788, 3562.104237101, 9176] # DBLP
# y_1 = [1.709044153, 41.002495332, 283.684482379, 985.101879517, 2025] # FourSquare

# y_1 = [0.06152919, 0.440590357, 1.754845049, 3.611251141, 9]  # PubMed
# y_2 = [0.19929046, 0.762432934, 2.665323563, 4.969741292, 12] # IMDB

y_1 = [5.132749468, 83.067918702, 973.788799788, 3562.104237101, 9176] # DBLP
y_2 = [1.709044153, 41.002495332, 283.684482379, 985.101879517, 2025] # FourSquare

# MC
# y_2 = [0.064849774, 0.559369999, 2.132595988, 2.791260870, 6] # PubMed
# y_2 = [0.203250724, 0.743572577, 2.100203336, 5.00330653, 14] # IMDB
# y_2 = [5.250215157, 136.51145318, 1244.288373691, 35000, 35000] # DBLP
# y_2 = [1.856161079, 48.171418919, 340.267588519, 902.257668937, 2255] # FourSquare

# KC
# y_3 = [0.043522509, 0.416279437, 1.753078401, 2.425013386, 5] # PubMed
# y_3 = [0.132027021, 0.860019935, 2.809673859, 5.042555161, 11] # IMDB
# y_3 = [3.201599088, 86.289840528, 965.682411703, 3612.952107704, 9269] # DBLP
# y_3 = [1.560463166, 39.021712989, 313.030594479, 1003.173739558, 2015] # FourSquare

plt.figure(figsize=(3.6, 2.4))

plt.rcParams['xtick.direction'] = 'in'
plt.rcParams['ytick.direction'] = 'in'

plt.axes(yscale="log")

ax = plt.gca()
ax.spines['bottom'].set_linewidth(2)
ax.spines['left'].set_linewidth(2)
ax.spines['top'].set_linewidth(2)
ax.spines['right'].set_linewidth(2)

# plt.plot(x, y_1, color = '#4D85BD', marker = '^', markersize = 8, label = 'MKC')
# plt.plot(x, y_2, color = '#F7903D', marker = 'v', markersize = 8, label = 'MC')
# plt.plot(x, y_3, color = '#59A95A', marker = 'o', markersize = 8, label = 'KC')

plt.plot(x, y_1, color = '#81B8DF', marker = 's', markersize = 8, label = 'DBLP')
plt.plot(x, y_2, color = '#FE817D', marker = 'D', markersize = 8, label = 'FourSquare')

plt.xlabel("percentage", fontdict={'size' : 13})
plt.ylabel("time(s)", fontdict={'size' : 13})

plt.xlim(0.18, 1.02)

# plt.ylim(0, 50) # PubMed
# plt.ylim(0, 100) # IMDB
plt.ylim(0, 30000) # DBLP
# plt.ylim(0, 10000) # FourSquare

plt.xticks(x, ["20%", "40%", "60%", "80%", "100%"], size = 12)
plt.yticks(size = 12)
plt.tick_params(bottom=False, top=False, left=False, right=False, which="minor")

plt.legend(loc='upper left', frameon=False, ncol=3, fontsize=9)

# plt.savefig('scalability_Index_2.pdf', dpi=600, format='pdf', bbox_inches='tight')
plt.show()