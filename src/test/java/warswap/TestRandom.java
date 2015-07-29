package warswap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import edu.osu.netmotifs.warswap.common.AliasMethod;
import edu.osu.netmotifs.warswap.common.GenerateRandom;
import peersim.util.WeightedRandPerm;

public class TestRandom {
	public static void main(String[] args) {
		// testRandomnonUniform();
		// testAliasMethod();
//		testAppRand();
		testPerrsim();
	}

	private static void testPerrsim() {
		int[] targets = new int[] { 1, 2, 3, 4, 5 };
		double[] probs = new double[] { 0.15, 0.45, 0.2, 0.17, 0.03 };
		WeightedRandPerm perm = new WeightedRandPerm(new Random(), probs);
		for (int i = 0; i < 20; i++) {
			perm.reset(5);
			while (perm.hasNext()) {
				System.out.print(perm.next() + ", ");
			}
			System.out.println();
		}
	}

	private static void testAliasMethod() {
		HashMap<Integer, Integer> cHash = new HashMap<Integer, Integer>();
		int[] targets = new int[] { 1, 2, 3, 4, 5 };
		double[] probs = new double[] { 0.15, 0.45, 0.2, 0.17, 0.03 };
		// double[] probs = new double[] { 0.02, 0.7, 0.08, 0.1, 0.1};
		// double[] probs = new double[] { 1,100,1,1,1};
		List<Double> l = new ArrayList<Double>();
		for (int i = 0; i < probs.length; i++) {
			l.add(probs[i]);
		}
		// AliasMethod aliasMethod = null;
		AliasMethod aliasMethod = new AliasMethod(l, new Random().nextLong());
		for (int i = 0; i < 100; i++) {
			int index = aliasMethod.next();
			// System.out.println(index);
			Integer count = cHash.get(index);
			if (count == null)
				count = 0;
			cHash.put(index, count + 1);
			// System.out.println(l.toString());
			// l.remove(index);
		}

		Iterator<Integer> keySet = cHash.keySet().iterator();
		while (keySet.hasNext()) {
			Integer key = (Integer) keySet.next();
			System.out.println((key + 1) + " = " + cHash.get(key));
		}
	}

	private static void testRandomnonUniform() {
		int[] targets = new int[] { 1, 2, 3, 4, 5 };
		double[] probs = new double[] { 100, 2, 1, 0.5, 0.5 };

		GenerateRandom gr = new GenerateRandom(targets, probs);
		for (int i = 0; i < 100; i++) {
			System.out.println(Arrays.toString(gr.nextRandList(2121)));
		}

	}

	private static void testAppRand() {
		int[] targets = new int[] { 1, 2, 3, 4, 5 };
		// double[] probs = new double[] { 100, 2, 1, 0.5, 0.5 };
		// double[] probs = new double[] { 5, 2, 1, 0.5, 0.5 };
//		double[] probs = new double[] { 2.25, 0.92, 0.92, 0.92, 0.92 };
		double[] probs = new double[] { 0.38, 0.155, 0.155, 0.155, 0.155 };

		GenerateRandom gr = new GenerateRandom(targets, probs);
		for (int i = 0; i < 20; i++) {
			System.out.println(Arrays.toString(gr.nextRandList(111111)));
		}

	}
}
