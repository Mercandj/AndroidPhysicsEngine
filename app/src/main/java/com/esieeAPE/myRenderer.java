/**
 * ESIEE OpenSource Project : OpenGL
 * <p/>
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
 * OpenGL Renderer : instantiate the camera and the world
 * Define what is draw
 *
 * @author Jonathan
 */
public class myRenderer implements GLSurfaceView.Renderer {

    public World world;
    public float mWidth, mHeight;
    public Camera camera;
    public long time, fps, tmp_time, tmp_fps; // fps measure
    Context context;
    MyGLSurfaceView mGLView;
    PhysicsEngine physicEngine;
    Handler handler = new Handler();
    private float[] mMVPMatrix = new float[16];
    private float[] mProjMatrix = new float[16];
    private float[] mVMatrix = new float[16];

    public myRenderer(Context context, MyGLSurfaceView mGLView) {
        this.context = context;
        this.mGLView = mGLView;
        this.camera = new Camera(context);
        this.world = new World(context, camera);
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // Set the background frame color
        GLES30.glClearColor(Const.BACKGROUD_COLOR.x, Const.BACKGROUD_COLOR.y, Const.BACKGROUD_COLOR.z, Const.BACKGROUD_COLOR.w);
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        GLES30.glDepthFunc(GLES30.GL_LESS);

        this.world.init();
        this.camera.init();

        physicEngine = new PhysicsEngine(context, mGLView);

        handler.post(new Runnable() { // Access UIThred
            public void run() {
                GLFragment.wait_rl.setVisibility(View.GONE);
                GLFragment.forward.setVisibility(View.VISIBLE);
                GLFragment.back.setVisibility(View.VISIBLE);
                GLFragment.left.setVisibility(View.VISIBLE);
                GLFragment.right.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT);

        camera.look(mVMatrix);

        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
        world.draw(mProjMatrix, mVMatrix);

        GLES30.glFlush();

        // fps measure and display
        tmp_fps++;
        if ((tmp_time = (System.currentTimeMillis() - time)) > 1000) {
            handler.post(new Runnable() { // Access UIThred
                public void run() {
                    String txt_display = "";
                    if (MainActivity.config.isDisplayPosition())
                        txt_display += "pos" + camera.mEye + " \t";
                    if (MainActivity.config.isDisplayFPS())
                        txt_display += "time = " + tmp_time + "\t  fps = " + fps + " fps";
                    GLFragment.fps.setText(txt_display);
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

        Matrix.perspectiveM(mProjMatrix, 0, camera.fovy, ratio, camera.zNear, camera.zFar);
        mWidth = width;
        mHeight = height;
    }
}