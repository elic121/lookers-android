package com.example.lookers.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import kotlin.math.sqrt

class ImageProcessor(
    private val context: Context,
) {
    /**
     * 이미지를 리사이징 및 압축하여 최대 파일 크기 이하로 줄임.
     *
     * @param file 원본 파일
     * @param maxFileSize 최대 파일 크기(MB 단위)
     * @return 압축된 파일
     */
    fun resizeAndCompressImage(
        file: File,
        maxFileSize: Long,
    ): File {
        val maxFileSizeInBytes = maxFileSize * 1024 * 1024

        // Bitmap 생성
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)

        // 기본 크기 설정
        var width = bitmap.width
        var height = bitmap.height

        // 크기 조정 비율
        val aspectRatio = width.toDouble() / height.toDouble()
        if (file.length() > maxFileSizeInBytes) {
            width = sqrt(((maxFileSizeInBytes * aspectRatio).toInt() / 2).toDouble()).toInt()
            height = (width / aspectRatio).toInt()
        }

        // 비트맵 리사이징
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)

        // 압축된 파일 생성
        val compressedFile = File(file.parent, "compressed_${file.name}")
        FileOutputStream(compressedFile).use { outputStream ->
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        }

        return if (compressedFile.length() > maxFileSizeInBytes) {
            resizeAndCompressImage(compressedFile, maxFileSize) // 재귀적으로 추가 압축
        } else {
            compressedFile
        }
    }

    /**
     * MultipartBody.Part 객체를 생성.
     *
     * @param file 파일 객체
     * @param partName 서버에서 받을 파트 이름
     * @return MultipartBody.Part 객체
     */
    fun createMultipartBodyPart(
        file: File,
        partName: String,
    ): MultipartBody.Part {
        val requestFile: RequestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

    /**
     * Uri에서 임시 파일 생성
     *
     * @param uri 이미지 URI
     * @return 임시 파일
     */
    fun createTempFileFromUri(uri: android.net.Uri): File {
        val inputStream =
            context.contentResolver.openInputStream(uri)
                ?: throw IllegalStateException("Failed to open input stream")

        val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)

        FileOutputStream(tempFile).use { outputStream ->
            inputStream.use { input ->
                input.copyTo(outputStream)
            }
        }

        return tempFile
    }
}
