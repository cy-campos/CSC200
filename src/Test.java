import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.lang.Double;

public class Test {

    public static Scanner _scanner = new Scanner(System.in);

    public static void main(String[] args) {

        // How many students?
        System.out.println("How many students?");
        int studentCount = _scanner.nextInt();

        // scores for student 1, 2, etc.
        List<Student> studentList = new ArrayList<Student>();

        for (int i = 0; i < studentCount; i++) {
            System.out.println("Scores for student " + i + ":");

            Student newStudent = new Student();

            double entry = 0;

            // entries
            do {
                entry = _scanner.nextDouble();

                if (entry == -1)
                    break;

                newStudent.gradeList.add(entry);
            } while (entry != -1);

            newStudent.setAverageGrade();

            studentList.add(newStudent);
        }

        Double gradeSum = new Double(0);
        Double count = new Double(0);

        for (int i = 0; i < studentList.size(); i++) {
            Double value = studentList.get(i).averageGrade;
            System.out.println("Student " + i + " Avg: " + value);

            for (int x = 0; x < studentList.get(i).gradeList.size(); x++) {
                gradeSum += studentList.get(i).gradeList.get(x);
                count++;
            }
        }

        System.out.println("\n\nClass Avg: " + gradeSum / count);
    }


    public static class Student {
        Double averageGrade;
        List<Double> gradeList;

        public Student() {
            averageGrade = new Double(0);
            gradeList = new ArrayList<Double>();
        }

        public Double getAverageGrade() {
            Double sum = 0.0;
            for (int i = 0; i < gradeList.size(); i++)
                sum += gradeList.get(i);

            return sum / gradeList.size();
        }

        public void setAverageGrade() {
            averageGrade = getAverageGrade();
        }
    }
}
