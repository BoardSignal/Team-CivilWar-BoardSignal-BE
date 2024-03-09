package com.civilwar.boardsignal.image.domain;

import org.springframework.web.multipart.MultipartFile;

public interface ImageRepository {

    public String save(MultipartFile image);

}
