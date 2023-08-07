package cc.connectcampus.connect_campus.domain.model

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty

@Embeddable
class Email(
    @Email
    @Column(name = "email")
    @NotEmpty
    val value: String,
) {
    fun getDomain(): String {
        return value.split("@")[1]
    }
    fun getId(): String {
        return value.split("@")[0]
    }

    fun isNotUnivEmail(): Boolean {
        val regex = """^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.(ac\.kr|edu)$""".toRegex()
        return !regex.matches(this.value)
    }
}