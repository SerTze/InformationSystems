import java.io._
import org.apache.spark._
import org.apache.spark.rdd.RDD
import org.apache.spark.graphx._
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.storage.StorageLevel

object weaklyConnectedComponents {
    def main(args: Array[String]): Unit = {
        
        // Create a Spark configuration and context
        val conf = new SparkConf()
            .setAppName("weaklyConnectedComponents")
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

        // Compute the weakly connected components
        val weaklyConnectedComponents = graph.connectedComponents().vertices.collect().map{
            case (id, comp) => s"$id $comp"
        }
        
        // Write the output to a file
        val outputs = new File("/home/user/workspace-graphx/outputs/weaklyConnectedComponents.txt")
        val bw = new BufferedWriter(new FileWriter(outputs))
        bw.write(weaklyConnectedComponents.mkString("\n"))
        bw.write("\n")
        bw.close()

        // Stop the Spark context
        sc.stop()
    }
}
