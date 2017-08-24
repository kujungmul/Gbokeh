package D2O_Spliter2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class Graph {
	private HashMap<Integer, Node> container;
	
	public Graph() {
		super();
		this.container = new HashMap<Integer, Node>();
	}

	@Override
	public String toString() {
		
		String reString = "Graph Information : \n";
		
		String part = "";
		for(Entry<Integer, Node> t : container.entrySet()){
			part = part + t.getValue();
		}
		
		return reString + part;
	}

	public HashMap<Integer, Node> getContainer() {
		return container;
	}

	public void setContainer(HashMap<Integer, Node> container) {
		this.container = container;
	}

	public void readFile(String filename) throws NumberFormatException, IOException {
		BufferedReader reader = null;
		String line;
		String[] token;
		reader = new BufferedReader(new FileReader(filename));
		
		while((line = reader.readLine()) != null){
			token = line.split("\\s+");
			this.insertNode(Integer.parseInt(token[0]), Integer.parseInt(token[1]));
		}
		reader.close();
		
		if(Main.printOn==true){
			System.out.println("***************Data Load Finished!***************");
			System.out.println("Total Number of nodes = "+container.size());
			System.out.println("Total Number of edges = "+this.getTotalDegree());			
		}
		//System.out.println(container);
	}

	public void writeMainCommunity(String fileName) throws IOException {
		BufferedWriter writer = null;
		writer = new BufferedWriter(new FileWriter(fileName));
		
		for(Entry<Integer, Node> t : container.entrySet()){
			writer.write(t.getValue().getElementId() + "\t" + t.getValue().getMainCommunity() + "\n");
		}
		
		writer.close();
	}
	
	public void printOverlapCommunity(CommunitySet cS) throws IOException{
		HashMap<Integer, HashSet<Node>> communityContainer = cS.getCommunityContainer();

		for(Entry<Integer, HashSet<Node>> t : communityContainer.entrySet()){
			System.out.print(t.getKey()+"\t");
			
			Iterator<Node> it = t.getValue().iterator();
			while(it.hasNext()){
				System.out.print(it.next().getElementId()+",");
			}
			System.out.println("");
		}
	}
	
	
	
	public void writeOverlapCommunity(CommunitySet cS, String fileName) throws IOException{
		BufferedWriter writer = null;
		writer = new BufferedWriter(new FileWriter(fileName));
		
		HashMap<Integer, HashSet<Node>> communityContainer = cS.getCommunityContainer();
		
		for(Entry<Integer, HashSet<Node>> t : communityContainer.entrySet()){
			if(t.getValue().size()==1){
				continue;
			}
			Iterator<Node> it = t.getValue().iterator();
			while(it.hasNext()){
				Node tt = it.next();
				if(Main.printOn==true){
					System.out.print(tt.getElementId()+" ");
				}
				
				writer.write(tt.getElementId()+" ");
			}
			if(Main.printOn==true){
				System.out.println("");
			}
			writer.write("\n");
		}
		writer.close();
		
		//System.out.println("Writing to "+fileName+" has finished!");
	}
	
	public int getNumberOfNodes(){
		return container.size();
	}
	
	public void buildPurity(){
		
		// original purity
		if(Params.getInstance().getPurityMethod()==1){
			for(Entry<Integer, Node> t : container.entrySet()){	
				Node current = t.getValue();
				current.setupPurity();
			}
		}
		
		// maximal entropy
		if(Params.getInstance().getPurityMethod()==2){
			for(Entry<Integer, Node> t : container.entrySet()){	
				Node current = t.getValue();
				current.setupPurityEntropy();
			}
		}		
	}
	
	public int getTotalDegree(){
		int sum = 0;
		for(Entry<Integer, Node> t : container.entrySet()){
			sum += t.getValue().getNeighborSet().size();
		}
		return sum;
	}

	public void insertNode(int n1, int n2){
		
		
		if(!container.containsKey(n1)){
			Node e = new Node(n1);
			container.put(n1, e);
		}
		
		if(!container.containsKey(n2)){
			Node e = new Node(n2);
			container.put(n2, e);	
		}
		
		container.get(n1).insertNeighborhood(container.get(n2));
		container.get(n2).insertNeighborhood(container.get(n1));
	}

	public void setCommunityInfo(int[] communityResult) {
		int i = 0;
		for(Entry<Integer, Node> t : container.entrySet()){
			if((i+1)==t.getKey()){
				t.getValue().setCommunityInfo(communityResult[i]);
			}
			i++;
		}
		
		if(Main.printOn==true)
			System.out.println("Loading from R result to Graph is successful");
	}

	public void buildCommunityRelationship() {
		for(Entry<Integer, Node> t : container.entrySet()){
			
			HashSet<Node> neighbosSet = t.getValue().getNeighborSet();
			Iterator<Node> ndSet =  neighbosSet.iterator();	
			HashMap<Integer, HashSet<Node>> neighbors_cmty_map = t.getValue().getCommunityToNeighbors();
			
			while(ndSet.hasNext()){
				Node neighborNode = ndSet.next();
				Set<Integer> neighborsCmtySet = neighborNode.getCommunitySet();
				Iterator<Integer> it = neighborsCmtySet.iterator();
				while(it.hasNext()){
					int itNext = it.next();
					if(neighbors_cmty_map.containsKey(itNext)){
						neighbors_cmty_map.get(itNext).add(neighborNode);
					}
					else{
						HashSet<Node> neighbors_cmty = new HashSet<Node>();
						neighbors_cmty.add(neighborNode);
						neighbors_cmty_map.put(itNext, neighbors_cmty);
					}
				}
			}
		}		
	}

	public String overlapNodeStringForRE() {
		HashSet<Node> overlappedNodes = this.getOverlapNodes();
		
		StringBuilder sb = new StringBuilder("");
		
		Iterator<Node> t = overlappedNodes.iterator();
		while(t.hasNext()){
			Node tt = t.next();
			sb.append(tt.getElementId());
			sb.append(",");
		}
		sb.deleteCharAt(sb.length()-1) ;
		return sb.toString();
	}

	private HashSet<Node> getOverlapNodes() {
		HashSet<Node> temp = new HashSet<Node>();
		
		for(Entry<Integer, Node> entry : this.container.entrySet()){
			Node temporaryNode = entry.getValue();
			if(temporaryNode.getCommunitySet().size() >= 2){
				temp.add(temporaryNode);
			}
		}
		return temp;
	}
}
