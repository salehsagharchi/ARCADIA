package Game.Network;

import java.util.concurrent.*;

/**
 * @author SALEHSAGHARCHI
 * Date: 2018-07-17
 * Time: 4:09 AM
 */
public class TimeLimitedCode {
    public static void runWithTimeout(final Runnable runnable, long timeout, TimeUnit timeUnit) throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                runnable.run();
                return "Ready!";
            }
        });
        try {
            //System.out.println("Started..");
            future.get(timeout, timeUnit);
            //System.out.println("Finished!");
        } catch (TimeoutException e) {
            future.cancel(true);
            //System.out.println("Terminated!");
        }
        executor.shutdownNow();
    }
}
