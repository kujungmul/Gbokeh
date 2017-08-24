package D2O_Spliter2;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Converge {

	public static double ConvergeRatio(HashSet<Node> selfSet, HashSet<Node> counterSet, Integer targetCmty, int selfSum, int counterSum) {
		double results = 0.0;
		
		/*			
			double densityRatio = Density.DensityRatio(
							node.getCommunityToNeighbors().get(node.getMainCommunity()), 
							entry.getValue(),
							cS.getCommunityInternalEdges(node.getMainCommunity()),
							cS.getCommunityInternalEdges(entry.getKey()),
							entry.getKey()
					);
			
			double coverageRatio = Converge.ConvergeRatio(
							node.getCommunityToNeighbors().get(node.getMainCommunity()), 
							entry.getValue(),
							entry.getKey(),
							cS.getCommunityContainer().get(node.getMainCommunity()).size(),
							cS.getCommunityContainer().get(entry.getKey()).size()
							);
			
		 * 
		 * */

		Set<Node> selfDistinctSet = new HashSet<Node>();
		Set<Node> counterDistinctSet = new HashSet<Node>();

		int r1Cmty;

		double r1Val;
		double r2Val;

		if(selfSet==null){
			r1Val = 1.0;
		}
		else{
			Iterator<Node> itR1 = selfSet.iterator();
			while(itR1.hasNext()){
				Node nxt = itR1.next();
				r1Cmty = nxt.getMainCommunity();	
				if(nxt.getCommunityToNeighbors().containsKey(r1Cmty))
					selfDistinctSet.addAll(nxt.getCommunityToNeighbors().get(r1Cmty));
			}
			r1Val = (double)selfDistinctSet.size();
		}


		if(counterSet==null){
			r2Val = 1.0;
		}
		else{
			Iterator<Node> itR2 = counterSet.iterator();
			while(itR2.hasNext()){
				Node nxt = itR2.next();
				
				if(nxt.getCommunityToNeighbors().containsKey(targetCmty))
					counterDistinctSet.addAll(nxt.getCommunityToNeighbors().get(targetCmty));
			}
			r2Val = (double)counterDistinctSet.size();
		}
		
		//System.out.println("[Coverage]  r1Val = "+r1Val+"\t r1Sum"+selfSum);

		
		r1Val = r1Val / selfSum;
		r2Val  = r2Val / counterSum;
		

		results = r2Val / r1Val;

		return results;
	}
	
	public static double ConvergeRatioAverage(HashSet<Node> selfSet, HashSet<Node> counterSet, CommunitySet cS, int selfIdx, int counterIdx) {
		double results = 0.0;

		Set<Node> selfDistinctSet = new HashSet<Node>();
		Set<Node> counterDistinctSet = new HashSet<Node>();

		int r1Cmty;

		double r1Val;
		double r2Val;

		if(selfSet==null){
			r1Val = 1.0;
		}
		else{
			Iterator<Node> itR1 = selfSet.iterator();
			while(itR1.hasNext()){
				Node nxt = itR1.next();
				r1Cmty = nxt.getMainCommunity();	
				if(nxt.getCommunityToNeighbors().containsKey(selfIdx))
					selfDistinctSet.addAll(nxt.getCommunityToNeighbors().get(selfIdx));
			}
			r1Val = (double)selfDistinctSet.size();
		}


		if(counterSet==null){
			r2Val = 1.0;
		}
		else{
			Iterator<Node> itR2 = counterSet.iterator();
			while(itR2.hasNext()){
				Node nxt = itR2.next();
				
				if(nxt.getCommunityToNeighbors().containsKey(counterIdx))
					counterDistinctSet.addAll(nxt.getCommunityToNeighbors().get(counterIdx));
			}
			r2Val = (double)counterDistinctSet.size();
		}
		

		//System.out.print("Coverage r1Val\t");		System.out.print(r1Val);
		//System.out.print("Coverage r2Val\t");		System.out.print(r2Val);
		
		double selfAverageDegree = cS.getAverageDegree(selfIdx);
		double counterAverageDegree = cS.getAverageDegree(counterIdx);
		
		double selfSize = selfAverageDegree * selfSet.size();
		double counterSize = counterAverageDegree * counterSet.size();
		
		
		r1Val = r1Val / selfSize;
		r2Val  = r2Val / counterSize;
		
		//System.out.print("Coverage r1Ratio\t");		System.out.print(r1Val);
		//System.out.print("Coverage r2Ratio\t");		System.out.println(r2Val);


		results = r2Val / r1Val;

		return results;
	}

}
