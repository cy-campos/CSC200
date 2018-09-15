public class MyScanner implements AutoCloseable {
    public MyScanner() {
        _scanner = new java.util.Scanner(System.in);
    }

    // Private field "_scanner" handles text input via System.in
    public java.util.Scanner _scanner;

    // will solve for now by using different method names - but should really use generics
    public long getUserInputAsLong(String question) {
        try {
            print(question);
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
            print(question);
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
            print(question);
            double _out = _scanner.nextDouble();
            return _out;
        } catch (Exception exception) {
            System.out.println(String.format("Error: Input Invalid | Please re-enter value\nMessage: %s\n", exception.toString()));
            _scanner.nextLine();

            // simple recursion
            return getUserInputAsDouble(question);
        }
    }

    public int getUserInputAsInteger(String question) {
        try {
            print(question);
            var _out = _scanner.nextInt();
            return _out;
        } catch (Exception exception) {
            System.out.println(String.format("Error: Input Invalid | Please re-enter value\nMessage: %s\n", exception.toString()));
            _scanner.nextLine();

            // simple recursion
            return getUserInputAsInteger(question);
        }
    }

    // looks like enums here do not work as well as they only support integer values by default
    // - a recommended solution to this is to use private static final- could also use key/value pairs but it seems like
    //      this pattern is done more often..
    public static final class InputTypes {
        public static final String INTEGER = java.lang.Integer.class.getName();
        public static final String DOUBLE = java.lang.Double.class.getName();
        public static final String LONG = java.lang.Long.class.getName();
        public static final String STRING = java.lang.String.class.getName();
        public static final String FLOAT = java.lang.Float.class.getName();
        public static final String BOOLEAN = java.lang.Boolean.class.getName();
    }

    // We need param Class<T> object as a work-around b/c the IDE cannot infer the type of T before run-time
    public <T> T getUserInput(String question, T object) throws Exception {
        if (object.getClass().isPrimitive())
            throw new Exception("T getUserInput(String question, T object) is not compatible with primitives");

        var strType = object.getClass().getName();

        // can't use a "switch" here because even though InputTypes are static and final, their values
        // are not known until compile time. To solve, we could manually initialize them as strings, but
        // from my understanding of "best practices", changes to the underlying primitive class methods such as "getName()"
        // could potentially break code (although not likely to ever happen).

        // For complete safety, we can just use if/else conditionals.. though it is ugly. Will eventually check out enums and
        // dictionaries to see if they provide a better alternative
        if (strType.equals(InputTypes.INTEGER))
            return (T)Integer.valueOf(getUserInputAsInteger(question));
        else if (strType.equals(InputTypes.DOUBLE))
            return (T)Double.valueOf(getUserInputAsDouble(question));
        else if (strType.equals(InputTypes.LONG))
            return (T)Long.valueOf(getUserInputAsLong(question));
        else if (strType.equals(InputTypes.STRING))
            return (T)String.valueOf(getUserInputAsString(question));
        else if (strType.equals(InputTypes.FLOAT))
            throw new UnsupportedOperationException();
        else if (strType.equals(InputTypes.BOOLEAN))
            throw new UnsupportedOperationException();
        else
            return null;
    }

    // lazy shortcut for System.out.println
    public void print(Object obj) {
        System.out.println(obj);
    }

    // lazy unit test
    public void runLazyUnitTest() {
        var _int = 0;
        var _long = (long) 0;
        var _double = 0.5;
        var _float = (float) _double;

        // Error - cannot pass primitives
        //var test = this.getUserInput("hmm", _int);

        // Try wrapping primitives to their respective reference classes
        Integer wrapped_int = _int;

        try  {
            var test = this.getUserInput(String.format("Enter a %s:", wrapped_int.getClass().getName()), wrapped_int);
            this.print(test);

            var test2 = this.getUserInput("Enter a double:", Double.valueOf(123.456));
            this.print(test2);

        } catch (Exception ex){

        }
    }

    // dispose of managed resources
    public void close() {
        _scanner = null;
    }
}