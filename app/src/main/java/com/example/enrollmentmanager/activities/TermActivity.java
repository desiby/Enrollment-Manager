package com.example.enrollmentmanager.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.enrollmentmanager.R;
import com.example.enrollmentmanager.adapters.TermAdapter;
import com.example.enrollmentmanager.database.DBhelper;
import com.example.enrollmentmanager.models.Term;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class TermActivity extends AppCompatActivity {

    private DBhelper mDatabase;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private ArrayList<Term> allTerms = new ArrayList<>();
    private TermAdapter termAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term);


        FloatingActionButton btnAddTerm = findViewById(R.id.btn_add_term);
        btnAddTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               addTermDialog();
            }
        });

        //define a RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewTerms);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        mDatabase = new DBhelper(this);
        allTerms = mDatabase.getAllTerms();

        if(allTerms.size() > 0){
            recyclerView.setVisibility(View.VISIBLE);
            termAdapter = new TermAdapter(this, allTerms);
            recyclerView.setAdapter(termAdapter);

        }else {
            recyclerView.setVisibility(View.GONE);
            Toast.makeText(this, "There is no term in the database. Start adding now",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void addTermDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View subView = inflater.inflate(R.layout.add_edit_term, null);

        final  EditText termTitleEditxt = subView.findViewById(R.id.editText_term_title);
        final  EditText startDateEditxt = subView.findViewById(R.id.editText_term_start_date);
        final  EditText endDateEditxt = subView.findViewById((R.id.editText_term_end_date));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Term");
        builder.setView(subView);
        builder.create();

        builder.setPositiveButton("SAVE TERM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               final String termTitle =  termTitleEditxt.getText().toString();
               final String startDate =  startDateEditxt.getText().toString();
               final String endDate =  endDateEditxt.getText().toString();

                if(TextUtils.isEmpty(termTitle) || TextUtils.isEmpty(startDate) || TextUtils.isEmpty(endDate)){
                    Toast.makeText(TermActivity.this, "Fields cannot be empty!", Toast.LENGTH_LONG).show();
                }else{
                    Term term = new Term(termTitle, LocalDate.parse(startDate,formatter), LocalDate.parse(endDate,formatter));
                    mDatabase.addTerms(term);

                    finish();
                    startActivity(getIntent());
                }

            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(TermActivity.this, "add cancelled!!!", Toast.LENGTH_LONG).show();
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
}
