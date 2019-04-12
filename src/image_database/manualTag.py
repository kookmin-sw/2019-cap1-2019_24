# -*- coding: utf-8 -*-

import sqlite3
import sys
reload(sys)
sys.setdefaultencoding('utf-8')

#(사용자가 입력한 태그가) 새로운 태그인지 db에 있던 태그인지 판단하는 함수
def if_new_tag(tag, cur):
    #Object_manual_tag 테이블에서 모든 정보를 가져온다.
    cur.execute("SELECT * FROM Object_manual_tag")
    rows = cur.fetchall()
    
    #db에 태그가 하나도 존재하지 않으면 0을 리턴
    if (len(rows) == 0):
        return 0
    
    # 해당 태그가 존재하지 않으면 tagID를 부여하기 위해 전체 태그의 수를 리턴하고
    # 해당 태그가 존재하면 -1을 리턴
    for row in rows:
        if(tag in row):
            return -1
        else:
            pass
    return len(rows)


#tag_log 테이블에 이미지 파일 명과 태그 정보를 입력하는 함수
def tag_log(cur, image_file_name, tagID):

    sql = "INSERT INTO Tag_log (imageFileName, manualTagID) VALUES (?,?)"
    cur.execute(sql, (image_file_name, tagID))

    return 0


#사용자가 입력한 태그가 들어왔을 때 시작하는 함수
def input_manual_tag(database_file, image_file_name, tag, categoryID):
    #sqlite 실행
    con = sqlite3.connect(database_file)
    cur = con.cursor()

    #db에 있는 태그일 경우(r==-1) tagCount를 하나 증가시키고 db의 Tag_log테이블에 정보 저장
    #db에 없는 태그일 경우 태그 정보 저장하고 db의 Tag_log테이블에 정보 저장
    r = if_new_tag(tag, cur)

    if(r == -1):
        #해당 tag의 tagCount값 가져옴
        sql = "SELECT tagCount FROM Object_manual_tag WHERE tag = "
        cur.execute(sql + '\''+ tag + '\'')
        tagCount = cur.fetchone()
        #하나 증가시킴
        tagCount = tagCount[0] + 1
        #증가시킨 값으로 업데이트
        sql = "UPDATE Object_manual_tag SET tagCount = ? WHERE tag = ?"
        cur.execute(sql, (tagCount, tag))
        con.commit()
        #해당 tag의 tag ID 가져옴
        sql = "SELECT tagID FROM Object_manual_tag WHERE tag = "
        cur.execute(sql + '\'' + tag + '\'')
        tagID = cur.fetchone()
        #tag_log 테이블에 정보 저장
        tag_log(cur, image_file_name, tagID[0])
        con.commit()

    else:
        #db에 없는 태그인 경우 r은 전체 태그의 개수
        r = r+1
        #tagID 만들기 (m으로 시작하는 8자리 숫자)
        tagID = 'm'
        for i in range(0, 8-int(r/10)):
            tagID = tagID + '0'
        tagID = tagID + str(r)
        #Object_manual_tag 테이블에 태그 정보 저장
        sql = "INSERT INTO Object_manual_tag (tagID, tag, tagCount, categoryID) VALUES(?, ?, ?, ?) "
        cur.execute(sql, (tagID, tag, 1, categoryID))
        con.commit()
        #tag_log 테이블에 정보 저장
        tag_log(cur, image_file_name, tagID)
        con.commit()
