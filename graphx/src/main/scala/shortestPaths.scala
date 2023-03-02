import org.apache.spark._
import org.apache.spark.rdd.RDD
import org.apache.spark.graphx._
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.storage.StorageLevel
import org.apache.spark.graphx.lib.ShortestPaths

object shortestPaths {
    def main(args: Array[String]): Unit = {
        
        // Create a Spark configuration and context
        val conf = new SparkConf()
            .setAppName("shortestPaths")
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

        // Compute the shortest paths
        val sourceVertexId = graph.vertices.map(_._1).reduce((id1, id2) => if (id1 < id2) id1 else id2)
        val shortestPaths = ShortestPaths.run(graph, Seq(sourceVertexId)).vertices.map {
            case (id, spMap) => (id, spMap.getOrElse(sourceVertexId, Double.PositiveInfinity))
        }

        // Print the results
        println("Shortest paths:")
        shortestPaths.collect().foreach(println)

        // Stop the Spark context
        sc.stop()
    }
}
