package cc.connectcampus.connect_campus.domain.member.service

import cc.connectcampus.connect_campus.domain.member.domain.EmailVerification
import cc.connectcampus.connect_campus.domain.member.dto.request.CodeVerificationRequest
import cc.connectcampus.connect_campus.domain.member.dto.request.EmailVerificationRequest

interface EmailVerificationService {
    fun sendVerificationEmail(emailVerificationRequest: EmailVerificationRequest): EmailVerification
    fun verifyCode(codeVerificationRequest: CodeVerificationRequest): Boolean
}