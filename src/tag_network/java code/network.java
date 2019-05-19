package network;
import java.util.Vector;


public class network {
	static Vector<Integer> a = new Vector<Integer>();
	public static void main(String[] args) 
	{
		a.add(10);
		//a.set(0, 1);
		System.out.println(a.get(0));
	}
	public float getRelationship(String tag1, String tag2, int depth)
	{
		int i = 0;
		int result=0;
		while(i<=depth)
		{
			result=result+getRel(tag1,tag2,i);
			i=i+1;
		}
		return (float) (result/Math.pow(2, depth));
	}
	public int getRel(String tag1, String tag2, int depth)
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
	public int getNumberOfRelaitiveTags(String tag1)//tag1의 이름을 하고 있는 테그와 연결된 테그의 숫자를 반환
	{
		return 0;
	}
	public String getRelaitiveTagName(String tag1, int num)//tag1의 이름을 하고있는 태그와 num번째로 연결된 테그 이름을 반
	{
		return "";
	}
	public int getNumOfRelation(String tag1, String tag2)//tag1이 tag2와 연결된 개수를 반환
	{
		return 0;
	}
	public int getNumOfTag(String tag1)//tag1의 전체 개수를 반
	{
		return 0;
	}
}