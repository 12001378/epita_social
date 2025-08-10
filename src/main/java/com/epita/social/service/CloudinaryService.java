package com.epita.social.service;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
    String upload(MultipartFile file, String type) throws Exception;
    String imageUpload(MultipartFile file) throws Exception;
    String videoUpload(MultipartFile file) throws Exception;
    String upload_media_url(MultipartFile file) throws Exception;
}
