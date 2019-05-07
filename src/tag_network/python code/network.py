

class picture:
	tags=[]#단순 string
	def __init__(self):
		self.tags=[]
	def findTag(self, string):
		for i in self.tags:
			if(i==string):
				return True
		return False
	def addTag(self, string):#새로 추가된다면 True반환
		for i in self.tags:
			if(i==string):
				return False
		self.tags.append(string)
		return True
	def numOfTags(self):
		return len(self.tags)
	def printTags(self):
		for i in self.tags:
			print(i)


class tagManager:
	tags=list()#tag class가 인자로서 들어간다.
	def __init__(self):
		self.tags=[]
	def addTag(self, string):
		for i in self.tags:
			if(i.name==string):
				return False
		self.tags.append(tag())
		self.tags[len(self.tags)-1].name=string
		self.tagSort()
		return True
	def getRelationship(self, tag1, tag2):#tag1이 등장할때 tag2도 같이 등장할 확률
		for i in self.tags:
			if(i.name==tag1):
				for k in i.tagRelation:
					if(k[0]==tag2):
						return k[1]/i.numOfTags
		return False#tag1 이나 #tag2중 하나 이상이 존재하지 않을경우
	def printTagName(self):
		for i in self.tags:
			print(i.name)
	def tagSort(self):
		i=0
		while(i<len(self.tags)):
			k=i
			while(k<len(self.tags)):
				if(self.tags[i].name>self.tags[k].name):
					self.tags[i],self.tags[k]=self.tags[k],self.tags[i]
				k=k+1
			i=i+1
	

class tag:
	name=""
	numOfTags=0
	tagRelation=list()# n X 2의 행열로 구성되며 1행은 string, 2행은 int
	def __init__(self):
		self.tagRelation=[]
	def addNumOfTags(self):
		self.numOfTags=self.numOfTags+1
	def addRelation(self, otherTagName):
		for i in self.tagRelation:
			if(i[0]==otherTagName):
				i[1]=i[1]+1
				return
		self.tagRelation.append([])
		self.tagRelation[len(self.tagRelation)-1].append(otherTagName)
		self.tagRelation[len(self.tagRelation)-1].append(1)
	def printRelation(self):
		for i in self.tagRelation:
			print(i[0]," : ",i[1])


class manager:	
	pictures=list()
	tagM=tagManager()
	def addTagOnPicture(self, tagName, picture):
		if(picture.addTag(tagName)):
			self.tagM.addTag(tagName)
			#picture.printTags()
			for i in picture.tags:
				for k in self.tagM.tags:
					if(i==k.name):
						k.addRelation(tagName)
						break
			for i in self.tagM.tags:
				if(i.name==tagName):
					i.addNumOfTags()
					for k in picture.tags:
						if(k!=tagName):
							i.addRelation(k)
	def addPicture(self, picture):#pictures에 특정 사진들을 추가
		self.pictures.append(picture)#########미완
		self.addTagOnPicture("User",self.pictures[len(self.pictures)-1])
	def printTag(self):
		for i in self.tagM.tags:
			print(i.name)
	def __init__(self):
		self.pictures=[]
		


