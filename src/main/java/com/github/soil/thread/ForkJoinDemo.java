package com.github.soil.thread;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author tanruidong
 * @since 2024/05/11 18:31
 */
public class ForkJoinDemo {

    private static HashMap<Combine, List<Integer>> globalMap = new HashMap<>();
    public static void main(String[] args) throws Exception {
        ForkJoinPool pool = new ForkJoinPool(12);
        List<ProcurementOrderLine> lines = new ArrayList<>();
        ForkJoinTask<Map<Combine, List<Integer>>> submit = pool.submit(new SplitTask(lines));
        System.out.println(submit.get());
    }

    private static class SplitTask extends RecursiveTask<Map<Combine, List<Integer>>>{

        private static final int THRESHOLD = 500;

        private List<ProcurementOrderLine> lines;

        public SplitTask(List<ProcurementOrderLine> lines) {
            this.lines = lines;
        }

        @Override
        protected Map<Combine, List<Integer>> compute() {
           if (lines.size() > THRESHOLD){
               List<SplitTask> subtasks = createSubtasks();
               invokeAll(subtasks).stream()
                       .map(SplitTask::join)
                       .forEach(v -> v.forEach((k, lineIds) -> {
                           List<Integer> lines = globalMap.computeIfAbsent(k, combine -> new ArrayList<>());
                           lines.addAll(lineIds);
                       }));
               return globalMap;
           }else {
               Map<Combine, List<ProcurementOrderLine>> collect = lines.stream().collect(Collectors.groupingBy(ProcurementOrderLine::getCombine));
                Map<Combine, List<Integer>> result = new HashMap<>();
                collect.forEach((k, v) -> result.put(k, v.stream().map(ProcurementOrderLine::getLineId).collect(Collectors.toList())));
                return result;
           }
        }

        private List<SplitTask> createSubtasks() {
            List<List<ProcurementOrderLine>> partitionTasks = Lists.partition(lines, THRESHOLD);
            return partitionTasks.stream().map(SplitTask::new).collect(Collectors.toList());
        }
    }

    @Data
    private class Combine{
        private List<TagInfo> tags;
        private List<AttrInfo> attrs;
        private Integer orderId;
    }

    @Data
    private class TagInfo{
        private Integer tagId;
        private Integer tagValueId;
    }

    @Data
    private class AttrInfo{
        private Integer attrId;
        private Integer attrValueId;
    }

    @Data
    private class ProcurementOrderLine{
        private Integer lineId; // 对于一个sku
        private Combine combine;
    }
}
