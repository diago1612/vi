package com.ibs.vi.config;

import com.esotericsoftware.kryo.Kryo;
import com.ibs.vi.model.*;

import java.util.ArrayList;
import java.util.HashMap;

public class KryoRegistrar {

    public static void registerClasses(Kryo kryo) {
        kryo.register(Route.class);
        kryo.register(Airline.class);
        kryo.register(Airport.class);
        kryo.register(Segment.class);
        kryo.register(ArrayList.class);
        kryo.register(HashMap.class);
        kryo.register(Layover.class);
    }
}
