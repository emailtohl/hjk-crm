package com.emailtohl.hjk.crm.entities;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.emailtohl.lib.jpa.BaseEntity;
import com.github.emailtohl.lib.jpa.StringBridgeCustomization;

@JsonIgnoreProperties(ignoreUnknown = true)
@Indexed
@Entity
public class Invoice extends BaseEntity {
	private static final long serialVersionUID = -2949903806197415296L;
	// 开票类型
	@NotNull
	private InvoiceType type;
	// 开票公司
	@NotNull
	private Organization organization;
	
	// 下面是财务填写
	// 收款金额
	private Double income;
	// 收款时间
	private Date receiveTime;
	// 差旅费扣除
	private Double deduct;
	// 开票金额
	private Double ticketfee;
	// 税金
	private Double tax;
	// 明细
	private String detail;
	
	// 下面有开票人填写
	// 开票时间
	private Date ticketTime;
	// 开票内容
	private String content;
	// 发票编号
	private String invoiceNumber;
	
	// 快递时间
	private Date expressTime;
	// 快递公司
	private String expressCompany;
	// 快递单号
	private String expressNumber;
	// 快递费
	private Double expressFee;
	// 垫付款
	private Double paymentOn;
	// 备注
	private String remark;
	// 流程
	private Flow flow;
	
	@Field
	@FieldBridge(impl = StringBridgeCustomization.class)
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, updatable = false)
	public InvoiceType getType() {
		return type;
	}
	public void setType(InvoiceType type) {
		this.type = type;
	}
	
	@IndexedEmbedded(depth = 1)
	@ManyToOne(optional = false)
	@JoinColumn(name = "organization_id", updatable = false)
	public Organization getOrganization() {
		return organization;
	}
	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
	
	@Field
	@FieldBridge(impl = StringBridgeCustomization.class)
	public Double getIncome() {
		return income;
	}
	public void setIncome(Double income) {
		this.income = income;
	}
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	@Field
	@FieldBridge(impl = StringBridgeCustomization.class)
	@Temporal(TemporalType.DATE)
	public Date getReceiveTime() {
		return receiveTime;
	}
	public void setReceiveTime(Date receiveTime) {
		this.receiveTime = receiveTime;
	}
	
	@Field
	@FieldBridge(impl = StringBridgeCustomization.class)
	public Double getDeduct() {
		return deduct;
	}
	public void setDeduct(Double deduct) {
		this.deduct = deduct;
	}
	
	@Field
	@FieldBridge(impl = StringBridgeCustomization.class)
	public Double getTax() {
		return tax;
	}
	public void setTax(Double tax) {
		this.tax = tax;
	}
	
	@Field
	@FieldBridge(impl = StringBridgeCustomization.class)
	public Double getTicketfee() {
		return ticketfee;
	}
	public void setTicketfee(Double ticketfee) {
		this.ticketfee = ticketfee;
	}
	
	@Field
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	@Field
	@FieldBridge(impl = StringBridgeCustomization.class)
	@Temporal(TemporalType.DATE)
	public Date getTicketTime() {
		return ticketTime;
	}
	public void setTicketTime(Date ticketTime) {
		this.ticketTime = ticketTime;
	}
	
	@Field
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	@Field
	public String getInvoiceNumber() {
		return invoiceNumber;
	}
	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	@Field
	@FieldBridge(impl = StringBridgeCustomization.class)
	@Temporal(TemporalType.DATE)
	public Date getExpressTime() {
		return expressTime;
	}
	public void setExpressTime(Date expressTime) {
		this.expressTime = expressTime;
	}
	
	@Field
	public String getExpressCompany() {
		return expressCompany;
	}
	public void setExpressCompany(String expressCompany) {
		this.expressCompany = expressCompany;
	}
	
	@Field
	public String getExpressNumber() {
		return expressNumber;
	}
	public void setExpressNumber(String expressNumber) {
		this.expressNumber = expressNumber;
	}
	
	@Field
	@FieldBridge(impl = StringBridgeCustomization.class)
	public Double getExpressFee() {
		return expressFee;
	}
	public void setExpressFee(Double expressFee) {
		this.expressFee = expressFee;
	}
	
	@Field
	@FieldBridge(impl = StringBridgeCustomization.class)
	public Double getPaymentOn() {
		return paymentOn;
	}
	public void setPaymentOn(Double paymentOn) {
		this.paymentOn = paymentOn;
	}
	
	@Field
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	@IndexedEmbedded(depth = 1)
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "flow_id")
	public Flow getFlow() {
		return flow;
	}
	public void setFlow(Flow flow) {
		this.flow = flow;
	}
	
}
