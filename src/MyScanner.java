public class MyScanner implements AutoCloseable {
    public MyScanner() {
        _scanner = new java.util.Scanner(System.in);
    }

    // Private field "_scanner" handles text input via System.in
    private java.util.Scanner _scanner;

    // will solve for now by using different method names - but should really use generics
    public long getUserInputAsLong(String question) {
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
    public String getUserInputAsString(String question) {
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

    public double getUserInputAsDouble(String question) {
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
    public void _print(Object obj) {
        System.out.println(obj);
    }

    public void close() {
        _scanner = null;
    }
}