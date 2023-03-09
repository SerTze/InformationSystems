import java.io._
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

        val path = "hdfs://master:9000/user/user/data/web-Google.txt"

        // Load the edges as a graph
        val graph: Graph[Int, Int] = GraphLoader.edgeListFile(
            sc,
            path,
            edgeStorageLevel = StorageLevel.MEMORY_AND_DISK,
            vertexStorageLevel = StorageLevel.MEMORY_AND_DISK
        )

        // Create the reversed graph so that the shortest paths are computed from the source vertex
        val reversedEdges = graph.edges.map(e => Edge(e.dstId, e.srcId, e.attr))
        val reversedGraph = Graph(graph.vertices, reversedEdges)

        // Compute the shortest paths and time the operation
        val sourceVertexId = 0
        val startTime = System.currentTimeMillis()
        val shortestPaths = ShortestPaths.run(reversedGraph, Seq(sourceVertexId)).vertices.map {
            case (id, spMap) => (id, spMap.getOrElse(sourceVertexId, Double.PositiveInfinity))
        }.collect().map {
            case (id, dist) => s"$id $dist"
        }
        val endTime = System.currentTimeMillis()

        // Write the results to seperate files for time and data
        val timeTakenStr = s"${endTime - startTime} ms"

        val times = new File("times/shortestPaths.txt")
        val bw = new BufferedWriter(new FileWriter(times, true))
        bw.write(timeTakenStr + "\n")
        bw.close()
        
        val outputs = new File("outputs/shortestPaths.txt")
        val bw2 = new BufferedWriter(new FileWriter(outputs))
        bw2.write(shortestPaths.mkString("\n"))
        bw2.write("\n")
        bw2.close()

        // Stop the Spark context
        sc.stop()
    }
}
