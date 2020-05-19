package com.machinestalk.stayhome.adapterdelegates.contactUs;

import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

import com.machinestalk.android.interfaces.Controller;
import com.machinestalk.android.viewholders.BaseViewHolder;
import com.machinestalk.stayhome.R;
import com.machinestalk.stayhome.constants.AppConstants;
import com.machinestalk.stayhome.entities.Mode;
import com.makeramen.roundedimageview.RoundedImageView;

public class AttachmentItemView extends BaseViewHolder<AttachmentEntity> {

    private Controller mController;
    private Mode mMode;
    private RoundedImageView mImageView;
    private AppCompatImageView mDeleteImageView;
    private AttachmentAdapter.onItemClickListener mOnItemClickListener;
    private AttachmentEntity mAttachmentEntity;

    public AttachmentItemView(View itemView, Controller controller, Mode mode, AttachmentAdapter.onItemClickListener listener) {
        super(itemView);

        mController = controller;
        mMode = mode;
        mOnItemClickListener = listener;

        mImageView = itemView.findViewById(R.id.view_attachment_item_image);
        mDeleteImageView = itemView.findViewById(R.id.view_attachment_item_delete_image);

    }

    @Override
    public void bind(AttachmentEntity entity) {

        if (entity == null){
            return;
        }
        mAttachmentEntity = entity;

        mImageView.setImageBitmap(mAttachmentEntity.getImageBitmap());

        if (mMode.getMode() != AppConstants.MODE_VIEW){
            mDeleteImageView.setVisibility(View.VISIBLE);
        }else {
            mDeleteImageView.setVisibility(View.INVISIBLE);
        }
        mDeleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null){
                    mOnItemClickListener.onDeleteAttachment(mAttachmentEntity);
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
    }

    @Override
    public boolean onLongClick(View view) {

        if (mOnItemClickListener != null){
            mOnItemClickListener.onAttachmentLongClick(mAttachmentEntity);
        }
        return super.onLongClick(view);


    }
}
