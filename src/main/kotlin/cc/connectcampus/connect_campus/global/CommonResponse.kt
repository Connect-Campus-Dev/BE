package cc.connectcampus.connect_campus.global

data class CommonResponse(
    val status: Int = 200,
    val code: String = "S000",
    val message: String = "success",
)