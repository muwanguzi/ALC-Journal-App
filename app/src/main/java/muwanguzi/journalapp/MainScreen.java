package muwanguzi.journalapp;


import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import muwanguzi.journalapp.floatingbutton.FloatingActionButton;
import muwanguzi.journalapp.sqlitedbhelper.LogTableHelper;

public class MainScreen extends ActionBarActivity implements OnClickListener, OnLongClickListener {

	private FirebaseAuth auth;
	private String uid;
	private SimpleCursorAdapter dataAdapter;
	private ProgressBar progressBar;
	private FirebaseUser user;

	private FirebaseAuth.AuthStateListener authListener;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu_screen);
		displayDashBoard();

		progressBar = (ProgressBar) findViewById(R.id.progressBar);

		if (progressBar != null) {
			progressBar.setVisibility(View.GONE);
		}

		//get current user
		user = FirebaseAuth.getInstance().getCurrentUser();
		//Get Firebase auth instance
		auth = FirebaseAuth.getInstance();

		if (auth.getCurrentUser() != null) {

			uid = auth.getUid();

		}
		//uid = "jujjff";
		//get firebase auth instance
		auth = FirebaseAuth.getInstance();



		authListener = new FirebaseAuth.AuthStateListener() {
			@Override
			public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
				FirebaseUser user = firebaseAuth.getCurrentUser();
				if (user == null) {
					// user auth state is changed - user is null
					// launch login activity
					startActivity(new Intent(MainScreen.this, LoginActivity.class));
					finish();
				}
			}
		};

	}

	private void displayDashBoard() {
		// TODO Auto-generated method stub
		LogTableHelper log=new LogTableHelper(getApplicationContext(),"log.db",null,1);
		log.getReadableDatabase();
		Cursor c=log.fetchReport();
		
		String[] columns=new String[]
				{
				log.LOG_ID,
				log.LOG_TITLE,
				log.LOG_DATA,
				log.LOG_DATE,
				//log.LOG_DATE
				};
		
		int[] to = new int[]
				{
				R.id.id,
				R.id.title,
				R.id.content,
				R.id.date
	//			R.id.continent,
	//			R.id.region,
				};
		
		dataAdapter=new SimpleCursorAdapter(this,R.layout.dashboard_listitem,c,columns,to,0);
		final ListView lv=(ListView) findViewById(R.id.dashboard);
		lv.setAdapter(dataAdapter);
	
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				// TODO Auto-generated method stub
				
				
				AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(MainScreen.this);
				
				
				alertDialogBuilder.setTitle("What do you want to do?");
				 
				// set dialog message
				alertDialogBuilder
					.setMessage("My Journals")
					.setCancelable(true)
					.setPositiveButton("Edit",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
							   
							   Cursor cr=dataAdapter.getCursor();
							    cr.moveToPosition(position);
							    String sring=cr.getString(0);
						//	    Toast.makeText(getApplicationContext(),sring+"",Toast.LENGTH_LONG).show();
							    cr.moveToFirst();
							    Intent i = new Intent(MainScreen.this, UpdateActivity.class);
							    i.putExtra("id", sring);
							    startActivity(i);
							    MainScreen.this.finish();
						}
					  })
					  .setNeutralButton("Delete",new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							   // TODO Auto-generated method stub
							   Cursor cr=dataAdapter.getCursor();
							    cr.moveToPosition(position);
							    String sring=cr.getString(0);
							    Toast.makeText(getApplicationContext(),sring,Toast.LENGTH_LONG).show();
						
							    LogTableHelper l=new LogTableHelper(getApplicationContext(),"log.db", null, 1);
							    l.getWritableDatabase();
							    l.Delete(sring);
						        displayDashBoard();
							    dialog.dismiss();
						
						}
					})
					.setNegativeButton("Read",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
							   Cursor cr=dataAdapter.getCursor();
							    cr.moveToPosition(position);
							    String sring=cr.getString(0);
						//	    Toast.makeText(getApplicationContext(),sring+"",Toast.LENGTH_LONG).show();
							    cr.moveToFirst();
							    Intent i = new Intent(getApplicationContext(), ViewActivity.class);
							    i.putExtra("id", sring);
							    startActivity(i);
							    MainScreen.this.finish();
						}
					});
	 
					AlertDialog alertDialog = alertDialogBuilder.create();
	 				alertDialog.show();				
			}
		});
		
		FloatingActionButton fabButton = new FloatingActionButton.Builder(this)
        .withDrawable(getResources().getDrawable(R.drawable.ic_create_white_24dp))
        .withButtonColor(Color.CYAN)
        .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
        .withMargins(0, 0, 16, 16)
        .create();
		fabButton.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_screen, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {

			if (user != null) {
				user.delete()
						.addOnCompleteListener(new OnCompleteListener<Void>() {
							@Override
							public void onComplete(@NonNull Task<Void> task) {
								if (task.isSuccessful()) {
									Toast.makeText(MainScreen.this, "Your profile is deleted:( Create a account now!", Toast.LENGTH_SHORT).show();
									startActivity(new Intent(MainScreen.this, SignupActivity.class));
									finish();
									progressBar.setVisibility(View.GONE);
								} else {
									Toast.makeText(MainScreen.this, "Failed to delete your account!", Toast.LENGTH_SHORT).show();
									progressBar.setVisibility(View.GONE);
								}
							}
						});
			}if (id == R.id.rest_password) {

				Toast.makeText(MainScreen.this, "Coming  soon", Toast.LENGTH_SHORT).show();

			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		finish();
		Intent i = new Intent(getApplicationContext(), MainActivity.class);
		startActivity(i);
	}
	
	public void onBackPressed()
	{
		
		
		  new AlertDialog.Builder(this)
	      .setTitle("Exit Application")
	      .setMessage("Are you sure you want to exit this application?")
	      .setIcon(R.drawable.ic_launcher)
	      .setPositiveButton("Yes", new DialogInterface.OnClickListener()
	  {
	      public void onClick(DialogInterface dialog, int which) {
	          finish(); 
	      	android.os.Process.killProcess(android.os.Process.myPid());
	          System.exit(0);
	      }
	  })
	  .setNeutralButton("No",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
	        dialog.dismiss();
			}
		})
		.setNegativeButton("Rate App",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(MainScreen.this, "Thank You for rating us *****", Toast.LENGTH_SHORT).show();

			}
		 })
	  .show();
		
		
		
		
		
	}

	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(),"You Have Long Pressed",Toast.LENGTH_LONG).show();
		return false;
	}

	
}
