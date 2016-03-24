/**
 * ESIEE OpenSource Project : OpenGL
 * <p>
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.esieeAPE;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Handler;
import android.view.View;

import com.esieeAPE.implementation.World;
import com.esieeAPE.objects.Camera;
import com.esieeAPE.physics.PhysicsEngine;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * OpenGL Renderer : instantiate the mCamera and the mWorld
 * Define what is draw
 *
 * @author Jonathan
 */
public class OpenGLRenderer implements GLSurfaceView.Renderer {

    private final OpenGLSurfaceView mGLView;
    public final Camera mCamera;
    private final World mWorld;

    public float mWidth, mHeight;
    public long time, fps, tmp_time, tmp_fps; // fps measure
    PhysicsEngine mPhysicEngine;
    Handler handler = new Handler();
    private float[] mMVPMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];
    private float[] mVMatrix = new float[16];

    public OpenGLRenderer(final Context context, final OpenGLSurfaceView openGLSurfaceView) {
        mGLView = openGLSurfaceView;
        mCamera = new Camera();
        mWorld = new World(context, mCamera);
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // Set the background frame color
        GLES30.glClearColor(Const.BACKGROUD_COLOR.x, Const.BACKGROUD_COLOR.y, Const.BACKGROUD_COLOR.z, Const.BACKGROUD_COLOR.w);
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        GLES30.glDepthFunc(GLES30.GL_LESS);

        mWorld.init();

        mPhysicEngine = new PhysicsEngine(mGLView);

        handler.post(new Runnable() { // Access UIThred
            public void run() {
                OpenGLFragment.wait_rl.setVisibility(View.GONE);
                OpenGLFragment.forward.setVisibility(View.VISIBLE);
                OpenGLFragment.back.setVisibility(View.VISIBLE);
                OpenGLFragment.left.setVisibility(View.VISIBLE);
                OpenGLFragment.right.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT);

        mCamera.look(mVMatrix);

        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mVMatrix, 0);
        mWorld.draw(mProjectionMatrix, mVMatrix);

        GLES30.glFlush();

        // fps measure and display
        tmp_fps++;
        if ((tmp_time = (System.currentTimeMillis() - time)) > 1000) {
            handler.post(new Runnable() { // Access UIThred
                public void run() {
                    String txt_display = "";
                    if (MainActivity.config.isDisplayPosition())
                        txt_display += "pos" + mCamera.mEye + " \t";
                    if (MainActivity.config.isDisplayFPS())
                        txt_display += "time = " + tmp_time + "\t  fps = " + fps + " fps";
                    OpenGLFragment.fps.setText(txt_display);
                }
            });
            fps = tmp_fps;
            tmp_fps = 0;
            time = System.currentTimeMillis();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
        float ratio = (float) width / height;

        Matrix.perspectiveM(mProjectionMatrix, 0, mCamera.mFovy, ratio, mCamera.mZNear, mCamera.mZFar);
        mWidth = width;
        mHeight = height;
    }

    public World getWorld() {
        return mWorld;
    }
}