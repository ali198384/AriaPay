package ir.neobank.ariapay.feature.profile.data.storage

import android.content.Context
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.neobank.ariapay.feature.profile.domain.ProfilePhotoStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

internal class InternalProfilePhotoStorage(
    @ApplicationContext private val context: Context
) : ProfilePhotoStorage {

    override suspend fun copyFromUri(uri: String): String = withContext(Dispatchers.IO) {
        val source = uri.toUri()
        val mimeType = context.contentResolver.getType(source)
            ?.takeIf { it.startsWith("image/", ignoreCase = true) }
            ?: throw IllegalArgumentException("انتخاب‌گر، تصویر معتبری برنگرداند.")
        val directory = photoDirectory().apply {
            if (!exists() && !mkdirs()) throw IOException("ساخت پوشهٔ تصویر ممکن نیست.")
        }
        val sourceStream = context.contentResolver.openInputStream(source)
            ?: throw IOException("خواندن تصویر انتخاب‌شده ممکن نیست.")
        val extension = mimeType.substringAfter('/', "image").lowercase()
            .replace(Regex("[^a-z0-9]"), "")
            .take(8)
            .ifBlank { "image" }
        val id = UUID.randomUUID().toString()
        val temporaryFile = File(directory, "$id.tmp")
        val finalFile = File(directory, "$id.$extension")

        try {
            sourceStream.use { input ->
                FileOutputStream(temporaryFile).use { output ->
                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                    var totalBytes = 0L
                    while (true) {
                        val read = input.read(buffer)
                        if (read < 0) break
                        totalBytes += read
                        require(totalBytes <= MAX_IMAGE_BYTES) {
                            "حجم تصویر بیشتر از حد مجاز است."
                        }
                        output.write(buffer, 0, read)
                    }
                    output.fd.sync()
                }
            }
            if (!temporaryFile.renameTo(finalFile)) {
                throw IOException("ذخیرهٔ نهایی تصویر انجام نشد.")
            }
            finalFile.absolutePath
        } catch (error: Exception) {
            temporaryFile.delete()
            finalFile.delete()
            throw error
        }
    }

    override suspend fun delete(path: String) = withContext(Dispatchers.IO) {
        val target = File(path).canonicalFile
        val directory = photoDirectory().canonicalFile
        require(target.parentFile == directory) { "مسیر تصویر خارج از پوشهٔ پروفایل است." }
        if (target.exists() && !target.delete()) {
            throw IOException("پاک‌کردن تصویر پروفایل انجام نشد.")
        }
    }

    override suspend fun deleteAll() = withContext(Dispatchers.IO) {
        val directory = photoDirectory()
        if (!directory.exists()) return@withContext
        val files = directory.listFiles() ?: throw IOException("خواندن پوشهٔ تصاویر ممکن نیست.")
        files.forEach { file ->
            if (!file.isFile || !file.delete()) {
                throw IOException("پاک‌کردن فایل تصویر پروفایل انجام نشد.")
            }
        }
        if (!directory.delete()) throw IOException("پاک‌کردن پوشهٔ تصاویر انجام نشد.")
    }

    private fun photoDirectory(): File = File(context.filesDir, PHOTO_DIRECTORY)

    private companion object {
        const val PHOTO_DIRECTORY = "profile-photos"
        const val MAX_IMAGE_BYTES = 15L * 1024L * 1024L
    }
}
