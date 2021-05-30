package vez.chat.service

import vez.chat.model.MessageVM

interface MessageService {

    fun latest(): List<MessageVM>

    fun after(lastMessageId: String): List<MessageVM>

    fun post(message: MessageVM): MessageVM

}
