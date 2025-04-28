package org.novalearn.controllers.user;

import javafx.fxml.FXML;
import org.novalearn.MainApp;

public class ChooseRegisterController {

    @FXML
    private void onStudentRegisterClicked() {
        try {
            MainApp.showStudentRegister();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onParentRegisterClicked() {
        try {
            MainApp.showParentRegister();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onBackClicked() {
        try {
            MainApp.showLogin();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}