package com.civilwar.boardsignal.image.infrastructure;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.civilwar.boardsignal.image.domain.ImageRepository;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3ImageRepository implements ImageRepository {

    private final AmazonS3 amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Override
    public String save(MultipartFile image) {
        if (image.getSize() == 0 || image.isEmpty()) {
            return null;
        }

        String originalFilename = image.getOriginalFilename();
        String convertName = "upload/" + originalFilename;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(image.getSize());
        metadata.setContentType(image.getContentType());

        try {
            amazonS3Client.putObject(bucket, convertName, image.getInputStream(), metadata);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return amazonS3Client.getUrl(bucket, convertName).toString();
    }
}
