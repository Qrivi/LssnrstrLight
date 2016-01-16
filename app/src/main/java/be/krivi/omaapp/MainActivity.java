package be.krivi.omaapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import cz.msebera.android.httpclient.Header;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.util.CompatibilityHints;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    private static final String URI_LESSEN = "https://rooster.ucll.be/export.php?what=uurrooster_studenten_persoonlijk&type=ical&academiejaar=2015&username=r0468690&unique=1d6d4d95c1d6d82cd3186e0a3c2b077019870818";
    private static final String URI_AGENDA = "https://calendar.google.com/calendar/ical/kristof.victor%40gmail.com/private-67364099033724de4f5463ce7b8aea13/basic.ics";

    private RecyclerView recyclerView;
    private CalAdapter adapter;
    private MainActivity activity;

    private ConnectivityManager cm;

    private CalendarBuilder builder;
    private List<CalItem> data;
    private AsyncHttpClient client;

    private LinearLayout eHolder, cHolder;
    private TextView cName, cCal, cLoc, cTime;

    private Animation fade;

    @Override
    protected void onCreate( Bundle savedInstanceState ){
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        this.activity = this;
        this.recyclerView = (RecyclerView)findViewById( R.id.recycler );

        this.eHolder = (LinearLayout)findViewById( R.id.event_holder );
        this.cHolder = (LinearLayout)findViewById( R.id.course_holder );
        this.cName = (TextView)findViewById( R.id.course_name );
        this.cCal = (TextView)findViewById( R.id.course_cal );
        this.cLoc = (TextView)findViewById( R.id.cource_loc );
        this.cTime = (TextView)findViewById( R.id.course_time );

        cm = (ConnectivityManager)getSystemService( Context.CONNECTIVITY_SERVICE );

        CompatibilityHints.setHintEnabled( CompatibilityHints.KEY_RELAXED_PARSING, true );
        builder = new CalendarBuilder();
        data = new ArrayList<>();
        client = new AsyncHttpClient();

        adapter = new CalAdapter( activity, data );
        recyclerView.setAdapter( adapter );
        recyclerView.setLayoutManager( new LinearLayoutManager( activity ) );

        fade = AnimationUtils.loadAnimation( this, R.anim.fade_in );

        fetchEvents();
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ){
        getMenuInflater().inflate( R.menu.menu_main, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ){
        // there is only one menu item :)
        fetchEvents();

        return super.onOptionsItemSelected( item );
    }

    private void fetchEvents(){
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if( netInfo != null && netInfo.isConnectedOrConnecting() ){
            data.clear();
            adapter.updateData( data );
            client.get( URI_AGENDA, new ICSParser( this, CalType.ACADEMISCHE_AGENDA ) );
            client.get( URI_LESSEN, new ICSParser( this, CalType.LESSENROOSTER ) );
            Toast.makeText( this, R.string.fetching_calendars, Toast.LENGTH_LONG ).show();
        }else{
            Toast.makeText( this, R.string.no_internet_connection, Toast.LENGTH_LONG ).show();
        }
    }

    private void setCurrentEvent( CalItem event ){
        if( event != null ){
            this.eHolder.setVisibility( View.VISIBLE );
            this.cHolder.startAnimation( fade );

            this.cName.setText( event.getName() );
            this.cLoc.setText( event.getLoc() );
            this.cTime.setText( getString( R.string.over_at_time, event.getEndTime() ) );

            this.cCal.setText( event.getType().toString() );

            switch( event.getType() ){
                case ACADEMISCHE_AGENDA:
                    this.cHolder.setBackgroundResource( R.drawable.academische_agenda );
                    break;
                case LESSENROOSTER:
                    this.cHolder.setBackgroundResource( R.drawable.lessenrooster );
                    break;
            }
        }
    }

    public class ICSParser extends AsyncHttpResponseHandler{

        private Context context;
        private CalType type;

        public ICSParser( Context context, CalType type ){
            this.context = context;
            this.type = type;
        }

        @Override
        public void onSuccess( int statusCode, Header[] headers, byte[] response ){
            try{
                InputStream is = new ByteArrayInputStream( response );
                Calendar calendar = builder.build( is );

                String name = "", loc = "", start = "", end = "";

                for( Object o : calendar.getComponents() ){
                    Component component = (Component)o;

                    for( Object p : component.getProperties() ){
                        Property property = (Property)p;
                        //Log.v( "CalItem", "Property [" + property.getName() + ", " + property.getValue() + "]" );

                        switch( property.getName() ){
                            case "SUMMARY":
                                name = property.getValue();
                                break;
                            case "LOCATION":
                                loc = property.getValue();
                                break;
                            case "DTSTART":
                                start = property.getValue();
                                break;
                            case "DTEND":
                                end = property.getValue();
                                break;
                        }
                    }

                    if( !name.equals( "" ) && !loc.equals( "" ) && !start.equals( "" ) && !end.equals( "" ) ){
                        DateTime startTime = new DateTime( start );
                        DateTime endTime = new DateTime( end );
                        Date now = new Date();

                        if( startTime.after( now ) )
                            data.add( new CalItem( name, loc, startTime, endTime, type ) );
                        else if( endTime.after( now ) )
                            setCurrentEvent( new CalItem( name, loc, startTime, endTime, type ) );
                    }
                    Collections.sort( data );
                    adapter.updateData( data );
                }
                //            }catch( IOException e ){
                //                e.printStackTrace();
                //            }catch( ParserException e ){
                //                e.printStackTrace();
                //            }catch( ParseException e ){
                //                e.printStackTrace();
            }catch( Exception e ){
                Toast.makeText( context, R.string.error_in_calendar, Toast.LENGTH_LONG ).show();
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure( int statusCode, Header[] headers, byte[] errorResponse, Throwable e ){
            // called when response HTTP status is "4XX" (eg. 401, 403, 404)
            Toast.makeText( context, R.string.cannot_retrieve_calendar, Toast.LENGTH_LONG ).show();
            e.printStackTrace();
        }
    }
}
