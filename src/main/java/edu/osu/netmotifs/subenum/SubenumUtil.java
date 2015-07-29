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

public class SubenumUtil {

    public static int arrayContains(int[] array, int value) {
        int index = 0;
        for (; index < array.length; index++)
            if (array[index] == value)
                break;
        if (index < array.length)
            return index;
        else
            return -1;
    }


    final public static void rankedBubbleSort(int[] array, long[] ranks) {
        int j;
        boolean flag = true;   // set flag to true to begin first pass
        int temp1;
        long temp;   //holding variable

        while (flag) {
            flag = false;    //set flag to false awaiting a possible swap
            for (j = 0; j < array.length - 1; j++) {
                if (ranks[j] > ranks[j + 1])   // change to > for ascending sort
                {
                    temp1 = array[j];                //swap elements
                    array[j] = array[j + 1];
                    array[j + 1] = temp1;
                    temp = ranks[j];                //swap elements
                    ranks[j] = ranks[j + 1];
                    ranks[j + 1] = temp;
                    flag = true;              //shows a swap occurred
                }
            }
        }
    }

    final public static void rankedInsertionSort(int[] array, long[] ranks) {
        int temp1;
        long temp;   //holding variable

        for (int i = 0; i < array.length; i++) {
            for (int j = i; j > 0; j--) {
                if (ranks[j - 1] > ranks[j]) {
                    temp1 = array[j];                //swap elements
                    array[j] = array[j - 1];
                    array[j - 1] = temp1;
                    temp = ranks[j];                //swap elements
                    ranks[j] = ranks[j - 1];
                    ranks[j - 1] = temp;
                }
            }
        }
    }

}
