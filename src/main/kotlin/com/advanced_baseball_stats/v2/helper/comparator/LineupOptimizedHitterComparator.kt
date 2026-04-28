package com.advanced_baseball_stats.v2.helper.comparator

import com.advanced_baseball_stats.v2.model.fantasy.LineupOptimizedHitter

class LineupOptimizedHitterComparator: Comparator<Pair<LineupOptimizedHitter, Double>>
{
    override fun compare(p1: Pair<LineupOptimizedHitter, Double>, p2: Pair<LineupOptimizedHitter, Double>): Int {
        if (p1.second < p2.second)
        {
            return -1
        }
        else if (p1.second > p2.second)
        {
            return 1
        }

        return 0
    }

}