package cc.connectcampus.connect_campus.domain.love.repository

import cc.connectcampus.connect_campus.domain.love.domain.QLoveProfile
import cc.connectcampus.connect_campus.domain.love.domain.QLoveProfileImage
import cc.connectcampus.connect_campus.domain.love.domain.QMemberHobby
import cc.connectcampus.connect_campus.domain.love.domain.QMemberPersonality
import cc.connectcampus.connect_campus.domain.love.dto.response.LoveProfileResponse
import cc.connectcampus.connect_campus.domain.member.domain.QMember
import com.querydsl.core.types.Projections
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Repository
class LoveProfileQuerydslRepository(
    private val queryFactory: JPAQueryFactory,
) {

//    @Transactional(readOnly = true)
//    fun findLoveProfileByMemberId(memberId: UUID): LoveProfileResponse? {
//        val member = QMember.member
//        val loveProfile = QLoveProfile.loveProfile
//        val loveProfileImage = QLoveProfileImage.loveProfileImage
//        val memberPersonality = QMemberPersonality.memberPersonality
//        val memberHobby = QMemberHobby.memberHobby
//
//        return queryFactory.select(
//            Projections.constructor(
//                LoveProfileResponse::class.java,
//                loveProfile.nickname,
//                loveProfile.residence,
//                loveProfile.mbti,
//                loveProfile.height,
//                loveProfile.introduction,
//                JPAExpressions.selectFrom(loveProfileImage)
//                    .where(loveProfileImage.loveProfile.eq(loveProfile))
//                    .fetch(),
//                JPAExpressions.selectFrom(memberPersonality)
//                    .where(memberPersonality.loveProfile.eq(loveProfile))
//                    .fetch(),
//                JPAExpressions.selectFrom(memberHobby)
//                    .where(memberHobby.loveProfile.eq(loveProfile))
//                    .fetch()
//            )
//        )
//            .from(loveProfile)
//            .innerJoin(loveProfile.member, member)
//            .where(loveProfile.member.id.eq(memberId))
//            .fetchFirst()
//    }
}