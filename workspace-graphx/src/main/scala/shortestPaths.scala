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

        val path = "file:///home/user/data/web-Google.txt"

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
        val sourceVertexId = 0

        // Compute the shortest paths        
        val shortestPaths = ShortestPaths.run(reversedGraph, Seq(sourceVertexId)).vertices.map {
            case (id, spMap) => (id, spMap.getOrElse(sourceVertexId, Double.PositiveInfinity))
        }.collect().map {
            case (id, dist) => s"$id $dist"
        }
        
        // Write the output to a file
        val outputs = new File("/home/user/workspace-graphx/outputs/shortestPaths.txt")
        val bw = new BufferedWriter(new FileWriter(outputs))
        bw.write(shortestPaths.mkString("\n"))
        bw.write("\n")
        bw.close()

        // Stop the Spark context
        sc.stop()
    }
}
