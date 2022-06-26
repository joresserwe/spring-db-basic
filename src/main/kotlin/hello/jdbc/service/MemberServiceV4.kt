package hello.jdbc.service

import hello.jdbc.repository.MemberRepository
import mu.KotlinLogging
import org.springframework.transaction.annotation.Transactional

/**
 * Transaction - Transaction Template
 */
open class MemberServiceV4(private val memberRepository: MemberRepository) {
    private val log = KotlinLogging.logger {}

    @Transactional
    open fun accountTransfer(fromId: String, toId: String, money: Int) {
        bizLogic(fromId, toId, money)
    }

    private fun bizLogic(fromId: String, toId: String, money: Int) {
        val fromMember = memberRepository.findById(fromId)
        val toMember = memberRepository.findById(toId)
        memberRepository.update(fromId, fromMember.money - money)
        check(toMember.memberId != "ex") { "이체중 예외 발생" }
        memberRepository.update(toId, toMember.money + money)
    }
}

