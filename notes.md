# Dataset
```
wget https://snap.stanford.edu/data/web-Google.txt.gz
gzip -d web-Google.txt.gz
```

# GraphX

## Scala
```
sudo apt-get install scala
scala -version
```

## Project directory setup
```
mkdir -p scala-project/src/main/scala
cd scala-project/src/main/scala
touch degreeCentrality.scala shortestPaths.scala triangleCount.scala weaklyConnectedComponents.scala
```

Alternatively, you can use `sbt` to create the project structure.

```
sbt new scala/scala-seed.g8
```

## sbt
```
wget https://github.com/sbt/sbt/releases/download/v1.8.2/sbt-1.8.2.tgz
tar -xf sbt-1.8.2.tgz
rm sbt-1.8.2.tgz
vim ~/.bashrc

    export PATH=$PATH:/home/user/sbt/bin

source ~/.bashrc
```

## Compilation and packaging
```
cd scala-project
vim build.sbt

    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core" % "3.3.1",
      "org.apache.spark" %% "spark-graphx" % "3.3.1"
    )

sbt package
```

## Job Submitition
```
spark-submit --class <class-name> <jar-path>
```

## Monitoring and scripts
```
Spark UI: <public-ipv4>:8080
start-all.sh
stop-all.sh
start-workers.sh
stop-workers.sh
```

# Flink

## Download
```
wget --no-check-certificate https://dlcdn.apache.org/flink/flink-1.16.1/flink-1.16.1-bin-scala_2.12.tgz
tar -xvf flink-1.16.1-bin-scala_2.12.tgz
mv flink-1.16.1 flink
vim ~/.bashrc

    export PATH=$PATH:/home/user/flink/bin

source ~/.bashrc
rm flink-1.16.1-bin-scala_2.12.tgz
```

## Configuration
```
vim flink/conf/masters
    
    master

vim flink/conf/workers
    
    master
    slave1
    slave2

vim flink/conf/flink-conf.yaml
    
    jobmanager.rpc.address: <public-ipv4>
    jobmanager.bind-host: 0.0.0.0
    jobmanager.memory.flink.size: 2g
    taskmanager.bind-host: 0.0.0.0
    // select the hostname of your machine
    taskmanager.host: {master, slave1, slave2}
    // select the respective memory size
    taskmanager.memory.flink.size: {3g, 7g, 7g}
    taskmanager.numberOfTaskSlots: 2
    parallelism.default: 6
    rest.address: <public-ipv4>
    rest.bind-address: 0.0.0.0
    akka.framesize: "104857600b"
```

## Rest of setup
```
cd flink
cp opt/flink-gelly-1.16.1.jar lib/

cd ..
scp -r flink slave1:~
scp -r flink slave2:~
```
Make sure you change back taskmanager.host and taskmanager.memory.flink.size on flink-conf.yaml based on the machine you are copying to.

## Compilation and packaging
```
sudo apt install maven -y
mvn archetype:generate \
  -DarchetypeGroupId=org.apache.flink \
  -DarchetypeArtifactId=flink-quickstart-java \
  -DarchetypeVersion=1.16.0

    adis
    {degreeCentrality, shortestPaths, triangleCount, weaklyConnectedComponents}
    1.0
    jar
```

* Change `name` on `pom.xml` to `{Degree Centrality, Shortest Paths, Triangle Count, Weakly Connected Components}`
* Change `mainClass` on `pom.xml` to `jar.{degreeCentrality, shortestPaths, triangleCount, weaklyConnectedComponents}`
* Add `gelly` dependency on `pom.xml`
```
    <dependency>
        <groupId>org.apache.flink</groupId>
        <artifactId>flink-gelly</artifactId>
        <version>1.16.0</version>
    </dependency>
```
* Rename main `.java` file to `{degreeCentrality, shortestPaths, triangleCount, weaklyConnectedComponents}.java`
* Change java main `public class` name to `{degreeCentrality, shortestPaths, triangleCount, weaklyConnectedComponents}`
```
mvn clean package
```

## Job Submitition
```
flink run target/<jar-name>.jar
```

## Monitoring and scripts
```
Flink UI : <public-ipv4>:8081
start-cluster.sh
stop-cluster.sh
```