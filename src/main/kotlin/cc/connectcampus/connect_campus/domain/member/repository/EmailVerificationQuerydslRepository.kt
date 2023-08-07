package cc.connectcampus.connect_campus.domain.member.repository

import cc.connectcampus.connect_campus.domain.member.domain.QEmailVerification.emailVerification
import cc.connectcampus.connect_campus.domain.model.Email
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Component

@Component
class EmailVerificationQuerydslRepository(
    private val queryFactory: JPAQueryFactory,
) {
    fun notExists(email: Email): Boolean {
        return queryFactory.select(emailVerification)
            .from(emailVerification)
            .where(
                emailVerification.email.eq(email),
                emailVerification.isVerified.eq(true)
            )
            .fetchFirst() == null
    }
}