/**
 * ESIEE OpenSource Project : OpenGL
 * <p/>
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.esieeAPE;

import android.app.Activity;
import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Config {

    private Activity activity;
    private String file = "settings_json_1.txt";

    public Config(Activity my_activity) {
        this.activity = my_activity;
        load(my_activity);
    }

    private static void write_txt(Activity activity, String file, String txt) {
        try {
            // Flux interne
            final FileOutputStream output = activity.openFileOutput(file, Context.MODE_PRIVATE);

            // On ecrit dans le flux interne
            output.write((txt).getBytes());
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String read_txt(Activity activity, String file) {
        // Lecture
        String res = "";
        try {
            FileInputStream input = activity.openFileInput(file);
            int value;
            // On utilise un StringBuffer pour construire la chaine au fur et a mesure
            StringBuffer lu = new StringBuffer();
            // On lit les caracteres les uns apres les autres
            while ((value = input.read()) != -1) {
                // On ecrit dans le fichier le caractere lu
                lu.append((char) value);
            }
            //Toast.makeText(PseudoActivity.this, "Bienvenue : " + lu.toString(), Toast.LENGTH_SHORT).show();
            if (input != null) {
                input.close();
                if (lu.toString() != null)
                    res = lu.toString();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (res == null) return "";
        return res;
    }

    private void save(Activity activity) {
        try {
            JSONObject tmp_json = new JSONObject();
            JSONObject tmp_settings_1 = new JSONObject();
            for (ENUM_Int enum_int : ENUM_Int.values())
                tmp_settings_1.put(enum_int.key, enum_int.value);
            for (ENUM_Boolean enum_boolean : ENUM_Boolean.values())
                tmp_settings_1.put(enum_boolean.key, enum_boolean.value);
            for (ENUM_String enum_string : ENUM_String.values())
                tmp_settings_1.put(enum_string.key, enum_string.value);
            tmp_json.put("settings_1", tmp_settings_1);
            write_txt(activity, file, tmp_json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void load(Activity activity) {
        try {
            JSONObject tmp_json = new JSONObject(read_txt(activity, file));
            if (tmp_json.has("settings_1")) {
                JSONObject tmp_settings_1 = tmp_json.getJSONObject("settings_1");
                for (ENUM_Int enum_int : ENUM_Int.values())
                    if (tmp_settings_1.has(enum_int.key))
                        enum_int.value = tmp_settings_1.getInt(enum_int.key);
                for (ENUM_Boolean enum_boolean : ENUM_Boolean.values())
                    if (tmp_settings_1.has(enum_boolean.key))
                        enum_boolean.value = tmp_settings_1.getBoolean(enum_boolean.key);
                for (ENUM_String enum_string : ENUM_String.values())
                    if (tmp_settings_1.has(enum_string.key))
                        enum_string.value = tmp_settings_1.getString(enum_string.key);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean isDisplayFPS() {
        return ENUM_Boolean.DISPLAY_FPS.value;
    }

    public void setDisplayFPS(boolean value) {
        if (ENUM_Boolean.DISPLAY_FPS.value != value) {
            ENUM_Boolean.DISPLAY_FPS.value = value;
            save(activity);
        }
    }

    public boolean isDisplayPosition() {
        return ENUM_Boolean.DISPLAY_FPS.value;
    }

    public void setDisplayPosition(boolean value) {
        if (ENUM_Boolean.DISPLAY_POSITION.value != value) {
            ENUM_Boolean.DISPLAY_POSITION.value = value;
            save(activity);
        }
    }

    public int getControlMode() {
        return ENUM_Int.CONTROL_MODE.value;
    }

    public void setControlMode(int value) {
        if (ENUM_Int.CONTROL_MODE.value != value) {
            ENUM_Int.CONTROL_MODE.value = value;
            OpenGLFragment.updateModeControlIcon(value);
            save(activity);
        }
    }

    private enum ENUM_Int {
        CONTROL_MODE(0, "int_control_mode"),;

        int value;
        String key;

        ENUM_Int(int init, String key) {
            this.value = init;
            this.key = key;
        }
    }

    private enum ENUM_Boolean {
        DISPLAY_FPS(true, "boolean_display_fps"),
        DISPLAY_POSITION(false, "boolean_display_position"),;

        boolean value;
        String key;

        ENUM_Boolean(boolean init, String key) {
            this.value = init;
            this.key = key;
        }
    }

    private enum ENUM_String {
        STRING("com.faitout", "string_1"),;

        String value;
        String key;

        ENUM_String(String init, String key) {
            this.value = init;
            this.key = key;
        }
    }
}
