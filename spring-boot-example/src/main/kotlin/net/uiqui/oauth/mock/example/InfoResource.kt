package net.uiqui.oauth.mock.example

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(
    path = ["info"],
    produces = [MediaType.APPLICATION_JSON_VALUE]
)
class InfoResource {
    @GetMapping("/runtime")
    fun runtimeInfo(): ResponseEntity<Map<String, Any>> {
        val runtime = Runtime.getRuntime()
        val response = mapOf(
            "availableProcessors" to runtime.availableProcessors(),
            "freeMemory" to runtime.freeMemory(),
            "totalMemory" to runtime.totalMemory(),
            "maxMemory" to runtime.maxMemory(),
            "java.runtime.version" to System.getProperty("java.runtime.version"),
        )
        return ResponseEntity.ok().body(response)
    }
}
