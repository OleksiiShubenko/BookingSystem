package com.booking.service;

import com.booking.dataModel.CertificateKey;
import com.booking.repository.CertificateKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

@Service
public class CertificationKeyService {

    private final CertificateKeyRepository certificateKeyRepository;

    @Autowired
    public CertificationKeyService(CertificateKeyRepository certificateKeyRepository) {
        this.certificateKeyRepository = certificateKeyRepository;
    }

    public void loadCertificate(String serviceNumber, MultipartFile certificate, MultipartFile privateKey) throws CertificateException, IOException {
        byte[] certificatePem = Base64.getDecoder().decode(certificate.getBytes());
        byte[] privateKeyPem = Base64.getDecoder().decode(privateKey.getBytes());
        Instant expiryDate = getX509Certificate(certificatePem).getNotAfter().toInstant();
        CertificateKey certificateKey = new CertificateKey(
                serviceNumber,
                certificatePem,
                privateKeyPem,
                Instant.now(),
                expiryDate
        );
        certificateKeyRepository.save(certificateKey);
    }

    private X509Certificate getX509Certificate(byte[] certificatePem) throws CertificateException {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(certificatePem);
        return (X509Certificate) certificateFactory.generateCertificate(byteArrayInputStream);
    }

    public void getCertificate(Long id) {
        Optional<CertificateKey> certificateKey = certificateKeyRepository.findById(id);

        //todo
    }
}
