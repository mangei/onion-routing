package comparator;

import model.ChainNode;

import java.util.Comparator;

public class ChainNodeUsedComparator implements Comparator<ChainNode> {

    @Override
    public int compare(ChainNode o1, ChainNode o2) {
        if (o1.getLasttimeused() < o2.getLasttimeused()) {
            return -1;
        }

        if (o1.getLasttimeused() > o2.getLasttimeused()) {
            return 1;
        }
        return 0;
    }
}
