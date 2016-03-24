/**
 * ESIEE OpenSource Project : OpenGL
 * <p/>
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.esieeAPE.objects;

import android.opengl.Matrix;

import com.esieeAPE.lib.Vector3D;

/**
 * "Main" Camera used as the principal view
 *
 * @author Jonathan
 */
public class Camera {

    public boolean forward = false;
    public boolean back = false;
    public boolean left = false;
    public boolean right = false;

    public Vector3D mEye;
    public Vector3D mForward;
    public Vector3D mUp;

    /**
     * Specifies the field of view angle, in degrees, in the y direction.
     */
    public float mFovy;

    /**
     * Specifies the distance from the viewer to the near clipping plane (always positive).
     */
    public float mZNear;

    /**
     * Specifies the distance from the viewer to the far clipping plane (always positive).
     */
    public float mZFar;

    float vx;
    float vy;

    public Camera() {
    }

    public void init() {
        mEye = new Vector3D(0, 2, 2);
        mForward = new Vector3D(0, 0, -1);
        mUp = new Vector3D(0, 1, 0);
        mFovy = 90;
        mZNear = 0.1f;
        mZFar = 150;
    }

    public void look(float[] mVMatrix) {
        Matrix.setLookAtM(mVMatrix, 0,
                mEye.dX, mEye.dY, mEye.dZ,
                mEye.dX + mForward.dX, mEye.dY + mForward.dY, mEye.dZ + mForward.dZ,
                mUp.dX, mUp.dY, mUp.dZ);
    }

    public void setView(float dx, float dy) {
        vx += dx;
        vy += dy;
        computeForward();
    }

    public void computeForward() {
        if (mForward == null) {
            return;
        }
        // Change reference.
        mForward.dX = (float) Math.sin(vx) * (float) Math.cos(vy);
        mForward.dY = (float) Math.sin(vy);
        mForward.dZ = -(float) Math.cos(vx) * (float) Math.cos(vy);
        mForward.normalize();
    }

    /**
     * Specifies the field of view angle, in degrees, in the y direction.
     */
    public void setFovy(float fovy) {
        mFovy = fovy;
    }
}
