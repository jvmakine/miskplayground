package com.github.jvmakine

import com.github.jvmakine.misk.findAnnotationWithOverrides
import misk.asAction
import misk.web.DispatchMechanism
import misk.web.Get
import misk.web.NetworkInterceptor
import misk.web.actions.WebAction
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import javax.servlet.Servlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.reflect.full.functions

class JettyBuilder() {
    private var port = 8080
    private val handler = ServletContextHandler()
    private val webActions: MutableList<WebAction> = mutableListOf()

    fun port(value: Int): JettyBuilder {
        this.port = value
        return this
    }

    fun withServlet(path: String, servlet: Servlet): JettyBuilder {
        handler.addServlet(ServletHolder(servlet), path)
        return this
    }

    fun withWebAction(action: WebAction): JettyBuilder {
        webActions.add(action)
        return this
    }

    fun build(): Server {
        val server = Server(port)
        server.setHandler(handler)

        webActions.forEach { webAction ->
            val clazz = webAction::class
            clazz.functions.forEach { func ->
                func.findAnnotationWithOverrides<Get>()?.let { get ->
                    val path = get.pathPattern
                    val action = func.asAction(DispatchMechanism.GET)

                    handler.addServlet(ServletHolder(object: HttpServlet() {
                        override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
                            val result = action.function.call(webAction)
                            action.responseContentType?.toString()?.let { response.contentType = it }

                            response.status = HttpServletResponse.SC_OK
                            response.writer.println(result)
                        }
                    }), path)
                }
            }
        }
        return server
    }
}