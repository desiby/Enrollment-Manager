package com.example.enrollmentmanager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.enrollmentmanager.activities.AssessmentActivity;
import com.example.enrollmentmanager.activities.CourseDetailsActivity;
import com.example.enrollmentmanager.activities.TermActivity;
import com.example.enrollmentmanager.database.DBhelper;
import com.example.enrollmentmanager.models.Assessment;
import com.example.enrollmentmanager.models.CourseDetails;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DBhelper mDatabase = new DBhelper(this);
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }


    //trigger notification when app not running
    @Override
    protected void onPause(){
        super.onPause();
        triggerAssessmentDueDateNotification();
        triggerCourseNotificationStartDate();
        triggerCourseNotificationEndDate();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_course_details) {
           Intent intent = new Intent(MainActivity.this, CourseDetailsActivity.class);
           startActivity(intent);

        } else if (id == R.id.nav_term) {
            Intent intent = new Intent(MainActivity.this, TermActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_assessment) {
            Intent intent = new Intent(MainActivity.this, AssessmentActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //trigger assessment due date notification
    private void triggerAssessmentDueDateNotification(){
        ArrayList<Assessment> allAssessment;
        allAssessment = mDatabase.getAllAssessmentsWithCourses();
        Intent intent = new Intent(MainActivity.this,MyReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this,0,intent,0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        for(Assessment assessment : allAssessment){
            LocalDate localDate = assessment.getDue_date();
            if(localDate.isEqual(LocalDate.now())) {
                Date date = java.sql.Date.valueOf(localDate.format(formatter));
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                long milliSec = calendar.getTimeInMillis();
                alarmManager.set(AlarmManager.RTC_WAKEUP, milliSec, sender);
            }
        }
    }

    //trigger course start date notification
    private void triggerCourseNotificationStartDate(){
        ArrayList<CourseDetails> allCourses;
        allCourses = mDatabase.getAllCourseDetailsWithTerm();
        Intent intent = new Intent(MainActivity.this,MyReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this,0,intent,0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        for(CourseDetails course : allCourses){
         LocalDate localDateStart = course.getStart_date();
         if(localDateStart.isEqual(LocalDate.now())) {
             Date date = java.sql.Date.valueOf(localDateStart.format(formatter));
             Calendar calendar = Calendar.getInstance();
             calendar.setTime(date);
             long millisec = calendar.getTimeInMillis();
             alarmManager.set(AlarmManager.RTC_WAKEUP, millisec, sender);
         }
        }
    }

    //trigger course end date notification
    private void triggerCourseNotificationEndDate(){
        ArrayList<CourseDetails> allCourses;
        allCourses = mDatabase.getAllCourseDetailsWithTerm();
        Intent intent = new Intent(MainActivity.this,MyReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this,0,intent,0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        for(CourseDetails course : allCourses) {
            LocalDate localDateEnd = course.getEnd_date();
            if (localDateEnd.isEqual(LocalDate.now())) {
                Date date = java.sql.Date.valueOf(localDateEnd.format(formatter));
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                long millisec = calendar.getTimeInMillis();
                alarmManager.set(AlarmManager.RTC_WAKEUP, millisec, sender);
            }
        }
    }
}
