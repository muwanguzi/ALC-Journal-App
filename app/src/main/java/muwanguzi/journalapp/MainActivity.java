package muwanguzi.journalapp;

import java.text.SimpleDateFormat;
import java.util.Date;


import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import muwanguzi.journalapp.sqlitedbhelper.LogTableHelper;


public class MainActivity extends ActionBarActivity implements OnClickListener {


	EditText texttitle,summary;
	private FirebaseAuth auth;
	private String uid;
	Button save;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.additem);
	
		texttitle=(EditText)findViewById(R.id.texttitle);
		summary=(EditText)findViewById(R.id.editsummary);
		
		save=(Button)findViewById(R.id.save);
		save.setOnClickListener(this);

		//Get Firebase auth instance
		auth = FirebaseAuth.getInstance();

		if (auth.getCurrentUser() != null) {

			uid = auth.getUid();

		}
		//uid = "jujjff";
		}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			FirebaseUser user = auth.getCurrentUser();
			if (user != null) {
				user.delete()
						.addOnCompleteListener(new OnCompleteListener<Void>() {
							@Override
							public void onComplete(@NonNull Task<Void> task) {
								if (task.isSuccessful()) {
									Toast.makeText(MainActivity.this, "Your profile is deleted:( Create a account now!", Toast.LENGTH_SHORT).show();
									startActivity(new Intent(MainActivity.this, SignupActivity.class));
									finish();

								} else {
									Toast.makeText(MainActivity.this, "Failed to delete your account!", Toast.LENGTH_SHORT).show();

								}
							}
						});
			}
			return true;
		}if (id == R.id.rest_password) {

			Toast.makeText(MainActivity.this, "Coming  soon", Toast.LENGTH_SHORT).show();

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v==save)
		{
			if(uid !=null&& texttitle.getText().toString().length()>0 && summary.getText().toString().length()>0)
			{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String currentDateandTime = sdf.format(new Date());
				LogTableHelper log=new LogTableHelper(getApplicationContext(),"log.db",null,1);
				log.insertLog(uid,texttitle.getText().toString(),summary.getText().toString(),currentDateandTime);
				Toast.makeText(getApplicationContext(),currentDateandTime,Toast.LENGTH_LONG).show();
				
				finish();
				Intent i=new Intent(getApplicationContext(), MainScreen.class);
				startActivity(i);
			}
			else
			{
				Toast.makeText(getApplicationContext(),"Summary or Text is missing",Toast.LENGTH_LONG).show();
			}
		}
	}
	
	public void onBackPressed()
	{
		finish();
		Intent i=new Intent(getApplicationContext(), MainScreen.class);
		startActivity(i);
	}
	
}
