package D2O_Spliter2;

public class Params {
	
	double candidateGammaThreshold;
	double clusterThreshold;
	double candidatePruningParams;
	
	double MixedCommunityCCThreshold = 0.5;
	
	String algoName;
	
	// 1 : original purity
	// 2 : entropy
	int purityMethod;
	boolean communityBreaking;
	
	int callSpliter;
	
	final int minimumSizeOfCommunity = 3;
	
	private static final Params instance = new Params();
	
	
	public double getMixedCommunityCCThreshold(){
		return MixedCommunityCCThreshold;
	}
	
	public int getMinimumCommunitySize(){
		return minimumSizeOfCommunity;
	}
	
	public boolean isBreaking(){
		return communityBreaking;
	}
	
	private Params(){
	}
	
	public int getPurityMethod(){
		return purityMethod;
	}
	
	public void setGammaThreshold(String p){
		//String to double and set
		this.candidateGammaThreshold = Double.parseDouble(p);
	}
	
	public void setPruningParam(String p){
		//String to double and set
		this.candidatePruningParams = Double.parseDouble(p);
	}
	
	public double getGammaThreshold(){
		return candidateGammaThreshold;
	}
	
	public static Params getInstance(){
		return instance;
	}

	public double getCandidatePruningParams() {
		return candidatePruningParams;
	}

	public void setPurityMethod(int purityM) {
		this.purityMethod = purityM;
	}

	public boolean checkCandidate(double purity) {
		if (purityMethod == 1){
			if (purity <= this.getCandidatePruningParams()){
				return true;
			}
			else{
				return false;
			}
		}
		if (purityMethod == 2){
			if (purity >= this.getCandidatePruningParams()){
				return true;
			}
			else{
				return false;
			}
		}
		return false;
	}

	public void setCallBreaking(int callBreaking) {
		if(callBreaking==1){
			communityBreaking = true;
		}
		else{
			communityBreaking = false;
		}
	}

	public void setAlgoName(String algoName) {
		this.algoName = algoName;
	}
	
	public String getAlgoName(){
		return algoName;
	}

	public void setCallSpliter(int callSpliterInt) {
		this.callSpliter = callSpliterInt;
	}

	public int getCallSpliter() {
		return callSpliter;
	}
}
