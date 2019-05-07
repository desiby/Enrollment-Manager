package com.example.enrollmentmanager.database;

import android.provider.BaseColumns;

public final class DBContract {

    private DBContract(){}
    /* Inner class that defines the table contents */
    static class TbTerm implements BaseColumns {
        static final String TABLE_NAME = "term";
        static final String COLUMN_NAME_TITLE = "title";
        static final String COLUMN_NAME_START_DATE = "start_date";
        static final String COLUMN_NAME_END_DATE = "end_date";
    }

    /* Inner class that defines the table contents */
    static class TbCourse implements BaseColumns {
        static final String TABLE_NAME = "course";
        static final String COLUMN_NAME_TERM_ID = "term_id";
        static final String COLUMN_NAME_TITLE = "title";
        static final String COLUMN_NAME_START_DATE = "start_date";
        static final String COLUMN_NAME_END_DATE = "end_date";
        static final String COLUMN_NAME_STATUS = "status";
        static final String COLUMN_NAME_MENTOR_NAME = "mentor_name";
        static final String COLUMN_NAME_MENTOR_PHONE = "mentor_phone";
        static final String COLUMN_NAME_MENTOR_EMAIL = "mentor_email";
        static final String COLUMN_NAME_NOTES = "notes";
    }

    /* Inner class that defines the table contents */
    static class  TbAssessment implements BaseColumns {
        static final String TABLE_NAME = "assessment";
        static final String COLUMN_NAME_COURSE_ID = "course_id";
        static final String COLUMN_NAME_TITLE = "name";
        static final String COLUMN_NAME_DUE_DATE = "due_date";

    }
}
