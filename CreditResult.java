public class CreditResult {
    public final double prePrCredited;
    public final double postPrCredited;

    public CreditResult(double prePrCredited, double postPrCredited) {
        this.prePrCredited = prePrCredited;
        this.postPrCredited = postPrCredited;
    }

    public double totalCredited() {
        return prePrCredited + postPrCredited;
    }
    @Override
public String toString() {
    return String.format(
        "Pre-PR credited: %.1f days\nPost-PR credited: %.1f days\nTotal credited: %.1f days",
        prePrCredited, postPrCredited, totalCredited()
    );
}

}
