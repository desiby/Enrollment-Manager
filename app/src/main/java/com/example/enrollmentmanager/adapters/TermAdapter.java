package com.example.enrollmentmanager.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.enrollmentmanager.R;
import com.example.enrollmentmanager.database.DBhelper;
import com.example.enrollmentmanager.models.Term;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TermAdapter extends RecyclerView.Adapter<TermAdapter.TermHolder> {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private Context context;
    private DBhelper mDatabase;

    private List<Term> terms;

    public TermAdapter(Context context, List<Term> terms) {
        this.context = context;
        this.terms = terms;
        mDatabase = new DBhelper(context);
    }

    //create the view
    @NonNull
    @Override
    public TermHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View termView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.term_item, viewGroup, false);
        return new TermHolder(termView);
    }

    @Override
    public void onBindViewHolder(@NonNull TermHolder termHolder, int position) {
        final Term currentTerm = terms.get(position);
        termHolder.termIdTextView.setText(String.valueOf(currentTerm.getId()));
        termHolder.termTitleTextView.setText(currentTerm.getTitle());
        termHolder.termStartDate.setText(currentTerm.getStart_date().format(formatter));
        termHolder.termEndDate.setText(currentTerm.getEnd_date().format(formatter));

        //edit event handler
        termHolder.editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTermDialog(currentTerm);
            }
        });

        //delete event handler
        termHolder.deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    mDatabase.deleteTerm(currentTerm.getId());
                    //refresh the activity page.
                    ((Activity) context).finish();
                    context.startActivity(((Activity) context).getIntent());
                }catch (Exception ex){
                   Toast.makeText(context, "Cannot delete term associated with a course! ", Toast.LENGTH_LONG).show();
                   Log.d("<<< EXCEPTION:  ", ex.getMessage().toUpperCase()) ;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return terms.size();
    }

    //Edit Term dialog window method
    private void editTermDialog(final Term term) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View subView = inflater.inflate(R.layout.add_edit_term, null);

        final EditText termTitleEditxt = subView.findViewById(R.id.editText_term_title);
        final EditText startDateEditxt = subView.findViewById(R.id.editText_term_start_date);
        final EditText endDateEditxt = subView.findViewById((R.id.editText_term_end_date));

        if (term != null) {
            termTitleEditxt.setText(term.getTitle());
            startDateEditxt.setText(term.getStart_date().format(formatter));
            endDateEditxt.setText(term.getEnd_date().format(formatter));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Term");
        builder.setView(subView);
        builder.create();

        //Edit action
        builder.setPositiveButton("UPDATE TERM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String termTitle = termTitleEditxt.getText().toString();
                final String startDate = startDateEditxt.getText().toString();
                final String endDate = endDateEditxt.getText().toString();

                if (TextUtils.isEmpty(termTitle) || TextUtils.isEmpty(startDate) || TextUtils.isEmpty(endDate)) {
                    Toast.makeText(context, "Fields cannot be empty!", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        mDatabase.updateTerms(new Term(term.getId(),termTitle,
                                LocalDate.parse(startDate, formatter),LocalDate.parse(endDate,formatter)));
                        //refresh the activity
                        ((Activity) context).finish();
                        context.startActivity(((Activity) context).getIntent());
                    } catch (Exception ex) {
                        Log.d("EDIT TERM EXECPTION: ", ex.getMessage());
                    }
                }
            }
        });
        //cancelling the edit action
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Task cancelled", Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
    }

//Defining the class that holds the term
    class TermHolder extends RecyclerView.ViewHolder {
        private TextView termIdTextView;
        private TextView termTitleTextView;
        private TextView termStartDate;
        private TextView termEndDate;
        private ImageView editIcon;
        private ImageView deleteIcon;

        public TermHolder(@NonNull View itemView) {
            super(itemView);
            termIdTextView = itemView.findViewById(R.id.textViewTerm_id);
            termTitleTextView = itemView.findViewById(R.id.textViewTermTitle);
            termStartDate = itemView.findViewById(R.id.textViewTermStartDate);
            termEndDate = itemView.findViewById(R.id.textViewTermEndDate);
            editIcon = itemView.findViewById(R.id.icon_edit);
            deleteIcon = itemView.findViewById(R.id.icon_delete);
        }

    }
}



