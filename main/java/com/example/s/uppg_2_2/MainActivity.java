package com.example.s.uppg_2_2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    /** start value, this must be odd */
    static final long lowestPrime = 3;
    /** this must be even */
    static final int step = 2;
    /**  Database Name */
    static final String DATABASE_NAME = "PrimeDB";
    /**  Table Name */
    static final String TABLE_NAME = "prime";
    /** tag in log file */
    static final String TAG = "STOREPRIMEINDB";

    TextView textView;
    Context context;
    SQLiteDatabase sqlitedatabase;
    long prime;
    String numberAsString;

    /** initialize variables and view at startup */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView2);

        context = MainActivity.this;

        init();

        /** prime must be odd */
        if ((prime % 2) == 0) {
            prime += 1;
        }

        numberAsString = Long.toString(prime);
        textView.setText(numberAsString);

        Log.d(TAG, "Start with " + Long.toString(prime));

    }

    /** quit application */
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    /** open database and create table if not present at start-up */
    private void init() {

        sqlitedatabase = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);

        sqlitedatabase.execSQL(
                "CREATE TABLE IF NOT EXISTS " +
                        TABLE_NAME +
                        "(number INTEGER);" );

        if (getNoOfRowsInDataBase() > 0) {

            prime = getPrime();
        } else {

            prime = lowestPrime;
        }
    }

    /** check if database is empty or hold a prime number */
    private int getNoOfRowsInDataBase() {

        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        Cursor cursor = sqlitedatabase.rawQuery(countQuery, null);

        int cnt = -1;

        if (cursor != null) {

            cnt = cursor.getCount();
            cursor.close();
        }

        return cnt;
    }

    /** check if database is empty or have a prime number */
    private long getPrime() {

        String getQuery = "SELECT * FROM " + TABLE_NAME + " LIMIT 1";

        Cursor cursor = sqlitedatabase.rawQuery(getQuery, null);

        long primeNumber = -1;

        if (cursor != null) {

            cursor.moveToFirst();
            primeNumber = Long.parseLong(cursor.getString(0));
            cursor.close();

            // remove all entries in table
            sqlitedatabase.execSQL("DROP TABLE " + TABLE_NAME);
            // create a new empty table
            sqlitedatabase.execSQL(
                    "CREATE TABLE IF NOT EXISTS " +
                            TABLE_NAME +
                            "(number INTEGER);");
        }

        return primeNumber;
    }


    /** update database when application ends */
    private void updateDateBase() {

        Log.d(TAG, "Store prime: " + Long.toString(prime));
        sqlitedatabase.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(" + prime + ");");

        sqlitedatabase.close();
    }


    /** onclicklistener , called when user taps the quit button */
    public void closeActivity(View view) {

        updateDateBase();

        finish();
    }


    /**  onclicklistener, find next prime , this should be higher than last one */
    /**  Called when the user taps the  get next button */
    public void findNextPrime(View view) {

        boolean found = false;

        while (!found) {

            prime += step;
            found = isPrime(prime);
        }

        numberAsString = Long.toString(prime);
        textView.setText(numberAsString);

    }

    /** check if number is a prime, return true when a prime is found  */
    private boolean isPrime(long candidate) {

        long sqrt = (long)Math.sqrt(candidate);
        String tmp;

        for(long i = lowestPrime; i <= sqrt; i += step) {

            if((candidate % i) == 0) {
                tmp = Long.toString(candidate);
                Log.d(TAG, "this is not prime " + tmp);
                return false;
            }
        }

        tmp = Long.toString(candidate);
        Log.d(TAG, "prime found " + tmp);

        return true;
    }

}
