package D2O_Spliter2;

import java.util.HashSet;
import java.util.Iterator;

public class Utility {
	public static double calcAverage(HashSet<Node> k, int selfIdx){
		double sum = 0;
		
		 Iterator<Node> it = k.iterator();
		 while(it.hasNext()){
			 //sum += it.next().getDegree();
			 sum += it.next().getCommunityToNeighbors().get(selfIdx).size();
		 }
		return sum / k.size();
	}
}
