import java.util.*;
import org.sql2o.*;
import java.text.*;

public class Student {
  private int id;
  private String first_name;
  private String last_name;
  private String doe;
  private Format formatter = new SimpleDateFormat("MMMM dd yyyy");

  public Student(String first_name, String last_name) {
    Date date = new Date();
    this.first_name = first_name;
    this.last_name = last_name;
    this.doe = formatter.format(date);
  }

  public String getFirstName() {
    return first_name;
  }

  public String getLastName() {
    return last_name;
  }

  public String getDoe() {
    return doe;
  }

  public int getId() {
    return id;
  }

  public static List<Student> all() {
    String sql = "SELECT * FROM students ORDER BY last_name ASC";
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery(sql).executeAndFetch(Student.class);
    }
  }

  @Override
  public boolean equals(Object otherStudent) {
    if (!(otherStudent instanceof Student)) {
      return false;
    } else {
      Student newStudent = (Student) otherStudent;
      return this.getFirstName().equals(newStudent.getFirstName()) &&
      this.getId() == newStudent.getId();
    }
  }

  public void save() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO students(first_name, last_name, doe) VALUES (:first_name, :last_name, :doe)";
      this.id = (int) con.createQuery(sql, true)
      .addParameter("first_name", this.first_name)
      .addParameter("last_name", this.last_name)
      .addParameter("doe", this.doe)
      .executeUpdate()
      .getKey();
    }
  }

  public static Student find(int id) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT * FROM students where id=:id";
      Student student = con.createQuery(sql)
      .addParameter("id", id)
      .executeAndFetchFirst(Student.class);
      return student;
    }
  }

  public void delete() {
  try(Connection con = DB.sql2o.open()) {
    String deleteQuery = "DELETE FROM students WHERE id = :id;";
      con.createQuery(deleteQuery)
        .addParameter("id", this.getId())
        .executeUpdate();

    String joinDeleteQuery = "DELETE FROM student_courses WHERE student_id = :studentId";
      con.createQuery(joinDeleteQuery)
        .addParameter("studentId", this.getId())
        .executeUpdate();
  }
}

  public void addCourse(Course course) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO student_courses (student_id, course_id, complete) VALUES (:student_id, :course_id, :complete)";
      con.createQuery(sql)
      .addParameter("student_id", this.getId())
      .addParameter("course_id", course.getId())
      .addParameter("complete", false)
      .executeUpdate();
    }
  }


  public List<Course> getCourses() {
    try(Connection con = DB.sql2o.open()){
      String joinQuery = "SELECT course_id FROM student_courses WHERE student_id = :student_id";
      List<Integer> courseIds = con.createQuery(joinQuery)
      .addParameter("student_id", this.getId())
      .executeAndFetch(Integer.class);

      List<Course> courses = new ArrayList<Course>();

      for (Integer courseId : courseIds) {
        String courseQuery = "Select * From courses WHERE id = :courseId ORDER BY course ASC";
        Course course = con.createQuery(courseQuery)
        .addParameter("courseId", courseId)
        .executeAndFetchFirst(Course.class);
        courses.add(course);
      }
      return courses;
    }
  }

}
