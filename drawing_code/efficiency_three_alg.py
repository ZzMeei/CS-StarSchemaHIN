import matplotlib.pyplot as plt

# For Figure 7

x = [6, 8, 10, 12, 14]

# basic online
# y_1 = [8.94601502, 9.242835314, 9.207821089, 9.131244042, 9.07635128] # PubMed
# y_1 = [119.46366684, 121.08825102, 133.362829447, 132.119474137, 134.381491215] # IMDB
# y_1 = [4735.882567962, 4319.746112455, 4262.774072681, 4334.677378085, 4517.869765029] # DBLP
# y_1 = [807.836281418, 775.735357567, 817.139833487, 878.240547398, 917.194453301] # FourSquare

# y_1 = [11.885147821, 12.544814949, 12.782399467, 13.042537363, 13.062001068] # PubMed
# y_1 = [215.038978224, 213.389157711, 215.940991787, 214, 215] # IMDB
# y_1 = [9100, 9000, 9050, 9120, 9100] # DBLP
y_1 = [1600, 1540, 1620, 1700, 1800] # FourSquare

# fast online
# y_2 = [0.102592666, 0.085149622, 0.094477514, 0.102890566, 0.112316071] # PubMed
# y_2 = [9.748968611, 10.111903727, 10.671570766, 10.860013571, 10.974934165] # IMDB
# y_2 = [28.302995812, 29.457362677, 33.918428231, 37.988499148, 41.831235946] # DBLP
y_2 = [30.346279953, 33.000906298, 37.961870881, 45.998690132, 56.595840517] # FourSquare

# index
# y_3 = [0.014471439, 0.014868377, 0.014758353, 0.019580874, 0.019410565] # PubMed
# y_3 = [3.491371092, 5.118649159, 4.867929482, 4.446233636, 4.444148899] # IMDB
# y_3 = [11.84213908, 12.635801159, 15.856759802, 17.597108449, 18.289370805] # DBLP
y_3 = [5.07689502, 5.019548705, 5.112494106, 5.055756753, 4.748119646] # FourSquare

plt.figure(figsize=(3.6, 2.4))

plt.rcParams['xtick.direction'] = 'in'
plt.rcParams['ytick.direction'] = 'in'

plt.axes(yscale="log")

ax = plt.gca()
ax.spines['bottom'].set_linewidth(2)
ax.spines['left'].set_linewidth(2)
ax.spines['top'].set_linewidth(2)
ax.spines['right'].set_linewidth(2)

plt.plot(x, y_1, color = '#4D85BD', marker = '^', markersize = 8, label = 'NaiveOnline')
plt.plot(x, y_2, color = '#F7903D', marker = 'v', markersize = 8, label = 'FastOnline')
plt.plot(x, y_3, color = '#59A95A', marker = 'o', markersize = 8, label = 'IndexQuery')

plt.xlabel("k", fontdict={'size' : 13})
plt.ylabel("time(s)", fontdict={'size' : 13})

plt.xlim(5.5, 14.5)

# plt.ylim(0, 500) # PubMed
# plt.ylim(0, 2000) # IMDB
# plt.ylim(0, 200000) # DBLP
plt.ylim(0, 50000) # FourSquare

plt.xticks(x, [6, 8, 10, 12, 14], size = 12)
plt.yticks(size = 12)
plt.tick_params(bottom=False, top=False, left=False, right=False, which="minor")

plt.legend(frameon=False, fontsize=9, ncol=2)

plt.savefig('K_FourSquare.pdf', dpi=600, format='pdf', bbox_inches='tight')
plt.show()