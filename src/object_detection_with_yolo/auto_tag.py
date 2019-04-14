import sqlite3

#auto tag에 태그 저장
#어플리케이션 설치 후 처음 한번만 작동
def input_labels():
    file = open('coco_english.txt', 'r')
    all = file.read()
    file.close()
    eng_tags = all.split('\n')
    file = open('./coco_korean.txt', 'r')
    all = file.read()
    file.close()
    kor_tags = all.split('\n')

    con = sqlite3.connect('test.db')
    cur = con.cursor()

    cnt = 1
    for i in range(0, len(eng_tags)-1):

        tagID = str(cnt).zfill(8)
        tagID = 'n' + tagID

        categoryId = int(input('카테고리:'))
        sql = "INSERT INTO Object_Auto_tag(tagID, EnglishTag, KoreanTag, tagCount, tagState, categoryID) VALUES (?, ?, ?, ?, ?, ?)"
        cur.execute(sql, (tagID, eng_tags[i], kor_tags[i], 0, 1, categoryId))
        con.commit()

        cnt = cnt + 1

#tagState를 찾고 리턴한다.
def find_state(tag, cur):
    sql = "SELECT tagState FROM Object_Auto_tag WHERE EnglishTag = "
    cur.execute(sql + tag)
    return (int(cur.fetchone()[0]))

#중복여부 검사함수
def if_duplicate(i, tagID, cur):
    cur.execute("SELECT * FROM Tag_log WHERE imageFileName = " + i )
    rows = cur.fetchall()
    if(len(rows) == 0):
        return 0
    else:
        cur.execute("SELECT autoTagID FROM Tag_log WHERE imageFileName = "+ i)
        tags = cur.fetchall()
        tag_id = tags[0][0]
        if(tag_id in tagID):
            return 1
        else:
            return 0

#auto tag의 tagcount값을 증가시키고 tag_log테이블에 정보를 저장한다.
def auto_tag(database_file, path_dir, i, tag):
    con = sqlite3.connect(database_file)
    cur = con.cursor()

    tag = '\'' + tag + '\''
    img = '\'' + i + '\''

    sql = "SELECT tagID FROM Object_Auto_tag WHERE EnglishTag = "
    cur.execute(sql + tag)
    tagID = cur.fetchone()[0]

    #중복여부를 검사한다.
    if (if_duplicate(img, tagID, cur) == 0 ):
        #tagState가 1일경우만 실행한다.
        if(find_state(tag, cur) == 1):
             sql = "UPDATE Object_Auto_tag SET tagCount = tagCount + 1 WHERE EnglishTag = "
             cur.execute(sql + tag)
             con.commit()
             sql = "INSERT INTO Tag_log(imageFileName, autoTagID) VALUES(?,?)"
             cur.execute(sql, (i, tagID))
             con.commit()
