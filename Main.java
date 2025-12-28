import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Main {


    private static LocalDate parseDateOrNull(String s) {
        if (s == null) return null;
        s = s.trim();
        if (s.isEmpty()) return null;
        return LocalDate.parse(s); // expects YYYY-MM-DD
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createAndShowUI);
    }

    private static void createAndShowUI() {
        JFrame frame = new JFrame("Canada Citizenship Calculator (1095 days / rolling 5-year window)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("PR date (YYYY-MM-DD):"));
        JTextField prField = new JTextField(12);
        top.add(prField);

        JButton calcBtn = new JButton("Calculate");
        top.add(calcBtn);

        JLabel todayLabel = new JLabel("Today: " + LocalDate.now());
        top.add(todayLabel);

        // Table for stays
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Entry (YYYY-MM-DD)", "Exit (YYYY-MM-DD, blank = ongoing)"}, 0);
        JTable table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);

        JButton addRow = new JButton("Add stay");
        JButton removeRow = new JButton("Remove selected");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(addRow);
        buttons.add(removeRow);

        JTextArea output = new JTextArea(10, 80);
        output.setEditable(false);
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane outScroll = new JScrollPane(output);

        addRow.addActionListener(e -> model.addRow(new Object[]{"", ""}));
        removeRow.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r >= 0) model.removeRow(r);
        });

        calcBtn.addActionListener(e -> {
            try {
                LocalDate prDate = LocalDate.parse(prField.getText().trim());
                LocalDate today = LocalDate.now();

                List<Stay> stays = new ArrayList<>();
                for (int i = 0; i < model.getRowCount(); i++) {
                    String entryStr = String.valueOf(model.getValueAt(i, 0)).trim();
                    String exitStr  = String.valueOf(model.getValueAt(i, 1)).trim();

                    if (entryStr.isEmpty()) continue; // ignore blank rows

                    LocalDate entry = LocalDate.parse(entryStr);
                    LocalDate exit = parseDateOrNull(exitStr);

                    // basic sanity
                    if (exit != null && exit.isBefore(entry)) {
                        throw new IllegalArgumentException("Row " + (i+1) + ": exit is before entry.");
                    }

                    stays.add(new Stay(entry, exit));
                }

                if (stays.isEmpty()) {
                    throw new IllegalArgumentException("Please add at least one stay (entry date).");
                }


                 LocalDate eligibility = CitizenshipCalculator.findEligibilityDate(stays, prDate, today);

                StringBuilder sb = new StringBuilder();
                sb.append("=== Result ===\n");
                sb.append("PR date: ").append(prDate).append("\n");
                sb.append("Today:   ").append(today).append("\n\n");

                CreditResult r = CitizenshipCalculator.creditedBreakdownInWindow(stays, prDate, today);
                sb.append("\n--- Credit breakdown (today) ---\n");
                sb.append(r.toString()).append("\n");

                if (eligibility == null) {
                    sb.append("\nEligibility date: NOT FOUND within 10 years (check inputs)\n");
                } else {
                    long countdown = ChronoUnit.DAYS.between(today, eligibility);
                    sb.append("\nEligibility date (qualifying date): ").append(eligibility).append("\n");
                    sb.append("Countdown (days): ").append(countdown).append("\n");

                    // also show the window used on that qualifying date
                    LocalDate winStart = eligibility.minusYears(5);
                    sb.append("Window checked: ").append(winStart).append(" to ").append(eligibility).append("\n");
                }

                output.setText(sb.toString());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // Layout
        JPanel center = new JPanel(new BorderLayout());
        center.add(scroll, BorderLayout.CENTER);
        center.add(buttons, BorderLayout.SOUTH);

        JPanel root = new JPanel(new BorderLayout());
        root.add(top, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        root.add(outScroll, BorderLayout.SOUTH);

        frame.setContentPane(root);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
