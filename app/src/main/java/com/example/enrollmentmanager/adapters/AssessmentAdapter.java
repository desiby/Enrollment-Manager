package com.example.enrollmentmanager.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.enrollmentmanager.R;
import com.example.enrollmentmanager.database.DBhelper;
import com.example.enrollmentmanager.models.Assessment;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class AssessmentAdapter extends RecyclerView.Adapter<AssessmentAdapter.AssessmentHolder> implements AdapterView.OnItemSelectedListener {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private Context context;
    private DBhelper mDatabase;
    private ArrayList<Assessment> assessmentList;

    private String courseChoice;
    

    public AssessmentAdapter(Context context, ArrayList<Assessment> assessmentList) {
        this.context = context;
        this.assessmentList = assessmentList;
        mDatabase = new DBhelper(context);

    }

    @NonNull
    @Override
    public AssessmentHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
       View assessmentView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.assessment_item, viewGroup,false);
        return new AssessmentHolder(assessmentView);
    }

    @Override
    public void onBindViewHolder(@NonNull AssessmentHolder assessmentHolder, int position) {

        final Assessment currentAssessment = assessmentList.get(position);

        assessmentHolder.assessmentIdTextView.setText(String.valueOf(currentAssessment.getId()));
        assessmentHolder.assessmentNameTextView.setText(currentAssessment.getName());
        assessmentHolder.courseTitleTextView.setText(currentAssessment.getCourseTitle());
        assessmentHolder.due_date.setText(currentAssessment.getDue_date().format(formatter));

        assessmentHolder.deleteAssessmentIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.deleteAssessment(currentAssessment.getId());
                //refresh the activity page.
                ((Activity) context).finish();
                context.startActivity(((Activity) context).getIntent());
            }
        });

        assessmentHolder.editAssessmentIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editAssessmentDialog(currentAssessment);
            }
        });

    }

    //Edit assessment dialog
    private void editAssessmentDialog(final Assessment assessment) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View subView = inflater.inflate(R.layout.add_edit_assessment, null);

        final EditText assessmentNameEditText = subView.findViewById(R.id.editText_assessment_name);
        final Spinner spinnerCourseChoice = subView.findViewById(R.id.spinner_course_choice);
        final EditText dueDateEditText = subView.findViewById(R.id.editText_assessment_dueDate);

        if(assessment != null){
            assessmentNameEditText.setText(assessment.getName());
            dueDateEditText.setText(assessment.getDue_date().format(formatter));


            //query db to get list of course title for the spinner
            mDatabase = new DBhelper(context);
            ArrayList<String> courseTitleList = new ArrayList<>();
            Cursor cursor = mDatabase.getAllCoursesforSpinner();
            while(cursor.moveToNext()){
                String courseTitle = cursor.getString(1);
                courseTitleList.add(courseTitle);
            }
            cursor.close();
            //set term spinner
            ArrayAdapter<String> courseSpinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item,courseTitleList );
            spinnerCourseChoice.setAdapter(courseSpinnerAdapter);
            spinnerCourseChoice.setOnItemSelectedListener(this);

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Edit Assessment");
            builder.setView(subView);
            builder.create();

            builder.setPositiveButton("UPDATE ASSESSMENT", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final String assessmentName = assessmentNameEditText.getText().toString();
                    final String dueDate = dueDateEditText.getText().toString();

                    if(TextUtils.isEmpty(assessmentName)||TextUtils.isEmpty(dueDate)){
                        Toast.makeText(context, "Fields cannot be empty!", Toast.LENGTH_LONG).show();
                    }else{
                        try{
                           mDatabase.updateAssessment(new Assessment(assessment.getId(),assessmentName,
                                   courseChoice, LocalDate.parse(dueDate,formatter)));
                            //refresh the activity
                            ((Activity) context).finish();
                            context.startActivity(((Activity) context).getIntent());
                        }catch(Exception ex){
                            Log.d("EXCEPTION: ",ex.getMessage());
                        }
                    }
                }
            });

            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
        }
    }

    @Override
    public int getItemCount() {
        return assessmentList.size();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        courseChoice = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //Define a class that holds an assessment
    class AssessmentHolder extends RecyclerView.ViewHolder{

        private TextView assessmentIdTextView;
        private TextView assessmentNameTextView;
        private TextView courseTitleTextView;
        private TextView due_date;
        private ImageView deleteAssessmentIcon;
        private ImageView editAssessmentIcon;

        public AssessmentHolder(@NonNull View itemView) {
            super(itemView);
            assessmentIdTextView = itemView.findViewById(R.id.textview_assessment_id);
            assessmentNameTextView = itemView.findViewById(R.id.textview_assessment_name);
            courseTitleTextView = itemView.findViewById(R.id.textview_ass_course_title);
            due_date = itemView.findViewById(R.id.textview_due_date);
            deleteAssessmentIcon = itemView.findViewById(R.id.icon_delete_assessment);
            editAssessmentIcon = itemView.findViewById((R.id.icon_edit_assessment));

        }
    }
}
