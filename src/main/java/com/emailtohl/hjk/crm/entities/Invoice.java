package com.emailtohl.hjk.crm.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.emailtohl.lib.jpa.BaseEntity;

/**
 * 发票
 * @author HeLei
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Indexed
@Audited
@Entity
public class Invoice extends BaseEntity {
	private static final long serialVersionUID = -2949903806197415296L;
	// 公司名
	@NotNull
	private String organization;
	// 税号
	@NotNull
	private String taxNumber;
	// 公司注册地址
	@NotNull
	private String organizationAddress;
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
	
	// 与流程相关的信息
	private Flow flow;
	
	@Field
	@Column(nullable = false)
	public String getOrganization() {
		return organization;
	}
	public void setOrganization(String organization) {
		this.organization = organization;
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
	public String getOrganizationAddress() {
		return organizationAddress;
	}
	public void setOrganizationAddress(String organizationAddress) {
		this.organizationAddress = organizationAddress;
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
	@JoinTable(name = "invoice_credentials"
	, joinColumns = { @JoinColumn(name = "invoice_id", referencedColumnName = "id") }
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
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "flow_id")
	public Flow getFlow() {
		return flow;
	}
	public void setFlow(Flow flow) {
		this.flow = flow;
	}
	
}
