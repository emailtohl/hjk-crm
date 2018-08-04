package com.emailtohl.hjk.crm.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Indexed;

import com.github.emailtohl.lib.jpa.BaseEntity;
import com.github.emailtohl.lib.jpa.EnumBridgeCust;

/**
 * 发票
 * @author HeLei
 */
@Indexed
@Entity
public class Invoice extends BaseEntity {
	private static final long serialVersionUID = -2949903806197415296L;
	@NotNull
	private InvoiceType type;
	@NotNull
	private String organization;
	// 财务负责人
	@NotNull
	private String principal;
	@NotNull
	private String telephone;
	@NotNull
	private String taxNumber;
	// 收票地址
	@NotNull
	private String address;
	// 开户行
	@NotNull
	private String depositBank;
	// 开户行账号
	@NotNull
	private String account;
	// 上传的凭证
	private Set<Image> credentials = new HashSet<Image>();
	// 是否审核通过
	private Boolean approved; 
	
	@Field(bridge = @FieldBridge(impl = EnumBridgeCust.class))
	@Column(nullable = false)
	public InvoiceType getType() {
		return type;
	}
	public void setType(InvoiceType type) {
		this.type = type;
	}
	
	@Field
	@Column(nullable = false)
	public String getOrganization() {
		return organization;
	}
	public void setOrganization(String organization) {
		this.organization = organization;
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
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
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
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinTable(name = "invoice_credentials",
		joinColumns = { @JoinColumn(name = "invoice_id", referencedColumnName = "id") },
		inverseJoinColumns = { @JoinColumn(name = "image_id", referencedColumnName = "id") })
	public Set<Image> getCredentials() {
		return credentials;
	}
	public void setCredentials(Set<Image> credentials) {
		this.credentials = credentials;
	}
	
	@Column(nullable = false)
	public Boolean getApproved() {
		return approved;
	}
	public void setApproved(Boolean approved) {
		this.approved = approved;
	}
}
