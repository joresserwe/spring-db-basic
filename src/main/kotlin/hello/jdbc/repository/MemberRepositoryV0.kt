package hello.jdbc.repository

import hello.jdbc.connection.DBConnectionUtil
import hello.jdbc.domain.Member
import java.sql.SQLException

/**
 * JDBC - DriverManager 사용
 */
class MemberRepositoryV0 {

    fun save(member: Member): Member {
        val sql = "insert into member(member_id, money) values (?, ?)"

        try {
            getConnection().use { connection ->
                connection.prepareStatement(sql).use { pstmt ->
                    pstmt.setString(1, member.memberId)
                    pstmt.setInt(2, member.money)
                    pstmt.executeUpdate()
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            throw e
        }
        return member
    }

    fun findById(memberId: String): Member {
        val sql = "select * from member where member_id = ?"

        try {
            getConnection().use { connection ->
                connection.prepareStatement(sql).use { pstmt ->
                    pstmt.setString(1, memberId)
                    pstmt.executeQuery().use { rs ->
                        if (rs.next())
                            return Member(
                                rs.getString("member_id"),
                                rs.getInt("money")
                            )
                        else
                            throw NoSuchElementException("member not found memberId=${memberId}")

                    }
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            throw e
        }
    }

    fun update(memberId: String, money: Int) {
        val sql = "update member set money=? where member_id=?"

        try {
            getConnection().use { connection ->
                connection.prepareStatement(sql).use { pstmt ->
                    pstmt.setInt(1, money)
                    pstmt.setString(2, memberId)
                    pstmt.executeUpdate()
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            throw e
        }
    }

    fun delete(memberId: String) {
        val sql = "delete from member where member_id=?"

        try {
            getConnection().use { connection ->
                connection.prepareStatement(sql).use { pstmt ->
                    pstmt.setString(1, memberId)
                    pstmt.executeUpdate()
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            throw e
        }
    }

    private fun getConnection() = DBConnectionUtil.getConnection()
}

