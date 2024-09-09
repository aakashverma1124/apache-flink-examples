package com.apacheflink;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

import java.sql.Timestamp;

public class TumblingWindowEventTime {
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStream<String> input = env.socketTextStream("localhost", 9090);

        DataStream<Tuple2<Long, String>> mapped = input.map((MapFunction<String, Tuple2<Long, String>>) str -> {
            String[] words = str.split(",");
            return new Tuple2<>(Long.parseLong(words[0]), words[1]);
        }).returns(Types.TUPLE(Types.LONG, Types.STRING));

        DataStream<Tuple2<Long, String>> reduced = mapped
        .assignTimestampsAndWatermarks(new AscendingTimestampExtractor<>() {
            @Override
            public long extractAscendingTimestamp(Tuple2<Long, String> timestamp) {
                return timestamp.f0;
            }
        })
        .windowAll(TumblingEventTimeWindows.of(Time.seconds(5)))
        .reduce((ReduceFunction<Tuple2<Long, String>>) (t1, t2) -> {
            int num1 = Integer.parseInt(t1.f1);
            int num2 = Integer.parseInt(t2.f1);
            int sum = num1 + num2;
            Timestamp t = new Timestamp(System.currentTimeMillis());
            return new Tuple2<>(t.getTime(), String.valueOf(sum));
        }).returns(Types.TUPLE(Types.LONG, Types.STRING));

        reduced.writeAsText("/Users/aakashverma/Documents/learning/Java/apache-flink/apache-flink-examples/tumbling-window-event-time/output").setParallelism(1);

        env.execute("Tumbling Window Event Time");
    }
}