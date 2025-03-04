import java.io.*;
import java.util.*;

public class StudentDatabase {
    private String csvFile;

    public StudentDatabase(String csvFile)
    {
        this.csvFile = csvFile;
    }

    public void createStudent(String id, String firstName, String lastName, String yearLevel, String gender, String programCode)
    {
        Student student = new Student(id, firstName, lastName, yearLevel, gender, programCode);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile, true)))
        {
            bw.write(student.toString());
            bw.newLine();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public List<Student> readStudents()
    {
        List<Student> students = new ArrayList<>();
        String line;
        String csvSplitBy = ",";

        try(BufferedReader br = new BufferedReader(new FileReader(csvFile)))
        {
            while ((line = br.readLine()) != null)
            {
                String[] data = line.split(csvSplitBy);
                if (data.length == 6)
                {
                    Student student = new Student(data [0], data[1], data[2], data[3], data[4], data[5]);
                    students.add(student);
                }
            }
        }

        catch (IOException e)
        {
            e.printStackTrace();
        }

        return students;
    }

    public void updateStudent(String id, String firstName, String lastName, String yearLevel, String gender, String programCode) {
        List<Student> students = readStudents();
    
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile))) {
            for (Student student : students) {
                if (student.getId().equals(id)) {
                    bw.write(id + "," + firstName + "," + lastName + "," + yearLevel + "," + gender + "," + programCode);
                } else {
                    bw.write(student.toString());
                }
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    public void deleteStudent(String id)
    {
        List<Student> students =  readStudents();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile)))
        {
            for(Student student : students)
            {
                if(!student.getId().equals(id))
                {
                    bw.write(student.toString());
                    bw.newLine();
                }
            }
        }

        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public boolean validateStudentId(String studentId) {
        // Check length
        if (studentId.length() != 9) {
            return false;
        }

        // Check year part (YYYY)
        for (int i = 0; i < 4; i++) {
            if (!Character.isDigit(studentId.charAt(i))) {
                return false;
            }
        }

        // Extract year and check if it's greater than 1968
        int year = Integer.parseInt(studentId.substring(0, 4));
        if (year <= 2019) {
            return false;
        }

        // Check hyphen
        if (studentId.charAt(4) != '-') {
            return false;
        }

        // Check number part (NNNN)
        for (int i = 5; i < 9; i++) {
            if (!Character.isDigit(studentId.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    public boolean doesStudentExist(String Id, String firstName, String lastName)
    {
        List<Student> students = readStudents(); 
        for(Student student : students)
        {
            if(student.getId().equals(Id) || (student.getFirstName().equals(firstName) && student.getLastName().equals(lastName)))
            {
                return true;
            }
        }
        return false;
    }

    
    
    
}
