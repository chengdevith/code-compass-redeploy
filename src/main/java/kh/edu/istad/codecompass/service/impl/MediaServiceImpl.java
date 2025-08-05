package kh.edu.istad.codecompass.service.impl;

import kh.edu.istad.codecompass.domain.Media;
import kh.edu.istad.codecompass.dto.MediaResponse;
import kh.edu.istad.codecompass.repository.MediaRepository;
import kh.edu.istad.codecompass.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

    final MediaRepository mediaRepository;

    @Value("${media.server-path}")
    private String serverPath;

    @Value("${media.base-uri}")
    private String baseUri;

    @Override
    public MediaResponse upload(MultipartFile file) {

        String name = UUID.randomUUID().toString();
        int lastIndex = Objects.requireNonNull(file.getOriginalFilename()).lastIndexOf(".");
        String extension = file.getOriginalFilename().substring(lastIndex + 1);
        Path path = Paths.get(serverPath + String.format("%s.%s", name, extension));

        try{
            Files.copy(file.getInputStream(), path);
        }catch (IOException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"File upload failed");
        }

        Media media = new Media();
        media.setName(name);
        media.setExtension(extension);
        media.setMineTypeFile(file.getContentType());
        media.setIsDeleted(false);

        mediaRepository.save(media);

        return MediaResponse.builder()
                .name(media.getName())
                .extension(media.getExtension())
                .mimeTypeFile(media.getMineTypeFile())
                .uri(baseUri + String.format("%s.%s", name, extension))
                .size(file.getSize())
                .build();
    }
}
