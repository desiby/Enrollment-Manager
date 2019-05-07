package com.example.enrollmentmanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.enrollmentmanager.database.DBContract.TbAssessment;
import com.example.enrollmentmanager.database.DBContract.TbCourse;
import com.example.enrollmentmanager.database.DBContract.TbTerm;
import com.example.enrollmentmanager.models.Assessment;
import com.example.enrollmentmanager.models.CourseDetails;
import com.example.enrollmentmanager.models.Term;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class DBhelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "course_enrollment";
    private static final int VERSION = 3;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    //Term Table
    private static final String SQL_CREATE_TABLE_TERM =
            "CREATE TABLE " + TbTerm.TABLE_NAME + "("+
                    TbTerm._ID + " INTEGER PRIMARY KEY,"+
                    TbTerm.COLUMN_NAME_TITLE + " TEXT,"+
                    TbTerm.COLUMN_NAME_START_DATE + " DATE,"+
                    TbTerm.COLUMN_NAME_END_DATE + " DATE)";
    //Course Table
    private static final String SQL_CREATE_TABLE_COURSE =
            "CREATE TABLE " + TbCourse.TABLE_NAME + "("+
                    TbCourse._ID + " INTEGER PRIMARY KEY,"+
                    TbCourse.COLUMN_NAME_TITLE + " TEXT,"+
                    TbCourse.COLUMN_NAME_TERM_ID + " INTEGER,"+
                    TbCourse.COLUMN_NAME_START_DATE + " DATE,"+
                    TbCourse.COLUMN_NAME_END_DATE + " DATE,"+
                    TbCourse.COLUMN_NAME_STATUS + " TEXT," +
                    TbCourse.COLUMN_NAME_MENTOR_NAME + " TEXT," +
                    TbCourse.COLUMN_NAME_MENTOR_PHONE + " TEXT," +
                    TbCourse.COLUMN_NAME_MENTOR_EMAIL + " TEXT," +
                    TbCourse.COLUMN_NAME_NOTES + " TEXT," +
                    "FOREIGN KEY " +"("+TbCourse.COLUMN_NAME_TERM_ID+") REFERENCES " + TbTerm.TABLE_NAME+"("+TbTerm._ID+") ON DELETE RESTRICT" +
                    ")";
    //Assessment Table
    private static final String SQL_CREATE_TABLE_ASSESSMENT =
            "CREATE TABLE " + TbAssessment.TABLE_NAME + "("+
                    TbAssessment._ID + " INTEGER PRIMARY KEY,"+
                    TbAssessment.COLUMN_NAME_COURSE_ID + " TEXT,"+
                    TbAssessment.COLUMN_NAME_TITLE + " TEXT," +
                    DBContract.TbAssessment.COLUMN_NAME_DUE_DATE + " DATE," +
                    "FOREIGN KEY " +"("+TbAssessment.COLUMN_NAME_COURSE_ID+") REFERENCES " +TbCourse.TABLE_NAME+"("+TbCourse._ID+")" +
                    ")";


    private static final String SQL_DELETE_TERM = "DROP TABLE IF EXISTS " + TbTerm.TABLE_NAME;
    private static final String SQL_DELETE_COURSE = "DROP TABLE IF EXISTS " + TbCourse.TABLE_NAME;
    private static final String SQL_DELETE_ASSESSMENT = "DROP TABLE IF EXISTS " + TbAssessment.TABLE_NAME;


    public DBhelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_TERM);
        db.execSQL(SQL_CREATE_TABLE_COURSE);
        db.execSQL(SQL_CREATE_TABLE_ASSESSMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_TERM);
        db.execSQL(SQL_DELETE_COURSE);
        db.execSQL(SQL_DELETE_ASSESSMENT);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    /*----------- TERMS--------------------------------*/
    //List of all Terms
    public ArrayList<Term> getAllTerms(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TbTerm.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );
        ArrayList<Term> terms = new ArrayList<>();
        while(cursor.moveToNext()){
            long term_id = cursor.getLong(cursor.getColumnIndexOrThrow(TbTerm._ID));
            String term_title = cursor.getString(cursor.getColumnIndexOrThrow(TbTerm.COLUMN_NAME_TITLE));
            String termStartDate = cursor.getString(cursor.getColumnIndexOrThrow(TbTerm.COLUMN_NAME_START_DATE));
            String termEndDate = cursor.getString(cursor.getColumnIndexOrThrow(TbTerm.COLUMN_NAME_START_DATE));

            LocalDate startFetchedDate = LocalDate.parse(termStartDate, formatter);
            LocalDate endFetchedDate = LocalDate.parse(termEndDate, formatter);
            terms.add(new Term(term_id,term_title,startFetchedDate,endFetchedDate));
        }
        cursor.close();
        db.close();
        return terms;
    }

    //get a term by name
    public Cursor getTermByName(String name){
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = TbTerm.COLUMN_NAME_TITLE + " = ?";
        String[] projection = {TbTerm._ID};
        String[] selectionArgs = { name };
        return db.query(
                TbTerm.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );
    }

    //get a term by id
    public Cursor getTermById(Long id){
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = TbTerm._ID + " = ?";
        String[] projection = {TbTerm.COLUMN_NAME_TITLE};
        String[] selectionArgs = { String.valueOf(id) };
        return db.query(
                TbTerm.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );
    }

    //get all terms to be loaded in spinner
    public Cursor getAllTermsForSpinner(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TbTerm.TABLE_NAME,null,null,null,null,null,null);
    }

    //add a term
    public void addTerms(Term term){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TbTerm.COLUMN_NAME_TITLE, term.getTitle());
        values.put(TbTerm.COLUMN_NAME_START_DATE, term.getStart_date().format(formatter));
        values.put(TbTerm.COLUMN_NAME_END_DATE, term.getEnd_date().format(formatter));

        long newRowId = db.insert(TbTerm.TABLE_NAME, null, values);
        Log.d("DATABASE: ",+newRowId+" row");
        db.close();
    }

    //update a term
    public void updateTerms(Term term){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TbTerm.COLUMN_NAME_TITLE, term.getTitle());
        values.put(TbTerm.COLUMN_NAME_START_DATE, term.getStart_date().format(formatter));
        values.put(TbTerm.COLUMN_NAME_END_DATE, term.getEnd_date().format(formatter));
        // Define 'where' part of query.
        String selection = TbTerm._ID + " = ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { String.valueOf(term.getId()) };
        // Issue SQL statement.

        int count = db.update(
                TbTerm.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );
    }

    //delete a term
    public void deleteTerm(long id){

         SQLiteDatabase db = this.getWritableDatabase();
         int deleteRow = db.delete(TbTerm.TABLE_NAME, TbTerm._ID + " = ?", new String[]{String.valueOf(id)});
         db.close();
    }

    /*------------------------------------ COURSES--------------------------------*/

    //Get list of all courses
    public ArrayList<CourseDetails> getAllCourseDetailsWithTerm(){
        SQLiteDatabase db = this.getReadableDatabase();
        //join term/course table to grab term name
        Cursor cursor = db.rawQuery("select term.title, course._id, course.title, " +
                "course.start_date, course.end_date, course.status, course.mentor_name, course.mentor_phone, " +
                "course.mentor_email, course.notes from term " +
                "join course on term._id = course.term_id", null );

         ArrayList<CourseDetails> courseDetailsList = new ArrayList<>();
         while(cursor.moveToNext()){
            long courseDetails_id = cursor.getLong(cursor.getColumnIndexOrThrow(TbCourse._ID));
            String courseDetailsTitle = cursor.getString(cursor.getColumnIndexOrThrow(TbCourse.COLUMN_NAME_TITLE));
            String cTermName = cursor.getString(0);
            String courseDetailsStartDate = cursor.getString(cursor.getColumnIndexOrThrow(TbCourse.COLUMN_NAME_START_DATE));
            String courseDetailsEndDate = cursor.getString(cursor.getColumnIndexOrThrow(TbCourse.COLUMN_NAME_END_DATE));
            String cMentorName = cursor.getString(cursor.getColumnIndexOrThrow(TbCourse.COLUMN_NAME_MENTOR_NAME));
            String cMentorPhone = cursor.getString(cursor.getColumnIndexOrThrow(TbCourse.COLUMN_NAME_MENTOR_PHONE));
            String cMentorEmail = cursor.getString(cursor.getColumnIndexOrThrow(TbCourse.COLUMN_NAME_MENTOR_EMAIL));
            String courseStatus = cursor.getString(cursor.getColumnIndexOrThrow(TbCourse.COLUMN_NAME_STATUS));
            String optionalNotes = cursor.getString(cursor.getColumnIndexOrThrow(TbCourse.COLUMN_NAME_NOTES));

            LocalDate startFetchedDate = LocalDate.parse(courseDetailsStartDate, formatter);
            LocalDate endFetchedDate = LocalDate.parse(courseDetailsEndDate, formatter);

            CourseDetails courseDetails = new CourseDetails(courseDetails_id, courseDetailsTitle, cTermName,
                    startFetchedDate, endFetchedDate, courseStatus, cMentorName,
                    cMentorPhone, cMentorEmail, optionalNotes);
            courseDetailsList.add(courseDetails);

        }
        cursor.close();
        db.close();
        return courseDetailsList;
    }

    //Add course details
    public void addCourseDetails(CourseDetails courseDetails){
        long term_id = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        //grab term_id from term table
        String[] columns = {TbTerm._ID};
        String[] selectionArgs = {courseDetails.getTermName()};
        Cursor cursor = db.query(
                TbTerm.TABLE_NAME,   // The table to query
                columns,             // The array of columns to return (pass null to get all)
                TbTerm.COLUMN_NAME_TITLE + "= ? ",            // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );
        while(cursor.moveToNext()){
           term_id = cursor.getLong(cursor.getColumnIndexOrThrow(TbTerm._ID));
        }
        cursor.close();
        db.close();
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TbCourse.COLUMN_NAME_TITLE, courseDetails.getTitle());
        values.put(TbCourse.COLUMN_NAME_TERM_ID, term_id);
        values.put(TbCourse.COLUMN_NAME_START_DATE, courseDetails.getStart_date().format(formatter));
        values.put(TbCourse.COLUMN_NAME_END_DATE, courseDetails.getEnd_date().format(formatter));
        values.put(TbCourse.COLUMN_NAME_STATUS, courseDetails.getStatus());
        values.put(TbCourse.COLUMN_NAME_MENTOR_NAME, courseDetails.getMentorName());
        values.put(TbCourse.COLUMN_NAME_MENTOR_PHONE, courseDetails.getMentorPhone());
        values.put(TbCourse.COLUMN_NAME_MENTOR_EMAIL, courseDetails.getMentorEmail());
        values.put(TbCourse.COLUMN_NAME_NOTES, courseDetails.getOptionalNotes());

        long newRowId = db.insert(TbCourse.TABLE_NAME,null,values);
        Log.d("DATABASE: ",+newRowId+" course row inserted!");
        db.close();
    }

    //Update a Course
    public void updateCourseDetails(CourseDetails courseDetails){
        long termId = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        //grab term_id from term table
        String[] columns = {TbTerm._ID};
        String selection = TbTerm.COLUMN_NAME_TITLE + "= ? ";
        String[] selectionArgs = {courseDetails.getTermName()};
        Cursor cursor = db.query(TbTerm.TABLE_NAME,columns,selection,selectionArgs,null,null,null);
        while(cursor.moveToNext()){
            termId = cursor.getLong(cursor.getColumnIndexOrThrow(TbTerm._ID));
        }
        cursor.close();
        db.close();

        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TbCourse.COLUMN_NAME_TITLE, courseDetails.getTitle());
        values.put(TbCourse.COLUMN_NAME_TERM_ID, termId);
        values.put(TbCourse.COLUMN_NAME_START_DATE, courseDetails.getStart_date().format(formatter));
        values.put(TbCourse.COLUMN_NAME_END_DATE, courseDetails.getEnd_date().format(formatter));
        values.put(TbCourse.COLUMN_NAME_STATUS, courseDetails.getStatus());
        values.put(TbCourse.COLUMN_NAME_MENTOR_NAME, courseDetails.getMentorName());
        values.put(TbCourse.COLUMN_NAME_MENTOR_PHONE, courseDetails.getMentorPhone());
        values.put(TbCourse.COLUMN_NAME_MENTOR_EMAIL, courseDetails.getMentorEmail());
        values.put(TbCourse.COLUMN_NAME_NOTES, courseDetails.getOptionalNotes());
        // Define 'where' part of query.
        String sel = TbCourse._ID + " = ?";
        // Specify arguments in placeholder order.
        String[] selArgs = { String.valueOf(courseDetails.getId()) };
        // Issue SQL statement.
        int count = db.update(TbCourse.TABLE_NAME, values, sel, selArgs);
        db.close();
    }

    public void deleteCourseDetails(long id){
        SQLiteDatabase db = this.getWritableDatabase();
        int deleteRow = db.delete(TbCourse.TABLE_NAME, TbCourse._ID +" = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    /*-------------------------------ASSESSMENTS------------------------------------------*/

    //Get List of All Assessments including associated courses
    public ArrayList<Assessment> getAllAssessmentsWithCourses(){
        SQLiteDatabase db = this.getReadableDatabase();
        //join assessment/courses
        Cursor cursor = db.rawQuery("select assessment._id, course.title, assessment.course_id, assessment.name, " +
                "assessment.due_date from course join assessment on course._id = assessment.course_id",
                null);
        ArrayList<Assessment> assessmentsList = new ArrayList<>();
        while(cursor.moveToNext()){
            long assessmentId = cursor.getLong(cursor.getColumnIndexOrThrow(TbAssessment._ID));
            String assessmentName = cursor.getString(cursor.getColumnIndexOrThrow(TbAssessment.COLUMN_NAME_TITLE));
            String aCourseName = cursor.getString(1);
            String due_date = cursor.getString(4);

            LocalDate aDue_date = LocalDate.parse(due_date,formatter);

            assessmentsList.add(new Assessment(assessmentId,assessmentName,aCourseName,aDue_date));

        }
        cursor.close();
        db.close();
        return assessmentsList;
    }

    //Add Assessment
    public void addAssessment(Assessment assessment){
       //query course table to retrieve course id
        long course_id = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {TbCourse._ID};
        String selection = TbCourse.COLUMN_NAME_TITLE +" = ?";
        String[] selectionArgs = {assessment.getCourseTitle()};
        Cursor cursor = db.query(TbCourse.TABLE_NAME,columns,selection,selectionArgs,null,null,null);
        while(cursor.moveToNext()){
           course_id = cursor.getLong(cursor.getColumnIndexOrThrow(TbCourse._ID));
        }
        cursor.close();
        db.close();

        //add new row in assessment table using retrieve course id
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TbAssessment.COLUMN_NAME_TITLE, assessment.getName());
        values.put(TbAssessment.COLUMN_NAME_COURSE_ID, course_id);
        values.put(TbAssessment.COLUMN_NAME_DUE_DATE, assessment.getDue_date().format(formatter));
        long newRowId = db.insert(TbAssessment.TABLE_NAME,null,values);
        Log.d("DATABASE: ",+newRowId+" Assessment row inserted!");
    }

    //Update Assessment
    public void updateAssessment(Assessment assessment){
        //query course table to retrieve course id
        long course_id = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {TbCourse._ID};
        String selection = TbCourse.COLUMN_NAME_TITLE +" = ?";
        String[] selectionArgs = {assessment.getCourseTitle()};
        Cursor cursor = db.query(TbCourse.TABLE_NAME,columns,selection,selectionArgs,null,null,null);
        while(cursor.moveToNext()){
            course_id = cursor.getLong(cursor.getColumnIndexOrThrow(TbCourse._ID));
        }
        cursor.close();
        db.close();
        //update assessment using field course id retrieved
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TbAssessment.COLUMN_NAME_TITLE, assessment.getName());
        values.put(TbAssessment.COLUMN_NAME_COURSE_ID, course_id);
        values.put(TbAssessment.COLUMN_NAME_DUE_DATE, assessment.getDue_date().format(formatter));
        // Define 'where' part of query.
        String sel = TbAssessment._ID + " = ?";
        // Specify arguments in placeholder
        String[] selArgs = { String.valueOf(assessment.getId()) };
        // Issue SQL statement.
        int count = db.update(TbAssessment.TABLE_NAME, values, sel, selArgs);
        db.close();

    }

    //Delete Assessment
    public void deleteAssessment(long id){
       SQLiteDatabase db = this.getWritableDatabase();
       int deletedRow = db.delete(TbAssessment.TABLE_NAME,TbAssessment._ID+" = ?",new String[]{String.valueOf(id)});
    }

    //GET ALL COURSES TO BE LOADED IN SPINNER
    public Cursor getAllCoursesforSpinner(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TbCourse.TABLE_NAME,null,null,null,null,null,null);
    }
}



