package com.appdvl.silenttimer.Fragments;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appdvl.silenttimer.Activities.MainActivity;
import com.appdvl.silenttimer.Activities.TimeActivity;
import com.appdvl.silenttimer.Adapter.SilentTimeAdapter;
import com.appdvl.silenttimer.Models.Silencer;
import com.appdvl.silenttimer.R;
import com.appdvl.silenttimer.Utils.DividerItemDecoration;
import com.appdvl.silenttimer.Utils.RecyclerItemClickListener;
import com.appdvl.silenttimer.Utils.SilentContentProvider;
import com.appdvl.silenttimer.Utils.SilentRecyclerView;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    private boolean mTwoPane;
    private static final int INTENT_ADD = 1;
    private static final int INTENT_UPDATE = 2;
    private static final String TIME_DETAILS_FRAGMENT = "TIME_DETAILS_FRAGMENT";

    TextView emptyWelcome;
    TextView emptyLabel;
    LinearLayout emptyLayout;
    CoordinatorLayout rootLayout;
    SilentRecyclerView recyclerView;
    FloatingActionButton fab;

    SilentContentProvider.SilentDatabaseHelper databaseHelper;
    SilentTimeAdapter adapter;
    TimeDetailsFragment alarm;
    ContentResolver cr;
    Cursor cursor;
    CursorLoader loader;

    Silencer refreshSilencer = null;
    int refreshPosition = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_list_time, container, false);

        rootLayout = (CoordinatorLayout) root.findViewById(R.id.time_recyclerview_container);
        recyclerView = (SilentRecyclerView) root.findViewById(R.id.time_recycler_view);

        Typeface robotoMediumFont = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Medium.ttf");
        Typeface robotoRegularFont = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Regular.ttf");

        emptyLayout = (LinearLayout)root.findViewById(R.id.time_emptylist_view);
        emptyWelcome = (TextView)root.findViewById(R.id.time_lbl_welcome);
        emptyWelcome.setTypeface(robotoMediumFont);
        emptyLabel = (TextView)root.findViewById(R.id.time_lbl_empty);
        emptyLabel.setTypeface(robotoRegularFont);

        fab = (FloatingActionButton) root.findViewById(R.id.time_fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                addNewItem();
            }
        });

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setVerticalScrollBarEnabled(false);
        recyclerView.setHorizontalScrollBarEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setEmptyView(emptyLayout);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView,
                new RecyclerItemClickListener.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(View view, int position)
                    {
                        updateItem(adapter.getItemId(position), position);
                    }

                    @Override
                    public void onItemLongClick(View view, int position)
                    {
                        //do nothing for now
                        //updateItem(adapter.getItemId(position));

                    }
                }));

        cr = getActivity().getContentResolver();
        cursor = cr.query(SilentContentProvider.SILENCER_CONTENT_URI, null, null, null, null);

        databaseHelper = new SilentContentProvider.SilentDatabaseHelper(
                getActivity(),
                SilentContentProvider.SilentDatabaseHelper.DATABASE_NAME,
                null,
                SilentContentProvider.SilentDatabaseHelper.DATABASE_VERSION);

        adapter = new SilentTimeAdapter(getActivity(), databaseHelper.getSilencers(), cursor);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT)
        {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1)
            {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir)
            {
                //Remove swiped item from list and notify the RecyclerView
                final int position = viewHolder.getAdapterPosition();
                adapter.removeItem(adapter.getItemId(position), position);

                //Remove alarms that have been set
                removeAlarms(adapter.getItemId(position));

                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                //Show snack bar with undo option for deleted item
                Snackbar snackbar = Snackbar.make(rootLayout, "Silencer deleted.", Snackbar.LENGTH_LONG);

                //Change the text color of the the snackbar
                ViewGroup group = (ViewGroup) snackbar.getView();
                for (int i = 0; i < group.getChildCount(); i++) {
                    View v = group.getChildAt(i);
                    if (v instanceof TextView) {
                        TextView t = (TextView) v;
                        t.setTextColor(getResources().getColor(R.color.color_white));
                    }
                }
                snackbar.setAction("Undo", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        adapter.undoItem(position);
                    }
                })
                        .setActionTextColor(getResources().getColor(R.color.color_primary_accent))
                        .show();
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive)
            {
                SilentTimeAdapter.ViewHolder holder = (SilentTimeAdapter.ViewHolder) viewHolder;
                FrameLayout frameLayout = holder.container;

                Bitmap drawingPic = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_white_36dp);

                //Variables that will be used to set the position of drawingPic
                int drawingPic_x;
                int drawingPic_y;

                if(dX > 0)
                {
                    drawingPic_x = frameLayout.getLeft() + 50;
                    drawingPic_y = frameLayout.getTop() + (frameLayout.getHeight() / 3);
                }
                else
                {
                    drawingPic_x = frameLayout.getRight() - (drawingPic.getWidth() + 50);
                    drawingPic_y = frameLayout.getTop() + (frameLayout.getHeight() / 3);
                }

                Paint background = new Paint();
                //red color when swiped
                background.setARGB(255, 255, 3, 62);

                c.drawRect(frameLayout.getLeft(), frameLayout.getTop(), frameLayout.getRight(), frameLayout.getBottom(), background);
                c.drawBitmap(drawingPic, drawingPic_x, drawingPic_y, null);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);

        itemTouchHelper.attachToRecyclerView(recyclerView);

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(0, null, this);

        View detailsFrame = getActivity().findViewById(R.id.detail_container);
        mTwoPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        Thread t = new Thread(new Runnable()
        {
            public void run()
            {
                restartLoader();
            }
        });
        t.start();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    private void addNewItem()
    {
        if (mTwoPane)
        {
            // In two-pane mode, show the detail view in this activity by adding or replacing
            // the detail fragment using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putInt("intentID", INTENT_ADD);
            arguments.putBoolean("isTwoPane", mTwoPane);

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.animator.fade_in, R.animator.fade_out);

            TimeDetailsFragment fragment = new TimeDetailsFragment();
            fragment.setArguments(arguments);

            fragmentTransaction.replace(R.id.detail_container, fragment, TIME_DETAILS_FRAGMENT)
                    .addToBackStack(TIME_DETAILS_FRAGMENT)
                    .commit();
        }
        else
        {
            Intent intent = new Intent(getActivity(), TimeActivity.class);
            intent.putExtra("intentID", INTENT_ADD);
            getActivity().startActivityForResult(intent, MainActivity.REQUEST_CODE_TIME);
        }
    }

    private void updateItem(long id, int position)
    {
        String name = "";
        String startTime = "";
        String endTime = "";
        int icon = 0;
        int sunday = 0;
        int monday = 0;
        int tuesday = 0;
        int wednesday = 0;
        int thursday = 0;
        int friday = 0;
        int saturday = 0;
        int isChecked = 0;

        cr = getActivity().getContentResolver();

        cursor = cr.query(ContentUris.withAppendedId(SilentContentProvider.SILENCER_CONTENT_URI, id), null, null, null, null);

        if (cursor.moveToFirst())
        {
            name = cursor.getString(cursor.getColumnIndex(SilentContentProvider.SILENCER_NAME));
            startTime = cursor.getString(cursor.getColumnIndex(SilentContentProvider.SILENCER_START_TIME));
            endTime = cursor.getString(cursor.getColumnIndex(SilentContentProvider.SILENCER_END_TIME));
            sunday = cursor.getInt(cursor.getColumnIndex(SilentContentProvider.SILENCER_SUNDAY));
            monday = cursor.getInt(cursor.getColumnIndex(SilentContentProvider.SILENCER_MONDAY));
            tuesday = cursor.getInt(cursor.getColumnIndex(SilentContentProvider.SILENCER_TUESDAY));
            wednesday = cursor.getInt(cursor.getColumnIndex(SilentContentProvider.SILENCER_WEDNESDAY));
            thursday = cursor.getInt(cursor.getColumnIndex(SilentContentProvider.SILENCER_THURSDAY));
            friday = cursor.getInt(cursor.getColumnIndex(SilentContentProvider.SILENCER_FRIDAY));
            saturday = cursor.getInt(cursor.getColumnIndex(SilentContentProvider.SILENCER_SATURDAY));
            isChecked = cursor.getInt(cursor.getColumnIndex(SilentContentProvider.SILENCER_IS_CHECKED));
        }

        if(mTwoPane)
        {
            Bundle arguments = new Bundle();
            arguments.putInt("intentID", INTENT_UPDATE);
            arguments.putInt("update_id", (int)id);
            arguments.putString("update_name", name);
            arguments.putString("update_startTime", startTime);
            arguments.putString("update_endTime", endTime);
            arguments.putInt("update_icon", icon);
            arguments.putInt("update_sunday", sunday);
            arguments.putInt("update_monday", monday);
            arguments.putInt("update_tuesday", tuesday);
            arguments.putInt("update_wednesday", wednesday);
            arguments.putInt("update_thursday", thursday);
            arguments.putInt("update_friday", friday);
            arguments.putInt("update_saturday", saturday);
            arguments.putInt("update_isChecked", isChecked);
            arguments.putBoolean("isTwoPane", mTwoPane);
            arguments.putInt("update_position", position);

            TimeDetailsFragment fragment = new TimeDetailsFragment();
            fragment.setArguments(arguments);
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.detail_container, fragment, TIME_DETAILS_FRAGMENT)
                    .addToBackStack(TIME_DETAILS_FRAGMENT)
                    .commit();
        }
        else
        {
            Intent intent = new Intent(getActivity(), TimeActivity.class);
            intent.putExtra("intentID", INTENT_UPDATE);
            intent.putExtra("update_id", (int)id);
            intent.putExtra("update_name", name);
            intent.putExtra("update_startTime", startTime);
            intent.putExtra("update_endTime", endTime);
            intent.putExtra("update_icon", icon);
            intent.putExtra("update_sunday", sunday);
            intent.putExtra("update_monday", monday);
            intent.putExtra("update_tuesday", tuesday);
            intent.putExtra("update_wednesday", wednesday);
            intent.putExtra("update_thursday", thursday);
            intent.putExtra("update_friday", friday);
            intent.putExtra("update_saturday", saturday);
            intent.putExtra("update_isChecked", isChecked);
            intent.putExtra("isTwoPane", mTwoPane);
            intent.putExtra("update_position", position);

            getActivity().startActivityForResult(intent, MainActivity.REQUEST_CODE_TIME);
        }
    }

    Handler handler = new Handler();

    public void restartLoader()
    {
        handler.post
                (
                        new Runnable()
                        {
                            public void run()
                            {
                                if(isAdded())
                                {
                                    getLoaderManager().restartLoader(0, null, TimeFragment.this);
                                }
                            }
                        }
                );
    }

    private void removeAlarms(long id)
    {
        String name = "";
        String startTime = "";
        String endTime = "";
        boolean sunday = false;
        boolean monday = false;
        boolean tuesday = false;
        boolean wednesday = false;
        boolean thursday = false;
        boolean friday = false;
        boolean saturday = false;
        boolean isChecked = false;

        cr = getActivity().getContentResolver();

        cursor = cr.query(ContentUris.withAppendedId(SilentContentProvider.SILENCER_CONTENT_URI, id), null, null, null, null);

        if (cursor.moveToFirst())
        {
            name = cursor.getString(cursor.getColumnIndex(SilentContentProvider.SILENCER_NAME));
            startTime = cursor.getString(cursor.getColumnIndex(SilentContentProvider.SILENCER_START_TIME));
            endTime = cursor.getString(cursor.getColumnIndex(SilentContentProvider.SILENCER_END_TIME));
            sunday = toBool(cursor.getInt(cursor.getColumnIndex(SilentContentProvider.SILENCER_SUNDAY)));
            monday = toBool(cursor.getInt(cursor.getColumnIndex(SilentContentProvider.SILENCER_MONDAY)));
            tuesday = toBool(cursor.getInt(cursor.getColumnIndex(SilentContentProvider.SILENCER_TUESDAY)));
            wednesday = toBool(cursor.getInt(cursor.getColumnIndex(SilentContentProvider.SILENCER_WEDNESDAY)));
            thursday = toBool(cursor.getInt(cursor.getColumnIndex(SilentContentProvider.SILENCER_THURSDAY)));
            friday = toBool(cursor.getInt(cursor.getColumnIndex(SilentContentProvider.SILENCER_FRIDAY)));
            saturday = toBool(cursor.getInt(cursor.getColumnIndex(SilentContentProvider.SILENCER_SATURDAY)));
            isChecked = toBool(cursor.getInt(cursor.getColumnIndex(SilentContentProvider.SILENCER_IS_CHECKED)));
        }

        Silencer silencer = new Silencer( name, startTime, endTime, sunday, monday, tuesday, wednesday,
                thursday, friday, saturday, isChecked);

        alarm = new TimeDetailsFragment();
        alarm.cancelAlarmForDays(getActivity().getApplicationContext(),
                silencer.getSunday(),
                silencer.getMonday(),
                silencer.getTuesday(),
                silencer.getWednesday(),
                silencer.getThursday(),
                silencer.getFriday(),
                silencer.getSaturday(), (int)id);
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        String[] projection = new String[]
                {
                        SilentContentProvider.SILENCER_ID,
                        SilentContentProvider.SILENCER_NAME,
                        SilentContentProvider.SILENCER_START_TIME,
                        SilentContentProvider.SILENCER_END_TIME,
                        SilentContentProvider.SILENCER_SUNDAY,
                        SilentContentProvider.SILENCER_MONDAY,
                        SilentContentProvider.SILENCER_TUESDAY,
                        SilentContentProvider.SILENCER_WEDNESDAY,
                        SilentContentProvider.SILENCER_THURSDAY,
                        SilentContentProvider.SILENCER_FRIDAY,
                        SilentContentProvider.SILENCER_SATURDAY,
                        SilentContentProvider.SILENCER_IS_CHECKED
                };

        loader = new CursorLoader(getActivity(), SilentContentProvider.SILENCER_CONTENT_URI, projection, null, null, null);

        return loader;
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor)
    {
        if(recyclerView.getAdapter() != null)
        {
            adapter.swapCursor(cursor);
            int position = adapter.getItemCount();

            if (refreshSilencer != null)
            {
                if(refreshPosition == -1)
                {
                    adapter.insertItem(refreshSilencer);
                    recyclerView.scrollToPosition(position);
                }
                else
                {
                    adapter.updateItem(refreshSilencer, refreshPosition);
                }

                refreshSilencer = null;
                refreshPosition = -1;
            }
        }
    }

    public void onLoaderReset(Loader<Cursor> loader)
    {
        if(recyclerView.getAdapter() != null)
        {
            adapter.swapCursor(null);
        }
    }

    public void refreshSilencer(Silencer silencer, int position)
    {
        this.refreshSilencer = silencer;
        this.refreshPosition = position;
    }

    public boolean toBool(int i)
    {
        return !(i % 2 == 0);
    }
}