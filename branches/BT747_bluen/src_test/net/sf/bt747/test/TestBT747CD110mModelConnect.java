package net.sf.bt747.test;

import net.sf.bt747.test.IBlue747Model.DeviceModelType;

public class TestBT747CD110mModelConnect {

	{
		IBlue747Model.defaultModelType = DeviceModelType.BTCD110m;
	}
	
	public static void main(String[] args) {
		TestModelConnect.main(args);
	}
}
