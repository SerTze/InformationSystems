package jar;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.io.BufferedWriter;
import org.apache.flink.graph.*;
import org.apache.flink.graph.library.*;
import org.apache.flink.types.LongValue;
import org.apache.flink.types.NullValue;
import org.apache.flink.api.java.tuple.*;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.ExecutionEnvironment;

public class degreeCentrality {

    public static void main(String[] args) throws Exception {
		
		// Set up the execution environment
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

		// Compute the degree centrality
		DataSet<Tuple2<Integer, LongValue>> result = graph.getDegrees();

		ArrayList<Tuple2<Integer, LongValue>> degrees = new ArrayList<Tuple2<Integer, LongValue>>();
		result.collect().forEach(degrees::add);

		// Write the output to a file
		File outputs = new File("/home/user/workspace-flink/outputs/degreeCentrality.txt");
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputs));
		for (Tuple2<Integer, LongValue> vertex : degrees) {
			bw.write(vertex.f0 + " " + vertex.f1 + "\n");
		}
		bw.close();
	}
}
