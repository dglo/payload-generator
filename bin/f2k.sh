#!/bin/sh

currentDir=`pwd`
scriptDir=${0%/*}

libDir=${scriptDir}/../../lib
classPath="${libDir}/payload-generator.jar:${libDir}/icebucket.jar"

#echo $classPath

java -Xmx1024m -cp ${classPath} icecube.daq.sim.F2kDriver $*




