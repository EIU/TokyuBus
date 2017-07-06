package eiu.example.tuann.bus;

import java.util.HashMap;

/**
 * Created by tuann on 6/12/2017.
 */

public class AllBusStopInfomation {
    private HashMap<String, BusStopInfomation> allBus = new HashMap<String, BusStopInfomation>();

    public AllBusStopInfomation(HashMap<String, BusStopInfomation> allBus) {
        this.allBus = allBus;
    }

    public HashMap<String, BusStopInfomation> getAllBus() {
        return allBus;
    }

    public void setAllBus(HashMap<String, BusStopInfomation> allBus) {
        this.allBus = allBus;
    }

}
