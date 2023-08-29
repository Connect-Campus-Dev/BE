package cc.connectcampus.connect_campus.global.config.security

data class TokenInfo(
    val grantType: String,
    val accessToken: String,
)