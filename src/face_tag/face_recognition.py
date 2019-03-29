import face_recognition

# 사진 파일 가져옴
file_name = 'test1.jpeg'
image = face_recognition.load_image_file(file_name)

#사진에 있는 모든 얼굴의 위치
boxes = face_recognition.face_locations(image, model="hog")
#128 벡터로 변환
encodings = face_recognition.face_encodings(image, boxes)


#소수점 이하를 얼마나 잘라서 저장할까가 문제가 될 듯 합니다....
f = open("./data.txt", 'w')
for box, encoding in zip(boxes, encodings):
    #각 박스 영역(box), 인코딩(encoding) 값을 파일로 쓰기
    for i in range(0,4):
        f.write("%d "%box[i])
    f.write("\n")
    for j in range(0,128):
        f.write("%0.20f "%encoding[j])
    f.write("\n")
    print(encoding[0])

f.close()