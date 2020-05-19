package com.machinestalk.stayhome.adapterdelegates.contactUs;

import android.view.View;

import androidx.annotation.NonNull;

import com.machinestalk.android.interfaces.Controller;
import com.machinestalk.stayhome.R;
import com.machinestalk.stayhome.adapterdelegates.AdapterDelegate;
import com.machinestalk.stayhome.entities.Mode;

import java.util.List;

public class AttachmentAdapter implements AdapterDelegate<AttachmentItemView> {

    private onItemClickListener mOnItemClickListener;
    private Mode mMode;
    private Controller mController;

    public Mode getMode() {
        return mMode;
    }

    public void setMode(Mode mode) {
        this.mMode = mode;
    }

    public AttachmentAdapter(Controller controller, onItemClickListener listener, Mode mode) {
        mController = controller;
        mOnItemClickListener = listener;
        mMode = mode;
    }


    @Override
    public boolean isForViewType(@NonNull List<?> items, int position) {
        return items.get(position) instanceof AttachmentEntity;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.view_attachment_item;
    }

    @NonNull
    @Override
    public AttachmentItemView getViewHolder(View itemView) {
        return new AttachmentItemView(itemView, mController, mMode, mOnItemClickListener);
    }

    public interface onItemClickListener {

        void onAttachmentLongClick(AttachmentEntity attachmentEntity);
        void onDeleteAttachment(AttachmentEntity attachmentEntity);
    }
}

