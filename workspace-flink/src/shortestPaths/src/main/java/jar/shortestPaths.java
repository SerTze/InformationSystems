package jar;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.io.BufferedWriter;
import org.apache.flink.graph.Edge;
import org.apache.flink.graph.Graph;
import org.apache.flink.graph.Vertex;
import org.apache.flink.types.NullValue;
import org.apache.flink.api.java.tuple.*;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.graph.library.SingleSourceShortestPaths;

public class shortestPaths {

	public static void main(String[] args) throws Exception {
		final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

		String edgeListFilePath = "/home/user/data/web-Google.txt";

		DataSet<Tuple2<Integer, Integer>> edges = env.readTextFile(edgeListFilePath)
			.filter(line -> !line.startsWith("#"))
			.map(line -> {
				String[] tokens = line.split("\\s+");
				return new Tuple2<Integer, Integer>(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]));
			}).returns(Types.TUPLE(Types.INT, Types.INT));

		Graph<Integer, NullValue, NullValue> graph = Graph.fromTuple2DataSet(edges, env);

		int source = 0;
		int iters = 10; // Maximum number of iterations

		// Adding weights to graph edges for the shortest path algorithm
		Graph<Integer, NullValue, Double> weightedGraph = graph.mapEdges(new MapFunction<Edge<Integer, NullValue>, Double>(){
			@Override
			public Double map(Edge<Integer, NullValue> edge) throws Exception {
				return 1.0;
			}
		});

		long toc = System.nanoTime();

		SingleSourceShortestPaths<Integer, NullValue> singleSourceShortestPaths = new SingleSourceShortestPaths<>(source,iters);
		DataSet<Vertex<Integer, Double>> result = singleSourceShortestPaths.run(weightedGraph);

		ArrayList<Vertex<Integer, Double>> paths = new ArrayList<Vertex<Integer, Double>>();
		result.collect().forEach(paths::add);

		long tic = System.nanoTime();

		long totalMillis = (tic-toc) / 1000000;

		File times = new File("/home/user/workspace-flink/times/shortestPaths.txt");
		BufferedWriter bw = new BufferedWriter(new FileWriter(times, true));
		bw.write(totalMillis + " ms\n");
		bw.close();

		File outputs = new File("/home/user/workspace-flink/outputs/shortestPaths.txt");
		BufferedWriter bw2 = new BufferedWriter(new FileWriter(outputs));
		for (Vertex<Integer, Double> vertex : paths) {
			bw2.write(vertex.getId() + " " + vertex.getValue() + "\n");
		}
		bw2.close();
	}
}
