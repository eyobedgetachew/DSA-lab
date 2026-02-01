import java.util.Scanner;

class Blockchain {
    int id;
    String data;
    long timestamp;
    long prevhash;
    long curhash;
    Blockchain next;

    Blockchain(int id, String data, long prevhash) {
        this.id = id;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
        this.prevhash = prevhash;
        this.curhash = calculateHash(data, prevhash);
        this.next = null;
    }

    static long calculateHash(String data, long prevhash) {
        long sum = 0;
        for (char c : data.toCharArray()) {
            sum += c;
        }
        return (sum + prevhash) % 100000;
    }
}

class BlockStack {
    class Node {
        Blockchain block;
        Node next;

        Node(Blockchain block) {
            this.block = block;
        }
    }

    Node top;

    void push(Blockchain block) {
        Node newNode = new Node(block);
        newNode.next = top;
        top = newNode;
    }

    Blockchain pop() {
        if (top == null) return null;
        Blockchain block = top.block;
        top = top.next;
        return block;
    }

    boolean isEmpty() {
        return top == null;
    }
}

class HashSnapshot {
    private long[] storage = new long[10];
    private int count = 0;

    void add(long hash) {
        if (count == storage.length) {
            long[] newStorage = new long[storage.length * 2];
            System.arraycopy(storage, 0, newStorage, 0, storage.length);
            storage = newStorage;
        }
        storage[count++] = hash;
    }

    public long get(int index) {
        return (index >= 0 && index < count) ? storage[index] : -1;
    }
}

public class Ledger {
    Blockchain head;
    Blockchain tail;
    int blockCount = 0;
    HashSnapshot snapshot = new HashSnapshot();

    void addBlock(String data) {
        long prevhash = (tail == null) ? 0 : tail.curhash;
        Blockchain newNode = new Blockchain(blockCount++, data, prevhash);

        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }

        snapshot.add(newNode.curhash);
        System.out.println("Block added successfully with hash: " + newNode.curhash);
    }

    void viewchain() {
        if (head == null) {
            System.out.println("Blockchain is empty.");
            return;
        }

        Blockchain current = head;
        while (current != null) {
            System.out.println("Block ID: " + current.id);
            System.out.println("Data: " + current.data);
            System.out.println("Timestamp: " + current.timestamp);
            System.out.println("Previous Hash: " + current.prevhash);
            System.out.println("Current Hash: " + current.curhash);
            System.out.println("---------------------------");
            current = current.next;
        }
    }

    void tamper(int id, String newData) {
        Blockchain current = head;
        while (current != null) {
            if (current.id == id) {
                current.data = newData;
                current.curhash = Blockchain.calculateHash(newData, current.prevhash);
                System.out.println("Block " + id + " tampered successfully.");
                return;
            }
            current = current.next;
        }
        System.out.println("Block with ID " + id + " not found.");
    }

    void validate() {
        BlockStack stack = new BlockStack();
        Blockchain current = head;

        while (current != null) {
            stack.push(current);
            current = current.next;
        }

        boolean isChainValid = true;
        System.out.println("Validating blockchain...");

        while (!stack.isEmpty()) {
            Blockchain block = stack.pop();
            long recalculatedHash =
                    Blockchain.calculateHash(block.data, block.prevhash);

            if (recalculatedHash != block.curhash ||
                block.curhash != snapshot.get(block.id)) {
                System.out.println("Block ID " + block.id + " has been tampered!");
                isChainValid = false;
            }
        }

        System.out.println(isChainValid
                ? "Blockchain is valid."
                : "Blockchain is invalid.");
    }

    public static void main(String[] args) {
        Ledger ledger = new Ledger();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("1. Add Block");
            System.out.println("2. View Blockchain");
            System.out.println("3. Tamper Block");
            System.out.println("4. Validate Blockchain");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter data for the new block: ");
                    ledger.addBlock(scanner.nextLine());
                    break;
                case 2:
                    ledger.viewchain();
                    break;
                case 3:
                    System.out.print("Enter Block ID to tamper: ");
                    int id = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter new data: ");
                    ledger.tamper(id, scanner.nextLine());
                    break;
                case 4:
                    ledger.validate();
                    break;
                case 5:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}
