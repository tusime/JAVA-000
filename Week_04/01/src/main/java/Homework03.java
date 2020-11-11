
import org.junit.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 本周作业：（必做）思考有多少种方式，在main函数启动一个新线程或线程池，
 * 异步运行一个方法，拿到这个方法的返回值后，退出主线程？
 * 写出你的方法，越多越好，提交到github。
 *
 * 一个简单的代码参考：
 */
public class Homework03 {

    public static void main(String[] args) throws InterruptedException {

        long start=System.currentTimeMillis();
        // 在这里创建一个线程或线程池，
        // 异步执行 下面方法

        int result = sum(); //这是得到的返回值

        // 确保拿到 result 后输出 24157817
        System.out.println("异步计算结果为："+result);
        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");

        // 然后退出main线程
    }

    private static int sum() {
        return fibo(36);
    }

    private static int fibo(int a) {
        if ( a < 2) {
            return 1;
        }
        return fibo(a-1) + fibo(a-2);
    }

    /* ------------------------------------------homework------------------------------------------*/

    @Test
    public void test_while() throws InterruptedException {
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
            Thread.sleep(100);
        }
        System.out.println("异步计算结果为：" + result);
        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }

    @Test
    public void test_join() throws InterruptedException {
        long start = System.currentTimeMillis();
        AtomicInteger result = new AtomicInteger();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                result.set(sum());
            }
        });
        thread.start();
        thread.join();
        System.out.println("异步计算结果为：" + result);
        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }

    @Test
    public void test_futureTask() throws InterruptedException, ExecutionException {
        long start = System.currentTimeMillis();
        FutureTask<Integer> futureTask = new FutureTask<Integer>(()->sum());
        new Thread(futureTask).start();
        System.out.println("异步计算结果为：" + futureTask.get());
        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }

    @Test
    public void test_completableFuture() throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();
        CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(()->{return sum();});
        System.out.println("异步计算结果为：" + completableFuture.get());
        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }

    @Test
    public void test_executorService() throws InterruptedException, ExecutionException {
        long start = System.currentTimeMillis();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Future<Integer> future = executorService.submit(()->sum());
        System.out.println("异步计算结果为：" + future.get());
        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }

    @Test
    public void test_synchronized() throws InterruptedException {
        long start = System.currentTimeMillis();
        AtomicInteger result = new AtomicInteger();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized(this) {
                    result.set(sum());
                }
            }
        });
        thread.start();
        Thread.sleep(200);
        synchronized(this) {}
        System.out.println("异步计算结果为：" + result);
        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }

    @Test
    public void test_lock() throws InterruptedException {
        long start = System.currentTimeMillis();
        AtomicInteger result = new AtomicInteger();
        Lock lock = new ReentrantLock();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lock.lock();
                    result.set(sum());
                } finally {
                    lock.unlock();
                }
            }
        });
        thread.start();
        Thread.sleep(100);
        try {
            lock.lock();
        } finally {
            lock.unlock();
        }
        System.out.println("异步计算结果为：" + result);
        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }

    @Test
    public void test_semaphore() throws InterruptedException {
        long start = System.currentTimeMillis();
        AtomicInteger result = new AtomicInteger();
        Semaphore semaphore = new Semaphore(2);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    semaphore.acquire();
                    result.set(sum());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    semaphore.release();
                }
            }
        });
        thread.start();
        Thread.sleep(100);
        try {
            semaphore.acquire();
        } finally {
            semaphore.release();
        }
        System.out.println("异步计算结果为：" + result);
        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
    }

//    @Test
//    public void test_countDownLatch() throws InterruptedException {
//        long start = System.currentTimeMillis();
//        AtomicInteger result = new AtomicInteger();
//        CountDownLatch countDownLatch = new CountDownLatch(2);
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                result.set(sum());
//                countDownLatch.countDown();
//            }
//        });
//        thread.start();
//        //主线程等待，直到锁存器倒计数至零
//        countDownLatch.await();
//        System.out.println("异步计算结果为：" + result);
//        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
//    }

//    @Test
//    public void test_cyclicBarrier() throws InterruptedException {
//        long start = System.currentTimeMillis();
//        AtomicInteger result = new AtomicInteger();
//        System.out.println("异步计算结果为：" + result);
//        System.out.println("使用时间："+ (System.currentTimeMillis()-start) + " ms");
//    }
}

