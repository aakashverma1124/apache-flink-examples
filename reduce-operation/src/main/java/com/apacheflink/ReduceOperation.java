package com.apacheflink;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple5;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class ReduceOperation {
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        final ParameterTool parameters = ParameterTool.fromArgs(args);

        DataStream<String> input = env.readTextFile(parameters.get("filePath"));

        DataStream<Tuple5<String, String, String, Integer, Integer>> mapped = input.map(new Splitter());

        DataStream<Tuple5<String, String, String, Integer, Integer>> reduced = mapped.keyBy(0).reduce(new Reducer());

        DataStream<Tuple2<String, Double>> profitPerMonth = reduced.map(
                (MapFunction<Tuple5<String, String, String, Integer, Integer>, Tuple2<String, Double>>) record ->
                new Tuple2<>(record.f0, (record.f3 * 1.0) / record.f4))
                .returns(Types.TUPLE(Types.STRING, Types.DOUBLE));

        profitPerMonth.writeAsText("/Users/aakashverma/Documents/learning/Java/apache-flink/apache-flink-examples/reduce-operation/output");

        env.execute("Average Profit Per Month Using Reduce Operation");

    }

    public static class Reducer implements ReduceFunction<Tuple5<String, String, String, Integer, Integer>> {

        @Override
        public Tuple5<String, String, String, Integer, Integer> reduce(
                Tuple5<String, String, String, Integer, Integer> currentRecord,
                Tuple5<String, String, String, Integer, Integer> preCalculatedRecord
        ) {
            return new Tuple5<>(
                    currentRecord.f0,
                    currentRecord.f1,
                    currentRecord.f2,
                    currentRecord.f3 + preCalculatedRecord.f3,
                    currentRecord.f4 + preCalculatedRecord.f4
            );
        }
    }
    public static class Splitter implements MapFunction<String, Tuple5<String, String, String, Integer, Integer>> {
        @Override
        public Tuple5<String, String, String, Integer, Integer> map(String str) {
            String[] salesRecord = str.split(",");
            return new Tuple5<>(
                    salesRecord[1],
                    salesRecord[2],
                    salesRecord[3],
                    Integer.parseInt(salesRecord[4]),
                    1
            );
        }
    }
}