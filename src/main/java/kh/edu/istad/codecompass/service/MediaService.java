package kh.edu.istad.codecompass.service;

import kh.edu.istad.codecompass.dto.media.MediaResponse;
import org.springframework.web.multipart.MultipartFile;

public interface MediaService {

    MediaResponse upload(MultipartFile file);

    void deleteFile(String fileName);

}
