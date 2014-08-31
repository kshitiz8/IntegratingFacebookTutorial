package com.appDiscovery.PO;

import java.util.ArrayList;
import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

public class BasePO {
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
		if(objectName.equals(parseObject.getClassName())){
			this.parseObject = parseObject;
		}else{
			this.parseObject = null;
		}
	}
	
	public static <T extends BasePO>  FindCallback<ParseObject> getParseFindCallback(
			final String objectName, 
			final com.appDiscovery.PO.FindCallback<T> c){
		return new FindCallback<ParseObject>() {
		@Override
		public void done(List<ParseObject> objects, ParseException e) {
			if(e == null){
				List<T> basePOs = new ArrayList<T>();
				for(ParseObject parseObject :objects  ){
					if(objectName.equals(parseObject.getClassName())){
						T p = (T) new BasePO();
						p.setParseObject(parseObject);
						basePOs.add(p);
					}
					c.done(basePOs, e);
				}
			}else{
				c.done(null, e);
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
