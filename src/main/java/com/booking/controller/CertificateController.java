package com.booking.controller;

import com.booking.service.CertificationKeyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/certificates")
public class CertificateController {

    private final CertificationKeyService certificationKeyService;

    public CertificateController(CertificationKeyService certificationKeyService) {
        this.certificationKeyService = certificationKeyService;
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getCertificate(@PathVariable Long id) {
        certificationKeyService.getCertificate(id);
        return ResponseEntity.status(200).build();
    }

    @PostMapping("/load-certificate-key")
    public ResponseEntity<?> loadCertificate(@RequestParam("serviceNumber") String serviceNumber,
                                             @RequestParam("certificate") MultipartFile certificateFile,
                                             @RequestParam("privateKey") MultipartFile privateKeyFile) {
        try {
            certificationKeyService.loadCertificate(serviceNumber, certificateFile, privateKeyFile);
            return ResponseEntity.status(200).build();
        } catch (Exception ex) {
            return ResponseEntity.status(400).body("Certificate conversion errors!");
        }
    }

    @PostMapping(value = "/upload-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadCertificate(
            @RequestParam("serviceNumber") String serviceNumber,
            @RequestParam("certificate") MultipartFile certificateFile
//            @RequestParam("privateKey") MultipartFile privateKeyFile
    ) {
        try {
            byte[] certificatePem = certificateFile.getBytes();
//            byte[] privateKeyPem = privateKeyFile.getBytes();

            System.out.println("Received certificate for service: " + serviceNumber);

            return ResponseEntity.ok("Certificate and key uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File processing error");
        }
    }
}