package DataAccessObject.SQLite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import DataAccessObject.iContactDAO;
import entities.Contact;
import entities.Message;

public class ContactDAOSQLite extends SQLiteOpenHelper implements iContactDAO {
    public static final String DB_NAME="APP_MESSAGES_DEMO";
    public static final int DB_VERSION=3;
    private Context context=null;
    public ContactDAOSQLite(@Nullable Context context, @Nullable  String name, @Nullable  SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context=context;
        /*
        this.getWritableDatabase().execSQL(
                "drop table  contact"
        );

         */
        this.getWritableDatabase().execSQL(
                "create table if not exists contact (identifier varchar(50) primary key, imageAvatarId integer)"
        );

    }


    @Override
    public boolean saveContact(Contact c) {
        try {
            String sql = "insert into contact (identifier,imageAvatarId) values(?,?)";
            this.getWritableDatabase().execSQL(sql, new Object[]{c.getName(), c.getImageId()});
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteContact(Contact c) {
        return false;
    }

    @Override
    public ArrayList<Contact> readAllContacts() {
        ArrayList<Contact> list= new ArrayList<>();
        Cursor cursor= this.getReadableDatabase().rawQuery("select identifier,imageAvatarId from contact",null);
        while(cursor.moveToNext()) {
            String name=cursor.getString(0);
            int imageId=cursor.getInt(1);
            Contact c= new Contact(name, imageId);
            //add messages
            ArrayList<Message> messages=new MessageDAOSQLite(this.context,DB_NAME,null, DB_VERSION).getAllMessagesToContact(c);
            c.setMessages(messages);
            list.add(c);
        }
        return list;
    }

    @Override
    public Contact findContactByIdentifierName(String identifier) {
        return null;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {    }
}
