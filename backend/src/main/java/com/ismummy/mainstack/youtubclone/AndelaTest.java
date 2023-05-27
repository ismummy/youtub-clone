package com.ismummy.mainstack.youtubclone;

import com.nimbusds.jose.util.Pair;
import org.springframework.security.core.parameters.P;

import java.util.*;

/*Interview at GS
        Problem solving challenge
        Given a jumbled collection of segments, each of which is represented as
        a Pair (startPoint, endPoint), this function sorts the segments to
        make a continuous path
        A few assumptions you can make:
        1. Each particular segment goes in one direction only, i.e.: if you
        • see (1, 2), you will not see (2, 1).
        2. Each starting point only have one way to the end point, i.e.: if
        -you see • (6, 5), you will not see• (6, 10), (6, 3), etc.
        For example, if you're passed a list containing the following intarrays:
        .. • [(4, 5), • (9, 4), • (5, •1), • (11, 9)]
        Then your implementation should sort it such:
        • [ (11, 9), • (9, 4) , • (4, 5), (5, 1)] @param segments collection of segments, each represented by a Pair
        (startPoint, endPoint).
        @return • The sorted segments such that they form a continuous path.
        @throws Exception if there is no way to create one continuous path -from *all - the segments passed into this function. Feel -free to change the
        Exception type as you think appropriate.*/

public class AndelaTest {
    public static List<Pair<Integer, Integer>> sortSegment(List<Pair<Integer, Integer>> segments) throws Exception {
        Map<Integer, Integer> pairs = new HashMap<>();

        int largestStart = 0;


        for (Pair<Integer, Integer> segment : segments) {
            int startPoint = segment.getLeft();
            int endPoint = segment.getRight();

            if (pairs.containsKey(startPoint)) {
                throw new Exception("Invalid segment!");
            }

            largestStart = Math.max(startPoint, largestStart);

            pairs.put(startPoint, endPoint);
        }

        List<Pair<Integer, Integer>> result = new ArrayList<>();


        int currentEnd = pairs.get(largestStart);
        result.add(Pair.of(largestStart, currentEnd));
        pairs.remove(largestStart);

        while (pairs.containsKey(currentEnd)) {
            int tempCurrentEnd = currentEnd;

            currentEnd = pairs.get(tempCurrentEnd);
            pairs.remove(tempCurrentEnd);
            result.add(Pair.of(tempCurrentEnd, currentEnd));
        }

        if (pairs.size() > 0) {
            throw new Exception("Chain discontinued!");
        }
        return result;

    }

    public static void main(String[] arg) {
        List<Pair<Integer, Integer>> pairs = new ArrayList<>();
        pairs.add(Pair.of(4, 5));
        pairs.add(Pair.of(9, 4));
        pairs.add(Pair.of(5, 1));
        pairs.add(Pair.of(11, 9));

        try {

            for (Pair<Integer, Integer> pair : sortSegment(pairs)) {
                int startPoint = pair.getLeft();
                int endPoint = pair.getRight();

                System.out.printf("%d, %d%n",startPoint,endPoint);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
