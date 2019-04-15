import pickle
import matplotlib.pyplot as plt
from sklearn.manifold import TSNE
from sklearn.cluster import DBSCAN

face_set=[]
with open('testdata.txt', 'rb') as p:
    try:
        while True:
            data = pickle.load(p)
            face_set.append(data)

    except EOFError:
        print()
        
# 128벡터가 잘 뽑혔는지 확인
model = TSNE(learning_rate=10)
transformed = model.fit_transform(face_set)

xs = transformed[:, 0]
ys = transformed[:, 1]
plt.scatter(xs, ys)
plt.show()

# eps, min_samples 값을 어떻게 주냐에 따라 라벨이 상이
db = DBSCAN(metric="euclidean", n_jobs=-1, eps=0.3, min_samples=2).fit(face_set)
skl_labels = db.labels_
print(skl_labels)
