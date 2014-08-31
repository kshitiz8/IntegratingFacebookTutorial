package com.appDiscovery.PO;

import java.util.List;

import com.parse.ParseException;

public interface FindCallback<T extends BasePO>  {
	public void done(List<T > objects, ParseException e);
	
}
