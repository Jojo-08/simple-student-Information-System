import java.io.*;
import java.util.*;

public class ProgramDatabase {

    private String csvFile;
    private StudentDatabase studentDB;

    public ProgramDatabase(String csvFile, StudentDatabase studentDB)
    {   
        this.studentDB = studentDB;
        this.csvFile = csvFile;
    }

    public void createProgram(String programCode, String programName, String collegeCode) {
        Program program = new Program(programCode, programName, collegeCode);
    
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile, true))) {
            bw.write(program.toString());
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void updateProgram(String oldProgramCode, String newProgramCode, String programName, String collegeCode) {
        List<Program> programs = readPrograms();
        boolean updated = false; // Track if an update occurred
    
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile))) {
            for (Program program : programs) {
                if (program.getProgramCode().equals(oldProgramCode)) {
                    // Update the program name and code
                    bw.write(newProgramCode + "," + programName + "," + collegeCode); // Write updated program
                    updated = true; // Mark as updated
                    System.out.println("Updated program: " + newProgramCode + " - " + programName); // Debugging
                    
                    // Update corresponding students
                    studentDB.updateStudentsProgramCode(oldProgramCode, newProgramCode);
                } else {
                    bw.write(program.toString()); // Write existing program
                }
                bw.newLine();
            }
            
            if (!updated) {
                System.out.println("No program found with code: " + oldProgramCode); // Debugging
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateProgramsCollegeCode(String oldCollegeCode, String newCollegeCode)
    {
        List<Program> programs = readPrograms();

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile)))
        {
            for( Program program : programs)
            {
                if(program.getCollegeCode().equals(oldCollegeCode))
                {
                    program.setCollegeCode(newCollegeCode);
                    //System.out.println("Updated the program " + program.getProgramName() + " College Code: " + oldCollegeCode + " to " + newCollegeCode);
                }
                bw.write(program.toString());
                bw.newLine();

            }
            
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public List<Program> readPrograms()
    {
        List<Program> programs = new ArrayList<>();

        String line;

        String csvSplitBy =",";

        try( BufferedReader br = new BufferedReader (new FileReader(csvFile)))
        {
            while((line = br.readLine()) != null)
            {
                String [] data  = line.split(csvSplitBy);
                if(data.length == 3)
                {
                    Program program = new Program (data [0], data[1], data[2]);
                    programs.add(program);
                }
            }
        }

        catch(IOException e)
        {
            e.printStackTrace();
        }
        return programs;
    }

    public String getCollegeCodeByProgram(String programCode) {
        List<Program> programs = readPrograms();
        for (Program program : programs) {
            if (program.getProgramCode().equals(programCode)) {
                return program.getCollegeCode();
            }
        }
        return null; // or throw an exception if the program code is not found
    }

    public void deleteProgram(String programCode) {
        
        // First, delete all students associated with the program
        StudentDatabase studentDB = new StudentDatabase("students.csv");
        List<Student> students = studentDB.readStudents();
        
        for (Student student : students) {
            if (student.getProgramCode().equals(programCode)) {
                studentDB.updateStudentsProgramCode(programCode, "Unenrolled"); // Delete the student
            }
        }
    
        // Now delete the program
        List<Program> programs = readPrograms();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile))) {
            for (Program program : programs) {
                if (!program.getProgramCode().equals(programCode)) {
                    bw.write(program.toString());
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public boolean doesProgramExist(String programCode, String programName) {
        List<Program> programs = readPrograms(); // Fetch all programs
        for (Program program : programs) {
            if (program.getProgramCode().equalsIgnoreCase(programCode) ||program.getProgramName().equalsIgnoreCase(programName)) {
                return true; // ✅ Program exists
            }
        }
        return false; // ❌ Program does not exist
    }

    public boolean doesProgramExist(String programCode)
    {
       return doesProgramExist(programCode, "");
    }


    
    
}
