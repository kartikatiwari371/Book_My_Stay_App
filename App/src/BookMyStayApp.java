import java.util.*;

public class BookMyStayApp {

    // ---------------- ROOM ----------------
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
    }

    static class SingleRoom extends Room {
        public SingleRoom() { super("Single Room", 1, 200, 80); }
    }

    static class DoubleRoom extends Room {
        public DoubleRoom() { super("Double Room", 2, 350, 120); }
    }

    static class SuiteRoom extends Room {
        public SuiteRoom() { super("Suite Room", 3, 600, 250); }
    }

    // ---------------- INVENTORY ----------------
    static class RoomInventory {
        private Map<String, Integer> inventory = new HashMap<>();

        public RoomInventory() {
            inventory.put("Single Room", 2);
            inventory.put("Double Room", 1);
            inventory.put("Suite Room", 1);
        }

        public int getAvailability(String type) {
            return inventory.getOrDefault(type, 0);
        }

        public void reduceAvailability(String type) {
            inventory.put(type, inventory.get(type) - 1);
        }

        public void display() {
            System.out.println("\nInventory:");
            inventory.forEach((k,v)-> System.out.println(k+" -> "+v));
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
        private Map<String, Set<String>> allocations = new HashMap<>();
        private int counter = 100;

        public BookingService(RoomInventory inventory) {
            this.inventory = inventory;
        }

        private String generateId(String type) {
            counter++;
            return type.replace(" ", "").toUpperCase() + "-" + counter;
        }

        public List<String> process(BookingRequestQueue queue) {

            List<String> confirmedReservationIds = new ArrayList<>();

            while (!queue.isEmpty()) {

                Reservation r = queue.next();

                if (inventory.getAvailability(r.roomType) > 0) {

                    String id = generateId(r.roomType);

                    if (!allocatedIds.contains(id)) {

                        allocatedIds.add(id);

                        allocations
                                .computeIfAbsent(r.roomType, k -> new HashSet<>())
                                .add(id);

                        inventory.reduceAvailability(r.roomType);

                        confirmedReservationIds.add(id);

                        System.out.println("CONFIRMED: " + r.guestName + " -> " + id);
                    }
                } else {
                    System.out.println("FAILED: " + r.guestName);
                }
            }

            return confirmedReservationIds;
        }
    }

    // ---------------- NEW: ADD-ON SERVICE ----------------
    static class AddOnService {
        String name;
        double cost;

        public AddOnService(String name, double cost) {
            this.name = name;
            this.cost = cost;
        }
    }

    // ---------------- NEW: SERVICE MANAGER ----------------
    static class AddOnServiceManager {

        private Map<String, List<AddOnService>> serviceMap = new HashMap<>();

        // attach service to reservation
        public void addService(String reservationId, AddOnService service) {

            serviceMap
                    .computeIfAbsent(reservationId, k -> new ArrayList<>())
                    .add(service);

            System.out.println("Added service " + service.name + " to " + reservationId);
        }

        // calculate total cost
        public double calculateTotal(String reservationId) {

            double total = 0;

            List<AddOnService> services = serviceMap.get(reservationId);

            if (services != null) {
                for (AddOnService s : services) {
                    total += s.cost;
                }
            }

            return total;
        }

        public void displayServices(String reservationId) {

            System.out.println("\nServices for " + reservationId);

            List<AddOnService> services = serviceMap.get(reservationId);

            if (services == null) {
                System.out.println("No services selected.");
                return;
            }

            for (AddOnService s : services) {
                System.out.println("- " + s.name + " ($" + s.cost + ")");
            }

            System.out.println("Total Add-On Cost: $" + calculateTotal(reservationId));
        }
    }

    // ---------------- MAIN ----------------
    public static void main(String[] args) {

        RoomInventory inventory = new RoomInventory();

        BookingRequestQueue queue = new BookingRequestQueue();
        queue.add(new Reservation("Alice", "Single Room"));
        queue.add(new Reservation("Bob", "Double Room"));

        BookingService bookingService = new BookingService(inventory);

        // Process bookings
        List<String> reservationIds = bookingService.process(queue);

        // ---------------- ADD-ON SERVICES ----------------
        AddOnServiceManager manager = new AddOnServiceManager();

        // Add services to first reservation
        if (!reservationIds.isEmpty()) {

            String resId = reservationIds.get(0);

            manager.addService(resId, new AddOnService("Breakfast", 20));
            manager.addService(resId, new AddOnService("Airport Pickup", 40));

            manager.displayServices(resId);
        }

        // Final inventory check
        inventory.display();
    }
}