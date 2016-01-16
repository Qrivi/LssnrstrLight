package be.krivi.omaapp;

/**
 * Created by Krivi on 15/01/16.
 */
public enum CalType{
    LESSENROOSTER( "Lessenrooster" ),
    ACADEMISCHE_AGENDA( "Academische agenda" );

    String name;

    CalType(String name){
        this.name = name;
    }

    @Override
    public String toString(){
        return name;
    }
}
