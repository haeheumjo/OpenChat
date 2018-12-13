package team1.mobileapp.com.model;

public class ChatRoom {

    String key;
    String name;
    String description;
    int optionMaxCount;
    int optionName;
    int optionAge;
    int optionSex;
    int optionStudentNumber;
    int optionDepartment;

    public ChatRoom(){

    }

    public ChatRoom(String key, String name, String description, int optionName, int optionMaxCount, int optionAge, int optionSex, int optionStudentNumber, int optionDepartment){
        this.key = key;
        this.name = name;
        this.description = description;
        this.optionMaxCount = optionMaxCount;
        this.optionName = optionName;
        this.optionAge = optionAge;
        this.optionSex = optionSex;
        this.optionStudentNumber = optionStudentNumber;
        this.optionDepartment = optionDepartment;

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getOptionMaxCount() {
        return optionMaxCount;
    }

    public void setOptionMaxCount(int optionMaxCount) {
        this.optionMaxCount = optionMaxCount;
    }

    public int getOptionName() {
        return optionName;
    }

    public void setOptionName(int optionName) {
        this.optionName = optionName;
    }

    public int getOptionAge() {
        return optionAge;
    }

    public void setOptionAge(int optionAge) {
        this.optionAge = optionAge;
    }

    public int getOptionSex() {
        return optionSex;
    }

    public void setOptionSex(int optionSex) {
        this.optionSex = optionSex;
    }

    public int getOptionStudentNumber() {
        return optionStudentNumber;
    }

    public void setOptionStudentNumber(int optionStudentNumber) {
        this.optionStudentNumber = optionStudentNumber;
    }

    public int getOptionDepartment() {
        return optionDepartment;
    }

    public void setOptionDepartment(int optionDepartment) {
        this.optionDepartment = optionDepartment;
    }
}
