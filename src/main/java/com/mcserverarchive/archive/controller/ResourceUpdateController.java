package com.mcserverarchive.archive.controller;


import com.google.gson.Gson;
import com.mcserverarchive.archive.config.exception.RestException;
import com.mcserverarchive.archive.dtos.in.CreateUpdateRequest;
import com.mcserverarchive.archive.service.ResourceUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class ResourceUpdateController {
    private final ResourceUpdateService resourceUpdateService;

    @PostMapping("/upload")
    public void createUpdate(@CookieValue(name = "user-cookie") String token, @RequestParam("file") MultipartFile file, @RequestPart("data") String data) throws RestException {
//        Path resourcePath = Path.of("C:\\Users\\prest\\Downloads\\txt.txt");
//        try (InputStream inputStream = file.getInputStream()) {
//            Files.copy(inputStream, resourcePath);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        this.resourceUpdateService.createUpdate(file, new Gson().fromJson(data, CreateUpdateRequest.class));
    }

    @Bean
    public WebMvcConfigurer corsConfigurerFile() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/file/upload/**")
                        .allowedOrigins("http://localhost:3000")
                        .allowedOriginPatterns("http://localhost:3000")
                        .allowCredentials(true)
                        .allowedMethods("GET", "POST");
            }
        };
    }

    @GetMapping(value = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<java.io.File> getFile(@PathVariable int updateId) throws RestException {
        ResourceUpdateService.FileReturn fileReturn = this.resourceUpdateService.getDownload(updateId);
        return ResponseEntity.ok()
                .headers(httpHeaders -> httpHeaders.setContentDisposition(
                        ContentDisposition.attachment()
                                .filename(fileReturn.realName())
                                .build()
                ))
                .body(fileReturn.file());
    }
}
