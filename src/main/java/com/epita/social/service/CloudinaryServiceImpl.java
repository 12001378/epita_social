package com.epita.social.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    @Override
    public String upload(MultipartFile file, String type) throws Exception {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("resource_type", type));
        return (String) uploadResult.get("secure_url");
    }

    @Override
    public String imageUpload(MultipartFile file) throws Exception {
        return upload(file, "image");
    }

    @Override
    public String videoUpload(MultipartFile file) throws Exception {
        return upload(file, "video");
    }

    @Override
    public String upload_media_url(MultipartFile file) throws Exception {

            try {
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                return (String) uploadResult.get("secure_url");
            } catch (IOException e) {
                throw new RuntimeException("Cloudinary upload failed", e);
            }
    }
}
