package com.example.andrey.myapplication2;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.andrey.myapplication2.Core.LocalStorage;

public class SignInFragment extends Fragment {

    public SignInFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_sign_in, container, false);
        return view;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        LocalStorage myLocalStorage=new LocalStorage(this.getActivity());
        ((EditText) view.findViewById(R.id.edit_email)).setText(myLocalStorage.GetEmail());
        ((EditText) view.findViewById(R.id.edit_pass)).setText(myLocalStorage.GetPassword());
    }



}
