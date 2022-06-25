package hello.jdbc.service

import hello.jdbc.repository.MemberRepositoryV3
import mu.KotlinLogging
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.DefaultTransactionDefinition

/**
 * Transaction - Transaction Manager
 */
class MemberServiceV3_1(
    private val memberRepository: MemberRepositoryV3,
    private val transactionManager: PlatformTransactionManager
) {
    private val log = KotlinLogging.logger {}

    fun accountTransfer(fromId: String, toId: String, money: Int) {

        // Transaction 시작
        val status = transactionManager.getTransaction(DefaultTransactionDefinition())
        try {
            bizLogic(fromId, toId, money)
            transactionManager.commit(status)
        } catch (e: Exception) {
            transactionManager.rollback(status)
            throw e
        }
    }

    private fun bizLogic(fromId: String, toId: String, money: Int) {
        val fromMember = memberRepository.findById(fromId)
        val toMember = memberRepository.findById(toId)
        memberRepository.update(fromId, fromMember.money - money)
        check(toMember.memberId != "ex") { "이체중 예외 발생" }
        memberRepository.update(toId, toMember.money + money)
    }
}

