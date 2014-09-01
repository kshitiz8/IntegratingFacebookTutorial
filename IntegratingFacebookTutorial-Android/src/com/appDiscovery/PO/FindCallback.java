package com.appDiscovery.PO;

import java.util.List;

import com.parse.ParseException;

public abstract class FindCallback<T extends BasePO>  {
	public abstract void done(List<T > objects, ParseException e);
}
