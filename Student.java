
public class Student {
    private String id;
    private String firstName;
    private String lastName;
    private String yearLevel;
    private String gender;
    private String programCode;

    public Student(String id, String firstName, String lastName, String yearLevel, String gender, String programCode)
    {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.yearLevel = yearLevel;
        this.gender = gender;
        this.programCode = programCode;
    }
    public String getId()
    {
        return id;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public String getYearLevel()
    {
        return yearLevel;
    }
    public String getGender()
    {
        return gender;
    }

    public String getProgramCode()
    {
        return programCode;
    }

    public void setProgramCode(String programCode)
    {
        this.programCode = programCode;
    }
    
    @Override
    public String toString()
    {
        return id + "," + firstName + "," + lastName + "," + yearLevel + "," + gender + "," + programCode;
    }
}
