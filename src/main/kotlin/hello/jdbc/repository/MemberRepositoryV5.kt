package hello.jdbc.repository

import hello.jdbc.domain.Member
import mu.KotlinLogging
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import javax.sql.DataSource

/**
 * JDBC Template
 */
class MemberRepositoryV5(dataSource: DataSource) : MemberRepository {

    private val log = KotlinLogging.logger {}
    private val template = JdbcTemplate(dataSource)

    override fun save(member: Member): Member {
        val sql = "insert into member(member_id, money) values (?, ?)"
        template.update(sql, member.memberId, member.money)
        return member
    }

    override fun findById(memberId: String): Member {
        val sql = "select * from member where member_id = ?"
        return template.queryForObject(sql, memberRowMapper(), memberId)!!
    }

    private fun memberRowMapper() = RowMapper { rs: ResultSet, rowNum: Int ->
        Member(
            rs.getString("member_id"),
            rs.getInt("money")
        )
    }

    override fun update(memberId: String, money: Int) {
        val sql = "update member set money=? where member_id=?"
        template.update(sql, money, memberId)
    }

    override fun delete(memberId: String) {
        val sql = "delete from member where member_id=?"
        template.update(sql, memberId)
    }
}

