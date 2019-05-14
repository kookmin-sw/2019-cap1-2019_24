from django.shortcuts import render
from django.http import HttpResponse, JsonResponse
import json
import cv2 as cv
import numpy as np
from objectDetect.models import GetImagePath, PostImageTag

# Initialize the parameters
confThreshold = 0.5  # Confidence threshold
nmsThreshold = 0.4  # Non-maximum suppression threshold
inpWidth = 416  # Width of network's input image
inpHeight = 416  # Height of network's input image

# Load names of classes
classesFile = "./objectDetect/coco.names"
classes = None
with open(classesFile, 'rt') as f:
    classes = f.read().rstrip('\n').split('\n')

# Give the configuration and weight files for the model and load the network using them.
modelConfiguration = "./objectDetect/yolov3.cfg"
modelWeights = "./objectDetect/yolov3.weights"

net = cv.dnn.readNetFromDarknet(modelConfiguration, modelWeights)
net.setPreferableBackend(cv.dnn.DNN_BACKEND_OPENCV)
net.setPreferableTarget(cv.dnn.DNN_TARGET_CPU)


def returnAutoTag(request):

    imagePath = request.
    return JsonResponse({'imagePath': imagePath})

#    return JsonResponse({'imagePath': 'imagePath'})
#    imagePaths = json.loads(request.body)
#    imageFileList = []
#    for i in imagePaths['imagePath']:
#        imageFileList += i
#    return HttpResponse(object_detect(imageFileList))


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


def object_detect(imageFileList):
    cv.namedWindow('image', cv.WINDOW_NORMAL)

    for imgFileName in imageFileList:
        img = cv.imread(imgFileName,1)

        blob = cv.dnn.blobFromImage(img, 1 / 255, (inpWidth, inpHeight), [0, 0, 0], 1, crop=False)
        net.setInput(blob)
        outs = net.forward(getOutputsNames(net))
        classIds = postprocess(img, outs)
        tags = []

        for classId in classIds:
            tags += classes[classId]

        tagData = {
            'imageFileName': imgFileName,
            'tags': tags
        }
        jsonString = json.dumps(tagData)

        return jsonString
#        shrink = cv.resize(img, None, fx=0.15, fy=0.15, interpolation=cv.INTER_AREA)
 #       cv.imshow(imgFileName, shrink)
#        cv.waitKey(0)
#        cv.destroyAllWindows()
