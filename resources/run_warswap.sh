#!/bin/bash

JPATH=/usr/bin
LIBDIR=/nfs/BPP/Megraw_Lab/mitra/software/warswap_tool/lib
CLASSESDIR=/nfs/BPP/Megraw_Lab/mitra/software/warswap_tool/classes

CPATH=$CLASSESDIR:$LIBDIR/jgrapht-core-0.9.0.jar:$LIBDIR/log4j-1.2.14.jar:$LIBDIR/derby.jar:$LIBDIR/log4j-1.2.14.jar:$LIBDIR/peersim-1.0.4.jar
THISDIR=`pwd`
export CLASSPATH=$CPATH

taskId=$SGE_TASK_ID

$JPATH/java -classpath $CPATH -Xms2000m -Xmx2400m edu.osu.netmotifs.warswap.WarswapTask $taskId

