package com.cmput402w2016.t1.converter;

import com.cmput402w2016.t1.data.Node;

import java.util.HashMap;
import java.util.Map;

/**
 * Used to store the data that will end up being dumped to a json file and run in the simulator
 */
public class SimulatorData {
    /**
     * Holds traffic counts of data, timestamp -> count
     */
    protected Map<String, Integer> hourlyTrafficCountData;
    protected String source;
    protected Map<String, String> from;
    protected transient Node fromNode;
    protected Map<String, String> to;
    protected transient Node toNode;

    /**
     * @param fromNode Source of traffic
     * @param toNode Destination of traffic
     * @param source Where the measured data came from
     */
    public SimulatorData(Node fromNode, Node toNode, String source) {
        from = new HashMap<>();
        this.fromNode = fromNode;
        from.put("lat", fromNode.getLat().toString());
        from.put("lon", fromNode.getLon().toString());

        to = new HashMap<>();
        this.toNode = toNode;
        to.put("lat", toNode.getLat().toString());
        to.put("lon", toNode.getLon().toString());

        this.source = source;
        hourlyTrafficCountData = new HashMap<>();
    }

    /**
     * Used when converting with GSON, it will generate the Nodes based on the lat & lon.
     */
    public void constructNodes() {
        if(from != null && fromNode == null) {
            fromNode = new Node(Double.valueOf(from.get("lat")), Double.valueOf(from.get("lon")));
        }
        if(to != null && toNode == null) {
            toNode = new Node(Double.valueOf(to.get("lat")), Double.valueOf(to.get("lon")));
        }
    }

    /**
     * @param timestamp Timestamp that count was taken at
     * @param count How many cars passed in the past hour
     */
    public void addHourlyCountData(Long timestamp, int count) {
        hourlyTrafficCountData.put(timestamp.toString(), count);
    }
}
