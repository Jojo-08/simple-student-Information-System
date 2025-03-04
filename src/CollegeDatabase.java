import java.io.*;
import java.util.*;
import javax.swing.JOptionPane;

public class CollegeDatabase {

    private String csvFile;

    public CollegeDatabase(String csvFile)
    {
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
                System.out.println("Raw line from CSV: " + line); // Debugging
                
                //  Split into exactly two parts: College Code & Full College Name
                String[] parts = line.split(",", 2);
                
                if (parts.length == 2) {
                    String collegeCode = parts[0].trim();
                    String collegeName = parts[1].trim(); // Preserve full name
                    System.out.println("Parsed College: " + collegeCode + " - " + collegeName); // Debugging
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
    
    
    
    
    public void updateCollege(String collegeCode, String collegeName) {
        List<College> colleges = readColleges();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile))) {
            for (College college : colleges) {
                if (college.getCollegeCode().equals(collegeCode)) {
                    bw.write(collegeCode + "," + collegeName); // Correct update
                } else {
                    bw.write(college.toString());
                }
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void deleteCollege(String collegeCode) {
        // Show a confirmation dialog to the user for college deletion
        int confirmation = JOptionPane.showConfirmDialog(null, 
            "Warning: Deleting this college will also delete all associated programs and students. Do you want to proceed?", 
            "Confirm Deletion", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirmation == JOptionPane.YES_OPTION) {
            // First, delete all programs associated with the college without showing another dialog
            ProgramDatabase programDB = new ProgramDatabase("programs.csv");
            List<Program> programs = programDB.readPrograms();
            
            for (Program program : programs) {
                if (program.getCollegeCode().equals(collegeCode)) {
                    // Call deleteProgram with true to skip the confirmation dialog
                    programDB.deleteProgram(program.getProgramCode(), true); // Delete the program
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
        } else {
            // User chose not to proceed with deletion
            System.out.println("Deletion canceled by the user.");
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
