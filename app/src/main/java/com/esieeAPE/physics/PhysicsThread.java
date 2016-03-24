/**
 * ESIEE OpenSource Project : OpenGL
 * <p/>
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.esieeAPE.physics;

/**
 * Define thread behavior with time regulation
 *
 * @author Jonathan
 */
public class PhysicsThread extends Thread {

    public boolean isRunning = true;
    private final IPhysicsThreadContent mContent;
    private final int mTimeLoop;

    public PhysicsThread(IPhysicsThreadContent content, int timeLoop) {
        this.mContent = content;
        this.mTimeLoop = timeLoop;
    }

    @Override
    public void run() {
        try {
            while (isRunning) {
                final long tmp_time = System.currentTimeMillis();

                // Execute mPhysic
                mContent.execute();

                // World Speed
                long time_sleep;
                if ((time_sleep = mTimeLoop - (System.currentTimeMillis() - tmp_time)) > 0) {
                    sleep(time_sleep);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
