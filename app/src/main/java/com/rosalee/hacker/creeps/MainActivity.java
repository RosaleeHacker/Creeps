package com.rosalee.hacker.creeps;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

//runs all functional code for the app
public class MainActivity extends AppCompatActivity {

    //all variables for UI elements
    TextView responseView;
    RelativeLayout relativeLayout;
    Spinner categories;
    Button button;
    RatingBar ratingBar;
    TextView txtRatingValue;
    HashMap<String,ArrayList<Story>> stories = new HashMap<String, ArrayList<Story>>();


    //calls methods and sets up variables with their UI elements
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        get_json();

        responseView = (TextView) findViewById(R.id.responseView);
        categories = (Spinner) findViewById(R.id.category);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.categories_array, R.layout.dropdown);
        // Set the Adapter for the spinner
        categories.setAdapter(adapter);
    }


    //Inflates the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    //switch that handles showing the specific menu options
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch(item.getItemId()) {
            case R.id.action_about:
                responseView.setText("This application is all about delivering creepy stories to the user. Picking on a category" +
                        "will fill the bottom part of the screen with a story, which they can read then move on to the next." +
                        "Reader beware, you're in for a scare.");
                break;
            case R.id.action_help:
                responseView.setText("Click on the spinner to reveal a list of options. Afte that, click on the 'GO' button to pull a story from that category. You can click the button again to get a different story");
                break;
            case R.id.action_developer:
                responseView.setText("Made by Rosalee Hacker, for Mobile App Dev 2.");
                break;
        }
        return false;
    }

    //class for the story, handles a name and story text
    class Story {
        String name;
        String story;
        public Story(String _name, String _story){
            name = _name;
            story = _story;
        }
    }


    //gets and parses the json from the a.json file in the assets folder
    public void get_json() {
        String json;
        try {
            InputStream is = getAssets().open("a.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            JSONArray jsonArray = new JSONArray(json);

            //gets all information and adds it to an array of stories
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                Story story = new Story(obj.getString("name"), obj.getString("number"));
                if (stories.containsKey(obj.getString("category"))) {
                    ArrayList<Story> categoryStories = stories.get(obj.getString("category"));
                    categoryStories.add(story);
                } else {
                    ArrayList<Story> newCategory = new ArrayList<Story>();
                    newCategory.add(story);
                    stories.put(obj.getString("category"), newCategory);

                }
            }

            //when the button is pressed, show a story from the category
            button = (Button)findViewById(R.id.queryButton);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String category = categories.getSelectedItem().toString();
                    ArrayList<Story> selectedStories = stories.get(category);

                    ratingBar = (RatingBar) findViewById(R.id.ratingBar2);
                    txtRatingValue = (TextView) findViewById(R.id.txtRatingValue);

                    //handles the rating bar
                    ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                        public void onRatingChanged(RatingBar ratingBar, float rating,
                                                    boolean fromUser) {

                            txtRatingValue.setText(String.valueOf(rating));

                        }
                    });

                    Random rand = new Random();
                    final int randomNum = rand.nextInt((5 - 0) + 1) + 0;

                    //fills the text with a random story
                    responseView.setText(selectedStories.get(randomNum).name + "\n\n" + selectedStories.get(randomNum).story);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
