public class College {
    private String collegeCode;
    private String collegeName;

    public College(String collegeCode, String collegeName)
    {
        this.collegeCode = collegeCode;
        this.collegeName = collegeName;

    }

    public String getCollegeCode()
    {
        return collegeCode;
    }

    public String getCollegeName()
    {
        return collegeName;
    }
    
    @Override
public String toString() {
    return collegeCode + "," + collegeName;
}

}
