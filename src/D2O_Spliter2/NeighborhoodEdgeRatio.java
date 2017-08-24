package D2O_Spliter2;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class NeighborhoodEdgeRatio{
	public static double NeighborhoodEdgeRatio(HashSet<Node> selfSet, HashSet<Node> counterSet, Integer targetCmty){
		double result = 0.0f;
		
		Set<Node> selfDistinctSet = new HashSet<Node>();
		Set<Node> counterDistinctSet = new HashSet<Node>();

		int r1Cmty;

		double r1N = 0;
		double r2N = 0;
		
		double r2E = 0;
		double r1E = 0;

		if(selfSet==null){
			r1N = 1.0;
		}
		else{
			Iterator<Node> itR1 = selfSet.iterator();
			while(itR1.hasNext()){
				Node nxt = itR1.next();
				selfDistinctSet.add(nxt);
				r1Cmty = nxt.getMainCommunity();	
				if(nxt.getCommunityToNeighbors().containsKey(r1Cmty))
					selfDistinctSet.addAll(nxt.getCommunityToNeighbors().get(r1Cmty));
			}
			r1N = (double)selfDistinctSet.size();
			
			Iterator<Node> it2R1 =  selfDistinctSet.iterator();
			while(it2R1.hasNext()){
				Node nxt = it2R1.next();
				r1Cmty = nxt.getMainCommunity();	
				if(nxt.getCommunityToNeighbors().containsKey(r1Cmty)){
//					Set<Node> sameCmtyNodes = nxt.getCommunityToNeighbors().get(r1Cmty);
//					sameCmtyNodes.retainAll(selfDistinctSet);
					//r1E = r1E + sameCmtyNodes.size();
					
					r1E = r1E + nxt.getCommunityToNeighbors().get(r1Cmty).size();
				}
			}
		}

		if(counterSet==null){
			r2N = 1.0;
		}
		else{
			Iterator<Node> itR2 = counterSet.iterator();
			while(itR2.hasNext()){
				Node nxt = itR2.next();
				counterDistinctSet.add(nxt);
				if(nxt.getCommunityToNeighbors().containsKey(targetCmty))
					counterDistinctSet.addAll(nxt.getCommunityToNeighbors().get(targetCmty));
			}
			r2N = (double)counterDistinctSet.size();
			
			Iterator<Node> it2R2 =  counterDistinctSet.iterator();
			while(it2R2.hasNext()){
				Node nxt = it2R2.next();
				if(nxt.getCommunityToNeighbors().containsKey(targetCmty)){
//					Set<Node> sameCmtyNodes = nxt.getCommunityToNeighbors().get(targetCmty);
//					sameCmtyNodes.retainAll(counterDistinctSet);
//					r2E = r2E + sameCmtyNodes.size();
					r2E = r2E + nxt.getCommunityToNeighbors().get(targetCmty).size();
				}
			}
		}
		
		double r1Val = r1E / r1N;
		double r2Val = r2E / r2N;
//		
//		System.out.println("self average degree="+r1Val);
//		System.out.println("counter average degree="+r2Val);
//		System.out.println("self_count="+selfSet.size());
//		System.out.println("counter_count="+counterSet.size());
		
		
		r1Val = selfSet.size() / r1Val;
		r2Val = counterSet.size() / r2Val;
		
		//System.out.println("[Which one] self="+r1Val+"\tcounter="+r2Val);
		
		result = r2Val / r1Val;
//		
//		System.out.print("r1N"+r1N); System.out.print(" ");
//		System.out.print("r1E"+r1E); System.out.print(" ");
//		System.out.print("r2N"+r2N); System.out.print(" ");
//		System.out.print("r2E"+r2E); System.out.print(" \n");
//		
		
		return result;
	}
}
