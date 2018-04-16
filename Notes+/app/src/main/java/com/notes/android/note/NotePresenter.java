package com.notes.android.note;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.itextpdf.text.Chapter;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.CMYKColor;
import com.itextpdf.text.pdf.PdfWriter;
import com.notes.android.R;
import com.notes.android.database.AppDb;
import com.notes.android.home.HomeActivity;
import com.notes.android.models.NoteModel;
import com.notes.android.models.ToDoModel;
import com.notes.android.utilities.Utils;
import com.philliphsu.bottomsheetpickers.date.BottomSheetDatePickerDialog;
import com.philliphsu.bottomsheetpickers.date.DatePickerDialog;
import com.philliphsu.bottomsheetpickers.time.BottomSheetTimePickerDialog;
import com.philliphsu.bottomsheetpickers.time.grid.GridTimePickerDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Aniket on 23-03-2018.
 */

public class NotePresenter {

    private NoteView noteView;
    private List<ToDoModel> todoList = new ArrayList<>();
    private Date noteDate;
    private NoteModel noteData;
    public boolean isToDoChanged = false;
    public Date remiderDate;
    private Dialog dialog;

    public NotePresenter(NoteView noteView) {
        this.noteView = noteView;
    }

    public void exitCheck() {
        if (noteView.bottomSheetStatus()) {
            showBottomSheet();
        } else {
            if (((Note) noteView).isFromNotification) {
                noteView.startHomeActivity();
            } else {
                noteView.exitActivity();
            }
        }
    }

    public void unDoText() {
        noteView.undoText();
    }

    public void reDoText() {
        noteView.redoText();
    }


    public void getNoteDate() {
        Calendar calendar = Calendar.getInstance();
        noteDate = calendar.getTime();
        noteView.setDate(noteDate.getDate() + " " + getMonthName(noteDate.getMonth()));
    }

    private String getMonthName(int monthNo) {
        switch (monthNo) {
            case 0:
                return "Jan";
            case 1:
                return "Feb";
            case 2:
                return "Mar";
            case 3:
                return "Apr";
            case 4:
                return "May";
            case 5:
                return "Jun";
            case 6:
                return "Jul";
            case 7:
                return "Aug";
            case 8:
                return "Sep";
            case 9:
                return "Oct";
            case 10:
                return "Nov";
            case 11:
                return "Dec";
            default:
                return "";
        }
    }

    public void showLockNoteDialog(final Context context) {
        final Dialog passworDialog = new Dialog(context);
        passworDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            passworDialog.getWindow().setBackgroundDrawable(null);
        } else {
            passworDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        passworDialog.setContentView(R.layout.password_dialog);
        passworDialog.setCancelable(true);
        passworDialog.show();

        final EditText password = (EditText) passworDialog.findViewById(R.id.et_password);
        final EditText confirmPassword = (EditText) passworDialog.findViewById(R.id.et_confirm_password);
        Button ok = (Button) passworDialog.findViewById(R.id.btn_ok);
        Button cancel = (Button) passworDialog.findViewById(R.id.btn_cancel);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(password.getText()) || TextUtils.isEmpty(confirmPassword.getText())) {
                    Toast.makeText(context, "Enter Password", Toast.LENGTH_SHORT).show();
                } else {
                    if (password.getText().toString().equals(confirmPassword.getText().toString())) {
                        passworDialog.dismiss();
                        noteView.setPassword(password.getText().toString());
                        noteView.showLockIcon();
                    } else {
                        Toast.makeText(context, "Password Doesn't match", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passworDialog.dismiss();
            }
        });
    }

    private void askForPassword(final NoteModel clickedData, final Context mContext) {
        final Dialog passworDialog = new Dialog(mContext);
        passworDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        passworDialog.setContentView(R.layout.ask_password);
        passworDialog.getWindow().setBackgroundDrawable(null);
        passworDialog.setCancelable(true);
        passworDialog.show();

        final EditText etPassword = (EditText) passworDialog.findViewById(R.id.et_ask_password);
        Button ok = (Button) passworDialog.findViewById(R.id.btn_ask_ok);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passworDialog.dismiss();
                if (etPassword.getText().toString().equals(clickedData.getPassword())) {
                    noteView.setPassword("");
                } else {
                    Toast.makeText(mContext, "Wrong Password", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }


    public void showPictureSelectionDialog(Context context) {
        final Dialog selectionDialog = new Dialog(context);
        selectionDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        selectionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(android.R.color.transparent)));
        View view = LayoutInflater.from(context).inflate(R.layout.selection_dialog, null);
        selectionDialog.setContentView(view);
        view.findViewById(R.id.txtCamera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteView.openCamera();
                selectionDialog.dismiss();
            }
        });

        view.findViewById(R.id.txtGallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteView.openGallery();
                selectionDialog.dismiss();
            }
        });

        selectionDialog.show();
    }


    public boolean checkForStoragePermissions(Context mCotext) {

        if (ContextCompat.checkSelfPermission(mCotext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    void showBottomSheet() {
        noteView.showBottomSheet();
    }

    void loadImagesRv(List<Uri> imagesList) {
        noteView.loadImageRv(imagesList);
    }

    public void setNoteData(NoteModel noteData) {
        this.noteData = noteData;
        remiderDate = new Date(noteData.getReminderDate());
    }

    public List<ToDoModel> getToDoList() {
        return todoList;
    }

    void addToDo(String todo) {
        if (TextUtils.isEmpty(todo)) {
            Toast.makeText((Context) noteView, "Enter ToDo", Toast.LENGTH_SHORT).show();
            return;
        } else {
            ToDoModel data = new ToDoModel();
            data.setTodoGoal(todo);
            data.setDone(false);
            noteView.addToDo(data);
            todoList.add(data);
            isToDoChanged = true;
        }
    }

    public NoteModel getAllData(Context mContext) {
        AppDb appDb = AppDb.getAppDatabase(mContext);

        if (noteData == null)
            noteData = new NoteModel();

        if (((Note) noteView).getNoteText().equalsIgnoreCase("") && todoList.size() == 0) {
            return null;
        }
        noteData.setNoteData(((Note) noteView).getNoteText());
        noteData.setCreatedAt(noteDate.getTime());
        noteData.setImagesList(((Note) noteView).getImagesList().toString());
        noteData.setPassword(((Note) noteView).getPassword());
        if (((Note) noteView).getPassword() != null)
            noteData.setLocked(((Note) noteView).getPassword().equalsIgnoreCase("") ? false : true);
        if (remiderDate != null) {
            noteData.setReminderDate(remiderDate.getTime());
        } else {
            noteData.setReminderDate(0);
        }
        noteData.setLastEditDate(new Date().getTime());

        //Insert in db
        if (((Note) noteView).isUpdate || noteData.getNoteId() > 0) {
            appDb.notesDao().updateNoteData(noteData.getNoteData(), noteData.getImagesList(), noteData.getPassword(), noteData.isLocked(), noteData.getNoteId(), noteData.getReminderDate(), noteData.getLastEditDate());
        } else {
            appDb.notesDao().insertData(noteData);
        }

        //inert
        int noteId;
        if (noteData.getNoteId() != 0 && isToDoChanged) {
            appDb.todoDao().deleteAll(noteData.getNoteId());
            noteId = noteData.getNoteId();
        } else {
            noteId = appDb.notesDao().getLastEntry().get(0).getNoteId();
        }

        for (ToDoModel data : todoList) {
            if (((Note) noteView).isUpdate && !isToDoChanged) {
                appDb.todoDao().updateToDo(data.getTodoGoal(), data.isDone(), noteId, data.getTodoId());
            } else {
                data.setNoteId(noteId);
                appDb.todoDao().insertData(data);
            }
        }

        return noteData;
    }

    public boolean getToDoFromDb(Context mContext, int noteId) {
        AppDb appDb = AppDb.getAppDatabase(mContext);

        todoList = appDb.todoDao().findForNote(noteId);
        if (todoList != null && todoList.size() > 0) {
            for (ToDoModel data : todoList)
                noteView.addToDo(data);

            return true;
        }

        return false;
    }


    public void showDatePicker(final AppCompatActivity mContex) {
        Calendar now = Calendar.getInstance();
        BottomSheetDatePickerDialog date;
        date = BottomSheetDatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
                        Log.d("DatePicker", "onDateSet: " + year + " " + monthOfYear + " " + dayOfMonth);

                        showTimePickerDialog(mContex, year, monthOfYear, dayOfMonth);
                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));
        date.show(mContex.getSupportFragmentManager(), "");
    }

    private void showTimePickerDialog(final AppCompatActivity mContex, final int year, final int month, final int day) {
        GridTimePickerDialog grid;
        Calendar now = Calendar.getInstance();
        grid = GridTimePickerDialog.newInstance(
                new BottomSheetTimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(ViewGroup viewGroup, int hourOfDay, int minute) {
                        Log.d("TimePicker", "onTimeSet: " + hourOfDay + " " + minute);
                        remiderDate = Utils.getDate(year, month, day, hourOfDay, minute);
                        noteView.showReminderIcon();
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(remiderDate);
                        ((Note) noteView).setAlarm(cal, getAllData(mContex));
                    }
                },
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(mContex));
        grid.show(mContex.getSupportFragmentManager(), "");
    }

    public void deleteNote(Context mContext, NoteModel noteData) {
        if (mContext != null) {
            AppDb.getAppDatabase(mContext).todoDao().deleteAll(noteData.getNoteId());
            AppDb.getAppDatabase(mContext).notesDao().delete(noteData);
        } else {
            Toast.makeText(mContext, "Unable to delete at the moment", Toast.LENGTH_SHORT).show();
        }

        exitCheck();
    }

    public void showRemovePasswordDialog(final Context mContext, final boolean forPassword) {
        Date reminderDate = new Date(getAllData(mContext).getReminderDate());
        String header;
        if (forPassword) {
            header = "Remove Password  ?";
        } else {
            header = "Remove Reminder of " +
                    reminderDate.getDate() + "/" + reminderDate.getMonth() +
                    "/" + (reminderDate.getYear() < 2000 ? "" + (reminderDate.getYear() + 1900) : reminderDate.getYear() + "") + "" + " ?";
        }


        dialog = new AlertDialog.Builder(mContext).setMessage(header)
                .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (forPassword) {
                            askForPassword(getAllData(mContext), mContext);
                        } else {
                            noteView.hideReminderIcon();
                            remiderDate = null;
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (dialog != null)
                            dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }


    public void shareNoteData(Context mContext, String noteData) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Note From Notes+");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, noteData);
        mContext.startActivity(Intent.createChooser(sharingIntent, "Share Note"));
    }

    public String getDataToShare(NoteModel noteData, Context mContext) {
        if (noteData != null) {
            StringBuffer builder = new StringBuffer();
            builder.append(noteData.getNoteData());
            builder.append("\n\n");

            List<ToDoModel> todoList = AppDb.getAppDatabase(mContext).todoDao().findForNote(noteData.getNoteId());

            if (todoList != null && todoList.size() > 0) {

                for (ToDoModel data : todoList) {
                    builder.append("\u25CF " + data.getTodoGoal() + "\n");
                }
                return builder.toString();
            } else {
                return builder.toString();
            }
        }

        return null;
    }


    public void generatePDF(File pdfFile, NoteModel noteData, Context mContext) {


        Font themeColor = FontFactory.getFont(FontFactory.HELVETICA, 20, Font.BOLD, new CMYKColor(81, 22, 0, 42));
        Font textColor = FontFactory.getFont(FontFactory.HELVETICA, 15, Font.NORMAL, new CMYKColor(255, 255, 255, 255));
        Font zapfdingbats = new Font(Font.FontFamily.ZAPFDINGBATS, 8);
        Chunk bullet = new Chunk(String.valueOf((char) 108), zapfdingbats);
        try {
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();


            Paragraph chapterTitle = new Paragraph("Note", themeColor);
            Chapter chapter1 = new Chapter(chapterTitle, 1);

            Paragraph sectionTitle = new Paragraph("Note Text", themeColor);
            Section section1 = chapter1.addSection(sectionTitle);

            Paragraph sectionContent = new Paragraph(noteData.getNoteData(), textColor);
            section1.add(sectionContent);
            chapter1.setNumberDepth(0);


            List<Uri> listOfImageUri = ((Note) noteView).getImagesList();
            if (listOfImageUri != null && listOfImageUri.size() > 0) {
                Paragraph imagesTitle = new Paragraph("Note Images", themeColor);
                Section section2 = chapter1.addSection(imagesTitle);

                if (listOfImageUri != null) {
                    for (Uri image : listOfImageUri) {
                        Image pdfImage = getPdfImage(image, mContext);
                        if (pdfImage != null) {
                            section2.add(pdfImage);
                        }
                    }
                }
            }

            if (todoList != null && todoList.size() > 0) {
                Paragraph todoSection = new Paragraph("Todo List", themeColor);
                Section section3 = chapter1.addSection(todoSection);
                for (ToDoModel todo : todoList) {
                    Paragraph todoItem = new Paragraph();
                    todoItem.add(bullet);
                    todoItem.add(Chunk.createWhitespace(" ").append(todo.getTodoGoal()).toString());
                    section3.add(todoItem);
                }
            }

            document.add(chapter1);
            document.close();
            writer.close();

            noteView.pdfGenerated(pdfFile);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Image getPdfImage(Uri uri, Context mContext) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), false);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            byte[] bitmapdata = stream.toByteArray();

            Image image = Image.getInstance(bitmapdata);
            return image;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public void openFile(Context context, File url) throws IOException {
        // Create URI
        File file = url;
        Uri uri = Uri.fromFile(file);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        // Check what kind of file you are trying to open, by comparing the url with extensions.
        // When the if condition is matched, plugin sets the correct intent (mime) type,
        // so Android knew what application to use to open the file
        if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
            // Word document
            intent.setDataAndType(uri, "application/msword");
        } else if (url.toString().contains(".pdf")) {
            // PDF file
            intent.setDataAndType(uri, "application/pdf");
        } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
            // Powerpoint file
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
            // Excel file
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if (url.toString().contains(".zip") || url.toString().contains(".rar")) {
            // WAV audio file
            intent.setDataAndType(uri, "application/x-wav");
        } else if (url.toString().contains(".rtf")) {
            // RTF file
            intent.setDataAndType(uri, "application/rtf");
        } else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
            // WAV audio file
            intent.setDataAndType(uri, "audio/x-wav");
        } else if (url.toString().contains(".gif")) {
            // GIF file
            intent.setDataAndType(uri, "image/gif");
        } else if (url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
            // JPG file
            intent.setDataAndType(uri, "image/jpeg");
        } else if (url.toString().contains(".txt")) {
            // Text file
            intent.setDataAndType(uri, "text/plain");
        } else if (url.toString().contains(".3gp") || url.toString().contains(".mpg") || url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
            // Video files
            intent.setDataAndType(uri, "video/*");
        } else {
            //if you want you can also define the intent type for any other file

            //additionally use else clause below, to manage other unknown extensions
            //in this case, Android will show all applications installed on the device
            //so you can choose which application to use
            intent.setDataAndType(uri, "*/*");
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
