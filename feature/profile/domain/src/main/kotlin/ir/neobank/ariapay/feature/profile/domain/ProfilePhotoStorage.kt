package ir.neobank.ariapay.feature.profile.domain

/** Copies a selected image into app-owned storage and manages those copied files. */
interface ProfilePhotoStorage {
    suspend fun copyFromUri(uri: String): String

    suspend fun delete(path: String)

    suspend fun deleteAll()
}
