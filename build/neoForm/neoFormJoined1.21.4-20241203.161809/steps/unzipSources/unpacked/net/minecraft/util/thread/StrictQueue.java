package net.minecraft.util.thread;

import com.google.common.collect.Queues;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;

public interface StrictQueue<T extends Runnable> {
    @Nullable
    Runnable pop();

    boolean push(T p_371943_);

    boolean isEmpty();

    int size();

    public static final class FixedPriorityQueue implements StrictQueue<StrictQueue.RunnableWithPriority> {
        private final Queue<Runnable>[] queues;
        private final AtomicInteger size = new AtomicInteger();

        public FixedPriorityQueue(int p_18773_) {
            this.queues = new Queue[p_18773_];

            for (int i = 0; i < p_18773_; i++) {
                this.queues[i] = Queues.newConcurrentLinkedQueue();
            }
        }

        @Nullable
        @Override
        public Runnable pop() {
            for (Queue<Runnable> queue : this.queues) {
                Runnable runnable = queue.poll();
                if (runnable != null) {
                    this.size.decrementAndGet();
                    return runnable;
                }
            }

            return null;
        }

        public boolean push(StrictQueue.RunnableWithPriority p_371717_) {
            int i = p_371717_.priority;
            if (i < this.queues.length && i >= 0) {
                this.queues[i].add(p_371717_);
                this.size.incrementAndGet();
                return true;
            } else {
                throw new IndexOutOfBoundsException(String.format(Locale.ROOT, "Priority %d not supported. Expected range [0-%d]", i, this.queues.length - 1));
            }
        }

        @Override
        public boolean isEmpty() {
            return this.size.get() == 0;
        }

        @Override
        public int size() {
            return this.size.get();
        }
    }

    public static final class QueueStrictQueue implements StrictQueue<Runnable> {
        private final Queue<Runnable> queue;

        public QueueStrictQueue(Queue<Runnable> p_18792_) {
            this.queue = p_18792_;
        }

        @Nullable
        @Override
        public Runnable pop() {
            return this.queue.poll();
        }

        @Override
        public boolean push(Runnable p_371489_) {
            return this.queue.add(p_371489_);
        }

        @Override
        public boolean isEmpty() {
            return this.queue.isEmpty();
        }

        @Override
        public int size() {
            return this.queue.size();
        }
    }

    public static record RunnableWithPriority(int priority, Runnable task) implements Runnable {
        @Override
        public void run() {
            this.task.run();
        }
    }
}
