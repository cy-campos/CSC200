/*
    Title:          HomeworkAssignment3
    Scope:          CSC 200 (002M) - Yun-Sheng Wang, Ph.D
    Author:         Christopher Yap Campos
    Contact:        cy.campos1983@gmail.com
    Created:        17OCT2018
    Dependencies:   Requires java 10 in order to use inference type "var"
    Summary:        HW2

        - UI Will show:

            under 2's comp 4-bit repr;  adding two #s

            Give me a decimal # A:

            9

            >> wrong; exiting

            ==

            Give me a decimal # A:

            3

            bin repr -->

            Give me a decimal # B:

            -2

            bin repr-->

            flow-in: xxx

            flow-out: xxx

            the sum of the two #s in binary --->

            continuie?

            yes <--- repeat the process

            no <--- exit
 */


public class Hw3_Campos {
    public static void main(String[] args) {

        var shouldContinue = false;
        do {
            // Get User Input -> "decimal # A" and "decimal # B" and display "bin repr-->"
            var homework3Data = getUserInput();

            // calculate the data received from the user
            homework3Data.calculate();

            // display output
            try (var _scanner = new MyScanner()) {
                _scanner.print(String.format("flow-in: %s", homework3Data.flowIn));
                _scanner.print(String.format("flow-out: %s", homework3Data.flowOut));
                _scanner.print(String.format("the sum of the two #s in binary ---> %s", homework3Data.get_sum()));

                String strShouldContinue = _scanner.getUserInputAsString("\ncontinue?");

                // requirements to continue running loop are not too clear : if user does not enter 'yes' or 'no'
                // explicitly, then what? Will assume that 'yes' is clear intent to continue and all other values are
                // 'no'
                shouldContinue = strShouldContinue.matches("yes");
            }
        } while (shouldContinue);
    }

    public static HW3Data getUserInput() {
        var out = new HW3Data();

        try (var _scanner = new MyScanner()) {
            _scanner.print("under 2's comp 4-bit repr;  adding two #s");

            //  ">> wrong; exiting" - per requirements, validation must occur immediately after input
            var decimalAInput = _scanner.getUserInputAsInteger("Give me a decimal # A:");
            out.set_decimalA(decimalAInput);

            // (Decimal A) bin repr-->
            _scanner.print(String.format("bin repr--> %s", out.get_decimalA_asBinary()));

            var decimalBInput = _scanner.getUserInputAsInteger("Give me a decimal # B:");
            out.set_decimalB(decimalBInput);

            // (Decimal B) bin repr-->
            _scanner.print(String.format("bin repr--> %s", out.get_decimalB_asBinary()));

            // validation of the sum of values
            var decimalSum = out.get_decimalA() + out.get_decimalB();

            if ((decimalSum > 7) || (decimalSum < -8))
                throw new IllegalArgumentException("Range of values for the sum (in 4-bit signed binary) must be no less than -8 and no greater than 7");

        } catch (IllegalArgumentException ex) {
            System.out.println("wrong; exiting");
            System.out.println(String.format("Reason: %s", ex.getMessage()));

            System.exit(-1);
        }

        return out;
    }
}

// Data object for Homework 3 Data - Extends BaseValue (Created in Homework 2 in order to re-use code)
class HW3Data{
    private int _decimalA;
    public int get_decimalA() {
        return _decimalA;
    }
    public void set_decimalA(int value) throws IllegalArgumentException {
        if (isValid4BitNumber(value))
            _decimalA = value;
        else
            throw new IllegalArgumentException(String.format("Illegal input: %s", value));
    }
    public String get_decimalA_asBinary() {
        return _toBinary(_decimalA);
    }

    private int _decimalB;
    public int get_decimalB() {
        return _decimalB;
    }
    public void set_decimalB(int value) throws IllegalArgumentException {
        if (isValid4BitNumber(value))
            _decimalB = value;
        else
            throw new IllegalArgumentException(String.format("Illegal input: %s", value));
    }
    public String get_decimalB_asBinary() {
        return _toBinary(_decimalB);
    }

    private boolean isValid4BitNumber(int value) {
        if ((value < -8) || (value > 7))
            return false;
        else
            return true;
    }

    // convert value to binary
    private String _toBinary(int value) {
        // need absolute value of the input
        var absoluteValue = Math.abs(value);

        if (value >= 0) {
//            _out = _out.length() < 4 ? (0 + _out) : _out;
            return getBinaryRepresentation(absoluteValue);
        }
        else {
            var positveBinary = getBinaryRepresentation(absoluteValue);
            var negativeBinary = get4BitBinaryTwosComplement(positveBinary);
            var _out = addBinaryNumbers(negativeBinary, _toBinary(1));

            return _out;
        }
    }

    private String getBinaryRepresentation(int positiveNumber) throws IllegalArgumentException {
        if (positiveNumber < 0)
            throw new IllegalArgumentException();

        var _out = "";

        do {
            _out = (positiveNumber % 2) + _out;
            positiveNumber = positiveNumber / 2;
        } while (positiveNumber > 0);

        while (_out.length() < 4) {
            _out = "0" + _out;
        }

        return _out;
    }

    private String get4BitBinaryTwosComplement(String value) {
        // flip values and add 1 to get get 2's complement
        var charArray = value.toCharArray();
        var inverse = "";

        // get inverse
        for (int i = 0; i < charArray.length; i++) {
            inverse += (charArray[i] == '1') ? "0" : "1";
        }

        return inverse;
    }

    private String addBinaryNumbers(String binaryValue1, String binaryValue2) {
        return addBinaryNumbers(binaryValue1, binaryValue2, false);
    }
    private String addBinaryNumbers(String binaryValue1, String binaryValue2, boolean setFlowVariables) throws IllegalArgumentException {
        var charArray1 = binaryValue1.toCharArray();
        var charArray2 = binaryValue2.toCharArray();

        if ((charArray1.length != 4) || (charArray1.length != charArray2.length))
            throw new IllegalArgumentException();
        if (!binaryValue1.matches("[0-1]{4}"))
            throw new IllegalArgumentException();

        // Honestly, not exactly sure what flow-in and flow-out refer to--assume it is the part of the "un-signed"
        // part of the binaryValues to add?
        if (setFlowVariables) {
            flowIn = "";

            for (int i = 1; i < charArray1.length; i++) {
                flowIn += charArray1[i];
            }
        }

        var _out = "";

        Integer overflow = 0;

        for (int i = (charArray1.length - 1); i >= 0; i--) {
            var sum = 0;
            var bit1 = Integer.valueOf(String.valueOf(charArray1[i]));
            var bit2 = Integer.valueOf(String.valueOf(charArray2[i]));

            sum = overflow + bit1 + bit2;

            _out = (sum % 2) + _out;
            overflow = sum >= 1 ? sum - 1 : sum;
        }

        if (setFlowVariables) {
            flowOut = "";
            for (int i = 1; i < _out.length(); i++)
                flowOut += _out.charAt(i);
        }

        return _out;
    }

    private String _sum;
    public String get_sum() { return _sum; }
    private void _set_sum(String value) throws IllegalArgumentException {
        // just some quick validation
        if (value.matches("[0-9]{4}"))
            _sum = value;
        else
            throw new IllegalArgumentException();
    }

    public String flowIn = null;
    public String flowOut = null;

    public void calculate() {
        var binaryA = get_decimalA_asBinary();
        var binaryB = get_decimalB_asBinary();
        _set_sum(addBinaryNumbers(binaryA, binaryB, true));
    }
}