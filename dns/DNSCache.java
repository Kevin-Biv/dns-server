package dns;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;

/**
 * Class representing a cache of stored DNS records.
 *
 * @version 1.0
 */
public class DNSCache {
    private ArrayList<DNSCacheRecord> cache;

    public DNSCache() {
        cache = new ArrayList<>();
    }

    public void addRecords(String name, String type, String clazz, ArrayList<DNSRecord> records) {
        if ("A".equals(type)) {
            for (DNSRecord record : records) {
                DNSCacheRecord cacheRecord = new DNSCacheRecord(name, type, clazz, record, Instant.now());
                cache.add(cacheRecord);
            }
        }
    }

    public ArrayList<DNSRecord> lookup(String name, String type, String clazz) {
        return getRecords(name, type, clazz);
    }

    private ArrayList<DNSRecord> getRecords(String name, String type, String clazz) {
        ArrayList<DNSRecord> matchingRecords = new ArrayList<>();
        for (DNSCacheRecord cacheRecord : cache) {
            if (cacheRecord.matches(name, type, clazz)) {
                matchingRecords.add(cacheRecord.getRecord());
            }
        }
        return matchingRecords;
    }

    public void cleanup() {
        Instant currentTime = Instant.now();
        Iterator<DNSCacheRecord> iterator = cache.iterator();
        while (iterator.hasNext()) {
            DNSCacheRecord cacheRecord = iterator.next();
            Duration duration = Duration.between(cacheRecord.getTimestamp(), currentTime);
            if (duration.getSeconds() >= cacheRecord.getRecord().getTTL()) {
                iterator.remove();
            }
        }
    }

    private static class DNSCacheRecord {
        private String name;
        private String type;
        private String clazz;
        private DNSRecord record;
        private Instant timestamp;

        public DNSCacheRecord(String name, String type, String clazz, DNSRecord record, Instant timestamp) {
            this.name = name;
            this.type = type;
            this.clazz = clazz;
            this.record = record;
            this.timestamp = timestamp;
        }

        public boolean matches(String name, String type, String clazz) {
            return this.name.equals(name) && this.type == type && this.clazz == clazz;
        }

        public DNSRecord getRecord() {
            return record;
        }

        public Instant getTimestamp() {
            return timestamp;
        }
    }
}
