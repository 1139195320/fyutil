package store.javac.fyutil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class ClassUtil {
	
	/**
	 * 
	 * @Description <p>通过反射执行对象的类的方法</p>
	 * @version <p>v1.0</p>
	 * @Date <p>2018年6月25日 上午11:11:40</p> 
	 * @author <p>jack</p>
	 *
	 * @param classPath 类名，完整类路径
	 * @param methodName 方法名
	 * @param args 参数数组
	 * @return 方法执行返回值
	 */
	public static Object invokeMethod(String classPath , String methodName , Object ... args) {
		if(null == classPath 
				|| "".equals(classPath.trim()) 
				|| null == methodName 
				|| "".equals(methodName.trim())) {
			throw new IllegalArgumentException("类路径和方法名不允许为空！");
		}
		Class<? extends Object> thisClass = getClass(classPath);
		Object classInstance = newInstance(thisClass);
		if(null == classInstance) {
			throw new RuntimeException("该类构造方法受保护...");
		}
		return doInvokeMethod(thisClass, classInstance, methodName, args);
	}
	
	/**
	 * 
	 * @Description <p>实例化一个类</p>
	 * @version <p>v1.0</p>
	 * @Date <p>2018年6月25日 上午11:10:26</p> 
	 * @author <p>jack</p>
	 *
	 * @param beanClass 要实例化的类
	 * @return 该类的实例化对象
	 */
	public static <T> T newInstance(Class<T> beanClass) {
		try {
			/*默认构造方法*/
			return (T) beanClass.newInstance();
		} catch (Exception e) {
		}

		final Constructor<T>[] constructors = getConstructors(beanClass);
		Class<?>[] parameterTypes;
		for (Constructor<T> constructor : constructors) {
			parameterTypes = constructor.getParameterTypes();
			if (0 == parameterTypes.length) {
				continue;
			}
			try {
				return constructor.newInstance(ClassUtil.getDefaultValues(parameterTypes));
			} catch (Exception e) {
				 /*构造出错时继续尝试下一种构造方式*/
				continue;
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @Description <p>获得一个类中所有构造列表</p>
	 * @version <p>v1.0</p>
	 * @Date <p>2018年6月25日 上午11:07:31</p> 
	 * @author <p>jack</p>
	 *
	 * @param beanClass 类 <T> 构造的对象类型
	 * @return 构造函数列表
	 */
	@SuppressWarnings("unchecked")
	private static <T> Constructor<T>[] getConstructors(Class<T> beanClass) {
		Constructor<T>[] constructors = null;
		if(null != beanClass) {
			constructors = (Constructor<T>[]) beanClass.getDeclaredConstructors();
		}
		return constructors;
	}
	
	/**
	 * 
	 * @Description <p>判断一个方法是否是静态方法</p>
	 * @version <p>v1.0</p>
	 * @Date <p>2018年6月25日 上午11:06:48</p> 
	 * @author <p>jack</p>
	 *
	 * @param method
	 * @return
	 */
	public static boolean isStatic(Method method) {
		return Modifier.isStatic(method.getModifiers());
	}
	
	/**
	 * 
	 * @Description <p>通过反射执行对象的类的方法</p>
	 * @version <p>v1.0</p>
	 * @Date <p>2018年6月25日 上午11:11:53</p> 
	 * @author <p>jack</p>
	 *
	 * @param classInstance 该类的一个实例对象
	 * @param methodName 方法名
	 * @param args 参数数组
	 * @return 方法执行返回值
	 */
	public static Object invokeMethod(Object classInstance, String methodName, Object... args) {
		if (null == classInstance)
			return null;
		Class<? extends Object> thisClass = classInstance.getClass();
		return doInvokeMethod(thisClass, classInstance, methodName, args);
	}
	
	private static Object doInvokeMethod(Class<?> thisClass , Object classInstance , String methodName , Object ... args) {
		try {
			Class<?>[] argsClass = null;
			if(null != args) {
				Integer argsLength = args.length;
				argsClass = new Class[argsLength];
				if (argsLength > 0) {
					for (int i = 0; i < argsLength; i++) {
						argsClass[i] = args[i].getClass();
					}
				}
			}
			Method method = thisClass.getDeclaredMethod(methodName, argsClass);
			/* 抑制Java的访问控制检查，这样才能执行私有方法，如果不抑制则只能执行公有方法 */
			method.setAccessible(true);
			return method.invoke(isStatic(method) ? null : classInstance, args);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(methodName + "方法未找到！");
		} catch (InvocationTargetException e) {
			throw new RuntimeException("方法执行失败：" + e.getMessage());
		} catch (IllegalAccessException e) {
			throw new RuntimeException("该类构造方法受保护：" + e.getMessage());
		} 
	}
	
	/**
	 * 
	 * @Description <p>获取一个类的方法信息</p>
	 * @version <p>v1.0</p>
	 * @Date <p>2018年6月25日 上午11:17:36</p> 
	 * @author <p>jack</p>
	 *
	 * @param classPath 类路径（全路径）
	 * @return Map<方法名 , Map<（"paramType" : List<Object> 参数类型集合 ， "returnType" :
	 *         String 返回值类型）>>
	 */
	public static Map<String, Map<String, Object>> getClassMethods(String classPath) {
		if (null == classPath || "".equals(classPath.trim())){
			return null;
		}
		return getClassMethods(getClass(classPath));
	}
	
	/**
	 * 
	 * @Description <p>获取一个类的方法信息</p>
	 * @version <p>v1.0</p>
	 * @Date <p>2018年6月25日 上午11:17:36</p> 
	 * @author <p>jack</p>
	 *
	 * @param obj 该类的一个实例对象
	 * @return Map<方法名 , Map<（"paramType" : List<Object> 参数类型集合 ， "returnType" :
	 *         String 返回值类型）>>
	 */
	public static Map<String, Map<String, Object>> getClassMethodsFromObject(Object obj) {
		if (null == obj){
			return null;
		}
		return getClassMethods(obj.getClass());
	}
	
	/**
	 * 
	 * @Description <p>获取一个类的方法信息</p>
	 * @version <p>v1.0</p>
	 * @Date <p>2018年6月25日 下午3:51:04</p> 
	 * @author <p>jack</p>
	 *
	 * @param thisClass 类
	 * @return Map<方法名 , Map<（"paramType" : List<Object> 参数类型集合 ， "returnType" :
	 *         String 返回值类型）>>
	 */
	public static Map<String, Map<String, Object>> getClassMethods(Class<?> thisClass){
		if(null == thisClass) {
			return null;
		}
		Map<String, Map<String, Object>> methodMap = new HashMap<>();
		for (Method method : thisClass.getDeclaredMethods()) {
			/* 方法名 */
			String methodName = method.getName();
			/* 参数类型 */
			List<Object> paramTypeList = new ArrayList<>();
			for (Parameter param : method.getParameters()) {
				paramTypeList.add(param.getType().getName());
			}
			/* 返回值类型 */
			String returnType = method.getGenericReturnType().getTypeName();
			Map<String, Object> methodInfo = new HashMap<>();
			methodInfo.put("paramType", paramTypeList);
			methodInfo.put("returnType", returnType);
			methodMap.put(methodName, methodInfo);
		}
		return methodMap;
	}
	
	/**
	 * 
	 * @Description <p>获取一个类实体对象的信息</p>
	 * @version <p>v1.0</p>
	 * @Date <p>2018年6月25日 上午11:18:15</p> 
	 * @author <p>jack</p>
	 *
	 * @param obj 该实体类的一个实例对象
	 * @return Map<（"class" : 该实体的类全路径,"hashcode" : 该实体的hashcode）>
	 */
	public static Map<String, Object> getPojoInfoFromObject(Object obj) {
		if (null == obj)
			return null;
		Map<String, Object> infoMap = new HashMap<>();
		infoMap.put("class", obj.getClass().getName());
		infoMap.put("hashcode", obj.hashCode());
		return infoMap;
	}
	
	/**
	 * 
	 * @Description <p>获取一个类的属性信息及默认属性值</p>
	 * @version <p>v1.0</p>
	 * @Date <p>2018年6月25日 上午11:18:36</p> 
	 * @author <p>jack</p>
	 *
	 * @param thisClass 类
	 * @return Map<属性名, 属性默认值>
	 */
	public static Map<String, Object> getClassFields(Class<?> thisClass){
		if(null == thisClass) {
			throw new IllegalArgumentException("类路径不能为空！");
		}
		return doGetClassFields(thisClass, newInstance(thisClass));
	}
	
	/**
	 * 
	 * @Description <p>获取一个类的属性信息及默认属性值</p>
	 * @version <p>v1.0</p>
	 * @Date <p>2018年6月25日 上午11:18:36</p> 
	 * @author <p>jack</p>
	 *
	 * @param classPath 类名，完整类路径
	 * @return Map<属性名, 属性默认值>
	 */
	public static Map<String, Object> getClassFields(String classPath){
		if(null == classPath || "".equals(classPath.trim())) {
			throw new IllegalArgumentException("类路径不能为空！");
		}
		return getClassFields(getClass(classPath));
	}
	
	private static Class<?> getClass(String classPath){
		try {
			return ClassLoader.getSystemClassLoader().loadClass(classPath);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(classPath + "类未找到！");
		}
	}
	
	/**
	 * 
	 * @Description <p>获取一个类的属性信息及默认属性值</p>
	 * @version <p>v1.0</p>
	 * @Date <p>2018年6月25日 上午11:18:36</p> 
	 * @author <p>jack</p>
	 *
	 * @param obj 该实体类的一个实例对象
	 * @return Map<属性名, 属性默认值>
	 */
	public static Map<String, Object> getClassFieldsFromObject(Object obj) {
		if (null == obj)
			return null;
		return doGetClassFields(obj.getClass(), obj);
	}
	
	private static Map<String, Object> doGetClassFields(Class<?> thisClass , Object classInstance){
		if(null == thisClass || null == classInstance) {
			return null;
		}
		Map<String, Object> fieldMap = new HashMap<>();
		/* getDeclaredField是可以获取一个类的所有字段. getField只能获取类的public 字段. */
		for (Field field : thisClass.getDeclaredFields()) {
			/* 构造getter方法 */
			String methodName = "get" + StringUtils.capitalize(field.getName());
			Object fieldValue = null;
			try {
				/* 执行getter方法,获取其默认返回值，若该属性没有设置getter方法，则会发生异常 */
				fieldValue = thisClass.getDeclaredMethod(methodName).invoke(classInstance);
			} catch (Exception e) {
				/* 一旦发生异常,便将属性值置为UnKnown,故此处没必要一一捕获所有异常 */
				fieldMap.put(field.getName(), "UnKnown");
				continue;
			}
			if (fieldValue == null) {
				fieldMap.put(field.getName(), "null");
			} else {
				fieldMap.put(field.getName(), fieldValue);
			}
		}
		return fieldMap;
	}
	
	/**
	 * 
	 * @Description <p>获取指定类型分的默认值
	 * <pre>
	 * 1、如果为原始类型，返回0
	 * 2、非原始类型返回{@code null}
	 * </pre>
	 * </p>
	 * @version <p>v1.0</p>
	 * @Date <p>2018年6月25日 上午11:19:06</p> 
	 * @author <p>jack</p>
	 *
	 * @param clazz 类 
	 * @return  默认值
	 */
	public static Object getDefaultValue(Class<?> clazz) {
		if(clazz.isPrimitive()) {
			if(long.class == clazz) {
				return 0L;
			}else if(int.class == clazz) {
				return 0;
			}else if(short.class == clazz) {
				return (short)0;
			}else if(char.class == clazz) {
				return (char)0;
			}else if(byte.class == clazz) {
				return (byte)0;
			}else if(double.class == clazz) {
				return 0D;
			}else if(float.class == clazz) {
				return 0f;
			}else if(boolean.class == clazz) {
				return false;
			}
		}
		
		return null;
	}
	
	/**
	 * 
	 * @Description <p>获得默认值列表</p>
	 * @version <p>v1.0</p>
	 * @Date <p>2018年6月25日 上午11:20:16</p> 
	 * @author <p>jack</p>
	 *
	 * @param classes 值类型
	 * @return 默认值列表
	 */
	public static Object[] getDefaultValues(Class<?>... classes) {
		final Object[] values = new Object[classes.length];
		for(int i = 0; i < classes.length; i++) {
			values[i] = getDefaultValue(classes[i]);
		}
		return values;
	}
}
