package hello.jdbc.service

import hello.jdbc.connection.ConnectionConst.Companion.PASSWORD
import hello.jdbc.connection.ConnectionConst.Companion.URL
import hello.jdbc.connection.ConnectionConst.Companion.USERNAME
import hello.jdbc.domain.Member
import hello.jdbc.repository.MemberRepositoryV1
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.startWith
import mu.KotlinLogging
import org.springframework.jdbc.datasource.DriverManagerDataSource

/**
 * Transaction이 없어서 문제 발생
 */
internal class MemberServiceV1Test : DescribeSpec({

    isolationMode = IsolationMode.InstancePerLeaf

    lateinit var memberRepository: MemberRepositoryV1
    lateinit var memberService: MemberServiceV1

    beforeSpec {
        log.info { "Before Spec - 새로운 connection을 받는다." }
        val dataSource = DriverManagerDataSource(URL, USERNAME, PASSWORD)
        memberRepository = MemberRepositoryV1(dataSource)
        memberService = MemberServiceV1(memberRepository)
    }

    afterSpec {
        log.info { "DB를 초기화 한다." }
        memberRepository.delete(MEMBER_A)
        memberRepository.delete(MEMBER_B)
        memberRepository.delete(MEMBER_EX)
    }

    describe("No Transaction") {
        context("정상적인 상황") {
            val memberA = Member(MEMBER_A, 10000)
            val memberB = Member(MEMBER_B, 10000)
            memberRepository.save(memberA)
            memberRepository.save(memberB)
            context("A가 B에게 이체를 함") {
                val money = 2000
                memberService.accountTransfer(memberA.memberId, memberB.memberId, money)
                it("A는 돈이 빠지고, B는 돈이 늘어야한다.") {
                    val findMemberA = memberRepository.findById(memberA.memberId)
                    val findMemberB = memberRepository.findById(memberB.memberId)
                    findMemberA.money shouldBe memberA.money - money
                    findMemberB.money shouldBe memberB.money + money
                }
            }
        }
        context("예외 발생") {
            val memberA = Member(MEMBER_A, 10000)
            val memberEX = Member(MEMBER_EX, 10000) // 예외 대상
            memberRepository.save(memberA)
            memberRepository.save(memberEX)
            context("이체도중 예외가 발생하면") {
                val money = 2000
                val exception = shouldThrowExactly<IllegalStateException> {
                    memberService.accountTransfer(memberA.memberId, memberEX.memberId, money)
                }
                exception.message should startWith("이체중 예외 발생")

                it("A는 돈이 빠지고, EX는 돈이 그대로다") {
                    val findMemberA = memberRepository.findById(memberA.memberId)
                    val findMemberEX = memberRepository.findById(memberEX.memberId)
                    findMemberA.money shouldBe memberA.money - money
                    findMemberEX.money shouldBe memberEX.money
                }
            }
        }
    }


}) {
    companion object {
        private val log = KotlinLogging.logger {}
        private const val MEMBER_A = "memberA"
        private const val MEMBER_B = "memberB"
        private const val MEMBER_EX = "ex"
    }
}
