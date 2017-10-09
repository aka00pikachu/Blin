package com.scottrealapps.calculater.util;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Information about one player's score in the game.
 */
public class HighScore {
    private static final String SCORE_FILE_NAME = "scores.txt";

    private String name;
    private int score;
    private int speed;
    private Date date;

    public HighScore(String name, int score, int speed) {
        this.name = name;
        this.score = score;
        this.speed = speed;
        date = new Date();
    }

    public HighScore(String name, int score, int speed, Date date) {
        this.name = name;
        this.score = score;
        this.speed = speed;
        this.date = date;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }
    public void setScore(int score) {
        this.score = score;
    }

    public int getSpeed() {
        return speed;
    }
    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }

    public static void writeScores(Context context, List<HighScore> scores) {
        PrintWriter os;
        try {
            os = new PrintWriter(context.openFileOutput(SCORE_FILE_NAME, Context.MODE_PRIVATE));
            os.println("#  These are high scores for Elee's tile thing");
            for (HighScore score : scores) {
                os.println(score.getName() + "\t" + score.getScore() +
                        "\t" + score.getSpeed() + "\t" + score.getDate().getTime());
            }
            os.close();
        } catch (Exception ex) {
            Log.e("ELEE", "Failed to write scores!", ex);
        }
    }

    public static List<HighScore> readScores(Context context) {
        ArrayList<HighScore> scores = new ArrayList<HighScore>(6);
        BufferedReader is;
        try {
            is = new BufferedReader(new InputStreamReader(context.openFileInput(SCORE_FILE_NAME)));
            String line;
            while ((line = is.readLine()) != null) {
                if (line.startsWith("#")) continue;
                String[] bits = line.split("\\t");
                String name = bits[0];
                int score = Integer.parseInt(bits[1]);
                int speed = Integer.parseInt(bits[2]);
                long date = Long.parseLong(bits[3]);
                scores.add(new HighScore(name, score, speed, new Date(date)));
            }
        } catch (FileNotFoundException fnfe) {
            //  This is normal, the first time!
        } catch (IOException ioe) {
            Log.e("ELEE", "Failed to read scores!", ioe);
        }
        return scores;
    }
}
