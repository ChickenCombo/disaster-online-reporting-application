package com.app.dorav4.bluetoothchat.fragments;


import android.animation.Animator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dorav4.R;
import com.app.dorav4.activities.OfflineDashboardActivity;
import com.app.dorav4.bluetoothchat.BluetoothChatActivity;
import com.app.dorav4.bluetoothchat.gui.CustomAnimator;
import com.app.dorav4.bluetoothchat.gui.GuiTools;
import com.app.dorav4.bluetoothchat.gui.MessagesAdapter;
import com.bluetooth.communicator.BluetoothCommunicator;
import com.bluetooth.communicator.Message;
import com.bluetooth.communicator.Peer;


public class ConversationFragment extends Fragment {
    private ProgressBar loading;
    private static final float LOADING_SIZE_DP = 24;
    private EditText editText;
    private AppCompatImageButton sendButton;
    private RecyclerView mRecyclerView;
    protected TextView description;
    private ConstraintLayout constraintLayout;
    private BluetoothCommunicator.Callback communicatorCallback;
    private OfflineDashboardActivity offlineDashboardActivity;
    private BluetoothChatActivity activity;
    private MessagesAdapter mAdapter;
    private RecyclerView.SmoothScroller smoothScroller;

    public ConversationFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        communicatorCallback = new BluetoothCommunicator.Callback() {
            @Override
            public void onConnectionLost(Peer peer) {
                super.onConnectionLost(peer);
                Toast.makeText(activity,"Connection lost, reconnecting...",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onConnectionResumed(Peer peer) {
                super.onConnectionResumed(peer);
                Toast.makeText(activity,"Connection resumed",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onMessageReceived(Message message, int source) {
                super.onMessageReceived(message, source);
                mAdapter.addMessage(message);
                smoothScroller.setTargetPosition(mAdapter.getItemCount() - 1);
                mRecyclerView.getLayoutManager().startSmoothScroll(smoothScroller);
            }

            @Override
            public void onDisconnected(Peer peer, int peersLeft) {
                super.onDisconnected(peer, peersLeft);
                if (peersLeft == 0) {
                    activity.setFragment(BluetoothChatActivity.DEFAULT_FRAGMENT);
                }
            }
        };
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_conversation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editText = view.findViewById(R.id.editText);
        sendButton = view.findViewById(R.id.button_send);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        description = view.findViewById(R.id.description);
        loading = view.findViewById(R.id.progressBar2);
        constraintLayout = view.findViewById(R.id.container2);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (BluetoothChatActivity) requireActivity();
        WindowInsets windowInsets = activity.getFragmentContainer().getRootWindowInsets();
        if (windowInsets != null) {
            constraintLayout.dispatchApplyWindowInsets(windowInsets.replaceSystemWindowInsets(windowInsets.getSystemWindowInsetLeft(), windowInsets.getSystemWindowInsetTop(), windowInsets.getSystemWindowInsetRight(), 0));
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        layoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(layoutManager);
        smoothScroller = new LinearSmoothScroller(activity) {
            @Override
            protected int calculateTimeForScrolling(int dx) {
                return 100;
            }
        };

        mAdapter = new MessagesAdapter(OfflineDashboardActivity.getBluetoothCommunicator().getUniqueName(), () -> {
            description.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        });

        mRecyclerView.setAdapter(mAdapter);

        sendButton.setOnClickListener(v -> {
            if (OfflineDashboardActivity.getBluetoothCommunicator().getConnectedPeersList().size() > 0) {
                if (editText.getText().length() > 0) {
                    Message message = new Message(offlineDashboardActivity, "m", editText.getText().toString(), OfflineDashboardActivity.getBluetoothCommunicator().getConnectedPeersList().get(0));
                    OfflineDashboardActivity.getBluetoothCommunicator().sendMessage(message);
                    editText.setText("");
                    mAdapter.addMessage(message);
                    smoothScroller.setTargetPosition(mAdapter.getItemCount() - 1);
                    mRecyclerView.getLayoutManager().startSmoothScroll(smoothScroller);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        OfflineDashboardActivity.getBluetoothCommunicator().addCallback(communicatorCallback);
    }

    @Override
    public void onPause() {
        super.onPause();
        OfflineDashboardActivity.getBluetoothCommunicator().removeCallback(communicatorCallback);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void appearLoading() {
        int loadingSizePx = GuiTools.convertDpToPixels(activity, LOADING_SIZE_DP);
        CustomAnimator animator = new CustomAnimator();
        Animator animation = animator.createAnimatorSize(loading, 1, 1, loadingSizePx, loadingSizePx, getResources().getInteger(R.integer.durationShort));
        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if(loading != null) {
                    loading.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animation.start();
    }
}
