package be.krivi.omaapp;

import net.fortuna.ical4j.model.DateTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Krivi on 14/01/16.
 */
public class CalItem implements Comparable{

    private Calendar c;
    private SimpleDateFormat sdfDay, sdfMonth;

    private String name, loc;
    private DateTime start, end;
    private CalType type;

    public CalItem( String name, String loc, DateTime start, DateTime end, CalType type ){
        c = Calendar.getInstance();

        sdfDay = new SimpleDateFormat( "EEEE", Locale.getDefault() );
        sdfMonth = new SimpleDateFormat( "MMMM", Locale.getDefault() );

        setName( name );
        setLoc( loc );
        setStart( start );
        setEnd( end );
        setType( type );
    }

    private void setName( String name ){
        this.name = name.toUpperCase().replaceAll( "MB.*-", "" );
        // cleans UCLL event names a bit and uppercase makes other events more uniform
    }

    private void setLoc( String loc ){
        if( loc.length() > 0 && loc.charAt( loc.length() - 1 ) == ';' )
            loc = loc.substring( 0, loc.length() - 1 ); // removes mysterious semicolon in UCLL events
        this.loc = loc;
    }

    private void setStart( DateTime start ){
        this.start = start;
    }

    private void setEnd( DateTime end ){
        this.end = end;
    }

    private void setType( CalType type ){
        this.type = type;
    }

    public String getName(){
        return name;
    }

    public String getLoc(){
        return loc;
    }

    public DateTime getStart(){
        return start;
    }

    public DateTime getEnd(){
        return end;
    }

    public CalType getType(){
        return type;
    }

    public String getDayNumber(){
        c.setTime( this.getStart() );
        return c.get( Calendar.DAY_OF_MONTH ) + "";
    }

    public String getDayName(){
        return sdfDay.format( this.getStart() );
    }

    public String getMonthName(){
        return sdfMonth.format( this.getStart() );
    }

    public String getStartTime(){
        c.setTime( this.getStart() );

        String minutes = c.get( Calendar.MINUTE ) + "";
        if( minutes.length() == 1 )
            minutes = 0 + minutes;

        return c.get( Calendar.HOUR_OF_DAY ) + ":" + minutes;
    }

    public String getEndTime(){
        c.setTime( this.getEnd() );

        String minutes = c.get( Calendar.MINUTE ) + "";
        if( minutes.length() == 1 )
            minutes = 0 + minutes;

        return c.get( Calendar.HOUR_OF_DAY ) + ":" + minutes;
    }

    @Override
    public int compareTo( Object o ){
        CalItem i = (CalItem)o;
        if( this == i )
            return 0;
        else if( start.after( i.getStart() ) )
            return 1;
        else
            return -1;
    }
}
