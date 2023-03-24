package top.iseason.bukkittemplate;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 在插件 onDisable 时自动调用 onDisable() 方法
 */
public final class DisableHook {

    private static final Queue<DisableTask> tasks = new ConcurrentLinkedQueue<>();

    /**
     * 运行所有注册的任务，会在插件 onDisable 时自动调用
     */
    public static void disableAll() {
        for (DisableTask task : tasks) {
            try {
                task.onDisable();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        tasks.clear();
    }

    /**
     * 添加一个在插件onDisable 时调用的任务
     *
     * @param task 任务
     */
    public static void addTask(DisableTask task) {
        tasks.add(task);
    }

    public interface DisableTask {
        void onDisable();
    }
}
