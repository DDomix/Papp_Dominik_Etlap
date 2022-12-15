package hu.petrik.etlap;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class EtlapController {

    @FXML
    private TableColumn<Etlap, Integer> armezo;
    @FXML
    private Button felvetelgomb;
    @FXML
    private Spinner<Integer> forintSpinner;
    @FXML
    private Button torlesgomb;
    @FXML
    private Button szazalekemelesGomb;
    @FXML
    private Spinner<Integer> szazalekSpinner;
    @FXML
    private TableColumn<Etlap, String> nevmezo;
    @FXML
    private Button forintemelesGomb;
    @FXML
    private TableColumn<Etlap, String > kategoriamezo;
    @FXML
    private TableView<Etlap> etlapTable;
    private EtlapDB db;


    @FXML
    private void initialize() {
        nevmezo.setCellValueFactory(new PropertyValueFactory<>("nev"));
        armezo.setCellValueFactory(new PropertyValueFactory<>("ar"));
        kategoriamezo.setCellValueFactory(new PropertyValueFactory<>("kategoria"));
        forintSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 5000));
        szazalekSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100));
        try {
            db = new EtlapDB();
            etlapTable.getItems().addAll(db.readEtlap());
        } catch (SQLException e) {
            Platform.runLater(() -> {
                sqlAlert(e);
                Platform.exit();
            });
        }
    }

    private void sqlAlert(SQLException e) {
        alert(Alert.AlertType.ERROR,
                "Hiba történt az adatbázis kapcsolat kialakításakor",
                e.getMessage());
    }

    @FXML
    public void szazalekemelesClick(ActionEvent actionEvent) throws SQLException {
        int selectedIndex = etlapTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex==-1){
            Optional<ButtonType> optionalButtonType = alert(Alert.AlertType.CONFIRMATION,
                    "Biztos, hogy emelni szeretné az összes ételt?", "");
            if (optionalButtonType.isEmpty() ||
                    (!optionalButtonType.get().equals(ButtonType.OK) &&
                            !optionalButtonType.get().equals(ButtonType.YES))) {
            }else{
                for (int i = 0; i < etlapTable.getItems().size(); i++) {
                    Etlap actual = etlapTable.getItems().get(i);
                    int alapar = actual.getAr();
                    SpinnerValueFactory<Integer> emeles = szazalekSpinner.getValueFactory();
                    double vegar = alapar*((double)emeles.getValue()/100);
                    actual.setAr((int)Math.round(vegar) + alapar);
                    db.updateEtlap(actual);
                    etlapTable.setItems(FXCollections.observableList(db.readEtlap()));
                }

            }
        }else{
            Optional<ButtonType> optionalButtonType = alert(Alert.AlertType.CONFIRMATION,
                    "Biztos, hogy emelni szeretné az választott ételt?", "");
            if (optionalButtonType.isEmpty() ||
                    (!optionalButtonType.get().equals(ButtonType.OK) &&
                            !optionalButtonType.get().equals(ButtonType.YES))) {
            }else{
                int alapar = getSelectedEtlap().getAr();
                SpinnerValueFactory<Integer> emeles = szazalekSpinner.getValueFactory();
                double vegar = alapar*((double)emeles.getValue()/100);
                getSelectedEtlap().setAr((int)Math.round(vegar) + alapar);
                db.updateEtlap(getSelectedEtlap());
                etlapTable.setItems(FXCollections.observableList(db.readEtlap()));
            }
        }
    }

    @FXML
    public void torlesClick(ActionEvent actionEvent) {
        Etlap selected = getSelectedEtlap();
        if (selected == null) return;

        Optional<ButtonType> optionalButtonType = alert(Alert.AlertType.CONFIRMATION,
                "Biztos, hogy törölni szeretné a választott ételt?", "");
        if (optionalButtonType.isEmpty() ||
                (!optionalButtonType.get().equals(ButtonType.OK) &&
                        !optionalButtonType.get().equals(ButtonType.YES))) {
            return;
        }

        try {
            if (db.deleteEtlap(selected.getId())) {
                alert(Alert.AlertType.WARNING, "Sikeres törlés", "");
            } else {
                alert(Alert.AlertType.WARNING, "Sikertelen törlés", "");
            }
            etlapTable.setItems(FXCollections.observableList(db.readEtlap()));
        } catch (SQLException e) {
            sqlAlert(e);
        }
    }
    private Etlap getSelectedEtlap() {
        int selectedIndex = etlapTable.getSelectionModel().getSelectedIndex();
       // if (selectedIndex == -1) {
       //     alert(Alert.AlertType.WARNING,
       //             "Előbb válasszon ki ételt a táblázatból", "");
       //     return null;
       // }
        return etlapTable.getSelectionModel().getSelectedItem();
    }

    private Optional<ButtonType> alert(Alert.AlertType alertType, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        return alert.showAndWait();
    }

    @FXML
    public void felvetelClick(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("uj-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 640, 480);
            Stage stage = new Stage();
            stage.setTitle("Create People");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void forintemelesClick(ActionEvent actionEvent) throws SQLException {
        int selectedIndex = etlapTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex==-1){
            Optional<ButtonType> optionalButtonType = alert(Alert.AlertType.CONFIRMATION,
                    "Biztos, hogy emelni szeretné az összes ételt?", "");
            if (optionalButtonType.isEmpty() ||
                    (!optionalButtonType.get().equals(ButtonType.OK) &&
                            !optionalButtonType.get().equals(ButtonType.YES))) {
            }else{
                for (int i = 0; i < etlapTable.getItems().size(); i++) {
                    Etlap actual = etlapTable.getItems().get(i);
                    int alapar = actual.getAr();
                    Integer emeles = forintSpinner.getValueFactory().getValue();
                    int vegosszeg=alapar+=emeles;
                    actual.setAr(vegosszeg);
                    db.updateEtlap(actual);
                    etlapTable.setItems(FXCollections.observableList(db.readEtlap()));
                }

        }
        }else{
            Optional<ButtonType> optionalButtonType = alert(Alert.AlertType.CONFIRMATION,
                    "Biztos, hogy emelni szeretné az választott ételt?", "");
            if (optionalButtonType.isEmpty() ||
                    (!optionalButtonType.get().equals(ButtonType.OK) &&
                            !optionalButtonType.get().equals(ButtonType.YES))) {
            }else{
                int alapar=getSelectedEtlap().getAr();
                Integer emeles = forintSpinner.getValueFactory().getValue();
                int vegosszeg=alapar+=emeles;
                getSelectedEtlap().setAr(vegosszeg);
                db.updateEtlap(getSelectedEtlap());
                etlapTable.setItems(FXCollections.observableList(db.readEtlap()));
            }
        }


    }
}