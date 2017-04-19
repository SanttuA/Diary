package com.example.santtu.diary;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;

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

        SetupDiaryTextField();

    }

    private void SetupDiaryTextField()
    {
        //Today's date
        date = new Date();
        String dateString = DateFormat.getDateFormat(getApplicationContext()).format(date);

        String titleString = getResources().getText(R.string.app_name) + " - " + dateString;
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

    //reload currentEntry's data into title and diary edit text
    private void ReloadEntryData()
    {
        if(currentEntry != null)
        {
            System.out.println("currentEntry is not null");
            //reload data
            diaryEditText.setText(currentEntry.getDiaryEntry());
            //title according to diary entry's date
            String titleString = getResources().getText(R.string.app_name) + " - " + currentEntry.getDate();
            setTitle(titleString);
        }
        else
        {
            System.out.println("currentEntry is null");
            //currentEntry doesn't exist anymore, recreate it with SetupDiaryTextField()
            SetupDiaryTextField();
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

        if(dataSource != null)
            dataSource.Open();
        else
        {
            dataSource = new DiaryDataSource(this);
            dataSource.Open();
        }
        ReloadEntryData();

        super.onResume();
    }

    @Override
    protected void onPause()
    {
        SaveCurrentEntryData();
        dataSource.Close();

        super.onPause();
    }

    //saves currentEntry's diary entry into database
    private void SaveCurrentEntryData()
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
    }

    //saves current diary entry and moves to previous entry if one exists
    public void GoToPreviousDiaryEntry(View view)
    {
        //if previous exists, load previous diary entry from database
        long index = currentEntry.getId();
        System.out.println("currentEntry's ID: " +index);
        if(index > 1)
        {
            SaveCurrentEntryData();
            currentEntry = dataSource.getDiaryEntryById(index-1);
            ReloadEntryData();
        }
        else
        {
            //index is 1, don't allow going backwards
            System.out.println("index is 1, don't allow going backwards");
        }
    }

    //saves current diary entry and moves to next entry if one exists
    public void GoToNextDiaryEntry(View view)
    {
        //if next exists, load next diary entry from database
        //if previous exists, load previous diary entry from database
        long index = currentEntry.getId();
        System.out.println("currentEntry's ID: " +index);
        if(index <  dataSource.getDiaryEntryCount())
        {
            SaveCurrentEntryData();
            currentEntry = dataSource.getDiaryEntryById(index+1);
            ReloadEntryData();
        }
        else
        {
            //index is last in list, don't allow going forward
            System.out.println("index is last in list, don't allow going forward");
        }
    }

    public void OpenDiaryEntryList(View view)
    {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);
        builderSingle.setTitle(getResources().getText(R.string.select_a_page));

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.select_dialog_singlechoice);
        if(dataSource != null)
        {
            List<DiaryEntry> values = dataSource.getAllEntries();
            for(int i = 0; i < values.size(); i++)
            {
                arrayAdapter.add(values.get(i).getId() + ". " + values.get(i).getDate());
            }
        }

        builderSingle.setNegativeButton(getResources().getText(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                SaveCurrentEntryData();
                currentEntry = dataSource.getDiaryEntryById(which+1);
                ReloadEntryData();
            }
        });
        builderSingle.show();
    }


}
