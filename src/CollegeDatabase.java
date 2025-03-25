import java.io.*;
import java.util.*;

public class CollegeDatabase {

    private String csvFile;
    private StudentDatabase studentDB;
    private ProgramDatabase programDB;

    public CollegeDatabase(String csvFile, StudentDatabase studentDB, ProgramDatabase programDB) 
    {
        this.studentDB =  studentDB;
        this.programDB = programDB;
        this.csvFile = csvFile;

    }

    public void createCollege(String collegeCode, String collegeName )
    {
        College college = new College(collegeCode, collegeName);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile, true)))
        {
            bw.write(college.toString());
            bw.newLine();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public List<College> readColleges() {
        List<College> colleges = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = br.readLine()) != null) {
               // System.out.println("Raw line from CSV: " + line); // Debugging
                
                //  Split into exactly two parts: College Code & Full College Name
                String[] parts = line.split(",", 2);
                
                if (parts.length == 2) {
                    String collegeCode = parts[0].trim();
                    String collegeName = parts[1].trim(); // Preserve full name
                   // System.out.println("Parsed College: " + collegeCode + " - " + collegeName); // Debugging
                    colleges.add(new College(collegeCode, collegeName));
                } else {
                    System.out.println("Invalid line format: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return colleges;
    }
    
    
    
    
    public void updateCollege(String oldCollegeCode, String newCollegeCode, String collegeName) {
        List<College> colleges = readColleges();
        boolean updated = false; // Track if an update occurred
    
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile))) {
            for (College college : colleges) {
                if (college.getCollegeCode().equals(oldCollegeCode)) {
                    // Update the college name
                    bw.write(newCollegeCode + "," + collegeName); // Write updated college
                    updated = true; // Mark as updated
                   
                    System.out.println("Updated college: " + newCollegeCode + " - " + collegeName); // Debugging
               
                    programDB.updateProgramsCollegeCode(oldCollegeCode, newCollegeCode);
                } else {
                    bw.write(college.toString()); // Write existing college
                }
                bw.newLine();
            }
            
            if (!updated) {
                System.out.println("No college found with code: " + newCollegeCode); // Debugging
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void deleteCollege(String collegeCode) {      
             programDB = new ProgramDatabase("programs.csv", studentDB);
            List<Program> programs = programDB.readPrograms();
            
            for (Program program : programs) {
                if (program.getCollegeCode().equals(collegeCode)) {
                    
                    programDB.deleteProgram(program.getProgramCode()); // Delete the program
                }
            }
    
            // Now delete the college
            List<College> colleges = readColleges();
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile))) {
                for (College college : colleges) {
                    if (!college.getCollegeCode().equals(collegeCode)) {
                        bw.write(college.toString());
                        bw.newLine();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } 
    
    
    public boolean doesCollegeExist(String collegeCode, String collegeName) {
        List<College> colleges = readColleges(); // Fetch all colleges
        for (College college : colleges) {
            if (college.getCollegeCode().equalsIgnoreCase(collegeCode) || college.getCollegeName().equalsIgnoreCase(collegeName)) {
                return true; // ✅ college exists
            }
        }
        return false; // ❌ college does not exist
    }

    public boolean doesCollegeExist(String collegeCode)
    {
        return doesCollegeExist(collegeCode, "");
    }


}
