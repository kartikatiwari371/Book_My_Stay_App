import java.util.*;  // for HashMap, Map, Queue, LinkedList

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

            inventory.put("Single Room", 5);
            inventory.put("Double Room", 3);
            inventory.put("Suite Room", 1);
        }

        public int getAvailability(String roomType) {
            return inventory.getOrDefault(roomType, 0);
        }

        public void displayInventory() {
            System.out.println("Inventory Status:");
            for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
                System.out.println(entry.getKey() + " -> " + entry.getValue());
            }
            System.out.println("----------------------------------");
        }
    }

    // ---------------- SEARCH SERVICE (UNCHANGED) ----------------
    static class SearchService {

        private RoomInventory inventory;

        public SearchService(RoomInventory inventory) {
            this.inventory = inventory;
        }

        public void searchAvailableRooms(Room[] rooms) {

            System.out.println("\nAvailable Rooms:");
            System.out.println("----------------------------------");

            for (Room room : rooms) {
                int available = inventory.getAvailability(room.getType());

                if (available > 0) {
                    room.displayDetails();
                    System.out.println("Available Count: " + available);
                    System.out.println("----------------------------------");
                }
            }
        }
    }

    // ---------------- NEW: RESERVATION ----------------
    static class Reservation {

        private String guestName;
        private String roomType;

        public Reservation(String guestName, String roomType) {
            this.guestName = guestName;
            this.roomType = roomType;
        }

        public void displayRequest() {
            System.out.println("Guest: " + guestName + " requested -> " + roomType);
        }
    }

    // ---------------- NEW: BOOKING REQUEST QUEUE ----------------
    static class BookingRequestQueue {

        private Queue<Reservation> queue;

        public BookingRequestQueue() {
            queue = new LinkedList<>();
        }

        // Add request (FIFO order maintained)
        public void addRequest(Reservation reservation) {
            queue.offer(reservation);
            System.out.println("Booking request added.");
        }

        // Display all queued requests
        public void displayQueue() {
            System.out.println("\nBooking Request Queue (FIFO Order):");
            System.out.println("----------------------------------");

            for (Reservation r : queue) {
                r.displayRequest();
            }

            System.out.println("----------------------------------");
        }
    }

    // ---------------- MAIN ----------------
    public static void main(String[] args) {

        System.out.println("Welcome to Book My Stay - Booking Queue System");
        System.out.println("----------------------------------");

        // Room objects
        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        Room[] rooms = { single, doubleRoom, suite };

        // Inventory
        RoomInventory inventory = new RoomInventory();
        inventory.displayInventory();

        // SEARCH (read-only)
        SearchService searchService = new SearchService(inventory);
        searchService.searchAvailableRooms(rooms);

        // ---------------- BOOKING REQUESTS ----------------
        BookingRequestQueue bookingQueue = new BookingRequestQueue();

        System.out.println("\nAdding booking requests...");

        bookingQueue.addRequest(new Reservation("Alice", "Single Room"));
        bookingQueue.addRequest(new Reservation("Bob", "Double Room"));
        bookingQueue.addRequest(new Reservation("Charlie", "Suite Room"));

        // Display queue (FIFO preserved)
        bookingQueue.displayQueue();

        // Verify inventory unchanged
        System.out.println("\nInventory remains unchanged:");
        inventory.displayInventory();
    }
}