package com.appDiscovery.PO;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

public class BasePO {
	public static final String TAG = "BasePO";
	protected String objectName = "";
	ParseObject parseObject = null;

	public BasePO() {
	}
	public BasePO(ParseObject po){
		setParseObject(po);
	}
	public BasePO(String objectName) {
		this.objectName =objectName; 
		parseObject = new ParseObject(objectName);
	}

	public ParseObject getParseObject() {
		return parseObject;
	}
	public void setParseObject(ParseObject parseObject) {
		this.objectName = parseObject.getClassName();
		this.parseObject = parseObject;
	}

	public static <T extends BasePO> FindCallback<ParseObject> getParseFindCallback(
			final String objectName, 
			final com.appDiscovery.PO.FindCallback<T> callback,
			final Class c){
		return new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				Log.d(TAG, "Inside  Parse FindCallback");
				Log.d(TAG, c.getCanonicalName());
				if(e == null){
					List<T> basePOs = new ArrayList<T>();
					for(ParseObject parseObject :objects  ){
						if(objectName.equals(parseObject.getClassName())){

							Class<?> clazz;
							try {
								clazz = Class.forName(c.getName());
								Constructor<?> ctor = clazz.getConstructor(ParseObject.class);
								Object object = ctor.newInstance(new Object[] {  parseObject});
								basePOs.add((T)object);
							} catch (ClassNotFoundException e1) {
								// TODO Auto-generated catch block
								Log.e(TAG,e.getMessage());
								e1.printStackTrace();
							} catch (InstantiationException e1) {
								Log.e(TAG,e.getMessage());
								e1.printStackTrace();
							} catch (IllegalAccessException e1) {
								Log.e(TAG,e.getMessage());
								e1.printStackTrace();
							} catch (IllegalArgumentException e1) {
								Log.e(TAG,e.getMessage());
								e1.printStackTrace();
							} catch (InvocationTargetException e1) {
								Log.e(TAG,e.getMessage());
								e1.printStackTrace();
							} catch (NoSuchMethodException e1) {
								Log.e(TAG,e.getMessage());
								e1.printStackTrace();
							}


						}
					}
					Log.d(TAG, "calling our FindCallback");
					callback.done(basePOs, e);
				}else{
					Log.d(TAG, "calling our FindCallback with error");
					callback.done(null, e);
				}

			}

		};
	}

	public static  <T extends BasePO> List<ParseObject> getPOList(List<T> basePOs){
		List<ParseObject> po = new ArrayList<ParseObject>();
		for(T basePO : basePOs){
			po.add(basePO.getParseObject());
		}
		return po;

	}


}
