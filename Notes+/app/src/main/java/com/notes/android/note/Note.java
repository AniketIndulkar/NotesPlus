package com.notes.android.note;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.notes.android.R;
import com.notes.android.adapters.ImagesRVAdapter;
import com.notes.android.custom.CustomEditText;
import com.notes.android.database.AppDb;
import com.notes.android.home.HomeActivity;
import com.notes.android.models.NoteModel;
import com.notes.android.models.ToDoModel;
import com.notes.android.services.AlarmReceiver;
import com.notes.android.utilities.AppConstants;
import com.notes.android.utilities.SharedPrefManager;
import com.notes.android.utilities.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Note extends AppCompatActivity implements NoteView, View.OnClickListener, ImagesRVAdapter.RVImageClickevents {

    private static final int TAKE_PICTURE = 3135;
    private static final int PICK_IMAGE = 7122;
    private NotePresenter presenter;
    private ImageView ivBack, ivUnDo, ivReDo, ivAddImage, ivToDo, ivMoreOption, ivAddToDo, ivReminder, ivLocked;
    private RelativeLayout linearTODO;
    private TextView txtDone, txtHeader;
    private CustomEditText customEditText;
    private EditText etToDo;
    private Uri imageUri;
    private LinearLayout bottomSheet, linearTodo;
    private BottomSheetBehavior sheetBehavior;
    private RecyclerView rvImages;
    private List<Uri> imagesList;
    private LayoutInflater layoutInflater;
    private String password;
    private int noteId = 0;
    private String TAG = getClass().getSimpleName();
    private NoteModel noteData;
    public boolean isUpdate;
    private File notesDirectory = new File("/sdcard/Notes+/");
    private File imagesDirectory = new File("/sdcard/Notes+/Images/");
    private File pdfDirectory = new File("/sdcard/Notes+/Pdf/");
    private final int EXTERNAL_STORAGE_PERMISSION = 5115;
    private final static int RQS_1 = 1;
    private String fontSize;
    public boolean isFromNotification;
    private boolean whilePdf = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_note);

        initializeViews();
        initializeObjects();
        getIntentData();
        setListners();
        presenter = new NotePresenter(this);
        presenter.getNoteDate();

        if (noteId != 0) {
            isUpdate = true;
            noteData = AppDb.getAppDatabase(Note.this).notesDao().findById(noteId);
            setPrevieousData();
        } else {
            isUpdate = false;
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);//TO hide keyboard
    }

    private void getIntentData() {
        Intent noteIntent = getIntent();
        noteId = noteIntent.getIntExtra(AppConstants.NOTE_ID, 0);
        if (noteIntent.hasExtra("From")) {
            isFromNotification = true;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setPrevieousData() {
        customEditText.setText(noteData.getNoteData());
        customEditText.setSelection(noteData.getNoteData().length());
        presenter.setNoteData(noteData);

        boolean isToDo = presenter.getToDoFromDb(Note.this, noteData.getNoteId());

        if (isToDo) {
            linearTODO.setVisibility(View.VISIBLE);
        } else {
            linearTODO.setVisibility(View.GONE);
        }

        if (noteData.getReminderDate() > 0) {
            if (noteData.getReminderDate() > new Date().getTime()) {
                ivReminder.setVisibility(View.VISIBLE);
                ivReminder.setOnClickListener(this);
            } else {
                ivReminder.setVisibility(View.GONE);
            }
        } else {
            ivReminder.setVisibility(View.GONE);
        }

        if (noteData.isLocked()) {
            setPassword(noteData.getPassword());
            showLockIcon();
            ivLocked.setOnClickListener(this);
        } else {
            hideLockIcon();
        }

        if (noteData.getImagesList() != null && !noteData.getImagesList().equalsIgnoreCase("[]")) {
            List<String> listOfString = Utils.getListOfString(noteData.getImagesList());
            List<Uri> listOfUri = new ArrayList<>();
            for (String uri : listOfString) {
                if (!uri.isEmpty())
                    listOfUri.add(Uri.parse(uri.replace(" ", "")));
            }
            imagesList = listOfUri;
            presenter.loadImagesRv(imagesList);
        }
    }


    private void initializeObjects() {
        layoutInflater = LayoutInflater.from(this);
    }

    private void setListners() {
        //ImageViews
        ivBack.setOnClickListener(this);
        ivUnDo.setOnClickListener(this);
        ivReDo.setOnClickListener(this);
        ivAddImage.setOnClickListener(this);
        ivToDo.setOnClickListener(this);
        ivMoreOption.setOnClickListener(this);
        ivAddToDo.setOnClickListener(this);

        //TextVies
        txtDone.setOnClickListener(this);
    }

    private void initializeViews() {
        //All imageVies
        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivUnDo = (ImageView) findViewById(R.id.ivUnDo);
        ivReDo = (ImageView) findViewById(R.id.ivReDo);
        ivAddImage = (ImageView) findViewById(R.id.ivAddImage);
        ivToDo = (ImageView) findViewById(R.id.ivToDo);
        ivMoreOption = (ImageView) findViewById(R.id.ivMoreOption);
        ivAddToDo = (ImageView) findViewById(R.id.ivAddToDo);
        ivReminder = (ImageView) findViewById(R.id.ivReminder);
        ivLocked = (ImageView) findViewById(R.id.ivLocked);

        //All textViews
        txtDone = (TextView) findViewById(R.id.txtDone);
        txtHeader = (TextView) findViewById(R.id.txtHeader);
        customEditText = (CustomEditText) findViewById(R.id.etNoteText);


        //Edittext
        etToDo = (EditText) findViewById(R.id.etToDo);

        //layouts
        bottomSheet = (LinearLayout) findViewById(R.id.bottomSheet);
        setUpBottomSheet();
        sheetBehavior = BottomSheetBehavior.from(bottomSheet);
        linearTODO = (RelativeLayout) findViewById(R.id.linearTODO);
        linearTodo = (LinearLayout) findViewById(R.id.linearTodo);

        //rv
        rvImages = (RecyclerView) findViewById(R.id.rvImages);

        imagesList = new ArrayList<>();

        if (new SharedPrefManager(Note.this).isContains(AppConstants.FONT_SIZE)) {
            fontSize = new SharedPrefManager(Note.this).getStringFromPref(AppConstants.FONT_SIZE);
            if (Integer.parseInt(fontSize) != 0) {
                customEditText.setTextSize(Integer.parseInt(fontSize));
                etToDo.setTextSize(Integer.parseInt(fontSize));
            }
        }
    }

    public List<Uri> getImagesList() {
        return imagesList;
    }

    private void setUpBottomSheet() {
        bottomSheet.findViewById(R.id.tvChangeColor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet();
            }
        });
        bottomSheet.findViewById(R.id.tvReminder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet();
                presenter.showDatePicker(Note.this);
            }
        });
        bottomSheet.findViewById(R.id.tvPdf).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet();

                if (presenter.checkForStoragePermissions(Note.this)) {
                    try {
                        NoteModel pdfData = presenter.getAllData(Note.this);
                        if (pdfData != null) {
                            presenter.generatePDF(createPdfFile("pdf" + new Date().getTime(), "pdf"), pdfData, Note.this);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    whilePdf = true;
                    ActivityCompat.requestPermissions(Note.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            EXTERNAL_STORAGE_PERMISSION);
                }


            }
        });
        bottomSheet.findViewById(R.id.tvLock).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet();
                presenter.showLockNoteDialog(Note.this);
            }
        });
        bottomSheet.findViewById(R.id.tvDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet();
                presenter.deleteNote(Note.this, noteData);
            }
        });
        bottomSheet.findViewById(R.id.tvShare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet();
                String dataToShare = presenter.getDataToShare(presenter.getAllData(Note.this), Note.this);
                if (dataToShare != null) {
                    presenter.shareNoteData(Note.this, dataToShare);
                } else {
                    Toast.makeText(Note.this, "No data to share", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        presenter.getAllData(Note.this);
        presenter.exitCheck();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ivBack:
                presenter.getAllData(Note.this);
                presenter.exitCheck();
                break;

            case R.id.ivUnDo:
                presenter.unDoText();
                break;

            case R.id.ivReDo:
                presenter.reDoText();
                break;

            case R.id.ivAddImage:
                if (presenter.checkForStoragePermissions(Note.this)) {
                    presenter.showPictureSelectionDialog(Note.this);
                } else {
                    whilePdf = false;
                    ActivityCompat.requestPermissions(Note.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            EXTERNAL_STORAGE_PERMISSION);
                }
                break;

            case R.id.ivToDo:
                linearTODO.setVisibility(View.VISIBLE);
                ivToDo.setEnabled(false);
                break;

            case R.id.ivMoreOption:
                presenter.showBottomSheet();
                break;

            case R.id.ivAddToDo:
                if (!TextUtils.isEmpty(etToDo.getText().toString())) {
                    presenter.addToDo(etToDo.getText().toString());
                    etToDo.setText("");
                }
                break;

            case R.id.txtDone:
                presenter.getAllData(Note.this);
                presenter.exitCheck();
                break;
            case R.id.ivLocked:
                presenter.showRemovePasswordDialog(Note.this, true);
                return;
            case R.id.ivReminder:
                presenter.showRemovePasswordDialog(Note.this, false);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case EXTERNAL_STORAGE_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (whilePdf) {
                        try {
                            NoteModel pdfData = presenter.getAllData(Note.this);
                            if (pdfData != null) {
                                presenter.generatePDF(createPdfFile("pdf" + new Date().getTime(), "pdf"), pdfData, Note.this);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        presenter.showPictureSelectionDialog(Note.this);
                    }
                } else {
                    String tostMsg = "";
                    if (whilePdf) {
                        tostMsg = "We need to acces storage to create PDF file";
                    } else {
                        tostMsg = "We need to acces storage to take picture";
                    }

                    Toast.makeText(this, tostMsg, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void addToDo(final ToDoModel todo) {
        final View toDoView = layoutInflater.inflate(R.layout.layout_todo, null);
        CheckBox rbToDo = (CheckBox) toDoView.findViewById(R.id.cbToDo);
        rbToDo.setChecked(todo.isDone());
        final TextView tvTodo = (TextView) toDoView.findViewById(R.id.tvToDo);
        if (new SharedPrefManager(Note.this).isContains(AppConstants.FONT_SIZE))
            tvTodo.setTextSize(Integer.parseInt(fontSize));
        tvTodo.setText(todo.getTodoGoal());
        if (todo.isDone()) {
            tvTodo.setPaintFlags(tvTodo.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            tvTodo.setPaintFlags(tvTodo.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }
        rbToDo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                todo.setDone(isChecked);
                presenter.isToDoChanged = true;
                if (todo.isDone()) {
                    tvTodo.setPaintFlags(tvTodo.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    tvTodo.setPaintFlags(tvTodo.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                }
            }
        });

        toDoView.findViewById(R.id.ivDeleteToDo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearTodo.removeView(toDoView);
                presenter.getToDoList().remove(todo);
                presenter.isToDoChanged = true;
            }
        });
        linearTodo.addView(toDoView, 0);
    }

    @Override
    public void setDate(String dateSring) {
        txtHeader.setText(dateSring);
    }

    @Override
    public void exitActivity() {
        finish();
    }

    @Override
    public void undoText() {
        customEditText.unDoText();
    }

    @Override
    public void redoText() {
        customEditText.reDo();
    }

    @Override
    public void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = null;
        try {
            // place where to store camera taken picture
            photo = this.createTemporaryFile("picture", ".jpg");
//            photo.delete();
            photo.getParentFile().mkdirs();
            photo.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (photo != null) {
            imageUri = Uri.fromFile(photo);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        }
//        start camera intent
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, TAKE_PICTURE);

        }
    }


    private File createTemporaryFile(String part, String ext) throws Exception {

        // have the object build the directory structure, if needed.
        if (!notesDirectory.exists()) {
            notesDirectory.mkdirs();
        }

        if (!imagesDirectory.exists()) {
            imagesDirectory.mkdir();
        }


        return File.createTempFile(part, ext, imagesDirectory.exists() ? imagesDirectory : getCacheDir());
    }


    public File createPdfFile(String part, String ext) throws Exception {
        // have the object build the directory structure, if needed.
        if (!notesDirectory.exists()) {
            notesDirectory.mkdirs();
        }

        if (!pdfDirectory.exists()) {
            pdfDirectory.mkdir();
        }

        File pdfFile = new File(pdfDirectory, part + "." + ext);

        return pdfFile;
    }

    @Override
    public void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            intent.setAction(Intent.ACTION_GET_CONTENT);
        } else {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }


        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    public void showBottomSheet() {
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    @Override
    public boolean bottomSheetStatus() {
        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void loadImageRv(List<Uri> bitmapList) {
        ImagesRVAdapter notesAdapter = new ImagesRVAdapter(Note.this);
        notesAdapter.setImageClickEvents(this);
        notesAdapter.setImagesList(bitmapList);
        rvImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvImages.setAdapter(notesAdapter);
        rvImages.setVisibility(View.VISIBLE);
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
        ivLocked.setVisibility(View.GONE);
    }

    @Override
    public void showReminderIcon() {
        ivReminder.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideReminderIcon() {
        ivReminder.setVisibility(View.GONE);
        ivReminder.setOnClickListener(null);
    }

    @Override
    public void showLockIcon() {
        ivLocked.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLockIcon() {
        ivLocked.setVisibility(View.GONE);
        ivLocked.setOnClickListener(null);
    }

    public void setAlarm(Calendar calendar, NoteModel noteData) {
        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        Bundle noteDataBundle = new Bundle();
        noteDataBundle.putString("ReminderData", noteData.getNoteData() != null ? noteData.getNoteData() : "");
        noteDataBundle.putInt(AppConstants.NOTE_ID, noteData.getNoteId());
        intent.putExtra("ReminderNoteData", noteDataBundle);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), RQS_1, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    @Override
    public void pdfGenerated(final File pdfFile) {
        final Dialog dialog = new Dialog(Note.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.pdf_generated);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(null);
        } else {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        dialog.setCancelable(true);
        dialog.show();
        Button btn_open, btn_share;
        btn_open = (Button) dialog.findViewById(R.id.btn_open);
        btn_share = (Button) dialog.findViewById(R.id.btn_share);

        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                try {
                    presenter.openFile(Note.this, pdfFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intentShareFile = new Intent(Intent.ACTION_SEND);

                if (pdfFile.exists()) {
                    intentShareFile.setType("application/pdf");
                    intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + pdfFile.getAbsolutePath()));

                    intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                            "Sharing Pdf");
                    intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing Pdf");

                    startActivity(Intent.createChooser(intentShareFile, "Share File"));
                }
            }
        });
    }

    @Override
    public void startHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public String getPassword() {
        return password;
    }


    public String getNoteText() {
        return customEditText.getText().toString();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri selectedImage = null;
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    selectedImage = imageUri;
                }
                break;
            case PICK_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    if (data == null) {
                        //Display an error
                        return;
                    }
                    selectedImage = data.getData();
                }

                break;
        }

        if (selectedImage != null) {
            imagesList.add(selectedImage);
            presenter.loadImagesRv(imagesList);
        } else {
            Toast.makeText(this, "Unable to get Image", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onClickImage(Uri clickedImageUri) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(clickedImageUri, "image/*");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(Note.this, "No Application Found to open Image", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeleteClick(int position) {
        imagesList.remove(position);
        presenter.loadImagesRv(imagesList);
    }
}
