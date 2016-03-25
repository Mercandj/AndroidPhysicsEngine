/**
 * ESIEE OpenSource Project : OpenGL
 * <p>
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.esieeAPE.objects;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.Matrix;
import android.util.Log;

import com.esieeAPE.R;
import com.esieeAPE.lib.IFunctionEntity;
import com.esieeAPE.lib.IndicesVertices;
import com.esieeAPE.lib.Vector3D;
import com.esieeAPE.physics.Force;
import com.esieeAPE.physics.PhysicsConst;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * "Real" object
 *
 * @author Jonathan
 */
public class Object3D extends Entity {

    private static final String TAG = "Object3D";

    private static String sVertexShaderCode;
    private static String sFragmentShaderCode;

    public float[] transformationMatrix = new float[16];
    public Texture mTexture;
    public Texture mTextureBumpMapping;
    IFunctionEntity mContactFloorListener;
    boolean mIsInsideLastLoop = false;
    private float[] mVertices;
    private float[] mTextureCoordinates;
    private short[] mIndices;
    private float[] mNormals, mTangents;
    private int[] buffers = new int[5];
    private float color[] = {0.8f, 0.409803922f, 0.498039216f, 1.0f};
    private int mProgram;
    private int mPositionHandle;
    private int mTexturecoordsHandle;
    private int mColorHandle;
    private int mNormalHandle;
    private int mTangentHandle;
    private int mMVPMatrixHandle;
    private int mNMatrixHandle;
    private int mMVMatrixHandle;

    public Object3D(final Context context, final IFunctionEntity contactFlourListener) {
        if (sVertexShaderCode == null) {
            sVertexShaderCode = readShaderFromRawResource(context, R.raw.shader_vertex);
        }
        if (sFragmentShaderCode == null) {
            sFragmentShaderCode = readShaderFromRawResource(context, R.raw.shader_fragment);
        }
        Matrix.setIdentityM(transformationMatrix, 0);
        mContactFloorListener = contactFlourListener;
    }

    public Object3D(final Context context) {
        if (sVertexShaderCode == null) {
            sVertexShaderCode = readShaderFromRawResource(context, R.raw.shader_vertex);
        }
        if (sFragmentShaderCode == null) {
            sFragmentShaderCode = readShaderFromRawResource(context, R.raw.shader_fragment);
        }
        Matrix.setIdentityM(transformationMatrix, 0);
    }

    private Vector3D computeTangent(int v0, int v1, int v2) {
        float du1 = mTextureCoordinates[2 * v1] - mTextureCoordinates[2 * v0];
        float dv1 = mTextureCoordinates[2 * v1 + 1] - mTextureCoordinates[2 * v0 + 1];
        float du2 = mTextureCoordinates[2 * v2] - mTextureCoordinates[2 * v0];
        float dv2 = mTextureCoordinates[2 * v2 + 1] - mTextureCoordinates[2 * v0 + 1];

        float f = 1.0f / (du1 * dv2 - du2 * dv1);
        if ((du1 * dv2 - du2 * dv1) == 0) {
            return new Vector3D(0, 0, 0);
        }

        float e1x = mVertices[3 * v1] - mVertices[3 * v0];
        float e1y = mVertices[3 * v1 + 1] - mVertices[3 * v0 + 1];
        float e1z = mVertices[3 * v1 + 2] - mVertices[3 * v0 + 2];

        float e2x = mVertices[3 * v2] - mVertices[3 * v0];
        float e2y = mVertices[3 * v2 + 1] - mVertices[3 * v0 + 1];
        float e2z = mVertices[3 * v2 + 2] - mVertices[3 * v0 + 2];

        return new Vector3D(f * (dv2 * e1x - dv1 * e2x), f * (dv2 * e1y - dv1 * e2y), f * (dv2 * e1z - dv1 * e2z));
    }

    public void computeTangents() {
        int i, j;
        float x1, y1, z1;

        int n = mVertices.length / 3;
        int m = mIndices.length / 3;

        mTangents = new float[3 * n];
        int[] incidences = new int[n];
        for (i = 0; i < 3 * n; i++) mTangents[i] = 0.0f;
        for (i = 0; i < n; i++) incidences[i] = 0;

        for (j = 0; j < m; j++) {
            Vector3D v = computeTangent(mIndices[3 * j], mIndices[3 * j + 1], mIndices[3 * j + 2]);
            x1 = v.dX;
            y1 = v.dY;
            z1 = v.dZ;
            mTangents[3 * mIndices[3 * j]] += x1;
            mTangents[3 * mIndices[3 * j] + 1] += y1;
            mTangents[3 * mIndices[3 * j] + 2] += z1;
            mTangents[3 * mIndices[3 * j + 1]] += x1;
            mTangents[3 * mIndices[3 * j + 1] + 1] += y1;
            mTangents[3 * mIndices[3 * j + 1] + 2] += z1;
            mTangents[3 * mIndices[3 * j + 2]] += x1;
            mTangents[3 * mIndices[3 * j + 2] + 1] += y1;
            mTangents[3 * mIndices[3 * j + 2] + 2] += z1;
            incidences[mIndices[3 * j]]++;
            incidences[mIndices[3 * j + 1]]++;
            incidences[mIndices[3 * j + 2]]++;
        }
        for (i = 0; i < n; i++) {
            float l = (float) Math.sqrt(mTangents[3 * i] * mTangents[3 * i] + mTangents[3 * i + 1] * mTangents[3 * i + 1] + mTangents[3 * i + 2] * mTangents[3 * i + 2]);
            mTangents[3 * i] /= l;
            mTangents[3 * i + 1] /= l;
            mTangents[3 * i + 2] /= l;
        }
    }

    public void createBuffers() {
        GLES30.glGenBuffers(5, buffers, 0);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, buffers[0]);
        ByteBuffer bb = ByteBuffer.allocateDirect(mVertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(mVertices);
        vertexBuffer.position(0);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GLES30.GL_STATIC_DRAW);

        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, buffers[1]);
        ByteBuffer dlb = ByteBuffer.allocateDirect(mIndices.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        ShortBuffer indexBuffer = dlb.asShortBuffer();
        indexBuffer.put(mIndices);
        indexBuffer.position(0);
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, indexBuffer.capacity() * 2, indexBuffer, GLES30.GL_STATIC_DRAW);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, buffers[2]);
        bb = ByteBuffer.allocateDirect(mNormals.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer normalBuffer = bb.asFloatBuffer();
        normalBuffer.put(mNormals);
        normalBuffer.position(0);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, normalBuffer.capacity() * 4, normalBuffer, GLES30.GL_STATIC_DRAW);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, buffers[3]);
        bb = ByteBuffer.allocateDirect(mTextureCoordinates.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer texturecoordsBuffer = bb.asFloatBuffer();
        texturecoordsBuffer.put(mTextureCoordinates);
        texturecoordsBuffer.position(0);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, texturecoordsBuffer.capacity() * 4, texturecoordsBuffer, GLES30.GL_STATIC_DRAW);

        if (mTangents != null) {//TODO

            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, buffers[4]);
            bb = ByteBuffer.allocateDirect(mTangents.length * 4);
            bb.order(ByteOrder.nativeOrder());
            FloatBuffer tagentBuffer = bb.asFloatBuffer();
            tagentBuffer.put(mTangents);
            tagentBuffer.position(0);
            GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, tagentBuffer.capacity() * 4, tagentBuffer, GLES30.GL_STATIC_DRAW);
        }

        int vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, sVertexShaderCode);
        int fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER, sFragmentShaderCode);

        mProgram = GLES30.glCreateProgram();             // create empty OpenGL Program
        GLES30.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES30.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES30.glLinkProgram(mProgram);                  // create OpenGL program executables

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    @Override
    public void draw(final float[] _mpMatrix, final float[] _mvMatrix) {

        float[] mvpMatrix = new float[16];
        float[] mvMatrix = new float[16];
        Matrix.multiplyMM(mvMatrix, 0, _mvMatrix, 0, transformationMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, _mpMatrix, 0, mvMatrix, 0);

        GLES30.glUseProgram(mProgram);

        mPositionHandle = GLES30.glGetAttribLocation(mProgram, "vPosition");
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, buffers[0]);
        GLES30.glEnableVertexAttribArray(mPositionHandle);
        GLES30.glVertexAttribPointer(mPositionHandle, 3, GLES30.GL_FLOAT, false, 12, 0);

        mNormalHandle = GLES30.glGetAttribLocation(mProgram, "vNormal");
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, buffers[2]);
        GLES30.glEnableVertexAttribArray(mNormalHandle);
        GLES30.glVertexAttribPointer(mNormalHandle, 3, GLES30.GL_FLOAT, false, 12, 0);

        int mtexMapHandle = GLES30.glGetUniformLocation(mProgram, "texMap");
        GLES30.glUniform1i(mtexMapHandle, 6);
        GLES30.glActiveTexture(GLES30.GL_TEXTURE6);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTexture.texName[0]);

        mTexturecoordsHandle = GLES30.glGetAttribLocation(mProgram, "vTexturecoords");
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, buffers[3]);
        GLES30.glEnableVertexAttribArray(mTexturecoordsHandle);
        GLES30.glVertexAttribPointer(mTexturecoordsHandle, 2, GLES30.GL_FLOAT, false, 8, 0);

        if (mTangents != null) { //TODO
            int isBumpMapping = GLES30.glGetUniformLocation(mProgram, "isBumpMapping");
            GLES30.glUniform1i(isBumpMapping, 1);

            mTangentHandle = GLES30.glGetAttribLocation(mProgram, "vTangent");
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, buffers[4]);
            GLES30.glEnableVertexAttribArray(mTangentHandle);
            GLES30.glVertexAttribPointer(mTangentHandle, 3, GLES30.GL_FLOAT, false, 12, 0);

            int mtexMapHandle_bump = GLES30.glGetUniformLocation(mProgram, "texMap_bump");
            GLES30.glUniform1i(mtexMapHandle_bump, 7);
            GLES30.glActiveTexture(GLES30.GL_TEXTURE7);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureBumpMapping.texName[0]);
        }

        mColorHandle = GLES30.glGetUniformLocation(mProgram, "vColor");
        GLES30.glUniform4fv(mColorHandle, 1, color, 0);

        mMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
        // Apply the projection and view transformation
        GLES30.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        mMVMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVMatrix");
        // Apply the projection and view transformation
        GLES30.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mvMatrix, 0);

        mNMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uNMatrix");
        float[] mNMatrix = new float[16];
        Matrix.invertM(mNMatrix, 0, mvMatrix, 0);
        Matrix.transposeM(mNMatrix, 0, Arrays.copyOf(mNMatrix, 16), 0);
        GLES30.glUniformMatrix4fv(mNMatrixHandle, 1, false, mNMatrix, 0);

        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, buffers[1]);
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, mIndices.length, GLES30.GL_UNSIGNED_SHORT, 0);
    }

    void computeNormal(int v1, int v2, int v3, float[] output) {
        double dx1 = mVertices[v2 * 3] - mVertices[v1 * 3];
        double dx2 = mVertices[v3 * 3] - mVertices[v2 * 3];
        double dy1 = mVertices[v2 * 3 + 1] - mVertices[v1 * 3 + 1];
        double dy2 = mVertices[v3 * 3 + 1] - mVertices[v2 * 3 + 1];
        double dz1 = mVertices[v2 * 3 + 2] - mVertices[v1 * 3 + 2];
        double dz2 = mVertices[v3 * 3 + 2] - mVertices[v2 * 3 + 2];

        output[0] = (float) (dy1 * dz2 - dz1 * dy2);
        output[1] = (float) (dz1 * dx2 - dx1 * dz2);
        output[2] = (float) (dx1 * dy2 - dy1 * dx2);

        float length = (float) Math.sqrt(output[0] * output[0] + output[1] * output[1] + output[2] * output[2]);
        if (length <= 0) {
            Log.v(TAG, "normal length zero error!");
            output[0] = output[1] = output[2] = 1.0f;
            return;
        }

        output[0] = output[0] / length;
        output[1] = output[1] / length;
        output[2] = output[2] / length;
    }

    public void computeNormals() {
        int i, j;
        float[] tmp = new float[3];
        float length;

        int n = mVertices.length / 3;
        int m = mIndices.length / 3;

        mNormals = new float[3 * n];
        int[] incidences = new int[n];
        for (i = 0; i < 3 * n; i++) mNormals[i] = 0.0f;
        for (i = 0; i < n; i++) incidences[i] = 0;

        for (j = 0; j < m; j++) {
            computeNormal(mIndices[3 * j], mIndices[3 * j + 1], mIndices[3 * j + 2], tmp);
            mNormals[3 * mIndices[3 * j]] += tmp[0];
            mNormals[3 * mIndices[3 * j] + 1] += tmp[1];
            mNormals[3 * mIndices[3 * j] + 2] += tmp[2];
            mNormals[3 * mIndices[3 * j + 1]] += tmp[0];
            mNormals[3 * mIndices[3 * j + 1] + 1] += tmp[1];
            mNormals[3 * mIndices[3 * j + 1] + 2] += tmp[2];
            mNormals[3 * mIndices[3 * j + 2]] += tmp[0];
            mNormals[3 * mIndices[3 * j + 2] + 1] += tmp[1];
            mNormals[3 * mIndices[3 * j + 2] + 2] += tmp[2];
            incidences[mIndices[3 * j]]++;
            incidences[mIndices[3 * j + 1]]++;
            incidences[mIndices[3 * j + 2]]++;
        }
        for (i = 0; i < n; i++) {
            if (incidences[i] != 0) {
                mNormals[3 * i] /= incidences[i];
            }
            mNormals[3 * i + 1] /= incidences[i];
            mNormals[3 * i + 2] /= incidences[i];

            length = (float) Math.sqrt(mNormals[3 * i] * mNormals[3 * i] + mNormals[3 * i + 1] * mNormals[3 * i + 1] + mNormals[3 * i + 2] * mNormals[3 * i + 2]);
            mNormals[3 * i] /= length;
            mNormals[3 * i + 1] /= length;
            mNormals[3 * i + 2] /= length;
        }
    }

    public void readMeshLocal(final IndicesVertices indicesVertices) {
        mVertices = indicesVertices.vertices;
        mIndices = indicesVertices.indices;
        mEdgePositionMin = indicesVertices.edgeVerticeMin;
        mEdgeVectorPositionMax = indicesVertices.edgeVerticeMax;
    }

    public void computeSphereTexture() {
        int n = mVertices.length / 3;
        mTextureCoordinates = new float[3 * n];
        double x, y, z;
        for (int i = 0; i < n; i++) {
            x = mVertices[3 * i];
            y = mVertices[3 * i + 1];
            z = mVertices[3 * i + 2];
            if (x == 0 && y == 0 && z == 0) continue;
            double l = Math.sqrt(x * x + y * y + z * z);
            x = x / l;
            y = y / l;
            z = z / l;

            if (-z >= 0.0) {
                mTextureCoordinates[2 * i] = (float) (Math.atan2(-z, x) / (2 * Math.PI));
            } else {
                mTextureCoordinates[2 * i] = (float) ((2 * Math.PI + Math.atan2(-z, x)) / (2 * Math.PI));
            }

            if (y >= 0.0) {
                mTextureCoordinates[2 * i + 1] = (float) (Math.acos(y) / Math.PI);
            } else {
                mTextureCoordinates[2 * i + 1] = (float) ((Math.PI - Math.acos(-y)) / Math.PI);
            }
        }
    }

    //sxs grid centered at the origin in the xy plane.
    public void generateGrid(int num, float s) {
        int i, j, k;

        int n = (num + 1) * (num + 1);
        int m = num * num * 2;
        mVertices = new float[3 * n];
        mIndices = new short[3 * m];
        mTextureCoordinates = new float[2 * n];

        mEdgePositionMin = new Vector3D(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
        mEdgeVectorPositionMax = new Vector3D(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);

        k = 0;
        for (i = 0; i <= num; i++) {
            for (j = 0; j <= num; j++) {
                mVertices[3 * ((num + 1) * i + j)] = (float) s * i / (float) num - s / 2;
                mVertices[3 * ((num + 1) * i + j) + 1] = (float) s * j / (float) num - s / 2;
                mVertices[3 * ((num + 1) * i + j) + 2] = (float) 0.0;

                if (mVertices[3 * ((num + 1) * i + j)] < mEdgePositionMin.dX)
                    mEdgePositionMin.dX = mVertices[3 * ((num + 1) * i + j)];
                else if (mVertices[3 * ((num + 1) * i + j)] > mEdgePositionMin.dX)
                    mEdgeVectorPositionMax.dX = mVertices[3 * ((num + 1) * i + j)];

                if (mVertices[3 * ((num + 1) * i + j) + 1] < mEdgePositionMin.dY)
                    mEdgePositionMin.dY = mVertices[3 * ((num + 1) * i + j) + 1];
                else if (mVertices[3 * ((num + 1) * i + j) + 1] > mEdgePositionMin.dY)
                    mEdgeVectorPositionMax.dY = mVertices[3 * ((num + 1) * i + j) + 1];

                if (mVertices[3 * ((num + 1) * i + j) + 2] < mEdgePositionMin.dZ)
                    mEdgePositionMin.dZ = mVertices[3 * ((num + 1) * i + j) + 2];
                else if (mVertices[3 * ((num + 1) * i + j) + 2] > mEdgePositionMin.dZ)
                    mEdgeVectorPositionMax.dZ = mVertices[3 * ((num + 1) * i + j) + 2];

                mTextureCoordinates[2 * ((num + 1) * i + j)] = (float) i / (float) (num + 1);
                mTextureCoordinates[2 * ((num + 1) * i + j) + 1] = (float) j / (float) (num + 1);
            }
        }

        k = 0;
        for (i = 0; i < num; i++) {
            for (j = 0; j < num; j++) {
                mIndices[k++] = (short) ((num + 1) * i + j);
                mIndices[k++] = (short) ((num + 1) * (i + 1) + j);
                mIndices[k++] = (short) ((num + 1) * (i) + j + 1);

                mIndices[k++] = (short) ((num + 1) * (i + 1) + j);
                mIndices[k++] = (short) ((num + 1) * (i + 1) + j + 1);
                mIndices[k++] = (short) ((num + 1) * (i) + j + 1);
            }
        }
    }

    public void clear() {
        Matrix.setIdentityM(transformationMatrix, 0);
    }

    public void computePlaneTexture() {
        int n = mVertices.length / 3;
        mTextureCoordinates = new float[2 * n];
        int num = (int) Math.sqrt((float) n);

        for (int i = 0; i < num; i++) {
            for (int j = 0; j < num; j++) {
                mTextureCoordinates[2 * (i * num + j)] = (float) i / num;
                mTextureCoordinates[2 * (i * num + j) + 1] = (float) (1.0 - (float) j / num);
            }
        }
    }

    public int loadShader(int type, String shaderCode) {
        int shader = GLES30.glCreateShader(type);
        if (shader != 0) {
            GLES30.glShaderSource(shader, shaderCode);
            GLES30.glCompileShader(shader);
            final int[] compileStatus = new int[1];
            GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compileStatus, 0);

            if (compileStatus[0] == 0) {
                Log.e(TAG, "Error compiling shader: " + GLES30.glGetShaderInfoLog(shader));
                GLES30.glDeleteShader(shader);
                shader = 0;
            }
        }
        if (shader == 0)
            throw new RuntimeException("Error creating shader.");
        return shader;
    }

    public String readShaderFromRawResource(final Context context, final int resourceId) {
        final InputStream inputStream = context.getResources().openRawResource(resourceId);
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String nextLine;
        final StringBuilder body = new StringBuilder();

        try {
            while ((nextLine = bufferedReader.readLine()) != null) {
                body.append(nextLine);
                body.append('\n');
            }
        } catch (IOException e) {
            return null;
        }

        return body.toString();
    }

    @Override
    public void translate(final float x, final float y, final float z) {
        this.mPosition.dX += x;
        this.mPosition.dY += y;
        this.mPosition.dZ += z;

        float[] tmp = new float[16];
        Matrix.setIdentityM(tmp, 0);
        Matrix.translateM(tmp, 0, x, y, z);
        Matrix.multiplyMM(transformationMatrix, 0, tmp, 0, Arrays.copyOf(transformationMatrix, 16), 0);
        //Matrix.translateM(transformationMatrix, 0, x, y, z);
    }

    @Override
    public void teleport(float x, float y, float z) {
        // TODO Auto-generated method stub
    }

    @Override
    public void rotate(float a, float x, float y, float z) {
        float[] tmp = new float[16];
        Matrix.setIdentityM(tmp, 0);
        Matrix.rotateM(tmp, 0, a, x, y, z);
        Matrix.multiplyMM(transformationMatrix, 0, tmp, 0, Arrays.copyOf(transformationMatrix, 16), 0);
        //Matrix.rotateM(transformationMatrix, 0, a, x, y, z);

        if (a == -90 && x == 1 && y == 0 && z == 0) {
            float tmpMin = -this.mEdgePositionMin.dY;
            float tmpMax = -this.mEdgeVectorPositionMax.dY;
            mEdgePositionMin.dY = mEdgePositionMin.dZ;
            mEdgeVectorPositionMax.dY = mEdgeVectorPositionMax.dZ;
            mEdgePositionMin.dZ = tmpMin;
            mEdgeVectorPositionMax.dZ = tmpMax;
        }
    }

    @Override
    public void scale(float rate) {
        for (int i = 0; i < mVertices.length; i++) {
            mVertices[i] *= rate;
        }

        mEdgePositionMin.dX = (mEdgePositionMin.dX - (mEdgeVectorPositionMax.dX + mEdgePositionMin.dX) / 2) * rate;
        mEdgePositionMin.dY = (mEdgePositionMin.dY - (mEdgeVectorPositionMax.dY + mEdgePositionMin.dY) / 2) * rate;
        mEdgePositionMin.dZ = (mEdgePositionMin.dZ - (mEdgeVectorPositionMax.dZ + mEdgePositionMin.dZ) / 2) * rate;

        mEdgeVectorPositionMax.dX = (mEdgeVectorPositionMax.dX - (mEdgeVectorPositionMax.dX + mEdgePositionMin.dX) / 2) * rate;
        mEdgeVectorPositionMax.dY = (mEdgeVectorPositionMax.dY - (mEdgeVectorPositionMax.dY + mEdgePositionMin.dY) / 2) * rate;
        mEdgeVectorPositionMax.dZ = (mEdgeVectorPositionMax.dZ - (mEdgeVectorPositionMax.dZ + mEdgePositionMin.dZ) / 2) * rate;
    }

    @Override
    public Entity isInside(final Entity object) {
        // TODO Use the cube made by the extreme point : easier

        if (object.mPhysic.noContact || mPhysic.noContact) {
            return null;
        }

        if (object.mEdgePositionMin.dX + object.mPosition.dX > mEdgeVectorPositionMax.dX + mPosition.dX ||
                mEdgePositionMin.dX + mPosition.dX > object.mEdgeVectorPositionMax.dX + object.mPosition.dX) {
            return null;
        }

        if (object.mEdgePositionMin.dY + object.mPosition.dY > mEdgeVectorPositionMax.dY + mPosition.dY ||
                mEdgePositionMin.dY + mPosition.dY > object.mEdgeVectorPositionMax.dY + object.mPosition.dY) {
            return null;
        }

        if (object.mEdgePositionMin.dZ + object.mPosition.dZ > mEdgeVectorPositionMax.dZ + mPosition.dZ ||
                mEdgePositionMin.dZ + mPosition.dZ > object.mEdgeVectorPositionMax.dZ + object.mPosition.dZ) {
            return null;
        }

        return object;
    }

    public boolean isInside(final EntityGroup contacts) {
        entitiesContact = new ArrayList<>();
        // Check contact
        for (Entity entity : contacts.mEntities) {
            if (entity.mId != mId && isInside(entity) != null) {
                entitiesContact.add(entity);
            }
        }
        return (entitiesContact.size() != 0);
    }

    @Override
    public void translateRepeatedWayPosition() {
        if (repetedWayPosition != null) {
            Vector3D tmp;
            if ((tmp = repetedWayPosition.getCurrentPosition()) != null) {
                translate(tmp.dX - mPosition.dX, tmp.dY - mPosition.dY, tmp.dZ - mPosition.dZ);
            }
        }
    }

    /**
     * Execute by the physics thread.
     * Compute the Newton 2nd law.
     */
    @Override
    public void computeForces(final EntityGroup contacts) {

        /* ************** */
        /*  NEWTON 2 LAW  */
        /* ************** */

        if (mPhysic.mass != 0) {

            mSumForces.dX = 0;
            mSumForces.dY = 0;
            mSumForces.dZ = 0;

            for (final Force force : mForces) {
                final Vector3D forceV = force.getForceV(this);
                mSumForces.dX += forceV.dX * ((force.dotMass) ? mPhysic.mass : 1);
                mSumForces.dY += forceV.dY * ((force.dotMass) ? mPhysic.mass : 1);
                mSumForces.dZ += forceV.dZ * ((force.dotMass) ? mPhysic.mass : 1);
            }

            if (contactEnable) {
                final boolean isInside = isInside(contacts);

                if ((mPosition.dY <= 0.0f + Math.abs(mEdgePositionMin.dY)) && mVelocity.dY < 0) { // Cheat Contact with floor : contact force
                    if (mContactFloorListener != null && mContactFloorListener.condition(this)) {
                        mContactFloorListener.execute(this);
                    }
                    mVelocity.dY = -mVelocity.dY * 0.65f;
                } else if (mIsInsideLastLoop && isInside) {
                    // Spring force : thanks teacher idea
                    if (entitiesContact != null) {
                        for (Entity entityContact : entitiesContact) {
                            if (entityContact.mEdgePositionMin.dY + entityContact.mPosition.dY < mEdgePositionMin.dY + mPosition.dY &&
                                    mEdgePositionMin.dY + mPosition.dY < entityContact.mEdgeVectorPositionMax.dY + entityContact.mPosition.dY) {

                                if ((entityContact.mEdgeVectorPositionMax.dY + entityContact.mPosition.dY) - (mEdgePositionMin.dY + mPosition.dY) + mPosition.dY > 0) {
                                    mSumForces.dY += 0.000003f * ((entityContact.mEdgeVectorPositionMax.dY + entityContact.mPosition.dY) - (mEdgePositionMin.dY + mPosition.dY)) / Math.abs(entityContact.mEdgeVectorPositionMax.dY - entityContact.mEdgePositionMin.dY);
                                }
                            } else {
                                if ((entityContact.mEdgePositionMin.dY + entityContact.mPosition.dY) - (mEdgeVectorPositionMax.dY + mPosition.dY) + mPosition.dY > 0) {
                                    mSumForces.dY += 0.000003f * ((entityContact.mEdgePositionMin.dY + entityContact.mPosition.dY) - (mEdgeVectorPositionMax.dY + mPosition.dY)) / Math.abs(entityContact.mEdgeVectorPositionMax.dY - entityContact.mEdgePositionMin.dY);
                                }
                            }
                        }
                    }
                } else if (isInside) {
                    mIsInsideLastLoop = true;
                    mVelocity.dX = -mVelocity.dX * 0.65f;
                    mVelocity.dY = -mVelocity.dY * 0.65f;
                    mVelocity.dZ = -mVelocity.dZ * 0.65f;
                }

                /*
                else if ((isInside) && this.mVelocity.dY < 0) { // Contact with object
                    mIsInsideLastLoop = true;
                    mVelocity.dY = -mVelocity.dY * 0.65f; // Cheat

                    //mVelocity.dY = entityContact.mVelocity.dY * 1.0f; // Cheat

                    //mVelocity.dY = 0;
					//for(Entity ent : entitiesContact)
					//	mVelocity.dY += ent.mVelocity.dY*ent.mVelocity.dY ;

                    //this.mVelocity.dY = (float) Math.sqrt(this.mVelocity.dY);

                    //this.mVelocity.dY = (entityContact.mPhysic.mass/mPhysic.mass) * entityContact.mVelocity.dY * 1.0f; // Best equation
                } else if (isInside) {
                    mIsInsideLastLoop = true;
                    mVelocity.dX = -mVelocity.dX * 0.65f;
                    mVelocity.dZ = -mVelocity.dZ * 0.65f;
                }
                */

                else if (!isInside) {
                    mIsInsideLastLoop = false;
                }
            }
        }
    }

    @Override
    public void applyForces(final EntityGroup contacts) {
        if (mPhysic.mass != 0) {

            mAcceleration.dX = (mSumForces.dX) / mPhysic.mass;
            mAcceleration.dY = (mSumForces.dY) / mPhysic.mass;
            mAcceleration.dZ = (mSumForces.dZ) / mPhysic.mass;

            if ((PhysicsConst.REAL_LOOP_TIME * (mVelocity.dY + PhysicsConst.REAL_LOOP_TIME * mAcceleration.dY / 2) < 0 &&
                    mPosition.dY - Math.abs(mEdgePositionMin.dY) < 0)) {

                mVelocity.dX *= 0.97;
                mVelocity.dZ *= 0.97;

                translate(
                        PhysicsConst.REAL_LOOP_TIME * (mVelocity.dX + PhysicsConst.REAL_LOOP_TIME * mAcceleration.dX / 2),
                        0,
                        PhysicsConst.REAL_LOOP_TIME * (mVelocity.dZ + PhysicsConst.REAL_LOOP_TIME * mAcceleration.dZ / 2));
            } else {
                translate(
                        PhysicsConst.REAL_LOOP_TIME * (mVelocity.dX + PhysicsConst.REAL_LOOP_TIME * mAcceleration.dX / 2),
                        PhysicsConst.REAL_LOOP_TIME * (mVelocity.dY + PhysicsConst.REAL_LOOP_TIME * mAcceleration.dY / 2),
                        PhysicsConst.REAL_LOOP_TIME * (mVelocity.dZ + PhysicsConst.REAL_LOOP_TIME * mAcceleration.dZ / 2));
            }

            mVelocity.dX += PhysicsConst.REAL_LOOP_TIME * mAcceleration.dX;
            mVelocity.dY += PhysicsConst.REAL_LOOP_TIME * mAcceleration.dY;
            mVelocity.dZ += PhysicsConst.REAL_LOOP_TIME * mAcceleration.dZ;
        }
    }

    @Override
    public void addForce(Force force) {
        mForces.add(force);
    }
}
