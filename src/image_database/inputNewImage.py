import sqlite3
import os
import getDataInfo
import faceEncoding
import objectRecognition
import manualTag

new_image_file_list = []

#객체 인식, 얼굴 인식 파일에 새로운 이미지 목록 전달하는 함수
def pass_new_image_file_list(new_image_file_list):
		faceEncoding.face_encoding(database_file, path_dir, new_image_file_list)
		objectRecognition.object_recong(database_file, path_dir, new_image_file_list)

#위도와 경도로 지역 주소를 알아낸 다음 manualTag에 보내서 해당 이미지의 지역 태그 설정하는 함수
def set_address(i, lat, lon):
	address = getDataInfo.GetDataInfo.get_address(lat, lon)
	manualTag.input_manual_tag(database_file, path_dir, i, address)

#메인 함수
if __name__ == "__main__":

	# SQLite DB연결기
	database_file = 'test.db'
	con = sqlite3.connect(database_file)

	# connection으로 부터 cursor생성
	cur = con.cursor()

	# example_image에서 image file name 가져오기
	path_dir = './example_image'
	image_file_list = os.listdir(path_dir)

	# DB에서 모든 image file name 가져오기
	cur.execute("SELECT imageFileName FROM Images")
	image_db_list = cur.fetchall()

	#비교해서 새로운 이미지들은 db에 파일명, 위치, 시간, gps 정보 저장(insert_new_data)
	for i in image_file_list:
		cnt = 0
		for j in image_db_list:
			if (i in j):
				cnt = cnt + 1
			else:
				pass
		if (cnt == 0 & (i is not None)):
			#exif 데이터 가져오기
			exif_data = getDataInfo.GetDataInfo(path_dir + '/' + i)
			#date와 time값 가져오기
			date_time = exif_data.get_date_time()
			#gps값 가져오기
			gps = exif_data.get_lat_lon()

	#db안에 저장하는 sql문
			sql = "INSERT INTO Images(imageFileName, imageFilePath, date, time, gpsLat, gpsLon) VALUES (?, ?, ?, ?, ? ,?)"
		#시간 정보와 gps가 없을 경우
			if (date_time is None and gps is None):
				cur.execute(sql, (i, path_dir, "", "", "", ""))
				con.commit()
				pass
			#시간 정보만 없을 경우
			elif (date_time is None and gps is not None):
				cur.execute(sql, (i, path_dir, "", "", gps[0], gps[1]))
				con.commit()
				set_address(i, gps[0], gps[1])
				pass
			#gps 정보만 없을 경우
			elif (gps is None and date_time is not None):
				cur.execute(sql, (i, path_dir, date_time[0], date_time[1], "", ""))
				con.commit()
				pass
			#모두 있을 경우
			else :
				cur.execute(sql, (i, path_dir, date_time[0], date_time[1], gps[0], gps[1]))
				con.commit()
				set_address(i, gps[0], gps[1])

			#확인용 출력문
			print('Insert data of \'' + i + '\'into Images table')

			#새로운 이미지 파일 리스트에 추가
			new_image_file_list.append(i)

	#데이터베이스 닫기
	cur.close()
	con.close()

	#새로운 이미지 파일 리스트 전달
	pass_new_image_file_list(new_image_file_list)

'''
삭제된 이미지들은 db안에
'''
