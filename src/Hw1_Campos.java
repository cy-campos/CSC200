/*
    Title:          HomeworkAssignment1
    Scope:          CSC 200 (002M) - Yun-Sheng Wang, Ph.D
    Author:         Christopher Yap Campos
    Contact:        cy.campos1983@gmail.com
    Created:        25AUG2018
    Dependencies:   Requires java 10 in order to use inference type "var"
    Summary:        Handle simple input for Painters. Painter model (see PainterData.java)
                    Data will represent how much a painter can paint (in square yards) given the amount of time he has.
                    More requirements will be given in Week 2.
    Requirements Updated 01SEP2018
        - UI will show:
            "Give me 2 names" -> Input 2 names (first and last names)               -> Input "John Smith", "Jane Doe"
            "Two painters are {name1_firstName} and {name2_firstName}"
            "How many square yards can {name1} paint in 45 minutes?"                -> Input "1245"
            "How many hours does it take for Jane to paint 2000 square yards?"      -> Input "3.6"
            "How many square feet do they need to paint?"                           -> Input "987654"
            "It will take them {hours} hours."
 */
import java.util.ArrayList;
import java.util.List;

public class Hw1_Campos {

    // Private field "_scanner" handles text input via System.in
    private static java.util.Scanner _scanner = new java.util.Scanner(System.in);

    public static void main(String[] args) {

        // "Give me 2 names"
        PainterData data1 = new PainterData();
        data1.name = getUserInputAsString("Give me name 1:");
        data1.time.set_minutes(45);

        PainterData data2 = new PainterData();
        data2.name = getUserInputAsString("Give me name 2:");
        data2.set_squareYards(2000);

        // "Two painters are {name1_firstName} and {name2_firstName}"
        _print(String.format("The two painters are %s and %s.", data1.getFirstName(), data2.getFirstName()));

        // "How many square yards can {name1} paint in 45 minutes?"
        data1.set_squareYards(getUserInputAsDouble(String.format("How may square yards can %s paint in %f minutes?",
                data1.getFirstName(),
                data1.time.get_minutes())));

        // "How many hours does it take for Jane to paint 2000 square yards?"
        data2.time.set_hours(getUserInputAsDouble(String.format("How many hours does it take for %s to paint in %f square yards?",
                data2.getFirstName(),
                data2.get_squareYards())));

        long squareFeetToPaint = getUserInputAsLong("How many square feet do they need to paint?");

        // package up the data - "List" is an "interface"; ArrayList inherits "List"
        List<PainterData> dataList = new ArrayList<>();
        dataList.add(data1);
        dataList.add(data2);

        MyTime result =  PainterData.getTimeToCompleteJob(squareFeetToPaint, dataList);

        _print(String.format("It will take them %f hours", result.get_hours()));
        _print(result.toString());
    }

    private static MyTime _calculateHours(long squareFeetToPaint, PainterData data1, PainterData data2) {
        return new MyTime();
    }

    // will solve for now by using different method names - but should really use generics
    private static long getUserInputAsLong(String question) {
        try {
            _print(question);
            return _scanner.nextLong();
        } catch (Exception exception) {
            System.out.println(String.format("Error: Input Invalid | Please re-enter value\nMessage: %s\n", exception.toString()));
            _scanner.nextLine();

            // simple recursion
            return getUserInputAsLong(question);
        }
    }

    // if we ever require the user to input a Data.name, this will come in handy
    private static String getUserInputAsString(String question) {
        try {
            _print(question);
            String _out = _scanner.nextLine();
            return _out;
        } catch (Exception exception) {
            System.out.println(String.format("Error: Input Invalid | Please re-enter value\nMessage: %s\n", exception.toString()));
            _scanner.nextLine();

            // simple recursion
            return getUserInputAsString(question);
        }
    }

    private static double getUserInputAsDouble(String question) {
        try {
            _print(question);
            double _out = _scanner.nextDouble();
            return _out;
        } catch (Exception exception) {
            System.out.println(String.format("Error: Input Invalid | Please re-enter value\nMessage: %s\n", exception.toString()));
            _scanner.nextLine();

            // simple recursion
            return getUserInputAsDouble(question);
        }
    }

    // will figure this out later; essentially, we have to use 'reference' representations of primitives (i.e. Long ~ long, Int ~ int, etc)
    /*
    private static <T> T getUserInput(String question, Class<T> object) {
        var strType = object.getName();
        _print(strType);
    }
    */

    // lazy shortcut for System.out.println
    private static void _print(Object obj) {
        System.out.println(obj);
    }
}

class PainterData {
    public String name;
    public MyTime time;

    private double _squareYards;
    public double get_squareYards() { return _squareYards; }
    public void set_squareYards(double squareYards) {
        _squareYards = squareYards;
        _squareFeet = squareYards * 9;
    }

    private double _squareFeet;
    public double get_squareFeet() { return _squareFeet; }
    public void set_squareFeet(long squareFeet) {
        _squareFeet = squareFeet;
        _squareYards = squareFeet / 9;
    }

    public PainterData() {
        name = null;
        time = new MyTime();
        _squareYards = 0;
    }

    public String toString() {
        String message = String.format("Name: %s\nTime (DD:HH:MM:SS): %s\nSquare Yards: %d",
                name,
                time != null ? time.toString() : "N/A",
                _squareYards);
        return message;
    }

    public String getFirstName() {
        String[] stringSplit = name.split(" ");
        return ((stringSplit != null) && (stringSplit.length > 0)) ? stringSplit[0] : null;
    }

    public String getLastName() {
        String[] stringSplit = name.split(" ");
        return ((stringSplit != null) && (stringSplit.length > 1)) ? stringSplit[1] : null;
    }

    public double getSquareFeetPerMinute() {
        double minutes = time != null ? time.get_minutes() : -1;
        return ((minutes != 0) || (minutes != -1)) ? (_squareFeet / minutes) : -1;
    }

    public static MyTime getTimeToCompleteJob(long squareFeetToPaint, List<PainterData> dataList) {
        // will figure "foreach" later; use "for" for now
        // foreach()

        double totalRate = 0;

        for(int i = 0; i < dataList.size(); i++) {
            PainterData data = dataList.get(i);

            // given 2 painters, calculate hours it will take for both painters to paint a room of X square feet
            // - lowest common denominator of time is minutes; target output is hours

            // formula is: totalHours = squareFeetToPaint / (rateOfData1 + rateOfData2)
            totalRate += data.getSquareFeetPerMinute();
        }

        MyTime _out = new MyTime();

        // for more precision, use seconds
        double totalSeconds = (((double)squareFeetToPaint) / totalRate) * 60;
        _out.set_seconds(Math.round(totalSeconds));

        return _out;
    }
}


// Simple custom time conversion class. This was created because I did not immediately find a simple and straightforward
// time conversion library in the Java framework; Although, I'm sure it exists somewhere.
// No logic to handle "double" overflows has been implemented - so beware (ex: put in a number outside of the acceptable range of double)
class MyTime {

    public MyTime() {
        _days = 0;
        _hours = 0;
        _minutes = 0;
        _seconds = 0;
        _totalSeconds = 0;
    }

    // _totalSeconds is the lowest common denominator of time that this class supports
    private double _totalSeconds;

    // 1 second in a second
    private double _seconds;
    public void set_seconds(double seconds) {
        _totalSeconds = seconds;
        _calculate();
    }

    // 60 seconds in a minute
    private double _minutes;
    public double get_minutes() { return _totalSeconds / 60; }
    public void set_minutes(long minutes) {
        _totalSeconds = minutes * 60;
        _calculate();
    }

    // 3,600 seconds in a hour
    private double _hours;
    public double get_hours() { return _totalSeconds / 3600; }
    public void set_hours(double hours) {
        _totalSeconds =  hours * 3600;
        _calculate();
    }

    private double _days;
    public void set_days(double _days) {
        // 86,400 seconds in a day
        _totalSeconds = _days * 86400;
        _calculate();
    }

    private void _calculate() {
        double totalSeconds = (double)_totalSeconds;
        _days = totalSeconds / (24*60*60);
        _hours = _days*24;
        _minutes = _hours*60;
        _seconds = _minutes*60;
    }

    public String toString() {
        // for accuracy, start with seconds and work way up to hours
        long _seconds = (long) this._totalSeconds % 60;
        long _minutes = (long) (this._totalSeconds / 60) % 60;
        long _hours = (long) (this._totalSeconds / (60 * 60)) % 24;
        long _days = (long) Math.floor(this._totalSeconds / (60*60*24));


        // format the numbers as string output (add a "0" if less than 10)
        String days = _days < 10 ? "0" + _days : _days + "";
        String hours = _hours < 10 ? "0" + _hours : _hours + "";
        String minutes = _minutes < 10 ? "0" + _minutes : _minutes + "";
        String seconds = _seconds < 10 ? "0" + _seconds : _seconds + "";

        String _out = String.format("%s:%s:%s:%s", days, hours, minutes, seconds);
        return _out;
    }
}