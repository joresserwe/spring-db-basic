package hello.jdbc.service

import hello.jdbc.repository.MemberRepositoryV1

class MemberServiceV1(private val memberRepository: MemberRepositoryV1) {

    fun accountTransfer(fromId: String, toId: String, money: Int) {
        val fromMember = memberRepository.findById(fromId)
        val toMember = memberRepository.findById(toId)

        memberRepository.update(fromId, fromMember.money - money)
        check(toMember.memberId != "ex") { "이체중 예외 발생" }
        memberRepository.update(toId, toMember.money + money)

    }
}
