package D2O_Spliter2;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

public class Main {	
	
	public static boolean printOn = false;
	public static void main(String[] args) throws NumberFormatException, IOException{
		
		if(args.length < 2){
			System.out.println("Instruction : \n"
					+ "[COMPILE] / javac -cp \".:/usr/lib/R/site-library/rJava/jri/JRI.jar\" [FileName]  [ALGORITH] [bfThreshold] [Split[0(no), -1(use full queue), integer(max secondary queue size)] \n"
					+ "[RUN] / java javac -cp \".:/usr/lib/R/site-library/rJava/jri/JRI.jar\" [FileName] [ALGORITHM] [bf] [Split] [Pruning]"
					+ "~ ~/network.dat louvain 0.6 100 0.6"
					);
			return;
		}
		
		// ARGUMENT SETTING
		String FileName = args[0];

		setupArgs(args);
		
		// Graph Load
		Graph totalGraph = new Graph();
		totalGraph.readFile(FileName);
		
		// Run R Engine
		REngine rE = new REngine();
		
		long start1 = System.currentTimeMillis();

		rE.runDistinctAlgorithm(FileName, Params.getInstance().getAlgoName());
		//rE.runClusteringCoefficient(totalGraph);
		long end1 = System.currentTimeMillis();

		
		// set from R result to graph data
		totalGraph.setCommunityInfo(rE.getCommunityResult());
		if(printOn==true) System.out.println(totalGraph);
		
		// build neighbor's community relationships
		totalGraph.buildCommunityRelationship();
		if(printOn==true) System.out.println(totalGraph);

		// building communitySet
		CommunitySet cS = new CommunitySet();
		cS.setupCommunity(totalGraph.getContainer());
    	//cS.setupAverageDegree();

		
		// calculate purity of each nodes
		totalGraph.buildPurity();
		if(printOn==true) System.out.println(totalGraph);

		// Use heap
		Heap hp = null;
		
		long start = System.currentTimeMillis();

		// STEP #1 //
		FindingOverlappedNodes(hp, totalGraph, cS);
		/////////////
				
		long end = System.currentTimeMillis();

		for(int s = 0; s < args.length; s++){
			System.out.print(args[s]+"\t");
		}
		System.out.print("distinct\t"+( end1 - start1 )/1000.0);
		System.out.println("framework\t"+( end - start )/1000.0);
		
        writeFile(args, totalGraph, cS, ( end - start )/1000.0 );
        closeAll(rE);
	}

	private static void FindingOverlappedNodes(Heap hp, Graph totalGraph, CommunitySet cS) {
		if(Params.getInstance().getPurityMethod()==1)		
			hp = new MinHeap(totalGraph);
		else												
			hp = new MaxHeap(totalGraph);
		
		hp.insert(totalGraph, Params.getInstance());
        hp.build();
        
        if(Params.getInstance().getCallSpliter()==0){ 
        	
        	HashSet<Node> entropyCnt = new HashSet<Node>();
        	
	        while(!hp.isEmpty()){
	        	Node node = hp.remove();
	        	entropyCnt.add(node);
	        	checkAndSetOverlapNode(node, Params.getInstance(), cS, hp);	
	        }     
	        
	        System.out.println("visited="+entropyCnt.size());
        }
        
        else if(Params.getInstance().getCallSpliter()==-1){
        	//infinite
        	HashMap<Node, HashSet<Integer>> nodeToNewCommunity = new HashMap<Node, HashSet<Integer>>();
        	
        	do{
	        	while(hp.isEmpty()==false){
	        			Node node = hp.remove();
	        	  		checkAndSetOverlapNode(node, Params.getInstance(), cS, hp, nodeToNewCommunity);
	        	}
	        	setupCommunity(cS, hp, nodeToNewCommunity);
	        	nodeToNewCommunity.clear();
        	} while(!hp.isEmpty());
        	
        		 
        }
        else {   	
        	HashMap<Node, HashSet<Integer>> nodeToNewCommunity = new HashMap<Node, HashSet<Integer>>();
        	int limit = Params.getInstance().getCallSpliter();
        	do{
	        	while(hp.isEmpty()==false){
	        			Node node = hp.remove();
	        	  		checkAndSetOverlapNode(node, Params.getInstance(), cS, hp, nodeToNewCommunity);
	        	  		if(nodeToNewCommunity.size() >= limit){
	        	  			break;
	        	  		}
	        	}
	        	setupCommunity(cS, hp, nodeToNewCommunity);
	        	//cS.setupAverageDegree();
	        	nodeToNewCommunity.clear();
        	} while(!hp.isEmpty());
        }
	}

	private static void setupCommunity(CommunitySet cS, Heap minHeap, HashMap<Node, HashSet<Integer>> nodeToNewCommunity) {

		Params params = Params.getInstance();
		
		for(Entry<Node, HashSet<Integer>> value : nodeToNewCommunity.entrySet() ){
			

			
			Node node = value.getKey();
//			minHeap.print();
			//System.out.print(node.isVisited()+"\tnode"+node.getElementId()+" is updating! to ");
			
			Iterator<Integer> itr = value.getValue().iterator();
			
			while(itr.hasNext()){
				int newCommunityLabel = itr.next();
								
				cS.getCommunityContainer().get(newCommunityLabel).add(node);

				HashMap<Integer, Integer> selfEdge = cS.getCommunitySelfEdges();
				selfEdge.put(newCommunityLabel, selfEdge.get(newCommunityLabel) + (2 * node.getCommunityToNeighbors().get(newCommunityLabel).size()) ); // because it is undirected

				// add info
				node.addCommunityInfo(newCommunityLabel);
				
				// 0305 update
				node.updatePurityItself(newCommunityLabel);


				
				HashSet<Node> nei = node.getNeighborSet();
				//HashSet<Node> nei = node.getCommunityToNeighbors().get(newCommunityLabel);
				Iterator<Node> it = nei.iterator();
				while(it.hasNext()){
					Node t = it.next();
					t.addNeighborCmty(newCommunityLabel, node);

					// node t's purity count update
					boolean isUpdated = t.updatePurityByNeighbor(newCommunityLabel);
					
					if(isUpdated == true && params.checkCandidate(t.getPurity())) {
						if(t.isVisited()==false){ 
							minHeap.insert(t);
							if(Main.printOn==true){
								System.out.println("Inserted!!!!"+t.getElementId()+" purity="+t.getPurity()+" heapSize="+minHeap.getSize());
							}
						}
					}
				}
			}
			node.NoVisited();
//			
//			if(params.checkCandidate(node.getPurity()) && node.isVisited()==false){
//				minHeap.insert(node);
//			}
//			
		}
	}

	private static void writeFile(String[] args, Graph totalGraph, CommunitySet cS, double time) throws IOException {
		
		String FileName = args[0];
		String algoName = args[1];
		

        
        StringBuilder outputFileNameBuilder = new StringBuilder( FileName.substring(0, FileName.length()-4));
        outputFileNameBuilder.append("_spliter_");
        outputFileNameBuilder.append(Params.getInstance().getCallSpliter());
        outputFileNameBuilder.append("_algo_");
        algoName = algoName.replace("_", "");
        outputFileNameBuilder.append(algoName);

        outputFileNameBuilder.append("_final_bf_");
        outputFileNameBuilder.append(Params.getInstance().getGammaThreshold());
        
        outputFileNameBuilder.append("_pruning_");
        outputFileNameBuilder.append(Params.getInstance().getCandidatePruningParams());
        
        outputFileNameBuilder.append(".dat");
        
        if(printOn==true) System.out.println(totalGraph);
        
        totalGraph.writeOverlapCommunity(cS, outputFileNameBuilder.toString());        
	}

	private static void closeAll(REngine rE) {
		rE.closeEngine();
	}

	private static void setupArgs(String[] args) {
		String CombParam =  args[2];
		String PruningParam = args[4];
		String AlgoName = args[1];
		int purityM = 2;
		int callBreaking = 0;
		int callSpliterInt = 0;
		
		callSpliterInt = Integer.parseInt(args[3]);
		
		Params PP = Params.getInstance();
		
		PP.setCallSpliter(callSpliterInt);
		
		PP.setGammaThreshold(CombParam);
		PP.setPruningParam(PruningParam);
		PP.setPurityMethod(purityM);
		PP.setCallBreaking(callBreaking);
		PP.setAlgoName(AlgoName);
		
	}
	
	private static void checkAndSetOverlapNode(Node node, Params params, CommunitySet cS, Heap hp,
			HashMap<Node, HashSet<Integer>> nodeToNewCommunity) {
		

		
		boolean resultBool = false;		
		
		double gammaCombinationThresdhold = gammaCombination(
				node, node.getMainCommunity(), // Edge Weight(n_k, edge_weight(n_k, G_self)
				params.getGammaThreshold());
				
		int numberOfCmty = node.getNumberOfNeighborsCmty();
		
		int internalCnt = node.getPurityCnt();
		
		HashMap<Integer, HashSet<Node>> cmtyToNode = node.getCommunityToNeighbors();
		
		
		for(Entry<Integer, HashSet<Node>> entry : cmtyToNode.entrySet()){
			
			if( node.getMainCommunity() == entry.getKey()){
				continue;
			}
			
			//update 0305
			if ( node.getCommunitySet().contains(entry.getKey())){
				continue;
			}
			
			double comparableValue = 0.0f;
			
			int currentCmty = entry.getKey();

			// the number of link to other community
			int edgeWeight = entry.getValue().size();
			
			double entropyValue;
			
			if(node.getPurityCnt()==0){
				entropyValue = 1;
			}
			else{
				entropyValue = Entropy.normalizedEntropy(edgeWeight, node.getPurityCnt());
			}
			
			//TODO: CHANGED.(snapshot)
			
//			double densityRatio = Density.DensityRatio(
//							node.getCommunityToNeighbors().get(node.getMainCommunity()), 
//							entry.getValue(),
//							cS.getCommunityInternalEdges(node.getMainCommunity()),
//							cS.getCommunityInternalEdges(entry.getKey()),
//							entry.getKey()
//					);
//			
//			double coverageRatio = Converge.ConvergeRatio(
//							node.getCommunityToNeighbors().get(node.getMainCommunity()), 
//							entry.getValue(),
//							entry.getKey(),
//							cS.getCommunityContainer().get(node.getMainCommunity()).size(),
//							cS.getCommunityContainer().get(entry.getKey()).size()
//							);
//			
//			if(densityRatio >= 1){
//				densityRatio = 1.0;
//			}
//			if(coverageRatio >= 1){
//				coverageRatio = 1.0;
//			}
//					
//			
//			comparableValue = entropyValue * densityRatio * coverageRatio;

			double neighborhoodEdgeRatio = NeighborhoodEdgeRatio.NeighborhoodEdgeRatio(node.getCommunityToNeighbors().get(node.getMainCommunity()), entry.getValue(), entry.getKey());
			
			if(neighborhoodEdgeRatio >= 1.0){
				neighborhoodEdgeRatio = 1.0;
			}
				
			comparableValue = neighborhoodEdgeRatio * entropyValue;
			
			// Print status
			if(printOn==true){
				System.out.print("From(nID):"+node.getElementId()+" / To(Cmty):"+entry.getKey());
				//System.out.print("\tentropy:"+entropyValue+"\tdensityRatio:"+densityRatio+"\tconverageRatio:"+coverageRatio);
				System.out.println("edgeWeight="+gammaCombinationThresdhold+" / entropyValue="+entropyValue+" / neighborhoodEdgeRatio="+neighborhoodEdgeRatio);

				System.out.println("\tThreshold:"+gammaCombinationThresdhold+"\tcomparableValue:"+comparableValue);
			}
			
			// assign new community
			if(gammaCombinationThresdhold <= comparableValue){
				
				//entry.getKey() : new community
				//node : currentNode
				
				int newCmtyIdx = entry.getKey();
				
				if(nodeToNewCommunity.containsKey(node)){
					HashSet<Integer> kk = nodeToNewCommunity.get(node);
					kk.add(newCmtyIdx);
					nodeToNewCommunity.put(node, kk);
				}
				else{
					HashSet<Integer> communityAddSeries = new HashSet<Integer>();
					communityAddSeries.add(newCmtyIdx);
					nodeToNewCommunity.put(node, communityAddSeries);
				}
			}
		}
	}

	private static boolean checkAndSetOverlapNode(Node node, Params params, CommunitySet cS, Heap minHeap) {
		
		boolean resultBool = false;		

		double gammaCombinationThresdhold = gammaCombination(
				node, node.getMainCommunity(), // Edge Weight(n_k, edge_weight(n_k, G_self)
				params.getGammaThreshold()
				);
		
		int numberOfCmty = node.getNumberOfNeighborsCmty();
		
		int internalCnt = node.getPurityCnt();
		
		HashMap<Integer, HashSet<Node>> cmtyToNode = node.getCommunityToNeighbors();
				
		for(Entry<Integer, HashSet<Node>> entry : cmtyToNode.entrySet()){
			
			if( node.getMainCommunity() == entry.getKey()){
				continue;
			}
			
			//update 0305
			if ( node.getCommunitySet().contains(entry.getKey())){
				continue;
			}
			
			double comparableValue = 0.0f;
			
			int currentCmty = entry.getKey();

			// the number of link to other community
			int edgeWeight = entry.getValue().size();
			
			
			double entropyValue;
			
			if(node.getPurityCnt()==0){
				entropyValue = 1;
			}
			else{
				entropyValue = Entropy.normalizedEntropy(edgeWeight, node.getPurityCnt());
			}
			
			
//			double densityRatio = Density.DensityRatio(
//							node.getCommunityToNeighbors().get(node.getMainCommunity()), 
//							entry.getValue(),
//							cS.getCommunityInternalEdges(node.getMainCommunity()),
//							cS.getCommunityInternalEdges(entry.getKey()),
//							entry.getKey()
//					);
//			
//			double coverageRatio = Converge.ConvergeRatio(
//							node.getCommunityToNeighbors().get(node.getMainCommunity()), 
//							entry.getValue(),
//							entry.getKey(),
//							cS.getCommunityContainer().get(node.getMainCommunity()).size(),
//							cS.getCommunityContainer().get(entry.getKey()).size()
//							);
//			
//			if(densityRatio >= 1.0){
//				densityRatio = 1.0;
//			}
//			
//			if(coverageRatio >= 1.0){
//				coverageRatio = 1.0;
//			}
			
			double neighborhoodEdgeRatio = NeighborhoodEdgeRatio.NeighborhoodEdgeRatio(node.getCommunityToNeighbors().get(node.getMainCommunity()), entry.getValue(), entry.getKey());
			
			if(neighborhoodEdgeRatio >= 1.0){
				neighborhoodEdgeRatio = 1.0;
			}
				
			//TODO: CHANGED (distinct case)
			//comparableValue = entropyValue * densityRatio * coverageRatio;
			comparableValue = neighborhoodEdgeRatio * entropyValue;
			
			// Print status
			if(printOn==true){
				System.out.println("From(nID)="+node.getElementId()+" / To(Cmty):"+entry.getKey());
//				System.out.println("edgeWeight="+gammaCombinationThresdhold+" / entropyValue="+entropyValue+" / densityRatio="+densityRatio+" / converageRatio="+coverageRatio);
				System.out.println("edgeWeight="+gammaCombinationThresdhold+" / entropyValue="+entropyValue+" / neighborhoodEdgeRatio="+neighborhoodEdgeRatio);
				System.out.println("====> Threshold:"+gammaCombinationThresdhold+" / comparableValue="+comparableValue);
			}
			
			// assign new community
			if(gammaCombinationThresdhold <= comparableValue){
				
				//System.out.println("============> PASSED!");
				
				int newCommunityLabel = entry.getKey();

				
				cS.getCommunityContainer().get(newCommunityLabel).add(node);

				HashMap<Integer, Integer> selfEdge = cS.getCommunitySelfEdges();
				selfEdge.put(newCommunityLabel, selfEdge.get(newCommunityLabel) + (2 * node.getCommunityToNeighbors().get(newCommunityLabel).size()) ); // because it is undirected

				// add info
				node.addCommunityInfo(newCommunityLabel);
				
				// 0305 update
				node.updatePurityItself(newCommunityLabel);

				
				HashSet<Node> nei = node.getNeighborSet();
				//HashSet<Node> nei = node.getCommunityToNeighbors().get(newCommunityLabel);
				Iterator<Node> it = nei.iterator();
				while(it.hasNext()){
					Node t = it.next();
					t.addNeighborCmty(newCommunityLabel, node);

					// node t's purity count update
					boolean isUpdated = t.updatePurityByNeighbor(newCommunityLabel);
					
					if(isUpdated == true && params.checkCandidate(t.getPurity())) {
						if(t.isVisited()==false){ 
							minHeap.insert(t);
							if(Main.printOn==true){
								System.out.println("Inserted!!!!"+t.getElementId()+" purity="+t.getPurity()+" heapSize="+minHeap.getSize());
							}
						}
					}
				}
				resultBool = true;
			}
		}
		node.NoVisited();
		
//		if(params.checkCandidate(node.getPurity()) && node.isVisited()==false){
//			minHeap.insert(node);
//		}
		
		return resultBool;
	}

	private static double gammaCombination(Node node, int communityNumber, double candidateThreshold) {
		// TODO Auto-generated method stub
		int size;
		if(node.getCommunityToNeighbors().containsKey(communityNumber)){
			size = node.getCommunityToNeighbors().get(communityNumber).size();
			//return Gamma.binomialDouble(size, candidateThreshold);
			return Params.getInstance().getGammaThreshold();
		}
		return 1.0f;
	}

	public static void testCases(String fileName){
		testREngine(fileName);
	}
	
	public static void testREngine(String fileName){
		REngine rE = new REngine();
		rE.runDistinctAlgorithm(fileName, "louvain");
		//rE.printResult();
	}
}
