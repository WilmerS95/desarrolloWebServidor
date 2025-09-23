package com.solutec.loan_application_server.service;

import com.google.cloud.storage.*;
import com.google.firebase.cloud.StorageClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class FirebaseStorageService {
    public String uploadFile(MultipartFile file, String folderName) throws IOException {
        Bucket bucket = StorageClient.getInstance().bucket();

        String fileName = folderName + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        Blob blob = bucket.create(fileName, file.getBytes(), file.getContentType());

        blob.createAcl(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));

        return String.format("https://storage.googleapis.com/%s/%s", bucket.getName(), fileName);
    }

    public List<String> uploadMultipleFiles(List<MultipartFile> files, String folderName) throws IOException {
        List<String> urls = new ArrayList<>();
        if (files == null) return urls;
        for (MultipartFile file : files) {
            urls.add(uploadFile(file, folderName));
        }
        return urls;
    }
}
