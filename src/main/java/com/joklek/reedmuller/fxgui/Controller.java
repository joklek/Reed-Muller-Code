package com.joklek.reedmuller.fxgui;

import com.joklek.reedmuller.Operator;
import com.joklek.reedmuller.WorkingMode;
import com.joklek.reedmuller.communicator.CodedCommunicator;
import com.joklek.reedmuller.communicator.Communicator;
import com.joklek.reedmuller.communicator.ReedMullerCodeGenerator;
import com.joklek.reedmuller.communicator.UncodedCommunicator;
import com.joklek.reedmuller.communicator.elements.Channel;
import com.joklek.reedmuller.communicator.elements.Decoder;
import com.joklek.reedmuller.communicator.elements.Encoder;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @FXML
    GridPane binaryPane;
    @FXML
    TextField codedBinary;
    @FXML
    TextField sentThroughChannel;
    @FXML
    Button decodeButton;
    @FXML
    Label numberOfErrors;

    private final Operator operator;
    private final Channel channel;
    private final Encoder encoder;
    private final Decoder decoder;
    private final Communicator uncodedCommunicator;
    private final FileChooser fileChooser;


    public Controller() {
        operator = new Operator();
        channel = new Channel();
        ReedMullerCodeGenerator generator = new ReedMullerCodeGenerator();
        encoder = new Encoder(generator);
        decoder = new Decoder();
        uncodedCommunicator = new UncodedCommunicator(channel);
        fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("BMP files (*.bmp)", "*.bmp");
        fileChooser.getExtensionFilters().add(extFilter);
    }

    private WorkingMode setWorkingMode() {
        if (imageRadio.isSelected()) {
            return WorkingMode.BITMAP;
        } else if (textRadio.isSelected()) {
            return WorkingMode.TEXT;
        } else if (binaryRadio.isSelected()) {
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
        LocalDateTime startTime = LocalDateTime.now();

        WorkingMode mode = setWorkingMode();
        int parsedM = Integer.parseInt(mField.getText());
        double parsedErrorRate = Double.parseDouble(errorField.getText()) / 100;

        String payload;

        if (mode == WorkingMode.BITMAP) {
            File file = new File(inputField.getText());
            originalImage.setImage(new Image(file.toURI().toString()));
            payload = file.getCanonicalPath();

        } else {
            payload = inputField.getText();
        }

        if(mode == WorkingMode.BINARY) {
            if(inputField.getText().length() != parsedM+1) {
                Alert alert = new Alert(Alert.AlertType.ERROR, String.format("Vector length should be '%d', but is '%d'", parsedM+1, inputField.getText().length()), ButtonType.OK);
                alert.showAndWait();
                return;
            }
            doBinaryTransfer(parsedM, parsedErrorRate, inputField.getText());
            return;
        }
        else {
            binaryPane.setVisible(false);
        }

        Communicator codedCommunicator = new CodedCommunicator(channel, encoder, decoder, parsedM);
        try {
            byte[] result = operator.sendWithCommunicator(mode, payload, codedCommunicator, parsedErrorRate);
            byte[] uncodedResult = operator.sendWithCommunicator(mode, payload, uncodedCommunicator, parsedErrorRate);

            if (mode.equals(WorkingMode.BITMAP)) {
                codedImage.setImage(new Image(new ByteArrayInputStream(result)));
                uncodedImage.setImage(new Image(new ByteArrayInputStream(uncodedResult)));
            } else {
                codedTextResultArea.setText(new String(result));
                uncodedTextResultArea.setText(new String(uncodedResult));
            }

        } catch (IOException e1) {
        }
        LocalDateTime endTime = LocalDateTime.now();
        Duration timeSpent = Duration.between(startTime, endTime);
        System.out.println(timeSpent);
    }

    // TODO: This is a quick hack, cobbled together without any thought, sorry
    private void doBinaryTransfer(int m, double errorRate, String text) {
        boolean[] vector = new boolean[text.length()];
        for(int i = 0; i < text.length(); i++) {
            vector[i] = text.charAt(i) == '1';
        }
        boolean[] encoded = encoder.encode(vector, m);
        byte[] encodedStringBytes = new byte[encoded.length];

        boolean[] sentThroughChannelString = channel.sendThroughChannel(encoded, errorRate);
        byte[] sentString = new byte[sentThroughChannelString.length];


        for(int i = 0; i < encoded.length; i++) {
            encodedStringBytes[i] = encoded[i] ? (byte)'1' : (byte)'0';
        }
        for(int i = 0; i < sentThroughChannelString.length; i++) {
            sentString[i] = sentThroughChannelString[i] ? (byte)'1' : (byte)'0';
        }

        String encodedString = new String(encodedStringBytes);
        codedBinary.setText(encodedString);
        String encodedStringWithErrors = new String(sentString);
        sentThroughChannel.setText(encodedStringWithErrors);

        List<Integer> errorIndexes = getErrorPositions(encodedString, encodedStringWithErrors);
        if(errorIndexes.isEmpty()) {
            numberOfErrors.setText("No errors occurred");
        }
        else {
            numberOfErrors.setText(String.format("%d errors occurred in positions: %s", errorIndexes.size(), StringUtils.join(errorIndexes, ',')));
        }
    }

    private List<Integer> getErrorPositions(String s1, String s2) {
        List<Integer> errors = new ArrayList<>();
        int minLength = Math.min(s1.length(), s2.length());
        for(int i = 0; i< minLength; i++) {
            if(s1.charAt(i) != s2.charAt(i)) {
                errors.add(i+1); // positions start from 1, I know - sad
            }
        }
        return errors;
    }

    public void pressDecodeButton() throws IOException {
        // This is very bad, but it works
        String text = sentThroughChannel.getText();
        int m = Integer.parseInt(mField.getText());

        boolean[] vector = new boolean[text.length()];
        for(int i = 0; i < text.length(); i++) {
            vector[i] = text.charAt(i) == '1';
        }
        boolean[] decoded = decoder.decode(vector, m);
        byte[] decodedStringBytes = new byte[decoded.length];

        for(int i = 0; i < decoded.length; i++) {
            decodedStringBytes[i] = decoded[i] ? (byte)'1' : (byte)'0';
        }

        double parsedErrorRate = Double.parseDouble(errorField.getText()) / 100;
        byte[] uncodedStringBytes = operator.sendWithCommunicator(WorkingMode.BINARY, inputField.getText(), uncodedCommunicator, parsedErrorRate);
        codedTextResultArea.setText(new String(decodedStringBytes));
        uncodedTextResultArea.setText(new String(uncodedStringBytes));
    }

    public void imageRadioClick() {
        binaryPane.setVisible(false);
        selectFileButton.setVisible(true);
        codedImage.setVisible(true);
        uncodedImage.setVisible(true);
        codedTextResultArea.setVisible(false);
        uncodedTextResultArea.setVisible(false);
    }

    public void textRadioClick() {
        nonImageButtonClick(textRadio);
        binaryPane.setVisible(false);
    }

    public void binaryRadioClick() {
        nonImageButtonClick(binaryRadio);
        if(binaryRadio.isSelected()) {
            binaryPane.setVisible(true);
            return;
        }
    }

    public void nonImageButtonClick(RadioButton button) {
        if (!button.isSelected()) {
            return;
        }
        selectFileButton.setVisible(false);
        codedImage.setVisible(false);
        uncodedImage.setVisible(false);
        codedTextResultArea.setVisible(true);
        uncodedTextResultArea.setVisible(true);
    }
}
