/*
    Title:          HomeworkAssignment3
    Scope:          CSC 200 (002M) - Yun-Sheng Wang, Ph.D
    Author:         Christopher Yap Campos
    Contact:        cy.campos1983@gmail.com
    Created:        17OCT2018
    Dependencies:   Requires java 10 in order to use inference type "var"
    Summary:        HW2

    Instructions:

        Due: 8am 11/17/2018

        Requirements:

        1) must use one-dimension array (section 7.1)

        PRELIMINARY SPECIFICATION:

        Specify the model with 3 numbers:

        > 1 3 5     (They can be 1, 7, and 19)

        The bias is: xxx

        Give me a 0/1 representation based on the above model:

        > 010100011

        The above 0/1 represents:

        > yyyy

        Give me a decimal:

        > -81.234

        Hey, it cannot be represented! Try again.

        Give me a decimal:

        -1.23

        The above decimal is represented as:

        0111011111 (you figure it out)
*/

import java.io.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Hw4_Campos {
    private static Scanner myScanner = new Scanner(System.in);

    public static void main(String[] args) {
        Model model = new Model();

        System.out.println("Specify the model with 3 numbers:");
        for (int i = 0; i < model.numbers.length; i++)
            model.numbers[i] = myScanner.nextInt();

        System.out.println("The bias is: " + model.getBias());
        myScanner.nextLine();

        System.out.println("Give me a 0/1 representation based on the above model:");
        var binaryRepresentation = myScanner.nextLine();

        System.out.println("The above 0/1 represents: " + model.getDecimalRepresentationFromBinary(binaryRepresentation));

        System.out.println("Give me a decimal:");
        var decimal = myScanner.nextLine();

        getBinaryRepresentationFromDecimal(decimal, model);
    }

    private static void getBinaryRepresentationFromDecimal(String decimal, Model model) {
        try {
            System.out.println("The above decimal is represented as: " + model.getBinaryRepresentationFromDecimal(decimal));
        } catch (IllegalArgumentException ex) {
            System.out.println("Hey, it cannot be represented. Try again.");
            myScanner.nextLine();
            getBinaryRepresentationFromDecimal(decimal, model);
        }
    }

    private static class Model {
        public String bias;

        public int[] numbers;

        public String binaryRepresentation;
        public double decimal;
        public String solution;

        public Model() {
            // [0] => sign
            // [1] => exponent
            // [2] => mantissa
            numbers = new int[3];
        }

        public double getBias() {
            // 2^(k−1) − 1 where k is the number of bits in the exponent.
            var numberOfBitsInExponent = numbers[1];
            return (Math.pow(2, (numberOfBitsInExponent - 1)));
        }

        private String calculateMantissa(String mantissa, double exponent) {
            var exponentAsInt = (int) exponent;

            List<String> charList = new ArrayList<String>();
            for (int i = 0; i < mantissa.length(); i++) {
                charList.add(String.valueOf(mantissa.charAt(i)));
            }

            if (exponentAsInt < 0) {
                var index = charList.size() - Math.abs(exponentAsInt);

                if (index < 0) {
                    for (int i = index; i < 0; i++) {
                        charList.add(0,"0");
                    }
                    charList.add(0,"0.");
                } else {
                    charList.add(index, ".");
                }

            } else if (exponentAsInt > 1) {
                for (int i = 0; i < exponentAsInt; i++)
                    charList.add("0");
            }

            String result = "";
            for (String bit : charList) {
                result += bit;
            }

            BaseValue baseValue = new BaseValue();
            baseValue.set_base(2);
            baseValue.set_value(result);

            var test = BaseValue.convertToBase(baseValue, 10);

            return test.get_value();
        }

        public String getDecimalRepresentationFromBinary(String binary) {
            var bias = getBias();

            // populate floating point model
            var floatingPointRepresentation = parseBinary(binary);

            // calculate values to decimal
            // exponent is twos complement
            var exponent = floatingPointRepresentation.getExponentAsDecimal() - getBias();

            var solvedMantissa = calculateMantissa(floatingPointRepresentation.mantissa, exponent);

            // M * B^E
            //var value = mantissa * (Math.pow(2,exponent - getBias() ));

            if (numbers[0] == 1)
                solvedMantissa = "-" + solvedMantissa;

            return solvedMantissa;
        }

        private class FloatingPointRepresentation {
            public String sign;
            public String exponent;
            public String mantissa;

            public FloatingPointRepresentation(String sign, String exponent, String mantissa) {
                this.sign = sign;
                this.exponent = exponent;
                this.mantissa = mantissa;
            }

            public int getExponentAsDecimal() {
                var numberOfbits = exponent.length();
                var total = 0;

                for (int i = 0; i < exponent.length(); i++) {
                    var bit = Integer.parseInt(String.valueOf(exponent.charAt(i)));
                    var multiplier = (numberOfbits - 1) - i;
                    var value = Math.pow(2, multiplier) * bit;

                    if (i == 0 && bit == 1)
                        value *= -1;

                    total += value;
                }

                return total;
            }

            public String toString() {
                return sign + exponent + mantissa;
            }
        }

        private FloatingPointRepresentation parseBinary(String binary) {
            String sign = "";
            int signLength = numbers[0];

            String exponent = "";
            int exponentLength = numbers[1] + signLength;

            String mantissa = "";
            int mantissaLength = numbers[2] + exponentLength;

            for (int i = 0; i < binary.length(); i++) {
                String bit = String.valueOf(binary.charAt(i));

                if (i < signLength) {
                    sign += bit;
                    continue;
                } else if (i < exponentLength) {
                    exponent += bit;
                    continue;
                } else {
                    mantissa += bit;
                }
            }

            return new FloatingPointRepresentation(sign, exponent, mantissa);
        }

        public String getBinaryRepresentationFromDecimal(String decimal) {
            // BaseValue does not support negative numbers
            var sign = decimal.contains("-") ? "1" : "0";

            if (sign.equals("1"))
                decimal = decimal.replace("-","");

            //11.01
            var binary = BaseValue.convertToBase(new BaseValue(10, decimal), 2).get_value();

            // get location of the point
            var pointIndex = binary.indexOf(".");
            if (pointIndex == -1)
                pointIndex = binary.length();

            if ((binary.length() != 5) && (!binary.contains("."))) {
                binary += "0";
            }

            // have to determine if we move the point left or right
            // case | binary: 0.001     | pointIndex: 1     => fail - should move right 2 and value should be 1000 (exp: 2)
            // case | binary: 111       | pointIndex: -1;   => fail - should move left 3 and value should be 0111 (exp: -3)
            // case | binary: 11.01     | pointIndex: 2     => pass - moves left 2 and value is 1101 (exp: -2)

            // if we have values to the right of the decimal that are not 0, then use a positive exponent

            // remove the point - result will be the mantissa
            binary = binary.replace(".","");

            var exponent = pointIndex * -1;
            var exponentValue = "";

            // if the pointIndex is negative, then convert the value to a negative binary
            if (exponent < 0) {
                var binaryExponent = BaseValue.convertToBase(new BaseValue(10, String.valueOf(Math.abs(exponent))), 2).get_value();
                var flippedBinaryExponent = "";

                // flip the exponent and add 1
                for (int i = 0; i < binaryExponent.length(); i++) {
                    var bit = binaryExponent.charAt(i);
                    flippedBinaryExponent += bit == '1' ? "0" : "1";
                }

                // generate a binary 1 to add
                var oneBinary = "";
                for (int i = 0; i < (flippedBinaryExponent.length() - 1); i++) {
                    oneBinary += "0";
                }
                oneBinary += "1";

                var test = new HW3Data();
                var sum = test.addBinaryNumbers(flippedBinaryExponent, oneBinary);

                exponentValue = "1" + sum;

                if (exponentValue.length() > numbers[1]) {
                    // try to trim zeros from the right first
                    var newValue = "";
                    boolean shouldAppend = false;
                    for (int i = 0; i < exponentValue.length(); i++) {
                        var bit = exponentValue.charAt(i);
                        if (bit == '0' && !shouldAppend) {
                            continue;
                        } else if (bit == '0' && shouldAppend)
                            newValue += bit;
                        else {
                            newValue += bit;
                            shouldAppend = true;
                        }
                    }

                    if (newValue.length() > numbers[1])
                        throw new IllegalArgumentException();

                    exponentValue = newValue;
                }
                else if (exponentValue.length() < numbers[1]) {
                    for (int i = (numbers[1] - exponentValue.length() + 1); i < numbers[1]; i++)
                        exponentValue += "0";
                }
            } else if (exponent == 0) {
                // generate 0 value for exponent
                for(int i = 0; i < numbers[1]; i++)
                    exponentValue += "0";
            }

            // trim trailing 0s from binary
            if (binary.length() > numbers[2]) {
                var newValue = "";
                for (int i = 0; i < numbers[2]; i++) {
                    var bit = binary.charAt(i);
                    newValue += bit;
                }
                binary = newValue;
            }

            var floatingPointRepresentation = new FloatingPointRepresentation(sign, exponentValue, binary);

            return floatingPointRepresentation.toString();
        }
    }

    static class BaseValue {
        public BaseValue() {
            _base = 0;
            _value = null;
        }

        public BaseValue(int base, String value) {
            _base = base;
            _value = value;
        }

        // Because we have delegated _base, _value to getters/setters, we can enforce logic for acceptable parameters in "set"
        private int _base;

        public int get_base() {
            return _base;
        }

        public void set_base(int base) throws IllegalArgumentException {
            if (base < 2)
                throw new IllegalArgumentException("Base cannot be less than 1");
            else if (base > 16)
                throw new IllegalArgumentException("Base cannot be greater than 16");
            else
                _base = base;
        }

        private String _value;

        public String get_value() {
            return _value;
        }

        public void set_value(String value) {
            value = value.toLowerCase();

            // use a regex: to support up to base 16, we can only take ranges "0-9" & "a-f"
            var conforms = Pattern.matches("^[a-f0-9\\.]+$", value);

            if (!conforms)
                throw new IllegalArgumentException("Value can only contain values [0-9][a-f]");
            else
                _value = value;
        }

        public static final class LetterValues {
            public static final int A = 10;
            public static final int B = 11;
            public static final int C = 12;
            public static final int D = 13;
            public static final int E = 14;
            public static final int F = 15;

            public static Integer getValue(String strNumber) throws IllegalArgumentException {
                if (strNumber.length() > 1)
                    throw new IllegalArgumentException("Too many characters.");

                switch (strNumber.toLowerCase()) {
                    case "a":
                        return A;
                    case "b":
                        return B;
                    case "c":
                        return C;
                    case "d":
                        return D;
                    case "e":
                        return E;
                    case "f":
                        return F;
                    default:
                        throw new IllegalArgumentException("Value out of range.");
                }
            }

            public static String getValue(double number) throws IllegalArgumentException {
                var asInt = (int) number;

                switch (asInt) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                        return asInt + "";
                    case 10:
                        return "a";
                    case 11:
                        return "b";
                    case 12:
                        return "c";
                    case 13:
                        return "d";
                    case 14:
                        return "e";
                    case 15:
                        return "f";
                    default:
                        throw new IllegalArgumentException();
                }
            }
        }

        private static boolean _containsDecimal(String value) {
            return value.contains(".");
        }

        public static BaseValue convertToBase(BaseValue value, Integer base) throws UnsupportedOperationException {
            if (value.get_base() == base)
                return value;

            if (base != 10) {
                var _out = new BaseValue();

                // convert to base 10
                var base10 = value.get_base() == 10 ? value : BaseValue.toBase10(value);

                _out.set_value(_convertBase10StringToNewBase(base10.get_value(), base));
                _out.set_base(base);

                return _out;

            } else {
                return BaseValue.toBase10(value);
            }
        }

        private static String _convertBase10StringToNewBase(String base10Value, int newBase) {

            if (_containsDecimal(base10Value)) {
                var strSplit = base10Value.split("\\.");
                var strLeft = strSplit[0];
                var strRight = strSplit[1];

                var strLeftSolved = _convertBase10ToBaseLeft(strLeft, newBase);

                var strRightSolved = _convertBase10ToBaseRight(strRight, newBase);

                return strLeftSolved + "." + strRightSolved;
            } else {
                return _convertBase10ToBaseLeft(base10Value, newBase);
            }
        }

        private static String _convertBase10ToBaseLeft(String strLeft, int newBase) {
            var base10ValueAsDouble = Double.parseDouble(strLeft);
            var remainder = 0;
            var roundedValue = (int) Math.floor(base10ValueAsDouble);

            java.util.List<String> stringList = new java.util.ArrayList<>();

            do {
                remainder = roundedValue % newBase;
                stringList.add(remainder > 9 ? LetterValues.getValue(remainder) : remainder + "");
                roundedValue = (int) Math.floor(roundedValue / newBase);

            } while (roundedValue != 0);

            var _outvalue = "";
            // stringlist is backwards, so we have append it to the value backwards
            for (var x = stringList.size() - 1; x >= 0; x--) {
                _outvalue += stringList.get(x);
            }

            return _outvalue;
        }

        private static String _convertBase10ToBaseRight(String strRight, int newBase) {
            var value = Double.valueOf("." + strRight);
            var base = Double.valueOf(newBase + "");
            var total = Double.valueOf(0);
            var _out = "";

            // we'll set precision to the amount of sigfigs in strRight
            var tries = strRight.length();

            do {
                var strValue = (value * base) + "";
                var strSplit = strValue.split("\\.");
                var _strLeft = strSplit[0];
                var _strRight = strSplit[1];
                _out += LetterValues.getValue(Double.valueOf(_strLeft).doubleValue());
                value = Double.parseDouble("." + _strRight);

                tries--;
            } while ((tries > 0) && (value != 0.0));

            return _out;
        }

        // Returns the (Base 10) Value of the given baseValue
        public static BaseValue toBase10(BaseValue baseValue) {
            var base = baseValue.get_base();
            var value = baseValue.get_value();

            if (base == 10)
                return baseValue;

            if (_containsDecimal(value)) {
                var strSplit = value.split("\\.");
                var strLeft = strSplit[0];
                var strRight = strSplit[1];

                // solve for the left side of the decimal
                var strLeftSolved = _convertToBase10Left(strLeft, base);

                // solve for the right side of the decimal
                var strRightSolved = _convertToBase10Right(strRight, base);

                // concatenate the values and return BaseValue
                return new BaseValue(10, strLeftSolved + "." + strRightSolved);
            } else {

                var _out = new BaseValue();

                _out.set_value(_convertToBase10Left(baseValue.get_value(), baseValue.get_base()));
                _out.set_base(10);

                return _out;
            }
        }

        private static String _convertToBase10Left(String leftValue, int base) {
            var total = Double.valueOf(0);
            var increment = Double.valueOf(0);
            var _value = leftValue;
            var _base = base;

            // convert base 10 to given base param
            for (var x = _value.length() - 1; x >= 0; x--) {
                // get the first letter as String
                var character = String.valueOf(_value.charAt(x));

                // determine if character is a number
                if (Pattern.matches("[0-9]", character)) {
                    var number = Integer.parseInt(character);
                    total += number * Math.pow(_base, increment);
                } else if (Pattern.matches("[a-f]", character)) {  // determine if char is a letter
                    var number = LetterValues.getValue(character);
                    total += number * Math.pow(_base, increment);
                }

                increment++;
            }

            var integer = total.intValue();
            return integer + "";
        }

        private static String _convertToBase10Right(String rightValue, int base) {
            var increment = Double.valueOf(1);
            var _base = Double.valueOf(base + "");
            var total = Double.valueOf(0);

            // convert base 10 to given base param
            for (var x = 0; x < rightValue.length(); x++) {
                var character = String.valueOf(rightValue.charAt(x));

                // determine if character is a number
                if (Pattern.matches("[0-9]", character)) {
                    var number = Double.parseDouble(character);
//                total += number * Math.pow(_base, -increment);
                    var pow = Math.pow(_base, (-1 * increment));
                    total += number * pow;
                } else if (Pattern.matches("[a-f]", character)) {  // determine if char is a letter
                    var number = LetterValues.getValue(character);
                    total += number * Math.pow(_base, -increment);
                }

                increment++;
            }

            return (total + "").replace("0.", "");
        }
    }

    static class HW3Data{
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

//            if ((charArray1.length != 4) || (charArray1.length != charArray2.length))
//                throw new IllegalArgumentException();
//            if (!binaryValue1.matches("[0-1]{4}"))
//                throw new IllegalArgumentException();

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
}