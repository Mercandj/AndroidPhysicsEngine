/**
 * ESIEE OpenSource Project : OpenGL
 *
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.esieeAPE.physics;

import android.content.Context;

import com.esieeAPE.Const;
import com.esieeAPE.MyGLSurfaceView;
import com.esieeAPE.lib.myVector3D;

/**
 * Define the physic's thread behavior
 * @author Jonathan
 *
 */
public class PhysicsEngine {
	
	final Context context;
	MyGLSurfaceView mGLView;
	public PhysicsThread thread;
	IPhysicsThreadContent threadContent;
	public static boolean actionButton = false;
	
	public PhysicsEngine(final Context context, final MyGLSurfaceView mGLView) {
		this.context = context;
		this.mGLView = mGLView;
		
		threadContent = new IPhysicsThreadContent() {
			
			@Override
			public void execute() {
				
				// Compute Forces to entities and myObject3D
				mGLView.mRenderer.world.computeForces(mGLView.mRenderer.world);
				// Apply Forces to entities and myObject3D
				mGLView.mRenderer.world.applyForces(mGLView.mRenderer.world);
				
				// Bot or repeated moves
				mGLView.mRenderer.world.translateRepetedWayPosition();
								
				// Camera Controls
				if(mGLView.mRenderer.camera.forward) {
					
					float mouveX = mGLView.mRenderer.camera.mForward.dX / (PhysicsConst.HIGH_CAMERA_SPEED_TRANSLATION?2.0f:8.0f);
					if( (mouveX < 0 && -Const.LIMIT < mGLView.mRenderer.camera.mEye.dX) || (mouveX > 0 && mGLView.mRenderer.camera.mEye.dX < Const.LIMIT) )
						mGLView.mRenderer.camera.mEye.dX += mouveX;
					
					float mouveY = mGLView.mRenderer.camera.mForward.dY / (PhysicsConst.HIGH_CAMERA_SPEED_TRANSLATION?2.0f:8.0f);
					if( (mouveY < 0 && 0 < mGLView.mRenderer.camera.mEye.dY + mouveY) || (mouveY > 0 && mGLView.mRenderer.camera.mEye.dY < Const.LIMIT) )
						mGLView.mRenderer.camera.mEye.dY += mouveY;
					
					float mouveZ = mGLView.mRenderer.camera.mForward.dZ / (PhysicsConst.HIGH_CAMERA_SPEED_TRANSLATION?2.0f:8.0f);
					if( (mouveZ < 0 && -Const.LIMIT < mGLView.mRenderer.camera.mEye.dZ) || (mouveZ > 0 && mGLView.mRenderer.camera.mEye.dZ < Const.LIMIT) )
						mGLView.mRenderer.camera.mEye.dZ += mouveZ;
					
					mGLView.mRenderer.camera.computeForward();
				}
				else if(mGLView.mRenderer.camera.back) {				
					
					float mouveX = -mGLView.mRenderer.camera.mForward.dX / (PhysicsConst.HIGH_CAMERA_SPEED_TRANSLATION?2.0f:8.0f);
					if( (mouveX < 0 && -Const.LIMIT < mGLView.mRenderer.camera.mEye.dX) || (mouveX > 0 && mGLView.mRenderer.camera.mEye.dX < Const.LIMIT) )
						mGLView.mRenderer.camera.mEye.dX += mouveX;
					
					float mouveY = -mGLView.mRenderer.camera.mForward.dY / (PhysicsConst.HIGH_CAMERA_SPEED_TRANSLATION?2.0f:8.0f);
					if( (mouveY < 0 && 0 < mGLView.mRenderer.camera.mEye.dY + mouveY) || (mouveY > 0 && mGLView.mRenderer.camera.mEye.dY < Const.LIMIT) )
						mGLView.mRenderer.camera.mEye.dY += mouveY;
					
					float mouveZ = -mGLView.mRenderer.camera.mForward.dZ / (PhysicsConst.HIGH_CAMERA_SPEED_TRANSLATION?2.0f:8.0f);
					if( (mouveZ < 0 && -Const.LIMIT < mGLView.mRenderer.camera.mEye.dZ) || (mouveZ > 0 && mGLView.mRenderer.camera.mEye.dZ < Const.LIMIT) )
						mGLView.mRenderer.camera.mEye.dZ += mouveZ;
					
					mGLView.mRenderer.camera.computeForward();
				}
				else if(mGLView.mRenderer.camera.right) {
					
					myVector3D tmp = mGLView.mRenderer.camera.mForward.cross(mGLView.mRenderer.camera.mUp);
					
					float mouveX = tmp.dX / (PhysicsConst.HIGH_CAMERA_SPEED_TRANSLATION?1.9f:7.0f);
					if( (mouveX < 0 && -Const.LIMIT < mGLView.mRenderer.camera.mEye.dX) || (mouveX > 0 && mGLView.mRenderer.camera.mEye.dX < Const.LIMIT) )
						mGLView.mRenderer.camera.mEye.dX += mouveX;
					
					float mouveY = tmp.dY / (PhysicsConst.HIGH_CAMERA_SPEED_TRANSLATION?1.9f:7.0f);
					if( (mouveY < 0 && 0 < mGLView.mRenderer.camera.mEye.dY + mouveY) || (mouveY > 0 && mGLView.mRenderer.camera.mEye.dY < Const.LIMIT) )
						mGLView.mRenderer.camera.mEye.dY += mouveY;
					
					float mouveZ = tmp.dZ / (PhysicsConst.HIGH_CAMERA_SPEED_TRANSLATION?1.9f:7.0f);
					if( (mouveZ < 0 && -Const.LIMIT < mGLView.mRenderer.camera.mEye.dZ) || (mouveZ > 0 && mGLView.mRenderer.camera.mEye.dZ < Const.LIMIT) )
						mGLView.mRenderer.camera.mEye.dZ += mouveZ;
				}
				else if(mGLView.mRenderer.camera.left) {					
					
					myVector3D tmp = mGLView.mRenderer.camera.mUp.cross(mGLView.mRenderer.camera.mForward);
					
					float mouveX = tmp.dX / (PhysicsConst.HIGH_CAMERA_SPEED_TRANSLATION?1.9f:7.0f);
					if( (mouveX < 0 && -Const.LIMIT < mGLView.mRenderer.camera.mEye.dX) || (mouveX > 0 && mGLView.mRenderer.camera.mEye.dX < Const.LIMIT) )
						mGLView.mRenderer.camera.mEye.dX += mouveX;
					
					float mouveY = tmp.dY / (PhysicsConst.HIGH_CAMERA_SPEED_TRANSLATION?1.9f:7.0f);
					if( (mouveY < 0 && 0 < mGLView.mRenderer.camera.mEye.dY + mouveY) || (mouveY > 0 && mGLView.mRenderer.camera.mEye.dY < Const.LIMIT) )
						mGLView.mRenderer.camera.mEye.dY += mouveY;
					
					float mouveZ = tmp.dZ / (PhysicsConst.HIGH_CAMERA_SPEED_TRANSLATION?1.9f:7.0f);
					if( (mouveZ < 0 && -Const.LIMIT < mGLView.mRenderer.camera.mEye.dZ) || (mouveZ > 0 && mGLView.mRenderer.camera.mEye.dZ < Const.LIMIT) )
						mGLView.mRenderer.camera.mEye.dZ += mouveZ;
				}
				
				mGLView.requestRender();				
			}
		};
		
		thread = new PhysicsThread(threadContent, PhysicsConst.REAL_LOOP_TIME );
		thread.start();
	}
}
