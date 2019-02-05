package com.umik.gnomex909.aurra;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;

/**
 * Created by Gnomex on 20.12.2017.
 * Klasa zajmująca się oknami dialogowymi, które pojawiają się w chwili, gdy użytkownik pragnie zaktualizować lokalizację domową, bądź tablice
 */

public class AppDialog extends AppCompatDialogFragment {
    private static final String TAG = "AppDialog";
    public static final String DIALOG_ID = "id";
    public static final String DIALOG_MESSAGE = "message";
    public static final String DIALOG_POSITIVE_RID = "positive_rid";
    public static final String DIALOG_NEGATIVE_RID = "negative_rid";

    interface DialogEvents {
        void onPositiveDialogResult(int dialogId, Bundle args);
        void onNegativeDialogResult(int dialogId, Bundle args);
        void onDialogCanceled(int dialogId);
    }
    private DialogEvents mDialogEvents;

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach: Entering onAttach, acitivity is " + context.toString());
        super.onAttach(context);
        if(!(context instanceof DialogEvents)){
            throw new ClassCastException(context.toString() + "must implement AppDialog.DialogEvents interface");
        }
        mDialogEvents = (DialogEvents) context;
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach: starts");
        super.onDetach();

        mDialogEvents = null;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d(TAG, "onCreateDialog: starts");
        final Bundle arguments = getArguments();
        final int dialogId;
        String messageString;
        int positiveStringId;
        int negativeStringId;

        if(arguments!= null){
            dialogId = arguments.getInt(DIALOG_ID);
            messageString = arguments.getString(DIALOG_MESSAGE);
            positiveStringId = arguments.getInt(DIALOG_POSITIVE_RID);

            if(dialogId == 0 || messageString == null){
                throw new IllegalArgumentException("DIALOG_ID and/or DIALOG_MESSAGE not present in the bundle");
            }

            if(positiveStringId == 0){
                positiveStringId = R.string.ok;
            }
            negativeStringId = arguments.getInt(DIALOG_NEGATIVE_RID);
            if(negativeStringId== 0){
                negativeStringId = R.string.cancel;
            }

        }
        else {
            throw new IllegalArgumentException("Must pass DIALOG_ID and DIALOG_MESSAGE in the bundle");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(messageString).setPositiveButton(positiveStringId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if(mDialogEvents !=null)
                    mDialogEvents.onPositiveDialogResult(dialogId, arguments);
            }
        })
                .setNegativeButton(negativeStringId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        if(mDialogEvents !=null)
                            mDialogEvents.onNegativeDialogResult(dialogId, arguments);
                    }
                });

        return builder.create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        Log.d(TAG, "onCancel: called");
        if(mDialogEvents != null){
            int dialogId = getArguments().getInt(DIALOG_ID);
            mDialogEvents.onDialogCanceled(dialogId);
        }

    }
}
