import sqlite3
import os
import get_data_info
import face_128vector

if __name__ == "__main__":

	# SQLite DB연결
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
		if (cnt == 0 & (i is not None)) :
			#exif 데이터 가져오기
			exif_data = get_data_info.GetDataInfo(path_dir+'/'+i)
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
			#시간 정보만 없을 경우
			elif (date_time is None and gps is not None):
				cur.execute(sql, (i, path_dir, "", "", gps[0], gps[1]))
				con.commit()
			#gps 정보만 없을 경우
			elif (gps is None and date_time is not None):
				cur.execute(sql, (i, path_dir, date_time[0], date_time[1], "", ""))
				con.commit()
			#모두 있을 경우
			else :
				cur.execute(sql, (i, path_dir, date_time[0], date_time[1], gps[0], gps[1]))
				con.commit()
				#확인용 출력문
			print('Insert data of \'' + i + '\'into Images table')
	#이후에 객체 인식, 얼굴 인식하는 함수와 연동코드 짜기

			vector_data = face_128vector.Face128Vector(i)
		#		vector_data = face_128vector.Face128Vector(i)

'''
	삭제된 이미지들을 db에서도 삭제하는 코드 추가!
'''
