package com.largedata;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.kafka.BrokerHosts;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.topology.base.BaseWindowedBolt;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.windowing.TupleWindow;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class Storm_Kafka {

//    public static class Storm_bolt extends BaseRichBolt {
//        private OutputCollector collector;
//        @Override
//        public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
//            this.collector = collector;
//        }
//
//        @Override
//        public void execute(Tuple input) {
//            byte[] binaryvalue = input.getBinaryByField("bytes");
//            String value = new String(binaryvalue);
//            System.out.println(value);
//        }
//
//        @Override
//        public void declareOutputFields(OutputFieldsDeclarer declarer) {
//
//        }
//    }

//    Tumbling Window    //
    public static class TumblingWindow extends BaseWindowedBolt {
        private OutputCollector collector;

        @Override
        public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
            this.collector = collector;
        }

        @Override
        public void execute(TupleWindow inputWindow) {
            int sum = 0;
            List<Tuple> tuplesInWindow = inputWindow.get();
//            LOG.debug("Events in current window: " + tuplesInWindow.size());
            if (tuplesInWindow.size() > 0) {
                /*
                 * Since this is a tumbling window calculation,
                 * we use all the tuples in the window to compute the avg.
                 */
                for (Tuple tuple : tuplesInWindow) {
                    byte[] binaryvalue = tuple.getBinary(0);
                    String value = new String(binaryvalue);
                    System.out.println(value);
                    // value: [location, timestamp, ]
                    //furthe

                }
//                collector.emit(new Values(sum / tuplesInWindow.size()));
            }
        }
    }
//           //

    public static void main(String[] args){

        TopologyBuilder builder = new TopologyBuilder();

        BrokerHosts hosts = new ZkHosts("hadoop000:2181");
        String topic = "project_topic";
        String id = UUID.randomUUID().toString();
        SpoutConfig spoutConfig = new SpoutConfig(hosts, topic, "/" + topic, id);
        KafkaSpout kafkaSpout = new KafkaSpout(spoutConfig);

        String Spout_Id = "spout";
//        String Bolt_Id = "bolt";
        builder.setSpout(Spout_Id, kafkaSpout);
//        Storm_bolt bolt = new Storm_bolt();
//        builder.setBolt(Bolt_Id, bolt).shuffleGrouping(Spout_Id);

        builder.setBolt("bolt",
                new TumblingWindow().withTumblingWindow(BaseWindowedBolt.Duration.seconds(3)),
                1).shuffleGrouping("spout");


        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("MyTopo", new Config(), builder.createTopology());
    }
}
