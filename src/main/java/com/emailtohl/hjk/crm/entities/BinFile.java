package com.emailtohl.hjk.crm.entities;

import javax.persistence.Entity;
import javax.persistence.Lob;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.emailtohl.lib.jpa.BaseEntity;

@Indexed
@Entity
public class BinFile extends BaseEntity {
	private static final long serialVersionUID = 5320953121708506932L;
	private String filename;
	private String mimeType;
	private byte[] bin;
	
	public BinFile() {
		super();
	}
	
	public BinFile(String filename, String mimeType, byte[] bin) {
		super();
		this.filename = filename;
		this.mimeType = mimeType;
		this.bin = bin;
	}

	@Field(store = Store.YES)
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	@Field
	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	
	@JsonIgnore
	@JsonIgnoreProperties
	@Lob
	public byte[] getBin() {
		return bin;
	}
	public void setBin(byte[] bin) {
		this.bin = bin;
	}
	
}
