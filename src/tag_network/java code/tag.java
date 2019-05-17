package network;

import java.util.Vector;

public class tag {
	String name="";
	relation rel;
	int numOfTags=0;
	public void addNumOfTags()
	{
		numOfTags=numOfTags+1;
		return;
	}
	public void addRelation(String otherTagName)//연결도 추
	{
		for(int i=0;i<rel.name.size();i++)
		{
			if(otherTagName==rel.name.get(i))
			{
				rel.num.set(i, rel.num.get(i)+1);
				return;
			}
		}
		rel.name.add(otherTagName);
		rel.num.add(1);
		return;
	}
}

class relation{//다른 tag의 이름과 연결된 숫자
	Vector<String> name =new Vector<String>();
	Vector<Integer> num=new Vector<Integer>();
}