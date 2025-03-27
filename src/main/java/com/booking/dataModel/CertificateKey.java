package com.booking.dataModel;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;

@Entity
public class CertificateKey {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "certificate_id_seq")
    @SequenceGenerator(name = "certificate_id_seq", sequenceName = "certificate_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "service_number", nullable = false)
    private String serviceNumber;

    @Column(nullable = false)
    private byte[] certificatePem;

    @Column(nullable = false)
    private byte[] privateKey;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant expireDate;

    public CertificateKey() {
    }

    public CertificateKey(String serviceNumber, byte[] certificatePem, byte[] privateKey, Instant createdAt, Instant expireDate) {
        this.serviceNumber = serviceNumber;
        this.certificatePem = certificatePem;
        this.privateKey = privateKey;
        this.createdAt = createdAt;
        this.expireDate = expireDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CertificateKey that = (CertificateKey) o;

        if (!Objects.equals(serviceNumber, that.serviceNumber)) return false;
        if (!Arrays.equals(certificatePem, that.certificatePem)) return false;
        if (!Arrays.equals(privateKey, that.privateKey)) return false;
        if (!Objects.equals(createdAt, that.createdAt)) return false;
        return Objects.equals(expireDate, that.expireDate);
    }

    @Override
    public int hashCode() {
        int result = serviceNumber != null ? serviceNumber.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(certificatePem);
        result = 31 * result + Arrays.hashCode(privateKey);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (expireDate != null ? expireDate.hashCode() : 0);
        return result;
    }

    public String getServiceNumber() {
        return serviceNumber;
    }

    public void setServiceNumber(String serviceNumber) {
        this.serviceNumber = serviceNumber;
    }

    public byte[] getCertificatePem() {
        return certificatePem;
    }

    public void setCertificatePem(byte[] certificatePem) {
        this.certificatePem = certificatePem;
    }

    public byte[] getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(byte[] privateKey) {
        this.privateKey = privateKey;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Instant expireDate) {
        this.expireDate = expireDate;
    }
}

