#!/bin/bash
JPATH=/usr/bin
LIBDIR=$LIBDIR
CLASSESDIR=/nfs/BPP/Megraw_Lab/mitra/software/warswap_tool/classes

CPATH=$CLASSESDIR:$LIBDIR/jgrapht-core-0.9.0.jar:$LIBDIR/log4j-1.2.14.jar:$LIBDIR/drmaa.jar:/local/cluster/sge/lib/lx24-amd64/
THISDIR=`pwd`

export CLASSPATH=$CPATH

SCRIPT_NAME=run_warswap.sh
edgeFileIn=input/dronet.edges.txt
vertexFileIn=input/dronet.vertices.txt
outDir=output
networkName=dronet
numOfIterations=10
motif_size=3

if [ ! -d $outDir ]; then
	mkdir $outDir
fi

$JPATH/java -d64 -classpath $CPATH -Djava.library.path=/local/cluster/sge/lib/lx24-amd64/ -Xms2000m -Xmx2400m edu.osu.netmotifs.warswap.JWarswapCluster $THISDIR $SCRIPT_NAME $edgeFileIn $vertexFileIn $outDir $networkName $motif_size $numOfIterations

#Clean up
log_dir=log_temp
if [ ! -d $log_dir ]; then
	mkdir $log_dir
fi
mv $SCRIPT_NAME.* $log_dir
