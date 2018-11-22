package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class Controller {

    //Lecture Table
    @FXML
    public TableView table;
    @FXML
    private TableColumn lessonCol;
    @FXML
    private TableColumn professorCol;
    @FXML
    private TableColumn dayCol;
    @FXML
    public MenuItem xmlButton;
    @FXML
    public MenuItem xsdButton;
    @FXML
    public Button addButton;
    @FXML
    public TextField lessonField;
    @FXML
    public TextField professorField;
    @FXML
    public TextField dayField;
    @FXML
    public TextField timeField;
    @FXML
    public ComboBox comboBox;


    private final ObservableList<Lecture> LCTRdata = FXCollections.observableArrayList();



    public void setLecture(String lesson, String professor, String day) {
        LCTRdata.add(new Lecture(lesson, professor, day));
        lessonCol.setCellValueFactory(new PropertyValueFactory<Lecture,String>("Lesson"));
        professorCol.setCellValueFactory(new PropertyValueFactory<Lecture,String>("Professor"));
        dayCol.setCellValueFactory(new PropertyValueFactory<Lecture,String>("Day"));
        lessonCol.setStyle("-fx-alignment: CENTER;");
        professorCol.setStyle("-fx-alignment: CENTER;");
        dayCol.setStyle("-fx-alignment: CENTER;");
        table.setItems(LCTRdata);
    }


    public static class Lecture {
        private final SimpleStringProperty Lesson;
        private final SimpleStringProperty Professor;
        private final SimpleStringProperty Day;

        private Lecture(String lesson, String professor, String day) {
            this.Lesson = new SimpleStringProperty(lesson);
            this.Professor = new SimpleStringProperty(professor);
            this.Day = new SimpleStringProperty(day);
        }

        public String getLesson() { return Lesson.get(); }
        public void setLesson(String lesson) { Lesson.set(lesson); }
        public String getProfessor() { return Professor.get(); }
        public void setProfessor(String professor) { Professor.set(professor); }
        public String getDay() { return Day.get(); }
        public void setDay(String day) { Day.set(day); }
    }



}