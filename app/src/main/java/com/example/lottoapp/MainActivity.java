package com.example.lottoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    // Views
    private TextView resultBall1, resultBall2, resultBall3, resultBall4, resultBall5, resultPoints;
    private EditText editBall_1, editBall_2, editBall_3, editBall_4, editBall_5;
    private Button playButton;

    // Text values
    private String textBall_1, textBall_2, textBall_3, textBall_4, textBall_5, randomNumber1, randomNumber2, randomNumber3, randomNumber4, randomNumber5;
    private String[] allBallValues, randomNumbers;

    // Toast messages props
    Toast fillCorrectlyToast, fillEmptyFieldToast, wrongNumberToast;
    int duration = Toast.LENGTH_SHORT;

    // Context
    Context context;

    // Others
    LinearLayout resultContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewsById();
        context = getApplicationContext();

        playButton.setOnClickListener(v -> {
            if (playButton.getText().toString().contentEquals("Sortear")) playTheGame();
            else reset();
        });
    }

    private void playTheGame() {
        getTextFromUserBalls();
        checkAll();
        getTextFromUserBalls();
        getTextFromGeneratedBalls();
        resultPoints.setText(String.valueOf(getResultPoints()));
    }

    private void toggleResultsVisibility() {
        if (resultContainer.isShown()) resultContainer.setVisibility(View.INVISIBLE);
        else resultContainer.setVisibility(View.VISIBLE);
    }

    private void reset() {
        setTextColorToBlack();
        resetUserInputs();
        resetGeneratedNumbers();
        toggleResultsVisibility();
        playButton.setText(R.string.play_button);
    }

    private void resetUserInputs() {
        resultBall1.setText("");
        resultBall2.setText("");
        resultBall3.setText("");
        resultBall4.setText("");
        resultBall5.setText("");
        resultPoints.setText("0");
    }

    private void resetGeneratedNumbers() {
        editBall_1.setText(null);
        editBall_2.setText(null);
        editBall_3.setText(null);
        editBall_4.setText(null);
        editBall_5.setText(null);
    }

    private void setTextColorToBlack() {
        resultBall1.setTextColor(ContextCompat.getColor(context, R.color.black));
        resultBall2.setTextColor(ContextCompat.getColor(context, R.color.black));
        resultBall3.setTextColor(ContextCompat.getColor(context, R.color.black));
        resultBall4.setTextColor(ContextCompat.getColor(context, R.color.black));
        resultBall5.setTextColor(ContextCompat.getColor(context, R.color.black));
    }

    private int getResultPoints() {
        int points = 0;

        for (String allBallValue : allBallValues) {
            for (int j = 0; j < randomNumbers.length; j++) {
                if (allBallValue.equals(randomNumbers[j])) {
                    points++;
                    changeNumbersColor(j + 1);
                }
            }
        }

        return points;
    }

    private void changeNumbersColor(int arrayPosition) {
        if (arrayPosition == 1) setTextColorToOrange(resultBall1);
        if (arrayPosition == 2) setTextColorToOrange(resultBall2);
        if (arrayPosition == 3) setTextColorToOrange(resultBall3);
        if (arrayPosition == 4) setTextColorToOrange(resultBall4);
        if (arrayPosition == 5) setTextColorToOrange(resultBall5);
    }

    private void setTextColorToOrange(TextView txt) {
        txt.setTextColor(ContextCompat.getColor(context, R.color.dark_orange));
    }

    private void checkAll() {
        boolean isNumbersOk = checkValidations();

        if (isNumbersOk) {
            generateNumbers();
            playButton.setText(R.string.reset_button);
            toggleResultsVisibility();
        } else {
            fillCorrectlyToast = Toast.makeText(context, "Preencha corretamente", duration);
            fillCorrectlyToast.show();
        }
    }

    private boolean checkValidations() {
        return validateInput(editBall_1, textBall_1) &&
                validateInput(editBall_2, textBall_2) &&
                validateInput(editBall_3, textBall_3) &&
                validateInput(editBall_4, textBall_4) &&
                validateInput(editBall_5, textBall_5) &&
                checkDuplicates();
    }

    private boolean checkDuplicates() {
        for (int i = 0; i < allBallValues.length; i++) {
            for (int j = i + 1 ; j < allBallValues.length; j++) {
                if (allBallValues[i].equals(allBallValues[j])) {
                    wrongNumberToast = Toast.makeText(context, "Números duplicados", duration);
                    wrongNumberToast.show();

                    return false;
                }
            }
        }

        return true;
    }

    private boolean validateInput(EditText v, String s) {
        if (TextUtils.isEmpty(v.getText())) {
            fillEmptyFieldToast = Toast.makeText(context, "Preencha o campo vazio", duration);
            fillEmptyFieldToast.show();
            v.requestFocus();

            return false;
        }

        if (Integer.parseInt(s) < 1 || Integer.parseInt(s) > 50) {
            wrongNumberToast = Toast.makeText(context, "Digite valores entre 1 e 50", duration);
            wrongNumberToast.show();
            v.requestFocus();

            return false;
        }

        return true;
    }

    private void getTextFromGeneratedBalls() {
        randomNumber1 = String.valueOf(resultBall1.getText());
        randomNumber2 = String.valueOf(resultBall2.getText());
        randomNumber3 = String.valueOf(resultBall3.getText());
        randomNumber4 = String.valueOf(resultBall4.getText());
        randomNumber5 = String.valueOf(resultBall5.getText());

        randomNumbers = new String[] {randomNumber1, randomNumber2, randomNumber3, randomNumber4, randomNumber5};
    }

    private void getTextFromUserBalls() {
        textBall_1 = String.valueOf(editBall_1.getText());
        textBall_2 = String.valueOf(editBall_2.getText());
        textBall_3 = String.valueOf(editBall_3.getText());
        textBall_4 = String.valueOf(editBall_4.getText());
        textBall_5 = String.valueOf(editBall_5.getText());

        allBallValues = new String[] {textBall_1, textBall_2, textBall_3, textBall_4, textBall_5};
    }

    private void generateRandomNumber(TextView v) {
        int x = (new Random().nextInt(50) + 1);
        v.setText(String.valueOf(x));
    }

    private void generateNumbers() {
        generateRandomNumber(resultBall1);
        generateRandomNumber(resultBall2);
        generateRandomNumber(resultBall3);
        generateRandomNumber(resultBall4);
        generateRandomNumber(resultBall5);
    }

    private void findViewsById() {
        editBall_1 = findViewById(R.id.ball_1_edit);
        editBall_2 = findViewById(R.id.ball_2_edit);
        editBall_3 = findViewById(R.id.ball_3_edit);
        editBall_4 = findViewById(R.id.ball_4_edit);
        editBall_5 = findViewById(R.id.ball_5_edit);

        playButton = findViewById(R.id.play_button);

        resultBall1 = findViewById(R.id.result1);
        resultBall2 = findViewById(R.id.result2);
        resultBall3 = findViewById(R.id.result3);
        resultBall4 = findViewById(R.id.result4);
        resultBall5 = findViewById(R.id.result5);

        resultContainer = findViewById(R.id.resultContainer);
        resultPoints = findViewById(R.id.points);
    }
}