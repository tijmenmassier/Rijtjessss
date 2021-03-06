package layout;


import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;

import com.absontwikkeling.rijtjes.DBAdapter;
import com.absontwikkeling.rijtjes.R;
import com.absontwikkeling.rijtjes.editWordListACTIVITY;
import com.absontwikkeling.rijtjes.question;
import com.absontwikkeling.rijtjes.settings;

/**
 * A simple {@link Fragment} subclass.
 */
public class displayList extends Fragment {

    DBAdapter dbAdapter;
    DBAdapter dbAdapterMain;
    View view;
    LinearLayout linearLayoutList;
    public static int radioState;
    // TextView showList;

    public displayList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_display_list, container, false);

        // setScrollView();

        // Debugging for displayQuery()
        // showList = (TextView) findViewById(R.id.showList);
        // showList.setText(displayQuery(DLdbAdapter.getAllRowsMain()));

        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int i) {
                switch(i) {
                    case R.id.questionTheList:
                        radioState = 0;
                        break;
                    case R.id.editList:
                        radioState = 1;
                        break;
                    case R.id.deleteList:
                        radioState = 2;
                        break;
                }
            }
        });

        // Opens database
        openDB();

        // Creates list of buttons
        linearLayoutList = view.findViewById(R.id.displayListLinearLayout);
        createButtonListInLayout(dbAdapterMain.getAllRowsMain());

        // Defines function of Floating Action Button that add a new list
        // TODO Zorg ervoor dat deze knop ook het menu switched naar het andere framgent.
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createList createList = new createList();
                FragmentManager manager = getFragmentManager();
                manager.beginTransaction().replace(R.id.relativelayout_fragment, createList).commit();
            }
        });

        return view;
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public void setScrollView() {
        // Gets scrollView
        ScrollView scrollView = view.findViewById(R.id.scrollView2);
        // Gets the layout params that will allow you to resize the layout
        ViewGroup.LayoutParams params = scrollView.getLayoutParams();
        // Changes the height and width to the specified *pixels*
        params.height = (int) (getScreenHeight()*0.75);
        scrollView.setLayoutParams(params);

    }

    public void onDestroyView() {
        super.onDestroyView();
        closeDB();
    }

    private void openDB() {
        dbAdapter = new DBAdapter(getContext(), DBAdapter.DATABASE_NAME, DBAdapter.DATABASE_VERSION);
        dbAdapter.open();
        dbAdapterMain = new DBAdapter(getContext(), DBAdapter.DATABASE_MAIN_NAME, DBAdapter.DATABASE_MAIN_VERSION);
        dbAdapterMain.open();
    }

    private void closeDB() {
        dbAdapter.close();
        dbAdapterMain.close();
    }

    private void createButtonListInLayout(Cursor c) {
        if (c.moveToFirst()) {
            do {
                // Define button
                Button button = new Button(getContext());
                // Layout
                final String tableName = c.getString(DBAdapter.COL_TABLE_NAME_MAIN);
                button.setText(tableName);
                button.setTextColor(Color.parseColor("#454545"));
                button.setBackgroundResource(R.drawable.button);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                params.setMargins(0, 0, 0, 10);
                button.setLayoutParams(params);
                button.setGravity(8);
                button.setPadding(30,40,0,0);
                button.setBackgroundResource(0); // Maakt background doorzichtig geloof ik, maar niet zeker, want zie niet of de buttons nog werken -Arun
                // Function
                View.OnClickListener buttonListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (radioState == 0) {
                            Intent i = new Intent(getActivity(), question.class);
                            i.putExtra("tableName", tableName);
                            startActivity(i);

                        } else if (radioState == 1) {
                            Intent i = new Intent(getActivity(), editWordListACTIVITY.class);
                            i.putExtra("tableName", tableName);
                            startActivity(i);

                        } else if (radioState == 2) {
                            dbAdapter.deleteTable(tableName);
                            dbAdapter.deleteRowMain(tableName);
                            displayList fragment = (displayList) getFragmentManager().findFragmentById(R.id.relativelayout_fragment);
                            getFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
                        }

                    }
                };

                button.setOnClickListener(buttonListener);

                // Add button to activity
                linearLayoutList.addView(button);

            } while(c.moveToNext());
        }

    }


    /*
    ################### Function to check if main table works ###########################

    private String displayQuery(Cursor cursor) {
        String message = "";
        if (cursor.moveToFirst()) {
            do {
                // Process the data:
                String listName = cursor.getString(DBAdapter.COL_TABLE_NAME_MAIN);

                // Append data to the message:
                message += listName +"\n";
            } while(cursor.moveToNext());
        }

        cursor.close();
        return message;
    }
    */
}
