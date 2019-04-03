import face_recognition
from sklearn.cluster import DBSCAN
import pickle
import numpy as np
# 사진 파일 가져옴

file_name = 'test1.jpg'
image = face_recognition.load_image_file(file_name)

boxes = face_recognition.face_locations(image, model="hog")
#128 벡터로 변환
encodings = face_recognition.face_encodings(image, boxes)
print(len(encodings))


clt = DBSCAN(metric="euclidean", n_jobs=-1)
clt.fit(encodings)
labelIDs = np.unique(clt.labels_)
numUniqueFaces = len(np.where(labelIDs > -1)[0])
print("[INFO] # unique faces: {}".format(numUniqueFaces))

for labelID in labelIDs:
    print("[INFO] faces for face ID: {}".format(labelID))
    idxs = np.where(clt.labels_ == labelID)[0]
