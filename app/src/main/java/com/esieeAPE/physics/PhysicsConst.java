/**
 * ESIEE OpenSource Project : OpenGL
 * <p/>
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.esieeAPE.physics;

/**
 * Global mWorld mPhysic constants
 *
 * @author Jonathan
 */
public class PhysicsConst {

    public static int WORLD_TIME_LOOP = 40; // m second	<10 Attention
    public static int WORLD_PCT_TIME_SPEED = 100;

    public static int WORLD_PCT_GRAVITY = 100;

    public static int REAL_LOOP_TIME = (int) (WORLD_TIME_LOOP * (WORLD_PCT_TIME_SPEED * 1.0) / 100.0);

    public static boolean HIGH_CAMERA_SPEED_TRANSLATION = false;

}
