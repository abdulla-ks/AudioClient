package com.acsia.client.ui;

import android.content.Context;
import android.net.Uri;
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
import android.widget.Toast;

import com.acsia.client.R;
import com.acsia.client.thrift.AudioManager;

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
                                initSeekBar(localMaxVolume, localVolume, remoteMaxVolume, remoteVolume);
                                initMuteSwitch(localMuteStatus, remoteMuteStatus);
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
            localMuteSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, final boolean checked) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            boolean success = AudioManager.muteAudio(checked, AudioManager.Device.LOCAL);
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
            });
        }
        if (remoteMuteSwitch != null) {
            remoteMuteSwitch.setChecked(remoteMute);
            remoteMuteSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, final boolean checked) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            boolean success = AudioManager.muteAudio(checked, AudioManager.Device.REMOTE);
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
            });
        }
    }

    static int localVolume;

    private void initSeekBar(int localMaxVolume, int localVolume, int remoteMaxVolume, int remoteVolume) {
        if (localVolumeSeekBar != null) {
            localVolumeSeekBar.setMax(localMaxVolume);
            localVolumeSeekBar.setKeyProgressIncrement(1);
            localVolumeSeekBar.setProgress(localVolume);
            ThriftSettingsFragment.localVolume = localVolume;
            localVolumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                boolean track = false;

                @Override
                public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
                    System.out.println("ThriftSettingsFragment.onProgressChanged");
                    if (fromUser) {
                        if (localMuteSwitch != null) {
                            if (localMuteSwitch.isChecked()) {
                                localMuteSwitch.performClick();
                            }
                        }
                        new SetVolumeTask(progress, AudioManager.Device.LOCAL).execute();
                     /*   new Thread(new Runnable() {
                            @Override
                            public void run() {
                                boolean success = false;
                                try {
                                    success = AudioManager.setVolume(progress, AudioManager.Device.LOCAL);
                                } catch (Throwable e) {
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
                        }).start();*/
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

        if (remoteVolumeSeekBar != null) {
            remoteVolumeSeekBar.setMax(remoteMaxVolume);
            remoteVolumeSeekBar.setKeyProgressIncrement(1);
            remoteVolumeSeekBar.setProgress(remoteVolume);
            remoteVolumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
                    if (fromUser) {
                        if (remoteMuteSwitch != null) {
                            if (remoteMuteSwitch.isChecked()) {
                                remoteMuteSwitch.performClick();
                            }
                        }
                        new SetVolumeTask(progress, AudioManager.Device.REMOTE).execute();
                        /*new Thread(new Runnable() {
                            @Override
                            public void run() {
                                boolean success = false;
                                try {
                                    success = AudioManager.setVolume(progress, AudioManager.Device.REMOTE);
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                }
                                if (!success) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            onButtonPressed("Failed to connect");
                                            *//*   if (getActivity()!=null) {
                                             Toast.makeText(getActivity(), "Failed to connect", Toast.LENGTH_SHORT);
                                                getActivity().finish();
                                            }*//*
                                        }
                                    });
                                }
                            }
                        }).start();*/
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

    class SetVolumeTask extends AsyncTask<Void, Void, Boolean> {
        int progress;
        AudioManager.Device device;

        SetVolumeTask(int progress, AudioManager.Device device) {
            this.device = device;
            this.progress = progress;
        }


        @Override
        protected Boolean doInBackground(Void... voids) {
            return AudioManager.setVolume(progress, device);
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
    }

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
