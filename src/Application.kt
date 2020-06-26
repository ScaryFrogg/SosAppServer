package me.vojinpuric.SosAppServer

import com.github.mustachejava.DefaultMustacheFactory
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.mustache.Mustache
import io.ktor.mustache.MustacheContent
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(Mustache) {
        mustacheFactory = DefaultMustacheFactory("templates/mustache")
    }
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }
    val lastKnownLocations = HashMap<String, LastLocation>()
    val location1 = LastLocation(44.78487, 20.46336)//predefined location for testing //TODO delete
    lastKnownLocations.put("1", location1)//predefined location for testing //TODO delete

    routing {
        get("/") {
            val userId = call.request.queryParameters["id"]
            if (lastKnownLocations.containsKey(userId)) {
                var lastLocation = lastKnownLocations[userId]
                val user = SosUser(userId!!, lastLocation!!)
                call.respond(MustacheContent("index.hbs", mapOf("user" to user)))
            } else {
                if (userId != null) {
                    var lastLocation = lastKnownLocations[userId]
                    if (lastLocation == null) {
                        lastLocation = LastLocation(-33.8569, 151.2152)

                    }
                    val user = SosUser(userId, lastLocation)
                    call.respond(MustacheContent("index.hbs", mapOf("user" to user)))

                } else println("userId is null")
            }
        }
        route("/api") {
            get {
                val userId = call.request.queryParameters["id"]
                if (userId != null) {
                    lastKnownLocations.get(userId)?.let { it1 ->
                        call.respond(it1)
                    }
                } else call.respond("error")
            }
            post {
                with(call.receive<String>().replace("\"", "").split(",")) {
                    val ll = LastLocation(this.first().toDouble(), this[1].toDouble())
                    lastKnownLocations[this[2]] = ll
                }
            }

        }
    }
}



