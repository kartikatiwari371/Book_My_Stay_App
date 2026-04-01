import java.util.*;

// Reservation class
class Reservation {
    private String bookingId;
    private String customerName;
    private String roomType;
    private int nights;

    public Reservation(String bookingId, String customerName, String roomType, int nights) {
        this.bookingId = bookingId;
        this.customerName = customerName;
        this.roomType = roomType;
        this.nights = nights;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getRoomType() {
        return roomType;
    }

    public int getNights() {
        return nights;
    }

    @Override
    public String toString() {
        return "BookingID: " + bookingId +
                ", Name: " + customerName +
                ", Room: " + roomType +
                ", Nights: " + nights;
    }
}

// Booking History (Storage)
class BookingHistory {
    private List<Reservation> history = new ArrayList<>();

    // Add confirmed booking
    public void addReservation(Reservation reservation) {
        history.add(reservation);
    }

    // Get all bookings
    public List<Reservation> getAllReservations() {
        return new ArrayList<>(history); // return copy (no modification)
    }
}

// Reporting Service
class BookingReportService {

    // Display all bookings
    public void displayAllBookings(List<Reservation> reservations) {
        System.out.println("\n--- Booking History ---");
        for (Reservation r : reservations) {
            System.out.println(r);
        }
    }

    // Generate summary report
    public void generateSummary(List<Reservation> reservations) {
        System.out.println("\n--- Booking Summary Report ---");

        int totalBookings = reservations.size();
        int totalNights = 0;

        for (Reservation r : reservations) {
            totalNights += r.getNights();
        }

        System.out.println("Total Bookings: " + totalBookings);
        System.out.println("Total Nights Booked: " + totalNights);
    }
}

// Main Class
public class UseCase8BookingHistoryReport {
    public static void main(String[] args) {

        BookingHistory history = new BookingHistory();
        BookingReportService reportService = new BookingReportService();

        // Simulating confirmed bookings
        Reservation r1 = new Reservation("B101", "Alice", "Deluxe", 2);
        Reservation r2 = new Reservation("B102", "Bob", "Suite", 3);
        Reservation r3 = new Reservation("B103", "Charlie", "Standard", 1);

        // Add to booking history
        history.addReservation(r1);
        history.addReservation(r2);
        history.addReservation(r3);

        // Admin retrieves booking history
        List<Reservation> storedBookings = history.getAllReservations();

        // Display bookings
        reportService.displayAllBookings(storedBookings);

        // Generate report
        reportService.generateSummary(storedBookings);
    }
}