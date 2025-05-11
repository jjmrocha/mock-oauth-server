package net.uiqui.oauth.mock.tools

import com.google.gson.Gson
import kotlin.reflect.KClass

internal object JsonHelper {
    private val jsonEncoder = Gson()

    fun toJson(obj: Any): String = jsonEncoder.toJson(obj)

    fun <T : Any> fromJson(
        json: String,
        type: KClass<T>,
    ): T = jsonEncoder.fromJson(json, type.java)
}
