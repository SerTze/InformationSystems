package jar;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import org.apache.flink.graph.*;
import org.apache.flink.graph.library.*;
import org.apache.flink.types.NullValue;
import org.apache.flink.api.java.tuple.*;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.ExecutionEnvironment;

public class triangleEnumerator {

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

		long toc = System.nanoTime();

		DataSet<Tuple3<Integer,Integer,Integer>> result = graph.run(new TriangleEnumerator<Integer, NullValue, NullValue>());

		long triangles = result.count();

		long tic = System.nanoTime();

		long totalMillis = (tic-toc) / 1000000;

		File times = new File("/home/user/workspace-flink/times/triangleEnumerator.txt");
		BufferedWriter bw = new BufferedWriter(new FileWriter(times, true));
		bw.write(totalMillis + " ms\n");
		bw.close();

		File outputs = new File("/home/user/workspace-flink/outputs/triangleEnumerator.txt");
		BufferedWriter bw2 = new BufferedWriter(new FileWriter(outputs));
		bw2.write(triangles + "\n");
		bw2.close();
	}
}
