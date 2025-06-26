package com.ibs.vi.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class RedisRepository {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public <T> void save(String hashKey, String key, T value) throws Exception{
        redisTemplate.opsForHash().put(hashKey, key, value);
    }

    public boolean hasKey(String hashKey, String key){
        return redisTemplate.opsForHash().hasKey(hashKey, key);
    }

    public<T> T get(String hashKey, String key){
        return (T) redisTemplate.opsForHash().get(hashKey, key);
    }

    public <T> List<T> values(Class<T> clazz, String hashKey, String... keys){
        Collection<Object> values = (keys == null || keys.length == 0)
                ? Optional.ofNullable(redisTemplate.opsForHash().values(hashKey)).orElse(Collections.emptyList())
                : Optional.ofNullable((redisTemplate.opsForHash().multiGet(hashKey, Arrays.asList(keys)))).orElse(Collections.emptyList());

        return values.stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .collect(Collectors.toList());
    }

    public <T> void put(String hashKey, String key, T value){
        redisTemplate.opsForHash().put(hashKey, key, value);
    }

    public boolean delete(String hashKey, String... keys){
        if(keys == null || keys.length == 0){
            return redisTemplate.delete(hashKey);
        }else{
            Long deletedCount = redisTemplate.opsForHash().delete(hashKey, keys);
            return (deletedCount != null && deletedCount > 0);
        }
    }

    public boolean deleteByHashKeys(String... hashkeys){
        Long deletedCount = redisTemplate.delete(Arrays.asList(hashkeys));
        return (deletedCount != null && deletedCount > 0);
    }

    public void addToSortedSet(String zsetKey, String value, double score) {
        redisTemplate.opsForZSet().add(zsetKey, value, score);
    }

    public boolean isRoutePresentInVI(String hashKey, String route) {
        return Boolean.TRUE.equals(redisTemplate.opsForHash().hasKey(hashKey, route));
    }

    public List<String> fetchSegmentKeysForDates(LocalDate departureDate) {
        String sortedSetKey = "SortedSegmentKeys";

        long fromEpoch = departureDate.atStartOfDay(ZoneOffset.UTC).toEpochSecond();
        long toEpoch = departureDate.plusDays(2).atTime(LocalTime.MAX).toEpochSecond(ZoneOffset.UTC);

        Set<Object> hashKeys = redisTemplate.opsForZSet()
                .rangeByScore(sortedSetKey, fromEpoch, toEpoch);

        if (hashKeys == null) {
            return Collections.emptyList();
        }

        return hashKeys.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

}
