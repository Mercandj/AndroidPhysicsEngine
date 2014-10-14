/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.physical.engine;

import com.android.physical.engine.MyGLSurfaceView;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class MainActivity extends Activity implements SensorEventListener {

    private GLSurfaceView mGLView;
    private SensorManager mSensorManager;
    private Sensor mRotation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity
        mGLView = new MyGLSurfaceView(this);
        setContentView(mGLView);
        
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mRotation =  mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // The following call pauses the rendering thread.
        // If your OpenGL application is memory intensive,
        // you should consider de-allocating objects that
        // consume significant memory here.
        mGLView.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The following call resumes a paused rendering thread.
        // If you de-allocated graphic objects for onPause()
        // this is a good place to re-allocate them.
        mGLView.onResume();
        mSensorManager.registerListener(this, mRotation, SensorManager.SENSOR_DELAY_NORMAL);
    }

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		Log.v("mysensors: ", Float.toString(event.values[0]) +","+ Float.toString(event.values[1]) +","+ Float.toString(event.values[2]));
		//((MyGLSurfaceView)mGLView).mRenderer.myobj.color[0]= lux;
		if ( (((MyGLSurfaceView)mGLView).mRenderer).mEye != null) {
			/*(((MyGLSurfaceView)mGLView).mRenderer.mEye.dX) += (event.values[0]/1000.0);
			(((MyGLSurfaceView)mGLView).mRenderer.mEye.dY) += (event.values[1]/1000.0);
			(((MyGLSurfaceView)mGLView).mRenderer.mEye.dZ) += (event.values[2]/1000.0);*/
			//(((MyGLSurfaceView)mGLView).mRenderer.myobj).translateM((float)(event.values[0]/10.0), 0, (float)(event.values[2]/10.0));
		    
			//SensorManager.getRotationMatrixFromVector(((MyGLSurfaceView)mGLView).mRenderer.myobj.transformationMatrix, event.values);
			//((WindowManager) getSystemService(WINDOW_SERVICE).mWindowManager.getDefaultDisplay()).;
			((MyGLSurfaceView)mGLView).requestRender();
		}
	}
}

class MyGLSurfaceView extends GLSurfaceView {

    public final MainRenderer mRenderer;
   
    public MyGLSurfaceView(Context context) {
        super(context);

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new MainRenderer(context);
        setEGLConfigChooser(8 , 8, 8, 8, 16, 0);
        setRenderer(mRenderer);
        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
                      
        //super(context, attrs, defStyle);
        //mIcon = context.getResources().getDrawable(R.drawable.icon);
        //mIcon.setBounds(0, 0, mIcon.getIntrinsicWidth(), mIcon.getIntrinsicHeight());
        
        // Create our ScaleGestureDetector
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());        
    }

    private float mPreviousX;
    private float mPreviousY;
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;
    private int mActivePointerId = -1;
    
    @Override
    public boolean onTouchEvent(MotionEvent e) {
    	mScaleDetector.onTouchEvent(e);
    	float x, y;
    	final int action = e.getAction(); 
        switch (action & MotionEvent.ACTION_MASK) {
	        case MotionEvent.ACTION_DOWN: {
	            mPreviousX = e.getX();
	            mPreviousY = e.getY();
	            mActivePointerId = e.getPointerId(0);
	            break;	
	        }        
            case MotionEvent.ACTION_MOVE: {
	                final int pointerIndex = e.findPointerIndex(mActivePointerId);
	                x = e.getX(pointerIndex);
	                y = e.getY(pointerIndex);
	                float dx = x - mPreviousX;
	                float dy = y - mPreviousY;
	                
	                if (!mScaleDetector.isInProgress()) {   
	                	mRenderer.setView(dx, dy);
	                	requestRender();
	                }
                mPreviousX = x;
                mPreviousY = y;
                break;
            }
            case MotionEvent.ACTION_UP: {
                mActivePointerId = -1;
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = -1;
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                final int pointerIndex = (e.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = e.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mPreviousX = e.getX(newPointerIndex);
                    mPreviousY = e.getY(newPointerIndex);
                    mActivePointerId = e.getPointerId(newPointerIndex);
                }
                break;
            }
        }
        return true;
    }
    
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor = detector.getScaleFactor();
  
            Log.v("mydebugger", Float.toString(mScaleFactor));
            if (mScaleFactor > 1) mScaleFactor = -1/mScaleFactor;
            mScaleFactor /= -4;
            mScaleFactor *= Math.sqrt(mRenderer.mEye.length())/10; 
            
            //mRenderer.mEye[2] *= (1 + mScaleFactor);
            mRenderer.mEye = mRenderer.mEye.plus(mRenderer.mForward.mult(mScaleFactor));
            
/*            mRenderer.mEye[0] += mRenderer.mForwarddirection[0]*mScaleFactor;
            mRenderer.mEye[1] += mRenderer.mForwarddirection[1]*mScaleFactor;
            mRenderer.mEye[2] += mRenderer.mForwarddirection[2]*mScaleFactor;
*/
            requestRender();
            return true;
        }
    }
}






