package com.civilwar.boardsignal.image.infrastructure;

import com.civilwar.boardsignal.image.domain.ImageRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class S3ImageRepository implements ImageRepository {

    @Override
    public String save(MultipartFile image) {
        return "TEMP_URL";
    }
}
