
# -*- coding: utf-8 -*-
from sklearn.cluster import DBSCAN
import numpy as np
import sqlite3
import array
import matplotlib.pyplot as plt
from sklearn.manifold import TSNE
import mglearn

#데이터베이스에서 모든 vector 값을 가져온다.
def set_vectorValue(database_file, con):
    #vector값을 저장할 list생성
    face_vector_list = []
    #데이터베이스에서 vector값 가져오기
    cur = con.cursor()
    cur.execute("SELECT vectorValue from Face_Vector_Value")
    v = cur.fetchall()
    for row in v:
        #byte값을 double로 형변환하여 array생성
        doubles = array.array('d', row[0])
        face_vector_list.append(doubles)
    return face_vector_list

def set_vectorID(database_file, con):
    vectorID_list = []
    cur = con.cursor()
    cur.execute("SELECT vectorID FROM Face_vector_value")
    vids = cur.fetchall()
    for vid in vids:
        vectorID_list.append(vid[0])

#vector값을 모두 클러스터링하고 데이터베이스에 label id 저장
def cluster(face_vector_list, con):
    cur = con.cursor()

    #DBSCAN을 사용한 클러스터
    clt = DBSCAN(metric="euclidean", n_jobs=-1)
    clt.fit(face_vector_list)

    # 분류한 값을 0,1, ... 차례대로 label을 매긴다 (비슷한 얼굴이 없으면 -1)
    labelIDs = np.unique(clt.labels_)
    #분류된 label의 개수
    numUniqueFaces = len(np.where(labelIDs > -1)[0])
    for labelID in labelIDs:
        if(labelID != -1):
            idxs = np.where(clt.labels_ == labelID)[0]
            for idx in idxs:
                cur.execute("UPDATE Face_vector_value SET label = " + str(labelID) + " WHERE vectorID = " + str(idx))
                con.commit()

'''
    t-sne을 사용하여 그래프로 확인
    model = TSNE(learning_rate=100)
    transformed = model.fit_transform(face_vector_list)

    xs = transformed[:, 0]
    ys = transformed[:, 1]
    plt.scatter(xs, ys )

    plt.show()
'''

#데이터베이스에 label을 검색하여 label 중 기존에 저장된 이름(태그)이 있으면 같은 이름으로 저장
def insert_tag_to_db (con):
    cur = con.cursor()
    cur.execute("SELECT faceName FROM Face_vector_value")
    names = cur.fetchall()
    for n in names:
        if(n[0] is not None):
            sql = "SELECT label FROM Face_Vector_value WHERE faceName = " + str(n[0])
            cur.execute(sql)
            label = cur.fetchone()
            sql = "UPDATE face_vector_value SET faceName = n WHERE label =" + str(label)
            cur.execute(sql)

#시작 함수
def face_clustering(database_file):
    #데이터베이스 열기
    con = sqlite3.connect(database_file)
    cur = con.cursor()
    #데이터베이스에 저장되어있는 모든 vector값 가져오기
    face_vector_list = set_vectorValue(database_file,con)
    vectorID_list = set_vectorID(database_file, con)
    #클러스터링후 label 저장
    cluster(face_vector_list, con)
    #데이터베이스에 label들을 비교하여 이름(태그)저장
    insert_tag_to_db(con)
    #데이터베이스 닫기
    cur.close()
    con.close()
