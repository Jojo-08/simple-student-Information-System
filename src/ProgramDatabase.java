import java.io.*;
import java.util.*;
import javax.swing.JOptionPane;

public class ProgramDatabase {

    private String csvFile;

    public ProgramDatabase(String csvFile)
    {
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
    
    public void updateProgram(String programCode, String programName, String collegeCode) {
        List<Program> programs = readPrograms();
    
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile))) {
            for (Program program : programs) {
                if (program.getProgramCode().equals(programCode)) {
                    bw.write(programCode + "," + programName + "," + collegeCode);
                } else {
                    bw.write(program.toString());
                }
                bw.newLine();
            }
        } catch (IOException e) {
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

    public void deleteProgram(String programCode, boolean fromCollegeDeletion) {
        // If not called from college deletion, show a confirmation dialog
        if (!fromCollegeDeletion) {
            int confirmation = JOptionPane.showConfirmDialog(null, 
                "Warning: Deleting this program will also delete all associated students. Do you want to proceed?", 
                "Confirm Deletion", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirmation != JOptionPane.YES_OPTION) {
                // User chose not to proceed with deletion
                System.out.println("Deletion canceled by the user.");
                return;
            }
        }
    
        // First, delete all students associated with the program
        StudentDatabase studentDB = new StudentDatabase("students.csv");
        List<Student> students = studentDB.readStudents();
        
        for (Student student : students) {
            if (student.getProgramCode().equals(programCode)) {
                studentDB.deleteStudent(student.getId()); // Delete the student
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
