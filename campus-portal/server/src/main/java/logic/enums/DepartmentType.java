package logic.enums;

public enum DepartmentType {
    COMPUTER_SCIENCE("Computer Science"),
    MATHEMATICS("Mathematics"),
    PHYSICS("Physics"),
    ELECTRICAL_ENGINEERING("Electrical Engineering"),
    CHEMISTRY("Chemistry"),
    GENERAL_STUDIES("General Studies");

    public final String title;

    DepartmentType(String title) {
        this.title = title;
    }
}
