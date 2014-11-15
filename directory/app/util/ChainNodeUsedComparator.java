package util;

import model.ChainNode;

import java.util.Comparator;

/**
 * Created by markus on 15.11.2014.
 */
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
