/**
 * The MIT License (MIT)

Copyright (c) 2014 Saeed Shahrivari

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

@modified by Mitra Ansariola

Original code is created by Saeed Shahrivari 
@cite : 1. Shahrivari S, Jalili S. Fast Parallel All-Subgraph Enumeration Using Multicore Machines. Scientific Programming. 2015 Jun 16;2015:e901321. 
@code available at : https://github.com/shahrivari/subenum
 */

package edu.osu.netmotifs.subenum;

import java.util.Arrays;

public class ByteArray implements Comparable<ByteArray> {
    private byte[] array;

    public ByteArray(byte[] array) {
        this.array = array.clone();
    }

    private ByteArray() {
    }

    public static ByteArray buildFrom(byte[] array) {
        ByteArray result = new ByteArray();
        result.array = array;
        return result;
    }


    public static byte[] ByteToBinaryArray(byte a) {
        byte[] arr = new byte[2];
        for (int i = 0; i < 2; i++)
            if ((a & (1 << i)) != 0)
                arr[i] = 1;
        return arr;
    }

    /**
     * MA: This method used for canonical labeling pre-process
     * Gives the bool array of given long number 
     * For example: 4 will convert to an array with following content: [false,false,true,false]
     * Note: The binary order is reverse (0100 will be stored as 0010) 
     * 
     * Changed by Mitra to give non-binary array which stores color-coded values
     * i.e. a graph with 001120111 adj matrix will be stored as binary like this: 00 00 01 01 10 00 01 01 01 which encodes number: 3605
     * 
     * Input: long number a
     * Output: byte array (Adj Matrix)
     * @param a
     * @param size = 2 * (motifSize * motifSize)
     * @return
     */

    public static byte[] longToByteArray(long a, int size) {
    	byte[] temparr = new byte[size];
    	byte[] arr = new byte[size/2];
    	for (int i = 0; i < size; i++) {
    		if ((a & (1L << i)) != 0)
    			temparr[size - i -1] = 1;
    	}
    	for (int i = 0; i < size; i = i + 2) {
    		arr[i/2] = (byte) (temparr[i + 1] + (temparr[i] * 2)); 
    		
    	}
//        System.out.println(Arrays.toString(arr));
    	return arr;
    }
    
    public static byte[] stringToByteArray(String a) {
        byte[] arr = new byte[a.length()];
        for (int i = 0; i < a.length(); i++) {
        	arr[i] = Byte.valueOf(a.substring(i, i+1));
		}
//        System.out.println(Arrays.toString(arr));
        return arr;
    }

    /**
     * MA: changed for color coding binary/decimal conversions
     * @param arr
     * @return
     */
    public static long byteArrayToLong(byte[] arr) {
    	if (arr.length > 64)
    		throw new IllegalStateException("Array size is larger than 64: " + arr.length);
    	
    	long result = 0;
    	int s = arr.length;
    	for (int k = 0; k < s; k++) {
    		for (int i = 0; i < 2; i++)
    			if ((arr[s-k-1] & (1 << i)) != 0) {
    				result |= (1L << ((k * 2) + i));                	
    			}
    	}
    	
    	return result;
    }
    
    public static String byteArrayToString(byte[] arr) {
        long result = 0;
        String str = "";
        int s = arr.length;
        for (int k = 0; k < s; k++) {
        	str += arr[k];
		}
        
        return str;
    }

    public byte[] getArray() {
        return array;
    }

    public int size() {
        return getArray().length;
    }
    
//    public BigInteger getAsBigInt() {
//        BigInteger result = BigInteger.ZERO;
//        for (int i = 0; i < getArray().length; i++)
//            if (getArray()[i])
//                result = result.setBit(i);
//        return result;
//    }

    @Override
    public String toString() {
        return Arrays.toString(getArray());
    }

    @Override
    public boolean equals(Object aThat) {
        if (this == aThat) return true;
        if (aThat == null) return false;
        if (!(aThat instanceof ByteArray)) return false;
        ByteArray that = (ByteArray) aThat;
        return Arrays.equals(getArray(), that.getArray());
    }

    @Override
    public int hashCode() {
        if (getArray() == null)
            return 0;
        int result = 1;
        for (int i = 0; i < getArray().length; i++)
            result = 31 * result + (getArray()[i] > 0 ? 1231 : 1237);
        return result;
    }

    @Override
    public int compareTo(ByteArray o) {
        if (getArray().length < o.getArray().length)
            return -1;
        if (getArray().length > o.getArray().length)
            return 1;

        for (int i = 0; i < getArray().length; i++) {
            if (getArray()[i] == o.getArray()[i])
                continue;
            if (getArray()[i] > o.getArray()[i])
                return 1;
            else
                return -1;
        }
        return 0;
    }

}
