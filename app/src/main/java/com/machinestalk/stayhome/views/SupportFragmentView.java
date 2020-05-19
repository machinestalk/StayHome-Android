package com.machinestalk.stayhome.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.machinestalk.android.interfaces.Controller;
import com.machinestalk.stayhome.R;
import com.machinestalk.stayhome.activities.DashboardActivity;
import com.machinestalk.stayhome.adapterdelegates.BaseRecyclerAdapter;
import com.machinestalk.stayhome.adapterdelegates.contactUs.AttachmentAdapter;
import com.machinestalk.stayhome.adapterdelegates.contactUs.AttachmentEntity;
import com.machinestalk.stayhome.components.Button;
import com.machinestalk.stayhome.components.EditText;
import com.machinestalk.stayhome.config.AppConfig;
import com.machinestalk.stayhome.constants.AppConstants;
import com.machinestalk.stayhome.entities.Mode;
import com.machinestalk.stayhome.fragments.SupportFragment;
import com.machinestalk.stayhome.helpers.ImagePickerManager;
import com.machinestalk.stayhome.listeners.ImageSelectionListener;
import com.machinestalk.stayhome.utils.ImageUtil;
import com.machinestalk.stayhome.utils.Util;
import com.machinestalk.stayhome.views.base.BaseView;
import com.orhanobut.logger.Logger;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.apache.commons.collections.BidiMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 12/26/2016.
 */

public class SupportFragmentView extends BaseView implements ImageSelectionListener, AttachmentAdapter.onItemClickListener {

    private EditText mOptionalName;
    private EditText mMessageEditText;
    private Button mSendButton;
    private List<String> listContactTypes;
    private ImagePickerManager mImagePickerManager;
    private View progress;
    private int selectedKey = 0;
    private int selectedIndex = 0;
    private BidiMap valueMap;
    private LinearLayout mAttachImageLinearLayout;
    private static final int REQUEST_CODE = 18;
    private int mSelectedRequestCode;
    private RecyclerView mAttachmentsRecyclerView;
    private BaseRecyclerAdapter mAttachmentsAdapter;
    private Mode mAttachmentMode = new Mode(AppConstants.MODE_VIEW);

    private List<AttachmentEntity> mAttachmentEntityList = new ArrayList<>();
    private LinearLayout mContactFieldsLinearLayout;


    public SupportFragmentView(Controller controller) {
        super(controller);
    }

    @Override
    protected int getViewLayout() {
        return R.layout.view_contact_us;
    }

    @Override
    protected void onCreate() {

        initUI();
        try {
            ((DashboardActivity) controller.getBaseActivity()).navigateSlideMenu(getString(R.string.ApDr_ApDr_SBtn_support), false);
        } catch (Exception e) {
        }

    }

    private void initUI() {

//        listContactTypes = new ArrayList<>(valueMap.values());

//        Collections.sort(listContactTypes);

        mOptionalName = findViewById(R.id.view_contact_us_name);
        mMessageEditText = findViewById(R.id.txtMessage);
        mSendButton = findViewById(R.id.view_support_send_button);
        mAttachImageLinearLayout = findViewById(R.id.view_contact_us_attach_image);
        mAttachmentsRecyclerView = findViewById(R.id.view_contact_us_recycler_attachment);
        mContactFieldsLinearLayout = findViewById(R.id.view_contact_us_fields_container);

        progress = findViewById(R.id.progress);
        initAttachmentAdapter();


    }

    @Override
    protected void onToolBarSetup(Toolbar toolBar) {
        super.onToolBarSetup(toolBar);
    }

    @Override
    protected void setActionListeners() {


        mContactFieldsLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.hideSoftKeyboard(getBaseActivity());
            }
        });
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isValidated()) {

                    List<String> attachmentList = new ArrayList<>();
                    if (mAttachmentEntityList != null && mAttachmentEntityList.size() > 0) {

                        Logger.i("image base 64 :"+mAttachmentEntityList.get(0).getAttachmentStream());
                        for (AttachmentEntity attachmentEntity : mAttachmentEntityList) {
                            if (attachmentEntity != null) {
                                attachmentList.add(attachmentEntity.getAttachmentStream());
                            }
                        }
                    }

                    String contactUsObject = getJson(AppConfig.getInstance().getUser().getPhoneNumber(), mOptionalName.getText().toString().trim(), mMessageEditText.getText().toString().trim(), attachmentList);
                    ((SupportFragment) controller).submitContactForm(contactUsObject);
                    //  Util.closeKeyboard(txtMessage, (SupportFragment) controller);
                }

            }
        });

        mAttachImageLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mImagePickerManager == null) {
                    mImagePickerManager = new ImagePickerManager(controller, SupportFragmentView.this, REQUEST_CODE);
                }
                if (!mImagePickerManager.hasNecessaryPermissions()) {
                    return;
                }
                mImagePickerManager.showImagePicker(4);
            }
        });
    }

    public void clearFields() {
        mOptionalName.setText("");
        mOptionalName.clearFocus();
        mMessageEditText.setText("");
        mMessageEditText.clearFocus();
    }

    public Mode getAttachmentMode() {
        return mAttachmentMode;
    }

    /**
     * initialize attachments adapter.
     */
    private void initAttachmentAdapter() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseActivity(), LinearLayoutManager.HORIZONTAL, false);
        mAttachmentsRecyclerView.setLayoutManager(layoutManager);
        mAttachmentsAdapter = new BaseRecyclerAdapter<>(mAttachmentEntityList);
        mAttachmentsAdapter.addAdapterDelegates(new AttachmentAdapter(controller, this, mAttachmentMode));
        mAttachmentsRecyclerView.setAdapter(mAttachmentsAdapter);

    }


    public void resetAttachmentList(){
        mAttachmentEntityList.clear();
        mAttachmentEntityList = new ArrayList<>();
        initAttachmentAdapter();
    }
    /**
     * save data as json type in preferences
     *
     * @return
     */
    private String getJson(String phoneNumber, String subject, String message, List<String> attachment) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("phoneNumber", phoneNumber);
        jsonObject.addProperty("subject", subject);
        jsonObject.addProperty("message", message);

        if (attachment != null && attachment.size() > 0){
            JsonObject attachmentJsonObject = new JsonObject();
            for (int i = 0; i < attachment.size(); i ++){
                attachmentJsonObject.addProperty("image"+i+".jpeg", attachment.get(i));
            }
            jsonObject.add("attachement", attachmentJsonObject);
        }

        return jsonObject.toString();
    }

    private boolean isValidated() {

        String messageTxt = mMessageEditText.getText().toString().trim();
        boolean status = true;
        if (TextUtils.isEmpty(messageTxt)) {
            Util.showInfoDialog(controller.getBaseActivity(), getString(R.string.ApI_ContUs_lbl_contact_us_required_message));
            status = false;
        }

        return status;
    }

    public void showProgress() {
        if (progress == null)
            return;

        progress.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        if (progress == null)
            return;

        progress.setVisibility(View.GONE);
    }

    @Override
    protected int getToolbarId() {
        return R.id.toolbar;
    }

    public void handleIntent(Intent data, int requestCode) {

        if (mSelectedRequestCode == REQUEST_CODE && requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            data.setData(result.getUri());
            mImagePickerManager.handleCropIntent(data, requestCode);

            return;
        }

        if (mSelectedRequestCode == REQUEST_CODE) {
            if (mImagePickerManager == null) {
                return;
            }

            if (requestCode == ImagePickerManager.GALLERY_REQUEST_CODE) {

                CropImage.activity(data.getData()).setInitialRotation(ImageUtil.exifToDegrees(data.getData()))
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(controller.getBaseActivity());

            } else if (requestCode == ImagePickerManager.CAMERA_REQUEST_CODE) {

                CropImage.activity(mImagePickerManager.getCameraUri()).setInitialRotation(ImageUtil.exifToDegrees(mImagePickerManager.getCameraUri()))
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(controller.getBaseActivity());

            }
            return;
        }

    }

    @Override
    public void willStartSelectingImage(int requestCode) {
        mSelectedRequestCode = requestCode;
    }

    @Override
    public void onImageSelected(Bitmap bitmap, int requestCode) {

        AttachmentEntity attachmentEntity = new AttachmentEntity();

        attachmentEntity.setImageBitmap(bitmap);
        attachmentEntity.setAttachmentExtension(mImagePickerManager.getImageExtension());
        attachmentEntity.setAttachmentStream(mImagePickerManager.getImageBase64());

        mAttachmentEntityList.add(attachmentEntity);
//        if (mAttachmentEntityList.size() == 3) {
//            mAttachImageLinearLayout.setVisibility(View.INVISIBLE);
//        }


        mAttachmentsAdapter.setDataListt(mAttachmentEntityList);

    }

    public boolean resetAttachmentModeOnBackPressed() {
        if (mAttachmentMode.getMode() == AppConstants.MODE_UPDATE) {
            mAttachmentMode.setMode(AppConstants.MODE_VIEW);
            mAttachmentsAdapter.notifyDataSetChanged();
            return true;
        }
        return false;
    }


    @Override
    public void onRemoveImage() {

    }
    /**
     *
     */

    @Override
    public void onAttachmentLongClick(AttachmentEntity attachmentEntity) {
        Logger.i("item attachment long click");
        mAttachmentMode.setMode(AppConstants.MODE_UPDATE);
        mAttachmentsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDeleteAttachment(AttachmentEntity attachmentEntity) {

        mAttachmentEntityList.remove(attachmentEntity);
//        if (mAttachmentEntityList.size() < 3) {
//            mAttachImageLinearLayout.setVisibility(View.VISIBLE);
//        }
        mAttachmentsAdapter.setDataListt(mAttachmentEntityList);
    }
}
