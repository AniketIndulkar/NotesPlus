package com.notes.android.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.notes.android.R;
import com.notes.android.database.AppDb;
import com.notes.android.models.NoteModel;
import com.notes.android.models.ToDoModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aniket on 23-03-2018.
 */

public class NoteRVAdapter extends RecyclerView.Adapter<NoteRVAdapter.NoteViewHolder> {

    private Context mContext;
    private List<NoteModel> noteDataList;
    private RVClickevents rvClickevents;

    public void setRvClickevents(RVClickevents rvClickevents) {
        this.rvClickevents = rvClickevents;
    }

    public NoteRVAdapter(Context mContext) {
        this.mContext = mContext;
        noteDataList = new ArrayList<>();
    }

    public void setNoteDataList(List<NoteModel> noteDataList) {
        this.noteDataList = noteDataList;
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.note_item, null);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, final int position) {


        String newline = System.getProperty("line.separator");
        String data = noteDataList.get(position).getNoteData();
        boolean hasNewline = data.contains(newline);

        if (hasNewline) {
            String stringArray[] = data.split(newline);
            if (stringArray.length > 0) {
                holder.title.setText(stringArray[0]);

                if (noteDataList.get(position).isLocked()) {
                    holder.content.setVisibility(View.GONE);
                    holder.linearRvToDo.setVisibility(View.GONE);
                } else {
                    String content = data.substring(data.indexOf(newline) + 1, data.length() > data.indexOf(newline) + 100 ? data.indexOf(newline) + 100 : data.length()).trim();
                    if (!content.equalsIgnoreCase("")) {
                        holder.content.setText(content);
                        holder.content.setVisibility(View.VISIBLE);
                        holder.linearRvToDo.setVisibility(View.GONE);
                    } else {
                        holder.content.setVisibility(View.GONE);
                        checkForToDo(holder.linearRvToDo, position);
                    }
                }
            }
        } else {
            if (data.length() > 20) {
                holder.title.setText(data.substring(0, 19));
                if (noteDataList.get(position).isLocked()) {
                    holder.content.setVisibility(View.GONE);
                    holder.linearRvToDo.setVisibility(View.GONE);
                } else {
                    holder.content.setText(data.substring(20, data.length() > 150 ? 150 : data.length()).trim());
                    holder.content.setVisibility(View.VISIBLE);
                    holder.linearRvToDo.setVisibility(View.GONE);
                }
            } else {
                holder.title.setText(data);
                if (noteDataList.get(position).isLocked()) {
                    holder.content.setVisibility(View.GONE);
                    holder.linearRvToDo.setVisibility(View.GONE);
                } else {
                    holder.content.setVisibility(View.GONE);
                    checkForToDo(holder.linearRvToDo, position);
                }


            }
        }


        if (noteDataList.get(position).isLocked()) {
            holder.ivNoteImage.setVisibility(View.GONE);
        } else {
            if (noteDataList.get(position).getImagesList() != null && noteDataList.get(position).getImagesList().length() > 4) {
                String imagesList = noteDataList.get(position).getImagesList();
                imagesList = imagesList.toString().replace("[", "").replace("]", "");
                String imagesArray[] = imagesList.split(",");
                if (imagesArray != null && imagesArray.length > 0) {
                    Uri uri = Uri.parse(imagesArray[0]);
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
                        holder.ivNoteImage.setImageBitmap(bitmap);
                        holder.ivNoteImage.setVisibility(View.VISIBLE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (SecurityException exp) {
                        Log.d("SecurityExp", "onBindViewHolder: " + exp.getLocalizedMessage());
                        holder.ivNoteImage.setVisibility(View.GONE);
                    }

                }

            } else {
                holder.ivNoteImage.setVisibility(View.GONE);
            }
        }


        if (noteDataList.get(position).isLocked()) {
            holder.ivLocked.setVisibility(View.VISIBLE);
        } else {
            holder.ivLocked.setVisibility(View.GONE);
        }

        holder.noteCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rvClickevents.onClick(noteDataList.get(position));
            }
        });
    }

    private void checkForToDo(LinearLayout linearRvToDo, int position) {
        List<ToDoModel> todoList = AppDb.getAppDatabase(mContext).todoDao().findForNote(noteDataList.get(position).getNoteId());
        if (todoList != null && todoList.size() > 0) {
            linearRvToDo.setVisibility(View.VISIBLE);
            linearRvToDo.removeAllViews();
            for (final ToDoModel todoData : todoList) {
                View toDoView = LayoutInflater.from(mContext).inflate(R.layout.layout_todo_rv, null);
                final CheckBox rbToDo = (CheckBox) toDoView.findViewById(R.id.cbToDo);
                rbToDo.setChecked(todoData.isDone());
                TextView tvTodo = (TextView) toDoView.findViewById(R.id.tvToDo);
                tvTodo.setText(todoData.getTodoGoal());
                if (todoData.isDone()) {
                    tvTodo.setPaintFlags(tvTodo.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    tvTodo.setPaintFlags(tvTodo.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                }

                rbToDo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                            rbToDo.setChecked(todoData.isDone());
                            // Code to display your message.
                    }
                });
                linearRvToDo.addView(toDoView, 0);
            }
        } else {
            linearRvToDo.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return noteDataList.size();
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        public TextView title, content;
        public ImageView ivNoteImage, ivLocked;
        public CardView noteCard;
        public LinearLayout linearRvToDo;

        public NoteViewHolder(View itemView) {
            super(itemView);
            noteCard = (CardView) itemView.findViewById(R.id.noteCard);
            title = (TextView) itemView.findViewById(R.id.txtTitle);
            content = (TextView) itemView.findViewById(R.id.txtContent);
            ivNoteImage = (ImageView) itemView.findViewById(R.id.ivNoteImage);
            ivLocked = (ImageView) itemView.findViewById(R.id.ivLocked);
            linearRvToDo = (LinearLayout) itemView.findViewById(R.id.linearRvToDo);
        }
    }


    public interface RVClickevents {
        void onClick(NoteModel clickedData);
    }
}
