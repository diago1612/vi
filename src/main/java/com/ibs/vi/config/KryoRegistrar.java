package com.ibs.vi.config;

import com.esotericsoftware.kryo.Kryo;
import com.ibs.vi.model.Route;

public class KryoRegistrar {

    public static void registerClasses(Kryo kryo) {
        kryo.register(Route.class);
    }
}
