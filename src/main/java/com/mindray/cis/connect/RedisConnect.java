/**
 * 50234332
 *2020年3月28日
 */
package com.mindray.cis.connect;

import redis.clients.jedis.Jedis;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author 50234332
 *
 */
//https://blog.csdn.net/zspppppp/article/details/84847323
public class RedisConnect implements IConnect {

	/**
	 * 
	 */
	public RedisConnect() {
		//连接本地的 Redis 服务
        Jedis jedis = new Jedis("localhost");
        System.out.println("连接成功");
        
        //查看服务是否运行
        System.out.println("服务正在运行: "+jedis.ping());
        
        //存储数据到列表中
        jedis.lpush("site-list", "Runoob");
        jedis.lpush("site-list", "Google");
        jedis.lpush("site-list", "Taobao");
        // 获取存储的数据并输出
        List<String> list = jedis.lrange("site-list", 0 ,0);
        for(int i=0; i<list.size(); i++) {
            System.out.println("列表项为: "+list.get(i));
        }
        
        // 获取数据并输出
        Set<String> keys = jedis.keys("*"); 
        Iterator<String> it=keys.iterator() ;   
        while(it.hasNext()){   
            String key = it.next();   
            System.out.println(key);   
        }
        
        jedis.close();
	}
	
	
}
