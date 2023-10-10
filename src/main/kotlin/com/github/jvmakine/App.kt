package com.github.jvmakine

import misk.web.Get
import misk.web.actions.WebAction

fun main() {
    val server = JettyBuilder()
        .withWebAction(object: WebAction {
            @Get("/foo") fun foo(): String { return "bar" }
        })
        .build()

    server.start()
    server.join()
}