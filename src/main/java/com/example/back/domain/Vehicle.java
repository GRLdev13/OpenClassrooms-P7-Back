package com.example.back.domain;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "vehicles")
public class Vehicle extends AbstractAuditedEntity {

    @Column(name = "base_price_per_day", precision = 12, scale = 2)
    private BigDecimal basePricePerDay;

    @Column(name = "base_price_per_hour", precision = 12, scale = 2)
    private BigDecimal basePricePerHour;

    @Column(name = "code_acriss")
    private String codeAcriss;

    @Column(name = "code_vin")
    private String codeVin;

    @Column(name = "license_plate")
    private String licensePlate;

    public BigDecimal getBasePricePerDay() {
        return basePricePerDay;
    }

    public void setBasePricePerDay(BigDecimal basePricePerDay) {
        this.basePricePerDay = basePricePerDay;
    }

    public BigDecimal getBasePricePerHour() {
        return basePricePerHour;
    }

    public void setBasePricePerHour(BigDecimal basePricePerHour) {
        this.basePricePerHour = basePricePerHour;
    }

    public String getCodeAcriss() {
        return codeAcriss;
    }

    public void setCodeAcriss(String codeAcriss) {
        this.codeAcriss = codeAcriss;
    }

    public String getCodeVin() {
        return codeVin;
    }

    public void setCodeVin(String codeVin) {
        this.codeVin = codeVin;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }
}
