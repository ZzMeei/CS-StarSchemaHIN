import matplotlib.pyplot as plt
from matplotlib.ticker import MultipleLocator, FormatStrFormatter

# For Figure 8

x = [0.2, 0.4, 0.6, 0.8, 1]

y_1 = [0.01111736, 0.321874679, 1.741954532, 4.000298213, 11.411818122] # PubMed
# y_1 = [0.072121214, 3.001115536, 16.001143274, 52.71204932, 135.236516541] # IMDB
# y_1 = [1.683858166, 37.289176738, 392.759616967, 2097.026154313, 4165.388819851] # DBLP
# y_1 = [0.17107723, 8.783985224, 107.537701896, 447.392713973, 1124.317350666] # FourSquare

y_2 = [0.004654261, 0.013768508, 0.026368403, 0.053582868, 0.099143213] # PubMed
# y_2 = [0.290852759, 0.87505664, 2.309261516, 4.736969362, 8.73556414] # IMDB
# y_2 = [0.493094202, 1.978401465, 5.116072796, 11.829275254, 25.485091053] # DBLP
# y_2 = [0.955421971, 3.745908815, 9.107844346, 16.047382128, 29.279092019] # FourSquare

plt.figure(figsize=(3.6, 2.4))

plt.rcParams['xtick.direction'] = 'in'
plt.rcParams['ytick.direction'] = 'in'

plt.axes(yscale="log")

ax = plt.gca()
ax.spines['bottom'].set_linewidth(2)
ax.spines['left'].set_linewidth(2)
ax.spines['top'].set_linewidth(2)
ax.spines['right'].set_linewidth(2)

plt.plot(x, y_1, color = '#81B8DF', marker = 's', markersize = 8, label = 'NaiveOnline')
plt.plot(x, y_2, color = '#FE817D', marker = 'D', markersize = 8, label = 'FastOnline')

plt.xlabel("percentage", fontdict={'size' : 13})
plt.ylabel("time(s)", fontdict={'size' : 13})

plt.xlim(0.18, 1.02)

plt.ylim(0, 100) # PubMed
# plt.ylim(0, 1000) # IMDB
# plt.ylim(0, 40000) # DBLP
# plt.ylim(0, 10000) # FourSquare

plt.xticks(x, ["20%", "40%", "60%", "80%", "100%"], size = 12)
plt.yticks(size = 12)
plt.tick_params(bottom=False, top=False, left=False, right=False, which="minor")

plt.legend(loc='best', frameon=False, ncol=2, fontsize=9)

plt.savefig('scalability_PubMed_new.pdf', dpi=600, format='pdf', bbox_inches='tight')
plt.show()
