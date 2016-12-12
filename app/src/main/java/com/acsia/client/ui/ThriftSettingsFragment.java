package com.acsia.client.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;

import com.acsia.client.R;
import com.acsia.client.support.ClientApplication;
import com.acsia.client.support.Constants;
import com.acsia.client.thrift.AudioManager;
import com.acsia.client.thrift.PollingService;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ThriftSettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ThriftSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ThriftSettingsFragment extends Fragment {
   /* // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;*/

    private OnFragmentInteractionListener mListener;

    public ThriftSettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ThriftSettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ThriftSettingsFragment newInstance() {
        ThriftSettingsFragment fragment = new ThriftSettingsFragment();
      /* String param1, String param2 Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);*/
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            /*mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);*/
        }
    }

    SeekBar localVolumeSeekBar;
    SeekBar remoteVolumeSeekBar;
    Switch localMuteSwitch;
    Switch remoteMuteSwitch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("ThriftSettingsFragment.onCreateView");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_thrift_settings, container, false);
        localVolumeSeekBar = (SeekBar) view.findViewById(R.id.localSeekBar);
        remoteVolumeSeekBar = (SeekBar) view.findViewById(R.id.remoteSeekBar);
        localMuteSwitch = (Switch) view.findViewById(R.id.localMute);
        remoteMuteSwitch = (Switch) view.findViewById(R.id.remoteMute);

        configureView();
        return view;
    }



    private void configureView() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean success = false;
                try {
                    final int localMaxVolume = AudioManager.getMaximumVolume(AudioManager.Device.LOCAL);
                    final int localVolume = AudioManager.getCurrentVolume(AudioManager.Device.LOCAL);
                    final int localMuteStatus = AudioManager.isMute(AudioManager.Device.LOCAL);
                    final int remoteMaxVolume = AudioManager.getMaximumVolume(AudioManager.Device.REMOTE);
                    final int remoteVolume = AudioManager.getCurrentVolume(AudioManager.Device.REMOTE);
                    final int remoteMuteStatus = AudioManager.isMute(AudioManager.Device.REMOTE);

                    if (localMaxVolume != -1 && remoteMaxVolume != -1
                            && localVolume != -1 && remoteVolume != -1
                            && localMuteStatus != -1 && remoteMuteStatus != -1) {
                        success = true;
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                //manage your edittext and Other UIs here
                                configSeekBar(localMaxVolume, localVolume, remoteMaxVolume, remoteVolume);
                                configureMuteSwitch(localMuteStatus, remoteMuteStatus);
                            }
                        });
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!success) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            onButtonPressed("Failed to connect");
                        }
                    });
                }
            }
        }).start();
    }

    private void configureMuteSwitch(int localMuteStatus, int remoteMuteStatus) {
        initMuteSwitch(localMuteStatus, remoteMuteStatus);
        configureLocalMuteSwitch();
        configureRemoteMuteSwitch();
    }

    private void configureRemoteMuteSwitch() {
        if (remoteMuteSwitch != null) {
            remoteMuteSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean checked = ((Switch) view).isChecked();
                    new VolumeTask(checked, AudioManager.Device.REMOTE, Constants.ACTION.MUTE_VOLUME).execute();
                }
            });
        /*    remoteMuteSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, final boolean checked) {
                    new VolumeTask(checked, AudioManager.Device.REMOTE, Constants.ACTION.MUTE_VOLUME).execute();
                }
            });*/
        }
    }

    private void configureLocalMuteSwitch() {
        if (localMuteSwitch != null) {
            localMuteSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean checked = ((Switch) view).isChecked();
                    new VolumeTask(checked, AudioManager.Device.LOCAL, Constants.ACTION.MUTE_VOLUME).execute();
                }
            });
            /*localMuteSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, final boolean checked) {
                    new VolumeTask(checked, AudioManager.Device.LOCAL, Constants.ACTION.MUTE_VOLUME).execute();
                }
            });*/
        }
    }

    private void initMuteSwitch(int localMuteStatus, int remoteMuteStatus) {
        boolean localMute = false, remoteMute = false;
        if (localMuteStatus == 0) {
            localMute = true;
        }
        if (remoteMuteStatus == 0) {
            remoteMute = true;
        }
        if (localMuteSwitch != null) {
            localMuteSwitch.setChecked(localMute);
        }
        if (remoteMuteSwitch != null) {
            remoteMuteSwitch.setChecked(remoteMute);
        }
    }

    private void configSeekBar(int localMaxVolume, int localVolume, int remoteMaxVolume, int remoteVolume) {
        initSeekBar(localMaxVolume, localVolume, remoteMaxVolume, remoteVolume);
        configureLocalSeekBar();
        configureRemoteSeekBar();
    }

    private void configureRemoteSeekBar() {
        if (remoteVolumeSeekBar != null) {
            remoteVolumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
                    if (fromUser) {
                        if (remoteMuteSwitch != null) {
                            if (remoteMuteSwitch.isChecked()) {
                                remoteMuteSwitch.performClick();
                            }
                        }
                        new VolumeTask(progress, AudioManager.Device.REMOTE, Constants.ACTION.SET_VOLUME).execute();
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }
    }

    private void configureLocalSeekBar() {
        if (localVolumeSeekBar != null) {
            localVolumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
                    System.out.println("ThriftSettingsFragment.onProgressChanged");
                    if (fromUser) {
                        if (localMuteSwitch != null) {
                            if (localMuteSwitch.isChecked()) {
                                localMuteSwitch.performClick();
                            }
                        }
                        new VolumeTask(progress, AudioManager.Device.LOCAL, Constants.ACTION.SET_VOLUME).execute();
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    System.out.println("ThriftSettingsFragment.onStartTrackingTouch");
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    System.out.println("ThriftSettingsFragment.onStopTrackingTouch");
                }
            });
        }
    }

    private void initSeekBar(int localMaxVolume, int localVolume, int remoteMaxVolume, int remoteVolume) {
        if (localVolumeSeekBar != null) {
            localVolumeSeekBar.setMax(localMaxVolume);
            localVolumeSeekBar.setKeyProgressIncrement(1);
            localVolumeSeekBar.setProgress(localVolume);
        }
        if (remoteVolumeSeekBar != null) {
            remoteVolumeSeekBar.setMax(remoteMaxVolume);
            remoteVolumeSeekBar.setKeyProgressIncrement(1);
            remoteVolumeSeekBar.setProgress(remoteVolume);
        }
    }

    class VolumeTask extends AsyncTask<Void, Void, Boolean> {

        Object value;
        AudioManager.Device device;
        Constants.ACTION action;

        VolumeTask(Object value, AudioManager.Device device, Constants.ACTION action) {
            this.device = device;
            this.value = value;
            this.action = action;
            System.out.println("value = [" + value + "], device = [" + device + "], action = [" + action + "]");
        }


        @Override
        protected Boolean doInBackground(Void... voids) {
            if (this.action == Constants.ACTION.SET_VOLUME) {
                return AudioManager.setVolume((Integer) value, device);
            } else {
                return AudioManager.muteAudio((Boolean) value, device);
            }

        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (!success) {
                onButtonPressed("Failed to connect");
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String uri) {
        if (mListener != null) {
            mListener.onLocalFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ClientApplication.getLocalBroadcastManager().registerReceiver(mYourBroadcastReceiver,
                new IntentFilter(Constants.THRIFT_RECEIVER));
        PollingService.startPollingService(getActivity());
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        ClientApplication.getLocalBroadcastManager().unregisterReceiver(mYourBroadcastReceiver);
        PollingService.stopPollingService(getActivity());
    }

    private final BroadcastReceiver mYourBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean success = intent.getBooleanExtra(Constants.THRIFT_DATA_SUCCESS, false);
            // System.out.println("ThriftSettingsFragment.onReceive::success = " + success);
            if (success) {
                int localMaxVolume = intent.getIntExtra(Constants.THRIFT_DATA_LOCAL_MAX_VOLUME, -1);
                int localVolume = intent.getIntExtra(Constants.THRIFT_DATA_LOCAL_VOLUME, -1);
                int localMuteStatus = intent.getIntExtra(Constants.THRIFT_DATA_LOCAL_MUTE_STATUS, -1);
                int remoteMaxVolume = intent.getIntExtra(Constants.THRIFT_DATA_REMOTE_MAX_VOLUME, -1);
                int remoteVolume = intent.getIntExtra(Constants.THRIFT_DATA_REMOTE_VOLUME, -1);
                int remoteMuteStatus = intent.getIntExtra(Constants.THRIFT_DATA_REMOTE_MUTE_STATUS, -1);
                initMuteSwitch(localMuteStatus, remoteMuteStatus);
                initSeekBar(localMaxVolume, localVolume, remoteMaxVolume, remoteVolume);
            } else {
                onButtonPressed("Failed to connect");
            }
        }
    };

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onLocalFragmentInteraction(String uri);
    }
}
