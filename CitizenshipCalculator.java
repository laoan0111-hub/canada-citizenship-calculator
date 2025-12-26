import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CitizenshipCalculator {

    public static final int REQUIRED_CREDITED_DAYS = 1096;
    public static final int PRE_PR_MAX_PHYSICAL_DAYS = 730; // max physical pre-PR days that can be credited at 0.5

    // Returns credited days in the 5-year window ending on "asOf" (inclusive end),
    // assuming the person is in Canada unless outside the stays list.
    // We only count days within stays intervals that overlap the window.
    public static double creditedDaysInWindow(List<Stay> stays, LocalDate prDate, LocalDate asOf) {
        LocalDate windowStart = asOf.minusYears(5); // start of window (same month/day), inclusive
        LocalDate windowEnd = asOf;                 // inclusive

        // Normalize stays: sort by entry
        List<Stay> sorted = new ArrayList<>(stays);
        sorted.sort(Comparator.comparing(s -> s.entry));

        double prePrCredited = 0.0;
        double postPrCredited = 0.0;

        int prePrPhysicalCounted = 0; // cap at 365 physical days before PR (credited at 0.5)

        for (Stay s : sorted) {
            LocalDate entry = s.entry;
            LocalDate exit = (s.exit == null) ? asOf : s.exit;

            // ignore future stays beyond asOf
            if (entry.isAfter(asOf)) continue;

            // overlap with [windowStart, windowEnd]
            LocalDate overlapStart = max(entry, windowStart);
            LocalDate overlapEnd = min(exit, windowEnd);

            if (overlapEnd.isBefore(overlapStart)) continue;

            // Count inclusive days in overlap
            long physicalDays = ChronoUnit.DAYS.between(overlapStart, overlapEnd) + 1;

            // Split by PR date
            // Part A: before PR (strictly < prDate)
            LocalDate preEnd = min(overlapEnd, prDate.minusDays(1));
            if (!preEnd.isBefore(overlapStart)) {
                long prePhysical = ChronoUnit.DAYS.between(overlapStart, preEnd) + 1;

                // apply cap on physical days that can be used pre-PR
                long remainingPhysicalCap = PRE_PR_MAX_PHYSICAL_DAYS - prePrPhysicalCounted;
                long usablePhysical = Math.max(0, Math.min(prePhysical, remainingPhysicalCap));

                prePrPhysicalCounted += (int) usablePhysical;
                prePrCredited += usablePhysical * 0.5;
            }

            // Part B: on/after PR (>= prDate)
            LocalDate postStart = max(overlapStart, prDate);
            if (!overlapEnd.isBefore(postStart)) {
                long postPhysical = ChronoUnit.DAYS.between(postStart, overlapEnd) + 1;
                postPrCredited += postPhysical;
            }
        }

        return prePrCredited + postPrCredited;
    }

    // Find earliest eligibility date >= today assuming continuous presence after last entry if exit is blank.
    public static LocalDate findEligibilityDate(List<Stay> stays, LocalDate prDate, LocalDate today) {
        // Search forward day-by-day until credited >= 1095
        // Safety limit: 10 years forward
        LocalDate d = today;
        LocalDate limit = today.plusYears(10);

        while (!d.isAfter(limit)) {
            double credited = creditedDaysInWindow(stays, prDate, d);
            if (credited >= REQUIRED_CREDITED_DAYS) return d;
            d = d.plusDays(1);
        }

        return null; // not found within 10 years
    }

    private static LocalDate max(LocalDate a, LocalDate b) {
        return a.isAfter(b) ? a : b;
    }

    private static LocalDate min(LocalDate a, LocalDate b) {
        return a.isBefore(b) ? a : b;
    }
}
