import numpy as np
import networkx as nx
import matplotlib.pyplot as plt

import matplotlib.font_manager as fm
from networkx.algorithms.community import girvan_newman
from networkx.algorithms.community import coverage, performance

# 커뮤니티 리스트
community_lst = []
# 커뮤니티가 얼마나 잘 뽑혔는지 평가하는 지표
coverage_lst, performance_lst = [], []

# 방향성 없는 네트워크 형성
G = nx.Graph()

# G.add_nodes_from([2,3]) : 노드 2, 3 추가
# G.add_nodes_from(fruits) : fruits list에 있는 항목들을 노드로 추가

# 알파벳 A부터 0까지 노드로 추가
G.add_nodes_from([chr(c) for c in range(ord('A'), ord('A')+18)])

# 노드 간의 링크 생성
G.add_edges_from([('A', n) for n in list(G.node())[:10]])
G.add_edges_from([('G', n) for n in list(G.node())[11:]])
G.add_edges_from([('B', 'J'), ('C', 'J'), ('C', 'H'), ('H', 'E'), ('E', 'D'), ('D', 'I'),
                  ('I', 'F'), ('B', 'K'), ('K', 'F')])
G.add_edges_from([('O', 'N'), ('N', 'L'), ('M', 'L'), ('O', 'K'), ('K', 'M')])
G.add_edges_from([('A', 'K'), ('G', 'K'), ('R', 'S'), ('E', 'S'), ('S', 'A')])
G.add_edges_from([('G', 'C'), ('G', 'K'), ('B', 'C'), ('B', 'E'), ('P', 'Q'), ('P', 'N')])

# 모든 node와 edge 확인
# print(G.nodes())
# print(G.edges())

# G를 그래프로 표현
pos = nx.spring_layout(G)

# G.remove_node('K')
# G.remove_nodes_from(['A', 'G'])

for i, comms in enumerate(girvan_newman(G)):
    # performance의 변화폭이 많이 적어지면 더이상 cluster를 나누어도 이득이 없으므로 멈춤
    if i != 0 and abs(performance(G, comms) - performance_lst[-1]) < 0.01:
        break
    else:
        community_lst.append(comms),
        coverage_lst.append(coverage(G, comms)), performance_lst.append(performance(G, comms))

# performance, coverage의 값을 확인한 다음
# performance의 변화 폭이 작고, coverage가 충분히 클 때까지 자름
'''
분류된 커뮤니티 확인
for s in community_lst[-1]:
    print(s)
print(len(community_lst[-1]))
'''

# 원래 그래프의 노드의 attribute에 community 정보를 넘겨줌
for i, comm in enumerate(community_lst[-1]):
    for p in comm:
        G.nodes()[p]['community'] = i

plt.figure(figsize=(10, 6))
pos = nx.spring_layout(G)

nx.draw_networkx_nodes(G, pos=pos, node_size=200,
                       node_color=[n[1]['community'] for n in G.nodes(data=True)],
                       cmap=plt.cm.gist_rainbow,
                       alpha=0.5, labels={n[0]: n[1]['community'] for n in G.nodes(data=True)})

nx.draw_networkx_edges(G, pos=pos, edge_color='gray', edge_cmap=plt.cm.binary)
tempx, tempy = 1.3, 0.0
for c in plt.cm.gist_rainbow(np.linspace(0.0, 1.0, len(community_lst[-1]))):
    plt.scatter(tempx, tempy, s=100, marker='o', alpha=0.5, zorder=0)
plt.scatter(tempx, tempy, s=100, marker='o', c='white',  zorder=1)
plt.legend()
plt.show()
