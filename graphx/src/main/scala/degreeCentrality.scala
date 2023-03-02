import org.apache.spark._
import org.apache.spark.rdd.RDD
import org.apache.spark.graphx._
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.storage.StorageLevel

object degreeCentrality {
    def main(args: Array[String]): Unit = {
        
        // Create a Spark configuration and context
        val conf = new SparkConf()
            .setAppName("degreeCentrality")
            .setMaster("spark://master:7077")
        val sc = new SparkContext(conf)

        val path = "hdfs://master:9000/user/user/data/test.txt"

        // Load the edges as a graph
        val graph: Graph[Int, Int] = GraphLoader.edgeListFile(
            sc,
            path,
            edgeStorageLevel = StorageLevel.MEMORY_AND_DISK,
            vertexStorageLevel = StorageLevel.MEMORY_AND_DISK
        )

        // Compute the degree centrality
        val degreeCentrality: VertexRDD[Int] = graph.degrees.mapValues(d => d.toInt)

        // Print the results
        println("Degree centrality:")
        degreeCentrality.collect().foreach(println)

        // Stop the Spark context
        sc.stop()
    }
}
