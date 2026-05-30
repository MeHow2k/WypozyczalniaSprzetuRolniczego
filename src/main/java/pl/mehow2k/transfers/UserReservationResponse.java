package pl.mehow2k.transfers;

import pl.mehow2k.models.ReservationStatus;

import java.time.LocalDate;

public class UserReservationResponse {
    private Long id;
    private String machineName;
    private String machineCategory;
    private LocalDate startDate;
    private LocalDate endDate;
    private ReservationStatus status;

    // Konstruktor do szybkiego mapowania
    public UserReservationResponse(Long id, String machineName, String machineCategory,
                                   LocalDate startDate, LocalDate endDate, ReservationStatus status) {
        this.id = id;
        this.machineName = machineName;
        this.machineCategory = machineCategory;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    // Gettery i Settery
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMachineName() { return machineName; }
    public void setMachineName(String machineName) { this.machineName = machineName; }
    public String getMachineCategory() { return machineCategory; }
    public void setMachineCategory(String machineCategory) { this.machineCategory = machineCategory; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }
}
