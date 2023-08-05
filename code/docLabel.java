public class docLabel {
    public String doc;
    public String label;
    public double distance;
    public docLabel(String doc, String label) {
        this.doc = doc;
        this.label = label;
    }
    public docLabel(String doc, String label, double distance) {
        this.doc = doc;
        this.label = label;
        this.distance = distance;
    }
}
