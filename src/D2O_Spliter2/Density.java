package D2O_Spliter2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

public class Density {
	public static double DensityRatio(HashSet<Node> selfSet, HashSet<Node> counterSet, int selfSize, int counterSize, Integer targetCmty) {
		double result = 0.0f;
		
		/*
		 * 			
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
			
		 * */
		
		//R1 = self
		//R2 = counter
		double r1Ratio;
		double r2Ratio;
		
		int r1Sum = 0;
		int r2Sum = 0;
		
		HashMap<Integer, HashSet<Integer>> startToEndEdgesSelf = new HashMap<Integer,HashSet<Integer>>();
		HashMap<Integer, HashSet<Integer>> startToEndEdgesCounter = new HashMap<Integer,HashSet<Integer>>();
		
		
		int r1Cmty, r2Cmty;
				
		if(selfSet==null){
			r1Sum = 1;
		}
		else{
			Iterator<Node> itR1 = selfSet.iterator();
			while(itR1.hasNext()){
				Node nxt = itR1.next();
				r1Cmty = nxt.getMainCommunity();
				
				if(nxt.getCommunityToNeighbors().containsKey(r1Cmty)){
					//r1Sum += nxt.getCommunityToNeighbors().get(r1Cmty).size(); 
					Iterator<Node> it = nxt.getCommunityToNeighbors().get(r1Cmty).iterator();
					while(it.hasNext()){
						Node cur = it.next();
						int st,  end;
						if(nxt.getElementId() < cur.getElementId()){
							st = nxt.getElementId();
							end = cur.getElementId();
						}
						else{
							st = cur.getElementId();
							end = nxt.getElementId();
						}
					//	System.out.println("st/end : "+st+"\t"+end);

						if(startToEndEdgesSelf.containsKey(st)){
							startToEndEdgesSelf.get(st).add(end);
						}
						else{
							HashSet<Integer> k = new HashSet<Integer>();
							k.add(end);
							startToEndEdgesSelf.put(st, k);
						}
					}
				}				
			}
		}

		if(counterSet==null){
			r2Sum = 1;
		}
		else{
			Iterator<Node> itR2 = counterSet.iterator();
			while(itR2.hasNext()){
				Node nxt = itR2.next();
				
				if(nxt.getCommunityToNeighbors().containsKey(targetCmty)){
					//r2Sum += nxt.getCommunityToNeighbors().get(targetCmty).size(); 	
					Iterator<Node> it = nxt.getCommunityToNeighbors().get(targetCmty).iterator();
					while(it.hasNext()){
						Node cur = it.next();
						int st,  end;
						if(nxt.getElementId() < cur.getElementId()){
							st = nxt.getElementId();
							end = cur.getElementId();
						}
						else{
							st = cur.getElementId();
							end = nxt.getElementId();
						}
					//	System.out.println("st/end : "+st+"\t"+end);
						if(startToEndEdgesCounter.containsKey(st)){
							startToEndEdgesCounter.get(st).add(end);
						}
						else{
							HashSet<Integer> k = new HashSet<Integer>();
							k.add(end);
							startToEndEdgesCounter.put(st, k);
						}
					}
				}
			}
		}
		
		if(selfSize==0)
			selfSize = 1;
		
		if(counterSize==0)
			counterSize = 1;
		
		r1Sum = 0;
		r2Sum = 0;
		
		for(Entry<Integer, HashSet<Integer>> entry : startToEndEdgesSelf.entrySet()){
			r1Sum += entry.getValue().size();
		}
		
		for(Entry<Integer, HashSet<Integer>> entry : startToEndEdgesCounter.entrySet()){
			r2Sum += entry.getValue().size();
		}
		
		selfSize = selfSize/2;
		counterSize = counterSize/2;

		double r1Val = (double)r1Sum / selfSize ;
		double r2Val = (double)r2Sum / counterSize ;
//		
//		double r1Val = r1Sum;
//		double r2Val = r2Sum;
		
//////		
//		System.out.print("r1Sum\t");		System.out.print(r1Sum);
////		System.out.print("/ r2Sum\t");		System.out.print(r2Sum);
////////		
////////
//		System.out.print("/ selfSize\t");		System.out.print(selfSize+"\n");
////		System.out.print("/ counterSize\t");		System.out.println(counterSize);
//////		
////
//		System.out.print("/ r1Val\t");		System.out.print(r1Val);
//		System.out.print("/ r2Val\t");		System.out.println(r2Val);

		
		
		result = r2Val/r1Val;
		
		return result;
	}
	
	public static double DensityRatioAverage(HashSet<Node> selfSet, HashSet<Node> counterSet, CommunitySet cS, int selfIdx, int counterIdx) {
		double result = 0.0f;
		
		//R1 = self
		//R2 = counter
		double r1Ratio;
		double r2Ratio;
		
		int r1Sum = 0;
		int r2Sum = 0;
		
		HashMap<Integer, HashSet<Integer>> startToEndEdgesSelf = new HashMap<Integer,HashSet<Integer>>();
		HashMap<Integer, HashSet<Integer>> startToEndEdgesCounter = new HashMap<Integer,HashSet<Integer>>();
		
		
		int r1Cmty, r2Cmty;
				
		if(selfSet==null){
			r1Sum = 1;
		}
		else{
			Iterator<Node> itR1 = selfSet.iterator();
			while(itR1.hasNext()){
				Node nxt = itR1.next();
				r1Cmty = nxt.getMainCommunity();
				
				if(nxt.getCommunityToNeighbors().containsKey(r1Cmty)){
					//r1Sum += nxt.getCommunityToNeighbors().get(r1Cmty).size(); 
					Iterator<Node> it = nxt.getCommunityToNeighbors().get(r1Cmty).iterator();
					while(it.hasNext()){
						Node cur = it.next();
						int st,  end;
						if(nxt.getElementId() < cur.getElementId()){
							st = nxt.getElementId();
							end = cur.getElementId();
						}
						else{
							st = cur.getElementId();
							end = nxt.getElementId();
						}
					//	System.out.println("st/end : "+st+"\t"+end);

						if(startToEndEdgesSelf.containsKey(st)){
							startToEndEdgesSelf.get(st).add(end);
						}
						else{
							HashSet<Integer> k = new HashSet<Integer>();
							k.add(end);
							startToEndEdgesSelf.put(st, k);
						}
					}
				}				
			}
		}

		if(counterSet==null){
			r2Sum = 1;
		}
		else{
			Iterator<Node> itR2 = counterSet.iterator();
			while(itR2.hasNext()){
				Node nxt = itR2.next();
				
				if(nxt.getCommunityToNeighbors().containsKey(counterIdx)){
					//r2Sum += nxt.getCommunityToNeighbors().get(targetCmty).size(); 	
					Iterator<Node> it = nxt.getCommunityToNeighbors().get(counterIdx).iterator();
					while(it.hasNext()){
						Node cur = it.next();
						int st,  end;
						if(nxt.getElementId() < cur.getElementId()){
							st = nxt.getElementId();
							end = cur.getElementId();
						}
						else{
							st = cur.getElementId();
							end = nxt.getElementId();
						}
					//	System.out.println("st/end : "+st+"\t"+end);
						if(startToEndEdgesCounter.containsKey(st)){
							startToEndEdgesCounter.get(st).add(end);
						}
						else{
							HashSet<Integer> k = new HashSet<Integer>();
							k.add(end);
							startToEndEdgesCounter.put(st, k);
						}
					}
				}
			}
		}
		
		r1Sum = 0;
		r2Sum = 0;
		
		for(Entry<Integer, HashSet<Integer>> entry : startToEndEdgesSelf.entrySet()){
			r1Sum += entry.getValue().size();
		}
		
		for(Entry<Integer, HashSet<Integer>> entry : startToEndEdgesCounter.entrySet()){
			r2Sum += entry.getValue().size();
		}
		
		//System.out.println(selfIdx);
		double selfAverageDegree = cS.getAverageDegree(selfIdx);
		double counterAverageDegree = cS.getAverageDegree(counterIdx);
		
		double selfSize = selfAverageDegree * selfSet.size();
		double counterSize = counterAverageDegree * counterSet.size();

		double r1Val = (double)r1Sum / selfSize ;
		double r2Val = (double)r2Sum / counterSize ;
		
		
		System.out.print("r1Sum\t");		System.out.print(r1Sum);
		System.out.print("/ r2Sum\t");		System.out.print(r2Sum);
//		
//
		System.out.print("/ selfSize\t");		System.out.print(selfSize);
		System.out.print("/ counterSize\t");		System.out.print(counterSize);
//		
//
		System.out.print("/ r1Val\t");		System.out.print(r1Val);
		System.out.print("/ r2Val\t");		System.out.println(r2Val);

		
		
		result = r2Val/r1Val;
		
		return result;
	}
}
