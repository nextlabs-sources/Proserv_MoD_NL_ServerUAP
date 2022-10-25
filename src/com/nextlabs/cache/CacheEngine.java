package com.nextlabs.cache;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ehcache.Cache;
import org.ehcache.Cache.Entry;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;

import com.nextlabs.common.UserObject;
import com.nextlabs.common.SAPObject;

public class CacheEngine {

	public static final String USER_CACHE_NAME = "UserAttributeProviderCache";
	public static final String AOR_CACHE_NAME = "AORAttributeProviderCache";
	private static final Log LOG = LogFactory.getLog(CacheEngine.class);
	private static CacheEngine engine;
	private CacheManager userCacheManager;
	private Cache<String, UserObject> userObjectCache;
	
	private CacheManager aorCacheManager;
	private Cache<String, SAPObject> aorObjectCache;
	private Map<String, String> identifierMap;

	public CacheEngine() {
	}

	public static CacheEngine getInstance() {
		if (engine == null) {
			engine = new CacheEngine();
		}
		return engine;
	}

	public void writeObjectToUserCache(UserObject obj) {
		if (userObjectCache == null) {
			LOG.error("Cache has not been initialized");
			return;
		}
		userObjectCache.put(obj.getId(), obj);
	}
	
	public void writeObjectToAORCache(SAPObject obj) {
		if (aorObjectCache == null) {
			LOG.error("AOR Cache has not been initialized");
			return;
		}
		aorObjectCache.put(obj.getId(), obj);
	}

	public void removeObjectFromUserCache(String objId) {
		String id = identifierMap.get(objId);
		if (id != null) {
			LOG.debug(String.format("Removing object [%s] from user cache", objId));
			userObjectCache.remove(id);
			LOG.debug(String.format("Object [%s] removed from user cache", objId));
		} else {
			LOG.warn(String.format("Object [%s] is not in cache. Purge skipped", objId));
		}
	}
	
	public void removeObjectFromAORCache(String objId) {
		if (objId != null) {
			LOG.debug(String.format("Removing object [%s] from aor cache", objId));
			aorObjectCache.remove(objId);
			LOG.debug(String.format("Object [%s] removed from aor cache", objId));
		} else {
			LOG.warn(String.format("Object [%s] is not in aor cache. Purge skipped", objId));
		}
	}
	
	public void purgeAORCache() {
		
		if (aorObjectCache == null) {
			LOG.error("AOR Cache has not been initialized");
			return;
		}
		LOG.info("Purging all cache data in AOR");
		aorObjectCache.clear();
		LOG.info("All data in AOR purged!");
		printCache();
	}


	public void initializeCache(Properties props) {
		ResourcePoolsBuilder resourceBuilder = ResourcePoolsBuilder.newResourcePoolsBuilder();
		
		String cacheHeap = props.getProperty("cache_heap_in_mb", "128");
		LOG.info(String.format("cache_heap_in_mb will be set to [%s] MB", cacheHeap)); 
		int iHeapMem = 128;
		
		try{
			iHeapMem = Integer.parseInt(cacheHeap);
		}catch(Exception ex){
			LOG.error("Not able to read cache_heap_in_mb, hard set to 128MB");
		}
		resourceBuilder = resourceBuilder.heap(iHeapMem, MemoryUnit.MB);
		LOG.info(String.format("cache_heap_in_mb will be set to [%s] MB", iHeapMem));
		
		
		String cache_max_object = props.getProperty("cache_max_object","5000");
		long lCache_max_object = 5000;
		try{
			lCache_max_object = Long.parseLong(cache_max_object);
		}catch(Exception ex){
			LOG.error("Not able to read cache_max_object, hard set to 5000");
		}
		
		LOG.info(String.format("cache_max_object will be set to [%s]", lCache_max_object));	

		String timeToLive = props.getProperty("user_time_to_live", "1_DAYS");
		
		Duration duration = getTimeToLive(timeToLive);
		
		//User Cache region
		CacheConfigurationBuilder<String, UserObject> userCacheConfigurationBuilder = CacheConfigurationBuilder
				.newCacheConfigurationBuilder(String.class, UserObject.class, resourceBuilder)
				.withExpiry(Expirations.timeToLiveExpiration(duration)).withSizeOfMaxObjectGraph(lCache_max_object);

		CacheManagerBuilder<CacheManager> cacheManagerBuilder = CacheManagerBuilder.newCacheManagerBuilder();
		cacheManagerBuilder = cacheManagerBuilder.withCache(USER_CACHE_NAME, userCacheConfigurationBuilder);
		userCacheManager = cacheManagerBuilder.build(true);

		userObjectCache = userCacheManager.getCache(USER_CACHE_NAME, String.class, UserObject.class);
		
		//AOR Cache region
		String aorMode = props.getProperty("aor_expired_mode", "purge");
		Duration aorDuration = null;
		
		if (aorMode.equalsIgnoreCase("purge")) {
			aorDuration = getTimeToLive("INFINITE");
		}
		else {
			String aorTimeToLive = props.getProperty("aor_time_to_live", "1_DAYS");
			aorDuration = getTimeToLive(aorTimeToLive);
		}
		
		CacheConfigurationBuilder<String, SAPObject> aorCacheConfigurationBuilder = CacheConfigurationBuilder
				.newCacheConfigurationBuilder(String.class, SAPObject.class, resourceBuilder)
				.withExpiry(Expirations.timeToLiveExpiration(aorDuration)).withSizeOfMaxObjectGraph(lCache_max_object);

		CacheManagerBuilder<CacheManager> aorCacheManagerBuilder = CacheManagerBuilder.newCacheManagerBuilder();
		aorCacheManagerBuilder = aorCacheManagerBuilder.withCache(AOR_CACHE_NAME, aorCacheConfigurationBuilder);
		aorCacheManager = aorCacheManagerBuilder.build(true);

		aorObjectCache = aorCacheManager.getCache(AOR_CACHE_NAME, String.class, SAPObject.class);

		// identifierMap can be modified concurrently by different requests
		identifierMap = new ConcurrentHashMap<String, String>();
	}
	
	private Duration getTimeToLive(String timeToLive) {
		
		Duration duration = null;

		if (timeToLive.equals("INFINITE")) {
			duration = Duration.INFINITE;
			LOG.info("Setting time to live to INFINITE");
		} else {

			TimeUnit unit = TimeUnit.DAYS;
			int iTimeToLive = 1;

			String[] temp = timeToLive.split("_");

			try {
				iTimeToLive = Integer.parseInt(temp[0]);
			} catch (IllegalArgumentException e) {
				LOG.error("Invalid time_to_live value(s), resetting to 1_DAYS");
				iTimeToLive = 1;
			}

			try {
				switch (temp[1]) {
				case "SECS":
					unit = TimeUnit.SECONDS;
					break;
				case "MINS":
					unit = TimeUnit.MINUTES;
					break;
				case "HRS":
					unit = TimeUnit.HOURS;
					break;
				case "DAYS":
					unit = TimeUnit.DAYS;
					break;
				default:
				}
			} catch (Exception ex) {
				LOG.error("Invalid time_to_live unit, resetting to DAYS");
				unit = TimeUnit.DAYS;
			}

			duration = Duration.of(iTimeToLive, unit);

			LOG.info(String.format("Setting expiration to %d %s", iTimeToLive, unit.toString()));
			
		}
		return duration;
	}

	public UserObject getUserObjectFromCache(String id) {
		if (userObjectCache == null) {
			LOG.error("User Cache has not been initialized");
			return null;
		}

		return (identifierMap.get(id) == null) ? null : userObjectCache.get(identifierMap.get(id));
	}
	
	public SAPObject getAorObjectFromCache(String id) {
		if (aorObjectCache == null) {
			LOG.error("AOR Cache has not been initialized");
			return null;
		}
		return (aorObjectCache.get(id));
	}

	public void printCache() {
		
		//User Cache
		if (userObjectCache == null) {
			LOG.error("User Cache has not been initialized");
			return;
		}

		int size = 0;

		Iterator<Entry<String, UserObject>> it = userObjectCache.iterator();
		while (it.hasNext()) {
			Entry<String, UserObject> entry = (Entry<String, UserObject>) it.next();
			size++;
			LOG.info(String.format("User Cache now contains [%s]", entry.getKey()));
		}

		LOG.info(String.format("User Cache now has [%d] entries", size));
		
		//AOR Cache
		if (aorObjectCache == null) {
			LOG.error("AOR Cache has not been initialized");
			return;
		}

		int asize = 0;

		Iterator<Entry<String, SAPObject>> itA = aorObjectCache.iterator();
		while (itA.hasNext()) {
			Entry<String, SAPObject> entry = (Entry<String, SAPObject>) itA.next();
			asize++;
			LOG.info(String.format("AOR Cache now contains [%s]", entry.getKey()));
		}

		LOG.info(String.format("AOR Cache now has [%d] entries", asize));
	}

	public void printIdentifierMap() {
		for (Map.Entry<String, String> entry : identifierMap.entrySet()) {
			LOG.info(String.format("Identifier map now contains [%s - %s]", entry.getKey(), entry.getValue()));
		}
	}

	public void addIdentifier(String id, String combinedId) {
		if (identifierMap == null) {
			LOG.error("Cache has not been initialized");
			return;
		}
		identifierMap.put(id, combinedId);
	}
}
