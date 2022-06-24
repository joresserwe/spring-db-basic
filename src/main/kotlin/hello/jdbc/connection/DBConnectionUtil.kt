package hello.jdbc.connection

import hello.jdbc.connection.ConnectionConst.Companion.PASSWORD
import hello.jdbc.connection.ConnectionConst.Companion.URL
import hello.jdbc.connection.ConnectionConst.Companion.USERNAME
import mu.KotlinLogging
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class DBConnectionUtil {

    companion object {
        private val log = KotlinLogging.logger {}

        fun getConnection(): Connection {
            lateinit var connection: Connection

            try {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)
                log.info { "get connection=${connection}, class=${connection::class}" }
            } catch (e: SQLException) {
                e.printStackTrace()
                throw IllegalStateException(e)
            }

            return connection
        }
    }
}
