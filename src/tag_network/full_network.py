import numpy as np
import networkx as nx
import matplotlib.pyplot as plt

import matplotlib.font_manager as fm
from networkx.algorithms.community import girvan_newman
from networkx.algorithms.community import coverage, performance

# 커뮤니티 리스트
community_lst = []
# 커뮤니티가 얼마나 잘 뽑혔는지 평가하는 지표
# performance, coverage의 값을 확인한 다음
# performance의 변화 폭이 작고, coverage가 충분히 클 때까지 자름
coverage_lst, performance_lst = [], []

# 방향성 없는 네트워크 형성
G = nx.Graph()


# 가중치를 포함한 노드 간의 링크 생성
G.add_weighted_edges_from([('A', 'B', 3), ('A', 'C', 2), ('A', 'I', 3), ('B', 'C', 5), ('B', 'D', 1),
                           ('B', 'E', 7), ('C', 'D', 2), ('B', 'G', 9), ('B', 'I', 1), ('C', 'K', 7),
                           ('D', 'F', 9), ('C', 'I', 6), ('E', 'F', 4), ('E', 'G', 5),
                           ('E', 'I', 1), ('H', 'I', 9), ('H', 'K', 1)])

# 노드 삭제 코드
# G.remove_node('K')
# G.remove_nodes_from(['A', 'G'])

# 모든 node와 edge 확인
# print(G.nodes())
# print(G.edges())

# G를 그래프로 표현
pos = nx.spring_layout(G)


for i, comms in enumerate(girvan_newman(G)):
    # performance의 변화폭이 많이 적어지면 더이상 cluster를 나누어도 이득이 없으므로 멈춤
    if i != 0 and abs(performance(G, comms) - performance_lst[-1]) < 0.01:
        break
    else:
        community_lst.append(comms),
        coverage_lst.append(coverage(G, comms)), performance_lst.append(performance(G, comms))


# 나눠진 커뮤니티 그룹 확인
for s in community_lst[-1]:
    print(s)
# print(len(community_lst[-1]))

selected_community = community_lst[-1]

# 원래 그래프의 노드의 attribute에 community 정보를 넘겨줌
for i, comm in enumerate(community_lst[-1]):
    for p in comm:
        G.nodes()[p]['community'] = i

plt.figure(figsize=(8, 4))
pos = nx.spring_layout(G)

nx.draw_networkx_nodes(G, pos=pos, node_size=200,
                       node_color=[n[1]['community'] for n in G.nodes(data=True)],
                       cmap=plt.cm.gist_rainbow,
                       alpha=0.5, labels={n[0]: n[1]['community'] for n in G.nodes(data=True)})

nx.draw_networkx_edges(G, pos=pos, edge_color='gray', edge_cmap=plt.cm.binary)

pos_min = (min((x for x, y in pos.values())), min((y for x, y in pos.values())))
pos_max = (max((x for x, y in pos.values())), max((y for x, y in pos.values())))

plt.xlim(pos_min[0]-0.1, pos_max[0]+0.1)
plt.ylim(pos_min[1]-0.1, pos_max[1]+0.1)

# 옆에 표시할 라벨 목록(분류된 그룹 수만큼 생성되도록 해야함)
community_label_lst = ['set1', 'set2', 'set3', 'set4']

tempx, tempy = 100, 100
for i, c in enumerate(plt.cm.gist_rainbow(np.linspace(0.0, 1.0, len(selected_community)))):
    # s 도형의 크기, marker 도형 모양, alpha 불투명도(0투명, 1불투명)
    plt.scatter(tempx, tempy, s=100, marker='o', label=community_label_lst[i], alpha=0.5, zorder=0)
plt.scatter(tempx, tempy, s=300, marker='o', c='white',  zorder=1)
plt.legend()
plt.show()

'''
안드로이드에서 그래프를 그려줄 예정이므로
분류된 노드가 묶인 그룹ID를 같이 데이터로 넘겨줘야할 듯
'''