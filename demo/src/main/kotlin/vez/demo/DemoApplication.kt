package vez.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.query
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*

@SpringBootApplication
class DemoApplication

fun main(args: Array<String>) {
	runApplication<DemoApplication>(*args)
}


@RestController
class MessageController(val service: MessageService) {

	@GetMapping
	fun findAll(): List<Message> = service.findMessages()

	@GetMapping("/{id}")
	fun findById(@PathVariable id: String): List<Message> = service.findMessageById(id)

	@PostMapping
	fun post(@RequestBody message: Message) = service.post(message)
}

@Service
class MessageService(val db: JdbcTemplate) {

	fun findMessages(): List<Message> = db.query("select * from MESSAGES") {
		rs, _ -> Message(rs.getString("id"), rs.getString("text"))
	}

	fun findMessageById(id: String): List<Message> = db.query("select * from MESSAGES where id = ?", id) {
			rs, _ -> Message(rs.getString("id"), rs.getString("text"))
	}

	fun post(message: Message) = db.update("insert into MESSAGES values ( ?,? )",
		message.id ?: message.text.uuid(), message.text)
}

data class Message(
	val id: String?,
	val text: String
){

	override fun toString(): String {
		return "Message(id=$id, text='$text')"
	}
}