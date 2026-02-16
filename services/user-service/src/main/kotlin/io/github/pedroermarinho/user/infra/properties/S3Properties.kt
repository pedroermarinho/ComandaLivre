package io.github.pedroermarinho.user.infra.properties

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
data class S3Properties(
    @Value("\${aws.s3.endpoint}") val endpoint: String,
    @Value("\${aws.s3.access-key}") val accessKey: String,
    @Value("\${aws.s3.secret-key}") val secretKey: String,
    @Value("\${aws.s3.bucket}") val bucket: String,
    @Value("\${aws.s3.region}") val region: String,
)
