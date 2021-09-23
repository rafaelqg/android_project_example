package viewhelper;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.app_example.ConversationActivity;
import com.example.app_example.InputsExample;
import com.example.app_example.R;

public class MainMenuViewHelper {


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public boolean onCreateOptionsMenu(Activity activity, Menu menu) {
        activity.getMenuInflater().inflate(R.menu.main_menu, menu);

        int itemId = View.generateViewId();
        int order = Menu.NONE;//0
        String menuText = "Share";
        int groupId = Menu.NONE;//0
        MenuItem item = menu.add(groupId, itemId, order, menuText);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                shareTextContent("Next assigments for your Jedi trainning.", "Choose an APP to share:", activity);
                return true;
            }
        });
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item, Activity activity) {
        String title = item.getTitle().toString();
        Intent myIntent = null;
        switch (item.getItemId()) {
            case R.id.menu_item_B:
                final String URI = "https://developer.android.com?p1=1";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URI));
                activity.startActivity(intent);
                Log.i("Opening browser:", URI);
                break;
            case R.id.menu_item_A:
                myIntent = new Intent(activity.getBaseContext(), InputsExample.class);
                activity.startActivity(myIntent);
                break;
            case R.id.menu_shortcut_luke:
                myIntent = new Intent(activity.getBaseContext(), ConversationActivity.class);
                myIntent.putExtra("userName", "Luke");
                myIntent.putExtra("imageResourceId", R.drawable.luke);
                activity.startActivity(myIntent);
                break;
            case R.id.menu_shortcut_obi_wan:
                myIntent = new Intent(activity.getBaseContext(), ConversationActivity.class);
                myIntent.putExtra("userName", "Obi-wan");
                myIntent.putExtra("imageResourceId", R.drawable.obi_wan);
                activity.startActivity(myIntent);
                break;
            default:
        }
    return true;
    }

    private void shareTextContent(String content,String title, Activity activity){
        //1. First step - create an Intent for ACTION_SEND
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,  content); // inform the text to be shared
        sendIntent.setType("text/plain");
        //2. Create an intent to open another APP by createChooser method
        Intent shareIntent = Intent.createChooser(sendIntent, title);
        activity.startActivity(shareIntent);
    }
}
