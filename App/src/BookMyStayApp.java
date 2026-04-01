import java.util.*;
import java.util.concurrent.*;

public class BookMyStayApp {

    // ---------------- INVENTORY ----------------
    static class RoomInventory {
        private Map<String, Integer> inventory = new HashMap<>();

        public RoomInventory() {
            inventory.put("Single Room", 5);
            inventory.put("Double Room", 3);
            inventory.put("Suite Room", 2);
        }

        // Thread-safe access using synchronized
        public synchronized boolean allocate(String roomType) {
            int count = inventory.getOrDefault(roomType, 0);
            if (count > 0) {
                inventory.put(roomType, count - 1);
                return true;
            }
            return false;
        }

        public synchronized void release(String roomType) {
            inventory.put(roomType, inventory.getOrDefault(roomType, 0) + 1);
        }

        public synchronized void display() {
            System.out.println("\nInventory:");
            inventory.forEach((k, v) -> System.out.println(k + " -> " + v));
        }
    }

    // ---------------- RESERVATION ----------------
    static class Reservation {
        String guestName;
        String roomType;

        public Reservation(String guestName, String roomType) {
            this.guestName = guestName;
            this.roomType = roomType;
        }
    }

    // ---------------- BOOKING SERVICE ----------------
    static class BookingService {
        private RoomInventory inventory;
        private Map<String, String> confirmedBookings = new HashMap<>();
        private int counter = 100;

        public BookingService(RoomInventory inventory) {
            this.inventory = inventory;
        }

        public Map<String, String> getConfirmedBookings() {
            return confirmedBookings;
        }

        // Thread-safe booking
        public void bookRoom(Reservation r) {
            synchronized (this) {
                if (r.guestName == null || r.guestName.isEmpty()) {
                    System.out.println("ERROR: Invalid guest name");
                    return;
                }

                if (inventory.allocate(r.roomType)) {
                    String reservationId = generateId(r.roomType);
                    confirmedBookings.put(reservationId, r.roomType);
                    System.out.println("CONFIRMED: " + r.guestName + " -> " + reservationId);
                } else {
                    System.out.println("ERROR: Room unavailable for " + r.guestName + " (" + r.roomType + ")");
                }
            }
        }

        private String generateId(String roomType) {
            counter++;
            return roomType.replace(" ", "").toUpperCase() + "-" + counter;
        }
    }

    // ---------------- MAIN ----------------
    public static void main(String[] args) throws InterruptedException {

        RoomInventory inventory = new RoomInventory();
        BookingService bookingService = new BookingService(inventory);

        // Simulate multiple guests
        List<Reservation> reservations = Arrays.asList(
                new Reservation("Alice", "Single Room"),
                new Reservation("Bob", "Double Room"),
                new Reservation("Charlie", "Single Room"),
                new Reservation("Diana", "Suite Room"),
                new Reservation("Eve", "Single Room"),
                new Reservation("Frank", "Double Room"),
                new Reservation("Grace", "Suite Room")
        );

        // ExecutorService for concurrency
        ExecutorService executor = Executors.newFixedThreadPool(4);

        for (Reservation r : reservations) {
            executor.submit(() -> bookingService.bookRoom(r));
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        // Show final state
        inventory.display();
        System.out.println("\nConfirmed Bookings: " + bookingService.getConfirmedBookings());
    }
}