package hello.jdbc.service

import hello.jdbc.domain.Member
import hello.jdbc.repository.MemberRepositoryV3
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.startWith
import mu.KotlinLogging
import org.springframework.aop.support.AopUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import javax.sql.DataSource

/**
 * Transaction Manager 사용
 */
@SpringBootTest
internal class MemberServiceV3_4Test : DescribeSpec() {
    companion object {
        private val log = KotlinLogging.logger {}
        private const val MEMBER_A = "memberA"
        private const val MEMBER_B = "memberB"
        private const val MEMBER_EX = "ex"
    }

    @Autowired
    lateinit var memberRepository: MemberRepositoryV3

    @Autowired
    lateinit var memberService: MemberServiceV3_3

    @TestConfiguration
    class TestConfig(
        private val dataSource: DataSource
    ) {
        @Bean
        fun memberRepository() = MemberRepositoryV3(dataSource)

        @Bean
        fun memberService() = MemberServiceV3_3(memberRepository())
    }


    init {

        this.afterSpec {
            log.info { "DB를 초기화 한다." }
            memberRepository.delete(MEMBER_A)
            memberRepository.delete(MEMBER_B)
            memberRepository.delete(MEMBER_EX)
        }

        this.it("AOP Test") {
            log.info { "memberService class=${memberService::class}" }
            log.info { "memberRepository class=${memberRepository::class}" }
            AopUtils.isAopProxy(memberService) shouldBe true
            AopUtils.isAopProxy(memberRepository) shouldBe false
        }

        this.describe("Transaction") {
            context("정상적인 상황") {
                val memberA = Member(MEMBER_A, 10000)
                val memberB = Member(MEMBER_B, 10000)
                memberRepository.save(memberA)
                memberRepository.save(memberB)
                context("A가 B에게 이체를 함") {
                    val money = 2000
                    log.info { "## START TX" }
                    memberService.accountTransfer(memberA.memberId, memberB.memberId, money)
                    log.info { "## END TX" }
                    it("A는 돈이 빠지고, B는 돈이 늘어야한다.") {
                        val findMemberA = memberRepository.findById(memberA.memberId)
                        val findMemberB = memberRepository.findById(memberB.memberId)
                        findMemberA.money shouldBe memberA.money - money
                        findMemberB.money shouldBe memberB.money + money
                    }
                }
            }
            describe("예외 발생") {
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

                    it("Rollback이 돼 금액은 그대로다.") {
                        val findMemberA = memberRepository.findById(memberA.memberId)
                        val findMemberEX = memberRepository.findById(memberEX.memberId)
                        findMemberA.money shouldBe memberA.money
                        findMemberEX.money shouldBe memberEX.money
                    }
                }
            }
        }
    }
}
