#!/bin/bash

# run degreeCentrality 10 times
for i in {1..10}
do
    echo "#$i Execution - degreeCentrality"
    flink run /home/user/workspace-flink/src/degreeCentrality/target/degreeCentrality-1.0.jar
done

# run shortestPaths 10 times
for i in {1..10}
do
    echo "#$i Execution - shortestPaths"
    flink run /home/user/workspace-flink/src/shortestPaths/target/shortestPaths-1.0.jar
done

# run triangleEnumerator 10 times
for i in {1..10}
do
    echo "#$i Execution - triangleEnumerator"
    flink run /home/user/workspace-flink/src/triangleEnumerator/target/triangleEnumerator-1.0.jar
done

# run weaklyConnectedComponents 10 times
for i in {1..10}
do
    echo "#$i Execution - weaklyConnectedComponents"
    flink run /home/user/workspace-flink/src/weaklyConnectedComponents/target/weaklyConnectedComponents-1.0.jar
done
