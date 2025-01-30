package fan.summer.hmoneta.util;

import java.util.List;
import java.util.stream.Stream;

/**
 * 类的详细说明
 *
 * @author phoebej
 * @version 1.00
 * @Date 2025/1/30
 */
public class DynamicStreamProcessingUtil {
    private static final int PARALLEL_THRESHOLD = 10000;

    /**
     * Processes a list of elements conditionally using parallel or sequential stream based on the size of the input list.
     * If the list size exceeds a predefined threshold, a parallel stream is used to potentially improve performance on multi-core systems.
     * Otherwise, a sequential stream is employed for smaller lists where parallelization overhead may outweigh its benefits.
     *
     * @param <T>       the type of elements in the input list
     * @param inputList the list of elements to be processed
     * @return a Stream of the input list's elements, either as a parallel or sequential stream based on the list size
     */
    public static <T> Stream<T> processWithConditionalParallel(List<T> inputList) {
        // 判断数据量大小，决定使用串行还是并行流
        return inputList.size() > PARALLEL_THRESHOLD ? inputList.parallelStream() : inputList.stream();
    }


}
