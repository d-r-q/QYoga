package nsu.fit.qyoga.core.exercises.api.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("exercise_steps")
data class ExerciseStep(
    @Id
    val id: Long,
    val description: String,
    val imageId: Long,
    val exerciseId: Long
)
