package D2O_Spliter2;

public class Entropy {


	private static double Log2(double n) {
		return Math.log(n) / Math.log(2);
	}

	public static double maxEntropy(int classNum){
		return -Log2(1.0/classNum);
	}


	public static double entropy(int[] arr){

		// Input : 6 6 2 1 
		int sum = 0;
		double entropy = 0;

		for(int i = 0; i < arr.length; i++){
			sum += arr[i];
		}

		for(int i = 0; i < arr.length; i++){
			double prob = (double)arr[i] / sum;
			entropy -= prob * Log2(prob);
		}

		return entropy;
	}

	private static double normalizedEntropy(int[] arr) {
		return entropy(arr) / maxEntropy(arr.length);
	}
	
	public static double normalizedEntropy(int a, int b){
		
		if(a == 0 && b!=0){
			return 1;
		}
		
		if(a != 0 && b == 0){
			return 1;
		}
		// modified version
		if(a > b){
			// it means if external count is bigger than internal count
			return 1.0;
		}
		
		int[] ar = new int[2];
		ar[0] = a;
		ar[1] = b;
		return normalizedEntropy(ar);
	}

	public static void main(String[] args){
		int[] arr = new int[]{1, 4};
		System.out.println(entropy(arr));
		System.out.println(maxEntropy(3));
		System.out.println(normalizedEntropy(arr));
	}
}
