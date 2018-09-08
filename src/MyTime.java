// Simple custom time conversion class. This was created because I did not immediately find a simple and straightforward
// time conversion library in the Java framework; Although, I'm sure it exists somewhere.
// No logic to handle "double" overflows has been implemented - so beware (ex: put in a number outside of the acceptable range of double)
public class MyTime {
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
        var totalSeconds = (double)_totalSeconds;
        _days = totalSeconds / (24*60*60);
        _hours = _days*24;
        _minutes = _hours*60;
        _seconds = _minutes*60;
    }

    public String toString() {
        // for accuracy, start with seconds and work way up to hours
        var _seconds = (long) this._totalSeconds % 60;
        var _minutes = (long) (this._totalSeconds / 60) % 60;
        var _hours = (long) (this._totalSeconds / (60 * 60)) % 24;
        var _days = (long) Math.floor(this._totalSeconds / (60*60*24));


        // format the numbers as string output (add a "0" if less than 10)
        String days = _days < 10 ? "0" + _days : _days + "";
        String hours = _hours < 10 ? "0" + _hours : _hours + "";
        String minutes = _minutes < 10 ? "0" + _minutes : _minutes + "";
        String seconds = _seconds < 10 ? "0" + _seconds : _seconds + "";

        var _out = String.format("%s:%s:%s:%s", days, hours, minutes, seconds);
        return _out;
    }
}