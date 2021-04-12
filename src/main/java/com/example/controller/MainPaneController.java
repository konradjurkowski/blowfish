package com.example.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.example.fileReader.TxtReader;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class MainPaneController {
    private SecretKey key;
    FileChooser fc = new FileChooser();

    @FXML
    private TextArea mainTextArea;
    @FXML
    private Button encryptButton;

    @FXML
    private Button decryptButton;

    @FXML
    private Button clearButton;

    @FXML
    private MenuItem fileMenuItem;

    @FXML
    private MenuItem saveMenuItem;

    @FXML
    private MenuItem closeMenuItem;

    @FXML
    private MenuItem aboutMenuItem;

    public void initialize() {
        setSecretKey();
        configureMenu();
        mainTextArea.setWrapText(true);

        encryptButton.setOnAction(actionEvent -> {
            try {
                List<String> str = getStrings();
                for (String s : str) {
                    if(!s.isEmpty()) {
                        s = encrypt(s);
                        mainTextArea.appendText(s + " ");
                    } else {
                        error("Błąd", "Nie możesz zaszyfrować pustego pola!", "Spróbuj ponownie!");
                    }
                }
            } catch (NullPointerException e) {
                error("Błąd", "Nie możesz zaszyfrować pustego pola!", "Spróbuj ponownie!");
            } catch (Exception e) {
                error("Błąd", "Błąd podczas szyfrowania!", "Spróbuj ponownie!");
            }
        });

        decryptButton.setOnAction(actionEvent -> {
            try {
                List<String> str = getStrings();
                for (String s : str) {
                    if(!s.isEmpty()) {
                        s = decrypt(s);
                        mainTextArea.appendText(s + " ");
                    } else {
                        error("Błąd", "Nie możesz odszyfrować pustego pola!", "Spróbuj ponownie!");
                    }
                }
            } catch (NullPointerException e) {
                error("Błąd", "Nie możesz odszyfrować pustego pola!", "Spróbuj ponownie!");
            } catch (Exception e) {
                error("Błąd", "Błąd podczas deszyfrowania!", "Spróbuj ponownie!");
            }
        });

        clearButton.setOnAction(actionEvent -> mainTextArea.clear());
    }

    private List<String> getStrings() {
        String text = mainTextArea.getText();
        mainTextArea.clear();
        String[] data = text.split(" ");
        return new ArrayList<>(Arrays.asList(data));
    }

    public String encrypt(String text) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("Blowfish");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(text.getBytes());
        return new String(Base64.getEncoder().encode(encrypted));
    }

    public void error (String typeOfAlert, String text, String info) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(typeOfAlert);
        alert.setHeaderText(text);
        alert.setContentText(info);
        alert.showAndWait().ifPresent(rs -> { });
    }

    public String decrypt(String text) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("Blowfish");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(text));
        return new String(decrypted);
    }

    private void setSecretKey() {
        String keyString = "12345";
        byte[] KeyData = keyString.getBytes();
        key = new SecretKeySpec(KeyData, "Blowfish");
    }

    private void configureMenu() {
        closeMenuItem.setOnAction(x -> Platform.exit());

        aboutMenuItem.setOnAction(new EventHandler<>() {
            @Override
            public void handle(ActionEvent arg0) {
                try {
                    Parent parent= FXMLLoader.load(getClass().getResource("/fxml/aboutPane.fxml"));
                    Scene scene = new Scene(parent);
                    Stage stage = new Stage();
                    stage.setTitle("Blowfish v1.0 - about");
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace(); //ignore
                }
            }
        });

        fileMenuItem.setOnAction(event -> {
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("txt", "*.txt"));
            try {
                File file = fc.showOpenDialog(new Stage());
                List<String> strings = TxtReader.createTxtFile(file);
                for (String s : strings) {
                    mainTextArea.appendText(s + " ");
                }
                error("Potwierdzenie", "Załadowano plik " + file.getName(), "");
            } catch (Exception e) {
                error("Błąd", "Nie można otworzyć pliku", "");
            }
        });

        saveMenuItem.setOnAction(event -> {
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("txt", "*.txt"));
            File file = fc.showSaveDialog(new Stage());
            if(file != null) {
                try {
                    TxtReader.saveTxtFile(mainTextArea.getText(), file);
                    error("Potwierdzenie", "Zapisano plik " + file.getName(), "");
                } catch (IOException e) {
                    error("Błąd", "Nie można zapisać pliku ", "");
                }
            }
        });
    }
}
