package com.neuricius.masterproject.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.neuricius.masterproject.R;

public class CustomDialog {

        public CustomDialog(Context context, String title, String message) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                Activity activity = (Activity) context;
                ViewGroup viewGroup = activity.findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(context).inflate(R.layout.customdialog, viewGroup, false);
                builder.setView(dialogView);

                TextView tvTitle = dialogView.findViewById(R.id.tv_dialog_title);
                tvTitle.setText(title);

                TextView tvMsg = dialogView.findViewById(R.id.tv_dialog_msg);
                tvMsg.setText(message);

                final AlertDialog alertDialog = builder.create();
                alertDialog.show();
                dialogView.findViewById(R.id.b_dialog_ok).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                alertDialog.dismiss();
                        }
                });

        }

}
