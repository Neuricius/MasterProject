package com.neuricius.masterproject.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;

import com.neuricius.masterproject.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import static com.neuricius.masterproject.util.UtilTools.AUTHOR_NAME;

public class AboutDialog extends AlertDialog.Builder {

    public AboutDialog(Context context) {
        super(context);

        setTitle(R.string.drawer_about);

        String dialogText = context.getResources().getString(R.string.author) + ": "+AUTHOR_NAME + " \n" +
                            " \n "+
                            context.getResources().getString(R.string.powered_by) + ": ";

        setMessage(dialogText);

        ImageView ivLogoImage = new ImageView(context);
        ivLogoImage.setPadding(150,0,150,0);
        Picasso.
                with(context).
                load(Uri.parse("https://www.themoviedb.org/assets/2/v4/logos/312x276-primary-green-74212f6247252a023be0f02a5a45794925c3689117da9d20ffe47742a665c518.png")).
                placeholder(R.drawable.ic_pic_placeholder_foreground).
                error(R.drawable.ic_pic_error_foreground).
                fit().
                into(ivLogoImage);

        setView(ivLogoImage);
        setCancelable(false);

        setPositiveButton(R.string.dialog_about_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        setNegativeButton(R.string.dialog_about_no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
    }


    public AlertDialog prepareDialog(){
        AlertDialog dialog = create();
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

}
