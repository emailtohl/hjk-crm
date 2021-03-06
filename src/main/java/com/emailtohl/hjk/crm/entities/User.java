package com.emailtohl.hjk.crm.entities;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.emailtohl.lib.ConstantPattern;
import com.github.emailtohl.lib.jpa.EntityBase;
import com.github.emailtohl.lib.jpa.StringBridgeCustomization;

/**
 * 用户实体类 javax校验的注解在field上，JPA约束的注解写在JavaBean属性上
 * 
 * @author HeLei
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Indexed
@Entity
@Table(name = "users")
public class User extends EntityBase {
	private static final long serialVersionUID = 921742113764984928L;

	private IdentityType identityType;
	private String idNumber;
	private String name;
	private String nickname;// 可存储第三方昵称
	@Pattern(// 校验
			regexp = ConstantPattern.EMAIL, flags = { Pattern.Flag.CASE_INSENSITIVE })
	@NotNull
	private String email;// 唯一识别、不能为空

	@Pattern(regexp = ConstantPattern.CELL_PHONE)
	private String cellPhone;
	@Size(min = 5, message = "{password.length}")
	@Pattern(regexp = "^[\\x21-\\x7e]*$", message = "{special.symbols}")
	private transient String password;
	private Boolean enabled;
	private Boolean accountNonExpired;
	private Boolean credentialsNonExpired;
	private Boolean accountNonLocked;
	private Date lastLogin; // 最后一次登录时间
	private Date lastChangeCredentials; // 最后更改密码时间
	private String address;
	@Past // 校验，日期相对于当前较早
	private Date birthday;
	private Gender gender;
	private BinFile image;
	@Size(max = 300)
	private String description;
	
	private Set<GroupEnum> groups = new HashSet<>();

	@Field(bridge = @FieldBridge(impl = StringBridgeCustomization.class))
	@Enumerated(EnumType.STRING)
	public IdentityType getIdentityType() {
		return identityType;
	}

	public void setIdentityType(IdentityType identityType) {
		this.identityType = identityType;
	}

	@Field
	@Column(unique = true)
	public String getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}

	@Field
	@Column
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Field
	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	/**
	 * 邮箱不能为空，用于登录
	 * @return
	 */
	@Field
	@Column(nullable = false, unique = true, updatable = true)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Field
	@Column(name = "cell_phone", /* nullable = false, */unique = true, updatable = true)
	public String getCellPhone() {
		return cellPhone;
	}

	public void setCellPhone(String cellPhone) {
		this.cellPhone = cellPhone;
	}

	@Column(nullable = false)
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	@Column(name = "account_non_expired")
	public Boolean getAccountNonExpired() {
		return accountNonExpired;
	}

	public void setAccountNonExpired(Boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}

	@Column(name = "credentials_non_expired")
	public Boolean getCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	public void setCredentialsNonExpired(Boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	@Column(name = "account_non_locked")
	public Boolean getAccountNonLocked() {
		return accountNonLocked;
	}

	public void setAccountNonLocked(Boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_login")
	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_change_credentials")
	public Date getLastChangeCredentials() {
		return lastChangeCredentials;
	}

	public void setLastChangeCredentials(Date lastChangeCredentials) {
		this.lastChangeCredentials = lastChangeCredentials;
	}

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	@Temporal(TemporalType.DATE)
	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	@Transient
	public Integer getAge() {
		if (this.birthday == null) {
			return null;
		}
		Instant timestamp = Instant.ofEpochMilli(this.birthday.getTime());
		LocalDateTime date = LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault());
		LocalDate today = LocalDate.now();
		LocalDate pastDate = date.toLocalDate();
		Period years = Period.between(pastDate, today);
		return years.getYears();
	}

	@Field(bridge = @FieldBridge(impl = StringBridgeCustomization.class))
	@Enumerated(EnumType.STRING)
	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public BinFile getImage() {
		return image;
	}

	public void setImage(BinFile image) {
		this.image = image;
	}

	@Field(store = Store.COMPRESS)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Transient
	public Set<GroupEnum> getGroups() {
		return groups;
	}
	public void setGroups(Set<GroupEnum> groups) {
		this.groups = groups;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (email == null) {
			if (other.getEmail() != null)
				return false;
		} else if (!email.equals(other.getEmail()))
			return false;
		return true;
	}

}
