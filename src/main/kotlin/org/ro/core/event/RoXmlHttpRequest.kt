package org.ro.core.event

import org.ro.core.Utils
import org.ro.core.aggregator.IAggregator
import org.ro.handler.ResponseHandler
import org.ro.to.Link
import org.ro.to.Method
import org.ro.ui.kv.UiManager
import org.w3c.xhr.XMLHttpRequest

/**
 * The name is somewhat misleading, see: https://en.wikipedia.org/wiki/XMLHttpRequest
 */
class RoXmlHttpRequest {

    fun invoke(link: Link, aggregator: IAggregator?) {
        val url = link.href
        if (EventStore.isCached(url)) {
            processCached(url)
        } else {
            process(link, aggregator)
        }
    }

    private fun processCached(url: String) {
        val le = EventStore.find(url)!!
        console.log("[RoXHR.processCached]")
        console.log(le)
        if (le.isRoot) {
            le.aggregator?.reset()
        }
        le.retrieveResponse()
        ResponseHandler.handle(le)
        EventStore.cached(url)
    }

    private fun process(link: Link, aggregator: IAggregator?) {
        val method = link.method
        var url = link.href
        if (method != Method.POST.operation) {
            url += Utils.argumentsAsUrlParameter(link)
        }
        val credentials: String = UiManager.getCredentials()

        val xhr = XMLHttpRequest()
        xhr.open(method, url, true)
        xhr.setRequestHeader("Authorization", "Basic $credentials")
        xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8")
        xhr.setRequestHeader("Accept", "application/json")

        xhr.onload = { _ -> resultHandler(url, xhr.responseText) }
        xhr.onerror = { _ -> errorHandler(url, xhr.responseText) }
        xhr.ontimeout = { _ -> errorHandler(url, xhr.responseText) }

        var body = ""
        if (link.hasArguments()) {
            body = Utils.argumentsAsBody(link)
            xhr.send(body)
        } else {
            xhr.send()
        }
        EventStore.start(url, method, body, aggregator)
    }

    private fun resultHandler(url: String, responseText: String) {
        val jsonString: String = responseText
        val logEntry: LogEntry? = EventStore.end(url, jsonString)
        if (logEntry != null) {
            ResponseHandler.handle(logEntry)
        }
    }

    private fun errorHandler(url: String, responseText: String) {
        EventStore.fault(url, responseText)
    }

}
