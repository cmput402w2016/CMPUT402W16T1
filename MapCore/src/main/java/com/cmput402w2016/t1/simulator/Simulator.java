package com.cmput402w2016.t1.simulator;

import com.cmput402w2016.t1.data.controller.DatabaseController;
import com.cmput402w2016.t1.data.controller.RestController;
import com.cmput402w2016.t1.data.TrafficData;
import com.google.gson.Gson;
import org.joda.time.DateTime;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public  class Simulator {


    /**
     * @param hostURI URL for REST Server (include http://)
     * @return integer representing exit value - 0 = success; >0 = failure
     * @throws Exception
     */
    public static int run(String hostURI) throws Exception {
        // TODO: Sanity checks

        Gson gson = new Gson();
        File folder = new File("data/simulator");
        ArrayList<SimulatorDataModel> allData = new ArrayList<>();

        // Read & Parse all the data we have
        File[] files = folder.listFiles();
        if(files == null) {
            System.err.println("'data/simulator' folder not found in current working directory");
            return 1;
        }
        for(File file : files) {
            if(file.isDirectory()) {
                continue;
            }
            FileReader reader = new FileReader(file);
            SimulatorDataModel data = gson.fromJson(reader, SimulatorDataModel.class);
            data.constructNodes();
            allData.add(data);
        }

        // Infinite Loops of Simulation on the data!
        DatabaseController controller = new RestController(hostURI);
        while(true) {
            // Start of hour
            DateTime this_hour = DateTime.now();

            // Sleep until we're at the new hour
            try {
                long sleepTime = DateTime.now().hourOfDay().roundCeilingCopy().getMillis() - this_hour.getMillis();
                System.out.printf("Waiting %ds before next simulation...\n", sleepTime/1000);
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Simulate everything we have data for
            for(SimulatorDataModel data : allData) {
                try {
                    // Get traffic sample
                    TrafficData traffic = data.sampleCarsPerHourModel(this_hour.getMillis());
                    // Post
                    controller.postTraffic(traffic);
                } catch (Exception e) {
                    // Couldn't generate the data for this hour
                    e.printStackTrace();
                    continue;
                }
            }
        }
    }
}
