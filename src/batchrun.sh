#!/usr/bin/env bash

JOB_ID=0
for f in onepiece.split.*; 
  do echo "processing $f";
  java Cracker $f &>>log.txt &
done

