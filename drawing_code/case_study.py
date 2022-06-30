import networkx as nx
import matplotlib.pyplot as plt

# For Figure 9

plt.figure(figsize=(7, 3.5))

G = nx.Graph()

nbMap_1 = {}
nbMap_2 = {}

with open("graph_data/size3_10201.txt", "r") as f:
    for line in f.readlines():
        line = line.strip('\n')
        ID = ""
        for i in range(0, len(line)):
            if line[i] == " ":
                break
            else:
                ID += line[i]
        nbMap_1[int(ID)] = []
        nb = ""
        for i in range(0, len(line)):
            if line[i] == " ":
                if nb != ID:
                    nbMap_1[int(ID)].append(int(nb))
                nb = ""
            else:
                nb += line[i]
    # print(nbMap_1)

with open("graph_data/size3_10301.txt", "r") as f:
    for line in f.readlines():
        line = line.strip('\n')
        ID = ""
        for i in range(0, len(line)):
            if line[i] == " ":
                break
            else:
                ID += line[i]
        nbMap_2[int(ID)] = []
        nb = ""
        for i in range(0, len(line)):
            if line[i] == " ":
                if nb != ID:
                    nbMap_2[int(ID)].append(int(nb))
                nb = ""
            else:
                nb += line[i]
    # print(nbMap_2)

IdToNameMap = {}
IdToNameMap[930] = 'Wenjie Zhang'
IdToNameMap[387] = 'Yizhou Sun'
IdToNameMap[69442] = 'Raymond Chi-Wing Wong'
IdToNameMap[35491] = 'Kajal T. Claypool'
IdToNameMap[995] = 'Jiawei Han'
IdToNameMap[1156] = 'Erik Buchmann'
IdToNameMap[132] = 'Duo Zhang'
IdToNameMap[198] = 'Yannis Kotidis'
IdToNameMap[523] = 'Philip S. Yu'
IdToNameMap[750] = 'ChengXiang Zhai'
IdToNameMap[59630] = 'Zaiben Chen'
IdToNameMap[248] = 'George A. Mihaila'
IdToNameMap[634] = 'Jeffrey Xu Yu'
IdToNameMap[827] = 'Binbin Liao'
IdToNameMap[20667] = 'Ben Kao'
IdToNameMap[348] = 'Carlo Zaniolo'
IdToNameMap[40924] = 'Shuigeng Zhou'
IdToNameMap[797] = 'Jens Teubner'

Edges_1 = []
Edges_2 = []
Edges = []
for id in nbMap_1:
    for nb in nbMap_1[id]:
        if (IdToNameMap[id], IdToNameMap[nb]) not in Edges and (IdToNameMap[nb], IdToNameMap[id]) not in Edges:
            Edges.append((IdToNameMap[id], IdToNameMap[nb]))
        if (IdToNameMap[id], IdToNameMap[nb]) not in Edges_1 and (IdToNameMap[nb], IdToNameMap[id]) not in Edges_1:
            Edges_1.append((IdToNameMap[id], IdToNameMap[nb]))

print(len(Edges))
print(len(Edges_1))

for id in nbMap_2:
    for nb in nbMap_2[id]:
        if (IdToNameMap[id], IdToNameMap[nb]) not in Edges and (IdToNameMap[nb], IdToNameMap[id]) not in Edges:
            Edges.append((IdToNameMap[id], IdToNameMap[nb]))
        if (IdToNameMap[id], IdToNameMap[nb]) not in Edges_2 and (IdToNameMap[nb], IdToNameMap[id]) not in Edges_2:
            if (IdToNameMap[id], IdToNameMap[nb]) in Edges_1:
                Edges_2.append((IdToNameMap[id], IdToNameMap[nb]))
                continue
            if (IdToNameMap[nb], IdToNameMap[id]) in Edges_1:
                Edges_2.append((IdToNameMap[nb], IdToNameMap[id]))
                continue
            Edges_2.append((IdToNameMap[id], IdToNameMap[nb]))

print(len(Edges))
print(len(Edges_2))

# G.add_edges_from(Edges)
# G.add_edges_from(Edges_1)

# node_ = G.nodes

count = 0

count_blue = 0
count_orange = 0
count_green = 0

edges_blue = []
edges_orange = []
edges_green = []

edge_color = []
for (id_1, id_2) in Edges:
    if ((id_1, id_2) in Edges_1 or (id_2, id_1) in Edges_1) and ((id_1, id_2) in Edges_2 or (id_2, id_1) in Edges_2):
        # edge_color.append('#4D85BD') # blue
        edges_blue.append((id_1, id_2))
        count += 1
        count_blue += 1
        continue
    if (id_1, id_2) in Edges_1 or (id_2, id_1) in Edges_1:
        count += 1
        count_orange += 1
        # edge_color.append('#F7903D') # orange
        edges_orange.append((id_1, id_2))
    if (id_1, id_2) in Edges_2 or (id_2, id_1) in Edges_2 in Edges_2:
        count += 1
        count_green += 1
        # edge_color.append('#59A95A') # green
        edges_green.append((id_1, id_2))

print(count)
print(len(Edges))
# for edge in Edges_1:
#     print(edge)
print("--")
print(count_blue)
print(count_orange)
print(count_green)

G.add_edges_from(Edges)

for (id_1, id_2) in G.edges:
    if (id_1, id_2) in edges_blue or (id_2, id_1) in edges_blue:
        edge_color.append('#4D85BD')  # blue
        # edge_color.append('#808A87')
    if (id_1, id_2) in edges_orange or (id_2, id_1) in edges_orange:
        edge_color.append('#F7903D')  # orange
        # edge_color.append('#808A87')
    if (id_1, id_2) in edges_green or (id_2, id_1) in edges_green:
        edge_color.append('#59A95A')  # green
        # edge_color.append('#808A87')

# print(edges)

# G.add_edges_from(edges)

# print(G.edges)

node_ = G.nodes

node_color = []
for name in node_:
    # if name == 'Jiawei Han' or name == 'Yizhou Sun' or name == 'Jeffrey Xu Yu':
    #     node_color.append('#FF3B3B')
    #     continue
    node_color.append('#070707')

print(node_)

labels_top = {}
labels_top['Binbin Liao'] = 'Binbin Liao'
labels_top['Ben Kao'] = 'Ben Kao'
labels_top['Carlo Zaniolo'] = 'Carlo Zaniolo'
labels_top['Jens Teubner'] = 'Jens Teubner'
labels_top['Kajal T. Claypool'] = 'Kajal T. Claypool'
labels_top['Zaiben Chen'] = 'Zaiben Chen'

labels_bottom = {}
labels_bottom['Philip S. Yu'] = 'Philip S. Yu'
labels_bottom['Yannis Kotidis'] = 'Yannis Kotidis'
labels_bottom['Duo Zhang'] = 'Duo Zhang'
labels_bottom['Erik Buchmann'] = 'Erik Buchmann'
labels_bottom['Raymond Chi-Wing Wong'] = 'Raymond Chi-Wing Wong'
labels_bottom['Jiawei Han'] = 'Jiawei Han'

labels_left = {}
labels_left['Yizhou Sun'] = 'Yizhou Sun'
labels_left['Wenjie Zhang'] = 'Wenjie Zhang'
labels_left['Shuigeng Zhou'] = 'Shuigeng Zhou'

labels_right = {}
labels_right['ChengXiang Zhai'] = 'ChengXiang Zhai'
labels_right['George A. Mihaila'] = 'George A. Mihaila'
labels_right['Jeffrey Xu Yu'] = 'Jeffrey Xu Yu'

options = {
    "edge_color" : edge_color,
    "node_size" : 75,
    "node_color" : node_color,
    "width" : 1.5
}

nx.draw(G, with_labels=False, pos=nx.circular_layout(G), **options)
# nx.draw_networkx_labels(G, pos=nx.circular_layout(G), font_size=13, font_color='#070707', verticalalignment='bottom', labels=labels_bottom, clip_on=False)
# nx.draw_networkx_labels(G, pos=nx.circular_layout(G), font_size=13, font_color='#070707', verticalalignment='top', labels=labels_top, clip_on=False)
# nx.draw_networkx_labels(G, pos=nx.circular_layout(G), font_size=13, font_color='#070707', horizontalalignment='left', labels=labels_left, clip_on=False)
# nx.draw_networkx_labels(G, pos=nx.circular_layout(G), font_size=13, font_color='#070707', horizontalalignment='right', labels=labels_right, clip_on=False)

# nx.draw_networkx_labels(G, pos=nx.circular_layout(G), font_size=13, font_color='#070707', verticalalignment='bottom', labels=labels_bottom)
# nx.draw_networkx_labels(G, pos=nx.circular_layout(G), font_size=13, font_color='#070707', verticalalignment='top', labels=labels_top)
# nx.draw_networkx_labels(G, pos=nx.circular_layout(G), font_size=13, font_color='#070707', horizontalalignment='left', labels=labels_left)
# nx.draw_networkx_labels(G, pos=nx.circular_layout(G), font_size=13, font_color='#070707', horizontalalignment='right', labels=labels_right)

# plt.savefig('network_for_test.pdf', dpi=600, format='pdf', bbox_inches='tight')

plt.show()

