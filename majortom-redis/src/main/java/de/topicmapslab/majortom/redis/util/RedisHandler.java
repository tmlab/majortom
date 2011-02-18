/**
 * 
 */
package de.topicmapslab.majortom.redis.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisException;
import redis.clients.jedis.Pipeline;

/**
 * Utility class to connect to Redis store and enable concurrent access
 * 
 * @author Sven Krosse
 * 
 */
public class RedisHandler {
	private static final String AUTO_INCREMENT_KEY = "id";

	/*
	 * lock for thread access
	 */
	private static final Lock lock = new ReentrantLock();

	private Jedis jedis;

	private final String host;
	private final int port;
	private final String password;
	private final int database;

	/**
	 * constructor
	 * 
	 * @param host
	 *            the host
	 * @param port
	 *            the port or {@link Integer#MIN_VALUE} if port is not needed
	 * @param password
	 *            the password or <code>null</code> if password is not needed
	 * @param database
	 *            the database number
	 */
	public RedisHandler(final String host, final int port, final String password, final int database) {
		this.host = host;
		this.database = database;
		this.port = port;
		this.password = password;
		reconnect();
	}

	/**
	 * Internal method to open a connection and close existing one
	 */
	private void reconnect() {
		if (jedis != null) {
			try {
				jedis.disconnect();
				jedis = null;
			} catch (Exception e) {
				// IGNORE
			}
		}
		if (Integer.MIN_VALUE == port) {
			jedis = new Jedis(host);
		} else {
			jedis = new Jedis(host, port);
		}
		if (password != null) {
			jedis.auth(password);
		}
		jedis.select(database);
	}

	/**
	 * Fetch the value for the given key. The method locked for all threads
	 * until caller is finished.
	 * 
	 * @param key
	 *            the key
	 * @return the value
	 */
	public String get(final String key) {
		while (!lock.tryLock()) {
			// VOID
		}
		try {
			try {
				return jedis.get(key);
			} catch (JedisException e) {
				reconnect();
				return jedis.get(key);
			}
		} finally {
			lock.unlock();
		}
	}

	public boolean exists(final String key) {
		while (!lock.tryLock()) {
			// VOID
		}
		try {
			try {
				return jedis.exists(key);
			} catch (JedisException e) {
				reconnect();
				return jedis.exists(key);
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Fetch the value for the given key. The method locked for all threads
	 * until caller is finished.
	 * 
	 * @param key
	 *            the key
	 * @return the value
	 */
	public Set<String> smembers(final String key) {
		while (!lock.tryLock()) {
			// VOID
		}
		try {
			try {
				return jedis.smembers(key);
			} catch (JedisException e) {
				reconnect();
				return jedis.smembers(key);
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Fetch the value for the given key. The method locked for all threads
	 * until caller is finished.
	 * 
	 * @param key
	 *            the key
	 * @param field
	 *            the field
	 * @return the value
	 */
	public String get(final String key, final String field) {
		while (!lock.tryLock()) {
			// VOID
		}
		try {
			try {
				return jedis.hget(key, field);
			} catch (JedisException e) {
				reconnect();
				return jedis.hget(key, field);
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Fetch all key-value-pairs for the given key. The method locked for all
	 * threads until caller is finished.
	 * 
	 * @param key
	 *            the key
	 * @return the key-value-pairs
	 */
	public Map<String, String> hgetall(final String key) {
		while (!lock.tryLock()) {
			// VOID
		}
		try {
			try {
				return jedis.hgetAll(key);
			} catch (JedisException e) {
				reconnect();
				return jedis.hgetAll(key);
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Fetch the value for the given key. The method locked for all threads
	 * until caller is finished.
	 * 
	 * @param key
	 *            the key
	 * @param field
	 *            the field
	 */
	public void hdel(final String key, final String field) {
		while (!lock.tryLock()) {
			// VOID
		}
		try {
			try {
				jedis.hdel(key, field);
			} catch (JedisException e) {
				reconnect();
				jedis.hdel(key, field);
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Delete the keys
	 * 
	 * @param keys
	 *            the keys
	 */
	public void del(final String... keys) {
		while (!lock.tryLock()) {
			// VOID
		}
		try {
			try {
				jedis.del(keys);
			} catch (JedisException e) {
				reconnect();
				jedis.del(keys);
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Store the given value for the given key. The method locked for all
	 * threads until caller is finished.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public void set(final String key, final String value) {
		while (!lock.tryLock()) {
			// VOID
		}
		try {
			try {
				jedis.set(key, value);
			} catch (JedisException e) {
				reconnect();
				jedis.set(key, value);
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Store the given value for the given key. The method locked for all
	 * threads until caller is finished.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public void set(final String key, final String field, final String value) {
		while (!lock.tryLock()) {
			// VOID
		}
		try {
			try {
				jedis.hset(key, field, value);
			} catch (JedisException e) {
				reconnect();
				jedis.hset(key, field, value);
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Store the given value for the given key. The method locked for all
	 * threads until caller is finished.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public void sadd(final String key, final String value) {
		while (!lock.tryLock()) {
			// VOID
		}
		try {
			try {
				jedis.sadd(key, value);
			} catch (JedisException e) {
				reconnect();
				jedis.sadd(key, value);
			}
		} finally {
			lock.unlock();
		}
	}

	public Object call(final String method, final Object... arguments) {
		while (!lock.tryLock()) {
			// VOID
		}
		try {
			Class<?>[] parameterTypes = new Class[arguments.length];
			int i = 0;
			for (Object argument : arguments) {
				parameterTypes[i++] = argument.getClass();
			}
			Method m = Jedis.class.getMethod(method, parameterTypes);
			Object o;
			try {
				o = m.invoke(jedis, arguments);
			} catch (JedisException e) {
				reconnect();
				o = m.invoke(jedis, arguments);
			}
			return o;
		} catch (SecurityException e) {
			e.printStackTrace(System.err);
		} catch (NoSuchMethodException e) {
			e.printStackTrace(System.err);
		} catch (IllegalArgumentException e) {
			e.printStackTrace(System.err);
		} catch (IllegalAccessException e) {
			e.printStackTrace(System.err);
		} catch (InvocationTargetException e) {
			e.printStackTrace(System.err);
		} finally {
			lock.unlock();
		}
		return null;
	}

	/**
	 * Calls intersect
	 * 
	 * @param keys
	 *            the keys
	 * @return the intersect result
	 */
	public Set<String> sinter(final String... keys) {
		while (!lock.tryLock()) {
			// VOID
		}
		try {
			return jedis.sinter(keys);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Returns a new construct id from redis
	 * 
	 * @return the new redis id
	 */
	public long nextId() {
		while (!lock.tryLock()) {
			// VOID
		}
		try {
			try {
				return jedis.incr(AUTO_INCREMENT_KEY);
			} catch (JedisException e) {
				reconnect();
				return jedis.incr(AUTO_INCREMENT_KEY);
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Returns all known keys
	 * 
	 * @param filter
	 *            the filter
	 * @return the key set
	 */
	public Set<String> list(String filter) {
		while (!lock.tryLock()) {
			// VOID
		}
		try {
			try {
				return jedis.keys(filter);
			} catch (JedisException e) {
				reconnect();
				return jedis.keys(filter);
			}
		} finally {
			lock.unlock();
		}
	}

	public Set<String> sunion(final String... keys) {
		while (!lock.tryLock()) {
			// VOID
		}
		try {
			try {
				return jedis.sunion(keys);
			} catch (JedisException e) {
				reconnect();
				return jedis.sunion(keys);
			}
		} finally {
			lock.unlock();
		}
	}

	public void srem(String key, String member) {
		while (!lock.tryLock()) {
			// VOID
		}
		try {
			try {
				jedis.srem(key, member);
			} catch (JedisException e) {
				reconnect();
				jedis.srem(key, member);
			}
		} finally {
			lock.unlock();
		}

	}

	/**
	 * Clear the database
	 */
	public void clear() {
		while (!lock.tryLock()) {
			// VOID
		}
		try {
			try {
				jedis.flushDB();
			} catch (JedisException e) {
				reconnect();
				jedis.flushDB();
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Returns a pipeline (Which is used OUTSIDE the LOCK)!!!
	 */
	public Pipeline pipeline() {
		while (!lock.tryLock()) {
			// VOID
		}
		try {
			try {
				return jedis.pipelined();
			} catch (JedisException e) {
				reconnect();
				return jedis.pipelined();
			}
		} finally {
			lock.unlock();
		}
	}

}
