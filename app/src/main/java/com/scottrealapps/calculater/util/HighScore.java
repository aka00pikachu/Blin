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

    //  either "tt<seconds>" for time tiles, or something else.
    private String gameID;
    private String name;
    private int score;
    private int speed;
    private Date date;

    public HighScore(String id, String name, int score, int speed) {
        this.gameID = id;
        this.name = name;
        this.score = score;
        this.speed = speed;
        date = new Date();
    }

    public HighScore(String id, String name, int score, int speed, Date date) {
        this.gameID = id;
        this.name = name;
        this.score = score;
        this.speed = speed;
        this.date = date;
    }

    public String getGameID() {
        return gameID;
    }
    public void setGameID(String gameID) {
        this.gameID = gameID;
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

    /**
     * Theoretically, what we're being passed is just the five-or-whatever
     * scores for this gameID.  Read all the scores; discard all the entries
     * with the new scores' gameID, add the new entries to the list, and write
     * the whole list.
     *
     * @param context
     * @param scores
     */
    public static void writeScores(Context context, List<HighScore> scores) {
        List<HighScore> allScores = readScores(context, null);
        //  Figure out the new scores' gameID, and remove all of those from
        //  allScores.
        String gameID = null;
        if (scores.size() > 0) {
            gameID = scores.get(0).getGameID();
        }
        if (gameID != null) {
            //  Run backwards through the list because we're deleting elements
            //  from it.
            for (int ii = allScores.size() - 1; ii >= 0; --ii) {
                if (allScores.get(ii).getGameID().equals(gameID)) {
                    allScores.remove(ii);
                }
            }
        }
        //  Now add the new scores to the list!
        allScores.addAll(scores);

        PrintWriter os;
        try {
            os = new PrintWriter(context.openFileOutput(SCORE_FILE_NAME, Context.MODE_PRIVATE));
            os.println("#  These are high scores for Elee's tile thing");
            for (HighScore score : allScores) {
                os.println(score.getGameID() + "\t" + score.getName() + "\t" + score.getScore() +
                        "\t" + score.getSpeed() + "\t" + score.getDate().getTime());
            }
            os.close();
        } catch (Exception ex) {
            Log.e("ELEE", "Failed to write scores!", ex);
        }
    }

    /**
     *
     * @param context
     * @param wantGameID null if you want all scores; non-null if you want only
     *                   the scores for the given game ID.
     * @return
     */
    public static List<HighScore> readScores(Context context, String wantGameID) {
        ArrayList<HighScore> scores = new ArrayList<HighScore>(6);
        BufferedReader is;
        try {
            is = new BufferedReader(new InputStreamReader(context.openFileInput(SCORE_FILE_NAME)));
            String line;
            while ((line = is.readLine()) != null) {
                if (line.startsWith("#")) continue;
                String[] bits = line.split("\\t");
                if (bits.length == 5) {
                    //  it's a new style of line starting with the game ID
                    String id = bits[0];
                    if ((wantGameID == null) || wantGameID.equals(id)) {
                        String name = bits[1];
                        int score = Integer.parseInt(bits[2]);
                        int speed = Integer.parseInt(bits[3]);
                        long date = Long.parseLong(bits[4]);
                        scores.add(new HighScore(id, name, score, speed, new Date(date)));
                    }
                } else {
                    //  it's an old-style line with no game ID.  Note that this
                    //  chunk can be removed as soon as saved-score-files are
                    //  updated on, uhh, both of the devices where this has been
                    //  installed.
                    if ((wantGameID == null) || wantGameID.equals("")) {
                        String name = bits[0];
                        int score = Integer.parseInt(bits[1]);
                        int speed = Integer.parseInt(bits[2]);
                        long date = Long.parseLong(bits[3]);
                        scores.add(new HighScore("", name, score, speed, new Date(date)));
                    }
                }
            }
        } catch (FileNotFoundException fnfe) {
            //  This is normal, the first time!
        } catch (IOException ioe) {
            Log.e("ELEE", "Failed to read scores!", ioe);
        }
        return scores;
    }
}
