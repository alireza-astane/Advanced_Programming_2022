package logic.enums;

public enum DepartmentType {
    ELECTRICAL_ENGINEERING("Electrical Engineering"),
    COMPUTER_SCIENCE("Computer Science"),
    MATHEMATICS("Mathematics"),
    PHYSICS("Physics");

    public final String name;

    DepartmentType(String name) {
        this.name = name;
    }
}
