package furrealvetclinicmanagement;

import java.math.BigDecimal;

public class ClinicService implements Identifiable {
    private int serviceId;
    private String serviceName;
    private String category;
    private BigDecimal price;

    public ClinicService(int serviceId, String serviceName, String category, BigDecimal price) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.category = category;
        this.price = price;
    }

    @Override
    public int getId() {
        return serviceId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
