#!/bin/bash

download_spark () {
	cd ~
	wget https://archive.apache.org/dist/spark/spark-3.3.1/spark-3.3.1-bin-hadoop3.tgz
	tar -xzf spark-3.3.1-bin-hadoop3.tgz
	mv spark-3.3.1-bin-hadoop3 spark
}

configure_spark () {
	echo "export SPARK_HOME=/home/user/spark" >> ~/.bashrc
	echo "export PATH=\$PATH:\$SPARK_HOME/bin" >> ~/.bashrc
	echo "export PATH=\$PATH:\$SPARK_HOME/sbin" >> ~/.bashrc
	echo "export PYSPARK_PYTHON=python3.8" >> ~/.bashrc
	echo "export PYSPARK_DRIVER_PYTHON=python3.8" >> ~/.bashrc
	echo "export PATH=\$PATH:\$SPARK_HOME/sbin" >> ~/.bashrc
	echo "alias start-all.sh='\$SPARK_HOME/sbin/start-all.sh'" >> ~/.bashrc
	echo "alias stop-all.sh='\$SPARK_HOME/sbin/stop-all.sh'" >> ~/.bashrc

	source ~/.bashrc

	cd /home/user/spark/conf

	cp spark-defaults.conf.template spark-defaults.conf
	echo -e "spark.master\t\tspark://master:7077" >> spark-defaults.conf
	echo -e "spark.submit.deployMode\t\tclient" >> spark-defaults.conf
	echo -e "spark.executor.cores\t\t2" >> spark-defaults.conf
	echo -e "spark.executor.memory\t\t3g" >> spark-defaults.conf
	echo -e "spark.driver.memory\t\t512m" >> spark-defaults.conf
	
	echo "master" > workers
	echo "slave1" >> workers
	echo "slave2" >> workers
}

echo "STARTING DOWNLOAD ON MASTER"
download_spark

echo "STARTING DOWNLOAD ON SLAVE1"
ssh user@slave1 "$(typeset -f download_spark); download_spark"

echo "STARTING DOWNLOAD ON SLAVE2"
ssh user@slave2 "$(typeset -f download_spark); download_spark"

echo "STARTING SPARK CONFIGURE ON MASTER"
configure_spark

echo "STARTING SPARK CONFIGURE ON SLAVE1"
ssh user@slave1 "$(typeset -f configure_spark); configure_spark"

echo "STARTING SPARK CONFIGURE ON SLAVE2"
ssh user@slave2 "$(typeset -f configure_spark); configure_spark"
