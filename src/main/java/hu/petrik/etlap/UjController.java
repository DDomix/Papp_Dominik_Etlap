package hu.petrik.etlap;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;


public class UjController {
    @FXML
    private TextField addkategoriamezo;
    @FXML
    private TextField addnevmezo;
    @FXML
    private TextField addleirasmezo;
    @FXML
    private Button addHozzaadBGomb;
    @FXML
    private Spinner<Integer> addArSpinner;

    private EtlapDB db;

    @FXML
    private void initialize() throws SQLException {
        addArSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 5000));
        db = new EtlapDB();

    }
    public void addHozzaadBGombClick(ActionEvent actionEvent) throws RuntimeException {
        String nev=addnevmezo.getText().trim();
        String leiras=addleirasmezo.getText().trim();
        String kategoria=addkategoriamezo.getText().trim();
        int ar= addArSpinner.getValue();
        if (nev.isEmpty()){
            alert(Alert.AlertType.WARNING, "Név megadása kötelező","");
            return;
        }
        if (kategoria.isEmpty()){
            alert(Alert.AlertType.WARNING, "Kategória megadása kötelező","");
            return;
        }
        if (ar == 0 && ar>0){
            alert(Alert.AlertType.WARNING, "Ár megadása kötelező","");
            return;
        }
        Etlap etlap =new Etlap(nev,leiras,kategoria,ar);
        try {
            if (db.createEtlap(etlap)) {
                alert(Alert.AlertType.WARNING,"Sikeres felvétel","");
                resetform();
            } else {
                alert(Alert.AlertType.WARNING,"Sikertelen felvétel","");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private Optional<ButtonType> alert(Alert.AlertType alertType, String headerText, String contentText){
        Alert alert=new Alert(alertType);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        return alert.showAndWait();
    }
    private void resetform() {
        addnevmezo.setText("");
        addkategoriamezo.setText("");
        addleirasmezo.setText("");
        addArSpinner.getValueFactory().setValue(0);
    }


}

