/**
Copyright (c) 2015 Oregon State University
All Rights Reserved.

AUTHOR
  Mitra Ansariola
  
  Department of Botany and Plant Pathology 
  2082 Cordley Hall
  Oregon State University
  Corvallis, OR 97331-2902
  
  E-mail:  megrawm@science.oregonstate.edu 
  http://bpp.oregonstate.edu/

====================================================================

Permission to use, copy, modify, and distribute this software and its
documentation for educational, research and non-profit purposes, without fee,
and without a written agreement is hereby granted, provided that the above
copyright notice, this paragraph and the following three paragraphs appear in
all copies. 

Permission to incorporate this software into commercial products may be obtained
by contacting Oregon State University Office of Technology Transfer.

This software program and documentation are copyrighted by Oregon State
University. The software program and documentation are supplied "as is", without
any accompanying services from Oregon State University. OSU does not warrant
that the operation of the program will be uninterrupted or error-free. The
end-user understands that the program was developed for research purposes and is
advised not to rely exclusively on the program for any reason. 

IN NO EVENT SHALL OREGON STATE UNIVERSITY BE LIABLE TO ANY PARTY FOR DIRECT,
INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF OREGON
STATE UNIVERSITY HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. OREGON STATE
UNIVERSITY SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
AND ANY STATUTORY WARRANTY OF NON-INFRINGEMENT. THE SOFTWARE PROVIDED HEREUNDER
IS ON AN "AS IS" BASIS, AND OREGON STATE UNIVERSITY HAS NO OBLIGATIONS TO
PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS. 
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import edu.osu.netmotifs.subenum.ByteArray;
import edu.osu.netmotifs.warswap.common.exception.VertexFileFormatException;

public class Utils {
	public static String getAdjMtxOfSubgraph(long l, int motifSize, long values, int subgNo) {
		String outMtx = "";
		byte[] arr = ByteArray.longToByteArray(l, motifSize * motifSize * 2);
//		System.out.println(Arrays.toString(arr));
		
		for (int i = 0; i < motifSize; i++) {
			if (i == 0)
				outMtx = subgNo + ",";
			for (int j = 0; j < motifSize; j++) {
				outMtx = outMtx + arr[(i * motifSize) + j];
			}
			if (i == 0)
				outMtx = outMtx + ", " + values + "\n";
			else
				outMtx += "\n";
		}
		
		return outMtx += "\n";
	}
	
	public static String getAdjMtxOfSubgraph(String matrixStr, int motifSize, long values, int subgNo) {
		String outMtx = "";
//		byte[] arr = ByteArray.longToByteArray(l, motifSize * motifSize * 2);
		byte[] arr = ByteArray.stringToByteArray(matrixStr);
//		System.out.println(Arrays.toString(arr));
		
		for (int i = 0; i < motifSize; i++) {
			if (i == 0)
				outMtx = subgNo + ",";
			for (int j = 0; j < motifSize; j++) {
				outMtx = outMtx + arr[(i * motifSize) + j];
			}
			if (i == 0)
				outMtx = outMtx + ", " + values + "\n";
			else
				outMtx += "\n";
		}
		
    	return outMtx += "\n";
	}
	
	public static String getCanonicalLabeling(long longValueOfSubg, int motifSize) throws IOException {
		char[] matrix = getAdjMtxOfSubgraph(longValueOfSubg, motifSize, 2, 1).toCharArray();
		int[] lab = new int[motifSize];
		int[] permutationList = generatePermutationList(motifSize);
		
		int l = 3;
		int a = 0, b = 0, k = 0;
		char[] max = new char[l * l];
		for (int i = 0; i < l * l; i++) {
			max[i] = '0';
		}
		for (int count = 0; count < permutationList.length / l; count++) {
			boolean compare = true;
			int turn = 0;
			int nn = 0;
			int cc = count * l;
			for (int i = 0; i < l; i++) {
				a = permutationList[cc + i];
				nn = a * l;
				for (int j = 0; j < l; j++) {
					b = permutationList[cc + j];
					k = nn + b ;
					if (i == j)
						continue;
					char c = matrix[k];
					int index = (i * l) + j;
					if (compare) {
						if (max[index] < c) {
							turn = 1;
							compare = false;
							max[index] = c;
						} else if (max[index] > c)
							compare = false;
					} else if (turn == 1) {
						max[index] = c;
					}
				}
			}
			if (turn == 1) {
				for (int i = 0; i < l; i++) {
					lab[i] = permutationList[cc + i];
				}
			}
		}
		char[] tempMtx = new char[motifSize* motifSize];
		char[] temp2Mtx = new char[motifSize * motifSize];
		for (int i = 0; i < lab.length; i++) {
			for (int j = 0; j < lab.length; j++) {
				// exchange rows
				tempMtx[(i * motifSize) + j] = matrix[(lab[i] * motifSize) + j];
			}
		}
		for (int i = 0; i < lab.length; i++) {
			for (int j = 0; j < lab.length; j++) {
				// exchange rows
				temp2Mtx[(j * motifSize) + i] = tempMtx[(j * motifSize) + lab[i]];
			}
		}
		
		return Arrays.toString(temp2Mtx);
	}
	
	public static int[] generatePermutationList(int motifSize) {
		List<Integer> permList = new ArrayList<Integer>();
		String permString = "";
		for (int i = 0; i < motifSize; i++) {
			permString += i;
		}
		permutation(permString, permList);
		int[] permutationList = new int[permList.size()];
		for (int i = 0; i < permList.size() / motifSize; i++) {
			for (int j = 0; j < motifSize; j++) {
				int index = i * motifSize + j;
				permutationList[index] = (permList.get(index));
			}
		}
		return permutationList;

	}
	public static void permutation(String str, List<Integer> permList) {
		permutation("", str, permList);
	}

	private static void permutation(String prefix, String str,
			List<Integer> permList) {
		int n = str.length();
		if (n == 0) {
			for (int i = 0; i < prefix.length(); i++) {
				permList.add(Integer.valueOf(prefix.substring(i, i + 1)));
			}
		} else {
			for (int i = 0; i < n; i++)
				permutation(prefix + str.charAt(i),
						str.substring(0, i) + str.substring(i + 1, n), permList);
		}
	}


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
