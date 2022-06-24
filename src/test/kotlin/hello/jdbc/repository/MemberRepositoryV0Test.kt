package hello.jdbc.repository

import hello.jdbc.domain.Member
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.startWith
import mu.KotlinLogging

internal class MemberRepositoryV0Test : DescribeSpec({

    val log = KotlinLogging.logger {}
    val repository = MemberRepositoryV0()

    describe("Save 후") {

        val member = Member("memberV4", 10000)
        repository.save(member)

        it("find") {
            val findMember = repository.findById(member.memberId)
            findMember shouldBe member
        }
        it("update") {
            repository.update(member.memberId, 20000)
            val updatedMember = repository.findById(member.memberId)
            updatedMember.money shouldBe 20000
        }
        it("delete") {
            repository.delete(member.memberId)
            val exception = shouldThrowExactly<NoSuchElementException> {
                run {
                    val updatedMember = repository.findById(member.memberId)
                }
            }
            exception.printStackTrace()
            exception.message should startWith("member not found")
        }
    }
})
