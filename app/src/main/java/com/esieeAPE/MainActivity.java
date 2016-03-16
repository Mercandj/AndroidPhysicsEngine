/**
 * ESIEE OpenSource Project : OpenGL
 * <p/>
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.esieeAPE;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;

import com.esieeAPE.lib.IFunction;
import com.esieeAPE.physics.PhysicsConst;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Main activity used with GLFragment in order to display the GLSurfaceView
 * Sliding Menu is used like buttons
 *
 * @author Jonathan
 */
public class MainActivity extends Activity {

    public static final int TYPE_IC = 0;
    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_SECTION = 2;
    public static int SECOND_FROM_LAUNCH;
    public static Config config;
    public static NavDrawerItemListe navDrawerItems;
    public NavDrawerItem TAB_1, TAB_2, TAB_3, TAB_4, TAB_5, TAB_6;
    GLFragment fragment;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        config = new Config(this);

        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        navDrawerItems = new NavDrawerItemListe();

        TAB_1 = new NavDrawerItem("APE  ESIEE", R.drawable.ic_launcher_b, new IFunction() {
            @Override
            public boolean condition() {
                return true;
            }

            @Override
            public void execute() {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.AlertDialogCustom));
                StringBuilder html = new StringBuilder();

                String post_html = "By " + getString(R.string.authors) + " : <font color=\"#26AEEE\"><i>ESIEE</i></font><br/>Version : <font color=\"#26AEEE\">" + getString(R.string.app_versionName) + "</font><br/>";
                String[] versions = getResources().getStringArray(R.array.versions_history);
                for (String version : versions) {
                    if (version.contains("Since "))
                        post_html += "<br/><b>" + version + "</b><br/>";
                    else
                        post_html += "<i>� " + version + "</i><br/>";
                }
                html.append(post_html);

                builder.setMessage(Html.fromHtml(html.toString()))
                        .setTitle(getString(R.string.app_name) + " " + getString(R.string.current_year))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.back), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }, TYPE_IC);
        navDrawerItems.add(TAB_1);

        TAB_2 = new NavDrawerItem("Resume", TYPE_NORMAL);
        navDrawerItems.add(TAB_2);

        TAB_3 = new NavDrawerItem("Restart", new IFunction() {
            @Override
            public boolean condition() {
                return true;
            }

            @Override
            public void execute() {
                fragment.mGLView.mRenderer.physicEngine.thread.isRunning = false;
                fragment.mGLView.onPause();
                fragment.mSensorManager.unregisterListener(fragment);

                fragment.mGLView.onResume();
                fragment.mSensorManager.registerListener(fragment, fragment.mRotation, SensorManager.SENSOR_DELAY_NORMAL);
                fragment.mGLView.mRenderer.physicEngine.thread.isRunning = true;
            }
        }, TYPE_NORMAL);
        //navDrawerItems.add(TAB_3);

        TAB_4 = new NavDrawerItem("High Cam", PhysicsConst.HIGH_CAMERA_SPEED_TRANSLATION, new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PhysicsConst.HIGH_CAMERA_SPEED_TRANSLATION = isChecked;
            }
        }, TYPE_NORMAL);
        navDrawerItems.add(TAB_4);

        TAB_5 = new NavDrawerItem("Control Mode", new IFunction() {
            @Override
            public boolean condition() {
                return true;
            }

            @Override
            public void execute() {
                final boolean[] mSelectedItems = new boolean[getResources().getStringArray(R.array.control_mode).length];
                mSelectedItems[config.getControlMode()] = true;
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(getString(R.string.app_name) + " " + getString(R.string.current_year))
                        .setItems(R.array.control_mode, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                config.setControlMode(which);
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }, TYPE_NORMAL);
        navDrawerItems.add(TAB_5);

        TAB_6 = new NavDrawerItem("Info Display", new IFunction() {
            @Override
            public boolean condition() {
                return true;
            }

            @Override
            public void execute() {
                final boolean[] mSelectedItems = new boolean[getResources().getStringArray(R.array.info_dispal).length];
                mSelectedItems[0] = config.isDisplayFPS();
                mSelectedItems[1] = config.isDisplayPosition();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(getString(R.string.app_name) + " " + getString(R.string.current_year))
                        .setMultiChoiceItems(R.array.info_dispal, mSelectedItems, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (which < mSelectedItems.length)
                                    mSelectedItems[which] = isChecked;
                            }
                        })
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                config.setDisplayFPS(mSelectedItems[0]);
                                config.setDisplayPosition(mSelectedItems[1]);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();
            }
        }, TYPE_NORMAL);
        navDrawerItems.add(TAB_6);

        mDrawerList.setAdapter(new NavDrawerListAdapter(this, navDrawerItems.getListe()));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        fragment = new GLFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        Timer t = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                SECOND_FROM_LAUNCH++;
            }
        };
        t.scheduleAtFixedRate(task, 0, 1000);
    }

    private void selectItem(int position) {
        for (NavDrawerItem nav : navDrawerItems.getListe())
            if (navDrawerItems.get(position).equals(nav))
                if (nav.listenerClick != null)
                    if (nav.listenerClick.condition())
                        nav.listenerClick.execute();

        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // The following call pauses the rendering thread.
        // If your OpenGL application is memory intensive,
        // you should consider de-allocating objects that
        // consume significant memory here.
        if (fragment != null) {
            fragment.mGLView.onPause();
            fragment.mSensorManager.unregisterListener(fragment);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The following call resumes a paused rendering thread.
        // If you de-allocated graphic objects for onPause()
        // this is a good place to re-allocate them.
        if (fragment != null) {
            fragment.mGLView.onResume();
            fragment.mSensorManager.registerListener(fragment, fragment.mRotation, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }
}
