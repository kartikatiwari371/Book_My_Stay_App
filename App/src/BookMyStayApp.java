import java.io.*;
import java.util.*;
import java.util.concurrent.*;

// Main application class
public class BookMyStayApp implements Serializable {

    private static final String DATA_FILE = "bookings.ser";

    // ---------------- INVENTORY ----------------
    static class RoomInventory implements Serializable {
        private Map<String, Integer> inventory = new HashMap<>();

        public RoomInventory() {
            inventory.put("Single Room", 5);
            inventory.put("Double Room", 3);
            inventory.put("Suite Room", 2);
        }

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

        public Map<String, Integer> getInventory() {
            return inventory;
        }

        public void setInventory(Map<String, Integer> inv) {
            this.inventory = inv;
        }
    }

    // ---------------- RESERVATION ----------------
    static class Reservation implements Serializable {
        String guestName;
        String roomType;

        public Reservation(String guestName, String roomType) {
            this.guestName = guestName;
            this.roomType = roomType;
        }
    }

    // ---------------- BOOKING SERVICE ----------------
    static class BookingService implements Serializable {
        private RoomInventory inventory;
        private Map<String, String> confirmedBookings = new HashMap<>();
        private int counter = 100;

        public BookingService(RoomInventory inventory) {
            this.inventory = inventory;
        }

        public Map<String, String> getConfirmedBookings() {
            return confirmedBookings;
        }

        public synchronized void bookRoom(Reservation r) {
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

        private String generateId(String roomType) {
            counter++;
            return roomType.replace(" ", "").toUpperCase() + "-" + counter;
        }
    }

    // ---------------- PERSISTENCE ----------------
    public static void saveState(RoomInventory inventory, BookingService service) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(inventory.getInventory());
            oos.writeObject(service.getConfirmedBookings());
            System.out.println("\nSystem state saved to " + DATA_FILE);
        } catch (IOException e) {
            System.out.println("ERROR: Could not save system state.");
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static void restoreState(RoomInventory inventory, BookingService service) {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            System.out.println("No previous state found. Starting fresh.");
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            Map<String, Integer> inv = (Map<String, Integer>) ois.readObject();
            Map<String, String> bookings = (Map<String, String>) ois.readObject();
            inventory.setInventory(inv);
            service.getConfirmedBookings().putAll(bookings);
            System.out.println("System state restored from " + DATA_FILE);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("ERROR: Could not restore system state. Starting fresh.");
        }
    }

    // ---------------- MAIN ----------------
    public static void main(String[] args) throws InterruptedException {
        RoomInventory inventory = new RoomInventory();
        BookingService bookingService = new BookingService(inventory);

        // Restore previous state if available
        restoreState(inventory, bookingService);

        // Simulate booking requests
        List<Reservation> reservations = Arrays.asList(
                new Reservation("Alice", "Single Room"),
                new Reservation("Bob", "Double Room"),
                new Reservation("Charlie", "Single Room"),
                new Reservation("Diana", "Suite Room")
        );

        ExecutorService executor = Executors.newFixedThreadPool(2);
        for (Reservation r : reservations) {
            executor.submit(() -> bookingService.bookRoom(r));
        }
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        // Display final inventory and bookings
        inventory.display();
        System.out.println("\nConfirmed Bookings: " + bookingService.getConfirmedBookings());

        // Save state before exit
        saveState(inventory, bookingService);
    }
}