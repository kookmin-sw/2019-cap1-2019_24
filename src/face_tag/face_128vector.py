import face_recognition
import pickle

# 사진 파일 가져옴

file_name = 'test4.jpg'
image = face_recognition.load_image_file(file_name)

boxes = face_recognition.face_locations(image, model="hog")
#128 벡터로 변환
encodings = face_recognition.face_encodings(image, boxes)

print("내가 찾은 얼굴 개수 : %d" % (len(boxes)))

f = open("./testdata.txt", 'wb')
f2 = open("./testdata2.txt", 'ab')
for box, encoding in zip(boxes, encodings):
    #각 박스 영역(box), 인코딩(encoding) 값을 파일로 쓰기
    pickle.dump(box, f)
    pickle.dump(box, f2)
    pickle.dump(encoding, f)
    pickle.dump(encoding, f2)

f.close()
f2.close()

# 테스트
with open('testdata2.txt', 'rb') as p:
    try:
        while True:
            data = pickle.load(p)
            print(data, end=" ")

    except EOFError:
        print("끝입니다")