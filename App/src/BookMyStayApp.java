/**
 * BookMyStayApp
 *
 * This class represents the entry point of the Book My Stay
 * Hotel Booking Management System application.
 * It now demonstrates basic room modeling using abstraction
 * and inheritance along with static availability.
 *
 * @author kuljot
 * @version 2.1
 */

public class BookMyStayApp {

    /**
     * Abstract Room class defining common properties of all rooms.
     */
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

        public void displayDetails(int availability) {
            System.out.println("Room Type: " + type);
            System.out.println("Beds: " + beds);
            System.out.println("Room Size: " + size + " sq ft");
            System.out.println("Price per Night: $" + price);
            System.out.println("Available Rooms: " + availability);
            System.out.println("----------------------------------");
        }
    }

    /** Single Room Implementation */
    static class SingleRoom extends Room {
        public SingleRoom() {
            super("Single Room", 1, 200, 80.0);
        }
    }

    /** Double Room Implementation */
    static class DoubleRoom extends Room {
        public DoubleRoom() {
            super("Double Room", 2, 350, 120.0);
        }
    }

    /** Suite Room Implementation */
    static class SuiteRoom extends Room {
        public SuiteRoom() {
            super("Suite Room", 3, 600, 250.0);
        }
    }

    /**
     * Main method - Entry point of the application.
     * The JVM begins execution from here.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Hotel Room Initialized");
        // Create room objects (Polymorphism)
        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        // Static availability variables
        int singleAvailability = 5;
        int doubleAvailability = 3;
        int suiteAvailability = 2;

        // Display room details
        single.displayDetails(singleAvailability);
        doubleRoom.displayDetails(doubleAvailability);
        suite.displayDetails(suiteAvailability);

        System.out.println("Application terminated successfully.");
    }
}