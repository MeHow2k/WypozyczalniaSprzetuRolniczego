package pl.mehow2k.models;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "machines")
public class Machine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // Np. "John Deere 6R"

    @Column(nullable = false)
    private String category; // Np. "Traktory", "Kombajny"

    @Column(nullable = false)
    private BigDecimal pricePerDay; // Cena za dobę wypożyczenia

    @Column(nullable = false)
    private boolean available = true; // Czy sprzęt jest doisterpny

    //konstruktor

    public Machine(String name, String category, BigDecimal pricePerDay, boolean available) {
        this.name = name;
        this.category = category;
        this.pricePerDay = pricePerDay;
        this.available = available;
    }
    public Machine() {
        this.name = "machine_name";
        this.category = "category_name";
        this.pricePerDay = new BigDecimal ("9999999999999999999999999999");
        this.available = false;
    }

    // Gettery i Settery
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public BigDecimal getPricePerDay() { return pricePerDay; }
    public void setPricePerDay(BigDecimal pricePerDay) { this.pricePerDay = pricePerDay; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}