#!/bin/bash

# Check if the number of arguments is correct
if [ -z "$1" ] || [ -z "$2" ]
  then
    echo "Please provide two arguments: the number of times to run the job, and the name of the job."
    exit 1
fi

# Check if number of times is a valid integer
if ! [[ "$1" =~ ^[0-9]+$ ]]; then
  echo "Invalid number of times. Please provide a valid integer."
  exit 1
fi

# Check if job name is valid
case $2 in
    degreeCentrality | shortestPaths | triangleCount | weaklyConnectedComponents)
        ;;
    *)
        echo "Invalid job name. Allowed job names are degreeCentrality, shortestPaths, triangleCount, or weaklyConnectedComponents."
        exit 1
        ;;
esac

# Run the job for the specified number of times
for i in $(seq 1 $1)
do
    echo "#$i Execution - $2"
    (time flink run /home/user/workspace-flink/src/$2/target/$2-1.0.jar) 2>> /home/user/workspace-flink/times/$2.txt
done
