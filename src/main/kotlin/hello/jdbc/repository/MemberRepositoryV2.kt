package hello.jdbc.repository

import hello.jdbc.domain.Member
import mu.KotlinLogging
import org.springframework.jdbc.support.JdbcUtils
import java.sql.*
import javax.sql.DataSource

/**
 * JDBC - Connection Param
 */
class MemberRepositoryV2(private val dataSource: DataSource) {

    private val log = KotlinLogging.logger {}

    fun save(member: Member): Member {
        val sql = "insert into member(member_id, money) values (?, ?)"

        lateinit var connection: Connection
        lateinit var pstmt: PreparedStatement

        try {
            connection = getConnection()
            pstmt = connection.prepareStatement(sql)
            pstmt.setString(1, member.memberId)
            pstmt.setInt(2, member.money)
            pstmt.executeUpdate()
        } catch (e: SQLException) {
            e.printStackTrace()
            throw e
        } finally {
            close(connection, pstmt, null)
        }
        return member
    }

    fun findById(memberId: String): Member {
        val sql = "select * from member where member_id = ?"

        lateinit var connection: Connection
        lateinit var pstmt: PreparedStatement
        lateinit var rs: ResultSet

        try {
            connection = getConnection()
            pstmt = connection.prepareStatement(sql)
            pstmt.setString(1, memberId)
            rs = pstmt.executeQuery()
            if (rs.next())
                return Member(
                    rs.getString("member_id"),
                    rs.getInt("money")
                )
            else
                throw NoSuchElementException("member not found memberId=${memberId}")
        } catch (e: SQLException) {
            e.printStackTrace()
            throw e
        } finally {
            close(connection, pstmt, rs)
        }
    }

    fun findById(memberId: String, connection: Connection): Member {
        val sql = "select * from member where member_id = ?"

        lateinit var pstmt: PreparedStatement
        lateinit var rs: ResultSet

        try {
            pstmt = connection.prepareStatement(sql)
            pstmt.setString(1, memberId)
            rs = pstmt.executeQuery()
            if (rs.next())
                return Member(
                    rs.getString("member_id"),
                    rs.getInt("money")
                )
            else
                throw NoSuchElementException("member not found memberId=${memberId}")
        } catch (e: SQLException) {
            e.printStackTrace()
            throw e
        } finally {
            // Connection은 유지가 돼야한다.
            JdbcUtils.closeResultSet(rs)
            JdbcUtils.closeStatement(pstmt)
            //JdbcUtils.closeConnection(connection)
        }
    }

    fun update(memberId: String, money: Int) {
        val sql = "update member set money=? where member_id=?"

        lateinit var connection: Connection
        lateinit var pstmt: PreparedStatement

        try {
            connection = getConnection()
            pstmt = connection.prepareStatement(sql)
            pstmt.setInt(1, money)
            pstmt.setString(2, memberId)
            pstmt.executeUpdate()
        } catch (e: SQLException) {
            e.printStackTrace()
            throw e
        } finally {
            close(connection, pstmt, null)
        }
    }

    fun update(memberId: String, money: Int, connection: Connection) {
        val sql = "update member set money=? where member_id=?"

        lateinit var pstmt: PreparedStatement

        try {
            pstmt = connection.prepareStatement(sql)
            pstmt.setInt(1, money)
            pstmt.setString(2, memberId)
            pstmt.executeUpdate()
        } catch (e: SQLException) {
            e.printStackTrace()
            throw e
        } finally {
            JdbcUtils.closeStatement(pstmt)
        }
    }

    fun delete(memberId: String) {
        val sql = "delete from member where member_id=?"

        lateinit var connection: Connection
        lateinit var pstmt: PreparedStatement

        try {
            connection = getConnection()
            pstmt = connection.prepareStatement(sql)
            pstmt.setString(1, memberId)
            pstmt.executeUpdate()
        } catch (e: SQLException) {
            e.printStackTrace()
            throw e
        } finally {
            close(connection, pstmt, null)
        }
    }

    private fun getConnection(): Connection {
        val connection = dataSource.connection
        log.info { "get connection=${connection}, class=${connection::class}" }
        return connection
    }

    private fun close(con: Connection, stmt: Statement, rs: ResultSet?) {
        JdbcUtils.closeResultSet(rs)
        JdbcUtils.closeStatement(stmt)
        JdbcUtils.closeConnection(con)
    }
}

