package vez.chat.model

import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import java.net.URL

fun MessageVM.asModel(contentType: ContentType = ContentType.MARKDOWN): MessageModel =
    MessageModel(
        this.content,
        contentType,
        this.sent,
        this.user.name,
        this.user.avatarImageLink.toString(),
        this.id
    )

fun MessageModel.asViewModel(): MessageVM =
    MessageVM(
        contentType.render(this.content),
        UserVM(this.username, URL(this.userAvatarImageLink)),
        this.sent,
        this.id
    )

fun ContentType.render(content: String): String = when(this) {
    ContentType.PLAIN -> content
    ContentType.MARKDOWN -> {
        val flavour = CommonMarkFlavourDescriptor()

        HtmlGenerator( content,
            MarkdownParser(flavour).buildMarkdownTreeFromString(content),
            flavour
        ).generateHtml()
    }
}