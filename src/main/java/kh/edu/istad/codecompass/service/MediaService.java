package kh.edu.istad.codecompass.service;

import kh.edu.istad.codecompass.dto.MediaResponse;
import org.springframework.web.multipart.MultipartFile;

public interface MediaService {

    MediaResponse upload(MultipartFile file);

}
