import java.util.*;

public class BookMyStayApp {

    // ---------------- ROOM CLASSES ----------------
    static abstract class Room {

        private String type;
        private int beds;
        private int size;
        private double price;

        public Room(String type, int beds, int size, double price) {
            this.type = type;
            this.beds = beds;
            this.size = size;
            this.price = price;
        }

        public String getType() {
            return type;
        }

        public void displayDetails() {
            System.out.println("Room Type: " + type);
            System.out.println("Beds: " + beds);
            System.out.println("Room Size: " + size + " sq ft");
            System.out.println("Price per Night: $" + price);
        }
    }

    static class SingleRoom extends Room {
        public SingleRoom() {
            super("Single Room", 1, 200, 80.0);
        }
    }

    static class DoubleRoom extends Room {
        public DoubleRoom() {
            super("Double Room", 2, 350, 120.0);
        }
    }

    static class SuiteRoom extends Room {
        public SuiteRoom() {
            super("Suite Room", 3, 600, 250.0);
        }
    }

    // ---------------- INVENTORY ----------------
    static class RoomInventory {

        private HashMap<String, Integer> inventory;

        public RoomInventory() {
            inventory = new HashMap<>();
            inventory.put("Single Room", 2);
            inventory.put("Double Room", 1);
            inventory.put("Suite Room", 1);
        }

        public int getAvailability(String roomType) {
            return inventory.getOrDefault(roomType, 0);
        }

        public void reduceAvailability(String roomType) {
            int count = inventory.getOrDefault(roomType, 0);
            if (count > 0) {
                inventory.put(roomType, count - 1);
            }
        }

        public void displayInventory() {
            System.out.println("\nInventory Status:");
            for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
                System.out.println(entry.getKey() + " -> " + entry.getValue());
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

        private Queue<Reservation> queue = new LinkedList<>();

        public void addRequest(Reservation r) {
            queue.offer(r);
        }

        public Reservation getNextRequest() {
            return queue.poll(); // FIFO
        }

        public boolean isEmpty() {
            return queue.isEmpty();
        }
    }

    // ---------------- BOOKING SERVICE ----------------
    static class BookingService {

        private RoomInventory inventory;

        // Track all allocated IDs (uniqueness)
        private Set<String> allocatedRoomIds = new HashSet<>();

        // Map room type → allocated IDs
        private Map<String, Set<String>> roomAllocations = new HashMap<>();

        private int idCounter = 100;

        public BookingService(RoomInventory inventory) {
            this.inventory = inventory;
        }

        // Generate unique room ID
        private String generateRoomId(String roomType) {
            idCounter++;
            String prefix = roomType.replace(" ", "").toUpperCase();
            return prefix + "-" + idCounter;
        }

        // PROCESS QUEUE
        public void processBookings(BookingRequestQueue queue) {

            System.out.println("\nProcessing Booking Requests...");
            System.out.println("----------------------------------");

            while (!queue.isEmpty()) {

                Reservation req = queue.getNextRequest();

                String type = req.roomType;

                // Check availability
                if (inventory.getAvailability(type) > 0) {

                    String roomId = generateRoomId(type);

                    // Ensure uniqueness (Set prevents duplicates)
                    if (!allocatedRoomIds.contains(roomId)) {

                        allocatedRoomIds.add(roomId);

                        // Map room type → IDs
                        roomAllocations
                                .computeIfAbsent(type, k -> new HashSet<>())
                                .add(roomId);

                        // Update inventory immediately
                        inventory.reduceAvailability(type);

                        System.out.println("Booking CONFIRMED for " + req.guestName +
                                " | Room: " + type +
                                " | ID: " + roomId);
                    }

                } else {
                    System.out.println("Booking FAILED for " + req.guestName +
                            " | Room type unavailable: " + type);
                }
            }
        }

        public void displayAllocations() {
            System.out.println("\nAllocated Rooms:");
            for (Map.Entry<String, Set<String>> entry : roomAllocations.entrySet()) {
                System.out.println(entry.getKey() + " -> " + entry.getValue());
            }
        }
    }

    // ---------------- MAIN ----------------
    public static void main(String[] args) {

        System.out.println("Book My Stay - Room Allocation सिस्टम");
        System.out.println("----------------------------------");

        // Inventory
        RoomInventory inventory = new RoomInventory();
        inventory.displayInventory();

        // Queue
        BookingRequestQueue queue = new BookingRequestQueue();

        queue.addRequest(new Reservation("Alice", "Single Room"));
        queue.addRequest(new Reservation("Bob", "Single Room"));
        queue.addRequest(new Reservation("Charlie", "Single Room")); // should fail
        queue.addRequest(new Reservation("David", "Double Room"));

        // Booking Service
        BookingService service = new BookingService(inventory);

        // Process queue
        service.processBookings(queue);

        // Final state
        service.displayAllocations();
        inventory.displayInventory();
    }
}