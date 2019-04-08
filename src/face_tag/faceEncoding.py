import face_recognition
import sqlite3


#input_new_image 파일에서 디렉토리 이름과 새로운 이미지 파일 리스트를 받아와서 이미지 경로 fullname 리스트 리턴
def set_image_file_list(path_dir, new_image_file_list):
    image_file_name = [];
    for element in new_image_file_list:
        element = path_dir + '/' + element
        image_file_name.append(element)
    return image_file_name

#이미지 파일 이름을 입력받아 face_recongnition의 load_image_file을 사용하여 입력받은 이미지 파일의 위치를 찾아가서 이미지 return
def get_image(image_file_name):
    image = face_recognition.load_image_file(image_file_name)
    return image

#이미지를 입력받아 hog를 사용하여 얼굴 detect 하고 얼굴 위치(box) return
def get_face_box(image):
    #찾은 얼굴 위치(box)
    face_boxes = face_recognition.face_locations(image, model="hog")
    return face_boxes

#이미지와 박스(리스트) 값을 입력받아 인코딩하여 얼굴의 128개의 벡터 리스트 return
def get_face_vector(image, face_box):
    face_vector_list = face_recognition.face_encodings(image, face_box)
    return face_vector_list

#input_new_image파일에 호출되는 함수
def face_encoding(database_file, path_dir, new_image_file_list):
    #데이터베이스 열기
    con = sqlite3.connect(database_file)
    cur = con.cursor()
    #이미지 파일 full name 생성
    image_file_name = set_image_file_list(path_dir, new_image_file_list)

    for i in image_file_name:
        #각 이미지마다 face detect 및 box 생성
        image = get_image(i)
        face_boxes = get_face_box(image)
        #이미지에 detect된 얼굴이 없을 경우 pass
        if(len(face_boxes) == 0):
            pass
        #이미지에 detect된 얼굴이 있으면 vector값을 받아서 db에 저장
        else:
            face_vector_list = get_face_vector(image, face_boxes)
            face_boxes
            face_vector_list
            for n in range(0, len(face_boxes)):
                top = int(face_boxes[n][0])
                right = int(face_boxes[n][1])
                bottom = int(face_boxes[n][2])
                left = int(face_boxes[n][3])
                sql = "INSERT INTO Face_Vector_Value(imageFileName,imageTop, imageRight, imageBottom, imageLeft, vectorValue) VALUES (?, ?, ?, ?, ?, ?)"
                cur.execute(sql, (i,top, right, bottom, left, face_vector_list[n]))
                con.commit()
    #데이터베이스 닫기
    cur.close()
    con.close()
