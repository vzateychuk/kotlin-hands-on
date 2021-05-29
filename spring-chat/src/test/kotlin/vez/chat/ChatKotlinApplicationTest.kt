package vez.chat

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.RequestEntity
import vez.chat.model.ContentType
import vez.chat.model.MessageModel
import vez.chat.model.MessageVM
import vez.chat.model.UserVM
import vez.chat.repo.MessageRepository
import java.net.URI
import java.net.URL
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 *  Testing three general cases:
 *  1. Resolving message when lastMessageId is not available.
 *  2. Resolving message when lastMessageId is present.
 *  3. And sending messages.
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = [
        "spring.datasource.url=jdbc:h2:mem:testdb"
    ]
)
class ChatKotlinApplicationTest {

    @Autowired lateinit var client: TestRestTemplate;
    @Autowired lateinit var messageRepository: MessageRepository;
    lateinit var lastMessageId: String
    val now: Instant = Instant.now()
    lateinit var firstMsg: MessageModel
    lateinit var secondMsg: MessageModel
    lateinit var thirdMsg: MessageModel

    @BeforeEach
    fun setUp() {
        this.firstMsg = MessageModel("testMessage1", ContentType.PLAIN, now.minusSeconds(2), "test1", "http://test.com")
        this.secondMsg = MessageModel("testMessage2", ContentType.PLAIN, now.minusSeconds(1), "test2", "http://test.com")
        this.thirdMsg = MessageModel("testMessage3", ContentType.PLAIN, now, "test3", "http://test.com")
        val savedMessages = messageRepository.saveAll( listOf(firstMsg,secondMsg,thirdMsg) )
        lastMessageId = savedMessages.first().id ?: ""
    }

    @AfterEach
    fun tearDown() {
        messageRepository.deleteAll()
    }

    @ParameterizedTest
    @ValueSource(booleans = [true,false])
    fun `messages API returns latest messages` (withLastMessageId: Boolean) {
        val lastId = if (withLastMessageId) lastMessageId else ""

        val messages: List<MessageVM>? = client.exchange(
            RequestEntity<Any>( HttpMethod.GET, URI("/api/v1/messages?lastMessageId=$lastId") ),
            object : ParameterizedTypeReference<List<MessageVM>>() {}
        ).body


        // Use copy method, which lets you make a full copy of the instance while customizing certain fields if necessary.
        val copiedMsgList = messages?.map { with(it) { copy(id=null, sent = sent.truncatedTo(ChronoUnit.MILLIS))} }

        assertThat(copiedMsgList).containsSequence(
                MessageVM("testMessage3",
                    UserVM("test3", URL("http://test.com") ),
                    now.truncatedTo(ChronoUnit.MILLIS)
                ),
            MessageVM("testMessage2",
                UserVM("test2", URL("http://test.com") ),
                now.minusSeconds(1).truncatedTo(ChronoUnit.MILLIS)
            )
        )

        if (!withLastMessageId) {
            assertThat(copiedMsgList).last().isEqualTo(
                MessageVM("testMessage1",
                    UserVM("test1", URL("http://test.com") ),
                    now.minusSeconds(2).truncatedTo(ChronoUnit.MILLIS)
                )
            )
        }

    }

    @Test
    fun `messages posted to API are stored`() {

        client.postForEntity<Any>(
            URI("/api/v1/messages"),
            MessageVM(
                "testMessage4",
                UserVM("test4", URL("http://test.com")),
                now.plusSeconds(1)
            )
        )

        messageRepository.findAll()
            .last { it.content.contains("testMessage4") }
            .apply {
                assertThat(this.copy(id=null, sent = sent.truncatedTo(ChronoUnit.MILLIS)))
                    .isEqualTo(
                        MessageModel("testMessage4",
                            ContentType.PLAIN,
                            now.plusSeconds(1).truncatedTo(ChronoUnit.MILLIS),
                            "test4",
                            "http://test.com"
                        )
                    )
            }
    }
}