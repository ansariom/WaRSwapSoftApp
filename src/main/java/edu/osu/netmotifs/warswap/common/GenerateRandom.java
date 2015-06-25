package edu.osu.netmotifs.warswap.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import peersim.util.WeightedRandPerm;

/** Copyright (C) 2015 
 * @author Mitra Ansariola 
 * 
 * This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
    
    Contact info:  megrawm@science.oregonstate.edu

 */

/**
 * Generate random dist. based on given probabilities 
 * {@code}  GenerateRandom gr = new GenerateRandom(targets, probs); 
 * {@code}  gr.nextRandList(largeInt);
 * 
 * @note This class is using alias method to generate discrete random distributions 
 * the probs need to be between 0 and 1 to work properly in weighted sampling
 * 
 * @author mitra
 *
 */
public class GenerateRandom {

	private int[] targetList;
	private double[] probList;

	public GenerateRandom() {
	}

	public GenerateRandom(int[] targetList, double[] probList) {
		this.targetList = targetList;
		this.probList = probList;
	}

	/**
	 * All probabilities = 1
	 * 
	 * @param targetList
	 * @param probList
	 */
	public GenerateRandom(int[] targetList) {
		this.targetList = targetList;
		probList = new double[targetList.length];
		for (int i = 0; i < targetList.length; i++) {
			probList[i] = 1.0;
		}
	}

	public int[] nextRandList(int inseed) {
		int[] orderedTargets = new int[targetList.length];
		long seed = Math.abs(new Random().nextInt(inseed));
		List<Integer> rList = sampleAlias(targetList, probList, seed);
//		List<Integer> rList = sample(targetList, probList, seed);

		int i = 0;
		for (Integer e : rList) {
			orderedTargets[i++] = e.intValue();
			// System.out.print(e + ",");
		}

		// System.out.println();
		return orderedTargets;
	}

	private List<Integer> sampleAlias(int[] targetList, double[] probList,
			long seed) {
		List<Integer> finalList = new ArrayList<Integer>();
		
		double sum = 0.0;
		for (int i = 0; i < probList.length; i++) {
			sum += probList[i];
		}
		
		for (int i = 0; i < probList.length; i++) {
			probList[i] /= sum;
//			pList.add(probList[i]/sum);
		}
		WeightedRandPerm perm = new WeightedRandPerm(new Random(seed), probList);
		perm.reset(probList.length);
		while (perm.hasNext()) {
			finalList.add(targetList[perm.next()]);
		}
		return finalList;
	}
	
	private List<Integer> sampleAlias_correct(int[] targetList, double[] probList,
			long seed) {
		List<Double> pList = new ArrayList<Double>();
		List<Double> probOrigList = new ArrayList<Double>();
		List<Integer> targetNewList = new ArrayList<Integer>();
		List<Integer> finalList = new ArrayList<Integer>();
		
		double sum = 0.0;
		for (int i = 0; i < probList.length; i++) {
			sum += probList[i];
			probOrigList.add(probList[i]);
			targetNewList.add(targetList[i]);
		}
		
		for (int i = 0; i < probList.length; i++) {
			pList.add(probList[i]/sum);
		}
		for (int i = 0; i < targetList.length; i++) {
			int index = new AliasMethod(pList, seed).next();
			finalList.add(targetNewList.remove(index));
			sum -= probOrigList.remove(index);
			pList.clear();
			for (int j = 0; j < probOrigList.size(); j++) {
				pList.add(probOrigList.get(j)/sum);
			}
		}
		return finalList;
	}
	
	private List<Integer> sampleAliasOld(int[] targetList, double[] probList,
			long seed) {
		List<Double> pList = new ArrayList<Double>();
		List<Integer> indexList = new ArrayList<Integer>();
		List<Integer> finalList = new ArrayList<Integer>();
		
		double sum = 0.0;
		for (int i = 0; i < probList.length; i++) {
			sum += probList[i];
		}
		
		for (int i = 0; i < probList.length; i++) {
			pList.add(probList[i]/sum);
			indexList.add(targetList[i]);
		}
		AliasMethod aliasMethod = null;
		for (int i = 0; i < targetList.length; i++) {
			aliasMethod = new AliasMethod(pList, seed);
			int index = aliasMethod.next();
			pList.remove(index);
			finalList.add(indexList.remove(index));
		}
		return finalList;
	}

	public static void main(String[] args) {
	}

	/**
	 * Simulate sampling with weight in java
	 * 
	 * @param targets
	 * @param probs
	 * @param seed
	 * @return
	 */
//	public List<Integer> sample(int[] targets, double[] probs, long seed) {
//		// List<Integer> rList = new ArrayList<Integer>();
//		List<Integer> indexMinList = new ArrayList<Integer>();
//		List<Integer> finalList = new ArrayList<Integer>();
//		
//		HashMap<Integer, Integer> tgtIndexMinHash = new HashMap<Integer, Integer>();
//		HashMap<Integer, Integer> indexMinHash = new HashMap<Integer, Integer>();
//		/**
//		 * Add each entry n times in the main list which n is calculated based
//		 * on it's weight using: Utils.getFreq method
//		 */
//		long[] inFreqList = new long[targets.length];
//		for (int i = 0; i < probs.length; i++) {
//			long f = getFreq(probs[i]);
//			inFreqList[i] = f;
//		}
//		
//		long lcm = Utils.lcm(inFreqList);
//		// System.out.println(lcm + " ***");
//		int min = 0;
//		for (int i = 0; i < probs.length; i++) {
//			int count = (int) (lcm * probs[i]);
//			// System.out.println(count);
//			indexMinList.add(min);
//			indexMinHash.put(min, i);
//			tgtIndexMinHash.put(min, i);
//			min = min + count;
//		}
//		
//		Random random = new Random(seed);
//		int tCount = targets.length;
//		int sumcount = min;
//		
//		for (int i = 0; i < tCount; i++) {
//			int randNo = random.nextInt(sumcount);
//			int minRange = findIndexInRange(randNo, indexMinList);
//			int mainTgtIdx = tgtIndexMinHash.get(minRange);
//			int curIdx = indexMinHash.get(minRange);
//			finalList.add(targets[mainTgtIdx]);
//			// correct the ranges for remaining items
//			int curSelectedItem = indexMinList.get(curIdx);
//			tgtIndexMinHash.remove(curSelectedItem);
//			indexMinHash.remove(curSelectedItem);
//			indexMinList.remove(curIdx);
//			int redAmount = sumcount - minRange;
//			if (curIdx < indexMinList.size()) {
//				redAmount = curSelectedItem - minRange;
//			}
//			for (int j = curIdx; j < indexMinList.size(); j++) {
//				int nextItemInList = indexMinList.get(j);
//				int newMinRnage = nextItemInList - redAmount;
//				int tgtIdx = tgtIndexMinHash.remove(nextItemInList);
//				indexMinHash.remove(nextItemInList);
//				indexMinList.set(j, newMinRnage);
//				tgtIndexMinHash.put(newMinRnage, tgtIdx);
//				indexMinHash.put(newMinRnage, j);
//			}
//			sumcount -= redAmount;
//		}
//		
//		return finalList;
//	}
	public List<Integer> sample(int[] targets, double[] probs, long seed) {
		NavigableMap<Integer, Integer> map = new TreeMap<Integer, Integer>();
		List<Integer> finalList = new ArrayList<Integer>();

		/**
		 * Add each entry n times in the main list which n is calculated based
		 * on it's weight using: Utils.getFreq method
		 */
		long[] inFreqList = new long[targets.length];
		for (int i = 0; i < probs.length; i++) {
			long f = getFreq(probs[i]);
			inFreqList[i] = f;
		}

		long lcm = Utils.lcm(inFreqList);
		int min = 0;
		for (int i = 0; i < probs.length; i++) {
			int count = (int) (lcm * probs[i]);
			map.put(min, targets[i]);
			min += count;
		}

		Random random = new Random(seed);
		int tCount = targets.length;
		int sumcount = min;

		long t1 = System.currentTimeMillis();
		int i = 0;
//		System.err.println(tCount);
		while(i++ < tCount) {
			int randNo = random.nextInt(sumcount);
			Entry<Integer, Integer> e = map.floorEntry(randNo);
			int selKey = e.getKey();
			int selIndex = e.getValue();
			finalList.add(selIndex);
			map.remove(selKey);
			Integer nextValue = map.ceilingKey(selKey);
			int redAmount = sumcount - selKey;
			if (nextValue != null) 
				redAmount = nextValue - selKey;
			
			SortedMap<Integer, Integer> editMap = new TreeMap<Integer, Integer>(map.tailMap(selKey));
			Iterator<Entry<Integer, Integer>> eKeySet = editMap.entrySet().iterator();
			while (eKeySet.hasNext()) {
				Integer oldKey = (Integer) eKeySet.next().getKey();
				Integer index = map.remove(oldKey);
				map.put(oldKey - redAmount, index);
			}
			sumcount -= redAmount;
		}
//		System.out.println(System.currentTimeMillis() - t1);

		return finalList;
	}

	private int findIndexInRange(int randNo, List<Integer> indexList) {
		int halfIdx = Math.round((indexList.size() - 1) / 2);
		int minRange = indexList.get(halfIdx);
		if (randNo == minRange)
			return minRange;
		if (halfIdx == indexList.size() - 1)
			return minRange;

		int nextMinRange = indexList.get(halfIdx + 1);
		if (randNo > minRange) {
			if (randNo < nextMinRange)
				return minRange;
			return findIndexInRange(randNo,
					indexList.subList(halfIdx + 1, indexList.size()));
		} else {
			return findIndexInRange(randNo, indexList.subList(0, halfIdx + 1));
		}
	}

	public long getFreq(double d) {
		long c = (long) (d * 100);
		long freq = 0;
		if (((c % 10) == 0)) {
			c = c / 10;
			freq = (long) (getMinMultiplyer((c % 10), 10));
		} else {
			freq = (long) (getMinMultiplyer((c % 100), 100));
		}
		return freq;
	}

	public int getMinMultiplyer(double floatPart, double div) {
		int m = 100;
		double num = 0;
		if (floatPart == 0)
			return 1;
		if ((floatPart % 2) == 0) {
			num = floatPart * 5;
			m = 5;
			if ((num % div) > 0) {
				m = 50;
			}
		} else if ((floatPart % 5) == 0) {
			num = floatPart * 2;
			m = 2;
			if ((num % div) > 0) {
				m = 20;
			}
		} else {
			num = floatPart * 10;
			m = 10;
			if ((num % div) > 0) {
				m = 100;
			}
		}
		return m;
	}
}
