package com.mty.groupfuel;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.mty.groupfuel.datamodel.Car;
import com.parse.LogOutCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsListAdapter extends BaseExpandableListAdapter{
    private final int CHILD_TYPE_COUNT = 4;
    private Context context;
    private List<String> listDataHeader;
    private Map<String, List<Object>> listDataChild;
    private List<CheckBox> checkBoxArrayList;

    public SettingsListAdapter(Context context, List<String> listDataHeader, Map<String, List<Object>> listDataChild) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listDataChild;
        this.checkBoxArrayList = new ArrayList<>();
    }

    public static View.OnClickListener signOut(final Context context) {
        return new View.OnClickListener() {
            ProgressDialog progress;

            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setMessage("Are you sure you want to log out?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                progress = ProgressDialog.show(context, "Logging you out", "Please Wait...");
                                ParseUser.logOutInBackground(new LogOutCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        progress.dismiss();
                                        context.startActivity(new Intent(context, DispatchActivity.class));
                                    }
                                });
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        };
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final Object child = getChild(groupPosition, childPosition);

        int type = getChildType(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(resourceFromType(type), null);
        }

        switch (getChildType(groupPosition, childPosition)) {
            case 0: // settings_addremove
                Button addNew = (Button)convertView.findViewById(R.id.add_new_car);
                addNew.setOnClickListener(addNewCar(this.context));
                Button remove = (Button)convertView.findViewById(R.id.remove_car);
                remove.setOnClickListener(removeCar(this.context));
                break;
            case 1: // settings_car
                Car car = (Car)child;
                TextView carText = (TextView) convertView.findViewById(R.id.car_text);
                carText.setText(((Car) child).getDisplayName());
                CheckBox cb = (CheckBox)convertView.findViewById(R.id.car_cb);
                cb.setTag(car);
                checkBoxArrayList.add((CheckBox)convertView.findViewById(R.id.car_cb));
                break;
            case 2: // settings_item
                TextView txtListChild = (TextView) convertView.findViewById(R.id.lblListItem_text);
                txtListChild.setText((String)child);
                break;
            case 3: // settings_button
                Button button = (Button) convertView.findViewById(R.id.settings_button);
                if (child == Consts.BUTTON_LOGOUT) {
                    button.setOnClickListener(signOut(context));
                    button.setText(context.getString(R.string.logout));
                } else if (child == Consts.BUTTON_UPDATE) {
                    button.setOnClickListener(updatePersonal(context));
                    button.setText(context.getString(R.string.change_personal));
                } else if (child == Consts.BUTTON_FACEBOOK) {
                    button.setOnClickListener(linkFacebook(ParseUser.getCurrentUser(), context));
                    button.setText((context.getString(R.string.link_facebook)));
                }
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public int getGroupCount() {
        return listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listDataChild.get(listDataHeader.get(groupPosition)).size();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listDataHeader.get(groupPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final String headerTitle = (String)getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater)this
                .context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.settings_group, null);
        }
        TextView lblListHeader = (TextView)convertView.findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public int getChildTypeCount() {
        return CHILD_TYPE_COUNT;
    }

    @Override
    public int getChildType(int groupPosition, int childPosition) {
        switch (groupPosition) {
            case 0:
                if ((getChildrenCount(groupPosition) - 1) == childPosition) {
                    return 0;
                } else {
                    return 1;
                }
            case 2:
                return 3;
            default:
                return 2;
        }
    }

    public int resourceFromType(int type) {
        switch (type) {
            case 0:
                return R.layout.settings_addremove;
            case 1:
                return R.layout.settings_car;
            case 2:
                return R.layout.settings_item;
            case 3:
                return R.layout.settings_button;
            default:
                return R.layout.settings_item;
        }
    }

    private View.OnClickListener addNewCar(final Context context) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final MainActivity activity = (MainActivity) context;
                FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, new AddCarFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        };
    }

    private View.OnClickListener removeCar(final Context context) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (CheckBox checkBox : checkBoxArrayList) {
                    if (checkBox.isChecked()) {
                        Car car = (Car)checkBox.getTag();
                        //car.deleteEventually();
                        Map<String, String> params = new HashMap();
                        params.put("carNumber", car.getCarNumber());
                        ParseCloud.callFunctionInBackground("removeCar", params);
                        listDataChild.get(context.getString(R.string.manage_cars)).remove(car);
                    }
                }
                notifyDataSetChanged();
            }
        };
    }

    private View.OnClickListener updatePersonal(final Context context) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final MainActivity activity = (MainActivity) context;
                FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, new PersonalFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        };
    }

    private View.OnClickListener linkFacebook(final ParseUser user, final Context context) {
        return new View.OnClickListener() {
            Activity activity = (Activity)context;
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                //ParseUser currentUser = ParseUser.getCurrentUser();
                if (!ParseFacebookUtils.isLinked(user)) {
                    ParseFacebookUtils.linkWithReadPermissionsInBackground(user, activity, null, new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(context, "Facebook account linked successfully!", Toast.LENGTH_LONG).show();
                            } else {
                                Alerter.createErrorAlert(e, context).show();
                            }
                        }
                    });
                }
            }
        };
    }
}
