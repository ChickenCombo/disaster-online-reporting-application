package com.app.dorav4.bluetoothchat.fragments;

import android.animation.Animator;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.app.dorav4.R;
import com.app.dorav4.activities.OfflineDashboardActivity;
import com.app.dorav4.bluetoothchat.BluetoothChatActivity;
import com.app.dorav4.bluetoothchat.gui.ButtonSearch;
import com.app.dorav4.bluetoothchat.gui.CustomAnimator;
import com.app.dorav4.bluetoothchat.gui.GuiTools;
import com.app.dorav4.bluetoothchat.gui.PeerListAdapter;
import com.app.dorav4.bluetoothchat.gui.RequestDialog;
import com.app.dorav4.bluetoothchat.tools.Tools;
import com.bluetooth.communicator.BluetoothCommunicator;
import com.bluetooth.communicator.Peer;
import com.bluetooth.communicator.tools.Timer;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class PairingFragment extends Fragment {
    public static final int CONNECTION_TIMEOUT = 5000;
    private RequestDialog connectionRequestDialog;
    private RequestDialog connectionConfirmDialog;
    private ConstraintLayout constraintLayout;
    private Peer confirmConnectionPeer;
    private ListView listViewGui;
    private Timer connectionTimer;
    @Nullable
    private PeerListAdapter listView;
    private TextView noDevices;
    private TextView noPermissions;
    private TextView noBluetoothLe;
    private final Object lock = new Object();
    private BluetoothChatActivity.Callback communicatorCallback;
    private CustomAnimator animator = new CustomAnimator();
    private Peer connectingPeer;
    protected OfflineDashboardActivity offlineDashboardActivity;
    protected BluetoothChatActivity activity;
    private static final float LOADING_SIZE_DP = 24;
    protected boolean isLoadingVisible = false;
    private boolean appearSearchButton = false;
    protected boolean isLoadingAnimating;
    protected ButtonSearch buttonSearch;
    private ProgressBar loading;
    private ArrayList<CustomAnimator.EndListener> listeners = new ArrayList<>();

    public PairingFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        communicatorCallback = new BluetoothChatActivity.Callback() {
            @Override
            public void onSearchStarted() {
                buttonSearch.setSearching(true, animator);
            }

            @Override
            public void onSearchStopped() {
                buttonSearch.setSearching(false, animator);
            }

            @Override
            public void onConnectionRequest(final Peer peer) {
                super.onConnectionRequest(peer);
                if (peer != null) {
                    String time = DateFormat.getDateTimeInstance().format(new Date());
                    connectionRequestDialog = new RequestDialog(activity, "Accept connection request from " + peer.getName() + " ?", 15000, (dialog, which) -> activity.acceptConnection(peer), (dialog, which) -> activity.rejectConnection(peer));
                    connectionRequestDialog.setOnCancelListener(dialog -> connectionRequestDialog = null);
                    connectionRequestDialog.show();
                }
            }

            @Override
            public void onConnectionSuccess(Peer peer, int source) {
                super.onConnectionSuccess(peer, source);
                connectingPeer = null;
                resetConnectionTimer();
                activity.setFragment(BluetoothChatActivity.CONVERSATION_FRAGMENT);
            }

            @Override
            public void onConnectionFailed(Peer peer, int errorCode) {
                super.onConnectionFailed(peer, errorCode);
                if (connectingPeer != null) {
                    if (connectionTimer != null && !connectionTimer.isFinished() && errorCode != BluetoothCommunicator.CONNECTION_REJECTED) {
                        // the timer has not expired and the connection has not been refused, so we try again
                        activity.connect(peer);
                    } else {
                        // the timer has expired, so the failure is notified
                        clearFoundPeers();
                        startSearch();
                        activateInputs();
                        disappearLoading(true, null);
                        connectingPeer = null;
                        if (errorCode == BluetoothCommunicator.CONNECTION_REJECTED) {
                            Toast.makeText(activity, peer.getName() + " refused the connection request", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(activity, "Connection error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onPeerFound(Peer peer) {
                super.onPeerFound(peer);
                synchronized (lock) {
                    if (listView != null) {
                        BluetoothAdapter bluetoothAdapter = OfflineDashboardActivity.getBluetoothCommunicator().getBluetoothAdapter();
                        int index = listView.indexOfPeer(peer.getUniqueName());
                        if (index == -1) {
                            listView.add(peer);
                        } else {
                            Peer peer1 = listView.get(index);
                            if (peer.isBonded(bluetoothAdapter)) {
                                listView.set(index, peer);
                            } else if (peer1.isBonded(bluetoothAdapter)) {
                                listView.set(index, listView.get(index));
                            } else {
                                listView.set(index, peer);
                            }
                        }
                    }
                }
            }

            @Override
            public void onPeerUpdated(Peer peer, Peer newPeer) {
                super.onPeerUpdated(peer, newPeer);
                onPeerFound(newPeer);
            }

            @Override
            public void onPeerLost(Peer peer) {
               synchronized (lock) {
                    if (listView != null) {
                        listView.remove(peer);
                        if (peer.equals(getConfirmConnectionPeer())) {
                            RequestDialog requestDialog = getConnectionConfirmDialog();
                            if (requestDialog != null) {
                                requestDialog.cancel();
                            }
                        }
                    }
                }
            }

            @Override
            public void onBluetoothLeNotSupported() {

            }

            @Override
            public void onMissingSearchPermission() {
                super.onMissingSearchPermission();
                clearFoundPeers();
                if (noPermissions.getVisibility() != View.VISIBLE) {
                    // appearance of the written of missing permission
                    listViewGui.setVisibility(View.GONE);
                    noDevices.setVisibility(View.GONE);
                    noPermissions.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSearchPermissionGranted() {
                super.onSearchPermissionGranted();
                if (noPermissions.getVisibility() == View.VISIBLE) {
                    // disappearance of the written of missing permission
                    noPermissions.setVisibility(View.GONE);
                    noDevices.setVisibility(View.VISIBLE);
                    initializePeerList();
                } else {
                    //reset list view
                    clearFoundPeers();
                }
                startSearch();
            }
        };
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pairing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        constraintLayout = view.findViewById(R.id.container);
        listViewGui = view.findViewById(R.id.list_view);
        noDevices = view.findViewById(R.id.noDevices);
        noPermissions = view.findViewById(R.id.noPermission);
        noBluetoothLe = view.findViewById(R.id.noBluetoothLe);
        buttonSearch = view.findViewById(R.id.searchButton);
        loading = view.findViewById(R.id.progressBar2);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (BluetoothChatActivity) requireActivity();
        WindowInsets windowInsets = activity.getFragmentContainer().getRootWindowInsets();
        if (windowInsets != null) {
            constraintLayout.dispatchApplyWindowInsets(windowInsets.replaceSystemWindowInsets(windowInsets.getSystemWindowInsetLeft(), windowInsets.getSystemWindowInsetTop(), windowInsets.getSystemWindowInsetRight(), 0));
        }

        initializePeerList();
        listViewGui.setOnItemClickListener((adapterView, view, i, l) -> {
            synchronized (lock) {
                if (listView != null) {
                    if (listView.isClickable()) {
                        Peer item = listView.get(i);
                        connect(item);
                    } else {
                        listView.getCallback().onClickNotAllowed(listView.getShowToast());
                    }
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        activateInputs();
        disappearLoading(true, null);
        if (!Tools.hasPermissions(activity, BluetoothChatActivity.REQUIRED_PERMISSIONS)) {
            startSearch();
        }

        buttonSearch.setOnClickListener(v -> {
            if (activity.isSearching()) {
                activity.stopSearch(false);
                clearFoundPeers();
            } else {
                startSearch();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        clearFoundPeers();

        activity.addCallback(communicatorCallback);
        // if you have permission to search it is activated from here
        if (Tools.hasPermissions(activity, BluetoothChatActivity.REQUIRED_PERMISSIONS)) {
            startSearch();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        activity.removeCallback(communicatorCallback);
        stopSearch();
        //communicatorCallback.onSearchStopped();
        if (connectingPeer != null) {
            activity.disconnect(connectingPeer);
            connectingPeer = null;
        }
    }

    private void connect(final Peer peer) {
        connectingPeer = peer;
        confirmConnectionPeer = peer;
        connectionConfirmDialog = new RequestDialog(activity, "Are you sure to connect with " + peer.getName() + "?", (dialog, which) -> {
            deactivateInputs();
            appearLoading(null);
            activity.connect(peer);
            startConnectionTimer();
        }, null);
        connectionConfirmDialog.setOnCancelListener(dialog -> {
            confirmConnectionPeer = null;
            connectionConfirmDialog = null;
        });
        connectionConfirmDialog.show();
    }

    protected void startSearch() {
        int result = activity.startSearch();
        if (result != BluetoothCommunicator.SUCCESS) {
            if (result == BluetoothCommunicator.BLUETOOTH_LE_NOT_SUPPORTED && noBluetoothLe.getVisibility() != View.VISIBLE) {
                listViewGui.setVisibility(View.GONE);
                noDevices.setVisibility(View.GONE);
                noBluetoothLe.setVisibility(View.VISIBLE);
            } else if (result != BluetoothChatActivity.NO_PERMISSIONS && result != BluetoothCommunicator.ALREADY_STARTED) {
                MotionToast.Companion.darkToast(
                        requireActivity(),
                        "Error",
                        "Cannot search for devices",
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(requireActivity(), R.font.helvetica_regular)
                );
            }
        }
    }

    private void stopSearch() {
        activity.stopSearch(connectingPeer == null);
    }

    private void activateInputs() {
        //click reactivation of listView
        setListViewClickable(true, true);
    }

    private void deactivateInputs() {
        //click deactivation of listView
        setListViewClickable(false, true);
    }

    public Peer getConfirmConnectionPeer() {
        return confirmConnectionPeer;
    }

    public RequestDialog getConnectionConfirmDialog() {
        return connectionConfirmDialog;
    }

    private void startConnectionTimer() {
        connectionTimer = new Timer(CONNECTION_TIMEOUT);
        connectionTimer.start();
    }

    private void resetConnectionTimer() {
        if (connectionTimer != null) {
            connectionTimer.cancel();
            connectionTimer = null;
        }
    }

    private void initializePeerList() {
        final PeerListAdapter.Callback callback = new PeerListAdapter.Callback() {
            @Override
            public void onFirstItemAdded() {
                super.onFirstItemAdded();
                noDevices.setVisibility(View.GONE);
                listViewGui.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLastItemRemoved() {
                super.onLastItemRemoved();
                listViewGui.setVisibility(View.GONE);
                if (noPermissions.getVisibility() != View.VISIBLE) {
                    noDevices.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onClickNotAllowed(boolean showToast) {
                super.onClickNotAllowed(showToast);
                Toast.makeText(activity, "Cannot interact with devices during connection", Toast.LENGTH_SHORT).show();
            }
        };

        listView = new PeerListAdapter(activity, new ArrayList<>(), callback);
        listViewGui.setAdapter(listView);
    }

    public void clearFoundPeers() {
        if (listView != null) {
            listView.clear();
        }
    }

    public void setListViewClickable(boolean isClickable, boolean showToast) {
        if (listView != null) {
            listView.setClickable(isClickable, showToast);
        }
    }

    public void appearLoading(@Nullable CustomAnimator.EndListener responseListener) {
        if (responseListener != null) {
            listeners.add(responseListener);
        }
        isLoadingVisible = true;
        if (!isLoadingAnimating) {
            if (loading.getVisibility() != View.VISIBLE) {
                isLoadingAnimating = true;
                buttonSearch.setVisible(false, new CustomAnimator.EndListener() {
                    @Override
                    public void onAnimationEnd() {
                        int loadingSizePx = GuiTools.convertDpToPixels(activity, LOADING_SIZE_DP);
                        Animator animation = animator.createAnimatorSize(loading, 1, 1, loadingSizePx, loadingSizePx, getResources().getInteger(R.integer.durationShort));
                        animation.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                loading.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                isLoadingAnimating = false;
                                if (!isLoadingVisible) {
                                    disappearLoading(appearSearchButton, null);
                                } else {
                                    notifyLoadingAnimationEnd();
                                }
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
                });
            } else {
                notifyLoadingAnimationEnd();
            }
        }
    }

    public void disappearLoading(final boolean appearSearchButton, @Nullable CustomAnimator.EndListener responseListener) {
        if (responseListener != null) {
            listeners.add(responseListener);
        }
        this.isLoadingVisible = false;
        this.appearSearchButton = appearSearchButton;
        if (!isLoadingAnimating) {
            if (loading.getVisibility() != View.GONE) {  // if the object has not already disappeared graphically
                // animation execution
                isLoadingAnimating = true;
                int loadingSizePx = GuiTools.convertDpToPixels(activity, LOADING_SIZE_DP);
                Animator animation = animator.createAnimatorSize(loading, loadingSizePx, loadingSizePx, 1, 1, getResources().getInteger(R.integer.durationShort));
                animation.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        loading.setVisibility(View.GONE);
                        CustomAnimator.EndListener listener = new CustomAnimator.EndListener() {
                            @Override
                            public void onAnimationEnd() {
                                isLoadingAnimating = false;
                                if (isLoadingVisible) {   // if isLoadingVisible has changed in the meantime
                                    appearLoading(null);
                                } else {
                                    notifyLoadingAnimationEnd();
                                }
                            }
                        };
                        if (appearSearchButton) {
                            buttonSearch.setVisible(true, listener);
                        } else {
                            listener.onAnimationEnd();
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
                animation.start();
            } else {
                notifyLoadingAnimationEnd();
            }
        }
    }

    private void notifyLoadingAnimationEnd() {
        // notify finished animation and elimination of listeners
        while (listeners.size() > 0) {
            listeners.remove(0).onAnimationEnd();
        }
    }
}