/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ibs.vi.config;

import com.ibs.vi.model.Route;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;

/**
 *
 * @author jithin123
 */
public class KryoRedisSerializer<T> implements RedisSerializer<T> {

    private final Class<T> type;
    private final ThreadLocal<Kryo> kryoThreadLocal;

    public KryoRedisSerializer(Class<T> type) {
        this.type = type;
        this.kryoThreadLocal = ThreadLocal.withInitial(() -> {
            Kryo kryo = new Kryo();

            // Common types
            kryo.register(String.class);
            kryo.register(StringBuilder.class);
            kryo.register(Object.class);

            // Application-specific types
            kryo.register(Route.class);
            kryo.register(HashMap.class);


            kryo.setRegistrationRequired(false); // You may set this true if strict registration is preferred
            return kryo;
        });
    }

    @Override
    public byte[] serialize(T t) throws SerializationException {
        if (t == null) return new byte[0];
        Kryo kryo = kryoThreadLocal.get();
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             Output output = new Output(baos)) {
            kryo.writeClassAndObject(output, t);
            output.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new SerializationException("Kryo serialization failed", e);
        }
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) return null;
        Kryo kryo = kryoThreadLocal.get();
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             Input input = new Input(bais)) {
            return (T) kryo.readClassAndObject(input);
        } catch (Exception e) {
            throw new SerializationException("Kryo deserialization failed", e);
        }
    }
}
