package com.cmput402w2016.t1.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;

public class Segment {
    private static Map<String, String> getNeighborGeohashesAsStringMap(String start_node_geohash, Table segment_table) {
        Map<String, String> hmap = new HashMap<>();
        try {
            Get get = new Get(Bytes.toBytes(start_node_geohash));
            Result r = segment_table.get(get);
            if (r != null) {
                NavigableMap<byte[], byte[]> nm = r.getFamilyMap(Bytes.toBytes("node"));
                if (nm != null) {
                    for (Map.Entry<byte[], byte[]> entry : nm.entrySet()) {
                        String key = Bytes.toString(entry.getKey());
                        String value = Bytes.toString(entry.getValue());
                        hmap.put(key, value);
                    }
                    return hmap;
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getNeighborGeohashesAsJSON(String start_node_geohash, Table segment_table) {
        Map<String, String> hmap = getNeighborGeohashesAsStringMap(start_node_geohash, segment_table);
        JsonObject jsonObject = new JsonObject();
        if (hmap == null) {
            return null;
        }
        for (Map.Entry<String, String> e : hmap.entrySet()) {
            String key = e.getKey();
            String value = e.getValue();
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(value);
            jsonObject.add(key, jsonElement);
        }
        return jsonObject.toString();
    }
}