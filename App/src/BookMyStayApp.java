import java.util.*;

public class BookMyStayApp {

    // ---------------- CUSTOM EXCEPTION ----------------
    static class InvalidBookingException extends Exception {
        public InvalidBookingException(String message) {
            super(message);
        }
    }

    // ---------------- ROOM ----------------
    static abstract class Room {
        String type;

        public Room(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }

    static class SingleRoom extends Room {
        public SingleRoom() { super("Single Room"); }
    }

    static class DoubleRoom extends Room {
        public DoubleRoom() { super("Double Room"); }
    }

    static class SuiteRoom extends Room {
        public SuiteRoom() { super("Suite Room"); }
    }

    // ---------------- INVENTORY ----------------
    static class RoomInventory {

        private Map<String, Integer> inventory = new HashMap<>();

        public RoomInventory() {
            inventory.put("Single Room", 1);
            inventory.put("Double Room", 1);
            inventory.put("Suite Room", 0);
        }

        public int getAvailability(String type) {
            return inventory.getOrDefault(type, 0);
        }

        public void reduceAvailability(String type) throws InvalidBookingException {

            int count = inventory.getOrDefault(type, 0);

            if (count <= 0) {
                throw new InvalidBookingException("No rooms available for: " + type);
            }

            inventory.put(type, count - 1);
        }

        public boolean isValidRoomType(String type) {
            return inventory.containsKey(type);
        }

        public void display() {
            System.out.println("\nInventory:");
            inventory.forEach((k,v)-> System.out.println(k+" -> "+v));
        }
    }

    // ---------------- VALIDATOR ----------------
    static class BookingValidator {

        public static void validate(Reservation r, RoomInventory inventory)
                throws InvalidBookingException {

            // Null / empty check
            if (r.guestName == null || r.guestName.isEmpty()) {
                throw new InvalidBookingException("Guest name cannot be empty.");
            }

            // Room type validation
            if (!inventory.isValidRoomType(r.roomType)) {
                throw new InvalidBookingException("Invalid room type: " + r.roomType);
            }

            // Availability check
            if (inventory.getAvailability(r.roomType) <= 0) {
                throw new InvalidBookingException("Room not available: " + r.roomType);
            }
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

    // ---------------- QUEUE ----------------
    static class BookingRequestQueue {
        Queue<Reservation> queue = new LinkedList<>();

        public void add(Reservation r) {
            queue.offer(r);
        }

        public Reservation next() {
            return queue.poll();
        }

        public boolean isEmpty() {
            return queue.isEmpty();
        }
    }

    // ---------------- BOOKING SERVICE ----------------
    static class BookingService {

        private RoomInventory inventory;
        private Set<String> allocatedIds = new HashSet<>();
        private int counter = 100;

        public BookingService(RoomInventory inventory) {
            this.inventory = inventory;
        }

        private String generateId(String type) {
            counter++;
            return type.replace(" ", "").toUpperCase() + "-" + counter;
        }

        public void process(BookingRequestQueue queue) {

            System.out.println("\nProcessing Bookings...");
            System.out.println("----------------------------------");

            while (!queue.isEmpty()) {

                Reservation r = queue.next();

                try {
                    // ✅ VALIDATE FIRST (Fail-Fast)
                    BookingValidator.validate(r, inventory);

                    // Allocate room
                    String id = generateId(r.roomType);

                    if (!allocatedIds.contains(id)) {
                        allocatedIds.add(id);

                        // Update inventory safely
                        inventory.reduceAvailability(r.roomType);

                        System.out.println("CONFIRMED: " + r.guestName +
                                " | Room: " + r.roomType +
                                " | ID: " + id);
                    }

                } catch (InvalidBookingException e) {
                    // ✅ Graceful failure
                    System.out.println("ERROR for " + r.guestName + ": " + e.getMessage());
                }
            }
        }
    }

    // ---------------- MAIN ----------------
    public static void main(String[] args) {

        RoomInventory inventory = new RoomInventory();

        BookingRequestQueue queue = new BookingRequestQueue();

        // VALID + INVALID cases
        queue.add(new Reservation("Alice", "Single Room"));   // valid
        queue.add(new Reservation("", "Double Room"));        // invalid name
        queue.add(new Reservation("Bob", "Luxury Room"));     // invalid type
        queue.add(new Reservation("Charlie", "Suite Room"));  // unavailable

        BookingService service = new BookingService(inventory);

        service.process(queue);

        // System still running fine
        inventory.display();
    }
}