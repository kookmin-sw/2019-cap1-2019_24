package Louvain;

//clone 복제하는 메소드
//여러정보를 담고 있는 객체를 clone메소드를 사용하여 같은 정보를 담고 있는 또 다른 하나의 객체 생성 가능
//clone은 cloneable 인터페이스의 추상 메소드 -> 복제할 수 있는 클래스는 cloneable 인터페이스가 구현된 클래스여야 함
public class Edge implements Cloneable{  
    int v;    
    double weight;  
    int next;   
    Edge(){}  
    public Object clone(){  
        Edge temp=null;  
        try{    
            temp = (Edge)super.clone();       
        }catch(CloneNotSupportedException e) {    
            e.printStackTrace();    
        }     
        return temp;  
    }  
}  