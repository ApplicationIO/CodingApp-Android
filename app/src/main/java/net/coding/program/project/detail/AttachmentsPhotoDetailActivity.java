package net.coding.program.project.detail;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ProgressBar;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import net.coding.program.ImagePagerFragment;
import net.coding.program.R;
import net.coding.program.model.AttachmentFileObject;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;

@EActivity(R.layout.activity_attachments_photo_detail)
public class AttachmentsPhotoDetailActivity extends AttachmentsDetailBaseActivity {

    @ViewById
    SubsamplingScaleImageView imageView;

    @ViewById
    View layout_dynamic_history, layout_image_prototype, clickImagePrototype;

    @ViewById
    ProgressBar progressBar;

    @AfterViews
    protected final void initAttachmentsPhotoDetailActivity() {
        updateDisplay();
    }

    private void updateDisplay() {
        if (mExtraFile != null) {
            String filePath = "file://" + mExtraFile.getPath();

            imageView.setImage(ImageSource.uri(filePath));
            layout_image_prototype.setVisibility(View.GONE);
            layout_dynamic_history.setVisibility(View.VISIBLE);
            findViewById(R.id.bottomPanel).setVisibility(View.GONE);

        } else if (mFile.exists()) {
            String filePath = "file://" + mFile.getPath();
            imageView.setImage(ImageSource.uri(filePath));
            layout_image_prototype.setVisibility(View.GONE);
            layout_dynamic_history.setVisibility(View.VISIBLE);
        } else {
            getImageLoad().imageLoader.loadImage(mAttachmentFileObject.owner_preview, ImagePagerFragment.optionsImage, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    File file = getImageLoad().imageLoader.getDiskCache().get(imageUri);
                    imageView.setImage(ImageSource.uri(file.getAbsolutePath()));
                }
            });
            layout_image_prototype.setVisibility(View.VISIBLE);
            layout_dynamic_history.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected int getMenuResourceId() {
        return R.menu.project_attachment_photo;
    }

    @Click
    protected void clickImagePrototype() {
        action_download();
        showMiddleToast("开始下载");

        progressBar.setVisibility(View.VISIBLE);
        clickImagePrototype.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onDownloadProgress(int progress) {
        progressBar.setProgress(progress);
    }

    @Override
    protected void onDownloadFinish(boolean success) {
        showProgressBar(false);
        if (success) {
            Intent intent = new Intent();
            mAttachmentFileObject.isDownload = true;
            intent.putExtra(AttachmentFileObject.RESULT, mAttachmentFileObject);
            intent.putExtra(AttachmentsActivity.FileActions.ACTION_NAME,
                    AttachmentsActivity.FileActions.ACTION_EDIT);
            setResult(RESULT_OK, intent);
            updateDisplay();
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            clickImagePrototype.setVisibility(View.VISIBLE);
        }
    }
}
