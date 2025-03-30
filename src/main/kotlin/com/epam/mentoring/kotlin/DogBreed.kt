package com.epam.mentoring.kotlin

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table
data class DogBreed(
    @Id
    var id: Long? = null,
    var breed: String,
    var subBreed: String? = null,
    var image: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DogBreed

        if (id != other.id) return false
        if (breed != other.breed) return false
        if (subBreed != other.subBreed) return false
        if (image != null) {
            if (other.image == null) return false
            if (!image.contentEquals(other.image)) return false
        } else if (other.image != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + breed.hashCode()
        result = 31 * result + (subBreed?.hashCode() ?: 0)
        result = 31 * result + (image?.contentHashCode() ?: 0)
        return result
    }
} 