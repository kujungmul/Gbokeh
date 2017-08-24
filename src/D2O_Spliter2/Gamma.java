package D2O_Spliter2;

import java.math.BigInteger;


public class Gamma {

	static double logGamma(double x) {
		double tmp = (x - 0.5) * Math.log(x + 4.5) - (x + 4.5);
		double ser = 1.0 + 76.18009173    / (x + 0)   - 86.50532033    / (x + 1)
				+ 24.01409822    / (x + 2)   -  1.231739516   / (x + 3)
				+  0.00120858003 / (x + 4)   -  0.00000536382 / (x + 5);
		return tmp + Math.log(ser * Math.sqrt(2 * Math.PI));
	}

	static double gamma(double x) { return Math.exp(logGamma(x)); }

	static BigInteger binomial(final int N, final int K) {
		BigInteger ret = BigInteger.ONE;
		for (int k = 0; k < K; k++) {
			ret = ret.multiply(BigInteger.valueOf(N-k))
					.divide(BigInteger.valueOf(k+1));
		}
		return ret;
	}


	static double binomialDouble(final int N, final double K) {

		BigInteger older = binomial(N, (int)K);
		BigInteger newer = binomial(N, (int)K+1);

		int gap = newer.subtract(older).intValue();
		double gapK = K - (int)K;

		return older.intValue() + (gap * gapK);
	}
}