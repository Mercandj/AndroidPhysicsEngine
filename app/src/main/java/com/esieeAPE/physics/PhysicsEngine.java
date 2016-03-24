/**
 * ESIEE OpenSource Project : OpenGL
 * <p/>
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.esieeAPE.physics;

import android.content.Context;

import com.esieeAPE.Const;
import com.esieeAPE.OpenGLRenderer;
import com.esieeAPE.OpenGLSurfaceView;
import com.esieeAPE.implementation.World;
import com.esieeAPE.lib.Vector3D;
import com.esieeAPE.objects.Camera;

/**
 * Define the mPhysic's thread behavior
 *
 * @author Jonathan
 */
public class PhysicsEngine {

    public static boolean actionButton = false;
    public PhysicsThread thread;
    OpenGLSurfaceView mGLView;
    IPhysicsThreadContent threadContent;

    public PhysicsEngine(final OpenGLSurfaceView mGLView) {
        this.mGLView = mGLView;

        threadContent = new IPhysicsThreadContent() {

            @Override
            public void execute() {

                final OpenGLRenderer renderer = mGLView.mRenderer;
                final World world = renderer.getWorld();

                // Compute Forces to mEntities and Object3D
                world.computeForces(world);

                // Apply Forces to mEntities and Object3D
                world.applyForces(world);

                // Bot or repeated moves
                world.translateRepeatedWayPosition();

                // Camera Controls
                final Camera camera = renderer.mCamera;
                if (camera.forward) {

                    float mouveX = camera.mForward.dX / (PhysicsConst.HIGH_CAMERA_SPEED_TRANSLATION ? 2.0f : 8.0f);
                    if ((mouveX < 0 && -Const.LIMIT < camera.mEye.dX) || (mouveX > 0 && camera.mEye.dX < Const.LIMIT)) {
                        camera.mEye.dX += mouveX;
                    }

                    float mouveY = camera.mForward.dY / (PhysicsConst.HIGH_CAMERA_SPEED_TRANSLATION ? 2.0f : 8.0f);
                    if ((mouveY < 0 && 0 < camera.mEye.dY + mouveY) || (mouveY > 0 && camera.mEye.dY < Const.LIMIT)) {
                        camera.mEye.dY += mouveY;
                    }

                    float mouveZ = camera.mForward.dZ / (PhysicsConst.HIGH_CAMERA_SPEED_TRANSLATION ? 2.0f : 8.0f);
                    if ((mouveZ < 0 && -Const.LIMIT < camera.mEye.dZ) || (mouveZ > 0 && camera.mEye.dZ < Const.LIMIT)) {
                        camera.mEye.dZ += mouveZ;
                    }

                    camera.computeForward();
                } else if (camera.back) {

                    float mouveX = -camera.mForward.dX / (PhysicsConst.HIGH_CAMERA_SPEED_TRANSLATION ? 2.0f : 8.0f);
                    if ((mouveX < 0 && -Const.LIMIT < camera.mEye.dX) || (mouveX > 0 && camera.mEye.dX < Const.LIMIT)) {
                        camera.mEye.dX += mouveX;
                    }

                    float mouveY = -camera.mForward.dY / (PhysicsConst.HIGH_CAMERA_SPEED_TRANSLATION ? 2.0f : 8.0f);
                    if ((mouveY < 0 && 0 < camera.mEye.dY + mouveY) || (mouveY > 0 && camera.mEye.dY < Const.LIMIT)) {
                        camera.mEye.dY += mouveY;
                    }

                    float mouveZ = -camera.mForward.dZ / (PhysicsConst.HIGH_CAMERA_SPEED_TRANSLATION ? 2.0f : 8.0f);
                    if ((mouveZ < 0 && -Const.LIMIT < camera.mEye.dZ) || (mouveZ > 0 && camera.mEye.dZ < Const.LIMIT)) {
                        camera.mEye.dZ += mouveZ;
                    }

                    camera.computeForward();
                } else if (camera.right) {

                    final Vector3D tmp = camera.mForward.cross(camera.mUp);

                    float mouveX = tmp.dX / (PhysicsConst.HIGH_CAMERA_SPEED_TRANSLATION ? 1.9f : 7.0f);
                    if ((mouveX < 0 && -Const.LIMIT < camera.mEye.dX) || (mouveX > 0 && camera.mEye.dX < Const.LIMIT)) {
                        camera.mEye.dX += mouveX;
                    }

                    float mouveY = tmp.dY / (PhysicsConst.HIGH_CAMERA_SPEED_TRANSLATION ? 1.9f : 7.0f);
                    if ((mouveY < 0 && 0 < camera.mEye.dY + mouveY) || (mouveY > 0 && camera.mEye.dY < Const.LIMIT)) {
                        camera.mEye.dY += mouveY;
                    }

                    float mouveZ = tmp.dZ / (PhysicsConst.HIGH_CAMERA_SPEED_TRANSLATION ? 1.9f : 7.0f);
                    if ((mouveZ < 0 && -Const.LIMIT < camera.mEye.dZ) || (mouveZ > 0 && camera.mEye.dZ < Const.LIMIT)) {
                        camera.mEye.dZ += mouveZ;
                    }

                } else if (camera.left) {

                    final Vector3D tmp = camera.mUp.cross(camera.mForward);

                    float mouveX = tmp.dX / (PhysicsConst.HIGH_CAMERA_SPEED_TRANSLATION ? 1.9f : 7.0f);
                    if ((mouveX < 0 && -Const.LIMIT < camera.mEye.dX) || (mouveX > 0 && camera.mEye.dX < Const.LIMIT)) {
                        camera.mEye.dX += mouveX;
                    }

                    float mouveY = tmp.dY / (PhysicsConst.HIGH_CAMERA_SPEED_TRANSLATION ? 1.9f : 7.0f);
                    if ((mouveY < 0 && 0 < camera.mEye.dY + mouveY) || (mouveY > 0 && camera.mEye.dY < Const.LIMIT)) {
                        camera.mEye.dY += mouveY;
                    }

                    float mouveZ = tmp.dZ / (PhysicsConst.HIGH_CAMERA_SPEED_TRANSLATION ? 1.9f : 7.0f);
                    if ((mouveZ < 0 && -Const.LIMIT < camera.mEye.dZ) || (mouveZ > 0 && camera.mEye.dZ < Const.LIMIT)) {
                        camera.mEye.dZ += mouveZ;
                    }
                }

                mGLView.requestRender();
            }
        };

        thread = new PhysicsThread(threadContent, PhysicsConst.REAL_LOOP_TIME);
        thread.start();
    }
}
