package com.mty.groupfuel;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.List;

public class SettingsListAdapter extends BaseExpandableListAdapter{
    private final int CHILD_TYPE_COUNT = 4;
    private Context context;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;

    public SettingsListAdapter(Context context, List<String> listDataHeader, HashMap<String, List<String>> listDataChild) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listDataChild;
    }


    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String)getChild(groupPosition, childPosition);

        int type = getChildType(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(resourceFromType(type), null);
        }

        switch (getChildType(groupPosition, childPosition)) {
            case 0: // settings_addremove
                Button addNew = (Button)convertView.findViewById(R.id.add_new_car);
                addNew.setOnClickListener(addNewCar());
                Button remove = (Button)convertView.findViewById(R.id.remove_car);
                remove.setOnClickListener(removecar());
                break;
            case 1: // settings_car
                TextView carText = (TextView) convertView.findViewById(R.id.car_text);
                carText.setText(childText);
                break;
            case 2: // settings_item
                TextView txtListChild = (TextView) convertView.findViewById(R.id.lblListItem_text);
                txtListChild.setText(childText);
                break;
            case 3: // settings_button
                Button button = (Button) convertView.findViewById(R.id.settings_button);
                if (childText == Consts.BUTTON_LOGOUT) {
                    button.setOnClickListener(signOut(context));
                    button.setText(context.getString(R.string.logout));
                } else if (childText == Consts.BUTTON_UPDATE) {
                    button.setOnClickListener(updatePersonal(context));
                    button.setText(context.getString(R.string.change_personal));
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

    private View.OnClickListener addNewCar() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
            }
        };
    }

    private View.OnClickListener removecar() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
            }
        };
    }
    private View.OnClickListener updatePersonal(final Context context) {
        return new View.OnClickListener() {
            final Context mcontext = context;
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mcontext, PersonalActivity.class);
                intent.putExtra(Consts.PARENT_ACTIVITY_NAME, SettingsFragment.class.getClasses());
                mcontext.startActivity(intent);
            }
        };
    }
    private View.OnClickListener signOut(final Context context) {
        return new View.OnClickListener() {
            final Context mcontext = context;
            ProgressDialog progress;

            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(mcontext)
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            progress = ProgressDialog.show(mcontext, "Logging you out", "Please Wait...");
                            ParseUser.logOutInBackground(new LogOutCallback() {
                                @Override
                                public void done(ParseException e) {
                                    progress.dismiss();
                                    mcontext.startActivity(new Intent(mcontext, DispatchActivity.class));
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
}
