package hello.jdbc.service

import hello.jdbc.repository.MemberRepositoryV2
import mu.KotlinLogging
import java.sql.Connection
import javax.sql.DataSource

/**
 * Transaction - Parameter, Connection Pool
 */
class MemberServiceV2(
    private val memberRepository: MemberRepositoryV2,
    private val dataSource: DataSource
) {
    private val log = KotlinLogging.logger {}

    fun accountTransfer(fromId: String, toId: String, money: Int) {

        dataSource.connection.use { con ->
            try {
                con.autoCommit = false
                bizLogic(con, fromId, toId, money)
                con.commit()
            } catch (e: Exception) {
                con.rollback()
                throw e
            } finally {
                con.autoCommit = true // Connection Poll 생각
            }
        }
    }

    private fun bizLogic(con: Connection, fromId: String, toId: String, money: Int) {
        val fromMember = memberRepository.findById(fromId, con)
        val toMember = memberRepository.findById(toId, con)
        memberRepository.update(fromId, fromMember.money - money, con)
        check(toMember.memberId != "ex") { "이체중 예외 발생" }
        memberRepository.update(toId, toMember.money + money, con)
    }
}

