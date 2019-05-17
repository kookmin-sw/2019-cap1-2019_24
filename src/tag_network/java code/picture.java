package network;
import java.util.Vector;

public class picture {
	private Vector<String> tags = new Vector<String>();//사진이 가지고 있는 tag들을 저
	public boolean findTag(String string)//특정 tag가 tags의 어디에 있는지 탐
	{
		for(int i=0;i<tags.size();i++)
		{
			if(string==tags.get(i))
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean addTag(String string)
	{
		for(int i=0;i<tags.size();i++)
		{
			if(string==tags.get(i))
			{
				return false;
			}
		}
		tags.add(string);
		return true;
	}
	
	public int numOfTags()
	{
		return tags.size();
	}
}