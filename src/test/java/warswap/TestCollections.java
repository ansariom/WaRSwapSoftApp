package warswap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.NavigableMap;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import edu.osu.netmotifs.warswap.common.ArrayIndexComparator;
import edu.osu.netmotifs.warswap.common.ListReverseIndexComparator;
import edu.osu.netmotifs.warswap.common.Vertex;

public class TestCollections {
	public static void main(String[] args) {
		// testMin();
		// testNonZeroMin();
		// testListComp();
		// testArrayConvert();
		// testShuffle();
		// testIndexCompare();
		Random random = new Random(Math.abs(new Random().nextInt()));
		for (int i = 0; i < 20000; i++) {
			int s = random.nextInt(5);
			if (s < 0)
				System.out.println("NNNN");
			;
		}
		// testPrintHash();
	}

	private void testNavigableMap() {
		NavigableMap<String, String> original = new TreeMap<String, String>();
		original.put("1", "1");
		original.put("2", "2");
		original.put("3", "3");
		original.put("4", "4");
		original.put("5", "5");

		// this submap1 will contain "3", "3"
		SortedMap<String, String> submap1 = original.subMap("2", "4");

		// this submap2 will contain ("2", "2") ("3", "3") and ("4", "4")
		// because
		// fromInclusive=true, and toInclusive=true
		NavigableMap<String, String> submap2 = original.subMap("2", true, "4", true);
	}

	private static void testPrintHash() {
		// TODO Auto-generated method stub
		HashMap<Vertex, Integer> hash = new HashMap<Vertex, Integer>();
		byte color = 0;
		hash.put(new Vertex(1, color), 0);
		hash.put(new Vertex(2, color), 1);
		hash.put(new Vertex(3, color), 2);
		System.out.println(hash.toString());

	}

	private static void testArrayConvert() {
		List<Integer> unsatTargetIdxs = new ArrayList<Integer>();
		unsatTargetIdxs.add(2);
		unsatTargetIdxs.add(3);
		unsatTargetIdxs.add(8);
		Integer[] a = null;
		unsatTargetIdxs.toArray(a);
		System.out.println(Arrays.toString(a));

	}

	private static void testListComp() {
		List<Integer> l = new ArrayList<Integer>();
		l.add(Integer.valueOf(23));
		l.add(Integer.valueOf(21));
		l.add(Integer.valueOf(12));
		l.add(Integer.valueOf(45));

		ListReverseIndexComparator comparator = new ListReverseIndexComparator(
				l);
		Integer[] srcDegIdxes = comparator.createIndexArray();
		Arrays.sort(srcDegIdxes, comparator);
		System.out.println(Arrays.toString(srcDegIdxes));
	}

	public static void testIndexCompare() {
		Double[] d = new Double[] { 10.6, 11.1, 5.7, 32.0, 6.9 };
		ArrayIndexComparator a = new ArrayIndexComparator(d);
		Integer[] indexes = a.createIndexArray();
		Arrays.sort(indexes, a);
		System.out.println(Arrays.toString(indexes));
	}

	private static void testShuffle() {
		List<Double> l = new ArrayList<Double>();
		l.add(12.9);
		l.add(10.3);
		l.add(3.4);
		l.add(14.0);
		l.add(1.3);
		l.add(1.3);
		Collections.shuffle(l);
		System.out.println(l);
		Collections.shuffle(l);
		System.out.println(l);
		Collections.shuffle(l);
		System.out.println(l);
	}

	private static void testNonZeroMin() {
		List<Double> tgtVDegList = new ArrayList<Double>();
		tgtVDegList.add(0.0);
		tgtVDegList.add(0.0);
		tgtVDegList.add(3.4);
		tgtVDegList.add(14.0);
		tgtVDegList.add(1.3);
		tgtVDegList.add(1.3);

		System.out.println(tgtVDegList.toString());

		double tgtMin = Collections.min(tgtVDegList);
		if (tgtMin == 0) {
			List<Double> tempDegList = new ArrayList<Double>();
			for (Double e : tgtVDegList) {
				tempDegList.add(e);
			}

			Collections.sort(tempDegList);
			while (tempDegList.get(0) == 0) {
				tempDegList.remove(0);
			}
			tgtMin = Collections.min(tempDegList);
		}
		System.out.println(tgtMin);
	}

	private static void testMin() {
		List<Double> l = new ArrayList<Double>();
		l.add(12.9);
		l.add(10.3);
		l.add(3.4);
		l.add(14.0);
		l.add(1.3);
		l.add(1.3);

		System.out.println(Collections.max(l));
		Object[] vv = l.toArray();

	}
}
