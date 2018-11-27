package team1.mobileapp.com;

public class User {
    private String name;
    private String studentNumber;
    private int gender;
    private int birthYear;
    private String department;

    public User(String name, int gender, int birthYear, String department, String studentNumber){
        this.name = name;
        this.gender = gender;
        this.birthYear = birthYear;
        this.department = department;
        this.studentNumber = studentNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
