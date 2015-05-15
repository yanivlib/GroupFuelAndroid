package com.mty.groupfuel;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UsageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UsageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UsageFragment extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UsageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UsageFragment newInstance(String param1, String param2) {
        UsageFragment fragment = new UsageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public UsageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_usage, container, false);
        TextView textView = (TextView) view.findViewById(R.id.usage_text);

        getCarMakes();
        getCarModels("Audi");
        getOwnedCars();
        getOwnedCars_2();

        textView.setText("This is the new text\n");
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

//    @Override
 //   public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    void getCarMakes() {
        ParseCloud.callFunctionInBackground("getCarMakes", new HashMap<String, Object>(), new FunctionCallback<ArrayList>() {
            @Override
            public void done(ArrayList arrayList, ParseException e) {
                if (e == null) {
                    for(int i = 0; i < arrayList.size(); i++) {
                        ParseObject parseObject = (ParseObject) arrayList.get(i);

                        System.out.println("getCarMakes: ");
                        System.out.println("as an array list, item (" + i + "): " + arrayList.get(i));
                        System.out.println("make is: " + parseObject.getString("Make"));
                    }

                    System.out.println("got a response from parse cloud: " + arrayList.toString());
                    Toast.makeText(getActivity().getBaseContext(), arrayList.toString(), Toast.LENGTH_LONG).show();
                    //Toast.makeText(getBaseContext(), mapObject.toString(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity().getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    System.out.println(e.getMessage());
                }
            }
        });
    }

    void getOwnedCars() {
        ParseCloud.callFunctionInBackground("getOwnedCars", new HashMap<String, Object>(), new FunctionCallback<ArrayList>() {
            @Override
            public void done(ArrayList arrayList, ParseException e) {
                if (e == null) {
                    for(int i = 0; i < arrayList.size(); i++) {
                        ParseObject parseObject = (ParseObject) arrayList.get(i);

                        System.out.println("getOwnedCars: ");
                        System.out.println("as an array list, item (" + i + "): " + arrayList.get(i));
                        System.out.println("object is: " + parseObject.toString());

                    }

                    System.out.println("got a response from parse cloud: " + arrayList.toString());
                    Toast.makeText(getActivity().getBaseContext(), arrayList.toString(), Toast.LENGTH_LONG).show();
                    //Toast.makeText(getBaseContext(), mapObject.toString(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity().getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    System.out.println(e.getMessage());
                }
            }
        });
    }

    void getOwnedCars_2() {
        ParseCloud.callFunctionInBackground("getOwnedCars_2", new HashMap<String, Object>(), new FunctionCallback<ArrayList>() {
            @Override
            public void done(ArrayList arrayList, ParseException e) {
                if (e == null) {
                    for(int i = 0; i < arrayList.size(); i++) {
                        ParseObject parseObject = (ParseObject) arrayList.get(i);

                        System.out.println("getOwnedCars_2: ");
                        System.out.println("as an array list, item (" + i + "): " + arrayList.get(i));
                        System.out.println("object is: " + parseObject.toString());

                    }

                    System.out.println("got a response from parse cloud: " + arrayList.toString());
                    Toast.makeText(getActivity().getBaseContext(), arrayList.toString(), Toast.LENGTH_LONG).show();
                    //Toast.makeText(getBaseContext(), mapObject.toString(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity().getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    System.out.println(e.getMessage());
                }
            }
        });
    }

    void getCarModels(String make) {
        final Map<String, String> params = new HashMap<>();
        params.put("Make", make);
        ParseCloud.callFunctionInBackground("getCarModels", params, new FunctionCallback<ArrayList>() {
            @Override
            public void done(ArrayList arrayList, ParseException e) {
                if (e == null) {
                    for(int i = 0; i < arrayList.size(); i++) {
                        ParseObject parseObject = (ParseObject) arrayList.get(i);
                        System.out.println("as an array list, item (" + i + "): " + arrayList.get(i));
                        System.out.println("object is: " + parseObject.toString());

                    }

                    System.out.println("got a response from parse cloud: " + arrayList.toString());
                    Toast.makeText(getActivity().getBaseContext(), arrayList.toString(), Toast.LENGTH_LONG).show();
                    //Toast.makeText(getBaseContext(), mapObject.toString(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity().getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    System.out.println(e.getMessage());
                }
            }
        });
    }
}
