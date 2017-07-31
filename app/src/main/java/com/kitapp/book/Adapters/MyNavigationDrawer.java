package com.kitapp.book.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import android.widget.Toolbar;

import com.amulyakhare.textdrawable.TextDrawable;
import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.local.UserIdStorageFactory;
import com.facebook.accountkit.AccountKit;
import com.kitapp.book.Activities.AddBookActivity;
import com.kitapp.book.Activities.BookListActivity;
import com.kitapp.book.Activities.MainActivity;
import com.kitapp.book.Activities.WelcomeActivity;
import com.kitapp.book.R;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

/**
 * Created by Admin on 20.06.2017.
 */

public class MyNavigationDrawer {
    public static void createDrawer(final Activity activity, android.support.v7.widget.Toolbar toolbar){

            Drawer result;

            String name = (String) Backendless.UserService.CurrentUser().getProperty("name");
            String surname = (String) Backendless.UserService.CurrentUser().getProperty("surname");

            String letter = "";
            if (name != null) {
                letter = "";
                letter += name.charAt(0);
            }
            if (surname != null) {
                letter += surname.charAt(0);
            }
            if (letter == "") letter = "Kit";

            String nameSurname = "";
            String phone = (String) Backendless.UserService.CurrentUser().getProperty("username");
            if (name != null)
                nameSurname += Backendless.UserService.CurrentUser().getProperty("name") + " ";
            if (surname != null)
                nameSurname += Backendless.UserService.CurrentUser().getProperty("surname");
            if (nameSurname.length() < 1) nameSurname += phone;

            AccountHeader headerResult = new AccountHeaderBuilder()
                    .withActivity(activity)
                    .withHeaderBackground(TextDrawable.builder().beginConfig().bold().fontSize(220).endConfig().buildRect("KITAPP", Color.rgb(186, 160, 138)))
                    .withHeaderBackground(R.color.colorPrimary)
                    .addProfiles(
                            new ProfileDrawerItem().withName(nameSurname).withEmail(phone).withIcon(TextDrawable.builder().buildRound(letter, R.color.pink))
                    )
                    .withSelectionListEnabled(false)
                    .build();


            PrimaryDrawerItem item1 = new PrimaryDrawerItem().withName(activity.getString(R.string.all_books)).withIdentifier(1);
            PrimaryDrawerItem item2 = new PrimaryDrawerItem().withName(activity.getString(R.string.my_books)).withIdentifier(2);
            PrimaryDrawerItem item3 = new PrimaryDrawerItem().withName(activity.getString(R.string.favorite_books)).withIdentifier(3);
            DividerDrawerItem div1 = new DividerDrawerItem();
            PrimaryDrawerItem item4 = new PrimaryDrawerItem().withName(activity.getString(R.string.genres));
            PrimaryDrawerItem item5 = new PrimaryDrawerItem().withName(activity.getString(R.string.add_book));
            PrimaryDrawerItem item6 = new PrimaryDrawerItem().withName(activity.getString(R.string.logout));


            result = new DrawerBuilder()
                    .withActivity(activity)
                    .withHeader(R.layout.header_drawer)
                    .withToolbar(toolbar)
                    .addDrawerItems(item1, item2, item3, div1, item4, item5, div1, item6)
                    .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                            //Toast.makeText(activity, "pos="+position, Toast.LENGTH_SHORT).show();

                            if (position==1){
                                if (activity instanceof MainActivity){
                                    ((MainActivity) activity).selectPage(0);

                                } else {
                                    Intent intent = new Intent(activity, MainActivity.class);
                                    activity.startActivity(intent);
                                }

                            }
                            else if (position >= 2 && position <= 3) {
                                Intent intent = new Intent(activity, BookListActivity.class);
                                intent.putExtra("command", position);
                                activity.startActivity(intent);
                            } else if (position == 5) {
                                if (activity instanceof MainActivity){
                                    ((MainActivity) activity).selectPage(1);

                                } else {
                                    Intent intent = new Intent(activity, MainActivity.class);
                                    intent.putExtra("command", position);
                                    activity.startActivity(intent);
                                }


                            } else if (position == 6) {
                                Intent intent = new Intent(activity, AddBookActivity.class);
                                activity.startActivity(intent);
                            } else if (position == 8) {
                                Log.d("BookList", "position=" + position);
                                AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                                builder.setTitle(activity.getString(R.string.logout)+"?");
                                builder.setMessage(activity.getString(R.string.data_will_not_lost));

                                String positiveText = activity.getString(R.string.yes);
                                builder.setPositiveButton(positiveText,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // positive button logic
                                                AccountKit.logOut();
                                                Backendless.UserService.setCurrentUser(new BackendlessUser());

                                                //Backendless.UserService.setCurrentUser(null);

                                                UserIdStorageFactory.instance().getStorage().set(null);
                                                Intent intent = new Intent(activity, WelcomeActivity.class);
                                                activity.startActivity(intent);


                                                Backendless.UserService.logout(new AsyncCallback<Void>() {
                                                    @Override
                                                    public void handleResponse(Void aVoid) {
                                                        activity.finish();
                                                    }

                                                    @Override
                                                    public void handleFault(BackendlessFault backendlessFault) {
                                                        activity.finish();
                                                    }
                                                });
                                            }
                                        });

                                String negativeText = activity.getString(R.string.no);;
                                builder.setNegativeButton(negativeText,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // negative button logic
                                            }
                                        });

                                AlertDialog dialog = builder.create();
                                dialog.show();
                                // display dialog

                            } else {

                            }

                            return false;
                        }
                    })
                    .withSelectedItem(-1)
                    .withAccountHeader(headerResult)
                    .withOnDrawerListener(new Drawer.OnDrawerListener() {


                        @Override
                        public void onDrawerOpened(View drawerView) {
                            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
                        }

                        @Override
                        public void onDrawerClosed(View drawerView) {
                        }

                        @Override
                        public void onDrawerSlide(View drawerView, float slideOffset) {

                        }
                    })
                    .build();




    }
}
