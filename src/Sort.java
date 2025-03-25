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

    public static Comparator<Student> byId() {
        return new Comparator<Student>() {
            @Override
            public int compare(Student s1, Student s2) {
                String id1 = s1.getId();
                String id2 = s2.getId();
    
                // Split the IDs into year and number parts
                String[] parts1 = id1.split("-");
                String[] parts2 = id2.split("-");
    
                // Compare the year parts
                int yearComparison = Integer.compare(Integer.parseInt(parts1[0]), Integer.parseInt(parts2[0]));
                if (yearComparison != 0) {
                    return yearComparison; // If years are different, return the comparison result
                }
    
                // If years are the same, compare the number parts as integers
                int numberComparison = Integer.compare(Integer.parseInt(parts1[1]), Integer.parseInt(parts2[1]));
                return numberComparison; // Return the comparison result for the number parts
            }
        };
    }
    
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

    public static Comparator<Student> byGender() {
        return Comparator.comparing(Student::getGender);
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
