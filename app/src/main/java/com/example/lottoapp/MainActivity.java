package com.example.lottoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    // Views
    private TextView resultBall1, resultBall2, resultBall3, resultBall4, resultBall5, resultPoints;
    private EditText editBall_1, editBall_2, editBall_3, editBall_4, editBall_5;
    private Button playButton;
    private ImageView victoryImage;

    // Text values
    private String textBall_1, textBall_2, textBall_3, textBall_4, textBall_5, randomNumber1, randomNumber2, randomNumber3, randomNumber4, randomNumber5;
    private String[] allBallValues, randomNumbers;
    private int[] randomNumbersInt;

    // Toast messages props
    Toast fillEmptyFieldToast, wrongNumberToast;
    int duration = Toast.LENGTH_SHORT;

    // Context
    Context context;

    // Others
    LinearLayout resultContainer;

    // Media player
    MediaPlayer winSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewsById();
        context = getApplicationContext();
        winSound = null;

        playButton.setOnClickListener(v -> {
            if (playButton.getText().toString().contentEquals("Sortear")) playTheGame();
            else reset();
        });

        victoryImage.setOnClickListener(v -> toggleWinImgVisibilityAndSound());
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopWinSound();
    }

    private void showJackpot() {
        toggleWinImgVisibilityAndSound();
    }

    private void playWinSound() {
        if (winSound == null) {
            winSound = MediaPlayer.create(context, R.raw.bingo);
            winSound.setOnCompletionListener(mp -> stopWinSound());
        }
        winSound.start();
    }

    private void stopWinSound() {
        winSound.release();
        winSound = null;
    }

    private void toggleWinImgVisibilityAndSound() {
        if (victoryImage.isShown()) victoryImage.setVisibility(View.GONE);
        else {
            victoryImage.setVisibility(View.VISIBLE);
            playWinSound();
        }
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
        resultPoints.setText("");
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

        if (points == 1) showJackpot();

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
        if (checkValidations()) {
            generateNumbers();
            if (checkGeneratedNumbersDuplicates()) {
                orderAndSetGeneratedNumbers();
                playButton.setText(R.string.reset_button);
                toggleResultsVisibility();
            } else checkAll();
        } else Toast.makeText(context, R.string.ask_to_fill_correctly_alert, duration).show();
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
                    wrongNumberToast = Toast.makeText(context, R.string.duplicate_numbers_alert, duration);
                    wrongNumberToast.show();

                    return false;
                }
            }
        }

        return true;
    }

    private boolean validateInput(EditText v, String s) {
        if (TextUtils.isEmpty(v.getText())) {
            fillEmptyFieldToast = Toast.makeText(context, R.string.fill_empty_field_alert, duration);
            fillEmptyFieldToast.show();
            v.requestFocus();

            return false;
        }

        if (Integer.parseInt(s) < 1 || Integer.parseInt(s) > 50) {
            wrongNumberToast = Toast.makeText(context, R.string.numbers_out_of_range_alert, duration);
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

    private int generateRandomNumber() {
        return (new Random().nextInt(50) + 1);
    }

    private int[] generateNumbers() {
        int rnd1, rnd2, rnd3, rnd4, rnd5;

        rnd1 = generateRandomNumber();
        rnd2 = generateRandomNumber();
        rnd3 = generateRandomNumber();
        rnd4 = generateRandomNumber();
        rnd5 = generateRandomNumber();

        randomNumbersInt = new int[] {rnd1, rnd2, rnd3, rnd4, rnd5};

        Arrays.sort(randomNumbersInt);

        return randomNumbersInt;
    }

    private void orderAndSetGeneratedNumbers() {
        int aux1, aux2, aux3, aux4, aux5;

        aux1 = randomNumbersInt[0];
        aux2 = randomNumbersInt[1];
        aux3 = randomNumbersInt[2];
        aux4 = randomNumbersInt[3];
        aux5 = randomNumbersInt[4];

        resultBall1.setText(String.valueOf(aux1));
        resultBall2.setText(String.valueOf(aux2));
        resultBall3.setText(String.valueOf(aux3));
        resultBall4.setText(String.valueOf(aux4));
        resultBall5.setText(String.valueOf(aux5));
    }

    private boolean checkGeneratedNumbersDuplicates() {
        for (int i = 0; i < randomNumbersInt.length; i++) {
            for (int j = i + 1 ; j < randomNumbersInt.length; j++) {
                if (randomNumbersInt[i] == randomNumbersInt[j]) {
                    return false;
                }
            }
        }

        return true;
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

        victoryImage = findViewById(R.id.imageView);
    }
}