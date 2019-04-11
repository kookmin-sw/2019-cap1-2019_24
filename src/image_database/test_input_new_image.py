'''
시연용 코드입니다
'''


# -*- coding: utf-8 -*-
import sqlite3
import os
import getImageInfo
import faceEncoding
#import objectRecognition
import manualTag

new_image_file_list = []

#객체 인식, 얼굴 인식 파일에 새로운 이미지 목록 전달하는 함수
def pass_new_image_file_list(new_image_file_list):
	#얼굴 인식에 전달
	faceEncoding.face_encoding(database_file, path_dir, new_image_file_list)
	#객체 인식에 전달
#	objectRecognition.object_recog(database_file, path_dir, new_image_file_list)

#위도와 경도로 지역 주소를 알아낸 다음 manualTag에 보내서 해당 이미지의 지역 태그 설정
def set_address(i, lat, lon):
	address = getImageInfo.GetDataInfo.get_address(lat, lon)
	manualTag.input_manual_tag(database_file, path_dir, i, address[0], 6)
	manualTag.input_manual_tag(database_file, path_dir, i, address[1], 6)

#새로운 이미지 파일들의 파일명, 시간, gps 정보를 저장하고 새로운 이미지 리스트(파일명) 리턴
def input_new_image(con, path_dir):
	cur = con.cursor()
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
			exif_data = getImageInfo.GetDataInfo(path_dir + '/' + i)
			#date와 time값 가져오기
			date_time = exif_data.get_date_time()
			#gps값 가져오기
			gps = exif_data.get_lat_lon()

		#db안에 저장하는 sql문
			sql = "INSERT INTO Images(imageFileName, imageFilePath, date, time, gpsLat, gpsLon) VALUES (?, ?, ?, ?, ? ,?)"
		#시간 정보와 gps가 없을 경우
			if (date_time is None and gps[0] is None):
				cur.execute(sql, (i, path_dir, "", "", "", ""))
				con.commit()
			#시간 정보만 없을 경우
			elif (date_time is None and gps[0] is not None):
				cur.execute(sql, (i, path_dir, "", "", gps[0], gps[1]))
				con.commit()
				set_address(i, gps[0], gps[1])
			#gps 정보만 없을 경우
			elif (gps[0] is None and date_time is not None):
				cur.execute(sql, (i, path_dir, date_time[0], date_time[1], "", ""))
				con.commit()
			#모두 있을 경우
			else :
				cur.execute(sql, (i, path_dir, date_time[0], date_time[1], gps[0], gps[1]))
				con.commit()
				set_address(i, gps[0], gps[1])

			#새로운 이미지 파일 리스트에 추가
			new_image_file_list.append(i)
			print("새로운 이미지 파일 : "+ i)
	print(">>>%d개의 이미지가 입력되었습니다"%len(new_image_file_list))
	print(">>>총 %d개의 이미지가 있습니다"%len(image_db_list))
	print("")
	#new_image_file_list 리턴
	return new_image_file_list

def show_all_images(con):
	cur = con.cursor()
	cur.execute("SELECT imageFileName FROM Images")
	rows = cur.fetchall()
	for row in rows:
		print(row[0])

#이미지에 지정된 모든 태그를 보여주는 함수
def show_tag(con, img):
	cur = con.cursor()
	img = '\'' + img + '\''
	#이미지에 지정 태그가 있는지 확인
	cur.execute("SELECT COUNT(*) FROM Tag_log WHERE imageFileName = " + img)
	cnt = cur.fetchone()

	#만약 태그가 존재하면 태그리스트 출력
	if(cnt[0] != 0):
		cur.execute("SELECT autoTagID, manualTagID, faceTagID FROM Tag_log WHERE imageFileName = " + img)
		tagIDs = cur.fetchall()
		for tagID in tagIDs:
			#autoTag 출력
			if(tagID[0] is not None):
				nID = tagID[0]
				cur.execute("SELECT KoreanTag, categoryID FROM Object_Auto_tag WHERE tagID = '" + nID + "'")
				nTag = cur.fetchall()
				cur.execute("SELECT category_name FROM Category WHERE categoryID = %d"%nTag[0][1])
				category = cur.fetchall()
				print("tag : " + nTag[0][0] + " | 카테고리 : " + str(category[0][0]))
			#manualTag 출력
			if(tagID[1] is not None):
				mID = tagID[1]
				cur.execute("SELECT tag, categoryID FROM Object_manual_tag WHERE tagID = '" + mID + "'")
				mTag = cur.fetchall()
				cur.execute("SELECT category_name FROM Category WHERE categoryID = %d"%mTag[0][1])
				category = cur.fetchall()
				print("tag : " + mTag[0][0] + " | 카테고리 : " + str(category[0][0]))
			#faceTag 출력
			if(tagID[2] is not None):
				fID = tagID[2]
				cur.execute("SELECT tag, categoryID FROM Face_tag WHERE tagID = '" + fID + "'")
				fTag = cur.fetchall()
				cur.execute("SELECT category_name FROM Category WHERE categoryID = %d"%fTag[0][1])
				category = cur.fetchall()
				print("tag : " + fTag[0][0] + " | 카테고리 : " + str(category[0][0]))

	#존재하지 않으면 no tag 출력
	else:
		print(img + " has no tag!")
		return 0

def add_tag(con, img):
	cur = con.cursor()

	tag = input("추가할 태그를 입력하세요 : ")
	print("카테고리를 선택하세요")
	print("인물 | 사물 | 동물 | 식물 | 음식 | 지역 | 장소 | 색깔 | 기타")
	category = input("카테고리 :")
	if(category == "인물"):
		cur.execute("SELECT imageLeft, imageTop, imageRight, imageBottom FROM Face_vector_value WHERE imageFileName = " + path_dir + img)
		faces = cur.fetchall()
		for face in faces:
			print(face)
	else:
		cur.execute("SELECT categoryID FROM Category WHERE category_name = '" + category +"'" )
		categoryID = cur.fetchone()
		manualTag.input_manual_tag(database_file, img, tag, categoryID[0])

def delete_tag(con, img):
	tag = input("삭제할 태그를 입력하세요 : ")
	cur = con.cursor()
	tag = '\'' + tag + '\''
	img = '\'' + img + '\''
	cur.execute("SELECT COUNT(*) FROM Object_Auto_tag WHERE KoreanTag = " + tag)
	cnt1 = cur.fetchone()
	cur.execute("SELECT COUNT(*) FROM Object_manual_tag WHERE tag = " + tag)
	cnt2 = cur.fetchone()
	cur.execute("SELECT COUNT(*) FROM Face_tag WHERE name = " + tag)
	cnt3 = cur.fetchone()

	if(cnt1[0] != 0):
		cur.execute("SELECT tagID, tagCount FROM Object_Auto_tag WHERE KoreanTag = " + tag)
		t = cur.fetchone()
		tagID = t[0]
		tagID = '\'' + tagID + '\''
		tagCount = t[1]
		if (tagCount == 1):
			cur.execute("DELETE FROM Object_Auto_tag WHERE tagCount = 1 AND tagID = " + tagID)
			con.commit()
		else:
			cur.execute("UPDATE Object_Auto_tag SET tagCount = tagCount - 1 WHERE tagID = " + tagID)
			con.commit()
		cur.execute("DELETE FROM Tag_log WHERE imageFileName = " + img + "AND autoTagID = " + tagID)
		con.commit()

	elif(cnt2[0] != 0):
		cur.execute("SELECT tagID, tagCount FROM Object_manual_tag WHERE tag = " + tag)
		t = cur.fetchone()
		tagID = t[0]
		tagID = '\'' + tagID + '\''
		tagCount = t[1]
		if (tagCount == 1):
			cur.execute("DELETE FROM Object_manual_tag WHERE tagCount = 1 AND tagID = " + tagID)
			con.commit()
		else:
			cur.execute("UPDATE Object_manual_tag SET tagCount = tagCount - 1 WHERE tagID = " + tagID)
			con.commit()
		cur.execute("DELETE FROM Tag_log WHERE imageFileName = " + img + "AND manualTagID = " + tagID)
		con.commit()

	elif(cnt3[0] != 0):
		cur.execute("SELECT tagID, tagCount FROM Face_tag WHERE name = " + tag)
		t = cur.fetchone()
		tagID = t[0]
		tagID = '\'' + tagID + '\''
		tagCount = t[1]
		if (tagCount == 1):
			cur.execute("DELETE FROM Face_tag WHERE tagCount = 1 AND tagID = " + tagID)
			con.commit()
		else:
			cur.execute("UPDATE Face_tag SET tagCount = tagCount - 1 WHERE name = " + tagID)
			con.commit()
		cur.execute("DELETE FROM Tag_log WHERE imageFileName = " + img + "AND faceTagID = " + tagID)
		con.commit()

	else:
		print("해당 태그가 없습니다")

def show_pie(con):
	return 0
def show_network(con):
	return 0
#메인 함수
if __name__ == "__main__":

	print("--------------WITTY PHOTOS----------------")
	print(">>>동기화를 시작합니다")

	# SQLite DB연결
	database_file = 'test.db'
	con = sqlite3.connect(database_file)

	# example_image에서 image file name 가져오기
	path_dir = './example_image'

	#input_new_image 함수에 db와 path_dir전달해서 new_image_file_list 받기
	new_image_file_list = input_new_image(con, path_dir)
	#새로운 이미지 파일 리스트 전달
	pass_new_image_file_list(new_image_file_list)

	run = 1
	while(run):
		print("1. 전체 사진 보기")
		print("2. 태그 통계 보기")
		print("*종료 : 0")
		c1 = int(input("번호를 입력하세요: "))
		print("")
		if(c1 == 1):
			runImage = 1
			while(runImage):
				print(">>>전체 사진")
				show_all_images(con)
				print(">>>")
				img = str(input("원하는 사진을 입력하세요\n뒤로 가기 : -1\n사진 파일명: "))
				if(img == str(-1)):
					runImage = 0
				else :
					res = show_tag(con, img)

					print("\n1. 태그 추가")
					print("2. 태그 삭제")
					print("*뒤로 가기 : -1")
					print("*종료 : 0")
					ic = int(input("번호를 입력하세요: "))
					if(ic == 1):
						add_tag(con, img)
						print("추가되었습니다")
						show_tag(con, img)
						ic2 = int(input("*뒤로 가기 : -1 "))
						if (ic2 == -1):
							pass
					elif(ic == 2):
						if(res == 0):
							print("삭제할 태그가 없습니다")
						else:
							delete_tag(con, img)
							print("삭제되었습니다")
							show_tag(con, img)
							ic2 = int(input("*뒤로 가기 : -1 "))
							if (ic2 == -1):
								pass
					elif(ic == -1):
						pass
					elif(ic == 0):
						print("------------------종료합니다----------------")
						runImage = 0
						run = 0

		elif(c1 == 2):
			runGrapth = 1
			while(runGrapth):
				print("1. 파이 그래프")
				print("2. 네트워크 그래프")
				print("*뒤로가기 : -1")
				print("*종료 : 0\n")
				c2 = int(input(">>>번호를 입력하세요.: "))
				if(c2 == 1):
					print(">>>파이 그래프")
					show_pie(con)
					print("\n*뒤로가기 : -1")
					print("*종료 : 0")
					c3 = int(input("번호를 입력하세요: "))
					if(c3 == -1):
						pass
				elif(c2 == 2):
					print(">>>네트워크 그래프")
					show_network(con)
					print("\n*뒤로가기 : -1")
					print("*종료 : 0")
					c3 = int(input("번호를 입력하세요: "))
					if(c3 == -1):
						pass
				elif(c2 == -1):
					runGrapth = 0
					pass
				elif(c2 == 0):
					run = 0
					runGrapth = 0
					print("------------------종료합니다----------------")
				else:
					print("잘못된 입력입니다")
		elif(c1 == 0):
			run = 0
			print("------------------종료합니다----------------")
		else:
			print("잘못된 입력입니다")
