from darkflow.net.build import TFNet
import cv2

#YOLO darkflow로 구현한 객체 인식 함수
def object_recog(database_file, path_dir, new_image_file_list):
    
    options = {"model":"cfg/yolo.cfg", "load":"bin/yolov2.weights","threshold":0.1}
    tfnet = TFNet(options)
    
    for i in range(0, len(new_image_file_list)):
        img = path_dir + i
        imgcv = cv2.imread(img)
	result = tfnet.return_predict(imgcv)        
	print(result) #json형태로 출력 -> 추후 태그만 db에 담는것으로 수정
