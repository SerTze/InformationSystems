import java.io._
import org.apache.spark._
import org.apache.spark.rdd.RDD
import org.apache.spark.graphx._
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.storage.StorageLevel
import org.apache.spark.graphx.lib._

object pageRank{
    def main(args: Array[String]): Unit ={

        // Create a Spark configuration and context
        val conf = new SparkConf()
            .setAppName("pageRank")
            .setMaster("spark://master:7077")
        val sc = new SparkContext(conf)

        val path = "hdfs://master:9000/user/user/data/dota-league.e"

        // Load the edges as a graph
        val graph: Graph[Int, Int] = GraphLoader.edgeListFile(
            sc,
            path,
            edgeStorageLevel = StorageLevel.MEMORY_AND_DISK,
            vertexStorageLevel = StorageLevel.MEMORY_AND_DISK
        )

        // Compute the PageRank and time the operation
        val startTime = System.currentTimeMillis()
        val ranks = PageRank.runWithOptions(graph, numIter = 10, resetProb = 0.15, srcId = None, normalized = true)
        val endTime = System.currentTimeMillis()

        // Print the time taken and append it to times file
        val timeTaken = endTime - startTime
        val timeTakenStr = s"$timeTaken ms"
        val file = new File("times/pageRank.txt")
        val bw = new BufferedWriter(new FileWriter(file, true))
        bw.write(timeTakenStr + "\n")
        bw.close()
        println("Time taken to compute PageRank: " + timeTakenStr + "\n")

        // Print the result
        println("PageRanks:")
        val output = ranks.vertices.collect().map {
            case (id, rank) => s"$id $rank"
        }
        println(output.mkString("\n"))

        sc.stop()
    }
}
