package codemwnci

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage
import org.eclipse.jetty.websocket.api.annotations.WebSocket
import spark.Spark.*
import java.util.concurrent.atomic.AtomicLong

fun main(args: Array<String>){
    port(9000)
    staticFileLocation("/public")
    webSocket("/chat", ChatWSHandler::class.java)
    init()
}

class User(val id: Long, val name: String)
class Message(val msgType: String, val data: Any)
class Invio(val nome: String, val messaggio: String)

@WebSocket
class ChatWSHandler {

    val users = HashMap<Session, User>()
    var uids = AtomicLong(0)

    @OnWebSocketConnect
    fun connected(session: Session) = println("Session connected")

    @OnWebSocketMessage
    fun message(session: Session, message: String) {
        val json = ObjectMapper().readTree(message)
        // (type: "join/say", data: "name/msg")
        when (json.get("type").asText()) {
            "join" -> {

                val name = json.get("data").asText()
                val user = User(uids.getAndIncrement(), name)
                users[session] = User(uids.getAndIncrement(), name)
                println("Utente connesso: " + json.get("data").asText())
            }

            "say" -> {
                val invio = Invio(users[session]!!.name, json.get("data").asText())
                println("Messaggio ricevuto: " + json.get("data").asText())
            }
        }
        println(message = "json msg $message")
    }

    @OnWebSocketClose
    fun disconnect(session: Session, code: Int, reason: String?) {


        println(message = "Uscito utente: ${users[session]?.name}")

        val user = users.remove(session)

    }


    private fun emit(session: Session, message: Message){
        session.remote.sendString(jacksonObjectMapper().writeValueAsString(message))
    }

    private fun broadcast(message: Message){
        users.forEach {
            emit(it.key, message)
        }
    }

    private fun broadcastToOthers(session: Session, message: Message){
        users.filter { it.key != session }.forEach {
            emit(it.key, message)
        }
    }

}