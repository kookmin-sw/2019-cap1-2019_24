from .serializers import imageSerializer
from rest_framework.generics import (CreateAPIView)
from uploadImage.models import MyImage
from rest_framework.response import Response
from rest_framework.decorators import api_view

class ImageCreateAPIView(CreateAPIView):
	serializer_class = imageSerializer
	queryset = MyImage.objects.all()

@api_view(['GET', 'POST'])
def ret(request):
    ret = {'res' : 'ok'
        }
    return Response(ret)