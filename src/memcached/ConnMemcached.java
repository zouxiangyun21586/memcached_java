package memcached;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Future;

import javax.imageio.stream.FileImageInputStream;

import net.spy.memcached.CASResponse;
import net.spy.memcached.CASValue;
import net.spy.memcached.MemcachedClient;

public class ConnMemcached {
	private MemcachedClient mcc;

	public static void main(String[] args) {
		ConnMemcached connMemcached = new ConnMemcached();
//		connMemcached.setUp(); // 连接
//		connMemcached.addData(); // 添加
//		connMemcached.replace(); // 替换(覆盖)
//		connMemcached.append(); // 前追加
//		connMemcached.prepend(); // 后追加
//		connMemcached.CAS(); // 类似更新
//		connMemcached.setData(); // 修改
//		connMemcached.delete(); // 删除	
//		connMemcached.select(); // 查询
//		connMemcached.query(); // 单个memgent集群 查询
//		connMemcached.upImgFile(); // 存图
		connMemcached.downImgFile(); // 取图
	}

	/**
	 * 查询主服务器和备用服务器的数据
	 */
	public void query(){
		for (int i = 10001; i < 10005; i++) { // 连接端口(集群端口)
			try {
				mcc = new MemcachedClient(new InetSocketAddress("192.168.1.16", 10240)); // 代理端口
				String aa = (String)mcc.get("name");
				System.out.println(aa);
				System.out.println("服务器: " + i + " 的值为: " + mcc.get("name"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 从memcached中读取图片流
	 * 
	 * @author zxy
	 * 
	 * 2018年6月7日 上午8:18:44
	 *
	 */
	public void downImgFile() {
		try {
			mcc = new MemcachedClient(new InetSocketAddress("192.168.1.18", 10240),new InetSocketAddress("192.168.1.16", 10240));

			byte[] bt = (byte[])mcc.get("img"); // 将获取到的值转成byte数组
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			output.write(bt); // 将获取到的流写入
		    FileOutputStream fos = new FileOutputStream("G:/xm/"+bt+".jpg"); // 文件保存路径
		    output.writeTo(fos); // 将此字节数组输出流的全部内容写入到指定的输出流参数中
			fos.flush();
			fos.close(); // 必须关闭流 否则一直在写数据进去 文件中会显示 0KB
			System.out.println("获取成功");
			mcc.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 上传图片到memcached中(设置流)
	 * @author zxy
	 * 
	 * 2018年6月7日 上午8:49:45
	 *
	 */
	public void upImgFile(){
		try {
			mcc = new MemcachedClient(new InetSocketAddress("192.168.1.18", 10240),new InetSocketAddress("192.168.1.16", 10240)); // 端口,代理节点
		
			byte[] data = null;
			FileImageInputStream input = null;
		    try {
		      input = new FileImageInputStream(new File("F:\\zxy\\my.png\\Innovation\\yy.jpg")); // 获取要上传的本地图片路径
		      ByteArrayOutputStream output = new ByteArrayOutputStream();
		      byte[] buf = new byte[10086]; // 可存入多大
		      int numBytesRead = 0;
		      while ((numBytesRead = input.read(buf)) != -1) { // 读取文件大小
		    	  output.write(buf, 0, numBytesRead); // 将指定字节数组中从偏移量 off(0) 开始的 len(numBytesRead) 个字节写入此字节数组输出流
		      }
		      data = output.toByteArray(); // 使用新的字节数组接住: 拷贝数组的大小和当前输出流的大小, 内容是的当前输出流
		      output.close();
		      input.close();
		    } catch (Exception ex1) {
		      ex1.printStackTrace();
		    }
		    Future fo = mcc.add("img", 0, data);
			System.out.println("缓存状态:" + fo.get());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 连接memcached的代理服务器10240
	 */
	public void setUp() {
		try {
			// 本地连接 Memcached 服务
			mcc = new MemcachedClient(new InetSocketAddress("192.168.1.18", 10240));
			System.out.println("Connection to server sucessful.");
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	/**
	 * 添加
	 */
	@SuppressWarnings("rawtypes")
	public void addData() {
		try {
			User user = new User();
			user.setName("Kimi");
			user.setAge(10);
			user.setSex("女");
			Future fo = mcc.add("add", 0, user);
			System.out.println("缓存状态:" + fo.get());
			mcc.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 修改操作
	 */
	@SuppressWarnings("rawtypes")
	public void setData() {
		try {
			User user = new User();
			user.setName("锤子");
			user.setSex("男");
			user.setAge(18);
			// 存储数据
			Future fo = mcc.set("add", 0, user);// 10(秒)表示缓存时间,10秒之后数据消失
			// 查看存储状态
			System.out.println("缓存状态:" + fo.get());
			// 输出值
			System.out.println("缓存数据User:" + mcc.get("add"));
			mcc.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除
	 */
	public void delete() {
		try {
			mcc.delete("add");
			mcc.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查询
	 */
	public void select() {
		try {
			System.out.println(mcc.get("add"));
			mcc.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * replace(替换)
	 * 
	 * @author zxy
	 * 
	 * 2018年6月5日 下午7:39:19
	 *
	 */
	public void replace() {
		try {
			User user = new User();
			user.setName("大马哈");
			user.setSex("未知");
			user.setAge(25);
			Future fo = mcc.replace("add", 900, user);
			// 输出执行 set 方法后的状态
			System.out.println("replace status:" + fo.get());
			// 关闭连接
			mcc.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * append (在后面追加)
	 * 
	 * @author zxy
	 * 
	 * 2018年6月5日 下午7:59:12
	 *
	 */
	public void append() {
		try {
			// 添加数据 --------- 第一步
			Future fo = mcc.set("runoob", 900, "Free Education");
			// 输出执行 set 方法后的状态
			System.out.println("set status:" + fo.get());
			// 获取键对应的值
			System.out.println("runoob value in cache - " + mcc.get("runoob"));

			// // 对存在的key进行数据添加操作 --------- 第二步
			// Future fo = mcc.append("runoob", " TWO");
			// // 输出执行 set 方法后的状态
			// System.out.println("append status:" + fo.get());
			// // 获取键对应的值
			// System.out.println("runoob value in cache - " +
			// mcc.get("runoob"));

			// 关闭连接
			mcc.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * prepend (在前面追加)
	 * 
	 * @author zxy
	 * 
	 * 2018年6月5日 下午8:10:54
	 *
	 */
	public void prepend() {
		try {
			// 添加数据
			Future fo = mcc.set("runoob", 900, "Education for All");
			// 输出执行 set 方法后的状态
			System.out.println("set status:" + fo.get());
			// 获取键对应的值
			System.out.println("runoob value in cache - " + mcc.get("runoob"));

			// 对存在的key进行数据添加操作
			// Future fo = mcc.prepend("runoob", "Free ");
			// // 输出执行 set 方法后的状态
			// System.out.println("prepend status:" + fo.get());
			// // 获取键对应的值
			// System.out.println("runoob value in cache - " +
			// mcc.get("runoob"));

			// 关闭连接
			mcc.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * CAS (更新)
	 * 
	 * @author zxy 
	 * 
	 * 2018年6月5日 下午8:13:22
	 *
	 */
	public void CAS() {
		try {
			// 添加数据
			// Future fo = mcc.set("runoob", 900, "Free Education");
			// // 输出执行 set 方法后的状态
			// System.out.println("set status:" + fo.get());
			// // 使用 get 方法获取数据
			// System.out.println("runoob value in cache - " +
			// mcc.get("runoob"));

			// 通过 gets 方法获取 CAS token（令牌）
			CASValue casValue = mcc.gets("runoob");
			// 输出 CAS token（令牌） 值
			System.out.println("CAS token - " + casValue);
			// 尝试使用cas方法来更新数据
			CASResponse casresp = mcc.cas("runoob", casValue.getCas(), 900, "Largest Tutorials-Library");
			// 输出 CAS 响应信息 OK代表成功
			System.out.println("CAS Response - " + casresp);
			// 输出值
			System.out.println("runoob value in cache - " + mcc.get("runoob"));
			// 关闭连接
			mcc.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}