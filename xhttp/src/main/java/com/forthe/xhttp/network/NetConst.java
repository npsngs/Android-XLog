package com.forthe.xhttp.network;
public class NetConst {

	/**
	 * 网络访问的各种错误类型
	 */
	public static final int ERR_NONE = 0x0000;
	public static final int ERR_UNKOWN = 0x0001;
	public static final int ERR_NO_NETWORK = 0x0002;
	public static final int ERR_NOT_WIFI = 0x0003;
	
	public static final int ERR_CONNECT_TIMEOUT = 0x0011;
	public static final int ERR_SERVER_ERR = 0x0012;
	
	public static final int ERR_JSON_ERR = 0x0013;
	
	/**
	 * 网络类型
	 */
	
	public static final int NETWORK_NONE = 0x0000;
	public static final int NETWORK_ALLTYPE = 0x0001;
	public static final int NETWORK_MOBILE = 0x0002;
	public static final int NETWORK_WIFI = 0x0003;
	public static final int NETWORK_OTHERS = 0x0004;
	
	/**
	 * 网络访问的模式
	 */
	public static final int REQUEST_NO_CACHE = 0x0000;                  //不会缓存数据
	public static final int REQUEST_RETURN_REFRESH = 0x0001;            //缓存数据，每次都会刷新缓存，并且只返回最新的数据
    public static final int REQUEST_CACHE_REFRESH = 0x0002;             //缓存数据，下次访问如果存在缓存先返回缓存再去加载服务端数据这次返回
    public static final int REQUEST_CACHE_REFRESH_NOTRETURN = 0x0003;   //缓存数据，下次访问如果存在缓存先返回缓存再去加载服务端数据不返回
    public static final int REQUEST_ONLY_CACHE = 0x0004;                //缓存数据，下次访问如果存在缓存不再去服务端加载数据
    public static final int REQUEST_INTERNAL_CACHE = 0x0005;            //缓存数据，下次访问如果存在缓存且数据没过期不再去服务端加载数据
}
