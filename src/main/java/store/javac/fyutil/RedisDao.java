package store.javac.fyutil;

import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;

/**
 * 
 * @Description <p>Redis操作类，应该先new出实例对象，然后进行操作，最后进行关闭操作</p>
 * @version <p>v1.0</p>
 * @Date <p>2018年7月4日 上午10:32:39</p> 
 * @author <p>fangyang</p>
 *
 */
public class RedisDao {

	private static String URL = "localhost";
	private static Integer PORT = 6379;
	
	private static Jedis jedis = null ;
	
	private static RedisDao redisDao = null;
	
	private RedisDao() {
	}
	
	private RedisDao(String url , Integer port) {
		if(null != url) {
			URL = url;
		}
		if(null != port) {
			PORT = port;
		}
	}
	
	public static RedisDao newInstance() {
		if(redisDao == null) {
			redisDao = new RedisDao();
			// jedis = new Jedis(URL);
			jedis = new Jedis(new JedisShardInfo(URL, PORT));
		}
		return redisDao;
	}
	
	public static RedisDao newInstance(String url , Integer port) {
		if(redisDao == null) {
			redisDao = new RedisDao(url , port);
			// jedis = new Jedis(URL);
			jedis = new Jedis(new JedisShardInfo(URL, PORT));
		}
		return redisDao;
	}
	
	
	/**
	 * 返回有序集 key 中，成员 member 对应的score排序值
	 * @param key
	 * @param member
	 * @return
	 */
	public double getZADDMemberScore(String key , String member) {
		return jedis.zscore(key, member);
	}
	
	/**
	 * 移除有序集 key 中，指定排名(start到end)之间(含)内的所有成员
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public RedisDao removeMembersFromZADDByRange(String key , long start , long end) {
		if(key != null && "zset".equals(getType(key))) {
			jedis.zremrangeByRank(key, start, end);
		}
		return redisDao;
	}

	/**
	 * 移除有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员
	 * @param key
	 * @param min
	 * @param max
	 * @return
	 */
	public RedisDao removeMembersFromZADDByDataRange(String key , double min , double max) {
		if(key != null && "zset".equals(getType(key))) {
			jedis.zremrangeByScore(key, min, max);
		}
		return redisDao;
	}
	
	/**
	 * 移除有序集 key 中的一个或多个成员，不存在的成员将被忽略
	 * @param key
	 * @param members
	 * @return
	 */
	public RedisDao removeMembersFromZADD(String key , String []members) {
		if(key != null && "zset".equals(getType(key))) {
			jedis.zrem(key, members);
		}
		return redisDao;
	}
	
	/**
	 * 获取有序集key中成员member的排名（以0为起始排名）
	 * @param key
	 * @param member
	 * @return
	 */
	public Long getMemberRankInZADD(String key , String member) {
		return jedis.zrank(key, member);
	}
	
	/**
	 * 获取有序集key中指定范围min到max的成员数据
	 * @param key
	 * @param min
	 * @param max
	 * @param order 是否以顺序输出（true为顺序，从小到大，false为逆序，从大到小）
	 * @return
	 */
	public Set<String> getMemberRangeZADD(String key ,double min , double max , boolean order) {
		if(null == key) return null;
		if(order) {
			return jedis.zrangeByScore(key, min, max);
		}else {
			return jedis.zrevrangeByScore(key, min, max);
		}
	}

	/**
	 * 获取有序集key中所有的成员数据
	 * @param key
	 * @param order 是否以顺序输出（true为顺序，从小到大，false为逆序，从大到小）
	 * @return
	 */
	public Set<String> getMemberRangeZADDAll(String key , boolean order) {
		if(null == key) return null;
		if(order) {
			return jedis.zrangeByScore(key, "-inf", "+inf");
		}else {
			return jedis.zrevrangeByScore(key, "-inf", "+inf");
		}
	}

	/**
	 * 给有序集key中的成员数据member的对应的排序的数增加num（num允许为负值）
	 * @param key
	 * @param num
	 * @param member
	 * @return
	 */
	public RedisDao addNumToZADDMember(String key , double num , String member) {
		if("zset".equals(getType(key))) {
			jedis.zincrby(key, num, member);
		}
		return redisDao;
	}
	
	/**
	 * 获取某个key对应value的类型
	 * @param key
	 * @return
	 */
	public String getType(String key) {
		if(null == key) return null;
		return jedis.type(key);
	}
	
	/**
	 * 返回有序集 key 中， 排序的数值在 min 和 max 之间(默认包括等于 min 或 max)的成员的数量
	 * @param key
	 * @param min
	 * @param max
	 * @return
	 */
	public Long getCountRangeZADD(String key ,double min , double max) {
		if(null == key) return 0L;
		return jedis.zcount(key, min, max);
	}

	/**
	 * 获取有序集成员总数量
	 * @param key
	 * @return
	 */
	public Long getCountRangeZADDAll(String key) {
		if(null == key) return 0L;
		return jedis.zcount(key, "-inf" , "+inf");
	}

	/**
	 * 设置有序集
	 * @param key
	 * @param scoreMembers <数据 , 排序的数>
	 * @return
	 */
	public RedisDao setZADD(String key, Map<String, Double> scoreMembers) {
		jedis.zadd(key, scoreMembers);
		return redisDao;
	}

	/**
	 * 获取有序集数据
	 * @param dataName 有序集名
	 * @param start 起始下标（0为第一个元素）
	 * @param end 终止下标（-1代表到最后）
	 * @param order 是否以顺序输出（true为顺序，从小到大，false为逆序，从大到小）
	 * @return
	 */
	public Set<String> getZADDData(String dataName , Integer start , Integer end , boolean order) {
		if(order) {
			return jedis.zrange(dataName, start , end);
		}else {
			return jedis.zrevrange(dataName, start , end);
		}
	}
	
	/**
	 * 返回有序集 key 的基数
	 * @param key
	 * @return
	 */
	public Long getBaseNumFromZADD(String key) {
		if(null == key) return null;
		return jedis.zcard(key);
	}
	
	/**
	 * 将 oldKey 改名为  newKey
	 * @param oldKey 旧key名
	 * @param newKey 新key名
	 * @param allowOverride 是否允许覆盖
	 * @return
	 */
	public RedisDao renameKey(String oldKey , String newKey , boolean allowOverride) {
		if(!isExistenceKey(oldKey)) {
			//oldKey不存在
		}else {
			if((isExistenceKey(newKey) && allowOverride) || !isExistenceKey(newKey)){
				//newKey存在且允许覆盖，或者newKey不存在
				jedis.rename(oldKey, newKey);
			}
		}
		return redisDao;
	}
	
	/**
	 * 从当前数据库中随机返回(不删除)一个 key
	 * @return
	 */
	public String getRandomKey() {
		return jedis.randomKey();
	}
	
	/**
	 * 将当前数据库的 key 移动到给定的数据库 db 当中
	 * 如果当前数据库(源数据库)和给定数据库(目标数据库)有相同名字的给定 key ，或者 key 不存在于当前数据库，那么 MOVE 没有任何效果
	 * @param key
	 * @param dbIndex （数据库默认为0-15）
	 * @return
	 */
	public RedisDao moveKeyOtherDb(String key , Integer dbIndex) {
		jedis.move(key, dbIndex);
		return redisDao;
	}
	
	/**
	 * 查找所有符合给定模式 pattern 的 key
	 * @param pattern 匹配模式（正则）
	 * @return
	 */
	public Set<String> getPatternKeys(String pattern){
		return jedis.keys(pattern);
	}
	
	/**
	 * 判断给定键值对是否存在
	 * @param key
	 * @return
	 */
	public boolean isExistenceKey(String key) {
		return jedis.exists(key);
	}
	
	/**
	 * 删除给定的一个或多个 key，不存在的 key 会被忽略
	 * @param keys 存放key的数组
	 * @return
	 */
	public RedisDao deleteKeys(String [] keys) {
		jedis.del(keys);
		return redisDao;
	}
	
	/**
	 * 获取某一个键值对的剩余生存时间
	 * @param key 键值对的key
	 * @param isSecond 是否返回以秒为单位，false则以毫秒为单位
	 * @return
	 */
	public Long getKeyExpire(String key , boolean isSecond) {
		if(isSecond) {
			return jedis.ttl(key);
		}else {
			return jedis.pttl(key);
		}
	}
	
	/**
	 * 移除某一个键值对的有效时间
	 * @param key 键值对的key
	 * @return
	 */
	public RedisDao removeKeyExpire(String key) {
		jedis.persist(key);
		return redisDao;
	}
	
	/**
	 * 设置某一个键值对的有效时间
	 * @param key 键值对的key
	 * @param time 有效时间（单位秒或毫秒）
	 * @param isSecond 是否以秒为单位设置，false则以毫秒为单位
	 * @return
	 */
	public RedisDao setKeyExpire(String key , Integer time , boolean isSecond) {
		if(isSecond) {
			jedis.expire(key, time);
		}else {
			jedis.expireAt(key, time);
		}
		return redisDao;
	}

	/**
	 * 获取列表数据
	 * @param dataName 列表名
	 * @param start 起始下标（0为第一个元素）
	 * @param end 终止下标（-1代表到最后）
	 * @return
	 */
	public List<String> getListData(String dataName , Integer start , Integer end) {
		List<String> data = jedis.lrange(dataName, start , end);
		return data;
	}

	/**
	 * 设置列表数据
	 * @param data 列表数据集合
	 * @param dataName 列表名
	 * @return
	 */
	public RedisDao setListData(List<String> data, String dataName) {
		if(null != data && data.size() > 0) {
			for (int i = 0; i < data.size(); i++) {
				jedis.lpush(dataName, data.get(i));
			}
		}
		return redisDao;
	}
	
	/**
	 * 根据字符串key获取value
	 * @param key
	 * @return
	 */
	public String getStringData(String key) {
		if(null != key) {
			return jedis.get(key);
		}else {
			return null;
		}
	}

	/**
	 * 存入字符串数据
	 * @param key 字符串键
	 * @param val 字符串值
	 * @return 
	 */
	public RedisDao setStringData(String key , String val) {
		jedis.set(key , val);
		return redisDao;
	}

	/**
	 * 测试连接是否成功
	 */
	public void testConnect() {
		try {
			System.out.println("连接成功！\n服务正在运行：" + jedis.ping());
		} catch (Exception e) {
			System.out.println("连接失败！" + e.getMessage());
		}
	}
	
	/**
	 * 关闭操作
	 */
	public void close() {
		if(null != jedis) jedis.close();
	}
}
