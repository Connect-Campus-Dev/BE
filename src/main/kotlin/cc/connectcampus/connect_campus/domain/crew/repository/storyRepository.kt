package cc.connectcampus.connect_campus.domain.crew.repository

import cc.connectcampus.connect_campus.domain.crew.domain.Story
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface storyRepository: JpaRepository<Story,Long> {
}