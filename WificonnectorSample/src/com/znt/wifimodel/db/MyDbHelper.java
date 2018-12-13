
package com.znt.wifimodel.db; 

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.jflavio1.wificonnectorsample.R;
import com.znt.wifimodel.entity.WifiModelConstant;
import com.znt.wifimodel.utils.FileUtils;
import com.znt.wifimodel.utils.SystemUtils;

/** 
 * @ClassName: MyDbHelper 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-2-18 濞戞挸顑呭畷锟�?4:02:36  
 */
public class MyDbHelper extends SQLiteOpenHelper
{

	protected SQLiteDatabase db = null;
	private Context context = null;
	private File dbFile = null;
	private final String DB_NAME = "znt_wifi.db";
	private final String ROW_ID = "_id";
	protected final String COIN_ORDER_ASC = "music_coin asc";//闂佸弶鍨电粩鐢稿础閸パ呯闁圭儤甯掔花锟�?
	protected final String COIN_DESC = "music_coin desc";//闂佸弶鍨电粩鐢告⒔瀹ュ懐纰嶉柟鐑樺笒缁拷
	protected final String TIME_ORDER_ASC = "modify_time asc";//闁告娲ょ花顓㈠箳閹烘垹锟�?
	protected final String TIME_ORDER_DESC = "modify_time desc";//闂傚嫬绉寸花顓㈠箳閹烘垹锟�?
	protected final String TBL_SONG_LIST = "song_list_admin";//"song_list";//闁绘劘顫夐幐閬嶅礆濡ゅ嫨锟斤拷
	protected final String TBL_SONG_LIST_ADMIN = "song_list_admin";//闁绘劘顫夐幐閬嶅礆濡ゅ嫨锟斤拷
	protected final String TBL_SONG_RECORD = "song_record_list";//闁绘劘顫夐幐杈╂媼閺夎法绉块柛鎺擃殙閵嗭拷
	protected final String TBL_USER_INFOR = "user_infor";//闁烩偓鍔嶉崺娑欑┍閳╁啩绱栭悶娑虫嫹
	protected final String TBL_USER_RECORD = "user_record";//闁烩偓鍔嶉崺娑㈡儌婵犳碍顎忛悹浣规緲缂嶅秶鎮伴敓锟�?
	protected final String TBL_ADMIN = "admin_list";//缂佺媴绱曢幃濠囧川濡搫鐏欓悶娑虫嫹
	protected final String TBL_WIFI = "wifi_list";//閺夆晝鍋炵敮鎾儍閸撳獢fi閻犱焦婢樼紞锟�?
	protected final String TBL_CUR_PLAN_LIST = "cur_plan_list";
	protected final String TBL_CUR_PLAN_MUSIC = "cur_plan_music";
	private String dbDir = null;
	
	public MyDbHelper(Context context)
	{
		super(context, null, null, 2);
		this.context = context;
		
		dbDir = SystemUtils.getAvailableDir(context, WifiModelConstant.WORK_DIR + "/db_wifi/").getAbsolutePath();
		
		dbFile = new File(dbDir + "/" + DB_NAME);
		
		db = getWritableDatabase();
		//createDb(dbFile);
		openDatabase();
	}
	
	public void deleteDbFile()
	{
		if(dbFile != null && dbFile.exists())
		{
			close();
			/*final File to = new File(dbFile.getAbsolutePath() + System.currentTimeMillis());
			dbFile.renameTo(to);
			to.delete();*/
			dbFile.delete();
			openDatabase();
		}
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		// TODO Auto-generated method stub
		
	}

	/**
	*callbacks
	*/
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// TODO Auto-generated method stub
		/*String sql = "drop table " + TBL_NAME;
        db.execSQL(sql);
        onCreate(db);*/
	}

	/**
	* @Description: 闁瑰灚鎸哥槐鎴﹀极閻楀牆绁﹂幖瀛樻惈缁辨繃淇婇崒娑氫函婵炲备鍓濆﹢浣轰焊閸楃偛鐏★拷?锟姐倧锟�?
	* @param @param file   
	* @return void 
	* @throws
	 */
	public int createDb(File file)
	{
		if(file == null)
			return 1;
		if(!file.exists())
		{
			int result = FileUtils.createFile(dbFile.getAbsolutePath());
			if(result != 0)
			{
				db = getWritableDatabase();
				return result;
			}
		}
		
		db = SQLiteDatabase.openOrCreateDatabase(file, null);
		
		if(db == null)
			return 1;
		return 0;
	}
	
	public int openDatabase() 
	{
        try 
        {
            File dir = new File(dbDir);
            if (!dir.exists())
                dir.mkdirs();
            if (!dbFile.exists()) 
            {
                InputStream is = context.getResources().openRawResource(R.raw.znt_wifi);
                FileOutputStream fos = new FileOutputStream(dbFile);
                byte[] buffer = new byte[8192];
                int count = 0;
                // 鐎殿噯鎷峰┑顔碱儏椤︽煡宕氱粋绺tionary.db闁哄倸娲ｅ▎锟�?
                while ((count = is.read(buffer)) > 0) 
                {
                    fos.write(buffer, 0, count);
                }

                fos.close();
                is.close();
            }
           
            // 闁瑰灚鎸哥槐鎴︽儎椤旇偐绉垮☉鎿冨幘濞堬拷.db闁哄倸娲ｅ▎锟�?
            db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
            if(db != null)
            {
            	return 0;
            }
        } 
        catch (Exception e) 
        {
        	
        }
        return 1;
    }
	
	protected void checkDbStatus()
	{
		if(db == null)
			db = getWritableDatabase();
	}
	
	/**
	* @Description: 闁告帗绋戠紓鎾舵偘閵婏妇锟�?
	* @param @param tblName   
	* @return void 
	* @throws
	 */
	public int cretaeTbl(String tblName)
	{
		if(TextUtils.isEmpty(tblName))
			return 1;
		String CREATE_TBL = " create table "  
	            + tblName + " (" + ROW_ID + " integer primary key autoincrement, name text, url text, size long) "; 
		try
		{
			db.execSQL(CREATE_TBL);
		} 
		catch (Exception e)
		{
			// TODO: handle exception
			return 1;
		}
		return 0;
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onOpen(SQLiteDatabase db)
	{
		// TODO Auto-generated method stub
		super.onOpen(db);
	}
	
	/**
	* @Description: 闁圭粯甯掗崣鍡樼▔閿熶粙寮堕埄鍐╂闁圭櫢锟�?
	* @param @param values
	* @param @param tblName
	* @param @return   
	* @return long 
	* @throws
	 */
	public long insert(ContentValues values, String tblName)
	{
		checkDbStatus();
		return db.insert(tblName, null, values);
	}
	
	/**
	* @Description: 闁哄被鍎撮锟�?
	* @param @param tblName
	* @param @return   
	* @return Cursor 
	* @throws
	 */
	public Cursor query(String tblName)
	{
		//Cursor cur = db.rawQuery("SELECT * FROM " + tblName, null);
		String order = COIN_DESC;
		
		/*if(!tblName.equals(TBL_SONG_LIST))//濞戞挸绉靛Σ鎼佹倷鐟欏嫭灏￠柛鎺擃殙閵嗗啴骞愭径灞藉季闁哄啫鐖煎Λ鍧楀箳閹烘垹锟�?
			order = TIME_ORDER_DESC;*/
		if(tblName.equals(TBL_SONG_RECORD))
			order = TIME_ORDER_ASC;//闁哄啫鐖煎Λ鍧楀础閸パ呯闁圭儤甯掗崹锟�?
		else if(tblName.equals(TBL_ADMIN))
			order = "";//濞戞挸绉电敮鎾存償閿燂拷
		else if(tblName.equals(TBL_WIFI))
			order = TIME_ORDER_ASC;//闁哄啫鐖煎Λ鍧楀础閸パ呯闁圭儤甯掗崹锟�?
		checkDbStatus();
		Cursor cursor = null;
		try
		{
			String[] s = {};
			cursor = db.query(tblName, s, "", s, "", "", order);
		} 
		catch (Exception e)
		{
			// TODO: handle exception
			close();
			db = getWritableDatabase();
			cursor = db.query(tblName, null, null, null, null, null, order);
		}
		return cursor;
	}
	public Cursor queryAsc(String tblName)
	{
		//Cursor cur = db.rawQuery("SELECT * FROM " + tblName, null);
		String order = COIN_DESC;
		
		if(!tblName.equals(TBL_SONG_LIST))//濞戞挸绉靛Σ鎼佹倷鐟欏嫭灏￠柛鎺擃殙閵嗗啴骞愭径灞藉季闁哄啫鐖煎Λ鍧楀箳閹烘垹锟�?
			order = TIME_ORDER_ASC;
		checkDbStatus();
		Cursor cursor = null;
		try
		{
			String[] s = {};
			cursor = db.query(tblName, s, "", s, "", "", order);
		} 
		catch (Exception e)
		{
			// TODO: handle exception
			close();
			db = getWritableDatabase();
			cursor = db.query(tblName, null, null, null, null, null, order);
		}
		return cursor;
	}
	public Cursor queryNormal(String tblName)
	{
		//Cursor cur = db.rawQuery("SELECT * FROM " + tblName, null);
		String order = TIME_ORDER_DESC;
		
		checkDbStatus();
		Cursor cursor = null;
		try
		{
			String[] s = {};
			cursor = db.query(tblName, s, "", s, "", "", order);
		} 
		catch (Exception e)
		{
			// TODO: handle exception
			close();
			db = getWritableDatabase();
			cursor = db.query(tblName, null, null, null, null, null, order);
		}
		return cursor;
	}
	
	public void edit(String tblName, int id, String key, String newValue)
	{
		if(TextUtils.isEmpty(newValue) || TextUtils.isEmpty(key))
			return ;
		ContentValues values = new ContentValues();
		values.put(key, newValue);
		try
		{
			checkDbStatus();
			db.update(tblName, values, ROW_ID + "=" + Integer.toString(id), null);
		} 
		catch (Exception e)
		{
			// TODO: handle exception
		}
	}
	
	/**
	 *  闁哄洤鐡ㄩ弻濠冨緞濮橆偊鍤嬮悗娑欘殕椤旓拷
	 * @param tblName
	 * @param id
	 * @param values
	 * @return
	 */
	protected int editWithTime(String tblName, String id, ContentValues values)
	{
		values.put("modify_time", System.currentTimeMillis());
		try
		{
			checkDbStatus();
			return db.update(tblName, values, "user_id=?", new String[]{id});
		} 
		catch (Exception e)
		{
			
			// TODO: handle exception
		}
		return -1;
	}
	protected int edit(String tblName, ContentValues values)
	{
		try
		{
			checkDbStatus();
			return db.update(tblName, values, null, null);
		} 
		catch (Exception e)
		{
			
			// TODO: handle exception
		}
		return -1;
	}
	protected int edit(String tblName, String id, String state, ContentValues values)
	{
		try
		{
			checkDbStatus();
			return db.update(tblName, values, "user_id=? AND play_state=?", new String[]{id, state});
		} 
		catch (Exception e)
		{
			
			// TODO: handle exception
		}
		return -1;
	}
	protected int editWifi(String tblName, String wifiName, ContentValues values)
	{
		try
		{
			checkDbStatus();
			return db.update(tblName, values, "wifi_name=?", new String[]{wifiName});
		} 
		catch (Exception e)
		{
			
			// TODO: handle exception
		}
		return -1;
	}
	
	/**
	* @Description: 闁告帞锟�?濞呭孩绋夐敓浠嬪级閳╁啯娈堕柟鐧告嫹
	* @param @param id
	* @param @param tblName
	* @param @return   
	* @return int 
	* @throws
	 */
	public int delete(int id, String tblName) 
	{  
		checkDbStatus();
        return db.delete(tblName, ROW_ID + "=?", new String[] { String.valueOf(id) });  
    }  
	protected int delete(String key, String value, String tblName) 
	{  
		try {
			checkDbStatus();
			return db.delete(tblName, key + "=?", new String[] { value }); 
		} catch (Exception e) {
			// TODO: handle exception
		}
		return -1;
	}  
	protected int deleteAll(String tblName) 
	{  
		try {
			checkDbStatus();
			return db.delete(tblName, null, null);  
		} catch (Exception e) {
			// TODO: handle exception
		}
		return -1;
	}  
	
	/**
	* @Description: 闁告帞锟�?濞呭酣寮悧鍫濈ウ閻炴冻锟�?
	* @param @param tblName
	* @param @return   
	* @return int 
	* @throws
	 */
	public int deleteTbl(String tblName)
	{
		if(TextUtils.isEmpty(tblName))
			return 1;
		String sql = "drop table " + tblName;
		checkDbStatus();
        db.execSQL(sql);
        
        return 0;
	}
	
    public void close() 
    {  
        if (db != null)  
        {
        	db.close();  
        	db = null;
        }
    }  
}
 
