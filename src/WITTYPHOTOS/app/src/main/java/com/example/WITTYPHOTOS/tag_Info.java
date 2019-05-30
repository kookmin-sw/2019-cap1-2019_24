package com.example.WITTYPHOTOS;
import java.util.Vector;

public class tag_Info {
	static Vector<String> a = new Vector<String>();
	public float getRelationship(String tag1, String tag2, int depth)
	{
		int i = 0;
		float result=0;
		while(i<=depth)
		{
			result=result+getRel(tag1,tag2,i);
			i=i+1;
		}
		return (float) (result/Math.pow(2, depth));
	}
	public float getRel(String tag1, String tag2, int depth)
	{
		String tagName;
		if(depth==0)
		{
			for(int i=0;i<getNumberOfRelaitiveTags(tag1);i++)
			{
				tagName=getRelaitiveTagName(tag1, i);
				if(tagName==tag2)
					return getNumOfRelation(tag1,tag2)/getNumOfTag(tag1);
			}
			return 0;
		}
		else
		{
			int result=0;
			for(int i=0;i<getNumberOfRelaitiveTags(tag1);i++)
			{
				tagName=getRelaitiveTagName(tag1, i);
				if(tagName!="User" && tagName!=tag2 && tagName!=tag1)
					result=result+1;
			}
			return 0;
		}
	}
	public String getMostCloseTag(String tag)//가장 연관도가 높은 tag를 반환 
	{
		float point=0;
		float imp=0;
		String tag2;
		String output="";
		for(int i=0;i<getNumberOfRelaitiveTags(tag);i++)
		{
			tag2=getRelaitiveTagName(tag, i);
			if(tag2!=tag && tag2!="User")
			{
				imp=getRelationship(tag, tag2, 3);
				if(imp>point)
				{
					point=imp;
					output=tag2;
				}
			}
		}
		return output;
	}
	public void recommendTag(int num)//num번 사진에 추천할 태그들을 결
	{
		Vector<Vector<String>> taglist = new Vector<Vector<String>>();
		boolean bool=true;
		Vector<String> picturesTag=getPicturesTags(num);
		Vector<String> allTags=getAllTags();
		for(int v1=0; v1<picturesTag.size();v1++)
		{
			if(picturesTag.get(v1)!="User")
			{
				for(int v2=0;v2<allTags.size();v2++)
				{
					if(allTags.get(v2)!=picturesTag.get(v1) && allTags.get(v2)!="User")
					{
						bool=true;
						for(int v3=0;v3<taglist.size();v3++)
						{
							if(taglist.get(v3).get(0)==allTags.get(v2))
							{
								taglist.get(v3).set(1,String.valueOf(Float.parseFloat(taglist.get(v3).get(1)) +
										getRel(picturesTag.get(v1), allTags.get(v2), 2)));
								bool=false;
								break;
							}
						}
						if(bool && getRel(picturesTag.get(v1), allTags.get(v2), 2)!=0 && 
								checkExist(picturesTag, allTags.get(v2))==false)
						{
							taglist.add(new Vector<String>());
							taglist.get(taglist.size()-1).add(allTags.get(v2));
							taglist.get(taglist.size()-1).add(String.valueOf(getRel(picturesTag.get(v1), 
									allTags.get(v2), 2)));
						}
					}
				}
			}
		}
		Vector<String> imVar=null;
		for(int i=0;i<taglist.size();i++)
		{
			for(int k=i;k<taglist.size();k++)
			{
				if(Float.parseFloat(taglist.get(i).get(1))<Float.parseFloat(taglist.get(k).get(1)))
				{
					imVar=taglist.get(i);
					taglist.set(i, taglist.get(k));
					taglist.set(k, imVar);
				}
			}
		}
	}
	public int getNumberOfRelaitiveTags(String tag1)//tag1의 이름을 하고 있는 테그와 연결된 테그의 숫자를 반환
	{
		return 0;
	}
	public String getRelaitiveTagName(String tag1, int num)//tag1의 이름을 하고있는 태그와 num번째로 연결된 테그 이름을 반환
	{
		return "";	
	}
	public int getNumOfRelation(String tag1, String tag2)//tag1이 tag2와 연결된 개수를 반환
	{
		return 0;
	}
	public int getNumOfTag(String tag1)//tag1의 전체 개수를 반환
	{
		return 0;
	}
	public Vector<String> getPicturesTags(int num)//num번째 사진에 달려있는 tag들을 반환
	{
		return null;
	}
	public Vector<String> getAllTags()//해당 DB에 존재하는 모든 tag들을 반환 
	{
		return null;
	}
	public boolean checkExist(Vector<String> list, String str)//list에 str이 있는지 확인
	{
		for(int i = 0; i<list.size();i++)
		{
			if(list.get(i)==str)
				return true;
		}
		return false;
	}
}
