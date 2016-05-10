import java.util.*;
import org.sql2o.*;
import java.text.*;
import java.lang.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Course {
  private int id;
  private String course;
  private String course_num;

  public Course(String course, String course_num) {
    this.course = course;
    this.course_num = course_num;
  }

  public String getCourse() {
    return course;
  }

  public String getCourseNum() {
    return course_num;
  }

  public int getId() {
    return id;
  }

  // public String formatDuedate(){
  //   SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd");
  //   SimpleDateFormat myFormat = new SimpleDateFormat("MMMM dd yyyy");
  //   try {
  //     String reformattedStr = myFormat.format(fromUser.parse(duedate));
  //     return reformattedStr;
  //   } catch (ParseException e) {
  //     return "broken";
  //   }
  //
  // }

  public static List<Course> all() {
    String sql = "SELECT * FROM courses ORDER BY course ASC";
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery(sql).executeAndFetch(Course.class);
    }
  }

  @Override
  public boolean equals(Object otherCourse){
    if (!(otherCourse instanceof Course)) {
      return false;
    } else {
      Course newCourse = (Course) otherCourse;
      return this.getCourse().equals(newCourse.getCourse()) &&
      this.getId() == newCourse.getId();
    }
  }

  public void save() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO courses(course, course_num) VALUES (:course, :course_num)";
      this.id = (int) con.createQuery(sql, true)
      .addParameter("course", this.course)
      .addParameter("course_num", this.course_num)
      .executeUpdate()
      .getKey();
    }
  }

  public static Course find(int id) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT * FROM courses where id=:id";
      Course course = con.createQuery(sql)
      .addParameter("id", id)
      .executeAndFetchFirst(Course.class);
      return course;
    }
  }

  // public void update(String newDescription, String newDuedate) {
  //   try(Connection con = DB.sql2o.open()) {
  //     String sql = "UPDATE courses SET description = :description, duedate = :duedate WHERE id = :id";
  //     con.createQuery(sql)
  //     .addParameter("description", newDescription)
  //     .addParameter("duedate", newDuedate)
  //     .addParameter("id", this.id)
  //     .executeUpdate();
  //   }
  // }

  public void complete(int student_id) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "UPDATE student_courses SET complete = true WHERE course_id = :id AND student_id = :student_id";
      con.createQuery(sql)
      .addParameter("id", this.id)
      .addParameter("student_id", student_id)
      .executeUpdate();
    }
  }

  public void delete() {
    try(Connection con = DB.sql2o.open()) {
      String deleteQuery = "DELETE FROM courses WHERE id = :id;";
        con.createQuery(deleteQuery)
          .addParameter("id", this.getId())
          .executeUpdate();

      String joinDeleteQuery = "DELETE FROM student_courses WHERE course_id = :courseId";
        con.createQuery(joinDeleteQuery)
          .addParameter("courseId", this.getId())
          .executeUpdate();
    }
  }

  public void addStudent(Student student) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO student_courses (student_id, course_id) VALUES (:student_id, :course_id)";
      con.createQuery(sql)
      .addParameter("student_id", student.getId())
      .addParameter("course_id", this.getId())
      .executeUpdate();
    }
  }

  public List<Student> getStudents() {
    try(Connection con = DB.sql2o.open()){
      String joinQuery = "SELECT student_id FROM student_courses WHERE course_id = :course_id";
      List<Integer> studentIds = con.createQuery(joinQuery)
      .addParameter("course_id", this.getId())
      .executeAndFetch(Integer.class);

      List<Student> students = new ArrayList<Student>();

      for (Integer studentId : studentIds) {
        String courseQuery = "Select * From students WHERE id = :studentId";
        Student student = con.createQuery(courseQuery)
        .addParameter("studentId", studentId)
        .executeAndFetchFirst(Student.class);
        students.add(student);
      }
      return students;
    }
  }

}
