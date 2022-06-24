package hello.jdbc.connection

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import mu.KotlinLogging

class DBConnectionUtilTest : FunSpec({
    val log = KotlinLogging.logger {}

    test("Connection") {
        val connection = DBConnectionUtil.getConnection()
        connection.shouldNotBeNull()
    }
})
