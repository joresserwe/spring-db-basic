package hello.jdbc.repository

import hello.jdbc.domain.Member
import mu.KotlinLogging
import org.springframework.jdbc.datasource.DataSourceUtils
import org.springframework.jdbc.support.JdbcUtils
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator
import java.sql.*
import javax.sql.DataSource

/**
 * SQLExceptionTranslator 추가
 */
class MemberRepositoryV4_2(private val dataSource: DataSource) : MemberRepository {

    private val log = KotlinLogging.logger {}
    private val exTranslator = SQLErrorCodeSQLExceptionTranslator(dataSource)

    override fun save(member: Member): Member {
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
            throw exTranslator.translate("save", sql, e)!!
        } finally {
            close(connection, pstmt, null)
        }
        return member
    }

    override fun findById(memberId: String): Member {
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
            throw exTranslator.translate("save", sql, e)!!
        } finally {
            close(connection, pstmt, rs)
        }
    }

    override fun update(memberId: String, money: Int) {
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
            throw exTranslator.translate("save", sql, e)!!
        } finally {
            close(connection, pstmt, null)
        }
    }

    override fun delete(memberId: String) {
        val sql = "delete from member where member_id=?"

        lateinit var connection: Connection
        lateinit var pstmt: PreparedStatement

        try {
            connection = getConnection()
            pstmt = connection.prepareStatement(sql)
            pstmt.setString(1, memberId)
            pstmt.executeUpdate()
        } catch (e: SQLException) {
            throw exTranslator.translate("save", sql, e)!!
        } finally {
            close(connection, pstmt, null)
        }
    }

    private fun getConnection(): Connection {
        // Transaction 동기화를 사용하려면 DataSourceUtils를 사용해야 한다.
        val connection = DataSourceUtils.getConnection(dataSource)
        log.info { "get connection=${connection}, class=${connection::class}" }
        return connection
    }

    private fun close(con: Connection, stmt: Statement, rs: ResultSet?) {
        JdbcUtils.closeResultSet(rs)
        JdbcUtils.closeStatement(stmt)
        //JdbcUtils.closeConnection(con)
        // Transaction 동기화를 사용하려면 DataSourceUtils를 사용해야 한다.
        DataSourceUtils.releaseConnection(con, dataSource)
    }
}

