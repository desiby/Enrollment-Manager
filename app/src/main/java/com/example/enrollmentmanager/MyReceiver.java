package com.example.enrollmentmanager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.example.enrollmentmanager.database.DBhelper;
import com.example.enrollmentmanager.models.Assessment;
import com.example.enrollmentmanager.models.CourseDetails;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;


public class MyReceiver extends BroadcastReceiver {

    static Random id = new Random(2000);
    static final String ENROLLMENT_CHANNEL_ID = "Enrollment";
    static final String ASSESSMENT_GROUP_KEY = "com.example.enrollmentmanager.ASSESSMENT_GROUP";
    static final String COURSE_GROUP_KEY = "com.example.enrollmentmanager.COURSE_GROUP";
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private  Context context = GlobalApplication.getAppContext();

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        createEnrollmentNotificationChannel(context);
        createCourseNotificationStartDate();
        createCourseNotificationEndDate();
        createAssessmentDueDateNotification();

    }

    // Create the COURSE NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    //create channel object with a unique ID
    private void createEnrollmentNotificationChannel(Context context){
        NotificationChannel notificationChannel = new NotificationChannel(ENROLLMENT_CHANNEL_ID,
                "course", NotificationManager.IMPORTANCE_DEFAULT);
        //submit notification channel object to notification manager
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    //Notifications alert for course start date
    private void createCourseNotificationStartDate(){
        DBhelper mDatabase = new DBhelper(context);
        ArrayList<CourseDetails> allCourses;
        allCourses = mDatabase.getAllCourseDetailsWithTerm();

        for (CourseDetails course : allCourses){
            if(course.getStart_date().isEqual(LocalDate.now())) {
                Notification notification = new NotificationCompat.Builder(context, ENROLLMENT_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_library_books_black_24dp)
                        .setContentTitle("course started")
                        .setContentText(course.getTitle() + " starts " + course.getStart_date().format(formatter))
                        .setGroup(COURSE_GROUP_KEY)
                        .build();
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(id.nextInt(), notification);
            }
        }
    }

    //Notifications alert for course end date
    private void createCourseNotificationEndDate(){
        DBhelper mDatabase = new DBhelper(context);
        ArrayList<CourseDetails> allCourses;
        allCourses = mDatabase.getAllCourseDetailsWithTerm();

        for (CourseDetails course : allCourses){
            if(course.getEnd_date().isEqual(LocalDate.now())) {
                Notification notification = new NotificationCompat.Builder(context, ENROLLMENT_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_library_books_black_24dp)
                        .setContentTitle("course ended")
                        .setContentText(course.getTitle() + " ends " + course.getEnd_date().format(formatter))
                        .setGroup(COURSE_GROUP_KEY)
                        .build();
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(id.nextInt(), notification);
            }
        }
    }

    //Notification alert for assessment due date
    private void createAssessmentDueDateNotification(){
        DBhelper mDatabase = new DBhelper(context);
        ArrayList<Assessment> allAssessment;
        allAssessment = mDatabase.getAllAssessmentsWithCourses();

        for (Assessment assessment : allAssessment) {
            if(assessment.getDue_date().isEqual(LocalDate.now())) {
                Notification notification = new NotificationCompat.Builder(context, ENROLLMENT_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_event_note_black_24dp)
                        .setContentTitle("Assessment due")
                        .setContentText(assessment.getName() + " due on " + assessment.getDue_date().format(formatter))
                        .setGroup(ASSESSMENT_GROUP_KEY)
                        .build();
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(id.nextInt(), notification);
            }
        }
    }

}
