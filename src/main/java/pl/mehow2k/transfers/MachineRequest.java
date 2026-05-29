package pl.mehow2k.transfers;

import java.math.BigDecimal;

public class MachineRequest {

    private String name;
    private String category;
    private BigDecimal pricePerDay;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public BigDecimal getPricePerDay() { return pricePerDay; }
    public void setPricePerDay(BigDecimal pricePerDay) { this.pricePerDay = pricePerDay;}
}
