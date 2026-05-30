package pl.mehow2k.transfers;

public class TransportResponse {
    private double distanceKm;
    private double totalCostPln;

    public TransportResponse(double distanceKm, double totalCostPln) {
        this.distanceKm = distanceKm;
        this.totalCostPln = totalCostPln;
    }

    // Gettery i settery
    public double getDistanceKm() { return distanceKm; }
    public double getTotalCostPln() { return totalCostPln; }
}
