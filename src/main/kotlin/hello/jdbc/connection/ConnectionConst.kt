package hello.jdbc.connection

abstract class ConnectionConst {
    companion object {
        const val URL = "jdbc:h2:tcp://localhost/./jdbc"
        const val USERNAME = "sa"
        const val PASSWORD = ""
    }
}
