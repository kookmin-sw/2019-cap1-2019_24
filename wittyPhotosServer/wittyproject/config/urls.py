from django.contrib import admin
from django.urls import include, path
from django.conf.urls import url,include
from django.conf import settings
from django.conf.urls.static import static
from uploadImage import views as v1
from objectDetect import views as v2
from faceRecog import views as v3

urlpatterns = [
#    path('', v1.ImageCreateAPIView.as_view()),
    path('uploadImage/', v1.ImageCreateAPIView.as_view()),
    path('objectDetect/', v2.returnAutoTag),
    path('faceRecog/', v3.returnFaceInfo),
] + static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)
