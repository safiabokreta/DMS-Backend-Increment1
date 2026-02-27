package com.lab.documents.controller;

import com.lab.documents.entity.Document;
import com.lab.documents.repository.DocumentRepository;
import com.lab.documents.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private S3Service s3Service;

    // GET all documents
    @GetMapping("/list")
    public List<Document> listDocuments() {
        return documentRepository.findAll();
    }

    // GET single document
    @GetMapping("/get/{id}")
    public ResponseEntity<Document> getDocument(@PathVariable Long id) {
        return documentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ADD document — now accepts optional file
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Document> addDocument(
            @RequestParam("title") String title,
            @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {

        Document document = new Document();
        document.setTitle(title);

        // If a file is provided, upload to S3 and store the key
        if (file != null && !file.isEmpty()) {
            String key = "documents/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
            s3Service.uploadFile(key, file.getBytes(), file.getContentType());
            document.setFileKey(key);
        }

        Document saved = documentRepository.save(document);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // DOWNLOAD file attached to a document
    @GetMapping("/{id}/file")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {
        Document doc = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found: " + id));

        if (doc.getFileKey() == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] bytes = s3Service.downloadFile(doc.getFileKey());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/pdf")  // ← changed
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=document_" + id + ".pdf")  // ← inline
                .body(bytes);
    }
}