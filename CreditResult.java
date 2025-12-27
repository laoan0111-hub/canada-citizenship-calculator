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
}
