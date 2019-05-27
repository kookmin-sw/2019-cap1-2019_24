# -*- coding: utf-8 -*-

from django.shortcuts import render
import face_recognition
from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
import cv2 as cv
import numpy as np
import os

@csrf_exempt
def returnFaceInfo(request):
    path_dir = "/home/jangjieun/wittyphotos/wittyServer/media/media/"
    file_list = os.listdir(path_dir)
    imageFileName = ""
    tagDatas = {
        }
    for i in file_list:
        imageFileName = path_dir + i

#이미지 파일 이름을 입력받아 face_recongnition의 load_image_file을 사용하여 입력받은 이미지 파일의 위치를 찾아가서 이미지 return
def get_image(imageFileName):
    image = face_recognition.load_image_file(imageFileName)
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

def face_recog(imageFileName):
        # faceInfo = {
        #     'top': imageFileName,
        #     'right' : [],
        #     'bottom' : ,
        #     'left' : ,
        #     ''v
        # }
        #각 이미지마다 face detect 및 box 생성
        image = get_image(imageFileName)
        face_boxes = get_face_box(image)
        #이미지에 detect된 얼굴이 없을 경우 pass
        if(len(face_boxes) == 0):
            pass
        #이미지에 detect된 얼굴이 있으면 vector값을 받아서 db에 저장
        else:
            face_vector_list = get_face_vector(image, face_boxes)
            for n in range(0, len(face_boxes)):
                top = int(face_boxes[n][0])
                right = int(face_boxes[n][1])
                bottom = int(face_boxes[n][2])
                left = int(face_boxes[n][3])

                faceInfo = {
                    'top': int(face_boxes[n][0]),
                    'right' : int(face_boxes[n][1]),
                    'bottom' : int(face_boxes[n][2]),
                    'left' : int(face_boxes[n][3]),
                    'vector' : face_vector_list[n],
                }
