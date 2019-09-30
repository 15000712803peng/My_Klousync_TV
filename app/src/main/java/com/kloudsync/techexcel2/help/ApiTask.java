package com.kloudsync.techexcel2.help;

import com.ub.service.activity.ThreadManager;

public class ApiTask {

    public ApiTask(Runnable task) {
        this.task = task;
    }

    Runnable task;

    public void start(ThreadManager threadManager) {
        if (task != null) {
            threadManager.execute(task);
        }
    }
}
