package vez.chat.service

import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service
import vez.chat.model.MessageVM
import vez.chat.model.asModel
import vez.chat.model.asViewModel
import vez.chat.repo.MessageRepository

@Service
@Primary
class PersistentMessageService(val messageRepository: MessageRepository) : MessageService {

    override fun latest(): List<MessageVM> =
        messageRepository.findLatest()
            .map { it.asViewModel() }


    override fun after(lastMessageId: String): List<MessageVM> =
        messageRepository.findLatest(lastMessageId)
            .map { it.asViewModel() }

    override fun post(msg: MessageVM): MessageVM =
        messageRepository.save( msg.asModel() ).asViewModel()

}