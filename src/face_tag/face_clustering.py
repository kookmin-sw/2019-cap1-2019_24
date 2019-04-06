from sklearn.cluster import DBSCAN
import numpy as np
import sqlite3
import array


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
#    return face_vector_list

#vector값을 모두 클러스터링하고 데이터베이스에 label id 저장
def cluster(face_vector_list, con):
    cur = con.cursor()
    #DBSCAN을 사용한 클러스터
    clt = DBSCAN(metric="euclidean", n_jobs=-1)
    clt.fit(face_vector_list)
    labelIDs = np.unique(clt.labels_)
    numUniqueFaces = len(np.where(labelIDs > -1)[0])
    print("[INFO] # unique faces: {}".format(numUniqueFaces))

    for labelID in labelIDs:
        print("[INFO] faces for face ID: {}".format(labelID))
        idxs = np.where(clt.labels_ == labelID)[0]
    #분류한 값을 0,1, ... 차례대로 label을 매긴다 (비슷한 얼굴이 없으면 -1)
    labelIDs = np.unique(clt.labels_)
    #분류된 label의 개수
    numUniqueFaces = len(np.where(labelIDs > -1)[0])
    print("[INFO] # unique faces: {}".format(numUniqueFaces))
    for labelID in labelIDs:
        print("[INFO] faces for face ID: {}".format(labelID))
        idxs = np.where(clt.labels_ == labelID)[0]
        #label id를 db에 저장

#데이터베이스에 label을 검색하여 label 중 기존에 저장된 이름(태그)이 있으면 같은 이름으로 저장
def insert_tag_to_db (con):
    cur = con.cursor()
    cur.execute("SELECT label from Face_Vector_Value")
    label_list = cur.fetchall()
    for l in label_list:
        sql = "SELECT faceName from Face_Vector_value WHERE label = ?"
        cur.execute(sql, l)
        name = cur.fetone()
        if (name is None) : pass
        else:
            for i in label_list:
                if (l == i) :
                    sql = "UPDATE Face_Vector_value SET faceName = ? WHERE lable = ?"
                    cur.execute(sql, (name, i))
                    con.commit()

#시작 함수
def face_clustering(database_file):
    #데이터베이스 열기
    con = sqlite3.connect(database_file)
    cur = con.cursor()
    #데이터베이스에 저장되어있는 모든 vector값 가져오기
    face_vector_list = set_vectorValue(database_file, con)
    #클러스터링후 label 저장
    cluster(face_vector_list, con)
    #데이터베이스에 label들을 비교하여 이름(태그)저장
    insert_tag_to_db(con)
    #데이터베이스 닫기
    cur.close()
    con.close()

face_clustering('test.db')

