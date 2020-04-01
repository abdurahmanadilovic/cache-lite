package io.github.kezhenxu94.cache.lite.impl

import io.github.kezhenxu94.cache.lite.Cache

/**
 * Created by kezhenxu94 on 11/14/17.
 *
 * @author kezhenxu94 (kezhenxu94 at 163 dot com)
 */
class FIFOCache(private val delegate: Cache, private val minimalSize: Int = DEFAULT_SIZE) : Cache by delegate {
	private val keyMap = object : LinkedHashMap<Any, Any>(minimalSize, .75f) {
		override fun removeEldestEntry(eldest: MutableMap.MutableEntry<Any, Any>): Boolean {
			val tooManyCachedItems = size > minimalSize
			if (tooManyCachedItems) eldestKeyToRemove = eldest.key
			return tooManyCachedItems
		}
	}

	private var eldestKeyToRemove: Any? = null

	override fun set(key: Any, value: Any) {
		delegate[key] = value
		cycleKeyMap(key)
	}

	override fun get(key: Any): Any? {
		keyMap[key]
		return delegate[key]
	}

	override fun clear() {
		keyMap.clear()
		delegate.clear()
	}

	private fun cycleKeyMap(key: Any) {
		keyMap[key] = PRESENT
		eldestKeyToRemove?.let { delegate.remove(it) }
		eldestKeyToRemove = null
	}

	companion object {
		private const val DEFAULT_SIZE = 100
		private const val PRESENT = true
	}
}
