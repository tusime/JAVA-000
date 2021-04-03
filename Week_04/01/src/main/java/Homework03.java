
import org.junit.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 思考有多少种方式，在main函数启动一个新线程或线程池，
 * 异步运行一个方法，拿到这个方法的返回值后，退出主线程
 *
 */
public class Homework03 {

    public static void main(String[] args) {

        long start = System.currentTimeMillis();
        // 创建线程或线程池，异步执行方法
        int result = sum();

        // 确保拿到 result 后输出 24157817
        System.out.println("异步计算结果为："+result);
        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");

        // 然后退出main线程
    }

    /**
     * 斐波那契数列（Fibonacci sequence），又称黄金分割数列。指这样一个数列：1、1、2、3、5、8、13、21、34、……
     * 从第3个数开始，每个数都等于它前两个数的和。
     * 输入n获取斐波那契数列的第n个数的值。
     */
    private static int sum() {
        return fibo(36);
    }

    private static int fibo2(int a) {
        if ( a < 2) {
            return 1;
        }
        return fibo2(a-1) + fibo2(a-2);
    }
    public static int fibo(int n) {
        if(n < 2) {
            return 1;
        }
        int n1 = 1, n2 = 1, sn = 0;
        for(int i = 0; i < n - 1; i ++){
            sn = n1 + n2;
            n1 = n2;
            n2 = sn;
        }
        return sn;
    }
    /* ------------------------------------------homework------------------------------------------*/

    /**
     * 1 循环等待获取结果
     */
    @Test
    public void test_while() {
        long start = System.currentTimeMillis();
        AtomicInteger result = new AtomicInteger();
        AtomicBoolean flag = new AtomicBoolean();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                result.set(sum());
                flag.set(true);
            }
        });
        thread.start();
        while(!flag.get()) {
            //当前线程从 Running 退回到 Runnable 状态
            Thread.yield();
            //sleep(100);
        }
        System.out.println("异步计算结果为：" + result);
        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }
    /**
     * 2 主线程join等待获取结果
     */
    @Test
    public void test_join() throws InterruptedException {
        long start = System.currentTimeMillis();
        AtomicInteger result = new AtomicInteger();
        Thread thread = new Thread(() -> {
            result.set(sum());
        });
        thread.start();
        thread.join();
        System.out.println("异步计算结果为：" + result);
        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }
    /**
     * 3 Callable Future
     */
    @Test
    public void test_future() throws InterruptedException, ExecutionException, TimeoutException {
        long start = System.currentTimeMillis();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Callable<Integer> task = () -> {
            return sum();
        };
        Future<Integer> future = executorService.submit(task);
        System.out.println("异步计算结果为：" + future.get(1000, TimeUnit.MILLISECONDS));
        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }
    /**
     * 4 FutureTask
     */
    @Test
    public void test_futureTask() throws InterruptedException, ExecutionException {
        long start = System.currentTimeMillis();
        FutureTask<Integer> futureTask = new FutureTask<>(Homework03::sum);
        new Thread(futureTask).start();
        System.out.println("异步计算结果为：" + futureTask.get());
        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }
    /**
     * 5 CompletableFuture
     */
    @Test
    public void test_completableFuture() throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();
        CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(()->{return sum();});
        System.out.println("异步计算结果为：" + completableFuture.get());
        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }
    /**
     * 6 wait notify
     */
    @Test
    public void test_waitNotify() throws InterruptedException {
        long start = System.currentTimeMillis();
        AtomicInteger result = new AtomicInteger();
        Thread thread = new Thread(() -> {
            synchronized(Thread.class) {
                result.set(sum());
                Thread.class.notifyAll();
            }
        });
        thread.start();
        synchronized(Thread.class) {
            Thread.class.wait();
        }
        System.out.println("异步计算结果为：" + result);
        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }
    /**
     * 7 Lock Condition
     */
    @Test
    public void test_lock() throws InterruptedException {
        long start = System.currentTimeMillis();
        AtomicInteger result = new AtomicInteger();
        Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        Thread thread = new Thread(() -> {
            try {
                lock.lock();
                result.set(sum());
                condition.signalAll();
            } finally {
                lock.unlock();
            }
        });
        thread.start();
        try {
            lock.lock();
            condition.await();
        } finally {
            lock.unlock();
        }
        System.out.println("异步计算结果为：" + result);
        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }
    /**
     * 8 Semaphore
     */
    @Test
    public void test_semaphore() throws InterruptedException {
        long start = System.currentTimeMillis();
        AtomicInteger result = new AtomicInteger();
        Semaphore semaphore = new Semaphore(1);
        semaphore.acquire();
        new Thread(() -> {
            try {
                result.set(sum());
            } finally {
                semaphore.release();
            }
        }).start();
        semaphore.acquire();
        System.out.println("异步计算结果为：" + result);
        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }
    /**
     * 9 CountDownLatch
     */
    @Test
    public void test_countDownLatch() throws InterruptedException {
        long start = System.currentTimeMillis();
        AtomicInteger result = new AtomicInteger();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        new Thread(() -> {
            result.set(sum());
            countDownLatch.countDown();
        }).start();
        // 主线程等待，直到计数器至0，防止线程一直阻塞设置超时时间
        countDownLatch.await(1000, TimeUnit.MILLISECONDS);
        System.out.println("异步计算结果为：" + result);
        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }
    /**
     * 10 CyclicBarrier
     */
    @Test
    public void test_cyclicBarrier() throws InterruptedException, BrokenBarrierException, TimeoutException {
        long start = System.currentTimeMillis();
        AtomicInteger result = new AtomicInteger();
        CyclicBarrier cyclicBarrier = new CyclicBarrier(2,() -> {
            // 计数达到设定值2后先执行回调，再执行主线程
            System.out.println("异步计算结果为：" + result);
        });
        Thread thread = new Thread(() -> {
            result.set(sum());
            try {
                // 线程等待，直到计数达到设定值2，防止线程一直阻塞设置超时时间
                cyclicBarrier.await(1000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException | BrokenBarrierException | TimeoutException e) {
                e.printStackTrace();
            }
        });
        new Thread(thread).start();
        // 主线程等待，直到计数达到设定值2，防止线程一直阻塞设置超时时间
        cyclicBarrier.await(1000, TimeUnit.MILLISECONDS);
        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
        // thread.start()不能重复执行
        new Thread(thread).start();
        cyclicBarrier.await(2000, TimeUnit.MILLISECONDS);
        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }
    /**
     * 11 LockSupport
     */
    @Test
    public void test_lockSupport() {
        long start = System.currentTimeMillis();
        AtomicInteger result = new AtomicInteger();
        MyThread myThread = new MyThread(Thread.currentThread(), result);
        myThread.start();
        LockSupport.park();
        System.out.println("异步计算结果为：" + result);
        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }
    static class MyThread extends Thread {
        Thread thread;
        AtomicInteger result;
        public MyThread(Thread thread, AtomicInteger result) {
            this.thread = thread;
            this.result = result;
        }
        @Override
        public void run() {
            result.set(sum());
            LockSupport.unpark(thread);
        }
    }
    /**
     * 12 BlockingQueue SynchronousQueue
     */
    @Test
    public void test_linkedBlockingQueue() throws InterruptedException {
        long start = System.currentTimeMillis();
        BlockingQueue<Integer> blockingQueue = new LinkedBlockingQueue<>();
        //BlockingQueue<Integer> blockingQueue = new ArrayBlockingQueue<>(1);
        //BlockingDeque<Integer> blockingDeque = new LinkedBlockingDeque<>();
        //SynchronousQueue synchronousQueue = new SynchronousQueue();
        new Thread(() -> {
            try {
                blockingQueue.put(sum());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        System.out.println("异步计算结果为：" + blockingQueue.take());
        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }
}

