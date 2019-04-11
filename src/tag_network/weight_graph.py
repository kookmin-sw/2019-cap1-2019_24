import numpy as np
import networkx as nx
import matplotlib.pyplot as plt


# 방향성 없는 네트워크 형성
G = nx.Graph()

# weight와 함께 노드 추가
G.add_weighted_edges_from([('A', 'B', 3), ('A', 'C', 2), ('B', 'C', 5), ('B', 'D', 1),
                           ('B', 'E', 7), ('C', 'D', 2), ('E', 'D', 9), ('E', 'F', 4)])
# 모든 node와 edge 확인
# print(G.nodes())
# print(len(G.edges()))

plt.figure(figsize=(10, 6))
pos = nx.spring_layout(G)


nx.draw_networkx_nodes(G, pos, node_shape='o', node_size=200, node_color='pink')

# weight에 따라 edge 색상이 다르게
edge_color = np.array([e[2]['weight'] for e in G.edges(data=True)])
nx.draw_networkx_edges(G, pos=pos, edge_color=edge_color, edge_cmap=plt.cm.Greys,
                       edge_vmin=edge_color.min(), edge_vmax=edge_color.max())


plt.show()
