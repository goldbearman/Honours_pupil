package com.sabirovfarit.android.rx;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sabirovfarit.android.rx.UsefulClass.QueryPreferences;
import com.sabirovfarit.android.rx.UsefulClass.ReplacingTheme;
import com.sabirovfarit.android.rx.UsefulClass.ToastInfomation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    static final int GALLERY_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String[] STORAGE_PERMISSION = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int REQUEST_PHOTO = 3;

    private Unbinder unbinder;
    private File photoFilePath;


    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_class)
    EditText etClass;
    @BindView(R.id.et_email_parent)
    EditText etEmailParent;
    @BindView(R.id.iv_photo_profile)
    ImageView ivPhoto;
    @BindView(R.id.tv_grade)
    TextView tvGrade;
    @BindView(R.id.tv_photo)
    TextView tvPhoto;
    @BindView(R.id.tv_study_words)
    TextView tvStudyWords;
    @BindView(R.id.tv_dictation_write)
    TextView tvDictationWrite;

    @BindView(R.id.tv_number_dictation_write)
    TextView tvNumberDictationWritten;
    @BindView(R.id.tv_number_study_words)
    TextView tvNumberStudyWords;


    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        Bundle args = new Bundle();
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        photoFilePath = getPhotoFile();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View profileView = inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, profileView);
        setDataFromPreferences();

        return profileView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnTextChanged(value = R.id.et_name, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChangedName(Editable e) {
        QueryPreferences.setName(getActivity(), e.toString());
    }

    @OnTextChanged(value = R.id.et_email_parent, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChangedEmail(Editable e) {
        QueryPreferences.setEmail(getActivity(), e.toString());
    }

    @OnTextChanged(value = R.id.et_class, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChanged(Editable s) {
        if (s.toString().equals("")) {
            QueryPreferences.setClass(getActivity(), "");
            return;
        }
        int intClass = Integer.parseInt(s.toString());
        if (intClass > 0 && intClass < 12) {
            QueryPreferences.setClass(getActivity(), s.toString());
        } else {
            ToastInfomation.showToast(getActivity(), "Введите класс от 1 до 11");
            etClass.setText("");
        }
    }

    @OnClick({R.id.btn_theme_yellow, R.id.btn_theme_orange, R.id.btn_theme_red, R.id.btn_theme_green, R.id.btn_theme_dark_blue})
    void onClick(View view) {
        ReplacingTheme.replace(getActivity(), view);
    }

    @OnClick(R.id.iv_photo_profile)
    void onClickIv(View view) {
        Log.i(TAG, "onClickPhoto: " + "in");
        if (hasReadStoragePermission()) {
            showImageSelectionDialog();
        } else {
            ActivityCompat.requestPermissions(getActivity(), STORAGE_PERMISSION, PERMISSION_REQUEST_CODE);
        }
    }

    private void pickImageFromGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        try {
                            InputStream inputStream = getActivity().getContentResolver().openInputStream(selectedImage);
                            writeInputStreamToFile(inputStream, photoFilePath);
                            updatePhotoView();
                            Log.i(TAG, "onActivityResult: " + selectedImage.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    updatePhotoView();
                }
            case REQUEST_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    updatePhotoView();
                }
        }
    }

    private void setDataFromPreferences() {
        String stringClass = QueryPreferences.getClass(getActivity());
        String stringName = QueryPreferences.getName(getActivity());
        String stringEmail = QueryPreferences.getEmail(getActivity());
        int learnedWordCount = QueryPreferences.getLearnedWordCount(getActivity());
        int dictationsWritten = QueryPreferences.getDictationsWritten(getActivity());
        etClass.setText(stringClass);
        etName.setText(stringName);
        etEmailParent.setText(stringEmail);
        tvNumberStudyWords.setText("" + learnedWordCount);
        tvNumberDictationWritten.setText("" + dictationsWritten);
        updatePhotoView();
    }

    private boolean hasReadStoragePermission() {
        int result = ContextCompat
                .checkSelfPermission(getActivity(), STORAGE_PERMISSION[0]);

        return result == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (hasReadStoragePermission()) {
                    pickImageFromGallery();
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showImageSelectionDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogCustom)
                .setTitle("Выбрать фотографию:")
                .setItems(R.array.variatns, (dialog, which) -> {
                    if (which == 0) {
                        takePhoto();
                    } else if (which == 1) {
                        pickImageFromGallery();
                    }
                }).create();
        if (!getActivity().isFinishing()) {
            alertDialog.show();
        }
    }

    private void takePhoto() {
        // Check camera availability
        final Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = photoFilePath != null && photoIntent.resolveActivity(getActivity().getPackageManager()) != null;
        ivPhoto.setEnabled(canTakePhoto);

        Uri uri = FileProvider.getUriForFile(getActivity(), "com.sabirovfarit.android.rx.fileprovider", photoFilePath);
        photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        List<ResolveInfo> cameraActivitys = getActivity().getPackageManager().queryIntentActivities(photoIntent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo activity : cameraActivitys) {
            getActivity().grantUriPermission(activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        startActivityForResult(photoIntent, REQUEST_PHOTO);
    }

    public File getPhotoFile() {
        //Get the internal directory of the application
        File fileDir = getActivity().getFilesDir();
        return new File(fileDir, "photo.jpg");
    }

    private void updatePhotoView() {
        if (photoFilePath == null || !photoFilePath.exists()) {
            ivPhoto.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.user));
        } else {
            //Получаем файл по пути
            Bitmap bitmap = BitmapFactory.decodeFile(photoFilePath.getAbsolutePath());
            ivPhoto.setImageBitmap(bitmap);
            tvPhoto.setText("");
        }
    }

    private void writeInputStreamToFile(InputStream inputStream, File outFile) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(outFile);

        byte[] buffer = new byte[8192];
        int n;

        while ((n = inputStream.read(buffer)) > 0) {
            fileOutputStream.write(buffer, 0, n);
        }

        fileOutputStream.flush();
        fileOutputStream.close();
        inputStream.close();
    }
}
