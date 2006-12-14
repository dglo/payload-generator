#!/bin/sh

currentDir=`pwd`
scriptDir=${0%/*}

libDir=${scriptDir}/../../lib
classPath="${libDir}/payload-generator.jar:${libDir}/icebucket.jar"

#echo $classPath

java -cp ${classPath} icecube.daq.sim.domhub.DomSimulator $*

