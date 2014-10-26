package com.baidu.bkm.wiki.client.xmlrpc.entity;

import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Map.Entry;

/**
 * 抽象rpc对象，提供通用的操作方法
 * @author wangye04 笨笨
 * @email wangye04@baidu.com
 * @datetime Nov 1, 2013 5:02:14 PM
 */
public abstract class RemoteBase {
	
	/**
	 * 将当前对象序列化成hashtable传递给后端
	 * @return
	 * @throws Exception
	 */
	public Hashtable<String, String> toHashtable() throws Exception {
		Hashtable<String, String> tb = new Hashtable<String, String>();
		Method[] ms = this.getClass().getDeclaredMethods();
		for (Method m : ms) {
			String name = m.getName();
			if (name.startsWith("get")) {
				Object o = m.invoke(this);
				if (o == null) {
					continue;
				}
				tb.put(name.substring(3, 4).toLowerCase() + name.substring(4), String.valueOf(o));
			}
		}
		return tb;
	}
	
	/**
	 * 通过反射找到对应的getter方法，得到对应setter方法需要传入的参数类型，并通过反射构造这个参数（默认调用valueOf方法），然后再通过setter方法设置这个数据; 这样的方式需要：
	 * <ul>
	 * <li>getter和setter是和field成对出现；</li>
	 * <li>getter的返回值和setter的参数值与field对应的类型相同；</li>
	 * <li>field对应类型有valueOf(String)的方法；</li>
	 * </ul>
	 * @param ht
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public <T> T fromHashtable(Hashtable<String, ?> ht) throws Exception {
		for (Entry<String, ?> e : ht.entrySet()) {
			// 获得fieldname
			String name = e.getKey();
			Object value = e.getValue();
			Class<?> clz = value.getClass();
			String tmp = name.substring(0, 1).toUpperCase() + name.substring(1);
			// 获取setter对应需要的参数类型，有可能是getXxx，也可能是isXxx
			Method getter = null;
			try {
				getter = getClass().getMethod("get" + tmp);
			} catch (NoSuchMethodException nsme) {
				try {
					getter = getClass().getMethod("is" + tmp);
				} catch (NoSuchMethodException nsme1) {
					continue;// 如果getXxx和isXxx都找不到，就直接忽略这个属性
				}
			}
			Class<?> grclz = getter.getReturnType();
			Object sp = null;
			Class<?> pclz = null;
			// 数据类型和setter类型是一样，直接使用该值
			if (grclz == clz) {
				sp = value;
			} else {// 不一样，一般肯定原始类型和就是string类型
				if (grclz.isPrimitive()) { // 原始类型，找到对应包装对象的构造函数
					String sn = grclz.getSimpleName();
					if (sn.equals("int")) {
						sn = "Integer";
					} else if (sn.equals("long")) {
						sn = "Long";
					}
					pclz = grclz;
					grclz = Class.forName("java.lang." + sn.substring(0, 1).toUpperCase() + sn.substring(1));
				}
				try {// 尝试用返回的类型直接构造需要的结构对象
					sp = grclz.getConstructor(clz).newInstance(value);
				} catch (NoSuchMethodException e2) {
					try {// 尝试用参数为String类型的构造函数
						sp = grclz.getConstructor(String.class).newInstance(value);
					} catch (NoSuchMethodException e3) {
						// 尝试用无参构造函数+valueOf(String)方法来构造
						Method nm = grclz.getMethod("valueOf", String.class);
						sp = nm.invoke(grclz.newInstance(), value);
					}
				}
			}
			// 调用setter方法
			Method setter = getClass().getMethod("set" + tmp, pclz == null ? grclz : pclz);
			setter.invoke(this, sp);
		}
		return (T) this;
	}
}
