import java.io._
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

        val path = "file:///home/user/data/web-Google.txt"

        // Load the edges as a graph
        val graph: Graph[Int, Int] = GraphLoader.edgeListFile(
            sc,
            path,
            edgeStorageLevel = StorageLevel.MEMORY_AND_DISK,
            vertexStorageLevel = StorageLevel.MEMORY_AND_DISK
        )

        // Compute the degree centrality
        val degreeCentrality = graph.degrees.mapValues(d => d.toInt).collect().map{
            case (id, deg) => s"$id $deg"
        }

        // Write the output to a file
        val outputs = new File("/home/user/workspace-graphx/outputs/degreeCentrality.txt")
        val bw = new BufferedWriter(new FileWriter(outputs))
        bw.write(degreeCentrality.mkString("\n"))
        bw.write("\n")
        bw.close()

        // Stop the Spark context
        sc.stop()
    }
}
