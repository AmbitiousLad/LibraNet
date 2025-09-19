import java.util.*;
import java.text.SimpleDateFormat;

abstract class Item {
    public int id;
    public String title;
    public String author;
    public boolean available;
    public int borrowDuration;
    public Date borrowDate;
    private static int nextId = 1;

    public Item(String title, String author) {
        this.id = nextId++;
        this.title = title;
        this.author = author;
        this.available = true;
    }

    public void borrow(int durationDays) {
        if (!available) {
            System.out.println("Item is already borrowed.");
            return;
        }
        if (durationDays <= 0) {
            System.out.println("Invalid borrow duration: " + durationDays);
            return;
        }
        this.borrowDuration = durationDays;
        this.borrowDate = new Date();
        this.available = false;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        System.out.println(title + " borrowed for " + durationDays + " days on " + sdf.format(borrowDate) +
                ". Due on " + sdf.format(DueDate()));
    }

    public void borrow(String durationStr) {
        try {
            int days = DurationParser.parse(durationStr);
            borrow(days);
        } catch (IllegalArgumentException e) {
            System.out.println("Failed to borrow '" + title + "': " + e.getMessage());
        }
    }

    public boolean isAvailable() {
        return available;
    }

    public int BorrowedDays() {
        if (borrowDate == null) return 0;
        Date today = new Date();
        long diffMillis = today.getTime() - borrowDate.getTime();
        return (int) (diffMillis / (1000L * 60 * 60 * 24));
    }

    public int OverdueDays() {
        int borrowedDays = BorrowedDays();
        if (borrowedDays > borrowDuration) {
            return borrowedDays - borrowDuration;
        }
        return 0;
    }

    public Date DueDate() {
        if (borrowDate == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(borrowDate);
        cal.add(Calendar.DAY_OF_MONTH, borrowDuration);
        return cal.getTime();
    }

    public void returnItem(Map<Integer, Double> fines, double fineRate) {
        if (available) {
            System.out.println(title + " was not borrowed.");
            return;
        }
        int overdue = OverdueDays();
        if (overdue > 0) {
            double fine = overdue * fineRate;
            fines.merge(id, fine, Double::sum);
            System.out.println(title + " returned late. Fine: Rs." + fine);
        } else {
            System.out.println(title + " returned on time. No fine.");
        }
        available = true;
        borrowDate = null;
        borrowDuration = 0;
    }

    public int getId() {
        return id;
    }

    public abstract void displayDetails();
}

class Book extends Item {
    private int pageCount;

    public Book(String title, String author, int pageCount) {
        super(title, author);
        this.pageCount = pageCount;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void displayDetails() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String due = (borrowDate != null) ? sdf.format(DueDate()) : "N/A";
        System.out.println("BookID: " + id + ", Title: " + title + ", Author: " + author +
                ", Pages: " + pageCount + ", Available: " + available + ", Due: " + due);
    }
}

interface Playable {
    void play();
}

class AudioBook extends Item implements Playable {

    public AudioBook(String title, String author) {
        super(title, author);
    }

    public void play() {
        if (available) {
            System.out.println("You must borrow the audiobook first to play it.");
            return;
        }
        System.out.println("Playing audiobook: " + title);
    }

    public void displayDetails() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String due = (borrowDate != null) ? sdf.format(DueDate()) : "N/A";
        System.out.println("AudioBookID: " + id + ", Title: " + title + ", Author: " + author +
                ", Available: " + available + ", Due: " + due);
    }
}

class EMagazine extends Item {
    private static int nextIssue = 1;
    private int issueNo;

    public EMagazine(String title, String author) {
        super(title, author);
        this.issueNo = nextIssue++;
    }

    public void archiveIssue() {
        System.out.println("Archiving issue #" + issueNo + " of " + title);
    }

    public void displayDetails() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String due = (borrowDate != null) ? sdf.format(DueDate()) : "N/A";
        System.out.println("EMagazineID: " + id + ", Title: " + title + ", Author: " + author +
                ", Issue No: " + issueNo + ", Available: " + available + ", Due: " + due);
    }
}

class DurationParser {
    public static int parse(String input) {
    input = input.toLowerCase().trim();
    if (input.matches("\\d+")) {
        return Integer.parseInt(input);
    } else if (input.contains("day")) {
        return Integer.parseInt(input.split(" ")[0]);
    } else if (input.contains("week")) {
        return Integer.parseInt(input.split(" ")[0]) * 7;
    } else if (input.contains("month")) {
        return Integer.parseInt(input.split(" ")[0]) * 30;
    } else {
        throw new IllegalArgumentException("Invalid duration format: " + input);
    }
}
}

class LibraNet {
    private List<Item> items = new ArrayList<>();
    private Map<Integer, Double> fines = new HashMap<>();
    private static final double FINE_PER_DAY = 10.0;

    public void addItem(Item item) {
        items.add(item);
    }


    public void borrow(Item item, String durationStr) {
        item.borrow(durationStr);
    }

    public void borrow(Item item, int days) {
        item.borrow(days);
    }

    public void returnItem(Item item) {
        item.returnItem(fines, FINE_PER_DAY);
    }

    public void showFines() {
        if (fines.isEmpty()) {
            System.out.println("No fines recorded.");
            return;
        }
        for (Map.Entry<Integer, Double> entry : fines.entrySet()) {
            System.out.println("Item ID " + entry.getKey() + " - Fine: Rs." + entry.getValue());
        }
    }

    public void listAllItems() {
        for (Item i : items)
        { 
            i.displayDetails();
        }
    }
}

public class main {
    public static void main(String[] args) {
        LibraNet library = new LibraNet();

        Book b1 = new Book("The Intelligent Investor", "Benjamin Graham", 180);
        AudioBook a1 = new AudioBook("Momentum Masters", "Mark Minervini");
        EMagazine m1 = new EMagazine("AI Weekly", "Tech Press");

        library.addItem(b1);
        library.addItem(a1);
        library.addItem(m1);

        library.borrow(b1, "1 week");
        library.borrow(a1, "10 days");
        library.borrow(m1, 5);


        library.returnItem(b1);
        library.returnItem(a1);
        library.returnItem(m1);

        library.showFines();
        library.listAllItems();
    }
}
