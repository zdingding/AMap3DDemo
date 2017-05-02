package com.amap.map3d.demo;

/**
 * Created by yys on 2017/4/10.
 */

public class Clock {

    private int a = 0;
    private int b;
    private Runnable c;

    public Clock(int paramInt, Runnable paramRunnable)
    {
        this.b = paramInt;
        this.c = paramRunnable;
    }

    public void doWork()
    {
        doWork(false);
    }

    public void doWork(boolean paramBoolean)
    {
        if (this.a % this.b == 0) {
            this.c.run();
        }
        this.a += 1;
    }

    public void reset()
    {
        this.a = 0;
    }
}
