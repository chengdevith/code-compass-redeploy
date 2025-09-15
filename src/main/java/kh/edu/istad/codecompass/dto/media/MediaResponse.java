package kh.edu.istad.codecompass.dto.media;

import lombok.Builder;

@Builder
public record MediaResponse(

        String name,
        String extension,
        String mimeTypeFile,
        String uri,
        Long size

) {
}
