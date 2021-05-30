package vez.chat

import vez.chat.model.MessageModel
import vez.chat.model.MessageVM
import java.time.temporal.ChronoUnit

fun MessageVM.copyVmForTest() = copy( id = null, sent = sent.truncatedTo(ChronoUnit.MILLIS) )

fun MessageModel.copyForTest() = copy( id = null, sent = sent.truncatedTo(ChronoUnit.MILLIS) )