package hello.jdbc.connection

import com.zaxxer.hikari.HikariDataSource
import hello.jdbc.connection.ConnectionConst.Companion.PASSWORD
import hello.jdbc.connection.ConnectionConst.Companion.URL
import hello.jdbc.connection.ConnectionConst.Companion.USERNAME
import io.kotest.core.spec.style.FunSpec
import mu.KotlinLogging
import org.springframework.jdbc.datasource.DriverManagerDataSource
import java.sql.DriverManager
import javax.sql.DataSource

class ConnectionTest : FunSpec({

    test("driverManager") {
        val connection1 = DriverManager.getConnection(URL, USERNAME, PASSWORD)
        val connection2 = DriverManager.getConnection(URL, USERNAME, PASSWORD)
        log.info { "connection=${connection1}, class=${connection1::class}" }
        log.info { "connection=${connection2}, class=${connection2::class}" }
    }

    test("dataSourceDriverManager") {
        // DriverManagerDataSource - 항상 새로운 Connection 획득
        val dataSource = DriverManagerDataSource(URL, USERNAME, PASSWORD)
        useDataSource(dataSource)
    }

    test("dataSourceConnectionPool") {
        // Connection Pooling
        val dataSource = HikariDataSource().apply {
            jdbcUrl = URL
            username = USERNAME
            password = PASSWORD
            maximumPoolSize = 10
            poolName = "MyPool"
        }

        useDataSource(dataSource)
        Thread.sleep(1000)
    }
}) {
    companion object {

        private val log = KotlinLogging.logger {}

        private fun useDataSource(dataSource: DataSource) {
            val connection1 = dataSource.connection
            val connection2 = dataSource.connection
            /*  val connection3 = dataSource.connection
              val connection4 = dataSource.connection
              val connection5 = dataSource.connection
              val connection6 = dataSource.connection
              val connection7 = dataSource.connection
              val connection8 = dataSource.connection
              val connection9 = dataSource.connection
              val connection10 = dataSource.connection
              val connection11 = dataSource.connection
              val connection12 = dataSource.connection*/
            log.info { "connection=${connection1}, class=${connection1::class}" }
            log.info { "connection=${connection2}, class=${connection2::class}" }
        }
    }
}
