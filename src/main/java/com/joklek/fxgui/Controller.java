package com.joklek.fxgui;

import com.joklek.Operator;
import com.joklek.WorkingMode;
import com.joklek.communicator.CodedCommunicator;
import com.joklek.communicator.Communicator;
import com.joklek.communicator.ReedMullerCodeGenerator;
import com.joklek.communicator.UncodedCommunicator;
import com.joklek.communicator.elements.Channel;
import com.joklek.communicator.elements.Decoder;
import com.joklek.communicator.elements.Encoder;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class Controller {

    @FXML
    Button selectFileButton;
    @FXML
    Button sendButton;
    @FXML
    TextField inputField;
    @FXML
    TextField mField;
    @FXML
    TextField errorField;

    @FXML
    TextArea codedTextResultArea;
    @FXML
    TextArea uncodedTextResultArea;

    @FXML
    RadioButton imageRadio;
    @FXML
    RadioButton textRadio;
    @FXML
    RadioButton binaryRadio;

    @FXML
    ImageView originalImage;
    @FXML
    ImageView codedImage;
    @FXML
    ImageView uncodedImage;
    @FXML
    VBox selectionGrid;

    private Operator operator;
    private Channel channel;
    private Encoder encoder;
    private Decoder decoder;
    private Communicator uncodedCommunicator;

    final FileChooser fileChooser = new FileChooser();

    {
        operator = new Operator();
        channel = new Channel();
        ReedMullerCodeGenerator generator = new ReedMullerCodeGenerator();
        encoder = new Encoder(generator);
        decoder = new Decoder();
        uncodedCommunicator = new UncodedCommunicator(channel);
    }

    private WorkingMode setWorkingMode() {
        if(imageRadio.isSelected()) {
            return WorkingMode.BITMAP;
        }
        else if(textRadio.isSelected()) {
            return WorkingMode.TEXT;
        }
        else if(binaryRadio.isSelected()) {
            return WorkingMode.BINARY;
        }
        throw new IllegalStateException("An unknown state has been reached. Bad news");
    }

    public void selectFileButtonPress() {
        Stage stage = (Stage) selectionGrid.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            inputField.setText(file.getAbsolutePath());
        }
    }

    public void sendButtonClick() throws IOException {
        if (inputField.getText() == null || inputField.getText().isEmpty()) {
            return;
        }

        WorkingMode mode = setWorkingMode();
        int parsedM = Integer.parseInt(mField.getText());
        double parsedErrorRate = Double.parseDouble(errorField.getText());

        String payload;

        if (mode == WorkingMode.BITMAP) {
            File file = new File(inputField.getText());
            originalImage.setImage(new Image(file.toURI().toString()));
            payload = file.getCanonicalPath();

        } else {
            payload = inputField.getText();
        }

        Communicator codedCommunicator = new CodedCommunicator(channel, encoder, decoder, parsedM);
        try {
            byte[] result = operator.sendWithCommunicator(mode, payload, codedCommunicator, parsedErrorRate/100);
            byte[] uncodedResult = operator.sendWithCommunicator(mode, payload, uncodedCommunicator, parsedErrorRate/100);

            if(mode.equals(WorkingMode.BITMAP)) {
                codedImage.setImage(new Image(new ByteArrayInputStream(result)));
                uncodedImage.setImage(new Image(new ByteArrayInputStream(uncodedResult)));
            }
            else {
                codedTextResultArea.setText(new String(result));
                uncodedTextResultArea.setText(new String(uncodedResult));
            }

        } catch (IOException e1) {
        }
    }

    public void imageRadioClick() {
        if(!imageRadio.isSelected()) {
            return;
        }
        selectFileButton.setVisible(true);
        codedImage.setVisible(true);
        uncodedImage.setVisible(true);
        codedTextResultArea.setVisible(false);
        uncodedTextResultArea.setVisible(false);
    }

    public void textRadioClick() {
        nonImageButtonClick(textRadio);
    }

    public void binaryRadioClick() {
        nonImageButtonClick(binaryRadio);
    }

    public void nonImageButtonClick(RadioButton button) {
        if(!button.isSelected()) {
            return;
        }
        selectFileButton.setVisible(false);
        codedImage.setVisible(false);
        uncodedImage.setVisible(false);
        codedTextResultArea.setVisible(true);
        uncodedTextResultArea.setVisible(true);
    }
}
