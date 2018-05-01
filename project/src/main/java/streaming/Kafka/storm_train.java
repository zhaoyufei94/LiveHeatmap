package com.largedata.project;

import org.apache.commons.lang.StringUtils;
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
import org.apache.storm.tuple.Values;
import org.apache.storm.windowing.TupleWindow;

import java.util.*;
import java.util.concurrent.TimeUnit;


public class storm_train {
//    Tumbling Window    //
public static class TumblingWindow extends BaseWindowedBolt {
    private OutputCollector collector;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(TupleWindow inputWindow) {
        Map<String, int[]> counts = new HashMap<String, int[]>();
        String timestamp = "";
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
                String words[] = value.split(" ");
                String key = StringUtils.join(new String[] {words[0], words[1] }, " ");
                int[] count = counts.get(key);
                if (count == null)
                    count = new int[] {0,0};
                count[0] += Integer.parseInt(words[2]);
                count[1] += Integer.parseInt(words[3]);
                counts.put(key, count);
                timestamp = words[4];
            }
            Iterator<String> keySetIterator = counts.keySet().iterator();
            while(keySetIterator.hasNext()) {
                String key = keySetIterator.next();
                System.out.println("key: " + key + " value: " + counts.get(key)[0] + " timestamp: " + timestamp);
                collector.emit(new Values(key, counts.get(key)[0], counts.get(key)[1], timestamp));
            }

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
