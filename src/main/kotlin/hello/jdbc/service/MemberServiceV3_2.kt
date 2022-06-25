package hello.jdbc.service

import hello.jdbc.repository.MemberRepositoryV3
import mu.KotlinLogging
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate

/**
 * Transaction - Transaction Template
 */
class MemberServiceV3_2(
    private val memberRepository: MemberRepositoryV3,
    private val transactionManager: PlatformTransactionManager
) {

    private val log = KotlinLogging.logger {}
    private val txTemplate = TransactionTemplate(transactionManager)

    fun accountTransfer(fromId: String, toId: String, money: Int) {
        txTemplate.executeWithoutResult {
            bizLogic(fromId, toId, money)
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

