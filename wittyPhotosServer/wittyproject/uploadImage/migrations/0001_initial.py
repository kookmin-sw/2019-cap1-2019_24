# Generated by Django 2.1 on 2019-05-26 00:15

from django.db import migrations, models


class Migration(migrations.Migration):

    initial = True

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='NewImage',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('imageName', models.CharField(max_length=50)),
                ('newImage', models.ImageField(upload_to='images/')),
            ],
        ),
    ]
