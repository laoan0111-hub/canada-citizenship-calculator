import java.time.LocalDate;

public class Stay {
    public final LocalDate entry;
    public final LocalDate exit; // null means ongoing

    public Stay(LocalDate entry, LocalDate exit) {
        this.entry = entry;
        this.exit = exit;
    }
}
