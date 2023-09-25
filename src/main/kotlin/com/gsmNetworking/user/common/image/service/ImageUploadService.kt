package com.gsmNetworking.user.common.image.service

import com.gsmNetworking.user.global.exception.ExpectedException
import io.awspring.cloud.s3.ObjectMetadata
import io.awspring.cloud.s3.S3Exception
import io.awspring.cloud.s3.S3Template
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
import java.util.UUID

@Service
class ImageUploadService(
    @Value("\${spring.cloud.aws.s3.bucket-name}")
    private val bucketName: String,
    private val s3Template: S3Template
) {

    fun execute(multipartFile: MultipartFile): String =
        try {
            val originFileName = multipartFile.originalFilename
            val extension = StringUtils.getFilenameExtension(originFileName)
            val fileName = createFileName(extension.toString())
            val s3Resource = s3Template.upload(bucketName, fileName, multipartFile.inputStream, ObjectMetadata.builder().contentType(extension).build())
            s3Resource.url.toString()
        } catch (e: S3Exception) {
            throw ExpectedException(HttpStatus.INTERNAL_SERVER_ERROR, "AWS S3에서 오류 발생")
        }

    private fun createFileName(fileExtension: String): String {
        return UUID.randomUUID().toString() + LocalDateTime.now() + "." + fileExtension
    }

}