#!/usr/bin/env bash

JOB_ID=0
for f in onepiece.split.*; 
  do echo "processing $f";
  java Cracker $f 1>>log-${i}.txt 2>>log-${i}.err &
  let "i+=1"
  echo $i
done

