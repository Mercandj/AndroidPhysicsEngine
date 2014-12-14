/**
 * ESIEE OpenSource Project : OpenGL
 *
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.esieeAPE;

import android.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.esieeAPE.physics.PhysicsEngine;

/**
 * OpenGL Fragment : display GLSurfaceView
 * @author Jonathan
 *
 */
public class GLFragment extends Fragment implements SensorEventListener {

	public MyGLSurfaceView mGLView;
	public SensorManager mSensorManager;
	public Sensor mRotation;
    
    public View rootView;
    
    public static TextView fps; // static because accept thread (myRenderer) access
    public static ImageView modeControlIcon;
    
    public ProgressBar wait;
    public static RelativeLayout wait_rl; // static because accept thread (myRenderer) access
    public static ProgressBar progressBar; // static because accept thread (myRenderer) access
    public static Button forward, back, left, right;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
        rootView = inflater.inflate(R.layout.glview, container, false);
        wait_rl = (RelativeLayout) rootView.findViewById(R.id.wait_rl);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        
        Animation fun_zoom_in = AnimationUtils.loadAnimation(getActivity(), R.anim.fun_zoom_in);
        ((RelativeLayout) rootView.findViewById(R.id.rl_center)).startAnimation(fun_zoom_in);
        Animation fun_open_buttom = AnimationUtils.loadAnimation(getActivity(), R.anim.fun_open_buttom);
        ((RelativeLayout) rootView.findViewById(R.id.rl_center_buttom)).startAnimation(fun_open_buttom);
            
        mGLView = (MyGLSurfaceView) rootView.findViewById(R.id.GLview);
        modeControlIcon = (ImageView) rootView.findViewById(R.id.modeControlIcon);
        updateModeControlIcon(MainActivity.config.getControlMode());
        
        final DisplayMetrics displayMetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		mGLView.setDensity(displayMetrics.density);
        
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mRotation =  mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        
        Font.applyFont(getActivity(), ((TextView) rootView.findViewById(R.id.about)), "fonts/MYRIADAB.TTF");
        Font.applyFont(getActivity(), ((TextView) rootView.findViewById(R.id.fps)), "fonts/MYRIADAB.TTF");
        
        Font.applyFont(getActivity(), ((TextView) rootView.findViewById(R.id.name)), "fonts/MYRIADAB.TTF");
        Font.applyFont(getActivity(), ((TextView) rootView.findViewById(R.id.name_full)), "fonts/MYRIADAB.TTF");
        Font.applyFont(getActivity(), ((TextView) rootView.findViewById(R.id.author_left)), "fonts/MYRIADAB.TTF");
        Font.applyFont(getActivity(), ((TextView) rootView.findViewById(R.id.author_right)), "fonts/MYRIADAB.TTF");
        
        fps = (TextView) rootView.findViewById(R.id.fps);
        wait = (ProgressBar) rootView.findViewById(R.id.wait);
        
        forward = (Button) rootView.findViewById(R.id.forward);
        forward.setVisibility(View.GONE);
        forward.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_UP)
					mGLView.mRenderer.camera.forward = false;
            	else if(event.getAction()==MotionEvent.ACTION_DOWN)
            		mGLView.mRenderer.camera.forward = true;				
				return false;
			}
        });
        
        back = (Button) rootView.findViewById(R.id.back);
        back.setVisibility(View.GONE);
        back.setOnTouchListener(new OnTouchListener() {
        	@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_UP)
					mGLView.mRenderer.camera.back = false;
            	else if(event.getAction()==MotionEvent.ACTION_DOWN)
            		mGLView.mRenderer.camera.back = true;				
				return false;
			}
        });
        
        left = (Button) rootView.findViewById(R.id.left);
        left.setVisibility(View.GONE);
        left.setOnTouchListener(new OnTouchListener() {
        	@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_UP)
					mGLView.mRenderer.camera.left = false;
            	else if(event.getAction()==MotionEvent.ACTION_DOWN)
            		mGLView.mRenderer.camera.left = true;				
				return false;
			}
        });
        
        right = (Button) rootView.findViewById(R.id.right);
        right.setVisibility(View.GONE);
        right.setOnTouchListener(new OnTouchListener() {
        	@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_UP)
					mGLView.mRenderer.camera.right = false;
            	else if(event.getAction()==MotionEvent.ACTION_DOWN)
            		mGLView.mRenderer.camera.right = true;				
				return false;
			}
        });
        
        ((ImageView) rootView.findViewById(R.id.circleButton)).setOnTouchListener(new OnTouchListener() {
        	@Override
			public boolean onTouch(View v, MotionEvent event) {        		
				if(event.getAction()==MotionEvent.ACTION_DOWN) {
					PhysicsEngine.actionButton = true;
					return true;
				}
				else if(event.getAction()==MotionEvent.ACTION_UP) {
					PhysicsEngine.actionButton = false;											
				}
				return false;
			}
        });
        
        return rootView;
    }

    public static void updateModeControlIcon(int value) {
    	switch(value) {
    	case 0:
			GLFragment.modeControlIcon.setImageResource(R.drawable.android);
			break;
		case 1:
			GLFragment.modeControlIcon.setImageResource(R.drawable.shuttle);
			break;
		case 2:
			GLFragment.modeControlIcon.setImageResource(R.drawable.car);
			break;
		}
    }
    
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {		
		//Log.v("mysensors: ", Float.toString(event.values[0]) +","+ Float.toString(event.values[1]) +","+ Float.toString(event.values[2]));
		//((MyGLSurfaceView)mGLView).mRenderer.myobj.color[0]= lux;
		if ( (((MyGLSurfaceView)mGLView).mRenderer).camera.mEye != null) {
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
