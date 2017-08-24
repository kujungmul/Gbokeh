package D2O_Spliter2;


import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;

// Just Elements
public class Node {
	
	public static String pattern = "#####.##";
	public static final DecimalFormat dformat = new DecimalFormat(pattern);

	
	private int elementId;
	private LinkedHashSet<Integer> communities;
	private HashSet<Node> neighborSet;
	private HashMap<Integer, HashSet<Node>> communityToNeighbors;
	private double purityToMainCommunity;
	private int purityToMainCommunityCnt;
	private int mainCommunity;
	private int degree;
	private boolean visited;
	//private double clusteringCoefficient;
	
	public Node(int elementId){
		super();
		this.neighborSet = new HashSet<Node>();
		this.elementId = elementId;
		this.purityToMainCommunity = 0.0f;
		this.communities = new LinkedHashSet<Integer>();
		this.mainCommunity = -1;
		this.purityToMainCommunityCnt = 0;
		this.visited = false;
		this.communityToNeighbors = new HashMap<Integer, HashSet<Node>>();
	}
	

	
	public static Node getInstanceForMaxHeap() {
		Node n = new Node(-1);
		n.setPurity(Integer.MAX_VALUE);
		return n;
	}
	
	public static final Node getInstanceForMinHeap(){
		Node n = new Node(-1);
		n.setPurity(Integer.MIN_VALUE);
		return n;
	}
	
	public void insertNeighborhood(Node _nid){
		neighborSet.add(_nid);
	}
	
	public HashMap<Integer, HashSet<Node>> getCommunityToNeighbors(){
		return this.communityToNeighbors;
	}

	public int getElementId() {
		return elementId;
	}
	
	public void setPurity(double k){
		this.purityToMainCommunity = k;
	}
	
	public double getPurity(){
		return this.purityToMainCommunity;
	}

	public void setElementId(int elementId) {
		this.elementId = elementId;
	}

	public Set<Integer> getCommunitySet() {
		return communities;
	}

	public void addCommunity(int community) {
		this.communities.add(community);
	}

	public HashSet<Node> getNeighborSet() {
		return neighborSet;
	}
	
	public int getMainCommunity(){
		return this.mainCommunity;
	}
	
	public void setCommunityInfo(int real){
		this.mainCommunity = real;
		this.communities.add(real);
	}
	
	public void addCommunityInfo(int adder){
		this.communities.add(adder);
	}
	
	public void setupMainCommunity(){
		if(this.communities.size()==1){
			this.mainCommunity = this.communities.iterator().next();
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(" #id=");
		builder.append(elementId);
		builder.append(", purity=");
		builder.append(""+dformat.format(this.purityToMainCommunity));
		builder.append(", communities=");
		builder.append(communities);
		builder.append(",\t nbr=(");
		
		Iterator<Node> it = neighborSet.iterator();
		int j = 0;
		while(it.hasNext()){
			if(j==0){
				builder.append(it.next().elementId);				
			}
			else{				
				builder.append(","+it.next().elementId);
			}
			j++;
		}
		builder.append(")\t");
		
		builder.append("cmty lists about neighbors = ");
		for(Entry<Integer, HashSet<Node>>entry : communityToNeighbors.entrySet()){
			builder.append("[cmty="+entry.getKey()+"]={");
			Iterator<Node> ite = entry.getValue().iterator();
			while(ite.hasNext()){
				builder.append(ite.next().getElementId()+",");
			}
			builder.append("}");
		}
		
		builder.append("\n");
		return builder.toString();	
	}

	public void setPurityCnt(int count) {
		this.purityToMainCommunityCnt = count;
	}
	
	public void PurityCntPlus(){
		this.purityToMainCommunityCnt++;
		this.purityToMainCommunity = this.purityToMainCommunityCnt / this.degree;
	}
	
	public void setDegree(){
		this.degree = this.neighborSet.size();
	}
	
	public int getDegree(){
		return this.degree;
	}
	
	public int getPurityCnt(){
		return this.purityToMainCommunityCnt;
	}

	// return exact number of community of neighbors of nodes
	public int getNumberOfNeighborsCmty() {
		return this.communityToNeighbors.size();
	}

	public void visited() {
		this.visited = true;
	}
	
	public void NoVisited(){
		this.visited = false;
	}

	public boolean isVisited() {
		return this.visited;
	}

	public void addNeighborCmty(int newCommunityLabel, Node node) {
		
		if(communityToNeighbors.containsKey(newCommunityLabel)){
			this.communityToNeighbors.get(newCommunityLabel).add(node);			
		}
		else{
			HashSet<Node> newCmty = new HashSet<Node>();
			newCmty.add(node);
			this.communityToNeighbors.put(newCommunityLabel, newCmty);
		}		
	}

	public void setupPurity() {
		Node t = this;
		int count = 0;
		double purityEachNode = 0.0;
		int myCommunity = t.getMainCommunity();
		Set<Node> neighbosSet = t.getNeighborSet();

		count = t.getCommunityToNeighbors().get(myCommunity).size();
		purityEachNode = (double)count / (double) neighbosSet.size();
		t.setPurityCnt(count);
		t.setPurity(purityEachNode);
		t.setDegree();		
	}


	public boolean updatePurityByNeighbor(int newCommunityLabel) {
		
		if(Params.getInstance().getPurityMethod()==1){
			if(this.mainCommunity==newCommunityLabel){
				this.purityToMainCommunityCnt++;
				this.purityToMainCommunity = (double) purityToMainCommunityCnt / (double)this.degree;
				return true;
			}
		}
		
		if(Params.getInstance().getPurityMethod()==2){
			double ent = 0;
			// 0305 (updated) 
			if(	!this.communities.contains(newCommunityLabel) && 
					this.getCommunityToNeighbors().containsKey(newCommunityLabel) && this.getCommunityToNeighbors().containsKey(this.getMainCommunity())){
				ent = Entropy.normalizedEntropy(this.getCommunityToNeighbors().get(newCommunityLabel).size(), this.getCommunityToNeighbors().get(this.getMainCommunity()).size());
				if(Main.printOn==true){
					//System.out.println("update entropy count="+this.getCommunityToNeighbors().get(newCommunityLabel).size()+"\t"+ this.getCommunityToNeighbors().get(this.getMainCommunity()).size());
					//System.out.println("Ent = "+ent);
				}
			}
			
			if ( this.purityToMainCommunity < ent){
				this.purityToMainCommunity = ent;
				return true;
			}
		}
		return false;
	}	

	public void setupPurityEntropy() {
		Node t = this;
		double purityEachNode = 0.0;
		int myCommunity = t.getMainCommunity();
		
		HashMap<Integer, HashSet<Node>> cmtySet = t.getCommunityToNeighbors();

		Iterator<Integer> it = cmtySet.keySet().iterator();
		int max = 0;
		while(it.hasNext()){
			
			int current = it.next();
			
			if(current == this.getMainCommunity()){
				continue;
			}
			
			int cnt = cmtySet.get(current).size();
			if( cnt > max){
				max = cnt;
			}
		}
		
		t.setDegree();
		
		if (cmtySet.containsKey(myCommunity)){
			t.setPurityCnt(cmtySet.get(myCommunity).size());
			if(max==0){
				t.setPurity(0.0);
			}
			else{	
				purityEachNode = Entropy.normalizedEntropy(max, t.getPurityCnt());
				t.setPurity(purityEachNode);
			}
		}
		else{
			t.setPurity(0.0);
			t.setPurityCnt(t.getDegree());
		}
	}

	public void updatePurityItself(int newCommunityLabel) {
		
		HashMap<Integer, HashSet<Node>> cmtySet = this.getCommunityToNeighbors();

		Iterator<Integer> it = cmtySet.keySet().iterator();
		int max = 0;
		while(it.hasNext()){
			
			int current = it.next();
			
			if(this.communities.contains(current)){
				continue;
			}
			
			int cnt = cmtySet.get(current).size();
			if( cnt > max){
				max = cnt;
			}
		}
		
		if (cmtySet.containsKey(mainCommunity)){
			this.setPurityCnt(cmtySet.get(mainCommunity).size());
			if(max==0){
				this.setPurity(0);
			}
			else{
				double purityEachNode = Entropy.normalizedEntropy(max, this.getPurityCnt());
				this.setPurity(purityEachNode);
			}
		}
	}
}
