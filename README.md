
## Project Goal

The goal of this project is to develop a system that allows for the comparison of the performance of graph processing systems. The comparison is based on the same dataset, workload, and hardware environment, and the results are validated.

### System Functionality

The system executes predefined queries on each system under test, measures the time required to complete each task, computes performance metrics for each system, and produces graphical representations of each metric for each SUT.

The functions that are compared include:

- Finding the shortest path
- Degree centrality
- Triangle count
- Weakly connected components

The two systems compared are Apache Spark GraphX and Apache Flink. Performance metrics include average, minimum and maximum query execution time.
