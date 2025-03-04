public class Program {
    private String programCode;
    private String programName;
    private String collegeCode;

    public Program(String programCode, String programName, String collegeCode)
    {
        this.programCode = programCode;
        this.programName = programName;
        this.collegeCode = collegeCode;
    }

    public String getProgramCode()
    {
        return programCode;
    }

    public String getProgramName()
    {
        return programName;
    }

    public String getCollegeCode ()
    {
        return collegeCode;
    }

    @Override
public String toString() {
    return programCode + "," + programName + "," + collegeCode;
}

}
