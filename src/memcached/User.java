package memcached;

import java.io.Serializable;

/**
 * 实体类一定要序列化,不然会报错
 * 
 * @author liuCong
 *
 * @dateTime 2017年5月8日上午10:41:23
 */
@SuppressWarnings("serial")
public class User implements Serializable {
	private String name;
	private Integer age;
	private String sex;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	@Override
	public String toString() {
		return "User [name=" + name + ", age=" + age + ", sex=" + sex + "]";
	}

}