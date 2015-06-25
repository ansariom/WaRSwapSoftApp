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

package edu.osu.netmotifs.warswap.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.osu.netmotifs.warswap.common.exception.VertexFileFormatException;

public class Utils {

	public static boolean isNumeric(String string) {
		return string.matches("^[-+]?\\d+(\\.\\d+)?$");
	}

	public static void printStrToFile(String str, String outFile) throws IOException {
		BufferedWriter out;
		try {
			out = new BufferedWriter(new FileWriter(new File(outFile)));
			out.write(str);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}

	}
	
	public static String parseToCientificNotation(double value) {
		DecimalFormat formatter = new DecimalFormat("0.00E00");
		return formatter.format(value).toLowerCase();
	}

	public static List<String> readNIOFile(String filePath) throws Exception {
		List<String> lineList = new ArrayList<String>();
		RandomAccessFile aFile = new RandomAccessFile(filePath, "r");
		FileChannel inChannel = aFile.getChannel();
		MappedByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_ONLY,
				0, inChannel.size());
		buffer.load();
		char c = '0';
		StringBuffer lineStr = new StringBuffer();
		for (int i = 0; i < buffer.limit(); i++) {
			while ((c = (char) buffer.get()) != '\n') {
				lineStr.append(c);
				i++;
			}
			lineList.add(lineStr.toString());
			lineStr.setLength(0);
		}
		buffer.clear(); // do something with the data and clear/compact it.
		inChannel.close();
		aFile.close();
		return lineList;
	}

	public static int[] toIntArray(List<Integer> list) {
		int[] iList = new int[list.size()];
		int i = 0;
		for (Integer integer : list) {
			iList[i++] = integer;
		}
		return iList;
	}

	public static double[] toDoubleArray(List<Double> list) {
		double[] iList = new double[list.size()];
		int i = 0;
		for (Double integer : list) {
			iList[i++] = integer;
		}
		return iList;
	}

	public static List<Long> getNewShuffledList(int[] indexes, List<Long> list) {
		List<Long> oList = new ArrayList<Long>();
		for (int i = 0; i < indexes.length; i++) {
			oList.add(list.get(indexes[i]));
		}
		return oList;
	}

	public static int[] createIndexFromList(List<Integer> list) {
		int[] indexes = new int[list.size()];
		for (int i = 0; i < list.size(); i++) {
			indexes[i] = i;
		}
		return indexes;
	}

	public static int[] createIndexUpdateTgtDeg(List<Integer> degNewList,
			HashMap<Integer, Vertex> tgtVHash, List<Integer> currentTgtDegList) {
		int[] indexes = new int[degNewList.size()];
		for (int i = 0; i < degNewList.size(); i++) {
			indexes[i] = i;
			tgtVHash.get(i).setInDegree(degNewList.get(i));
			currentTgtDegList.add(0);
		}
		return indexes;
	}

	public static double extractFloatPoint(double num) {
		double fpoint = 0;
		long fixPart = (long) num;
		fpoint = num - fixPart;
		long f = (long) (fpoint * 100);
		fpoint = f / 100.0;
		return fpoint;
	}

	/**
	 * Lowest Common Multiplier of a list
	 * 
	 * @param inList
	 * @return
	 */
	public static long lcm(long[] inFreqList) {
		long result = inFreqList[0];
		for (int i = 1; i < inFreqList.length; i++)
			result = lcm(result, inFreqList[i]);
		return result;
	}

	private static long lcm(long a, long b) {
		return a * (b / gcd(a, b));
	}

	private static long gcd(long a, long b) {
		while (b > 0) {
			long temp = b;
			b = a % b; // % is remainder
			a = temp;
		}
		return a;
	}

	public static double outDegreeSum(List<Vertex> v) {
		double sum = 0;
		for (Vertex vertex : v) {
			sum += vertex.getOutDegree();
		}
		return sum;
	}

	public static void convertVertexFile(String inputFile, String outputFile) {
		String str = "";
		HashMap<String, String> colorHash = new HashMap<String, String>();
		colorHash.put(String.valueOf(CONF.TF_Color), CONF.TF_STR);
		colorHash.put(String.valueOf(CONF.MIR_Color), CONF.MIR_STR);
		colorHash.put(String.valueOf(CONF.GENE_Color), CONF.GENE_STR);
		try {
			InputStream inputStream = new FileInputStream(new File(inputFile));
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(inputStream));
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				if (line.startsWith("name"))
					continue;
				String[] vParts = line.split("\t");
				str += vParts[0] + "\t" + colorHash.get(vParts[1]) + "\n";

			}
			bufferedReader.close();
			inputStream.close();
			printStrToFile(str, outputFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void convertToNumericalVColor(String inputFile,
			String outputFile) throws Exception {
		String str = "";
		HashMap<String, String> colorHash = new HashMap<String, String>();
		colorHash.put(CONF.TF_STR, String.valueOf(CONF.TF_Color));
		colorHash.put(CONF.MIR_STR, String.valueOf(CONF.MIR_Color));
		colorHash.put(CONF.GENE_STR, String.valueOf(CONF.GENE_Color));
		try {
			InputStream inputStream = new FileInputStream(new File(inputFile));
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(inputStream));
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				if (line.startsWith("name"))
					continue;
				String[] vParts = line.split("\t");
				if (colorHash.get(vParts[1]) == null)
					throw new VertexFileFormatException();
				str += vParts[0] + "\t" + colorHash.get(vParts[1]) + "\n";

			}
			bufferedReader.close();
			inputStream.close();
			printStrToFile(str, outputFile);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public static void main(String[] args) {
//		convertVertexFile(
//				"/home/mitra/test_wr/Sample_in/ath80_ver_num.txt",
//				"/home/mitra/test_wr/Sample_in/ath80_vertices.txt");
		double n1 = 1;
		double n2 = 500;
		
		System.out.println(new BigDecimal(n1/n2));
		System.out.println(parseToCientificNotation(n1/n2));
	}

}
