# -*- coding: utf-8 -*-
from __future__ import unicode_literals
from django.db import models
from rest_framework.response import Response

class MyImage(models.Model):
	model_pic = models.ImageField(upload_to = 'media', default = 'none/no-img.jpg')
	def res(request):
	    return Response('yes')