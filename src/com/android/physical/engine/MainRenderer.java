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

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.Vector;
import java.util.Scanner;
import java.io.FileNotFoundException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.android.physical.engine.R;

//import com.SceneGeneration.android.R;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Environment;
import android.util.Log;

public class MainRenderer implements GLSurfaceView.Renderer {
    private final Context context;

    //public myObject3D myobj;
    public myObject3D bwall, fwall, lwall, rwall, ceil, floor;
    
    public float mWidth, mHeight;
    private float[] mMVPMatrix = new float[16];
    private float[] mProjMatrix = new float[16];
    private float[] mVMatrix = new float[16];

    public myVector3D mEye;
    public myVector3D mForward;
    public myVector3D mUp;
    float fovy, zNear, zFar;
        
    public MainRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_LESS);
        
        /*
        myobj = new myObject3D();
        myobj.readMesh("Head.objtri.obj");
        myobj.computeNormals();
        myobj.computeSphereTexture();
        myobj.createBuffers();
        myobj.translateM(0, (float)0.5, 0);
        myobj.texture = new myTexture();
        //myobj.texture.createTexture(context, R.drawable.earthmap11);
        myobj.texture.createTexture(context, R.drawable.sky2);
        */
        
        bwall = new myObject3D();
        bwall.generateGrid(10,10);
        bwall.computeNormals();
        bwall.computePlaneTexture();
        bwall.createBuffers();
        bwall.translateM(0, 0, -5);
        bwall.texture = new myTexture();
        bwall.texture.createTexture(context, R.drawable.wall);        
        
        fwall = new myObject3D();
        fwall.generateGrid(10,10);
        fwall.computeNormals();
        fwall.computePlaneTexture();
        fwall.createBuffers();
		fwall.rotateM(180, 1,0,0);
		fwall.translateM(0,0,5);
        fwall.texture = bwall.texture;
        
		lwall = new myObject3D();
		lwall.generateGrid(10,10);
		lwall.computeNormals();
		lwall.computePlaneTexture();
		lwall.createBuffers();
		lwall.rotateM(90, 0,1,0);
		lwall.translateM(-5,0,0);
		lwall.texture = bwall.texture;

		rwall = new myObject3D();
		rwall.generateGrid(10,10);
		rwall.computeNormals();
		rwall.computePlaneTexture();
		rwall.createBuffers();
		rwall.rotateM(-90, 0,1,0);
		rwall.translateM(5,0,0);
		rwall.texture = bwall.texture;		
		
		/*
		floor = new myObject3D();
		floor.generateGrid(10,10);
		floor.computeNormals();
		floor.computePlaneTexture();
		floor.createBuffers();
		floor.rotateM(-90, 1,0,0);
		floor.texture = myobj.texture;
		floor.texture = new myTexture();
        floor.texture.createTexture(context, R.drawable.floordiffuse);
		*/
        
        mEye = new myVector3D(0,2,2);
        mForward = new myVector3D(0,0,-1);
        mUp = new myVector3D(0,1,0);
        fovy = 90; zNear = 0.1f; zFar = 600;
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);

        Matrix.setLookAtM(mVMatrix, 0,  mEye.dX, mEye.dY, mEye.dZ,   
        								mEye.dX+mForward.dX, mEye.dY+mForward.dY, mEye.dZ+mForward.dZ,   
        								mUp.dX, mUp.dY, mUp.dZ);

        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
        Matrix.translateM(mVMatrix, 0, 0, 1, 0);
        //myobj.draw(mProjMatrix, mVMatrix);
        
        Matrix.translateM(mVMatrix, 0, 0, -1, 0);
        bwall.draw(mProjMatrix, mVMatrix);
        /*
        fwall.draw(mProjMatrix, mVMatrix);
        lwall.draw(mProjMatrix, mVMatrix);
        rwall.draw(mProjMatrix, mVMatrix);
        floor.draw(mProjMatrix, mVMatrix);
*/
/*        Matrix.translateM(mVMatrix, 0, 2, 0, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
        myobj2.draw(mMVPMatrix, mVMatrix); 
*/        
        GLES20.glFlush();
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        //Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, (float) 0.1, 70);
        
        Matrix.perspectiveM(mProjMatrix, 0, fovy, ratio, zNear, zFar);
        mWidth = width;
        mHeight = height;
    }

    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    /**
     * Utility method for debugging OpenGL calls. Provide the name of the call
     * just after making it:
     *
     * <pre>
     * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
     * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
     *
     * If the operation is not successful, the check throws an error.
     *
     * @param glOperation - Name of the OpenGL call to check.
     */
    /*public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e("Texturer", glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }*/

	public void setView(float dx, float dy) {
		if (dx == 0 || dy == 0) return;
		float vx = dx / mWidth;
		float vy = dy / mHeight;

		float theta = (float) 4.0 * (Math.abs(vy)+ Math.abs(vx));
		myVector3D mRight = mForward.cross(mUp);
		mRight.normalize();

		myVector3D tomovein_direction = ((mRight.minus()).mult(vx)).plus((mUp).mult(vy));
		myVector3D rotation_axis = tomovein_direction.cross(mForward);
		rotation_axis.normalize();

		float[] tmpMatrix = new float[16];
		Matrix.setRotateM(tmpMatrix, 0, theta, rotation_axis.dX, rotation_axis.dY, rotation_axis.dZ);
		mForward.rotate(rotation_axis, theta);
		mUp.rotate(rotation_axis, theta);
		mEye.rotate(rotation_axis, theta);

		mUp.normalize();
		mForward.normalize();
	}
}

class myObject3D {
    private String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
            "uniform mat4 uNMatrix;" +
            "uniform mat4 uMVMatrix;" +
            "attribute vec3 vNormal;" +
            "attribute vec4 vPosition;" +
            "attribute vec2 vTexturecoords;" +
            "varying vec3 mynormal;" +
            "varying vec4 myvertex;" +
            "varying vec2 mytexturecoords;"+
            "varying mat4 myMVPMatrix;"+
            "varying mat4 myNMatrix;"+
            "varying mat4 myMVMatrix;"+

            "void main() {" +
            "  gl_Position = uMVPMatrix * vPosition;" +
            "  mynormal = vNormal;" +
            "  myvertex = vPosition;" +
            "  mytexturecoords = vTexturecoords;" +
            "  myMVPMatrix = uMVPMatrix;"+
            "  myNMatrix = uNMatrix;"+
            "  myMVMatrix = uMVMatrix;"+
            "}";

    private String fragmentShaderCode =
            "precision mediump float;" +
            "uniform vec4 vColor;" +
            "uniform sampler2D texMap;"+
            "varying mat4 myMVPMatrix;" +
            "varying mat4 myMVMatrix;" +
            "varying mat4 myNMatrix;" +
            "varying vec3 mynormal;" +
            "varying vec4 myvertex;" +
            "varying vec2 mytexturecoords;"+
            
            "void main() {" +

            "vec3 eyepos = vec3(0,0,0) ; "+ 

            "vec4 _mypos = myMVMatrix * myvertex ;"+ 
            "vec3 mypos = _mypos.xyz / _mypos.w ;"+

            "vec4 _lightpos = vec4(0,6,10,1);"+
            "_lightpos = myMVMatrix * _lightpos;" +
            "vec3 lightpos = _lightpos.xyz / _lightpos.w;"+

    		"vec3 normal = normalize(vec3(myNMatrix * vec4(mynormal,0)));"+

    		"vec3 eyedir = normalize(eyepos - mypos) ;"+
    		"vec3 lightdir = normalize (lightpos - mypos) ;"+

    		"vec3 reflectdir = normalize( reflect(-lightdir, normal) );"+

        	"gl_FragColor =  vec4(0.1,0.1,0.1,1) + texture2D(texMap, mytexturecoords) * max( dot(lightdir, normal), 0.0) "+
        										"+ texture2D(texMap, mytexturecoords) * pow(max( dot(reflectdir,eyedir), 0.0), 60.0);" +
        	//"gl_FragColor = texture2D(texMap, mytexturecoords);"+
            "}";

    private float[] vertices;
    private float[] texturecoords;
    private short[] indices;
    private float[] normals;
    private int[] buffers = new int[5];
    private float color[] = { 0.8f, 0.409803922f, 0.498039216f, 1.0f };
    public float[] transformationMatrix = new float[16];
    
    public myTexture texture;

    private int mProgram;
    private int mPositionHandle;
    private int mTexturecoordsHandle;
    private int mColorHandle;
    private int mNormalHandle;
    private int mMVPMatrixHandle;
    private int mNMatrixHandle;
    private int mMVMatrixHandle;

    public myObject3D() {
    	Matrix.setIdentityM(transformationMatrix, 0);
    }
    
    public void createBuffers()
    {
    	GLES20.glGenBuffers(5, buffers, 0);
    	
    	GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GLES20.GL_STATIC_DRAW);
        
    	GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffers[1]);
        ByteBuffer dlb = ByteBuffer.allocateDirect(indices.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        ShortBuffer indexBuffer = dlb.asShortBuffer();
        indexBuffer.put(indices);
        indexBuffer.position(0);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBuffer.capacity() * 2, indexBuffer, GLES20.GL_STATIC_DRAW);
        
    	GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[2]);
        bb = ByteBuffer.allocateDirect(normals.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer normalBuffer = bb.asFloatBuffer();
        normalBuffer.put(normals);
        normalBuffer.position(0);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, normalBuffer.capacity() * 4, normalBuffer, GLES20.GL_STATIC_DRAW);
        
    	GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[3]);
        bb = ByteBuffer.allocateDirect(texturecoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer texturecoordsBuffer = bb.asFloatBuffer();
        texturecoordsBuffer.put(texturecoords);
        texturecoordsBuffer.position(0);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, texturecoordsBuffer.capacity() * 4, texturecoordsBuffer, GLES20.GL_STATIC_DRAW);
        
        int vertexShader = MainRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                                                   vertexShaderCode);
        int fragmentShader = MainRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                                                     fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables   	
        
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public void draw(float[] _mpMatrix, float[] _mvMatrix) {
    	float[] mvpMatrix = new float[16];
    	float[] mvMatrix = new float[16];
    	Matrix.multiplyMM(mvMatrix, 0, _mvMatrix, 0, transformationMatrix, 0);
    	Matrix.multiplyMM(mvpMatrix, 0, _mpMatrix, 0, mvMatrix, 0);
    	
        GLES20.glUseProgram(mProgram);
        
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 12, 0);

        mNormalHandle = GLES20.glGetAttribLocation(mProgram, "vNormal");
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[2]);
        GLES20.glEnableVertexAttribArray(mNormalHandle);
        GLES20.glVertexAttribPointer(mNormalHandle, 3, GLES20.GL_FLOAT, false, 12, 0);

        int mtexMapHandle = GLES20.glGetUniformLocation(mProgram, "texMap");
        GLES20.glUniform1i(mtexMapHandle, 6);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE6);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.texName[0]);
                        
        mTexturecoordsHandle = GLES20.glGetAttribLocation(mProgram, "vTexturecoords");
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[3]);
        GLES20.glEnableVertexAttribArray(mTexturecoordsHandle);
        GLES20.glVertexAttribPointer(mTexturecoordsHandle, 2, GLES20.GL_FLOAT, false, 8, 0);	
                
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        Log.v("shaders", "mvp matrix: " + Integer.toString(mMVPMatrixHandle));
        //RollingBallRenderer.checkGlError("glGetUniformLocation");
        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        //RollingBallRenderer.checkGlError("glUniformMatrix4fv");
        
        mMVMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVMatrix");
        Log.v("shaders", "mv matrix: " + Integer.toString(mMVMatrixHandle));
        //RollingBallRenderer.checkGlError("glGetUniformLocation");
        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mvMatrix, 0);
        //RollingBallRenderer.checkGlError("glUniformMatrix4fv");
 
        mNMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uNMatrix");
        Log.v("shaders", "n matrix: " + Integer.toString(mNMatrixHandle));
        //RollingBallRenderer.checkGlError("glGetUniformLocation");
        float[] mNMatrix= new float[16];
        Matrix.invertM(mNMatrix, 0, mvMatrix, 0);
        Matrix.transposeM(mNMatrix, 0, Arrays.copyOf(mNMatrix, 16), 0);
        //mNMatrix = Arrays.copyOf(mvMatrix, 16);
        GLES20.glUniformMatrix4fv(mNMatrixHandle, 1, false, mNMatrix, 0);
        //RollingBallRenderer.checkGlError("glUniformMatrix4fv");  

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffers[1]);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_SHORT, 0);
    }
    
	void computeNormal(int v1, int v2, int v3, float [] output)
	{
		double dx1 = vertices[v2*3] - vertices[v1*3];
		double dx2 = vertices[v3*3] - vertices[v2*3];
		double dy1 = vertices[v2*3+1] - vertices[v1*3+1];
		double dy2 = vertices[v3*3+1] - vertices[v2*3+1];
		double dz1 = vertices[v2*3+2] - vertices[v1*3+2];
		double dz2 = vertices[v3*3+2] - vertices[v2*3+2];

		output[0] = (float) (dy1 * dz2 - dz1 * dy2);
		output[1] = (float) (dz1 * dx2 - dx1 * dz2);
		output[2] = (float) (dx1 * dy2 - dy1 * dx2);

		float length = (float) Math.sqrt(output[0]*output[0] + output[1]*output[1] + output[2]*output[2]);
		if (length <= 0)
		{
			Log.v("mydebugger", "normal length zero error!");
			output[0] = output[1] = output[2] = 1.0f;
			return;
		}

		output[0] = output[0]/length;
		output[1] = output[1]/length;
		output[2] = output[2]/length;
	}      

	void computeNormals( )
	{
		int i, j;
		float[] tmp = new float[3];

		int n = vertices.length/3;
		int m = indices.length/3;

		normals = new float[3*n];
		int [] incidences = new int[n];
		for (i=0;i<3*n;i++) normals[i] = 0.0f;
		for (i=0;i<n;i++) incidences[i] = 0;

		for (j=0;j<m;j++)
		{
			computeNormal(indices[3*j], indices[3*j+1], indices[3*j+2], tmp);
			normals[3*indices[3*j]] += tmp[0]; normals[3*indices[3*j]+1] += tmp[1]; normals[3*indices[3*j]+2] += tmp[2];
			normals[3*indices[3*j+1]] += tmp[0]; normals[3*indices[3*j+1]+1] += tmp[1]; normals[3*indices[3*j+1]+2] += tmp[2];
			normals[3*indices[3*j+2]] += tmp[0]; normals[3*indices[3*j+2]+1] += tmp[1]; normals[3*indices[3*j+2]+2] += tmp[2];
			incidences[indices[3*j]]++; incidences[indices[3*j+1]]++; incidences[indices[3*j+2]]++;
		}
		for (i=0;i<n;i++) {
			if (incidences[i]!=0) 
			{
				normals[3*i] /= incidences[i]; normals[3*i+1] /= incidences[i]; normals[3*i+2] /= incidences[i];
				
			}
			float length = (float) Math.sqrt(normals[3*i]*normals[3*i] + normals[3*i+1]*normals[3*i+1] + normals[3*i+2]*normals[3*i+2]);
			normals[3*i] /= length;
			normals[3*i+1] /= length;
			normals[3*i+2] /= length;
		}
	}
	
	void readMesh (String filename)
	{
		String t, u;
		try{ 
			File f = new File(Environment.getExternalStorageDirectory().toString()+"/Triobjs/"+filename);
			Scanner scanner = new Scanner(f);     
			Scanner lineTokenizer;

			Vector<Float> verts = new Vector<Float>();
			Vector<Short> inds = new Vector<Short>();

			while(scanner.hasNextLine())
			{
				lineTokenizer = new Scanner(scanner.nextLine());
				t = lineTokenizer.next();
				if (t.equals("v"))
				{
					u = lineTokenizer.next();
					verts.addElement(Float.parseFloat(u));
					u = lineTokenizer.next();
					verts.addElement(Float.parseFloat(u));
					u = lineTokenizer.next();
					verts.addElement(Float.parseFloat(u));
				}
				else if (t.equals("f"))
				{
					u = lineTokenizer.next();
					short i1 = (short)(Short.parseShort(u)-1);
					u = lineTokenizer.next();
					short i2 = (short)(Short.parseShort(u)-1);
					
					while (lineTokenizer.hasNext())
					{
						u = lineTokenizer.next();
						short i3 = (short)(Short.parseShort(u)-1);
						
						inds.addElement(i1);inds.addElement(i2);inds.addElement(i3);
						i2 = i3;
					}
				}
				lineTokenizer.close();
			}
			scanner.close();
		
			vertices = new float[verts.size()];
			for (int i=0;i<vertices.length;i++) vertices[i] = verts.get(i);
			indices = new short[inds.size()];
			for (int i=0;i<indices.length;i++) indices[i] = inds.get(i);	
			
			normalize();
			
			
		}
		catch (FileNotFoundException e) {}
	}
	
	void normalize()
	{
		int i;
		int tmpxmin = 0, tmpymin = 0, tmpzmin = 0, tmpxmax = 0, tmpymax = 0, tmpzmax = 0;
		
		int n = vertices.length/3;
		
		for (i=0;i<n;i++) {
			if (vertices[3*i] < vertices[3*tmpxmin]) tmpxmin = i;
			if (vertices[3*i] > vertices[3*tmpxmax]) tmpxmax = i;

			if (vertices[3*i+1] < vertices[3*tmpymin+1]) tmpymin = i;
			if (vertices[3*i+1] > vertices[3*tmpymax+1]) tmpymax = i;

			if (vertices[3*i+2] < vertices[3*tmpzmin+2]) tmpzmin = i;
			if (vertices[3*i+2] > vertices[3*tmpzmax+2]) tmpzmax = i;
		}

		double xmin = vertices[3*tmpxmin], xmax = vertices[3*tmpxmax], 
			   ymin = vertices[3*tmpymin+1], ymax = vertices[3*tmpymax+1], 
			   zmin = vertices[3*tmpzmin+2], zmax = vertices[3*tmpzmax+2];

		double scale = (xmax-xmin) <= (ymax-ymin) ? (xmax-xmin) : (ymax-ymin);
		scale = scale >= (zmax-zmin) ? scale : (zmax-zmin);

		for (i=0;i<n;i++) {
			vertices[3*i] -= (xmax+xmin)/2;
			vertices[3*i+1] -= (ymax+ymin)/2;
			vertices[3*i+2] -= (zmax+zmin)/2;

			vertices[3*i] /= scale;
			vertices[3*i+1] /= scale;
			vertices[3*i+2] /= scale;
		}
	}
	
	void computeSphereTexture()
	{
		int n = vertices.length/3;
		texturecoords = new float[3*n];
		double x, y, z;
		for (int i=0;i<n;i++)
		{
			x = vertices[3*i]; y = vertices[3*i+1]; z = vertices[3*i+2];
			if (x==0&&y==0&&z==0) continue;
			double l = Math.sqrt(x*x + y*y + z*z); 
			x = x/l; y = y/l; z = z/l;

			if (-z >= 0.0) texturecoords[2*i] = (float) (Math.atan2(-z, x) / (2*Math.PI));
			else texturecoords[2*i] = (float) ((2*Math.PI + Math.atan2(-z, x)) / (2*Math.PI)) ;
			
			if (y >= 0.0) texturecoords[2*i+1] = (float) (Math.acos(y) / Math.PI);
			else texturecoords[2*i+1] = (float) ((Math.PI - Math.acos(-y))/ Math.PI);
		}
	}

	//sxs grid centered at the origin in the xy plane.
	void generateGrid(int num, float s)
	{
		int i, j, k;

		int n = (num+1)*(num+1);
		int m = num*num*2;
		vertices = new float[3*n];
		indices = new short[3*m];
		texturecoords = new float[2*n];

		k = 0;
		for (i=0;i<=num;i++)
		{
			for (j=0;j<=num;j++){
				vertices[3*((num+1)*i+j)] = (float)s*i/(float)num - s/2; 
				vertices[3*((num+1)*i+j)+1] = (float)s*j/(float)num - s/2; 
				vertices[3*((num+1)*i+j)+2] = (float) 0.0;

				texturecoords[2*((num+1)*i+j)] = (float)i/(float)(num+1); 
				texturecoords[2*((num+1)*i+j)+1] = (float)j/(float)(num+1);
			}
		}

		k = 0;
		for (i=0;i<num;i++)
		{
			for (j=0;j<num;j++)
			{
				indices[k++] = (short) ((num+1)*i + j);
				indices[k++] = (short) ((num+1)*(i+1) + j);
				indices[k++] = (short) ((num+1)*(i) + j+1);

				indices[k++] = (short) ((num+1)*(i+1) + j);
				indices[k++] = (short) ((num+1)*(i+1) + j+1);
				indices[k++] = (short) ((num+1)*(i) + j+1);
			}
		}
	}
	
	void translateM(float x, float y, float z)
	{
		float[] tmp = new float[16];
		Matrix.setIdentityM(tmp, 0);
		Matrix.translateM(tmp, 0, x, y, z);
		Matrix.multiplyMM(transformationMatrix, 0, tmp, 0, Arrays.copyOf(transformationMatrix, 16), 0);
		//Matrix.translateM(transformationMatrix, 0, x, y, z);
		
	}
	
	void clearM()
	{
		Matrix.setIdentityM(transformationMatrix, 0);
	}
	
	void rotateM(float a, float x, float y, float z)
	{
		float[] tmp = new float[16];
		Matrix.setIdentityM(tmp, 0);
		Matrix.rotateM(tmp, 0, a, x, y, z);
		Matrix.multiplyMM(transformationMatrix, 0, tmp, 0, Arrays.copyOf(transformationMatrix, 16), 0);
		//Matrix.rotateM(transformationMatrix, 0, a, x, y, z);
	}
	

	void computePlaneTexture()
	{
		int n = vertices.length/3; 
		texturecoords = new float[2*n];
		int num = (int) Math.sqrt((float) n);

		for (int i=0;i<num;i++)
			for (int j=0;j<num;j++){
				texturecoords[2*(i*num+j)] =  (float) i/num;
				texturecoords[2*(i*num+j)+1] = (float) (1.0 - (float) j/num);
			}
	}
}