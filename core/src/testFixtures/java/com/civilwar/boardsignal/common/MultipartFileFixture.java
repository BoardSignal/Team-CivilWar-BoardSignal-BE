package com.civilwar.boardsignal.common;

import static lombok.AccessLevel.PRIVATE;

import java.io.FileInputStream;
import java.io.IOException;
import lombok.NoArgsConstructor;
import org.springframework.mock.web.MockMultipartFile;

@NoArgsConstructor(access = PRIVATE)
public class MultipartFileFixture {

    public static MockMultipartFile getMultipartFile() throws IOException {
        String fileName = "testFile.png";

        return new MockMultipartFile(
            "image",
            fileName,
            "image/png",
            new FileInputStream("src/test/resources/" + fileName)
        );
    }

}
