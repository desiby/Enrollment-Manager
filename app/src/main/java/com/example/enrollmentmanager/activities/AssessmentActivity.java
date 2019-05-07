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
import com.example.enrollmentmanager.adapters.AssessmentAdapter;
import com.example.enrollmentmanager.database.DBhelper;
import com.example.enrollmentmanager.models.Assessment;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class AssessmentActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private DBhelper mDatabase;
    private ArrayList<Assessment> allAssessment = new ArrayList<>();
    private AssessmentAdapter assessmentAdapter;
    private String courseChoice;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment);

        FloatingActionButton btnAddTerm = findViewById(R.id.btnAddAssessment);
        btnAddTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               addAssessment();
            }
        });

        //define a RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerview_assessement);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        mDatabase = new DBhelper(this);
        allAssessment = mDatabase.getAllAssessmentsWithCourses();

        if(allAssessment.size() > 0){
            recyclerView.setVisibility(View.VISIBLE);
            assessmentAdapter = new AssessmentAdapter(this, allAssessment);
            recyclerView.setAdapter(assessmentAdapter);

        }else {
            recyclerView.setVisibility(View.GONE);
            Toast.makeText(this, "There is no assessment in the database. Start adding now",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void addAssessment() {

        LayoutInflater inflater = LayoutInflater.from(this);
        View subView = inflater.inflate(R.layout.add_edit_assessment, null);

        final EditText assessmentNameEditText = subView.findViewById(R.id.editText_assessment_name);
        final Spinner spinnerCourseChoice = subView.findViewById(R.id.spinner_course_choice);
        final EditText due_dateEditText = subView.findViewById(R.id.editText_assessment_dueDate);

        //query db to get list of course title for the spinner
        mDatabase = new DBhelper(this);
        ArrayList<String> courseTitleList = new ArrayList<>();
        Cursor cursor = mDatabase.getAllCoursesforSpinner();
        while(cursor.moveToNext()){
            String courseTitle = cursor.getString(1);
            courseTitleList.add(courseTitle);
        }
        cursor.close();
        //set term spinner
        ArrayAdapter<String> courseSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,courseTitleList );
        spinnerCourseChoice.setAdapter(courseSpinnerAdapter);
        spinnerCourseChoice.setOnItemSelectedListener(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Assessment");
        builder.setView(subView);
        builder.create();

        builder.setPositiveButton("SAVE ASSESSMENT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                final String assessmentName = assessmentNameEditText.getText().toString();
                final String dueDate = due_dateEditText.getText().toString();

                if(TextUtils.isEmpty(assessmentName)||TextUtils.isEmpty(dueDate)){
                    Toast.makeText(AssessmentActivity.this, "Fields cannot be empty!", Toast.LENGTH_LONG).show();
                }else {
                    try {
                        Assessment assessment = new Assessment(assessmentName, courseChoice, LocalDate.parse(dueDate, formatter));
                        mDatabase.addAssessment(assessment);
                        //refresh
                        finish();
                        startActivity(getIntent());
                    }catch(Exception ex){
                        Log.d("EXCEPTION: ",ex.getMessage());
                    }
                }
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(AssessmentActivity.this, "add cancelled!!!", Toast.LENGTH_LONG).show();
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        courseChoice = parent.getItemAtPosition(position).toString();
        Log.d("SPINNER: ", parent.getItemAtPosition(position) + " selected!");
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
