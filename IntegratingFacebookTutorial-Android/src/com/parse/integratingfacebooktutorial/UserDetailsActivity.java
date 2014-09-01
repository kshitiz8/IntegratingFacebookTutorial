package com.parse.integratingfacebooktutorial;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

public class UserDetailsActivity extends Activity {
	private static final String TAG = "UserDetailsActivity";

	public static final String USER_APP_CLASS = "UserApp";
	public static final String APP_CLASS = "App";



	private ParseUser currentUser = null;

	private ProfilePictureView userProfilePictureView;
	private TextView userNameView;
	private TextView userLocationView;
	private TextView userGenderView;
	private TextView userDateOfBirthView;
	private TextView userRelationshipView;
	private ImageView appIcon;
	private Button logoutButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.userdetails);
		appIcon =  (ImageView) findViewById(R.id.app_icon);
		userProfilePictureView = (ProfilePictureView) findViewById(R.id.userProfilePicture);
		userNameView = (TextView) findViewById(R.id.userName);
		userLocationView = (TextView) findViewById(R.id.userLocation);
		userGenderView = (TextView) findViewById(R.id.userGender);
		userDateOfBirthView = (TextView) findViewById(R.id.userDateOfBirth);
		userRelationshipView = (TextView) findViewById(R.id.userRelationship);

		logoutButton = (Button) findViewById(R.id.logoutButton);
		logoutButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onLogoutButtonClicked();
			}
		});
		//		ParseQuery<ParseObject> query = ParseQuery.getQuery("Peter");
		////		query.whereEqualTo("", "bar");
		//		query.findInBackground(new FindCallback<ParseObject>() {
		//		    public void done(List<ParseObject> testObjects, ParseException e) {
		//		        if (e == null) {
		//		            Log.d("score", "Retrieved " + testObjects.size() + " scores");
		//		            for(ParseObject po : testObjects){
		//		            	Log.d(TAG, po.getString("foo"));
		//		            }
		//		        } else {
		//		            Log.d("score", "Error: " + e.getMessage());
		//		        }
		//		    }
		//		});


		//		//Fetch user firend's info from facebook
		//		Request.newMyFriendsRequest(ParseFacebookUtils.getSession(),  new Request.GraphUserListCallback() {
		//			@Override
		//			public void onCompleted(List<GraphUser> users, Response response){
		//				Log.v(TAG, response.toString());
		//				for (GraphUser graphUser : users) {
		//					Log.v(TAG, graphUser.getFirstName());
		//				}
		//			}
		//		}
		//				).executeAsync();

		// Fetch Facebook user info if the session is active
		Session session = ParseFacebookUtils.getSession();
		if (session != null && session.isOpened()) {
			makeMeRequest();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		proceedWithValidSession();
	}

	private void proceedWithValidSession(){
		currentUser = ParseUser.getCurrentUser();
		if (currentUser != null) {			
			updateViewsWithProfileInfo();
			//invoke card activity here 
			callAppUploaderService();


		} else {
			// If the user is not logged in, go to the
			// activity showing the login view.
			startLoginActivity();
		}
	}



	private void makeMeRequest() {

		Request request = Request.newMeRequest(ParseFacebookUtils.getSession(),new Request.GraphUserCallback() {
			@Override
			public void onCompleted(GraphUser user, Response response) {
				if (user != null) {
					// Create a JSON object to hold the profile info
					JSONObject userProfile = new JSONObject();
					try {
						// Populate the JSON object
						userProfile.put("facebookId", user.getId());
						userProfile.put("name", user.getName());
						if (user.getLocation().getProperty("name") != null) {
							userProfile.put("location", (String) user
									.getLocation().getProperty("name"));
						}
						if (user.getProperty("gender") != null) {
							userProfile.put("gender",
									(String) user.getProperty("gender"));
						}
						if (user.getBirthday() != null) {
							userProfile.put("birthday",
									user.getBirthday());
						}
						if (user.getProperty("relationship_status") != null) {
							userProfile
							.put("relationship_status",
									(String) user
									.getProperty("relationship_status"));
						}

						// Save the user profile info in a user property

						ParseUser currentUser = ParseUser
								.getCurrentUser();
						currentUser.put("profile", userProfile);
						currentUser.saveInBackground();

						// 
						proceedWithValidSession();
					} catch (JSONException e) {
						Log.d(IntegratingFacebookTutorialApplication.TAG,
								"Error parsing returned user data.");
					}

				} else if (response.getError() != null) {
					if ((response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_RETRY)
							|| (response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_REOPEN_SESSION)) {
						Log.d(IntegratingFacebookTutorialApplication.TAG,
								"The facebook session was invalidated.");
						onLogoutButtonClicked();
					} else {
						Log.d(IntegratingFacebookTutorialApplication.TAG,
								"Some other error: "
										+ response.getError()
										.getErrorMessage());
					}
				}
			}
		});
		request.executeAsync();

	}

	private void updateViewsWithProfileInfo() {
		if (currentUser.get("profile") != null) {
			JSONObject userProfile = currentUser.getJSONObject("profile");
			try {
				if (userProfile.getString("facebookId") != null) {
					String facebookId = userProfile.get("facebookId")
							.toString();
					userProfilePictureView.setProfileId(facebookId);
				} else {
					// Show the default, blank user profile picture
					userProfilePictureView.setProfileId(null);
				}
				if (userProfile.getString("name") != null) {
					userNameView.setText(userProfile.getString("name"));
				} else {
					userNameView.setText("");
				}
				if (userProfile.getString("location") != null) {
					userLocationView.setText(userProfile.getString("location"));
				} else {
					userLocationView.setText("");
				}
				if (userProfile.getString("gender") != null) {
					userGenderView.setText(userProfile.getString("gender"));
				} else {
					userGenderView.setText("");
				}
				if (userProfile.getString("birthday") != null) {
					userDateOfBirthView.setText(userProfile
							.getString("birthday"));
				} else {
					userDateOfBirthView.setText("");
				}
				if (userProfile.getString("relationship_status") != null) {
					userRelationshipView.setText(userProfile
							.getString("relationship_status"));
				} else {
					userRelationshipView.setText("");
				}
			} catch (JSONException e) {
				Log.d(IntegratingFacebookTutorialApplication.TAG,
						"Error parsing saved user data.");
			}

		}
	}

	
	private void onLogoutButtonClicked() {
		// Log the user out
		ParseUser.logOut();

		// Go to the login view
		startLoginActivity();
	}

	private void startLoginActivity() {
		Intent intent = new Intent(this, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
	
	private void callAppUploaderService() {
		Intent appUploaderService = new Intent(this, AppUploaderService.class);
	    startService(appUploaderService);
	    
	    
	    
	}
}
