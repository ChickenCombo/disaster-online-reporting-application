package com.app.dorav4.bluetoothchat;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.app.dorav4.R;
import com.app.dorav4.activities.OfflineDashboardActivity;
import com.app.dorav4.bluetoothchat.fragments.ConversationFragment;
import com.app.dorav4.bluetoothchat.fragments.PairingFragment;
import com.app.dorav4.bluetoothchat.tools.Tools;
import com.bluetooth.communicator.BluetoothCommunicator;
import com.bluetooth.communicator.Peer;

import java.util.ArrayList;
import java.util.List;

public class BluetoothChatActivity extends AppCompatActivity {
    public static final int PAIRING_FRAGMENT = 0;
    public static final int CONVERSATION_FRAGMENT = 1;
    public static final int DEFAULT_FRAGMENT = PAIRING_FRAGMENT;
    public static final int NO_PERMISSIONS = -10;
    private static final int REQUEST_CODE_REQUIRED_PERMISSIONS = 2;
    public static final String[] REQUIRED_PERMISSIONS = new String[]{
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };
    private int currentFragment = -1;
    private ArrayList<Callback> clientsCallbacks = new ArrayList<>();
    private CoordinatorLayout fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_chat);

        // Change status bar color
        getWindow().setStatusBarColor(ContextCompat.getColor(BluetoothChatActivity.this, R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        // Clean fragments (only if the app is recreated (When user disable permission))
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        // Remove previous fragments (case of the app was restarted after changed permission on android 6 and higher)
        List<Fragment> fragmentList = fragmentManager.getFragments();
        for (Fragment fragment : fragmentList) {
            if (fragment != null) {
                fragmentManager.beginTransaction().remove(fragment).commit();
            }
        }

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        fragmentContainer = findViewById(R.id.fragment_container);

        OfflineDashboardActivity.getBluetoothCommunicator().addCallback(new BluetoothCommunicator.Callback() {
            @Override
            public void onAdvertiseStarted() {
                super.onAdvertiseStarted();
                if (OfflineDashboardActivity.getBluetoothCommunicator().isDiscovering()) {
                    notifySearchStarted();
                }
            }

            @Override
            public void onDiscoveryStarted() {
                super.onDiscoveryStarted();
                if (OfflineDashboardActivity.getBluetoothCommunicator().isAdvertising()) {
                    notifySearchStarted();
                }
            }

            @Override
            public void onAdvertiseStopped() {
                super.onAdvertiseStopped();
                if (!OfflineDashboardActivity.getBluetoothCommunicator().isDiscovering()) {
                    notifySearchStopped();
                }
            }

            @Override
            public void onDiscoveryStopped() {
                super.onDiscoveryStopped();
                if (!OfflineDashboardActivity.getBluetoothCommunicator().isAdvertising()) {
                    notifySearchStopped();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // when we return to the app's gui we choose which fragment to start based on connection status
        if (OfflineDashboardActivity.getBluetoothCommunicator().getConnectedPeersList().size() == 0) {
            setFragment(DEFAULT_FRAGMENT);
        } else {
            setFragment(CONVERSATION_FRAGMENT);
        }
    }

    public void setFragment(int fragmentName) {
        switch (fragmentName) {
            case PAIRING_FRAGMENT: {
                // possible setting of the fragment
                if (getCurrentFragment() != PAIRING_FRAGMENT) {
                    PairingFragment paringFragment = new PairingFragment();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                    transaction.replace(R.id.fragment_container, paringFragment);
                    transaction.commit();
                    currentFragment = PAIRING_FRAGMENT;
                }
                break;
            }
            case CONVERSATION_FRAGMENT: {
                // possible setting of the fragment
                if (getCurrentFragment() != CONVERSATION_FRAGMENT) {
                    ConversationFragment conversationFragment = new ConversationFragment();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    transaction.replace(R.id.fragment_container, conversationFragment);
                    transaction.commit();
                    currentFragment = CONVERSATION_FRAGMENT;
                }
                break;
            }
        }
    }

    public int getCurrentFragment() {
        if (currentFragment != -1) {
            return currentFragment;
        } else {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (currentFragment != null) {
                if (currentFragment.getClass().equals(PairingFragment.class)) {
                    return PAIRING_FRAGMENT;
                }
                if (currentFragment.getClass().equals(ConversationFragment.class)) {
                    return CONVERSATION_FRAGMENT;
                }
            }
        }
        return -1;
    }

    @Override
    public void onBackPressed() {
        DialogInterface.OnClickListener confirmExitListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                exitFromConversation();
            }
        };
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null) {
            if (fragment instanceof ConversationFragment) {
                showConfirmExitDialog(confirmExitListener);
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    public void exitFromConversation() {
        if (OfflineDashboardActivity.getBluetoothCommunicator().getConnectedPeersList().size() > 0) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (fragment instanceof ConversationFragment) {
                ConversationFragment conversationFragment = (ConversationFragment) fragment;
                conversationFragment.appearLoading();
            }
            OfflineDashboardActivity.getBluetoothCommunicator().disconnectFromAll();
        } else {
            setFragment(DEFAULT_FRAGMENT);
        }
    }

    protected void showConfirmExitDialog(DialogInterface.OnClickListener confirmListener) {
        //creazione del dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setMessage("Confirm exit");
        builder.setPositiveButton(android.R.string.ok, confirmListener);
        builder.setNegativeButton(android.R.string.cancel, null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public int startSearch() {
        if (OfflineDashboardActivity.getBluetoothCommunicator().isBluetoothLeSupported() == BluetoothCommunicator.SUCCESS) {
            if (Tools.hasPermissions(this, REQUIRED_PERMISSIONS)) {
                int advertisingCode = OfflineDashboardActivity.getBluetoothCommunicator().startAdvertising();
                int discoveringCode = OfflineDashboardActivity.getBluetoothCommunicator().startDiscovery();
                if (advertisingCode == discoveringCode) {
                    return advertisingCode;
                }
                if (advertisingCode == BluetoothCommunicator.BLUETOOTH_LE_NOT_SUPPORTED || discoveringCode == BluetoothCommunicator.BLUETOOTH_LE_NOT_SUPPORTED) {
                    return BluetoothCommunicator.BLUETOOTH_LE_NOT_SUPPORTED;
                }
                if (advertisingCode == BluetoothCommunicator.SUCCESS || discoveringCode == BluetoothCommunicator.SUCCESS) {
                    if (advertisingCode == BluetoothCommunicator.ALREADY_STARTED || discoveringCode == BluetoothCommunicator.ALREADY_STARTED) {
                        return BluetoothCommunicator.SUCCESS;
                    }
                }
                return BluetoothCommunicator.ERROR;
            } else {
                requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_REQUIRED_PERMISSIONS);
                return NO_PERMISSIONS;
            }
        } else {
            return BluetoothCommunicator.BLUETOOTH_LE_NOT_SUPPORTED;
        }
    }

    public int stopSearch(boolean tryRestoreBluetoothStatus) {
        int advertisingCode = OfflineDashboardActivity.getBluetoothCommunicator().stopAdvertising(tryRestoreBluetoothStatus);
        int discoveringCode = OfflineDashboardActivity.getBluetoothCommunicator().stopDiscovery(tryRestoreBluetoothStatus);
        if (advertisingCode == discoveringCode) {
            return advertisingCode;
        }
        if (advertisingCode == BluetoothCommunicator.BLUETOOTH_LE_NOT_SUPPORTED || discoveringCode == BluetoothCommunicator.BLUETOOTH_LE_NOT_SUPPORTED) {
            return BluetoothCommunicator.BLUETOOTH_LE_NOT_SUPPORTED;
        }
        if (advertisingCode == BluetoothCommunicator.SUCCESS || discoveringCode == BluetoothCommunicator.SUCCESS) {
            if (advertisingCode == BluetoothCommunicator.ALREADY_STOPPED || discoveringCode == BluetoothCommunicator.ALREADY_STOPPED) {
                return BluetoothCommunicator.SUCCESS;
            }
        }
        return BluetoothCommunicator.ERROR;
    }

    public boolean isSearching() {
        return OfflineDashboardActivity.getBluetoothCommunicator().isAdvertising() && OfflineDashboardActivity.getBluetoothCommunicator().isDiscovering();
    }

    public void connect(Peer peer) {
        stopSearch(false);
        OfflineDashboardActivity.getBluetoothCommunicator().connect(peer);
    }

    public void acceptConnection(Peer peer) {
        OfflineDashboardActivity.getBluetoothCommunicator().acceptConnection(peer);
    }

    public void rejectConnection(Peer peer) {
        OfflineDashboardActivity.getBluetoothCommunicator().rejectConnection(peer);
    }

    public int disconnect(Peer peer) {
        return OfflineDashboardActivity.getBluetoothCommunicator().disconnect(peer);
    }

    public CoordinatorLayout getFragmentContainer() {
        return fragmentContainer;
    }



    public void addCallback(Callback callback) {
        // in this way the listener will listen to both this activity and the communicatorexample
        OfflineDashboardActivity.getBluetoothCommunicator().addCallback(callback);
        clientsCallbacks.add(callback);
    }

    public void removeCallback(Callback callback) {
        OfflineDashboardActivity.getBluetoothCommunicator().removeCallback(callback);
        clientsCallbacks.remove(callback);
    }

    private void notifyMissingSearchPermission() {
        for (int i = 0; i < clientsCallbacks.size(); i++) {
            clientsCallbacks.get(i).onMissingSearchPermission();
        }
    }

    private void notifySearchPermissionGranted() {
        for (int i = 0; i < clientsCallbacks.size(); i++) {
            clientsCallbacks.get(i).onSearchPermissionGranted();
        }
    }

    private void notifySearchStarted() {
        for (int i = 0; i < clientsCallbacks.size(); i++) {
            clientsCallbacks.get(i).onSearchStarted();
        }
    }

    private void notifySearchStopped() {
        for (int i = 0; i < clientsCallbacks.size(); i++) {
            clientsCallbacks.get(i).onSearchStopped();
        }
    }

    public static class Callback extends BluetoothCommunicator.Callback {
        public void onSearchStarted() {
        }

        public void onSearchStopped() {
        }

        public void onMissingSearchPermission() {
        }

        public void onSearchPermissionGranted() {
        }
    }
}