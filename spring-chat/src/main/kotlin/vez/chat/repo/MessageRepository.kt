package vez.chat.repo

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import vez.chat.model.MessageModel

interface MessageRepository: CrudRepository<MessageModel, String>  {

    @Query("select * from MESSAGES order by SENT desc limit 10")
    fun findLatest(): List<MessageModel>

    @Query("select * from MESSAGES where SENT > (select SENT from MESSAGES where ID = :id) order by SENT desc")
    fun findLatest(@Param("id") id: String): List<MessageModel>
}