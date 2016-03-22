package be.krivi.omaapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Krivi on 14/01/16.
 */
public class CalAdapter extends RecyclerView.Adapter<CalAdapter.ViewHolder>{

    private LayoutInflater inflater;
    private List<CalItem> data;

    private Animation fade;

    public CalAdapter( Context context, List<CalItem> data ){
        this.inflater = LayoutInflater.from( context );
        this.data = data;

        fade = AnimationUtils.loadAnimation( context, R.anim.fade_in );
    }

    public void updateData( List<CalItem> data ){
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public CalAdapter.ViewHolder onCreateViewHolder( ViewGroup viewGroup, int viewType ){
        return new ViewHolder( inflater.inflate( R.layout.item_cal, viewGroup, false ) );
    }

    @Override
    public void onBindViewHolder( CalAdapter.ViewHolder viewHolder, int i ){
        CalItem item = data.get( i );

        String dayNumber = item.getDayNumber();
        String dayName = item.getDayName();
        String monthName = item.getMonthName();

        if( i > 0 ){
            CalItem prev = data.get( i - 1 );
            if( dayNumber.equals( prev.getDayNumber() ) && monthName.equals( prev.getMonthName() ) )
                dayName = dayNumber = monthName = " ";
        }

        viewHolder.dDayName.setText( dayName );
        viewHolder.dDayNumber.setText( dayNumber );
        viewHolder.dMonth.setText( monthName );

        viewHolder.cName.setText( item.getName() );
        viewHolder.cLoc.setText( item.getLoc() );


        if( item.getStartTime().equals( item.getEndTime() ) )
        viewHolder.cTime.setText( R.string.all_day_event );
        else
        viewHolder.cTime.setText( item.getStartTime() + " — " + item.getEndTime() );

        viewHolder.cCal.setText( item.getType().toString() );

        switch( item.getType() ){
            case ACADEMISCHE_AGENDA:
                viewHolder.cHolder.setBackgroundResource( R.drawable.academische_agenda );
                break;
            case LESSENROOSTER:
                viewHolder.cHolder.setBackgroundResource( R.drawable.lessenrooster );
                break;
        }
    }

    @Override
    public int getItemCount(){
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout cHolder;

        private TextView dDayName, dDayNumber, dMonth;
        private TextView cName, cCal, cLoc, cTime;

        public ViewHolder( View itemView ){
            super( itemView );

            cHolder = (LinearLayout)itemView.findViewById( R.id.course_holder );

            dDayName = (TextView)itemView.findViewById( R.id.date_dayname );
            dDayNumber = (TextView)itemView.findViewById( R.id.date_daynumber );
            dMonth = (TextView)itemView.findViewById( R.id.date_month );

            cName = (TextView)itemView.findViewById( R.id.course_name );
            cCal = (TextView)itemView.findViewById( R.id.course_cal );
            cLoc = (TextView)itemView.findViewById( R.id.cource_loc );
            cTime = (TextView)itemView.findViewById( R.id.course_time );

            cHolder.startAnimation( fade );
        }
    }
}
