package com.example.enrollmentmanager.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.example.enrollmentmanager.models.CourseDetails;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CourseDetailsAdapter extends RecyclerView.Adapter<CourseDetailsAdapter.CourseDetailsHolder> implements AdapterView.OnItemSelectedListener {
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private Context context;
    private DBhelper mDatabase;
    private List<CourseDetails> courseDetailsList;
    private String statusChoice;
    private String termChoice;

    public CourseDetailsAdapter(Context context, List<CourseDetails> courseDetailsList) {
        this.context = context;
        mDatabase = new DBhelper(context);
        this.courseDetailsList = courseDetailsList;
    }


    @NonNull
    @Override
    public CourseDetailsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View courseDetailsView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.course_details_item, viewGroup, false);
        return new CourseDetailsHolder(courseDetailsView);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseDetailsHolder courseDetailsHolder, int position) {

        final CourseDetails currentCourseDetails = courseDetailsList.get(position);

        courseDetailsHolder.courseIdTextView.setText(String.valueOf(currentCourseDetails.getId()));
        courseDetailsHolder.courseTitleTextView.setText(currentCourseDetails.getTitle());
        courseDetailsHolder.termTitleTextView.setText(currentCourseDetails.getTermName());
        courseDetailsHolder.courseStartDateTextView.setText(currentCourseDetails.getStart_date().format(formatter));
        courseDetailsHolder.courseEndDateTextView.setText(currentCourseDetails.getEnd_date().format(formatter));
        courseDetailsHolder.courseStatusTextView.setText(currentCourseDetails.getStatus());
        courseDetailsHolder.cMentorNameTextView.setText(currentCourseDetails.getMentorName());
        courseDetailsHolder.cMentorPhoneTextView.setText(currentCourseDetails.getMentorPhone());
        courseDetailsHolder.cMentorEmailTextView.setText(currentCourseDetails.getMentorEmail());
        courseDetailsHolder.courseOptionalNotesTextView.setText(currentCourseDetails.getOptionalNotes());

        //edit course even handler
        courseDetailsHolder.editCourseIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editCourseDetailsDialog(currentCourseDetails);
            }
        });

        //delete course event handler
        courseDetailsHolder.deleteCourseIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           try {
               mDatabase.deleteCourseDetails(currentCourseDetails.getId());
               //refresh the activity page.
               ((Activity) context).finish();
               context.startActivity(((Activity) context).getIntent());
               }catch(Exception ex){
                   Toast.makeText(context, "Cannot delete course associated with an assignment! ", Toast.LENGTH_LONG).show();
                   Log.d("<<< EXCEPTION:  ", ex.getMessage().toUpperCase()) ;
               }
            }
        });

        //share notes event handler
        courseDetailsHolder.shareIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, currentCourseDetails.getOptionalNotes() );
                sendIntent.setType("text/plain");
                context.startActivity(sendIntent);
            }
        });

    }
        //Edit course dialog method
        private void editCourseDetailsDialog ( final CourseDetails courseDetails){

            LayoutInflater inflater = LayoutInflater.from(context);
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

            if (courseDetails != null) {
                courseTitleEditxt.setText(courseDetails.getTitle());
                cStartDateEditxt.setText(courseDetails.getStart_date().format(formatter));
                cEndDateEditxt.setText(courseDetails.getEnd_date().format(formatter));
                cMentorNameEditxt.setText(courseDetails.getMentorName());
                cMentorPhoneEditxt.setText(courseDetails.getMentorPhone());
                cMentorEmailEditxt.setText(courseDetails.getMentorEmail());
                optionalNotesEditxt.setText(courseDetails.getOptionalNotes());

                // -status choice- Create an ArrayAdapter using the string array and a default spinner layout
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.status_array,
                        android.R.layout.simple_spinner_item);
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                courseStatus.setAdapter(adapter);
                courseStatus.setOnItemSelectedListener(this);
                //query db to get list of term name for the spinner
                mDatabase = new DBhelper(context);
                ArrayList<String> termNames = new ArrayList<>();
                Cursor cursor = mDatabase.getAllTermsForSpinner();
                while(cursor.moveToNext()){
                    String termName = cursor.getString(1);
                    termNames.add(termName);
                }
                cursor.close();
                //set term spinner
                ArrayAdapter<String> termSpinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item,termNames );
                termTitleChoice.setAdapter(termSpinnerAdapter);
                termTitleChoice.setOnItemSelectedListener(this);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Edit Course");
                builder.setView(subView);
                builder.create();

                builder.setPositiveButton("UPDATE COURSE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String courseTitle = courseTitleEditxt.getText().toString();
                        final String startDate = cStartDateEditxt.getText().toString();
                        final String endDate = cEndDateEditxt.getText().toString();
                        final String mentorName = cMentorNameEditxt.getText().toString();
                        final String mentorPhone = cMentorPhoneEditxt.getText().toString();
                        final String mentorEmail = cMentorEmailEditxt.getText().toString();
                        final String optional_notes = optionalNotesEditxt.getText().toString();
                      if(TextUtils.isEmpty(courseTitle)||TextUtils.isEmpty(startDate)||TextUtils.isEmpty(endDate)
                              ||TextUtils.isEmpty(optional_notes)){
                          Toast.makeText(context, "Fields cannot be empty!", Toast.LENGTH_LONG).show();
                      }else{
                          try {
                             mDatabase.updateCourseDetails(new CourseDetails(courseDetails.getId(),
                                     courseTitle,termChoice,LocalDate.parse(startDate,formatter),
                                      LocalDate.parse(endDate,formatter),statusChoice, mentorName,
                                     mentorPhone, mentorEmail, optional_notes));
                              //refresh the activity
                              ((Activity) context).finish();
                              context.startActivity(((Activity) context).getIntent());
                          }catch(Exception ex){
                              Log.d("EDIT TERM EXECPTION: ", ex.getMessage());
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
        public int getItemCount () {
            return courseDetailsList.size();
        }

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

    //defining a class holding the course
    class CourseDetailsHolder extends RecyclerView.ViewHolder {

            private TextView courseIdTextView;
            private TextView courseTitleTextView;
            private TextView termTitleTextView;
            private TextView courseStartDateTextView;
            private TextView courseEndDateTextView;
            private TextView courseStatusTextView;
            private TextView cMentorNameTextView;
            private TextView cMentorPhoneTextView;
            private TextView cMentorEmailTextView;
            private TextView courseOptionalNotesTextView;
            private ImageView editCourseIcon;
            private ImageView deleteCourseIcon;
            private ImageView shareIcon;

            public CourseDetailsHolder(View courseDetailsView) {
                super(courseDetailsView);
                courseIdTextView = courseDetailsView.findViewById(R.id.textView_course_id);
                courseTitleTextView = courseDetailsView.findViewById(R.id.course_title_textview);
                termTitleTextView = courseDetailsView.findViewById(R.id.term_title_textview);
                courseStartDateTextView = courseDetailsView.findViewById(R.id.course_start_textview);
                courseEndDateTextView = courseDetailsView.findViewById(R.id.course_end_textview);
                courseStatusTextView = courseDetailsView.findViewById(R.id.course_status_textview);
                cMentorNameTextView = courseDetailsView.findViewById(R.id.mentor_name_textview);
                cMentorPhoneTextView = courseDetailsView.findViewById(R.id.mentor_phone_textview);
                cMentorEmailTextView = courseDetailsView.findViewById(R.id.mentor_email_textview);
                courseOptionalNotesTextView = courseDetailsView.findViewById(R.id.optional_notes);
                editCourseIcon = courseDetailsView.findViewById(R.id.icon_edit_course);
                deleteCourseIcon = courseDetailsView.findViewById(R.id.icon_delete_course);
                shareIcon = courseDetailsView.findViewById(R.id.btn_share_notes);
            }
        }
}

