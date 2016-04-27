#!/bin/sh

if [ -d "./workspace/div/" ]; then
  echo "The /worspace/div/ directory exists. Please remove (or backup before continuing)"
  exit 0
fi
if [ -d "./workspace/no_div/" ]; then
  echo "The /worspace/no_div/ directory exists. Please remove (or backup before continuing)"
  exit 0
fi

./1_from_csv.sh div.csv div 100000
./1_from_csv.sh no_div.csv no_div 100000

iter=100000
./3_batch_from_dir.sh div 0.16667 1_6 $iter
./3_batch_from_dir.sh div 0.33333 2_6 $iter
./3_batch_from_dir.sh div 0.5 3_6 $iter
./3_batch_from_dir.sh div 0.66667 4_6 $iter
./3_batch_from_dir.sh div 0.83333 5_6 $iter
./3_batch_from_dir.sh no_div 0.16667 1_6 $iter
./3_batch_from_dir.sh no_div 0.33333 2_6 $iter
./3_batch_from_dir.sh no_div 0.5 3_6 $iter
./3_batch_from_dir.sh no_div 0.66667 4_6 $iter
./3_batch_from_dir.sh no_div 0.83333 5_6 $iter