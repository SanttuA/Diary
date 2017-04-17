package com.example.santtu.diary;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Date date;
    private DiaryDataSource dataSource;
    private EditText diaryEditText;
    private DiaryEntry currentEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        diaryEditText = (EditText) findViewById(R.id.diaryEditText);

        //SetupDiaryTextField();

    }

    private void SetupDiaryTextField()
    {
        //Today's date
        date = new Date();
        String dateString = DateFormat.getDateFormat(getApplicationContext()).format(date);
        String titleString = getTitle() + " - " + dateString;
        setTitle(titleString);

        //get the database data for today's date if one exists
        dataSource = new DiaryDataSource(this);
        dataSource.Open();

        List<DiaryEntry> values = dataSource.getAllEntries();
        System.out.println("DiaryEntry size: "+values.size());
        if(values.size() > 0)
        {
            System.out.println("Last diaryEntry date: " + values.get(values.size()-1).getDate() + " current date: "+dateString);
            if(values.get(values.size()-1).getDate().equals(dateString) )
            {
                //date is the same as diary's last entry, use it
                System.out.println("Date is the same as diary's last entry, use it");
                currentEntry = values.get(values.size()-1);
                System.out.println("Diary ID: "+currentEntry.getId()+ "Diary date: "+currentEntry.getDate()+"DiaryEntry: "+currentEntry.getDiaryEntry());
                diaryEditText.setText(currentEntry.getDiaryEntry());
            }
            else
            {
                //date is new, create new entry
                System.out.println("date is new, create new entry");
                currentEntry = dataSource.createDiaryEntry(dateString);
            }
        }
        else
        {
            //no entries exist, create a new entry
            currentEntry = dataSource.createDiaryEntry(dateString);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume()
    {
        //load the date into diary entry text
        //dataSource.Open();
        SetupDiaryTextField();

        super.onResume();
    }

    @Override
    protected void onPause()
    {
        //save the data in diary edit text
        if(currentEntry != null)
        {
            currentEntry.setDiaryEntry(diaryEditText.getText().toString());
            //System.out.println("DiaryEditText: "+diaryEditText.getText().toString());
            dataSource.updateDiaryEntry(currentEntry);
        }
        else
        {
            System.out.println("CurrentEntry is null!");
        }

        dataSource.Close();

        super.onPause();
    }

    public void GoToPreviousDiaryEntry()
    {
        //if previous exists, load previous diary entry from database
    }

    public void GoToNextDiaryEntry()
    {
        //if next exists, load next diary entry from database
    }


}
