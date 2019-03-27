package com.emailtohl.hjk.crm.entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.emailtohl.lib.jpa.EntityBase;

/**
 * 公司和组织信息
 * @author HeLei
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Indexed
@Audited
@Entity
public class Organization extends EntityBase {
	private static final long serialVersionUID = -2949903806197415296L;
	// 创建者用户id
	private String creatorId;
	// 创建者用户姓名
	private String creatorName;
	// 公司名
	@NotNull
	private String name;
	// 税号
	@NotNull
	private String taxNumber;
	// 公司注册地址
	@NotNull
	private String address;
	// 公司电话
	@NotNull
	private String telephone;
	// 开户行
	@NotNull
	private String depositBank;
	// 开户行账号
	@NotNull
	private String account;
	// 联系人，财务负责人
	@NotNull
	private String principal;
	// 联系人，财务负责人联系电话
	@NotNull
	private String principalPhone;
	// 收票地址，若不填写，则取公司所在地址
	private String deliveryAddress;
	// 上传的凭证
	private Set<BinFile> credentials = new HashSet<BinFile>();
	// 备注
	private String remark;
	// 对接市场人员
	private String receiver;
	// 是否通过审批
	private Boolean pass;
	// 干系人，他们都可以使用本公司信息
	private Set<User> stakeholders = new HashSet<>();
	// 与流程相关的信息
	private List<Flow> flows = new ArrayList<>();
	
	@Field
	@Column(nullable = false)
	public String getCreatorId() {
		return creatorId;
	}
	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}
	
	@Transient
	public String getCreatorName() {
		return creatorName;
	}
	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}
	
	@Field
	@Column(nullable = false)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Field
	@Column(nullable = false, unique = true)
	public String getTaxNumber() {
		return taxNumber;
	}
	public void setTaxNumber(String taxNumber) {
		this.taxNumber = taxNumber;
	}
	
	@Field
	@Column(nullable = false)
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	@Field
	@Column(nullable = false)
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	
	@Field
	@Column(nullable = false)
	public String getDepositBank() {
		return depositBank;
	}
	public void setDepositBank(String depositBank) {
		this.depositBank = depositBank;
	}
	
	@Field
	@Column(nullable = false, unique = true)
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	
	@Field
	@Column(nullable = false)
	public String getPrincipal() {
		return principal;
	}
	public void setPrincipal(String principal) {
		this.principal = principal;
	}
	
	@Field
	@Column(nullable = false)
	public String getPrincipalPhone() {
		return principalPhone;
	}
	public void setPrincipalPhone(String principalPhone) {
		this.principalPhone = principalPhone;
	}
	
	@Field
	@Column(nullable = false)
	public String getDeliveryAddress() {
		return deliveryAddress;
	}
	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}
	
	@NotAudited
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinTable(name = "organization_credentials"
	, joinColumns = { @JoinColumn(name = "organization_id", referencedColumnName = "id") }
	, inverseJoinColumns = { @JoinColumn(name = "bin_file_id", referencedColumnName = "id") })
	public Set<BinFile> getCredentials() {
		return credentials;
	}
	public void setCredentials(Set<BinFile> credentials) {
		this.credentials = credentials;
	}
	
	@NotAudited
	@Field
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	@Field
	public String getReceiver() {
		return receiver;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	
	public Boolean getPass() {
		return pass;
	}
	public void setPass(Boolean pass) {
		this.pass = pass;
	}

	@NotAudited
	@ManyToMany
	@JoinTable(name = "organization_stakeholders"
	, joinColumns = { @JoinColumn(name = "organization_id", referencedColumnName = "id") }
	, inverseJoinColumns = { @JoinColumn(name = "user_id", referencedColumnName = "id") })
	public Set<User> getStakeholders() {
		return stakeholders;
	}
	public void setStakeholders(Set<User> stakeholders) {
		this.stakeholders = stakeholders;
	}
	
	@NotAudited
	@IndexedEmbedded(depth = 1)
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinTable(name = "organization_flow"
	, joinColumns = { @JoinColumn(name = "organization_id", referencedColumnName = "id") }
	, inverseJoinColumns = { @JoinColumn(name = "flow_id", referencedColumnName = "id") })
	@OrderBy(CREATE_TIME_PROPERTY_NAME + " ASC")
	public List<Flow> getFlows() {
		return flows;
	}
	public void setFlows(List<Flow> flows) {
		this.flows = flows;
	}
}
