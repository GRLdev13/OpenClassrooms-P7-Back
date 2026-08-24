package com.example.back.domain;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "rentals")
public class Rental extends AbstractAuditedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vehicle")
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_client")
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agency")
    private Agency agency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agency_start")
    private Agency startAgency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agency_end")
    private Agency endAgency;

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;

    @Column(name = "price", precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "deal", precision = 12, scale = 2)
    private BigDecimal deal;

    @Column(name = "status")
    private String status;

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Agency getAgency() {
        return agency;
    }

    public void setAgency(Agency agency) {
        this.agency = agency;
    }

    public Agency getStartAgency() {
        return startAgency;
    }

    public void setStartAgency(Agency startAgency) {
        this.startAgency = startAgency;
    }

    public Agency getEndAgency() {
        return endAgency;
    }

    public void setEndAgency(Agency endAgency) {
        this.endAgency = endAgency;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getDeal() {
        return deal;
    }

    public void setDeal(BigDecimal deal) {
        this.deal = deal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
