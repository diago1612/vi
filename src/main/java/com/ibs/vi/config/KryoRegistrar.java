package com.ibs.vi.config;

import com.esotericsoftware.kryo.Kryo;
import com.ibs.vi.model.Airline;
import com.ibs.vi.model.Route;
import com.ibs.vi.model.Segment;

public class KryoRegistrar {

    public static void registerClasses(Kryo kryo) {
        kryo.register(Route.class);
        kryo.register(Airline.class);
    }
}
