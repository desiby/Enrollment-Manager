package com.example.enrollmentmanager.activities;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.enrollmentmanager.R;
import com.example.enrollmentmanager.adapters.CourseDetailsAdapter;
import com.example.enrollmentmanager.database.DBhelper;
import com.example.enrollmentmanager.models.CourseDetails;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class CourseDetailsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private DBhelper mDatabase;

    private ArrayList<CourseDetails> allCourses = new ArrayList<>();
    private CourseDetailsAdapter courseDetailsAdapter;

    private String termChoice;
    private String statusChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);

        FloatingActionButton btnAddCourseDetails = findViewById(R.id.btn_add_course);
        btnAddCourseDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              addCourseDetails();
            }
        });

        //define a RecyclerView
        RecyclerView recyclerView = findViewById(R.id.course_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        mDatabase = new DBhelper(this);
        allCourses = mDatabase.getAllCourseDetailsWithTerm();

        if(allCourses.size() > 0){
            recyclerView.setVisibility(View.VISIBLE);
            courseDetailsAdapter = new CourseDetailsAdapter(this, allCourses);
            recyclerView.setAdapter(courseDetailsAdapter);

        }else {
            recyclerView.setVisibility(View.GONE);
            Toast.makeText(this, "There is no course in the database. Start adding now",
                    Toast.LENGTH_LONG).show();
        }

    }

    private void addCourseDetails() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View subView = inflater.inflate(R.layout.add_edit_course_details, null);

        final EditText courseTitleEditxt = subView.findViewById(R.id.editTxt_course_title);
        final Spinner termTitleChoice = subView.findViewById(R.id.spinner_term_choice);
        final EditText cStartDateEditxt = subView.findViewById(R.id.editText_course_start_date);
        final EditText cEndDateEditxt = subView.findViewById(R.id.editText_course_end_date);
        final Spinner courseStatus = subView.findViewById(R.id.spinner_status_choice);
        final EditText cMentorNameEditxt = subView.findViewById(R.id.editText_mentor_name);
        final EditText cMentorPhoneEditxt = subView.findViewById(R.id.editText_mentor_phone);
        final EditText cMentorEmailEditxt = subView.findViewById(R.id.editText_mentor_email);
        final EditText optionalNotesEditxt = subView.findViewById(R.id.editText_optional_note);

        // -status choice- Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.status_array,
                android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        courseStatus.setAdapter(adapter);
        courseStatus.setOnItemSelectedListener(this);

        //query db to get list of term name for the spinner
        mDatabase = new DBhelper(this);
        ArrayList<String> TermNames = new ArrayList<>();
        Cursor cursor = mDatabase.getAllTermsForSpinner();
        while(cursor.moveToNext()){
            String termName = cursor.getString(1);
            TermNames.add(termName);
        }
        cursor.close();
        //set term spinner
        ArrayAdapter<String> termSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,TermNames );
        termTitleChoice.setAdapter(termSpinnerAdapter);
        termTitleChoice.setOnItemSelectedListener(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Course");
        builder.setView(subView);
        builder.create();

        builder.setPositiveButton("SAVE COURSE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String courseTitle = courseTitleEditxt.getText().toString();
                final String startDate = cStartDateEditxt.getText().toString();
                final String endDate = cEndDateEditxt.getText().toString();
                final String mentorName = cMentorNameEditxt.getText().toString();
                final String mentorPhone = cMentorPhoneEditxt.getText().toString();
                final String mentorEmail = cMentorEmailEditxt.getText().toString();
                final String optional_notes = optionalNotesEditxt.getText().toString();

                if (TextUtils.isEmpty(courseTitle) || TextUtils.isEmpty(startDate)
                        || TextUtils.isEmpty(endDate) || TextUtils.isEmpty(optional_notes) ||
                        TextUtils.isEmpty(mentorName) || TextUtils.isEmpty(mentorPhone) ||
                        TextUtils.isEmpty(mentorEmail)) {
                    Toast.makeText(CourseDetailsActivity.this, "Fields cannot be empty!", Toast.LENGTH_LONG).show();
                  }else{
                    CourseDetails courseDetails = new CourseDetails(courseTitle, termChoice,
                            LocalDate.parse(startDate, formatter), LocalDate.parse(endDate, formatter),
                            statusChoice, mentorName, mentorPhone, mentorEmail, optional_notes);
                    mDatabase.addCourseDetails(courseDetails);

                    finish();
                    startActivity(getIntent());
                }
            }

        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(CourseDetailsActivity.this, "Add Cancelled!!!", Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mDatabase != null){
            mDatabase.close();
        }
    }

    //what to do when an item is selected
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // An item was selected. You can retrieve the selected item using
        if(parent.getId() == R.id.spinner_status_choice) {
            statusChoice = parent.getItemAtPosition(position).toString();
            Log.d("SPINNER: ", parent.getItemAtPosition(position) + " selected!");

        }else if(parent.getId() == R.id.spinner_term_choice){
            termChoice = parent.getItemAtPosition(position).toString();
            Log.d("SPINNER: ", parent.getItemAtPosition(position) + " selected!");
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}


