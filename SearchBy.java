import java.util.ArrayList;
import java.util.List;

public class SearchBy {

    private StudentDatabase studentDatabase;
    private ProgramDatabase programDatabase;
    private CollegeDatabase collegeDatabase;
    

    public SearchBy( StudentDatabase studentDatabase, ProgramDatabase programDatabase, CollegeDatabase collegeDatabase ) {
    
        this.studentDatabase = studentDatabase;
        this.programDatabase = programDatabase;
        this.collegeDatabase = collegeDatabase;
    }

    public  List <College> getCollegeByCollegeName( String CollegeName)
    {
        List<College> colleges = collegeDatabase.readColleges();
        List<College> filteredColleges = new ArrayList<>();
        String query = CollegeName.toLowerCase().trim();

        for (College college: colleges) {
            String collegeNameLower = college.getCollegeName().toLowerCase().trim();
            if (collegeNameLower.contains(query)) { // Partial match
                filteredColleges.add(college);
            }
        }

        return filteredColleges;
    }
    public  List <College> getCollegeByCollegeCode( String CollegeCode)
    {
        List<College> colleges = collegeDatabase.readColleges();
        List<College> filteredColleges = new ArrayList<>();
        String query = CollegeCode.toLowerCase().trim();
        for(College college : colleges)
        {   
            String collegeCodeLower = college.getCollegeCode().toLowerCase().trim();
            if (collegeCodeLower.contains(query))
            {
               filteredColleges.add(college);
            }
        }

        return filteredColleges;
    }

    public List<Program> getProgramsByProgramName(String ProgramName)
    {
        List<Program> programs = programDatabase.readPrograms();
        List<Program> filteredPrograms = new ArrayList<>();
        String query = ProgramName.toLowerCase().trim();

        for (Program program : programs) {
            String ProgramNameLower = program.getProgramName().toLowerCase().trim();
            if (ProgramNameLower.contains(query)) { // Partial match
                filteredPrograms.add(program);
            }
        }

        return filteredPrograms;
    }
    
    public List<Program> getProgramsByProgramCode(String programCode)
    {
        List<Program> programs = programDatabase.readPrograms();
        List<Program> filteredPrograms = new ArrayList<>();
        String query = programCode.toLowerCase().trim();
        for(Program program: programs)
        {
            String programCodeLower = program.getProgramCode().toLowerCase().trim();
            if (programCodeLower.contains(query))
            {
               filteredPrograms.add(program);
            }
        }

        return filteredPrograms;
    }

    public List<Program> getProgramsByCollege(String collegeCode)
    {
        List<Program> programs = programDatabase.readPrograms();
        List<Program> filteredPrograms = new ArrayList<>();
        String query = collegeCode.toLowerCase().trim();
        for(Program program: programs)
        {
            String collegeCodeLower = program.getCollegeCode().toLowerCase().trim();
            if (collegeCodeLower.contains(query))
            {
               filteredPrograms.add(program);
            }
        }

        return filteredPrograms;
    }
     public List<Student> getStudentsById(String Id)
    {
        List<Student> students = studentDatabase.readStudents();
        List<Student> filteredStudents =  new ArrayList<>();
        for (Student student : students)
        {
            if(student.getId().contains(Id))
            {
                filteredStudents.add(student);
            }
        }

        return filteredStudents;
    }

    public List<Student> getStudentsByProgramCode(String programCode)
    {
        List<Student> students = studentDatabase.readStudents();
        List<Student> filteredStudents = new ArrayList<>();
        String query = programCode.toLowerCase().trim();
    
        for (Student student : students) {
            if (student.getProgramCode().toLowerCase().contains(query)) { // Partial match
                filteredStudents.add(student);
            }
        }
    
        return filteredStudents;
    }

    public List<Student> getStudentsByFirstName(String firstName)
    {
        List<Student> students = studentDatabase.readStudents();
        List<Student> filteredStudents = new ArrayList<>();
        String query = firstName.toLowerCase().trim();

        for (Student student : students) {
            String studentName = student.getFirstName().toLowerCase().trim();
            if (studentName.contains(query)) { // Partial match
                filteredStudents.add(student);
            }
        }

        return filteredStudents;
    }

    public List<Student> getStudentsByLastName(String lastName)
    {
        List<Student> students = studentDatabase.readStudents();
    List<Student> filteredStudents = new ArrayList<>();
    String query = lastName.toLowerCase().trim();

    for (Student student : students) {
        String studentName = student.getLastName().toLowerCase().trim();
        if (studentName.contains(query)) { // Partial match
            filteredStudents.add(student);
        }
    }

    return filteredStudents;
    }

    public List<Student> getStudentsByCollegeCode(String collegeCode) {
        List<Program> programs = getProgramsByCollege(collegeCode);
        List<Student> students = studentDatabase.readStudents();
        List<Student> filteredStudents = new ArrayList<>();
    
        if (programs.isEmpty()) return filteredStudents; // Prevent unnecessary looping
    
        for (Student student : students) {
            for (Program program : programs) {
                if (student.getProgramCode().equals(program.getProgramCode())) {
                    filteredStudents.add(student);
                    break;
                }
            }
        }
    
        return filteredStudents;
    }
    
    
    
}
