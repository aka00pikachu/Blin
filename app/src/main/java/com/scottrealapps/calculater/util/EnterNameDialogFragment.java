package com.scottrealapps.calculater.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 */
public class EnterNameDialogFragment extends DialogFragment {

    /**
     * If the Activity this is attached to implements this, this method will be
     * called when someone enters a name.
     */
    public interface NameEnteredListener {
        public void nameEntered(String name);
    }

    // Use this instance of the interface to deliver action events
    NameEnteredListener listener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof NameEnteredListener) {
            listener = (NameEnteredListener)activity;
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//XXX fix this
        builder.setMessage("Are you Elee?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.nameEntered("EPB");
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.nameEntered("RDB");
                    }
                });
        return builder.create();
    }
}
