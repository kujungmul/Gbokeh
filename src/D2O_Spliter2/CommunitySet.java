package D2O_Spliter2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

public class CommunitySet {
	private HashMap<Integer, HashSet<Node>> communityContainer;
	private HashMap<Integer, Integer> communitySelfEdges;
	private HashMap<Integer, Double> cmtyToCC;
	private HashMap<Integer, Double> communityToAverageDegree;
	
	
	public CommunitySet(){
		this.communityContainer = new HashMap<Integer, HashSet<Node>>();
		this.communitySelfEdges = new HashMap<Integer, Integer>();
		this.cmtyToCC = new HashMap<Integer, Double>();
		this.communityToAverageDegree = new HashMap<Integer, Double>();
	}

	public HashMap<Integer, Integer> getCommunitySelfEdges() {
		return communitySelfEdges;
	}

	public void setCommunitySelfEdges(HashMap<Integer, Integer> communitySelfEdges) {
		this.communitySelfEdges = communitySelfEdges;
	}

	public HashMap<Integer, HashSet<Node>> getCommunityContainer(){
		return this.communityContainer;
	}
	
	public double getAverageDegree(int cmtyID){
		return this.communityToAverageDegree.get(cmtyID);
	}
	
	public int getCommunityInternalEdges(int cmty){
		// Check this one. 
		return communitySelfEdges.get(cmty);
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		
		for(Entry<Integer, HashSet<Node>> entry : communityContainer.entrySet()){
			sb.append("#cmtyID="+entry.getKey());
			sb.append("\t/ Nodes=");
			Iterator<Node> it = entry.getValue().iterator();
			while(it.hasNext()){
				sb.append(it.next().getElementId()+",");
			}
			sb.append("\n");
		}
		
		sb.append("\n");
		
		sb.append("SELF DENSITY\n");

		
		for(Entry<Integer, Integer> entry : communitySelfEdges.entrySet()){
			sb.append("#cmtyID="+entry.getKey());
			sb.append("\t/ #OFEDGES="+entry.getValue()+"\n");
		}
		
		
		return sb.toString();
	}
	
	public void setupAverageDegree(){
		for(Entry<Integer, HashSet<Node>> t : communityContainer.entrySet()){
			int cmtyID = t.getKey();
			HashSet<Node> cmtyGroup = t.getValue();
			double averageDegree = Utility.calcAverage(cmtyGroup, cmtyID);
			this.communityToAverageDegree.put(cmtyID, averageDegree);
		}
	}
	
	public void setupCommunity(HashMap<Integer, Node> container) {
		
		// SETUP COMMUNITY TO NODES
		for(Entry<Integer, Node> t : container.entrySet()){
			Iterator<Integer> ccm = t.getValue().getCommunitySet().iterator();
			while(ccm.hasNext()){
				int currentCmty = ccm.next();
				if(!communityContainer.containsKey(currentCmty)){
					HashSet<Node> newSet = new HashSet<Node>();
					newSet.add(t.getValue());
					communityContainer.put(currentCmty, newSet);
				}
				else{
					communityContainer.get(currentCmty).add(t.getValue());
				}
			}
		}
		
		// SETUP COMMUNITY TO NUMBER OF EDGES
		for(Entry<Integer, HashSet<Node>> entry : communityContainer.entrySet()){
			
			int sumOfWeight = 0;
			Iterator<Node> it = entry.getValue().iterator();
			while(it.hasNext()){	// the value it means node
				Node t = it.next();
				if(t.getCommunityToNeighbors().containsKey(entry.getKey()))
					sumOfWeight = sumOfWeight + t.getCommunityToNeighbors().get(entry.getKey()).size();
			}
			communitySelfEdges.put(entry.getKey(), sumOfWeight);
		}
		
		
	}

	public double getCommunityCC(int originalCmtyID) {
		return cmtyToCC.get(originalCmtyID);
	}
}
