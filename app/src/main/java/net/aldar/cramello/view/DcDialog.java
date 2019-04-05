package net.aldar.cramello.view;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.aldar.cramello.R;
import net.aldar.cramello.view.listener.OnClickRetryBtn;

import static net.aldar.cramello.App.mMontserratRegular;

public class DcDialog {

    private Context mContext;
    private OnClickRetryBtn mOnClickRetryBtn;

    public DcDialog(Context context) {
        mContext = context;
        onCreateDialog();
    }

    private void onCreateDialog() {

        AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(mContext);
        final AlertDialog mAlertDialog;
        View mDialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_no_internet_connection, null);
        mAlertDialogBuilder.setView(mDialogView);
        mAlertDialogBuilder.setCancelable(false);
        mAlertDialog = mAlertDialogBuilder.create();

        TextView dialogMsg = mDialogView.findViewById(R.id.no_internet_dialog_msg_tv);
        dialogMsg.setTypeface(mMontserratRegular);

        Button retryBtn = mDialogView.findViewById(R.id.no_internet_dialog_retryBtn);
        retryBtn.setTypeface(mMontserratRegular);
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickRetryBtn.retry();
                mAlertDialog.dismiss();
            }
        });
        mAlertDialog.show();
    }

    public void setOnClickRetryBtnListener(OnClickRetryBtn onClickRetryBtn) {
        mOnClickRetryBtn = onClickRetryBtn;
    }
}
