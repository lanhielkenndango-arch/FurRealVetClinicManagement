package furrealvetclinicmanagement;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Visit implements Identifiable {
    private int visitId;
    private int clientId;
    private int petId;
    private LocalDate visitDate;
    private String status;
    private BigDecimal total;

    public Visit(int visitId, int clientId, int petId, LocalDate visitDate, String status, BigDecimal total) {
        this.visitId = visitId;
        this.clientId = clientId;
        this.petId = petId;
        this.visitDate = visitDate;
        this.status = status;
        this.total = total;
    }

    @Override
    public int getId() {
        return visitId;
    }

    public int getVisitId() {
        return visitId;
    }

    public int getClientId() {
        return clientId;
    }

    public int getPetId() {
        return petId;
    }

    public LocalDate getVisitDate() {
        return visitDate;
    }

    public String getStatus() {
        return status;
    }

    public BigDecimal getTotal() {
        return total;
    }
}
