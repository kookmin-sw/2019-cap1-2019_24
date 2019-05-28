from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
import cv2 as cv
import numpy as np
import os


# Initialize the parameters
confThreshold = 0.5  # Confidence threshold
nmsThreshold = 0.4  # Non-maximum suppression threshold
inpWidth = 416  # Width of network's input image
inpHeight = 416  # Height of network's input image

# Load names of classes
classesFile = "/home/jangjieun/wittyphotos/wittyServer/objectDetect/coco.names"
classes = None
with open(classesFile, 'rt') as f:
    classes = f.read().rstrip('\n').split('\n')

# Give the configuration and weight files for the model and load the network using them.
modelConfiguration = "/home/jangjieun/wittyphotos/wittyServer/objectDetect/yolov3.cfg"
modelWeights = "/home/jangjieun/wittyphotos/wittyServer/objectDetect/yolov3.weights"

net = cv.dnn.readNetFromDarknet(modelConfiguration, modelWeights)
net.setPreferableBackend(cv.dnn.DNN_BACKEND_OPENCV)
net.setPreferableTarget(cv.dnn.DNN_TARGET_CPU)


@csrf_exempt
def returnAutoTag(request):
    path_dir = "/home/jangjieun/wittyphotos/wittyServer/media/media/"
    file_list = os.listdir(path_dir)
    tagInfoList = {
        "tagDatas" : []
        }
    for i in file_list:
        tagData = object_detect(i, path_dir)
        tagInfoList["tagDatas"].append(tagData)
    return JsonResponse(tagInfoList)

# Get the names of the output layers
def getOutputsNames(net):
    # Get the names of all the layers in the network
    layersNames = net.getLayerNames()
    # Get the names of the output layers, i.e. the layers with unconnected outputs
    return [layersNames[i[0] - 1] for i in net.getUnconnectedOutLayers()]

# Remove the bounding boxes with low confidence using non-maxima suppression
def postprocess(frame, outs):
    frameHeight = frame.shape[0]
    frameWidth = frame.shape[1]

    # Scan through all the bounding boxes output from the network and keep only the
    # ones with high confidence scores. Assign the box's class label as the class with the highest score.
    classIds = []
    confidences = []
    boxes = []
    for out in outs:
        for detection in out:
            scores = detection[5:]
            classId = np.argmax(scores)
            confidence = scores[classId]
            if confidence > confThreshold:
                center_x = int(detection[0] * frameWidth)
                center_y = int(detection[1] * frameHeight)
                width = int(detection[2] * frameWidth)
                height = int(detection[3] * frameHeight)
                left = int(center_x - width / 2)
                top = int(center_y - height / 2)
                classIds.append(classId)
                confidences.append(float(confidence))
                boxes.append([left, top, width, height])
    # Perform non maximum suppression to eliminate redundant overlapping boxes with
    # lower confidences.
    indices = cv.dnn.NMSBoxes(boxes, confidences, confThreshold, nmsThreshold)
    for i in indices:
        i = i[0]
        box = boxes[i]
        left = box[0]
        top = box[1]
        width = box[2]
        height = box[3]
    return classIds

def object_detect(imageFileName, path_dir):
    f_imageFileName = path_dir + imageFileName
    img = cv.imread(f_imageFileName,1)
    blob = cv.dnn.blobFromImage(img, 1 / 255, (inpWidth, inpHeight), [0, 0, 0], 1, crop=True)
    net.setInput(blob)
    outs = net.forward(getOutputsNames(net))
    classIds = postprocess(img, outs)
    tagData = {
        'imageFileName' : imageFileName,
        'tagList' : [],
        }
    for classId in classIds:
        tagData['tagList'].append(classes[classId])
    return tagData
