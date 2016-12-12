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
import com.acsia.client.aidl.AudioManager;
import com.acsia.client.support.Constants;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AIDLSettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AIDLSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AIDLSettingsFragment extends Fragment {
   /* // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;*/

    private OnFragmentInteractionListener mListener;

    public AIDLSettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AIDLSettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AIDLSettingsFragment newInstance() {
        AIDLSettingsFragment fragment = new AIDLSettingsFragment();
       /*String param1, String param2 Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);*/
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
          /*  mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);*/
        }
    }

    SeekBar localVolumeSeekBar;
    Switch localMuteSwitch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_aidl_settings, container, false);

        localVolumeSeekBar = (SeekBar) view.findViewById(R.id.localSeekBar);
        localMuteSwitch = (Switch) view.findViewById(R.id.localMute);
        configureView();
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //things to do when fragment is visible
            configureView();
        }
    }

    private void configureView() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean success = false;
                try {

                    final int localMaxVolume = AudioManager.getMaximumVolume();
                    final int localVolume = AudioManager.getCurrentVolume();
                    final int localMuteStatus = AudioManager.isMute();

                    if (localMaxVolume != -1
                            && localVolume != -1
                            && localMuteStatus != -1) {
                        success = true;
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                //manage your edittext and Other UIs here
                                initSeekBar(localMaxVolume, localVolume);
                                initMuteSwitch(localMuteStatus);
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

                               /* Toast.makeText(getActivity(), "Failed to connect", Toast.LENGTH_SHORT);
                                getActivity().finish();*/
                            onButtonPressed("Failed to connect");

                        }
                    });
                }
            }
        }).start();
    }

    private void initMuteSwitch(int localMuteStatus) {
        boolean localMute = false;
        if (localMuteStatus == 0) {
            localMute = true;
        }
        if (localMuteSwitch != null) {
            localMuteSwitch.setChecked(localMute);
            localMuteSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean checked = ((Switch) view).isChecked();
                    new VolumeTask(checked, Constants.ACTION.MUTE_VOLUME).execute();
                }
            });
          /*  localMuteSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, final boolean checked) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            boolean success = AudioManager.muteAudio(checked);
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
            });*/
        }

    }

    private void initSeekBar(int localMaxVolume, int localVolume) {
        if (localVolumeSeekBar != null) {
            localVolumeSeekBar.setMax(localMaxVolume);
            localVolumeSeekBar.setKeyProgressIncrement(1);
            localVolumeSeekBar.setProgress(localVolume);
            localVolumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
                    if (fromUser) {
                        if (localMuteSwitch != null) {
                            if (localMuteSwitch.isChecked()) {
                                localMuteSwitch.performClick();
                            }
                        }
                        new VolumeTask(progress, Constants.ACTION.SET_VOLUME).execute();
                     /*
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                boolean success = AudioManager.setVolume(progress);
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

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }
    }

    class VolumeTask extends AsyncTask<Void, Void, Boolean> {

        Object value;
        Constants.ACTION action;
        int localVolume = 0;

        VolumeTask(Object value, Constants.ACTION action) {
            this.value = value;
            this.action = action;
            System.out.println("value = [" + value + "], action = [" + action + "]");
        }


        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean success;
            if (this.action == Constants.ACTION.SET_VOLUME) {
                success = AudioManager.setVolume((Integer) value);
            } else {
                success = AudioManager.muteAudio((Boolean) value);
                this.localVolume = AudioManager.getCurrentVolume();

            }
            return success;

        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (!success) {
                onButtonPressed("Failed to connect");
            } else {
                if (localVolumeSeekBar != null&&this.action != Constants.ACTION.SET_VOLUME) {
                    localVolumeSeekBar.setProgress(this.localVolume);
                }
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String uri) {
        if (mListener != null) {
            mListener.onRemoteFragmentInteraction(uri);
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
        void onRemoteFragmentInteraction(String status);
    }
}
