/**
 * ESIEE OpenSource Project : OpenGL
 *
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.esieeAPE.implementation;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.esieeAPE.Const;
import com.esieeAPE.GLFragment;
import com.esieeAPE.MainActivity;
import com.esieeAPE.R;
import com.esieeAPE.lib.IFunctionEntity;
import com.esieeAPE.lib.Predicat;
import com.esieeAPE.lib.WayPosition;
import com.esieeAPE.lib.myVector3D;
import com.esieeAPE.objects.Camera;
import com.esieeAPE.objects.Car;
import com.esieeAPE.objects.Entity;
import com.esieeAPE.objects.EntityGroup;
import com.esieeAPE.objects.myObject3D;
import com.esieeAPE.objects.myTexture;
import com.esieeAPE.physics.Force;
import com.esieeAPE.physics.ForceSpring;
import com.esieeAPE.physics.PhysicsEngine;

/**
 * SPECIFIC world : Place and define all objects
 * @author Jonathan
 *
 */
public class World extends EntityGroup {
	
	Camera camera;
	public static int carId;

	public World(Context context, Camera camera) {
		super(context);
		this.camera = camera;
	}
	
	@Override
	public void init() {		
		
		/********* INIT OBJECTS *********/
		
		GLFragment.progressBar.setMax(ENUM_Obj.values().length);		
		
		final Car car_camaro = new Car(context, new myVector3D(0,0,-1));
		carId = this.entities.size();
		car_camaro.readMeshLocal(ENUM_Obj.CAR_CAMARO.getIndicesVertices(context));
		car_camaro.scale(1.5f);
		car_camaro.rotate(-90, 1, 0, 0);
		car_camaro.computeNormals();
		car_camaro.computeSphereTexture();
		car_camaro.createBuffers();
		car_camaro.translate(10f, 5.0f, 5f);
		car_camaro.texture = new myTexture(context, R.drawable.color_white);
		car_camaro.physic.mass = 0.11f;
        this.addEntity(car_camaro);
		
		int borne_apple = 1; float step_apple = 3.0f;
		myObject3D apple = null;		
		for(int z=0; z<step_apple; z++) {		
			for(float i=-borne_apple; i<=borne_apple; i++) {
				for(float j=-borne_apple; j<=borne_apple; j++) {
					apple = new myObject3D(context);
					apple.readMeshLocal(ENUM_Obj.APPLE.getIndicesVertices(context));
					apple.scale(0.5f);
					apple.computeNormals();
					apple.computeSphereTexture();
					apple.createBuffers();
					apple.translate(0.0f+i, (j+i)/3+5.0f + z*2.0f, -3.5f+j);
					if(z%3==0)
						apple.texture = new myTexture(context, R.drawable.color_green);
					else if(z%3==1)
						apple.texture = new myTexture(context, R.drawable.color_red);
					else
						apple.texture = new myTexture(context, R.drawable.color_white);					
					apple.physic.mass = 0.11f;
			        this.addEntity(apple);
				}			
			}
		}		
        
        float tmp_height = 3; float tmp_width = 2;
		myObject3D point = null;
		myObject3D[][] m_point = new myObject3D[(int) tmp_width][(int) tmp_height];
		for(int i=0; i<tmp_width; i++) {
			for(int j=0; j<tmp_height; j++) {
				point = new myObject3D(context);
				point.readMeshLocal(ENUM_Obj.SPHERE.getIndicesVertices(context));
				point.scale(1.5f);
				point.computeNormals();
				point.computeSphereTexture();
				point.createBuffers();
				point.translate(14.0f+i*1.7f, 18.0f-j*1.7f , -3.5f);
				if(j%3==0)
					point.texture = new myTexture(context, R.drawable.color_green);
				else if(j%3==1)
					point.texture = new myTexture(context, R.drawable.color_red);
				else
					point.texture = new myTexture(context, R.drawable.sky2);
				if(j!=0) {
					point.physic.mass = 0.11f;
					point.addForce(new ForceSpring(m_point[0][j-1], 0.00005f, true));//point.addForce(new ForceSpring(new myVector3D(8.0f+i, (tmp_height-1)+12.0f , -3.5f), 0.000001f, true));
				}
				
				m_point[i][j] = point;
			}			
		}        
		for(myObject3D[] o1 : m_point)
			for(myObject3D o2 : o1)
				this.addEntity(o2);
        
		final myObject3D shuttle = new myObject3D(context, new IFunctionEntity() {			
			@Override
			public void execute(Entity entity) {
				final float tmp = Math.abs(entity.velocity.dY);
				((Activity) World.this.context).runOnUiThread(new Runnable() {
			        public void run() {	
			        	if(MainActivity.config.getControlMode()==Const.MODE_SHUTTLE)
				        	if(tmp<0.0025) 	Toast.makeText(World.this.context, "LANDING : Shuttle Contact Velocity dY :"+tmp, Toast.LENGTH_SHORT).show();
				        	else 			Toast.makeText(World.this.context, "CRASH : Shuttle Contact Velocity dY :"+tmp, Toast.LENGTH_SHORT).show();			        	
			        }
			    });				
			}			
			@Override
			public boolean condition(Entity entity) {				
				return Math.abs(entity.velocity.dY)>0.001;
			}
		});
		final int shuttleId = this.entities.size();
		shuttle.readMeshLocal(ENUM_Obj.SHUTTLE.getIndicesVertices(context));
		shuttle.scale(5.0f);
		shuttle.rotate(-90, 1, 0, 0);
		shuttle.rotate(180, 0, 1, 0);
		shuttle.computeNormals();
		shuttle.computeSphereTexture();
		shuttle.createBuffers();
		shuttle.translate(0.0f, 7.0f, -20f);
		shuttle.texture = new myTexture(context, R.drawable.color_white);
		shuttle.physic.mass = 0.21f;
        this.addEntity(shuttle);        
        
        // BUMP MAPPING
        final int bumpAppleId = this.entities.size();
        apple = new myObject3D(context);
		apple.readMeshLocal(ENUM_Obj.APPLE.getIndicesVertices(context));
		apple.scale(2.5f);
		apple.computeNormals();
		apple.computeSphereTexture();
		apple.computeTangents();
		apple.createBuffers();
		apple.translate(-10f, 8.0f, -10f);		
		apple.texture = new myTexture(context, R.drawable.shingles);
		apple.texture_bump = new myTexture(context, R.drawable.shingles_bump, 1);
		apple.physic.mass = 0.11f;
        this.addEntity(apple);
        
		

        
        
        float translateTowerX = -22.0f;
        int scaleTower = 4;
        
        myObject3D bwall = new myObject3D(context);
        bwall.generateGrid(scaleTower*2, scaleTower*2);
        bwall.computeNormals();
        bwall.computePlaneTexture();
        bwall.createBuffers();
        bwall.translate(0+translateTowerX, scaleTower, -scaleTower);
        bwall.texture = new myTexture(context, R.drawable.wall);
        this.addEntity(bwall);
        
        myObject3D fwall = new myObject3D(context);
        fwall.generateGrid(scaleTower*2, scaleTower*2);
        fwall.computeNormals();
        fwall.computePlaneTexture();
        fwall.createBuffers();
		fwall.rotate(180, 1,0,0);
		fwall.translate(0+translateTowerX, scaleTower, scaleTower);
        fwall.texture = bwall.texture;
        this.addEntity(fwall);
        
        myObject3D lwall = new myObject3D(context);
		lwall.generateGrid(scaleTower*2, scaleTower*2);
		lwall.computeNormals();
		lwall.computePlaneTexture();
		lwall.createBuffers();
		lwall.rotate(90, 0,1,0);
		lwall.translate(-scaleTower+translateTowerX, scaleTower, 0);
		lwall.texture = bwall.texture;
		this.addEntity(lwall);

		myObject3D rwall = new myObject3D(context);
		rwall.generateGrid(scaleTower*2, scaleTower*2);
		rwall.computeNormals();
		rwall.computePlaneTexture();
		rwall.createBuffers();
		rwall.rotate(-90, 0,1,0);
		rwall.translate(scaleTower+translateTowerX, scaleTower, 0);
		rwall.texture = bwall.texture;
		this.addEntity(rwall);		
		
		/*
		myObject3D house = new myObject3D(context);
		house.readMeshLocal(ENUM_IndicesVertices.HOUSE.getIndicesVertices(context));
		house.scale(12);
        house.computeNormals();
        house.computeSphereTexture();
        house.createBuffers();
        house.rotate(-90, 0,1,0);
        house.translate(18.0f, 4.4f, 0);
        house.texture = new myTexture(context, R.drawable.floor);
        this.addEntity(house);
        
		myObject3D lamp;
		for(float i=-borne; i<=borne; i++) {
			for(float j=-borne; j<=borne; j++) {
		        lamp = new myObject3D(context);
		        lamp.readMeshLocal(ENUM_IndicesVertices.LAMP.getIndicesVertices(context));
		        lamp.scale(1.4f);
		        lamp.computeNormals();
		        lamp.computeSphereTexture();
		        lamp.createBuffers();
		        lamp.translate(5f,4f,0);
		        lamp.translate(-6.0f+i*3, -2.2f, -25.0f+j*3);
				lamp.texture = bwall.texture;
				this.addEntity(lamp);
			}			
		}
        */
		
		apple = new myObject3D(context);
		apple.readMeshLocal(ENUM_Obj.APPLE.getIndicesVertices(context));
		apple.scale(0.5f);
		apple.computeNormals();
		apple.computeSphereTexture();
		apple.createBuffers();
		apple.translate(0.0f, 1.0f, 0.0f);
		apple.texture = new myTexture(context, R.drawable.color_green);
		apple.physic.mass = 0.0f;		
		apple.repetedWayPosition = new WayPosition();		
		apple.repetedWayPosition.initCubeWabHorizontal(0,2.0f,0, 20.0f, 0.4f, true);
        this.addEntity(apple);
        
        apple = new myObject3D(context);
        apple.readMeshLocal(ENUM_Obj.APPLE.getIndicesVertices(context));
        apple.scale(0.5f);
        apple.computeNormals();
        apple.computeSphereTexture();
		apple.createBuffers();
		apple.translate(0.0f, 1.0f, 0.0f);
		apple.texture = new myTexture(context, R.drawable.color_red);
		apple.physic.mass = 0.0f;		
		apple.repetedWayPosition = new WayPosition();		
		apple.repetedWayPosition.initCubeWabHorizontal(0,2.0f,0, 20.0f, 0.4f, false);
        this.addEntity(apple);		
		
		myObject3D floor = new myObject3D(context);
		floor.generateGrid(100,100);
		floor.computeNormals();
		floor.computePlaneTexture();
		floor.createBuffers();
		floor.rotate(-90, 1,0,0);
		floor.texture = new myTexture(context, R.drawable.floor);
        this.addEntity(floor);
        
        
        /********* INIT FORCES *********/
        
        this.addForce(ENUM_Forces.GRAVITY.force);
        
        ENUM_Forces.GRAVITY_UP.force.isApplyForce = new Predicat() {
			@Override
			public myVector3D isTrue(Entity entity) {
				/*
				if(MainActivity.SECOND_FROM_LAUNCH < 30)
					return new myVector3D(0,0,0);				
				
				if(entity.position.dY<100 && MainActivity.SECOND_FROM_LAUNCH % 31 == 30 && shuttleId!=entity.id && carId!=entity.id)
					return new myVector3D(1,1+entity.position.dY/8,1);
				*/
				
				if(entity.position.dY<50 && PhysicsEngine.actionButton && MainActivity.config.getControlMode()==Const.MODE_JUMP && shuttleId!=entity.id && carId!=entity.id && bumpAppleId!=entity.id)
					return new myVector3D(1,1+entity.position.dY/20,1);
				
				return new myVector3D(0,0,0);
			}
		};
		this.addForce(ENUM_Forces.GRAVITY_UP.force);
		
		
		Predicat p_shuttle = new Predicat() {
			@Override
			public myVector3D isTrue(Entity entity) {				
				if(shuttle.id==entity.id && PhysicsEngine.actionButton && MainActivity.config.getControlMode()==Const.MODE_SHUTTLE)
					return new myVector3D(0,1.1f,0);
				return new myVector3D(0,0,0);
			}
		};
		this.addForce(new Force(0.000004f, true, p_shuttle));
		
		Predicat p_car_camaro = new Predicat() {
			@Override
			public myVector3D isTrue(Entity entity) {				
				if(car_camaro.id==entity.id && PhysicsEngine.actionButton && MainActivity.config.getControlMode()==Const.MODE_CAR)
					return new myVector3D(car_camaro.forward.dX, car_camaro.forward.dY, car_camaro.forward.dZ);				
				return new myVector3D(0,0,0);
			}
		};
		this.addForce(new Force(0.000003f, true, p_car_camaro));
	}
}
