package sample;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.*;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class Main extends Application {

    public File xmlAssigned = null;
    public String xsdAssigned = null;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();

        controller.comboBox.getItems().addAll(
                "All Days",
                "Monday",
                "Tuesday",
                "Wednesday",
                "Thursday",
                "Friday",
                "Saturday",
                "Sunday"
        );



        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML", "*.xml"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XSD", "*.xsd"));


        controller.xmlButton.setOnAction((event) -> {
            configureFileChooser(fileChooser);
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {

                try {

                    xmlAssigned = file;
                    File schemaFile = new File(xsdAssigned); // etc.
                    Source xmlFile = new StreamSource(new File(xmlAssigned.toString()));
                    SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
                    try {
                        Schema schema = schemaFactory.newSchema(schemaFile);
                        Validator validator = schema.newValidator();
                        validator.validate(xmlFile);
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("File Success Validation");
                        alert.setHeaderText("File is valid..!");
                        alert.showAndWait();
                    } catch (Exception e) {
                        System.out.println(xmlFile.getSystemId() + " is NOT valid reason:" + e);
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("File Validation Error");
                        alert.setHeaderText("Something went wrong with the validation..!");
                        alert.setContentText(e.getMessage());
                        alert.showAndWait();
                    }

                    XMLParsing(primaryStage, controller, file, "All Days");

                } catch (Exception ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("File Parsing Error");
                    alert.setHeaderText("Something went wrong with the parsing..!");
                    alert.showAndWait();
                    ex.printStackTrace();
                }

            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("File Selection Error");
                alert.setHeaderText("Please select a valid XML file..!");
                alert.showAndWait();
            }
        });

        controller.xsdButton.setOnAction((event) -> {
            configureFileChooser(fileChooser);
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {

                xsdAssigned = file.toString();

            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("File Selection Error");
                alert.setHeaderText("Please select a valid XSD file..!");
                alert.showAndWait();
            }
        });

        controller.addButton.setOnAction((event) -> {

            try {


                LessonAdd(primaryStage, controller, xmlAssigned);

            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lesson Add Error");
                alert.setHeaderText("Something went wrong with the Lesson addition..!");
                alert.showAndWait();
                ex.printStackTrace();
            }
        });

        controller.comboBox.setOnAction((event) -> {
            String choice = controller.comboBox.getValue().toString();
            try {
                XMLParsing(primaryStage, controller, xmlAssigned, choice);
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Day Filter Error");
                alert.setHeaderText("Something went wrong with the day filter..!");
                alert.showAndWait();
                ex.printStackTrace();
            }


        });



        primaryStage.setTitle("XMLnXSDSee: XML w/ XSD Viewer & Editor");
        primaryStage.setScene(new Scene(root, 600, 450));
        primaryStage.show();
    }

    public void XMLParsing(Stage primaryStage, Controller controller, File file, String comboDay) throws Exception{
        controller.table.getItems().clear();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        DocumentBuilder builder = factory.newDocumentBuilder();

        Document document = builder.parse(new InputSource("file:///" + file.toString()));

        NodeList nodeList = document.getDocumentElement().getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) node;

                for(int k = 0; k < (elem.getElementsByTagName("Lecture").getLength()); k++) {
                    // Get the value of all sub-elements.
                    String lesson = elem.getElementsByTagName("Title").item(0).getChildNodes().item(0).getNodeValue();

                    // Get the value of all sub-elements.
                    String professor = null;
                    if (elem.getElementsByTagName("Professor").item(0) != null) {
                        professor = elem.getElementsByTagName("Professor").item(0).getChildNodes().item(0).getNodeValue();
                    }

                    String day = elem.getElementsByTagName("Day").item(k).getChildNodes().item(0).getNodeValue();

                    if(comboDay.equals(day) || comboDay.equals("All Days")){
                        controller.setLecture(lesson, professor, day);
                    }
                }
            }
        }

        primaryStage.show();

    }

    public void LessonAdd(Stage primaryStage, Controller controller, File file) throws Exception {
        controller.table.getItems().clear();

        if (file != null) {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(file);

            Element root = document.getDocumentElement();
            Element event = document.createElement("Event");
            root.appendChild(event);

            Element lesson = document.createElement("Title");
            Element lecture = document.createElement("Lecture");
            Element day = document.createElement("Day");
            Element time = document.createElement("Time");
            Element professor = document.createElement("Professor");
            lecture.appendChild(day);
            lecture.appendChild(time);


            lesson.appendChild(document.createTextNode(controller.lessonField.getText()));
            day.appendChild(document.createTextNode(controller.dayField.getText()));
            time.appendChild(document.createTextNode(controller.timeField.getText()));
            professor.appendChild(document.createTextNode(controller.professorField.getText()));

            event.appendChild(lesson);
            event.appendChild(lecture);
            event.appendChild(professor);

            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(xmlAssigned.toString()));
            transformer.transform(domSource, streamResult);


            try{
                XMLParsing(primaryStage, controller, xmlAssigned, "All Days");
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("File Parsing Error");
                alert.setHeaderText("Something went wrong with the parsing..!");
                alert.showAndWait();
                ex.printStackTrace();
            }

        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("File Selection Error");
            alert.setHeaderText("Please select a valid XML file to add a Lecture!");
            alert.showAndWait();
        }
    }

    public class SimpleErrorHandler implements ErrorHandler {
        public void warning(SAXParseException e) throws SAXException {
            System.out.println(e.getMessage());
        }

        public void error(SAXParseException e) throws SAXException {
            System.out.println(e.getMessage());
        }

        public void fatalError(SAXParseException e) throws SAXException {
            System.out.println(e.getMessage());
        }
    }

    private static void configureFileChooser(final FileChooser fileChooser){
        fileChooser.setTitle("View Files");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home") + "/Desktop")
        );
    }


    public static void main(String[] args) {
        launch(args);
    }
}
