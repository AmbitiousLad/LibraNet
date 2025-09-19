import java.util.*;


abstract class Item {
    public int id;
    public String title;
    public String author;
    public boolean available;
    public int borrowDurationDays; 
    public int daysBorrowed;     
    private static int nextId = 1;


    public Item(String title, String author) {
        this.id = nextId++; 
        this.title = title;
        this.author = author;
        this.available = true;
        this.daysBorrowed = 0;
    }

    public void borrow(int durationDays) {
        if (!available) {
            System.out.println("Item is already borrowed.");
        }
        this.borrowDurationDays = durationDays;
        this.daysBorrowed = 0;  
        this.available = false;
        System.out.println(title + " borrowed for " + durationDays + " days.");
    }

    public void advanceDay() {
        if (!available) {
            daysBorrowed++;
        }
    }

    public int getOverdueDays() {
        if (daysBorrowed > borrowDurationDays) {
            return daysBorrowed - borrowDurationDays;
        }
        return 0;
    }

    public void return(Map<Integer, Double> fines, double fineRate) {
        if (available) {
            System.out.println(title + " was not borrowed.");
            return;
        }
        int overdue = getOverdueDays();
        if (overdue > 0) {
            double fine = overdue * fineRate;
            fines.put(id, fine);
            System.out.println(title + " returned late. Fine: Rs." + fine);
        } else {
            System.out.println(title + " returned on time. No fine.");
        }
        available = true;
        borrowDate = null;
        dueDate = null;
    }


    public abstract void displayDetails();
}



class Book extends Item {
    private int pageCount;

    public Book(String title, String author, int pageCount) {
        super(title, author);
        this.pageCount = pageCount;
    }

    public void displayDetails() {
        System.out.println("BookID: " + id + ", Title: " + title + ", Author: " + author +
                ", Pages: " + pageCount + ", Available: " + available);
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
        System.out.println("Playing audiobook: " + title);
    }

    public void displayDetails() {
        System.out.println("AudioBookID: " + id + ", Title: " + title + ", Author: " + author +
                ", Available: " + available);
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
        System.out.println("EMagazineID: " + id + ", Title: " + title + ", Author: " + author +
                ", Issue No: " + issueNo + ", Available: " + available);
    }
}



class LibraNet {
    private List<Item> items = new ArrayList<>();
    private Map<Integer, Double> fines = new HashMap<>();
    private static final double FINE_PER_DAY = 10.0;

    public void addItem(Item item) {
        items.add(item);
    }

    public void nextDay() {
        for (Item item : items) {
            item.advanceDay();
        }
    }

    public void borrow(Item item, int d){
        item.borrow(d);
    }

    public void returnItem(Item item) {
        item.return(fines,FINE_PER_DAY);
    }

    public void updateFines() {
        for (Item item : items) {
            int overdue = item.getOverdueDays();
            if (overdue > 0) {
                fines.put(item.id, overdue * FINE_PER_DAY);
            }
        }
    }

    public void advanceDay() {
        for(Item item : items) {
            item.advanceDay();
        }
    }

    public void showFines() {
        for (Map.Entry<Integer, Double> entry : fines.entrySet()) {
            System.out.println("Item ID " + entry.getKey() + " → Fine: Rs." + entry.getValue());
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

        library.borrow(b1, 7);
        library.advanceDay();

        

        b1.displayDetails();
        a1.displayDetails();
        m1.displayDetails();
    }
}
