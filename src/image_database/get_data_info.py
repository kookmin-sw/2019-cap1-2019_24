import sqlite3
from PIL import Image
from PIL.ExifTags import TAGS, GPSTAGS

class GetDataInfo(object):

    database_file = 'test.db'
    exif_data = None
    image = None

    #init함수
    def __init__(self, img_path):
        self.image = Image.open(img_path)
        self.get_exif_data()
        super(GetDataInfo, self).__init__()


    #exif 데이터 디코딩
    def get_exif_data(self):
        exif_data = {}
        info = self.image._getexif()

        #exif 데이터가 있는 경우 라벨과 값 디코딩
        if info:
            for label, value in info.items():
                decoded = TAGS.get(label, label)
                #gps info가 있는경우
                if decoded == "GPSInfo":
                    gps_data = {}
                    for v in value:
                        sub_decoded = GPSTAGS.get(v, v)
                        gps_data[sub_decoded] = value[v]
                    exif_data[decoded] = gps_data
                else:
                    exif_data[decoded] = value

        self.exif_data = exif_data
        return exif_data

    def get_if_exist(self, data, key):
        if key in data:
            return data[key]
        return None

    #gps값 경도, 위도로 바꾸기
    def get_lat_lon(self):

        lat = None #위도
        lon = None #경도

        #현재 데이터의 exif 데이터 저장
        exif_data = self.get_exif_data()

        #GPSInfo가 있는지 확인하고 각 위도, 위도 레퍼런스, 경도, 경도레퍼런스 레이블 확인
        if ('GPSInfo' in exif_data):
            gps_info = exif_data["GPSInfo"]
            gps_lat =  self.get_if_exist(gps_info, 'GPSLatitude')
            gps_lat_ref = self.get_if_exist(gps_info, 'GPSLatitudeRef')
            gps_lon = self.get_if_exist(gps_info, 'GPSLongitude')
            gps_lon_ref = self.get_if_exist(gps_info, 'GPSLongitudeRef')

            #위도, 경도가 모두 존재하는 경우 lat, lon값 저장
            if (gps_lat and gps_lat_ref and gps_lon and gps_lon_ref):
                lat_d0 = gps_lat[0][0]
                lat_d1 = gps_lat[0][1]
                lat_d = float(lat_d0) / float(lat_d1)
                lon_d0 = gps_lon[0][0]
                lon_d1 = gps_lon[0][1]
                lon_d = float(lon_d0) / float(lon_d1)

                lat_m0 = gps_lat[1][0]
                lat_m1 = gps_lat[1][1]
                lat_m = float(lat_m0) / float(lat_m1)
                lon_m0 = gps_lon[1][0]
                lon_m1 = gps_lon[1][1]
                lon_m = float(lon_m0) / float(lon_m1)

                lat_s0 = gps_lat[2][0]
                lat_s1 = gps_lat[2][1]
                lat_s = float(lat_s0) / float(lat_s1)
                lon_s0 = gps_lon[2][0]
                lon_s1 = gps_lon[2][1]
                lon_s = float(lon_s0) / float(lon_s1)

                lat = lat_d + (lat_m/60.0) + (lat_s/3600.0)
                lon = lon_d + (lon_m/60.0) + (lon_s/3600.0)

                if(gps_lat_ref != "N"): lat = 0 - lat
                if(gps_lon_ref != "E"): lon = 0 - lon

        return lat, lon

    def get_date_time(self):
        if 'DateTime' in self.exif_data:
            date_time = self.exif_data['DateTime']
        #날짜와 시간을 따로 분리해서 return한다.
        return date_time[:10], date_time[10:]
