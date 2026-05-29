package pl.mehow2k.transfers;

import pl.mehow2k.models.ReservationStatus;

public class ReservationStatusRequest {

    private ReservationStatus status; // Korzysta z naszego Enuma (PENDING, APPROVED itd.)

    // Getter i setter
    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }
}
