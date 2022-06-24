package hello.jdbc.repository

import com.zaxxer.hikari.HikariDataSource
import hello.jdbc.connection.ConnectionConst.Companion.PASSWORD
import hello.jdbc.connection.ConnectionConst.Companion.URL
import hello.jdbc.connection.ConnectionConst.Companion.USERNAME
import hello.jdbc.domain.Member
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.startWith
import mu.KotlinLogging

internal class MemberRepositoryV1Test : DescribeSpec({

    val log = KotlinLogging.logger {}
    lateinit var repository: MemberRepositoryV1

    beforeContainer {
        log.info { "beforeContainer" }
        // 항상 새로운 Connection 획득
        //val dataSource = DriverManagerDataSource(URL, USERNAME, PASSWORD)
        val dataSource = HikariDataSource().apply {
            jdbcUrl = URL
            username = USERNAME
            password = PASSWORD
        }
        repository = MemberRepositoryV1(dataSource)
    }

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
