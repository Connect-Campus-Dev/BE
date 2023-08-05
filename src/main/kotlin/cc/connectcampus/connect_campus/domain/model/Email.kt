package cc.connectcampus.connect_campus.domain.model

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import org.springframework.stereotype.Component

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
}