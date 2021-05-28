package vez.chat.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import vez.chat.model.MessageVM
import vez.chat.service.MessageService

@Controller
class HtmlController(val messageService: MessageService) {

    @GetMapping
    fun index(model: Model): String {
        val messages: List<MessageVM> = messageService.latest()

        model["messages"] = messages
        model["lastMessageId"] = messages.lastOrNull()?.id ?: ""

        return "chat"
    }

}