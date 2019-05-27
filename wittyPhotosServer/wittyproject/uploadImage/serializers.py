from rest_framework import serializers
from rest_framework.serializers import ModelSerializer
from uploadImage.models import MyImage

class imageSerializer(ModelSerializer):
   class Meta:
      model = MyImage
      fields = [
         'model_pic'
      ]