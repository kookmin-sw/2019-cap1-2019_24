import networkx as nx

# 방향성 없는 네트워크 형성
G = nx.Graph()

# weight와 함께 노드 추가
G.add_weighted_edges_from([('A', 'B', 3), ('A', 'C', 2), ('B', 'C', 5), ('B', 'D', 1),
                           ('B', 'E', 7), ('C', 'D', 2), ('E', 'D', 9), ('E', 'F', 4)])

# 노드의 중요도 계산

# degree
def return_degree_centrality(input_g):
    # 중요도는 연결된 노드의 비율
    # degree의 중심값은 그래프 n-1에서 가능한 최대 차수로 나누어 정규화
    return nx.degree_centrality(input_g)

# weighted_degree
def return_weighted_degree_centrality(input_g, normalized=True):
    w_d_centrality = {n:0.0 for n in input_g.nodes()}
    for u, v, d in input_g.edges(data=True):
        # 각 노드에 연결되었던 가중치를 전부 더한다.
        w_d_centrality[u]+=d['weight']
        w_d_centrality[v]+=d['weight']
    # 정규화
    if normalized==True:
        weighted_sum = sum(w_d_centrality.values())
        return {k:v/weighted_sum for k, v in w_d_centrality.items()}
    else:
        return w_d_centrality

# closeness
def return_closeness_centrality(input_g):
    # 근접성이 높을수록 중심성이 높음
    # 네트워크의 모든 노드로부터 얼마나 가깝게 위치해있는지를 고려
    # distance가 낮아야 중요한 노드
    new_g_with_distance = input_g.copy()
    for u,v,d in new_g_with_distance.edges(data=True):
        if 'distance' not in d:
            d['distance'] = 1.0/d['weight']
    return nx.closeness_centrality(new_g_with_distance, distance='distance')

# betweenness
def return_betweenness_centrality(input_g):
    # 모든 노드 쌍 간의 shortest path가 해당 노드를 지나는지를 고려
    return nx.betweenness_centrality(input_g, weight='weight')

# pagerank
def return_pagerank(input_g):
    # 영향력이 높은 노드가 많이 연결되어 있을 수록 중요한 노드
    return nx.pagerank(input_g, weight='weight')

def print_centrality(input_g, centrality_func):
    print(centrality_func(input_g))


print("weighted degree centrality")
print_centrality(G, return_weighted_degree_centrality)
print("degree centrality")
print_centrality(G, return_degree_centrality)
