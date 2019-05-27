from django.db import models

class GetImagePath(models.Model):
    image_path = models.CharField(max_length=200)

    def __str__(self):
        return self.image_path

class PostImageTag(models.Model):
    tag = models.CharField(max_length=50)

    def __str(self):
        return self.tag

