import java.util.HashMap;
import java.util.Map;

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
            inventory.put("Suite Room", 0); // unavailable
        }

        public int getAvailability(String roomType) {
            return inventory.getOrDefault(roomType, 0);
        }

        public void updateAvailability(String roomType, int newCount) {
            inventory.put(roomType, newCount);
        }

        public void displayInventory() {
            System.out.println("Inventory Status:");
            for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
                System.out.println(entry.getKey() + " -> " + entry.getValue());
            }
            System.out.println("----------------------------------");
        }
    }

    // ---------------- SEARCH SERVICE ----------------
    static class SearchService {

        private RoomInventory inventory;

        public SearchService(RoomInventory inventory) {
            this.inventory = inventory;
        }

        // READ-ONLY SEARCH
        public void searchAvailableRooms(Room[] rooms) {

            System.out.println("\nAvailable Rooms:");
            System.out.println("----------------------------------");

            for (Room room : rooms) {

                int available = inventory.getAvailability(room.getType());

                // Show only available rooms
                if (available > 0) {
                    room.displayDetails();
                    System.out.println("Available Count: " + available);
                    System.out.println("----------------------------------");
                }
            }
        }
    }

    // ---------------- MAIN ----------------
    public static void main(String[] args) {

        System.out.println("Welcome to Book My Stay - Room Search");
        System.out.println("----------------------------------");

        // Room objects
        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        Room[] rooms = { single, doubleRoom, suite };

        // Inventory
        RoomInventory inventory = new RoomInventory();

        // Display inventory
        inventory.displayInventory();

        // Search (READ-ONLY)
        SearchService searchService = new SearchService(inventory);
        searchService.searchAvailableRooms(rooms);

        // Verify no change
        System.out.println("\nInventory After Search (unchanged):");
        inventory.displayInventory();
    }
}