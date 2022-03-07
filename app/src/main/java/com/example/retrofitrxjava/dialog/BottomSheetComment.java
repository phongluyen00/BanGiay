package com.example.retrofitrxjava.dialog;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.retrofitrxjava.R;
import com.example.retrofitrxjava.UserModel;
import com.example.retrofitrxjava.adapter.MutilAdt;
import com.example.retrofitrxjava.databinding.BottomSheetCommentBinding;
import com.example.retrofitrxjava.model.EBook;
import com.example.retrofitrxjava.model.Message;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.helper.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BottomSheetComment extends BottomSheetFragment<BottomSheetCommentBinding> {

    private EBook eBook;
    private List<Message> messageList = new ArrayList<>();
    private MutilAdt<Message> messageAdapter;

    public BottomSheetComment(EBook eBook, UserModel userModel) {
        this.eBook = eBook;
        this.userModel = userModel;
    }

    public void getComment() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("messages");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Message message = dataSnapshot.getValue(Message.class);
                    assert message != null;
                    if (message.getDocumentId().equals(eBook.getDocumentId())) {
                        messageList.add(message);
                    }
                }
                binding.progressCircular.setVisibility(View.GONE);
                messageAdapter.setDt((ArrayList<Message>) messageList);
                binding.noData.setVisibility(!messageList.isEmpty() ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.progressCircular.setVisibility(View.GONE);
                binding.noData.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void initLayout() {
        messageAdapter = new MutilAdt<>(activity, R.layout.item_comment);
        binding.rclComment.setAdapter(messageAdapter);
        binding.send.setOnClickListener(v -> {
            if (StringUtil.isBlank(Objects.requireNonNull(binding.edtComment.getText()).toString())) {
                Toast.makeText(activity, "Nhập đủ dữ liệu", Toast.LENGTH_SHORT).show();
            } else {
                writeNewMessage(binding.edtComment.getText().toString());
            }
        });
        getComment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.bottom_sheet_comment;
    }

    private void writeNewMessage(String body) {
        String documentId = "";
        if (eBook != null) {
            documentId = eBook.getDocumentId();
        }
        Message message = new Message(userModel.getName(), body, userModel.getImage(), documentId);
        Map messageValues = message.toMap();
        Map childUpdates = new HashMap<>();

        String key = databaseReference.child("messages").push().getKey();
        childUpdates.put("/messages/" + key, messageValues);
        childUpdates.put("/user_messages/" + firebaseUser.getUid() + "/" + key, messageValues);

        databaseReference.updateChildren(childUpdates);
        Toast.makeText(getActivity(), "Đã gửi bình luận", Toast.LENGTH_SHORT).show();
        binding.edtComment.setText("");
        hideKeyboard(activity);
    }
}
