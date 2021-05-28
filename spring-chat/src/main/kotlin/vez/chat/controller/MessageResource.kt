package vez.chat.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import vez.chat.model.MessageVM
import vez.chat.service.MessageService

@RestController
@RequestMapping("/api/vi/messages")
class MessageResource(val messageService: MessageService) {

    @GetMapping
    fun latest( @RequestParam(value = "lastMessageId", defaultValue = "") lastMessageId: String)
        : ResponseEntity<List<MessageVM>> {

        val messages = if (lastMessageId.isNotEmpty()) {
            messageService.after(lastMessageId)
        } else {
            messageService.latest()
        }

        return if (messages.isEmpty()) {
                    with(ResponseEntity.noContent()) {
                        header("lastMessageId", lastMessageId)
                        build()
                    }
                } else {
                    with (ResponseEntity.ok()) {
                        header("lastMessageId", messages.last().id)
                        body(messages)
                    }
                }
    }
}