import java.util.*;

public class Sort {

   
    //Sorts a list of students based on the provided comparator.
   
     //param students the list of students to be sorted
     //param comparator the comparator defining the sorting order
    
    public static void sortStudents(List<Student> students, Comparator<Student> comparator) {
        if (students == null || comparator == null) {
            throw new IllegalArgumentException("Students list and comparator must not be null");
        }
        students.sort(comparator);
    }

    public static void sortPrograms(List<Program> programs, Comparator<Program> comparator) {
        if (programs == null || comparator == null) {
            throw new IllegalArgumentException("Programs list and comparator must not be null");
        }
        programs.sort(comparator);
    }

    public static void sortColleges(List<College> colleges, Comparator<College> comparator) {
        if (colleges == null || comparator == null) {
            throw new IllegalArgumentException("Colleges list and comparator must not be null");
        }
        colleges.sort(comparator);
    }


  
    //Comparator for sorting students by last name.
     
    public static Comparator<Student> byLastName() {
        return Comparator.comparing(Student::getLastName);
    }

  
    // Comparator for sorting students by first name.
     
    public static Comparator<Student> byFirstName() {
        return Comparator.comparing(Student::getFirstName);
    }

  
     //Comparator for sorting students by year level.
   
    public static Comparator<Student> byYearLevel() {
        return Comparator.comparing(Student::getYearLevel);
    }

  
     // Comparator for sorting students by program code.
    
    public static Comparator<Student> StudentbyProgramCode() {
        return Comparator.comparing(Student::getProgramCode);
    }

 
    public static Comparator<Student> StudentbyCollegeCode(ProgramDatabase programDatabase) {
        return new Comparator<Student>() {
            @Override
            public int compare(Student s1, Student s2) {
                String collegeCode1 = programDatabase.getCollegeCodeByProgram(s1.getProgramCode());
                String collegeCode2 = programDatabase.getCollegeCodeByProgram(s2.getProgramCode());
                return collegeCode1.compareTo(collegeCode2);
            }
        };
    }

    
    public static Comparator<Program> byProgramName() {
        return Comparator.comparing(Program::getProgramName);
    }

    public static Comparator<Program> ProgrambyProgramCode() 
    {
        return Comparator.comparing(Program::getProgramCode);
    }

    public static Comparator<Program> ProgrambyCollegeCode() {
        return Comparator.comparing(Program::getCollegeCode);
    }

    public static Comparator<College> byCollegeName() {
        return Comparator.comparing(College::getCollegeName);
    }

    public static Comparator<College> CollegebyCollegeCode() {
        return Comparator.comparing(College::getCollegeCode);
    }
}