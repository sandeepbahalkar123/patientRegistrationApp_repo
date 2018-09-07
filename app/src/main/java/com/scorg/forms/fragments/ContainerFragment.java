package com.scorg.forms.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scorg.forms.R;
import com.scorg.forms.activities.PersonalInfoActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContainerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContainerFragment extends Fragment {

    private OnContainerLoadListener mListener;

    public ContainerFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ContainerFragment newInstance() {
        return new ContainerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_container, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnContainerLoadListener) {
            mListener = (OnContainerLoadListener) context;
            mListener.generateForm();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnContainerLoadListener");
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
    public interface OnContainerLoadListener {
        void generateForm();
    }

}
