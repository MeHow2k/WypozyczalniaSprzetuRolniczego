package pl.mehow2k.models;

public enum ReservationStatus {
    PENDING, //oczekuje na potwierdzenie
    APPROVED,//potwierdzono rezerwacje
    REJECTED,//odmówiono rezerwacji
    LENT, // jest na wypożyczeniu - wydano sprzet
    COMPLETED//zakończono rezerwacje
}
