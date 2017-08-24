package D2O_Spliter2;

import java.io.*;
import java.awt.Frame;
import java.awt.FileDialog;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import org.rosuda.JRI.Rengine;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RMainLoopCallbacks;

public class REngine {

	private Rengine re;
	private int[] resultCluster;
	

	public REngine(){
		//System.out.println("Creating Rengine (with arguments)");
		String[] mp = {"--no-save",};
		re=new Rengine(mp, false, new TextConsole());
		//System.out.println("Rengine created, waiting for R");
		if (!re.waitForR()) {
			System.out.println("Cannot load R");
			return;
		}
		//System.out.println("engine load successful!");
	}

	public int[] getCommunityResult(){
		return resultCluster;
	}

	public void runDistinctAlgorithm(String filePath, String algorithmName){
		try{
			String graphPath = filePath;


			re.eval("library(igraph)");

			re.eval("g=read.graph(\""+graphPath+"\")");        	
			re.eval("g <- delete.vertices(graph = g, v = 1)");
			re.eval("g <- as.undirected(g)");
			re.eval("V(g)$name = seq(1:length(V(g)))");

//			if(topK != 0.0f){
//				re.eval("topK <- round(length(V(g)) * "+topK+"+ 1)");
//				re.eval("deg <- degree(g)");
//				re.eval("names(deg) <- V(g)");
//				re.eval("dSorted <- sort.int(deg, decreasing=TRUE, index.return=FALSE)");
//				re.eval("removalNode <- names(dSorted[1:topK])");
//				re.eval("gg <- delete.edges(g, E(g)[from(removalNode)])");
//				re.eval("cmty <- cluster_"+algorithmName+"(gg)");
//			}
//			else{
//				re.eval("cmty <- cluster_"+algorithmName+"(g)");
//			}

			re.eval("cmty <- cluster_"+algorithmName+"(g)");

			REXP communityResultR = re.eval("cmty$membership");
			
			if(communityResultR == null){
				System.out.println("Igraph cannot find community structure. Exit program");
				System.exit(0);
			}
			double[] communityInfo = communityResultR.asDoubleArray();

			resultCluster = new int[communityInfo.length];

			for(int i = 0; i < communityInfo.length; i++){
				resultCluster[i] = (int) communityInfo[i];
			}  		
		} catch (Exception e) {
			System.out.println("EX:"+e);
			e.printStackTrace();
		} finally {
		}	
	}
	
	

	public void closeEngine() {
		re.end();
	}
	

	
	
	
	public HashMap<String, HashSet<Node>> inducedCommunity(String nodeSets, Graph totalGraph, boolean split){
		
		HashMap<String, HashSet<Node>> cmtyToNodeSet = new HashMap<String, HashSet<Node>>();

		boolean spliter = split;
		
		if(nodeSets.equals("")){
			return null;
		}
		
		try{
			if(spliter == true){
				re.eval("memb<-cmty$membership");
				re.eval("eee<-get.edgelist(g)");
				re.eval("merged<-cbind(memb[eee[,1]], memb[eee[,2]])");
				re.eval("zz <- eee[merged[,1]==merged[,2],]");
				re.eval("zz<-graph_from_edgelist(zz, directed=FALSE)");
				re.eval("V(zz)$name = seq(1:length(V(zz)))");
				re.eval("sub=induced_subgraph(zz, c("+nodeSets+"))");
			
			}			
		
			else{
				re.eval("sub=induced_subgraph(g, c("+nodeSets+"))");
			}

			re.eval("cmty2 <- cluster_"+Params.getInstance().getAlgoName()+"(sub)");
			REXP ccResults = re.eval("cbind(V(sub)$name, cmty2$membership)");
			double[][] ccValue = ccResults.asDoubleMatrix();
			
			for( int i = 0; i < ccValue.length; i++){
				
				String cmtyInfo = "sub_"+ccValue[i][1];
				int nodeInfo = (int)ccValue[i][0];
				
				// if it exists
				if(cmtyToNodeSet.containsKey(cmtyInfo)){
					cmtyToNodeSet.get(cmtyInfo).add(totalGraph.getContainer().get(nodeInfo));
				}
				
				else{
					HashSet<Node> k = new HashSet<Node>();
					k.add(totalGraph.getContainer().get(nodeInfo));
					cmtyToNodeSet.put(cmtyInfo, k);
				}
			}

		} catch (Exception e) {
			System.out.println("EX:"+e);
			e.printStackTrace();
		} finally {
			//System.out.println("R execution finished!");
		}	
		return cmtyToNodeSet;
	}

	public void removeCrossingEdges() {
		// TODO Auto-generated method stub
		
	}


}



class TextConsole implements RMainLoopCallbacks
{
	public void rWriteConsole(Rengine re, String text, int oType) {
		//System.out.print(text);
	}

	public void rBusy(Rengine re, int which) {
		System.out.println("rBusy("+which+")");
	}

	public String rReadConsole(Rengine re, String prompt, int addToHistory) {
		System.out.print(prompt);
		try {
			BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
			String s=br.readLine();
			return (s==null||s.length()==0)?s:s+"\n";
		} catch (Exception e) {
			System.out.println("jriReadConsole exception: "+e.getMessage());
		}
		return null;
	}

	public void rShowMessage(Rengine re, String message) {
		//System.out.println("rShowMessage \""+message+"\"");
	}

	public String rChooseFile(Rengine re, int newFile) {
		FileDialog fd = new FileDialog(new Frame(), (newFile==0)?"Select a file":"Select a new file", (newFile==0)?FileDialog.LOAD:FileDialog.SAVE);
		//fd.show();
		String res=null;
		if (fd.getDirectory()!=null) res=fd.getDirectory();
		if (fd.getFile()!=null) res=(res==null)?fd.getFile():(res+fd.getFile());
		return res;
	}

	public void   rFlushConsole (Rengine re) {
	}

	public void   rLoadHistory  (Rengine re, String filename) {
	}			

	public void   rSaveHistory  (Rengine re, String filename) {
	}			
}

