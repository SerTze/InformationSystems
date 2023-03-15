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
import org.apache.flink.graph.library.ConnectedComponents;

public class weaklyConnectedComponents {

	public static void main(String[] args) throws Exception {
		
		// Setup the execution environment
		final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

		String edgeListFilePath = "/home/user/data/web-Google.txt";

		// Load the edges as a DataSet of Tuples
		DataSet<Tuple2<Integer, Integer>> edges = env.readTextFile(edgeListFilePath)
			.filter(line -> !line.startsWith("#"))
			.map(line -> {
				String[] tokens = line.split("\\s+");
				return new Tuple2<Integer, Integer>(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]));
			}).returns(Types.TUPLE(Types.INT, Types.INT));

		// Create the graph
		Graph<Integer, NullValue, NullValue> graph = Graph.fromTuple2DataSet(edges, env);

		// Maximum number of iterations, big enough to converge
		int iters = 100;

		// Annotate each vertex with its ID as a value
		Graph<Integer, Integer, NullValue> annotatedGraph = graph.mapVertices(new MapFunction<Vertex<Integer, NullValue>, Integer>() {
			public Integer map(Vertex<Integer, NullValue> value) {
				return value.getId();
			}
		});
		
		// Compute the weakly connected components
		ConnectedComponents<Integer, Integer, NullValue> connectedComponents = new ConnectedComponents<>(iters);
		DataSet<Vertex<Integer, Integer>> result = connectedComponents.run(annotatedGraph);

		ArrayList<Vertex<Integer, Integer>> components = new ArrayList<Vertex<Integer, Integer>>();
		result.collect().forEach(components::add);

		// Write the output to a file
		File outputs = new File("/home/user/workspace-flink/outputs/weaklyConnectedComponents.txt");
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputs));
		for (Vertex<Integer, Integer> vertex : components) {
			bw.write(vertex.getId() + " " + vertex.getValue() + "\n");
		}
		bw.close();
	}
}
