/*******************************************************************************
 * Copyright 2010, Topic Map Lab ( http://www.topicmapslab.de )
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.topicmapslab.majortom.inmemory.store;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author http://www.javamex.com/tutorials/random_numbers/numerical_recipes.shtml
 *
 */
public class HighQualityRandom extends Random {

	private static final long serialVersionUID = 1L;
	private Lock l = new ReentrantLock();
	  private long u;
	  private long v = 4101842887655102017L;
	  private long w = 1;
	  
	  public HighQualityRandom() {
	    this(System.nanoTime());
	  }
	  public HighQualityRandom(long seed) {
	    l.lock();
	    u = seed ^ v;
	    nextLong();
	    v = u;
	    nextLong();
	    w = v;
	    nextLong();
	    l.unlock();
	  }
	  
	  public long nextLong() {
	    l.lock();
	    try {
	      u = u * 2862933555777941757L + 7046029254386353087L;
	      v ^= v >>> 17;
	      v ^= v << 31;
	      v ^= v >>> 8;
	      w = 4294957665L * (w & 0xffffffff) + (w >>> 32);
	      long x = u ^ (u << 21);
	      x ^= x >>> 35;
	      x ^= x << 4;
	      long ret = (x + v) ^ w;
	      return ret;
	    } finally {
	      l.unlock();
	    }
	  }
	  
	  protected int next(int bits) {
	    return (int) (nextLong() >>> (64-bits));
	  }

	}
