package vez.chat.service

import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service
import vez.chat.model.ContentType
import vez.chat.model.MessageModel
import vez.chat.model.MessageVM
import vez.chat.model.UserVM
import vez.chat.repo.MessageRepository
import java.net.URL

@Service
@Primary
class PersistentMessageService(val messageRepository: MessageRepository) : MessageService {

    override fun latest(): List<MessageVM> =
        messageRepository.findLatest()
            .map {
                    MessageVM(it.content,
                        UserVM( it.username, URL(it.userAvatarImageLink) ),
                        it.sent,
                        it.id
                    )
            }


    override fun after(lastMessageId: String): List<MessageVM> =
        messageRepository.findLatest(lastMessageId)
            .map {
                    MessageVM(it.content,
                        UserVM( it.username, URL(it.userAvatarImageLink) ),
                        it.sent,
                        it.id
                    )
            }

    override fun post(msg: MessageVM) {
        messageRepository.save(
                MessageModel(msg.content,
                            ContentType.PLAIN,
                            msg.sent,
                            msg.user.name,
                            msg.user.avatarImageLink.toString()
                )
        )
    }
}