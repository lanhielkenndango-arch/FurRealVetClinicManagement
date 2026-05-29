package furrealvetclinicmanagement;

import java.math.BigDecimal;

public class VisitService implements Identifiable {
    private int visitServiceId;
    private int visitId;
    private int serviceId;
    private int quantity;
    private BigDecimal lineTotal;

    public VisitService(int visitServiceId, int visitId, int serviceId, int quantity, BigDecimal lineTotal) {
        this.visitServiceId = visitServiceId;
        this.visitId = visitId;
        this.serviceId = serviceId;
        this.quantity = quantity;
        this.lineTotal = lineTotal;
    }

    @Override
    public int getId() {
        return visitServiceId;
    }

    public int getVisitServiceId() {
        return visitServiceId;
    }

    public int getVisitId() {
        return visitId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }
}
