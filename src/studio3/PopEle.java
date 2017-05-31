package studio3;

public class PopEle<PX, PF extends Comparable> {
    private PF fit;
    private PX ele;

    PopEle(PX x_, PF f_) {
        fit = f_;
        ele = x_;
    }

    public String toString() {
        return "\""+ele + "\",  " + fit.toString();
    }

    public PX getEle() {
        return ele;
    }

    public PF getFit() {
        return fit;
    }
}
