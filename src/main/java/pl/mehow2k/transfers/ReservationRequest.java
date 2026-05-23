package pl.mehow2k.transfers;

import java.time.LocalDate;

public class ReservationRequest {
    private Long machineId;
    private LocalDate startDate;
    private LocalDate endDate;

    // Gettery i Settery
    public Long getMachineId() { return machineId; }
    public void setMachineId(Long machineId) { this.machineId = machineId; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
}
