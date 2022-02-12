package kg.iaau.diploma.local_storage.prefs

import android.content.SharedPreferences
import java.lang.IllegalArgumentException
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class PrefDelegate<V>(
    private val pref: SharedPreferences,
    private val key: String,
    private val defaultValue: V
) : ReadWriteProperty<Any?, V> {

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: V) {
        with(pref.edit()) {
            when (value) {
                is String -> putString(key, value)
                is Int -> putInt(key, value)
                is Long -> putLong(key, value)
                is Float -> putFloat(key, value)
                is Boolean -> putBoolean(key, value)
                else -> throw IllegalArgumentException("Not found realization for $defaultValue")
            }
            apply()
        }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): V {
        with(pref) {
            return when (defaultValue) {
                is String -> (getString(key, defaultValue) as? V) ?: defaultValue
                is Int -> (getInt(key, defaultValue) as? V) ?: defaultValue
                is Long -> (getLong(key, defaultValue) as? V) ?: defaultValue
                is Float -> (getFloat(key, defaultValue) as? V) ?: defaultValue
                is Boolean -> (getBoolean(key, defaultValue) as? V) ?: defaultValue
                else -> throw IllegalArgumentException("Not found realization for $defaultValue")
            }
        }
    }
}