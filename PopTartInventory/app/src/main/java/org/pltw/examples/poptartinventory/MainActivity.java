package org.pltw.examples.poptartinventory;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ArrayList<PopTart> inventory = new ArrayList<>();
    ListView simplelist;
    Button updateButton;
    Button emailButton;
    EditText newFlavor;
    EditText currentCount;
    CheckBox seasonal;
    EditText max;
    String saveStuff;
    Button delete;


    File target = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    File logFile = new File(target.getAbsolutePath() + "/", "PopTart.txt");

    private boolean mSwiping = false; // detects if user is swiping on ACTION_UP
   private boolean mItemPressed = false; // Detects if user is currently holding down a view
    private static final int SWIPE_DURATION = 250; // needed for velocity implementation
    private static final int MOVE_DURATION = 150;
    HashMap<Long, Integer> mItemIdTopMap = new HashMap<Long, Integer>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println(logFile);

        //newFlavor = (EditText) findViewById(R.id.newFlavor);
        //currentCount = (EditText) findViewById(R.id.newCurrent);
        // seasonal = (CheckBox) findViewById(R.id.isSeasonal);
        //max = (EditText) findViewById(R.id.newMax);

        // currentCount.setInputType(InputType.TYPE_CLASS_NUMBER);
        // max.setInputType(InputType.TYPE_CLASS_NUMBER);
        // newFlavor.setInputType(InputType.TYPE_CLASS_TEXT);
        load();
        Intent in = this.getIntent();

        String newFlavor = in.getStringExtra("flavor");
        Boolean isSeasonal = in.getBooleanExtra("seasonal", false);
        int current = in.getIntExtra("current", 0);
        int min = in.getIntExtra("min", 10);
        simplelist = (ListView) findViewById(R.id.simpleListView);

        if (getIntent().getExtras() != null) {
            saveStuff = "";
            inventory.add(new PopTart(newFlavor, current, min, isSeasonal));
            for (int i = 0; i < inventory.size(); i++) {
                System.out.println(saveStuff);
                if (inventory.get(i).getName().equals("null")) {
                } else {
                    saveStuff += inventory.get(i).getName() + "/" + String.valueOf(inventory.get(i).getCount()) + "/" + String.valueOf(inventory.get(i).getMinimum()) + "/" + String.valueOf(inventory.get(i).getSeasonal()) + "/";
                }
            }
            writeFile(saveStuff);
            load();
            //System.out.println(logFile);
        }
        /*
        inventory.add(new PopTart("Blueberry", 5, 10, false));
        inventory.add(new PopTart("Strawberry", 8, 15, false));
        inventory.add(new PopTart("Cinnamon", 2, 10, false));
        inventory.add(new PopTart("Pumpkin Pie", 19, 5, true));
        */
        inventory = PopTartSort(inventory);
        MyAdapter myAdapter = new MyAdapter(this, R.layout.list_view_inventory, inventory, mTouchListener);
        simplelist.setAdapter(myAdapter);


        updateButton = (Button) findViewById(R.id.addNew);
        updateButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PopUp2.class);
                startActivity(intent);
            }
        });

        emailButton = (Button) findViewById(R.id.email);
        emailButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //String emailInfo = generateList(inventory);
                //sendEmail(emailInfo);
                writeFile("");
                load();
            }
        });


    }

    public ArrayList<PopTart> PopTartSort(ArrayList<PopTart> tarts) {
        ArrayList<PopTart> yes = new ArrayList<>();
        ArrayList<PopTart> no = new ArrayList<>();
        for (PopTart tart : tarts) {
            if (tart.getSeasonal()) {
                yes.add(0, tart);
            } else {
                no.add(0, tart);
            }
        }
        tarts.clear();
        tarts.addAll(no);
        tarts.addAll(yes);
        return tarts;
    }

    public String generateList(ArrayList<PopTart> tarts) {
        String results = "PopTarts needed: \n";
        for (PopTart tart : tarts) {
            if (tart.getCount() < tart.getMinimum())
                results += tart.getName() + "  needs : " + (tart.getMinimum() - tart.getCount()) + " more. \n";
        }
        return results;

    }

    protected void sendEmail(String emailInfo) {
        Log.i("Send email", "");

        String[] TO = {"mrisk@wclark.k12.in.us"};
        String[] CC = {"mdrisk@yahoo.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");


        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "PopTart Inventory");
        emailIntent.putExtra(Intent.EXTRA_TEXT, emailInfo);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            Log.i("Finished sending email.", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this,
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }


    public void writeFile(String text) {


        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Catch A - writing");
            }
        }
        try {

            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, false));
            buf.write(text);
            buf.close();
            System.out.println(text);
            System.out.println("File written");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(text);
            System.out.println("Failed to write");
        }

    }

    public void load() {
        inventory.clear();
        int i = 0;


        try {
            FileReader fr = new FileReader(logFile);
            BufferedReader in = new BufferedReader(fr);
            String str;
            if ((str = in.readLine()) != null) {
                System.out.println(str);

                while (str.indexOf("/") >= 0) {
                    String name = str.substring(0, str.indexOf("/"));
                    System.out.println(name);
                    str = str.substring(str.indexOf("/") + 1);
                    int count = Integer.valueOf(str.substring(0, str.indexOf("/")));
                    System.out.println(String.valueOf(count));
                    str = str.substring(str.indexOf("/") + 1);
                    int min = Integer.valueOf(str.substring(0, str.indexOf("/")));
                    System.out.println(String.valueOf(min));
                    str = str.substring(str.indexOf("/") + 1);
                    Boolean seasonal = Boolean.valueOf(str.substring(0, str.indexOf("/")));
                    System.out.println(String.valueOf(seasonal));
                    str = str.substring(str.indexOf("/") + 1);
                    if (name.equals("null")) {
                    } else {
                        inventory.add(new PopTart(name, count, min, seasonal));
                        System.out.println(inventory.size());
                    }
                }
            }

        } catch (Exception e) {

        }

    }


    private View.OnTouchListener mTouchListener = new View.OnTouchListener()
    {

        float mDownX;
        private int mSwipeSlop = -1;
        boolean swiped;

        @Override
        public boolean onTouch(final View v, MotionEvent event) {
            if (mSwipeSlop < 0)
            {
                mSwipeSlop = ViewConfiguration.get(MainActivity.this).getScaledTouchSlop();
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mItemPressed)
                    {
                        // Doesn't allow swiping two items at same time
                        return false;
                    }
                    mItemPressed = true;
                    mDownX = event.getX();
                    swiped = false;
                    break;
                case MotionEvent.ACTION_CANCEL:
                    v.setTranslationX(0);
                    mItemPressed = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                {
                    float x = event.getX() + v.getTranslationX();
                    float deltaX = x - mDownX;
                    float deltaXAbs = Math.abs(deltaX);

                    if (!mSwiping)
                    {
                        if (deltaXAbs > mSwipeSlop) // tells if user is actually swiping or just touching in sloppy manner
                        {
                            mSwiping = true;
                            simplelist.requestDisallowInterceptTouchEvent(true);
                        }
                    }
                    if (mSwiping && !swiped) // Need to make sure the user is both swiping and has not already completed a swipe action (hence mSwiping and swiped)
                    {
                        v.setTranslationX((x - mDownX)); // moves the view as long as the user is swiping and has not already swiped

                        if (deltaX > v.getWidth() / 3) // swipe to right
                        {
                            //mDownX = x;
                            swiped = true;
                            mSwiping = false;
                            mItemPressed = false;


                            v.animate().setDuration(500).translationX(v.getWidth()/3); // could pause here if you want, same way as delete

                            return true;
                        }
                        else if (deltaX < -1 * (v.getWidth() / 3)) // swipe to left
                        {

                            v.setEnabled(false); // need to disable the view for the animation to run

                            // stacked the animations to have the pause before the views flings off screen
                            v.animate().setDuration(500).translationX(-v.getWidth()/3).withEndAction(new Runnable() {
                                @Override
                                public void run()
                                {
                                    v.animate().setDuration(500).alpha(0).translationX(-v.getWidth()).withEndAction(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            mSwiping = false;
                                            mItemPressed = false;
                                            animateRemoval(simplelist, v);
                                            int i = simplelist.getPositionForView(v);

                                            saveStuff = "";
                                            for(int j=0;j<inventory.size();j++) {
                                                saveStuff += inventory.get(j).getName() + "/" + String.valueOf(inventory.get(j).getCount()) + "/" + String.valueOf(inventory.get(j).getMinimum()) + "/" + String.valueOf(inventory.get(j).getSeasonal()) + "/";
                                            }
                                            System.out.println(saveStuff);
                                            writeFile(saveStuff);

                                        }
                                    });
                                }
                            });
                            mDownX = x;
                            swiped = true;
                            return true;
                        }
                    }

                }
                break;
                case MotionEvent.ACTION_UP:
                {
                    if (mSwiping) // if the user was swiping, don't go to the and just animate the view back into position
                    {
                        v.animate().setDuration(300).translationX(0).withEndAction(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                mSwiping = false;
                                mItemPressed = false;
                                simplelist.setEnabled(true);

                            }
                        });
                    }
                    else // user was not swiping; registers as a click
                    {
                        mItemPressed = false;
                        simplelist.setEnabled(true);

                        int i = simplelist.getPositionForView(v);

                        Toast.makeText(MainActivity.this, inventory.get(i).toString(), Toast.LENGTH_LONG).show();

                        int k = simplelist.getPositionForView(v);
                        delete = (Button) findViewById(R.id.delete);

                        delete.setVisibility(v.VISIBLE);

                        return false;
                    }
                }
                default:
                    return false;
            }
            return true;
        }
    };

    // animates the removal of the view, also animates the rest of the view into position
    private void animateRemoval(final ListView listView, View viewToRemove)
    {
        int firstVisiblePosition = listView.getFirstVisiblePosition();
        final ArrayAdapter adapter = (ArrayAdapter)simplelist.getAdapter();
        for (int i = 0; i < listView.getChildCount(); ++i)
        {
            View child = listView.getChildAt(i);
            if (child != viewToRemove)
            {
                int position = firstVisiblePosition + i;
                long itemId = listView.getAdapter().getItemId(position);
                mItemIdTopMap.put(itemId, child.getTop());
            }
        }



        adapter.remove(adapter.getItem(listView.getPositionForView(viewToRemove)));



        final ViewTreeObserver observer = listView.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw()
            {
                observer.removeOnPreDrawListener(this);
                boolean firstAnimation = true;
                int firstVisiblePosition = listView.getFirstVisiblePosition();
                for (int i = 0; i < listView.getChildCount(); ++i)
                {
                    final View child = listView.getChildAt(i);
                    int position = firstVisiblePosition + i;
                    long itemId = adapter.getItemId(position);
                    Integer startTop = mItemIdTopMap.get(itemId);
                    int top = child.getTop();
                    if (startTop != null)
                    {
                        if (startTop != top)
                        {
                            int delta = startTop - top;
                            child.setTranslationY(delta);
                            child.animate().setDuration(MOVE_DURATION).translationY(0);
                            if (firstAnimation) {
                                child.animate().withEndAction(new Runnable()
                                {
                                    public void run()
                                    {
                                        mSwiping = false;
                                        simplelist.setEnabled(true);
                                    }
                                });
                                firstAnimation = false;
                            }
                        }
                    }
                    else {
                        // Animate new views along with the others. The catch is that they did not
                        // exist in the start state, so we must calculate their starting position
                        // based on neighboring views.
                        int childHeight = child.getHeight() + listView.getDividerHeight();
                        startTop = top + (i > 0 ? childHeight : -childHeight);
                        int delta = startTop - top;
                        child.setTranslationY(delta);
                        child.animate().setDuration(MOVE_DURATION).translationY(0);
                        if (firstAnimation) {
                            child.animate().withEndAction(new Runnable()
                            {
                                public void run()
                                {
                                    mSwiping = false;
                                    simplelist.setEnabled(true);
                                }
                            });
                            firstAnimation = false;
                        }
                    }
                }
                mItemIdTopMap.clear();
                return true;
            }
        });
    }
}



