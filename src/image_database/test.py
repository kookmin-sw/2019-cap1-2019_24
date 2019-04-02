import sqlite3
import os
import get_data_info

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

#	cur.execute("INSERT INTO Images")
	#비교해서 새로운 이미지들은 db에 파일명, 위치, 시간, gps 정보 저장(insert_new_data)
	for i in image_file_list:
		cnt = 0
		for j in image_db_list:
			if (i in j):
				cnt = cnt + 1
			else:
				pass
		if (cnt == 0) :
			#exif 데이터 가져오기
			exif_data = get_data_info.GetDataInfo(path_dir+'/'+i)
			#date와 time값 가져오기
			date_time = exif_data.get_date_time()
			#gps값 가져오기
			gps = exif_data.get_lat_lon()

	#db안에 저장
			sql = "INSERT INTO Images VALUES (?, ?, ?, ?, ?, ?)"
			cur.execute(sql, (i, path_dir, date_time[0], date_time[1], gps[0], gps[1]))
			con.commit()
	#이후에 객체 인식, 얼굴 인식하는 함수와 연동코드 짜기
