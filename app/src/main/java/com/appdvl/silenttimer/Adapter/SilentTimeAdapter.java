package com.appdvl.silenttimer.Adapter;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.appdvl.silenttimer.Fragments.TimeDetailsFragment;
import com.appdvl.silenttimer.Models.Silencer;
import com.appdvl.silenttimer.R;
import com.appdvl.silenttimer.Utils.SilentContentProvider;

import java.util.List;


public class SilentTimeAdapter extends RecyclerView.Adapter<SilentTimeAdapter.ViewHolder>
{
    private  Cursor cursor;
    private  List<Silencer> silencers;
    private Activity acivity;
    private TimeDetailsFragment alarm = new TimeDetailsFragment();

    private Context mContext;
    private Cursor mCursor;

    private boolean mDataValid;
    private int mRowIdColumn;
    private DataSetObserver mDataSetObserver;

    private int undoId;
    private Silencer undoSilencer;
    private List<Silencer> mSilencers;
    private Typeface robotoMediumFont;
    private Typeface robotoLightFont;
    private Typeface geoSansLightFont;
    private int randomColor;
    private float alphaOpaque = 1f;
    private float alphaTransparent = 0.3f;

    public SilentTimeAdapter(Context context, List<Silencer> silencers, Cursor cursor)
    {
        mContext = context;
        mCursor = cursor;

        if(mSilencers != null)
        {
            mSilencers.clear();
        }

        mSilencers = silencers;

        mCursor = cursor;
        mDataValid = cursor != null;
        mRowIdColumn = mDataValid ? mCursor.getColumnIndex("_id") : -1;
        mDataSetObserver = new NotifyingDataSetObserver();
        if (mCursor != null)
        {
            mCursor.registerDataSetObserver(mDataSetObserver);
        }

        robotoMediumFont = Typeface.createFromAsset(context.getAssets(), "Roboto-Medium.ttf");
        robotoLightFont = Typeface.createFromAsset(context.getAssets(), "Roboto-Light.ttf");
        geoSansLightFont = Typeface.createFromAsset(context.getAssets(), "GeosansLight.ttf");


        randomColor = context.getResources().getColor(R.color.color_primary);
    }

    public SilentTimeAdapter(Activity activity, List<Silencer> silencers, Cursor cursor) {
        this.acivity=activity;
        this.silencers=silencers;
        this.cursor=cursor;

    }




    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public FrameLayout container;

        ImageView tileView;
        TextView silencerName;
        TextView startTimeTextView;
        TextView endTimeTextView;
        TextView toLabelTextView;
        TextView textViewSunday;
        TextView textViewMonday;
        TextView textViewTuesday;
        TextView textViewWednesday;
        TextView textViewThursday;
        TextView textViewFriday;
        TextView textViewSaturday;
        SwitchCompat switch1;

        public ViewHolder(View view)
        {
            super(view);

            container = (FrameLayout) view.findViewById(R.id.time_root_container);

            tileView = (ImageView) view.findViewById(R.id.time_tile_view);
            silencerName = (TextView) view.findViewById(R.id.silencer_name);
            startTimeTextView = (TextView) view.findViewById(R.id.startTimeTextView);
            toLabelTextView = (TextView) view.findViewById(R.id.toTextView);
            endTimeTextView = (TextView) view.findViewById(R.id.endTimeTextView);
            textViewSunday = (TextView) view.findViewById(R.id.textViewSunday);
            textViewMonday = (TextView) view.findViewById(R.id.textViewMonday);
            textViewTuesday = (TextView) view.findViewById(R.id.textViewTuesday);
            textViewWednesday = (TextView) view.findViewById(R.id.textViewWednesday);
            textViewThursday = (TextView) view.findViewById(R.id.textViewThursday);
            textViewFriday = (TextView) view.findViewById(R.id.textViewFriday);
            textViewSaturday = (TextView) view.findViewById(R.id.textViewSaturday);
            switch1 = (SwitchCompat) view.findViewById(R.id.list_switch);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_list_time_row, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position)
    {
        Silencer silencer = mSilencers.get(position);

        /* TODO: add some beauty with icons
        String[] cases = {"jobIcon", "schoolIcon", "homeIcon"};
        int i;
        for(i = 0; i < cases.length; i++)
            if(silencer.getSilencerName().contains(cases[i])) break;
        switch (i) {
            case 0:
                viewHolder.tileView.setImageDrawable(R.drawable.jobIcon);
                break;
            case 1:
                viewHolder.tileView.setImageDrawable(R.drawable.schoolIcon);
                break;
            case 2:
                viewHolder.tileView.setImageDrawable(R.drawable.homeIcon);

        }
        */

        String firstLetter = silencer.getSilencerName().substring(0, 1);
        TextDrawable drawable = TextDrawable.builder().buildRound(firstLetter, randomColor);
        viewHolder.tileView.setImageDrawable(drawable);

        viewHolder.silencerName.setText(silencer.getSilencerName());
        viewHolder.silencerName.setTypeface(robotoMediumFont);

        viewHolder.startTimeTextView.setText(silencer.getStartTime());
        viewHolder.startTimeTextView.setTypeface(geoSansLightFont);

        viewHolder.toLabelTextView.setTypeface(geoSansLightFont);

        viewHolder.endTimeTextView.setText(silencer.getEndTime());
        viewHolder.endTimeTextView.setTypeface(geoSansLightFont);

        viewHolder.textViewSunday.setAlpha(setAlpha(silencer.getSunday()));
        viewHolder.textViewSunday.setTypeface(robotoLightFont);

        viewHolder.textViewMonday.setAlpha(setAlpha(silencer.getMonday()));
        viewHolder.textViewMonday.setTypeface(robotoLightFont);

        viewHolder.textViewTuesday.setAlpha(setAlpha(silencer.getTuesday()));
        viewHolder.textViewTuesday.setTypeface(robotoLightFont);

        viewHolder.textViewWednesday.setAlpha(setAlpha(silencer.getWednesday()));
        viewHolder.textViewWednesday.setTypeface(robotoLightFont);

        viewHolder.textViewThursday.setAlpha(setAlpha(silencer.getThursday()));
        viewHolder.textViewThursday.setTypeface(robotoLightFont);

        viewHolder.textViewFriday.setAlpha(setAlpha(silencer.getFriday()));
        viewHolder.textViewFriday.setTypeface(robotoLightFont);

        viewHolder.textViewSaturday.setAlpha(setAlpha(silencer.getSaturday()));
        viewHolder.textViewSaturday.setTypeface(robotoLightFont);

        if(silencer.getIsChecked())
        {
            viewHolder.switch1.setChecked(true);
        }
        else
        {
            viewHolder.switch1.setChecked(false);

        }
    }

    public float setAlpha(boolean isSet)
    {
        if(isSet)
        {
            return alphaOpaque;
        }
        else
        {
            return alphaTransparent;
        }
    }

    @Override
    public void setHasStableIds(boolean hasStableIds)
    {
        super.setHasStableIds(true);
    }

    @Override
    public long getItemId(int position)
    {
        super.getItemId(position);
        if(mCursor != null)
        {
            if (mDataValid && mCursor.moveToPosition(position))
            {
                return mCursor.getLong(mRowIdColumn);
            }
        }
        return -1;
    }

    @Override
    public int getItemCount()
    {
        return mSilencers.size();
    }

    public void insertItem(Silencer silencer)
    {
        int position = getItemCount();
        this.mSilencers.add(silencer);
        notifyItemInserted(position);
    }

    public void updateItem(Silencer silencer, int position)
    {
        this.mSilencers.set(position, silencer);
        notifyItemChanged(position);
    }

    public void removeItem(long id, int position)
    {
        undoSilencer = mSilencers.get(position);
        undoId = (int)id;
        this.mSilencers.remove(position);
        notifyItemRemoved(position);
        String where = SilentContentProvider.SILENCER_ID + "=" + id;
        mContext.getContentResolver().delete(SilentContentProvider.SILENCER_CONTENT_URI, where, null);
        //notifyItemRemoved(position); //TODO: try notifying later
    }

    public void undoItem(int position)
    {
        if (undoId >= 0 && undoSilencer != null)
        {
            ContentResolver cr = mContext.getContentResolver();

            Cursor query = cr.query(SilentContentProvider.SILENCER_CONTENT_URI, null, null, null, null);

            ContentValues values = new ContentValues();

            values.put(SilentContentProvider.SILENCER_ID, undoId);
            values.put(SilentContentProvider.SILENCER_NAME, undoSilencer.getSilencerName());
            values.put(SilentContentProvider.SILENCER_START_TIME, undoSilencer.getStartTime());
            values.put(SilentContentProvider.SILENCER_END_TIME, undoSilencer.getEndTime());
            values.put(SilentContentProvider.SILENCER_SUNDAY, undoSilencer.getSunday());
            values.put(SilentContentProvider.SILENCER_MONDAY, undoSilencer.getMonday());
            values.put(SilentContentProvider.SILENCER_TUESDAY, undoSilencer.getTuesday());
            values.put(SilentContentProvider.SILENCER_WEDNESDAY, undoSilencer.getWednesday());
            values.put(SilentContentProvider.SILENCER_THURSDAY, undoSilencer.getThursday());
            values.put(SilentContentProvider.SILENCER_FRIDAY, undoSilencer.getFriday());
            values.put(SilentContentProvider.SILENCER_SATURDAY, undoSilencer.getSaturday());
            values.put(SilentContentProvider.SILENCER_IS_CHECKED, undoSilencer.getIsChecked());

            int rowID = (int) ContentUris.parseId(cr.insert(SilentContentProvider.SILENCER_CONTENT_URI, values));

            int startHour = hour(undoSilencer.getStartTime());
            int startMinute =  minutes(undoSilencer.getStartTime());
            int endHour = hour(undoSilencer.getEndTime());
            int endMinute = minutes(undoSilencer.getEndTime());

            if (undoSilencer.getIsChecked())
            {
                alarm.setAlarmForDays(mContext.getApplicationContext(),
                        undoSilencer.getSunday(),
                        undoSilencer.getMonday(),
                        undoSilencer.getTuesday(),
                        undoSilencer.getWednesday(),
                        undoSilencer.getThursday(),
                        undoSilencer.getFriday(),
                        undoSilencer.getSaturday(),
                        rowID,
                        startHour,
                        startMinute,
                        endHour,
                        endMinute,
                        undoSilencer.getEndTime());
            }

            query.close();
        }

        mSilencers.add(position, undoSilencer);
        notifyItemInserted(position);
    }

    public int hour(String time)
    {
        String c = time.substring(0, 2);
        return Integer.parseInt(c);
    }

    public int minutes(String time)
    {
        String c = time.substring(3);
        return Integer.parseInt(c);
    }

    /**
     * Change the underlying cursor to a new cursor. If there is an existing cursor it will be
     * closed.
     */
    @SuppressWarnings("unused")
    public void changeCursor(Cursor cursor)
    {
        Cursor old = swapCursor(cursor);
        if (old != null)
        {
            old.close();
        }
    }

    /**
     * Swap in a new Cursor, returning the old Cursor.  Unlike
     * {@link #changeCursor(Cursor)}, the returned old Cursor is <em>not</em>
     * closed.
     */
    public Cursor swapCursor(Cursor newCursor)
    {
        if (newCursor == mCursor)
        {
            return null;
        }

        final Cursor oldCursor = mCursor;

        if (oldCursor != null && mDataSetObserver != null)
        {
            oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }

        mCursor = newCursor;

        if (mCursor != null)
        {
            if (mDataSetObserver != null)
            {
                mCursor.registerDataSetObserver(mDataSetObserver);
            }
            mRowIdColumn = newCursor.getColumnIndexOrThrow("_id");
            mDataValid = true;
            //notifyDataSetChanged(); disrupts animations on items
        }
        else
        {
            mRowIdColumn = -1;
            mDataValid = false;
            //notifyDataSetChanged(); disrupts animations on items
        }
        return oldCursor;
    }

    private class NotifyingDataSetObserver extends DataSetObserver
    {
        @Override
        public void onChanged()
        {
            super.onChanged();
            mDataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated()
        {
            super.onInvalidated();
            mDataValid = false;
            notifyDataSetChanged();
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
    }
}